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

package com.webank.wedatasphere.dss.common.entity.node;

public class DSSEdgeDefault implements DSSEdge {
    private String source;
    private String target;
    private String sourceLocation;
    private String targetLocation;

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String getSourceLocation() {
        return sourceLocation;
    }

    @Override
    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    @Override
    public String getTargetLocation() {
        return targetLocation;
    }

    @Override
    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public String toString() {
        return "DWSEdge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", sourceLocation='" + sourceLocation + '\'' +
                ", targetLocation='" + targetLocation + '\'' +
                '}';
    }
}
