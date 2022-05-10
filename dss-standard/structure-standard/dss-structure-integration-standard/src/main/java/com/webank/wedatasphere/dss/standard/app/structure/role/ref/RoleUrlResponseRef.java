package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

/**
 * @author enjoyyin
 * @date 2022-05-07
 * @since 1.1.0
 */
public interface RoleUrlResponseRef extends ResponseRef {

    String getRoleJumpUrl();

    static Builder newBuilder() {
        return new Builder();
    }

    class Builder extends ResponseRefBuilder.ExternalResponseRefBuilder<Builder, RoleUrlResponseRef> {
        protected String roleJumpUrl;

        public Builder setRoleJumpUrl(String roleJumpUrl) {
            this.roleJumpUrl = roleJumpUrl;
            return this;
        }

        @Override
        protected RoleUrlResponseRef createResponseRef() {
            return new RoleUrlResponseRefImpl();
        }

        class RoleUrlResponseRefImpl extends ResponseRefImpl implements RoleUrlResponseRef {
            public RoleUrlResponseRefImpl() {
                super(Builder.this.responseBody, Builder.this.status,
                        Builder.this.errorMsg, Builder.this.responseMap);
            }

            @Override
            public String getRoleJumpUrl() {
                return roleJumpUrl;
            }
        }
    }

}
