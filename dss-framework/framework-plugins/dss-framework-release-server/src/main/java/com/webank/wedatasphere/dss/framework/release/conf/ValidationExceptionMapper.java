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

package com.webank.wedatasphere.dss.framework.release.conf;

import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * created by cooperyang on 2020/12/22
 * Description:
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public Response toResponse(ValidationException e) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("failed to validate request bean", e);
        }
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> cv : ((ConstraintViolationException) e).getConstraintViolations()) {
            strBuilder.append(cv.getMessage()).append(";");
        }
        Message message = Message.error(strBuilder.toString());
        return Message.messageToResponse(message);
    }
}