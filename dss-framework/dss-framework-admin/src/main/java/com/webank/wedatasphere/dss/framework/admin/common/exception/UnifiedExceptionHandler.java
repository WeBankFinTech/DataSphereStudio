package com.webank.wedatasphere.dss.framework.admin.common.exception;


import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.domain.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class UnifiedExceptionHandler {

    /**
     * 捕获Exception异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Message handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Message.error();
    }

    /**
     * 捕获BadSqlGrammarException异常
     * SQL语法错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BadSqlGrammarException.class)
    public Message handleException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        return Message.setResult(ResponseEnum.ERROR);
    }

    /**
     * 捕获AdminException异常
     * 这是自定义的异常，它将处理系统中所有自己抛出的异常，通过状态码和异常信息区分异常类型
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = AdminException.class)
    public Message handleException(AdminException e) {
        log.error(e.getMessage(), e);
        return Message.error().message(e.getMessage()).status(e.getCode());
    }

    /**
     * 捕获Controller上层相关异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public Message handleServletException(Exception e) {
        log.error(e.getMessage(), e);
        return Message.error().message(ResponseEnum.ERROR.getMessage()).status(ResponseEnum.ERROR.getStatus());
    }
}
