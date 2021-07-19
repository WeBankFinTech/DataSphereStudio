package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.dbapi.dao.DSSApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiListInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSApiConfigService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DSSApiConfigServiceImpl extends ServiceImpl<DSSApiConfigMapper, ApiConfig> implements DSSApiConfigService {
    @Autowired
    DSSApiConfigMapper dssApiConfigMapper;

    public void saveApi(ApiConfig apiConfig) throws JSONException {

        String apiType = apiConfig.getApiType();
        if("GUIDE".equals(apiType)){
         String table = apiConfig.getTblName();
         String resFields = apiConfig.getResFields();
         String reqFields = apiConfig.getReqFields();
         String orderFields = apiConfig.getOrderFields();
         String whereCause = getWhereCause(reqFields);
         String orderCause = getOrderCause(orderFields);
         String sql = String.format("%s%s%s%s%s%s","select ",resFields," from ",table,whereCause,orderCause);
         apiConfig.setSql(sql);
        }

        Integer id = apiConfig.getId();
        if( id != null){
            this.updateById(apiConfig);
        }else {
            this.save(apiConfig);
        }

//
//        UpdateWrapper<ApiConfig> apiConfigUpdateWrapper = new UpdateWrapper<ApiConfig>()
//                .eq("id", id);


    }

    @Override
    public void addGroup(ApiGroup apiGroup) {
      dssApiConfigMapper.addApiGroup(apiGroup);
    }

    @Override
    public List<ApiGroupInfo> getGroupList(String workspaceId) {
        List<ApiGroupInfo>  apiGroupInfoList = dssApiConfigMapper.getGroupByWorkspaceId(workspaceId);
        for(ApiGroupInfo apiGroupInfo : apiGroupInfoList){
            int groupId = apiGroupInfo.getGroupId();
           apiGroupInfo.setApis(dssApiConfigMapper.getApiListByGroup(groupId));
        }
        return apiGroupInfoList;
    }


   private String getWhereCause(String requestFields) throws JSONException {
       JSONArray jsonArray = new JSONArray(requestFields);
       StringBuilder whereCauseBuild = new StringBuilder();
       for(int i=0;i<jsonArray.length();i++){
           if(i == 0){
               whereCauseBuild.append(" where ");
           }
           JSONObject jsonObject = jsonArray.getJSONObject(i);
           String columnName = jsonObject.getString("name");
           String compareType = jsonObject.getString("compare");
           String whereCause = String.format("%s %s #{%s}",columnName,compareType,columnName);
           System.out.println(whereCause);
           if(i < jsonArray.length()-1){
               whereCauseBuild.append(whereCause).append(" and ");

           }else {
               whereCauseBuild.append(whereCause);
           }
       }
       return whereCauseBuild.toString();
   }

    private String getOrderCause(String orderFields) throws JSONException {
        JSONArray jsonArray = new JSONArray(orderFields);
        StringBuilder orderCauseBuild = new StringBuilder();
        for(int i=0;i<jsonArray.length();i++){
            if(i == 0){
                orderCauseBuild.append(" order by ");
            }
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String columnName = jsonObject.getString("name");
            String orderType = jsonObject.getString("type");
            String orderCause = String.format("%s %s",columnName,orderType);
            System.out.println(orderCause);
            if(i < jsonArray.length()-1){
                orderCauseBuild.append(orderCause).append(",");

            }else {
                orderCauseBuild.append(orderCause);
            }
        }
        return orderCauseBuild.toString();
    }



}
