package com.webank.wedatasphere.warehouse.dao.interceptor;


import lombok.AllArgsConstructor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.*;

@AllArgsConstructor
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class DssWorkspaceNameAutoExtractQueryInterceptor /*extends AbstractSqlParserHandler*/ implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(DssWorkspaceNameAutoExtractQueryInterceptor.class);
    private final DataSource dataSource;
    private static final DssWorkspaceNameAdapter dssWorkspaceNameAdapter = new DssWorkspaceNameAdapter();

//    @Override
//    public Object plugin(Object target) {
//        // 读取@Signature中的配置，判断是否需要生成代理类
//        if (target instanceof ResultSetHandler) {
//            return Plugin.wrap(target, this);
//        } else {
//            return target;
//        }
//    }

    //脱敏方法，将加密字段变为星号
    private Object extract(Object object) throws InvocationTargetException, IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            NameAttachWorkspaceTrans confidential = field.getAnnotation(NameAttachWorkspaceTrans.class);
            if (confidential==null){
                continue;
            }
            PropertyDescriptor ps = BeanUtils.getPropertyDescriptor(object.getClass(), field.getName());
            if (ps == null || ps.getReadMethod() == null || ps.getWriteMethod() == null) {
                continue;
            }
            Object value = ps.getReadMethod().invoke(object);
            if (value != null) {
                if (value instanceof String) {
                    String fieldValue = (String) value;
                    if (fieldValue.contains(".") && fieldValue.contains(dssWorkspaceNameAdapter.getWorkspaceName())) {
                        String[] split = fieldValue.split("\\.");
                        if (split.length == 2) {
                            ps.getWriteMethod().invoke(object, split[1]);
//                            resultMap.put(colName, split[1]);
                        }
                    }
                }
            }
        }
        return object;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String flag = DssWorkspaceNameAdapter.BDP_ENTITY_WORKSPACE_NAME_AUTO_TRANSFORM.getValue();
        if ("false".equalsIgnoreCase(flag)) {
            return invocation.proceed();
        }

        logger.info("use mybatis interceptor to handle name field [transform workspace.name ??]");

        DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();

        if (resultMaps == null || resultMaps.isEmpty()) {
            return invocation.proceed();
        }

        Class<?> resultType = resultMaps.get(0).getType();
//        Type genericSuperclass = resultType.getGenericSuperclass();

//        if (!genericSuperclass.getClass().getName().equals(DssWorkspaceEntity.class.getName())) {
//            return invocation.proceed();
//        }
//        if (!DssWorkspaceEntity.class.isAssignableFrom(resultType)) {
//            return invocation.proceed();
//        }

        Object result = invocation.proceed();
        if (result instanceof Collection) {
            Collection<Object> objList= (Collection) result;
            List<Object> resultList=new ArrayList<>();
            for (Object obj : objList) {
                resultList.add(extract(obj));
            }
            return resultList;
        }else {
            return extract(result);
        }

//        Object result = invocation.proceed();
//        if (result instanceof ArrayList) {
//            ArrayList<?> list = (ArrayList<?>) result;
//            if (!list.isEmpty()) {
//                Object o = list.get(0);
//                if (o instanceof Map) {
//                    System.out.println("yes");
//                }
//            }
//        }

//        DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
//        MetaObject metaObject = SystemMetaObject.forObject(defaultResultSetHandler);
//        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
//        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
//        String[] resultSets = mappedStatement.getResultSets();
//
//        if (resultMaps == null || resultMaps.isEmpty()) {
//            return invocation.proceed();
//        }
//
//        Class<?> resultType = resultMaps.get(0).getType();
//        Type genericSuperclass = resultType.getGenericSuperclass();
//
//        if (!genericSuperclass.getClass().getName().equals(DssWorkspaceEntity.class.getName())) {
//            invocation.proceed();
//        }
//
//        Field[] declaredFields = resultType.getDeclaredFields();
//        String extractFieldName = "";
//        for (Field field : declaredFields) {
//            if (field.getAnnotation(NameFieldTransform.class) != null) {
//                extractFieldName = field.getName();
//            }
//        }
//
//        Statement statement = (Statement) invocation.getArgs()[0];
//        ResultSet resultSet = statement.getResultSet();
//        if (resultSet != null) {
//            List<Object> resList = new ArrayList<>();
//            //获得对应列名
//            ResultSetMetaData rsmd = resultSet.getMetaData();
//            List<String> columnList = new ArrayList<>();
//            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//                columnList.add(rsmd.getColumnName(i));
//            }
//            while (resultSet.next()) {
//                LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
//                for (String colName : columnList) {
//                    // 针对要处理的字段做处理
//                    if (colName.equals(extractFieldName)) {
//                        String fieldValue = resultSet.getString(colName);
//                        if (fieldValue.contains(".") && fieldValue.contains(dssWorkspaceNameAdapter.getWorkspaceName())) {
//                            String[] split = fieldValue.split("\\.");
//                            if (split.length == 2) {
//                                resultMap.put(colName, split[1]);
//                            }
//                        }
//                    } else {
//                        resultMap.put(colName, resultSet.getString(colName));
//                    }
//                }
//
//                Object o = resultType.newInstance();
//                //将转换后的map转换为实体类中
//                BeanUtils.populate(o, resultMap);
//                resList.add(o);
//            }
//            return resList;
//        }

//        return invocation.proceed();
//        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
//
//        // 获取 SQL
//        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
//
//        // 获取参数
//        Object parameter = invocation.getArgs()[1];
//
//        if (parameter instanceof MapperMethod.ParamMap) {
//            MapperMethod.ParamMap<Object> inParams = (MapperMethod.ParamMap<Object>) parameter;
//            Object et = inParams.get("et");
//
//            Type[] genericInterfaces = et.getClass().getGenericInterfaces();
//
//
//            Field[] declaredFields = et.getClass().getDeclaredFields();
//            for (Field field : declaredFields) {
//                System.out.println(field);
//                if (field.getAnnotation(NameFieldTransform.class) != null) {
//                    field.setAccessible(true);
//                    String finalName = "";
//                    Object origVal = field.get(et);
//                    if (origVal instanceof String) {
//                        finalName = dssWorkspaceNameAdapter.getWorkspaceName()+"."+origVal;
//                    }
//                    field.set(et, finalName);
//                    field.setAccessible(false);
//                }
//            }
//        }
//
//        return invocation.proceed();
    }


}
