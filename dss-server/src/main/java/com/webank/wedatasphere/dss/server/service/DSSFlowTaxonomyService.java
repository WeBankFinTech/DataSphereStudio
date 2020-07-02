/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.service;


import com.webank.wedatasphere.dss.server.entity.DSSFlowTaxonomy;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;

import java.util.List;


public interface DSSFlowTaxonomyService {
    DSSFlowTaxonomy getFlowTaxonomyByID(Long id);

    //---------
    List<DSSFlowTaxonomy> listAllFlowTaxonomy(Long projectVersionID, Boolean isRootFlow);
    List<DSSFlowTaxonomy> listFlowTaxonomy(Long projectVersionID, Long flowTaxonomyID, Boolean isRootFlow);

    void addFlowTaxonomy(DSSFlowTaxonomy dssFlowTaxonomy, Long projectVersionID) throws DSSErrorException;

    void updateFlowTaxonomy(DSSFlowTaxonomy dssFlowTaxonomy, Long projectVersionID) throws DSSErrorException;

    boolean hasFlows(Long flowTaxonomyID);

    void deleteFlowTaxonomy(Long flowTaxonomyID, Long projectVersionID);
}
