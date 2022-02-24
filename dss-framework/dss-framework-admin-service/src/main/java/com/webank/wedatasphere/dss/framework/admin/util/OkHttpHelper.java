package com.webank.wedatasphere.dss.framework.admin.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/19-01-19-15:35
 */
public class OkHttpHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpHelper.class);
    public static int DEFAULT_TIMEOUT = 5;
    private OkHttpClient okHttpClient;

    private OkHttpHelper() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    public static OkHttpHelper getInstance() {
        return HttpHelperHolder.instance;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    private static class HttpHelperHolder {
        private static OkHttpHelper instance = new OkHttpHelper();
    }


    public static Response syncGet(Request request) throws Exception {
        OkHttpClient okHttpClient = OkHttpHelper.getInstance().getOkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response;
        } else {
            LOGGER.error("http error url:" + request.httpUrl().toString(), response);
            throw new Exception(request.httpUrl().toString() + " http调用失败");
        }
    }


}
