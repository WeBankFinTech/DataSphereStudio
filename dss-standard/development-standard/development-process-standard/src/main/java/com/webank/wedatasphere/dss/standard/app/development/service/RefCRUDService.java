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

package com.webank.wedatasphere.dss.standard.app.development.service;


import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;

/**
 * Job 管理规范，主要用于管理第三方 AppConn 的 Job（命名为 refJob）。
 * <br/>
 * 建议直接继承 AbstractRefCRUDService
 */
public interface RefCRUDService extends DevelopmentService {

     /**
      * 第三方应用工具的 Job（命名为 refJob）的创建操作。
      * @param <K> DSSJobContentRequestRef 的实现类
      * @return RefCreationOperation 的实现类
      */
     <K extends DSSJobContentRequestRef<K>> RefCreationOperation<K> getRefCreationOperation();

     /**
      * 第三方应用工具的 Job（命名为 refJob）的复制操作。
      * @param <K> CopyRequestRef 的实现类
      * @return RefCopyOperation 的实现类
      */
     <K extends CopyRequestRef<K>> RefCopyOperation<K> getRefCopyOperation();

     /**
      * 第三方应用工具的 Job（命名为 refJob）的更新操作。
      * @param <K> UpdateRequestRef 的实现类
      * @return RefUpdateOperation 的实现类
      */
     <K extends UpdateRequestRef<K>> RefUpdateOperation<K> getRefUpdateOperation();

     /**
      * 第三方应用工具的 Job（命名为 refJob）的删除操作。
      * @param <K> RefJobContentRequestRef 的实现类
      * @return RefDeletionOperation 的实现类
      */
     <K extends RefJobContentRequestRef<K>> RefDeletionOperation<K> getRefDeletionOperation();

}
