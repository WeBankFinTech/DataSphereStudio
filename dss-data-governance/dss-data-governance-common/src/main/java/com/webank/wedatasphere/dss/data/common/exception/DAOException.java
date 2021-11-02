package com.webank.wedatasphere.dss.data.common.exception;


/**
 * @Author:李嘉玮
 */
public class DAOException extends  RuntimeException {
    public  DAOException(){
        super();
    }
    public  DAOException (String message,Throwable cause){
        super(message,cause);
    }
    public  DAOException(String message){
        super(message);
    }
    public  DAOException(Throwable cause){
        super(cause);
    }
}
