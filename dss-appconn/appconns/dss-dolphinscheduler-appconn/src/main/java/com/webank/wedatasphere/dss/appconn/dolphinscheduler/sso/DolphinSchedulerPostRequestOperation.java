package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * The type Dolphin scheduler post request operation.
 *
 * @author yuxin.yuan
 * @date 2021/05/19
 */
public class DolphinSchedulerPostRequestOperation
    implements SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerPostRequestOperation.class);

    private DolphinSchedulerSecurityService dolphinSchedulerSecurityService;

    private CloseableHttpClient httpClient;

    public DolphinSchedulerPostRequestOperation(String baseUrl) {
        super();
        this.dolphinSchedulerSecurityService = DolphinSchedulerSecurityService.getInstance(baseUrl);
    }

    @Override
    public CloseableHttpResponse requestWithSSO(SSOUrlBuilderOperation urlBuilder, DolphinSchedulerHttpPost req)
        throws AppStandardErrorException {
        try {
            ArrayList<Header> headers = new ArrayList<>();
            Header header = new BasicHeader("token", dolphinSchedulerSecurityService.getUserToken(req.getUser()));
            headers.add(header);
            httpClient = HttpClients.custom().setDefaultHeaders(headers).build();
            return httpClient.execute(req);
        } catch (Throwable t) {
            throw new AppStandardErrorException(90009, t.getMessage(), t);
        }
    }

    @Override
    public CloseableHttpResponse requestWithSSO(String url, DolphinSchedulerHttpPost req)
        throws AppStandardErrorException {
        return null;
    }
}
