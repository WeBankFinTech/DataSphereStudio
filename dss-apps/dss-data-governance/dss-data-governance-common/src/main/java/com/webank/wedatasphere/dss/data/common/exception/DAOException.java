package com.webank.wedatasphere.dss.data.common.exception;


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
