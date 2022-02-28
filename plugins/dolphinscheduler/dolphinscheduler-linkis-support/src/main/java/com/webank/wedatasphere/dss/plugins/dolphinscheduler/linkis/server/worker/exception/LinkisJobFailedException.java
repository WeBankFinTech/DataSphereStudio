package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.server.worker.exception;

/**
 * linkis job failed exception
 */
public class LinkisJobFailedException extends Exception {
    public LinkisJobFailedException() {
    }

    public LinkisJobFailedException(String message) {
        super(message);
    }

    public LinkisJobFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
