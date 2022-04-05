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

package com.webank.wedatasphere.dss.appconn.sendemail.email.sender;

import com.webank.wedatasphere.dss.appconn.sendemail.email.Email;
import com.webank.wedatasphere.dss.appconn.sendemail.email.EmailSender;
import org.apache.linkis.common.utils.Utils;
import scala.runtime.BoxedUnit;

import java.util.Map;
import java.util.concurrent.Future;

public abstract class AbstractEmailSender implements EmailSender {

    @Override
    public void init(Map<String, String> properties) {
    }

    @Override
    public Future<BoxedUnit> sendAsync(Email email) {
        return Utils.defaultScheduler().submit(() -> {
            send(email);
            return BoxedUnit.UNIT;
        });
    }

}
