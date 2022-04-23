package com.webank.wedatasphere.dss.standard.common.entity.ref;

import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;


public interface ResponseRefBuilder<R extends ResponseRefBuilder<R, T>, T extends ResponseRef> {

    T build();

    T success();

    T error(String errorMsg);

    T error(Throwable cause);

    class InternalResponseRefBuilder<R extends InternalResponseRefBuilder<R, T>, T extends InternalResponseRef>
            implements ResponseRefBuilder<R, T> {

        protected String responseBody;

        public R setResponseBody(String responseBody) {
            this.responseBody = responseBody;
            return (R) this;
        }

        protected T createResponseRef(String responseBody, int status,
                                          String errorMsg, Map<String, Object> responseMap) {
            return (T) new InternalResponseRef(responseBody, status, errorMsg, responseMap);
        }

        @Override
        public final T build() {
            Map<String, Object> responseMap = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, Map.class);
            int status = (int) DSSCommonUtils.parseToLong(responseMap.get("status"));
            String errorMsg = (String) responseMap.get("message");
            return createResponseRef(responseBody, status, errorMsg, responseMap);
        }

        @Override
        public T success() {
            return createResponseRef("", 200, null, new HashMap<>(0));
        }

        @Override
        public T error(String errorMsg) {
            return createResponseRef("", 400, errorMsg, new HashMap<>(0));
        }

        @Override
        public T error(Throwable cause) {
            return error(ExceptionUtils.getRootCauseMessage(cause));
        }
    }

    class ExternalResponseRefBuilder<R extends ExternalResponseRefBuilder<R, T>, T extends ResponseRef>
            implements ResponseRefBuilder<R, T> {
        protected String responseBody;
        protected int status;
        protected String errorMsg;
        protected Map<String, Object> responseMap;

        public R setResponseBody(String responseBody) {
            this.responseBody = responseBody;
            return (R) this;
        }

        public R setStatus(int status) {
            this.status = status;
            return (R) this;
        }

        public R setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return (R) this;
        }

        public R setResponseMap(Map<String, Object> responseMap) {
            this.responseMap = responseMap;
            return (R) this;
        }

        @Override
        public T success() {
            this.status = 200;
            if(responseBody == null) {
                this.responseBody = "";
            }
            if(responseMap == null) {
                this.responseMap = new HashMap<>(0);
            }
            return build();
        }

        @Override
        public T error(String errorMsg) {
            this.status = 400;
            if(responseBody == null) {
                this.responseBody = "";
            }
            if(responseMap == null) {
                this.responseMap = new HashMap<>(0);
            }
            this.errorMsg = errorMsg;
            return build();
        }

        @Override
        public T error(Throwable cause) {
            return error(ExceptionUtils.getRootCauseMessage(cause));
        }

        protected T createResponseRef() {
            return (T) new ResponseRefImpl(responseBody, status, errorMsg, responseMap);
        }

        @Override
        public T build() {
            if(responseMap == null && StringUtils.isNotBlank(responseBody)) {
                responseMap = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, Map.class);
            } else if(responseMap == null) {
                responseMap = new HashMap<>();
            }
            return createResponseRef();
        }
    }

    class MutableExternalResponseRefBuilder<R extends MutableExternalResponseRefBuilder<R, T>, T extends ResponseRef>
            extends ExternalResponseRefBuilder<R, T> {

        public R extendsResponseBody() {
            responseMap = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, Map.class);
            return (R) this;
        }

        public R addResponse(String key, Object value){
            if(responseMap == null) {
                responseMap = new HashMap<>();
            }
            responseMap.put(key, value);
            return (R) this;
        }

    }
}
