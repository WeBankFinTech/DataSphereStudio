package com.webank.wedatasphere.dss.data.classification.service;

import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;

import java.util.List;

public interface ClassificationService {
    public AtlasTypesDef getClassificationDef( ) throws DataGovernanceException;

    public AtlasClassificationDef getClassificationDefByName(String name) throws DataGovernanceException;

    public List<AtlasClassificationDef> getClassificationDefListByName(String name) throws DataGovernanceException;

    public List<AtlasClassificationDef> getClassificationDefListForLayer() throws DataGovernanceException;

    public AtlasTypesDef createAtlasTypeDefs(final AtlasTypesDef typesDef) throws DataGovernanceException;

    public void updateAtlasTypeDefs(final AtlasTypesDef typesDef) throws DataGovernanceException;

    public void deleteClassificationDefByName(String name) throws DataGovernanceException;


}
