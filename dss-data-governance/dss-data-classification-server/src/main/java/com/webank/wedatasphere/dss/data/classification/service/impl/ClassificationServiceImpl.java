package com.webank.wedatasphere.dss.data.classification.service.impl;

import com.sun.jersey.api.client.ClientResponse;
import com.webank.wedatasphere.dss.data.classification.service.ClassificationService;
import com.webank.wedatasphere.dss.data.common.atlas.AtlasService;
import com.webank.wedatasphere.dss.data.common.conf.AtlasConf;
import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;
import lombok.AllArgsConstructor;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@AllArgsConstructor
public class ClassificationServiceImpl implements ClassificationService {
    private static final Logger logger = LoggerFactory.getLogger(ClassificationServiceImpl.class);
    private static final String ATLAS_ERROR_CODE ="errorCode";

    private AtlasService atlasService;

    /**
     * 判断系统预定义最顶层分类是否已存在，如果不存在，则需要进行初始化
     */
    @PostConstruct
    private void initializeSystemClassificationList(){
        logger.info("@@@@@@@@@@@@@@ ==> initializeSystemClassificationList");

        initializeClassification(AtlasConf.ATLAS_CLASSIFICATION_SUBJECT.getValue(),"数仓主题域");
        initializeClassification(AtlasConf.ATLAS_CLASSIFICATION_LAYER_SYSTEM.getValue(),"系统预定义分层");
        initializeClassification(AtlasConf.ATLAS_CLASSIFICATION_LAYER_USER.getValue(),"用户自定义分层");

        logger.info("@@@@@@@@@@@@@@ <== initializeSystemClassificationList");
    }

    private void initializeClassification(String name, String description){
        AtlasClassificationDef atlasClassificationDef =null;
        AtlasTypesDef atlasTypesDef =null;

        try {
            atlasClassificationDef = atlasService.getClassificationDefByName(name);
            if(atlasClassificationDef !=null) {
                logger.info(String.format("@@@@@@@@@@@@@@ === this classification [name=%s] exists,  ignore it", name));
            }
        }catch (AtlasServiceException ex) {
            logger.info("@@@@@@@@@@ error:" + ex.getMessage());
            // 404
            if (ClientResponse.Status.NOT_FOUND ==ex.getStatus()) {
                logger.info(String.format("@@@@@@@@@@@@@@ === this classification [name=%s] don't exist,  create it...", name));
                logger.info(String.format("@@@@@@@@@@@@@@ ==> initializeClassification, name=%s", name));
                atlasTypesDef = new AtlasTypesDef();
                atlasClassificationDef = new AtlasClassificationDef(name, description);
                atlasTypesDef.getClassificationDefs().add(atlasClassificationDef);
                this.createAtlasTypeDefs(atlasTypesDef);
                logger.info(String.format("@@@@@@@@@@@@@@ <== initializeClassification, name=%s", name));
            }
            else {
                throw new DataGovernanceException(ex.getMessage());
            }
        }
    }


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
