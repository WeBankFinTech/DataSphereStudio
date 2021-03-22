/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.util;

import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.math3.util.Pair;


import javax.ws.rs.core.Response;
import java.util.Arrays;

/**
 * created by cooperyang on 2020/11/4
 * Description:
 */
public class RestfulUtils {

    public static Response dealError(String reason){
        Message message = Message.error(reason);
        return Message.messageToResponse(message);
    }

    public static Response dealOk(String msg){
        Message message = Message.ok(msg);
        return Message.messageToResponse(message);
    }



    @SafeVarargs
    public static Response dealOk(String msg, Pair<String, Object>... data){
        Message message = Message.ok(msg);
        Arrays.stream(data).forEach(p -> message.data(p.getKey(), p.getValue()));
        return Message.messageToResponse(message);
    }


}
