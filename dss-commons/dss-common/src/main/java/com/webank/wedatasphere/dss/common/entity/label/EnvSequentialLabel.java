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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by cooperyang on 2021/1/12
 * Description:
 */
public class EnvSequentialLabel implements SequentialLabel<EnvSequential> {


    protected EnvSequential envSequential;

    protected static final String ENV_LABEL_KEY = "env";

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvSequentialLabel.class);

    public EnvSequentialLabel(EnvSequential envSequential) {
        super();
        this.envSequential = envSequential;
    }


    @Override
    public SequentialLabel<EnvSequential> getNextLabel(SequentialLabel<EnvSequential> sequentialLabel) throws DSSErrorException {
        EnvSequential next = this.envSequential.getNextSequential();
        if (next == null || EnvEnum.VIRTUAL.equals(next.getEnvEnum())) {
            LOGGER.warn("Failed to get next label for {}", sequentialLabel.getStringValue());
            throw new DSSErrorException(51123, "Failed to get next label for " + sequentialLabel.getStringValue());
        } else {
            return new EnvSequentialLabel(next);
        }
    }

    @Override
    public SequentialLabel<EnvSequential> getPreviousLabel(SequentialLabel<EnvSequential> sequentialLabel) throws DSSErrorException {
        EnvSequential previous = this.envSequential.getPreviousSequential();
        if (previous == null || EnvEnum.VIRTUAL.equals(previous.getEnvEnum())) {
            LOGGER.warn("Failed to get previous label for {}", sequentialLabel.getStringValue());
            throw new DSSErrorException(51123, "Failed to get next label for " + sequentialLabel.getStringValue());
        } else {
            return new EnvSequentialLabel(previous);
        }
    }

    @Override
    public String getLabelKey() {
        return ENV_LABEL_KEY;
    }

    @Override
    public EnvSequential getValue() {
        return envSequential;
    }

    @Override
    public String getStringValue() {
        return envSequential.getEnvEnum().name;
    }

    @Override
    public Boolean isEmpty() {
        return envSequential == null;
    }
}
