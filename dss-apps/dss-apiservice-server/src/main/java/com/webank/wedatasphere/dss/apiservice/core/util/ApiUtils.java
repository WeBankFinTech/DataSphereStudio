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

package com.webank.wedatasphere.dss.apiservice.core.util;

import com.webank.wedatasphere.dss.apiservice.core.exception.*;
import com.webank.wedatasphere.dss.apiservice.core.vo.MessageVo;
import com.webank.wedatasphere.linkis.common.exception.WarnException;
import com.webank.wedatasphere.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

/**
 *
 */
public class ApiUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ApiUtils.class);

    /**
     * @param tryOperation operate function
     * @param failMessage  message
     */
    public static Response doAndResponse(TryOperation tryOperation, String method, String failMessage) {
        try {
            Message message = tryOperation.operateAndGetMessage();
            return Message.messageToResponse(setMethod(message, method));
        } catch (ConstraintViolationException e) {
            LOG.error("api error, method: " + method, e);
            return new BeanValidationExceptionMapper().toResponse(e);
        } catch (WarnException e) {
            LOG.error("api error, method: " + method, e);
            return Message.messageToResponse(setMethod(Message.warn(e.getMessage()), method));
        } catch (AssertException e) {
            LOG.error("api error, method: " + method, e);
            return Message.messageToResponse(setMethod(Message.error(e.getMessage()), method));

        } catch (ApiExecuteException e) {
        LOG.error("api error, method: " + method, e);
        return Message.messageToResponse(setMethod(Message.error(e.getMessage()), method));
        }
        catch (ApiServiceQueryException e) {
            LOG.error("api error, method: " + method, e);
            return Message.messageToResponse(setMethod(Message.error(e.getMessage()), method));
        }
        catch (Exception e) {
            LOG.error("api error, method: " + method, e);
            return Message.messageToResponse(setMethod(Message.error(failMessage, e), method));
        }
    }

    /**
     * @param tryOperation operate function
     */
    public static Response doAndResponse(Operation tryOperation) {
        Object msg = null;
        try {
            msg = tryOperation.operateAndGetMessage();
            return Response.ok(msg).build();
        } catch (ConstraintViolationException e) {
            LOG.error("api error ", e);
            return new BeanValidationExceptionMapper().toResponse(e);
        } catch (WarnException e) {
            LOG.error("api error ", e);
            return Response.ok(setMsg("系统异常")).build();
        } catch (AssertException e) {
            LOG.error("api error ", e);
            return Response.ok(setMsg(e.getMessage())).build();
        }catch (ApiServiceRuntimeException e){
            LOG.error("api error ", e);
            return Response.ok(setMsg(e.getMessage())).build();
        }
        catch (Exception e) {
            LOG.error("api error ", e);
            return Response.ok(setMsg(String.valueOf(e.getCause()))).build();
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
        Object operateAndGetMessage() throws Exception;
    }
}
