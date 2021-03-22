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

package com.webank.wedatasphere.dss.common.entity.label;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.linkis.manager.label.entity.Label;

/**
 * created by cooperyang on 2021/1/12
 * Description:
 */
public interface SequentialLabel<T extends Sequential<T>> extends Label<T> {


    SequentialLabel<T> getNextLabel(SequentialLabel<T> sequentialLabel) throws DSSErrorException;


    SequentialLabel<T> getPreviousLabel(SequentialLabel<T> sequentialLabel) throws DSSErrorException;


}
