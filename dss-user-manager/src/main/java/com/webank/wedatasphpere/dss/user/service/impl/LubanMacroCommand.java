package com.webank.wedatasphpere.dss.user.service.impl;


import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;
import com.webank.wedatasphpere.dss.user.service.MacroCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @program: luban-authorization
 * @description: 开通命令实现
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-10 14:24
 **/
public class LubanMacroCommand implements MacroCommand {

    private List<AbsCommand> commandList =   new ArrayList<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static Integer RETRY_COUNT = 1;

    @Override
    public void add(AbsCommand command) {

        commandList.add(command);
    }

    @Override
    public String authorization(AuthorizationBody body) {
        try{
            return this.execute("authorization", body);
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public String undoAuthorization(AuthorizationBody json) {
        try{
            return this.execute("undoAuthorization", json);
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public String capacity(AuthorizationBody json) {
        try{
            return this.execute("capacity", json);
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public String renew(AuthorizationBody json) {
        try{
            return this.execute("renew", json);
        }catch (Exception e){
            return e.getMessage();
        }
    }


    /**
     * 授权操作基础方法
     * @param funName  调用的函数名
     * @param body 传入的数据
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    private String execute(String funName, AuthorizationBody body) {

        for (AbsCommand command : commandList) {
            Callable<String> callable = () -> {
                switch (funName){
                    case "authorization":
                        return command.authorization(body);

                    case "undoAuthorization":
                        return command.undoAuthorization(body);

                    case "capacity":
                        return command.capacity(body);
                }
                return command.authorization(body);
            };

            Retryer<String> retryer = RetryerBuilder.<String>newBuilder()
                    .retryIfResult(Predicates.not(Predicates.equalTo(Command.SUCCESS)))
                    .retryIfExceptionOfType(IOException.class)
                    .retryIfRuntimeException()
                    .withStopStrategy(StopStrategies.stopAfterAttempt(RETRY_COUNT))
                    .withRetryListener(new SMRetryListener())
                    .build();

            try {
                retryer.call(callable);
            } catch (ExecutionException e) {
                logger.error(funName + " error: ", e);
                return e.getMessage();
            } catch (RetryException e) {//需要通知到我们处理为什么开通失败
                logger.error(funName + " Retry error: ", e);
                return command.toMessage(e.getNumberOfFailedAttempts()+"重试失败");
            } catch (Exception e){
                logger.error(funName + " other error: ", e);
                return e.getMessage();
            }
        }
        return "success";
    }

    /**
     * 重试监听器
     * @param <CMSResultDTO>
     */
    private class SMRetryListener<CMSResultDTO> implements RetryListener {

        @Override
        public <CMSResultDTO> void onRetry(Attempt<CMSResultDTO> attempt) {
            logger.info("[retry]time=" + attempt.getAttemptNumber());
            if (attempt.hasException()) {
                logger.error("retry exception", attempt.getExceptionCause());
            }
            if (attempt.hasResult()) {
                if (attempt.getResult() == null) {
                    logger.info("retry return data is null");
                } else {
                    logger.info("retry return data is:{}", attempt.getResult());
                }
            }
        }

    }


}
