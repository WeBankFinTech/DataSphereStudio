package com.webank.wedatasphere.dss.data.classification.service.impl;

import com.sun.jersey.api.client.ClientResponse;
import com.webank.wedatasphere.dss.data.classification.service.ClassificationService;
import com.webank.wedatasphere.dss.data.common.atlas.AtlasService;
import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;
import lombok.AllArgsConstructor;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suyc
 * @Classname ClassificationServiceImpl
 * @Description TODO
 * @Date 2021/9/24 10:27
 * @Created by suyc
 */
@Service
@AllArgsConstructor
public class ClassificationServiceImpl implements ClassificationService {
    private AtlasService atlasService;

    @Override
    public AtlasTypesDef getClassificationDef() throws DataGovernanceException {
        try {
            return atlasService.getClassificationDef();
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public AtlasClassificationDef getClassificationDefByName(String name) throws DataGovernanceException {
        try {
            return atlasService.getClassificationDefByName(name);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public List<AtlasClassificationDef> getClassificationDefListByName(String name) throws DataGovernanceException {
        try {
            return atlasService.getClassificationDefListByName(name);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public List<AtlasClassificationDef> getClassificationDefListForLayer() throws DataGovernanceException {
        try {
            return atlasService.getClassificationDefListForLayer();
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public AtlasTypesDef createAtlasTypeDefs(final AtlasTypesDef typesDef) throws DataGovernanceException {
        try {
            return atlasService.createAtlasTypeDefs(typesDef);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public void updateAtlasTypeDefs(AtlasTypesDef typesDef) throws DataGovernanceException {
        try {
            atlasService.updateAtlasTypeDefs(typesDef);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public void deleteClassificationDefByName(String name) throws DataGovernanceException {
        try {
            atlasService.deleteTypedefByName(name);
        } catch (AtlasServiceException exception) {
            if(ClientResponse.Status.CONFLICT ==exception.getStatus()){
                throw new DataGovernanceException("该分类(主题域/分层)已经与表关联，无法删除");
            }
            throw new DataGovernanceException(exception.getMessage());
        }
    }
}
