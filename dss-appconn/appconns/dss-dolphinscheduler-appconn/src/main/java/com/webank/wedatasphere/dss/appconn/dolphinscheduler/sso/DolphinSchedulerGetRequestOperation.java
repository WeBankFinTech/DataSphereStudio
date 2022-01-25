package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Dolphin scheduler get request operation.
 *
 * @author yuxin.yuan
 * @date 2021/05/20
 */
public class DolphinSchedulerGetRequestOperation
    implements SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerGetRequestOperation.class);

    private DolphinSchedulerSecurityService dolphinSchedulerSecurityService;

    private CloseableHttpClient httpClient;

    public DolphinSchedulerGetRequestOperation(String baseUrl) {
        this.dolphinSchedulerSecurityService = DolphinSchedulerSecurityService.getInstance(baseUrl);
    }

    @Override
    public CloseableHttpResponse requestWithSSO(SSOUrlBuilderOperation urlBuilder, DolphinSchedulerHttpGet req)
        throws AppStandardErrorException {
        try {
            ArrayList<Header> headers = new ArrayList<>();
            Header header = new BasicHeader("token", dolphinSchedulerSecurityService.getUserToken(req.getUser()));
            logger.info("dolphin请求url"+req.getURI()+"token "+dolphinSchedulerSecurityService.getUserToken(req.getUser()));

            headers.add(header);
            httpClient = HttpClients.custom().setDefaultHeaders(headers).build();
            return httpClient.execute(req);
        } catch (Throwable t) {
            throw new AppStandardErrorException(90009, t.getMessage(), t);
        }
    }

}
