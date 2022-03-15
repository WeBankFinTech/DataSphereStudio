package com.webank.wedatasphere.dss.appconn.visualis.model;

import org.apache.linkis.httpclient.request.HttpAction;

/**
 * @author enjoyyin
 * @date 2022-03-08
 * @since 0.5.0
 */
public interface VisualisHttpAction extends HttpAction {

    void setUrl(String url);

}
