package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import java.util.ArrayList;
import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-04-19
 * @since 1.1.0
 */
public interface DSSProjectPrivilege {

    DSSProjectPrivilege EMPTY = newBuilder().setAccessUsers(new ArrayList<>(0))
            .setEditUsers(new ArrayList<>(0))
            .setReleaseUsers(new ArrayList<>(0)).build();

    List<String> getAccessUsers();

    List<String> getEditUsers();

    List<String> getReleaseUsers();

    static Builder newBuilder() {
        return new Builder();
    }

    class Builder {

        protected List<String> accessUsers;
        protected List<String> editUsers;
        protected List<String> releaseUsers;

        public Builder setAccessUsers(List<String> accessUsers) {
            this.accessUsers = accessUsers;
            return this;
        }

        public Builder setEditUsers(List<String> editUsers) {
            this.editUsers = editUsers;
            return this;
        }

        public Builder setReleaseUsers(List<String> releaseUsers) {
            this.releaseUsers = releaseUsers;
            return this;
        }

        public DSSProjectPrivilege build() {
            return new DSSProjectPrivilege(){
                @Override
                public List<String> getAccessUsers() {
                    return accessUsers;
                }

                @Override
                public List<String> getEditUsers() {
                    return editUsers;
                }

                @Override
                public List<String> getReleaseUsers() {
                    return releaseUsers;
                }
            };
        }
    }

}
