/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.utils;

import com.google.gson.Gson;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import javax.ws.rs.core.UriBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpUtils {
    public static URI buildUrI(String baseUrl, String path, String appId, String appToken,
        String nonce, String timestamp) throws NoSuchAlgorithmException {
        String signature = getSignature(appId, appToken, nonce, timestamp);
        URI uri = UriBuilder.fromUri(baseUrl)
            .path(path)
            .queryParam("app_id", appId)
            .queryParam("nonce", nonce)
            .queryParam("timestamp", timestamp)
            .queryParam("signature", signature)
            .build();
        return uri;
    }

    public static String getSignature(String appId, String appToken, String nonce, String timestamp) throws NoSuchAlgorithmException {
        return Sha256Utils.getSHA256L32(Sha256Utils.getSHA256L32(appId + nonce + timestamp) + appToken);
    }
}

