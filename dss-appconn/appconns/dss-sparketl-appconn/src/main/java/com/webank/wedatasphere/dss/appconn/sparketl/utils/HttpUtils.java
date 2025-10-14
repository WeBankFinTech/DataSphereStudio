package com.webank.wedatasphere.dss.appconn.sparketl.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class HttpUtils {

    public static URI buildUrI(String baseUrl, String path, String appId, String appToken,
                               String nonce, String timestamp) throws NoSuchAlgorithmException, URISyntaxException {
        String signature = getSignature(appId, appToken, nonce, timestamp);
        StringBuffer uriBuffer = new StringBuffer(baseUrl);
        uriBuffer.append(path).append("?")
                .append("app_id=").append(appId).append("&")
                .append("nonce=").append(nonce).append("&")
                .append("timestamp=").append(timestamp).append("&")
                .append("signature=").append(signature);

        URI uri = new URI(uriBuffer.toString());
        return uri;
    }

    public static String getSignature(String appId, String appToken, String nonce, String timestamp) throws NoSuchAlgorithmException {
        return Sha256Utils.getSHA256L32(Sha256Utils.getSHA256L32(appId + nonce + timestamp) + appToken);
    }

}
