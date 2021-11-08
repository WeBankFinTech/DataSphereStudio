package com.webank.wedatasphere.warehouse.dao.interceptor;

import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import lombok.AllArgsConstructor;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@AllArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class DssWorkspaceNameAutoTransformUpdateInteceptor extends AbstractSqlParserHandler implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(DssWorkspaceNameAutoTransformUpdateInteceptor.class);
    private final DataSource dataSource;
    private static final DssWorkspaceNameAdapter dssWorkspaceNameAdapter = new DssWorkspaceNameAdapter();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String flag = DssWorkspaceNameAdapter.BDP_ENTITY_WORKSPACE_NAME_AUTO_TRANSFORM.getValue();
        if ("false".equalsIgnoreCase(flag)) {
            return invocation.proceed();
        }

        logger.info("use mybatis interceptor to handle name field [transform workspace.name ??]");

        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        // 获取 SQL
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
//        System.out.println(sqlCommandType);

        if (sqlCommandType == SqlCommandType.DELETE) {
            return invocation.proceed();
        }

        // 获取参数
        Object parameter = invocation.getArgs()[1];

        if (parameter instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> inParams = (MapperMethod.ParamMap<Object>) parameter;
            Object et = inParams.get("et");

            Field[] declaredFields = et.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.getAnnotation(NameAttachWorkspaceTrans.class) != null) {
                    field.setAccessible(true);
                    String finalName = null;
                    Object origVal = field.get(et);
//                    if (Objects.isNull(origVal)) {
//                        // name 字段是 NOT NULL 字段
//                        // 当用 mybatis-plus 更新的时候，底层会根据 null 判断是否 set xxx = yyy
//                        // 如果这里不做这样的判断处理，则会在这个SQL拦截器中
//                        continue;
//                    }
                    if (!Objects.isNull(origVal) && origVal instanceof String) {
                        finalName = dssWorkspaceNameAdapter.getWorkspaceName()+"."+origVal;
                    }
                    field.set(et, finalName);
                    field.setAccessible(false);
                }
            }
        } else {
            Field[] declaredFields = parameter.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.getAnnotation(NameAttachWorkspaceTrans.class) != null) {
                    field.setAccessible(true);
                    String finalName = "";
                    Object origVal = field.get(parameter);
                    if (origVal instanceof String) {
                        finalName = dssWorkspaceNameAdapter.getWorkspaceName()+"."+origVal;
                    }
                    field.set(parameter, finalName);
                    field.setAccessible(false);
                }
            }
        }


//        Object[] args = invocation.getArgs();
//        Executor executor = PluginUtils.realTarget(invocation.getTarget());
//        MetaObject metaObject = SystemMetaObject.forObject(executor);
//        this.sqlParser(metaObject);
//
//        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
//        // 执行的SQL语句
//        String originalSql = boundSql.getSql();
//        // SQL语句的参数
//        Object parameterObject = boundSql.getParameterObject();
//        if (parameterObject instanceof MapperMethod.ParamMap) {
//            MapperMethod.ParamMap<Object> inParams = (MapperMethod.ParamMap<Object>) parameterObject;
//            Object et = inParams.get("et");
//            Field[] declaredFields = et.getClass().getDeclaredFields();
//            for (Field field : declaredFields) {
//                System.out.println(field);
//                if (field.getAnnotation(NameFieldTransform.class) != null) {
//                    field.setAccessible(true);
//                    field.set(et, "jhasjajjajsjadas");
//                    field.setAccessible(false);
//                }
//            }
//        }

//        MappedStatement statement = (MappedStatement) args[0];
//        if (Proxy.isProxyClass(firstArg.getClass())) {
//            statement = (MappedStatement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
//        } else {
//            statement = (MappedStatement) firstArg;
//        }
//        SqlCommandType sqlCommandType = statement.getSqlCommandType();
//        Object parameter = args[1];
//        Field[] declaredFields = parameter.getClass().getDeclaredFields();
//        for (Field field : declaredFields) {
//            if (field.getAnnotation(NameFieldTransform.class) != null) {
//                System.out.println("found!!");
//            }
//        }

//        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        return invocation.proceed();
    }

    private void invokeUpdate(MappedStatement ms, Object parameter) throws InvocationTargetException, IllegalAccessException {

        System.out.println(parameter);
//        if (parameter != null && parameter instanceof AbstractEntity) {	// Mapper操作
//            AbstractEntity entity = (AbstractEntity) parameter;
//
//            // 提取公共参数并填充
//            Map<String, Object> commonFieldsMap = buildCommonFields();
//            SqlCommandType sqlCommandType = ms.getSqlCommandType();
//            if (sqlCommandType == sqlCommandType.INSERT) {
//                entity.setAddAction((String) commonFieldsMap.get("action"));
//                entity.setAddTermIp((String) commonFieldsMap.get("termIp"));
//                entity.setAddUserId((Long) commonFieldsMap.get("userId"));
//                entity.setAddDt((Long) commonFieldsMap.get("dt"));
//            } else if (sqlCommandType == sqlCommandType.UPDATE) {
//                entity.setVersion((entity.getVersion() != null ? entity.getVersion() : 1) + 1);
//                entity.setUpdAction((String) commonFieldsMap.get("action"));
//                entity.setUpdTermIp((String) commonFieldsMap.get("termIp"));
//                entity.setUpdUserId((Long) commonFieldsMap.get("userId"));
//                entity.setUpdDt((Long) commonFieldsMap.get("dt"));
//            }
//        } else {	// Dao操作
//            BoundSql boundSql = ms.getBoundSql(parameter);
//            invokeQuery(parameter, boundSql);
//        }
    }

    public static void main(String[] args) {
    }
}
