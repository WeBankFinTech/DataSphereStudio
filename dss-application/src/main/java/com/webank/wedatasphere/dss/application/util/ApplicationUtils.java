/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.application.util;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by chaogefeng on 2019/11/20.
 */
public class ApplicationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationUtils.class);

    private static final String REDIRECT_FORMAT = "%s?redirect=%s&dssurl=${dssurl}&cookies=${cookies}";

    private static String URLEndoder(String str){
        try {
            return URLEncoder.encode(str,"utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("endoe failed:",e);
            return str;
        }
    }

    public static String redirectUrlFormat(String redirectUrl,String url){
        return String.format(REDIRECT_FORMAT,redirectUrl,URLEndoder(url));
    }

    public static void main(String[] args) throws DSSErrorException {
        System.out.println(redirectUrlFormat("http://127.0..0.1:8090/qualitis/api/v1/redirect","http://127.0..0.1:8090/#/projects/list?id={projectId}&flow=true"));
    }

}
