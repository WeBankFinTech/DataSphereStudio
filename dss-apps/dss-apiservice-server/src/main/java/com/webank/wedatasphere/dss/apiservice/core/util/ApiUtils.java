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

package com.webank.wedatasphere.dss.apiservice.core.util;

import com.webank.wedatasphere.dss.apiservice.core.exception.*;
import com.webank.wedatasphere.dss.apiservice.core.vo.MessageVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.common.exception.WarnException;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;


public class ApiUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ApiUtils.class);

    /**
     * @param tryOperation operate function
     * @param failMessage  message
     */
    public static Message doAndResponse(TryOperation tryOperation, String method, String failMessage) {
        try {
            Message message = tryOperation.operateAndGetMessage();
            return setMethod(message, method);
        } catch (WarnException e) {
            LOG.error("api error, method: " + method, e);
            return setMethod(Message.warn(e.getMessage()), method);
        } catch (AssertException | ApiExecuteException | ApiServiceQueryException e) {
            LOG.error("api error, method: " + method, e);
            return setMethod(Message.error(e.getMessage()), method);
        } catch (Exception e) {
            LOG.error("api error, method: " + method, e);
            return Message.error(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * @param tryOperation operate function
     */
    public static Message doAndResponse(Operation tryOperation) {
        Message msg = null;
        try {
            msg = tryOperation.operateAndGetMessage();
            return msg;
        } catch (ConstraintViolationException e) {
            LOG.error("api error ", e);
            return new BeanValidationExceptionMapper().toResponse(e);
        } catch (WarnException e) {
            LOG.error("api error ", e);
            return Message.error("系统异常");
        } catch (AssertException e) {
            LOG.error("api error ", e);
            return Message.error(e.getMessage());
        }catch (ApiServiceRuntimeException e){
            LOG.error("api error ", e);
            return Message.error(e.getMessage());
        }
        catch (Exception e) {
            LOG.error("api error ", e);
            return Message.error(String.valueOf(e.getCause()));
        }
    }

    public static Object setMsg(String message) {
        MessageVo messageVo = new MessageVo();
        messageVo.setMessage(message);

        return messageVo;
    }

    private static Message setMethod(Message message, String method) {
        message.setMethod(method);
        return message;
    }

    @FunctionalInterface
    public interface TryOperation {

        /**
         * Operate method
         */
        Message operateAndGetMessage() throws Exception;
    }

    @FunctionalInterface
    public interface Operation {

        /**
         * Operate method
         */
        Message operateAndGetMessage() throws Exception;
    }
}
