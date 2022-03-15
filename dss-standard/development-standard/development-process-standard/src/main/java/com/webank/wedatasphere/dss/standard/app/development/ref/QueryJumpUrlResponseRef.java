package com.webank.wedatasphere.dss.standard.app.development.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-11
 * @since 0.5.0
 */
public interface QueryJumpUrlResponseRef extends ResponseRef {

    /**
     * When user wants to create a new workflow node of third-part AppConn in front-web.
     * After creating, user tries to open this workflow node in front-web, in this time,
     * front-web will request dss framework to get the jump url, so dss framework will
     * try to get jump url of the front-web page of the only third-party AppConn refJob
     * by using RefQueryOperation of the third-part AppConn.
     * @return the jump url related to the front-web page of the only third-party AppConn refJob
     */
    String getJumpUrl();

    static QueryJumpUrlResponseRefBuilder newBuilder() {
        return new QueryJumpUrlResponseRefBuilder();
    }

    class QueryJumpUrlResponseRefBuilder
            extends ResponseRefBuilder.ExternalResponseRefBuilder<QueryJumpUrlResponseRefBuilder, QueryJumpUrlResponseRef> {

        private String jumpUrl;

        public QueryJumpUrlResponseRefBuilder setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
            return this;
        }

        class QueryJumpUrlResponseRefImpl extends ResponseRefImpl implements QueryJumpUrlResponseRef {
            public QueryJumpUrlResponseRefImpl() {
                super(QueryJumpUrlResponseRefBuilder.this.responseBody, QueryJumpUrlResponseRefBuilder.this.status,
                        QueryJumpUrlResponseRefBuilder.this.errorMsg, QueryJumpUrlResponseRefBuilder.this.responseMap);
            }
            @Override
            public String getJumpUrl() {
                return jumpUrl;
            }
        }

        @Override
        protected QueryJumpUrlResponseRefImpl createResponseRef() {
            return new QueryJumpUrlResponseRefImpl();
        }
    }

}
