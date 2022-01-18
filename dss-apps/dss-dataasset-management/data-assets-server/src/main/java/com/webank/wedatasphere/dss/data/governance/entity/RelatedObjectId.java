package com.webank.wedatasphere.dss.data.governance.entity;


import lombok.Data;

@Data
public class RelatedObjectId {
    private String guid;
    public String relationshipGuid;

    public static RelatedObjectId from(String guid,String relationshipGuid){
        RelatedObjectId relatedObjectId = new RelatedObjectId();
        relatedObjectId.setGuid(guid);
        relatedObjectId.setRelationshipGuid(relationshipGuid);
        return relatedObjectId;
    }
}
