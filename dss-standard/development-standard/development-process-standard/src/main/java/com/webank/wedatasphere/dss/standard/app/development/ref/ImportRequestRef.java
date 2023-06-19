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

package com.webank.wedatasphere.dss.standard.app.development.ref;


import com.webank.wedatasphere.dss.common.entity.BmlResource;

import java.util.Map;

public interface ImportRequestRef<R extends RefJobContentRequestRef<R>>
        extends CopyRequestRef<R> {

    default R setResourceMap(Map<String, Object> resourceMap) {
        setParameter("resourceMap", resourceMap);
        return (R) this;
    }

    /**
     * ResourceMap is the content of ExportRequestRef exported.
     * <br>
     * Now, DSS only supports to import BML resources or one inputStream,
     * so when you want to use BML resources to import/export, the resourceMap must be
     * consisted of `resourceId` and `version`; and if you want to use stream to
     * import/export, the resourceMap must be consisted of `inputStream` and `closeable`.
     * <br>
     * The `closeable` is remained field that if your inputStream comes from HttpClient,
     * you can set the `closeable` to CloseableHttpResponse to void exception.
     * @return the content of ExportRequestRef exported.
     */
    default Map<String, Object> getResourceMap() {
        return (Map<String, Object>) getParameter("resourceMap");
    }

    default boolean isLinkisBMLResources() {
        return true;
    }

    default boolean isStreamResources() {
        return !isLinkisBMLResources();
    }

    String RESOURCE_ID_KEY = "resourceId";
    String RESOURCE_VERSION_KEY = "version";

    String INPUT_STREAM_KEY = "inputStream";
    String CLOSEABLE_KEY = "closeable";

}
