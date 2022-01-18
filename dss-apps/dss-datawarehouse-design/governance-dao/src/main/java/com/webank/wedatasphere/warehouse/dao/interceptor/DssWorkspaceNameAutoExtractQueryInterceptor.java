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
    private static final DssWorkspaceNameAdapter dssWorkspaceNameAdapter = new DssWorkspaceNameAdapter();
    private final DataSource dataSource;
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
    }


}
