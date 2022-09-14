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
    private static final DssWorkspaceNameAdapter dssWorkspaceNameAdapter = new DssWorkspaceNameAdapter();
    private final DataSource dataSource;
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String flag = DssWorkspaceNameAdapter.BDP_ENTITY_WORKSPACE_NAME_AUTO_TRANSFORM.getValue();
        if ("false".equalsIgnoreCase(flag)) {
            return invocation.proceed();
        }

        logger.info("use mybatis interceptor to handle name field [transform workspace.name ??]");

        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (sqlCommandType == SqlCommandType.DELETE) {
            return invocation.proceed();
        }

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
        return invocation.proceed();
    }
}
