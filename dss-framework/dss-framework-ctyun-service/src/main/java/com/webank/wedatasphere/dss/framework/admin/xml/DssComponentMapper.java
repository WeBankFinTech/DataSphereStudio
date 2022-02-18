package com.webank.wedatasphere.dss.framework.admin.xml;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssApplicationInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssCreateApplicationData;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssOnestopMenuInfo;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssOnestopMenuJoinApplication;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * @ClassName: DssComponentMapper
 * @Description: dss component dao 层
 * @author: lijw
 * @date: 2022/1/6 10:27
 */
@Mapper
public interface DssComponentMapper {
    @Select("SELECT * from dss_onestop_menu a ORDER BY a.id")
    List<DssOnestopMenuInfo> queryMenu();
    @Select("SELECT b.*,a.name,a.url,a.is_user_need_init,a.level,a.user_init_url,a.exists_project_service,a.project_url,a.enhance_json,a.if_iframe,a.homepage_url,a.redirect_url from dss_onestop_menu_application b RIGHT JOIN dss_application a ON a.id = b.application_id ORDER BY a.id")
    List<DssOnestopMenuJoinApplication> queryAll();
    @Select("SELECT b.*,a.name,a.url,a.is_user_need_init,a.level,a.user_init_url,a.exists_project_service,a.project_url,a.enhance_json,a.if_iframe,a.homepage_url,a.redirect_url from dss_onestop_menu_application b RIGHT JOIN dss_application a ON (a.id = b.application_id) WHERE a.id=#{id}")
    DssOnestopMenuJoinApplication query(@Param("id") int id);

    void insertApplication(DssApplicationInfo dssApplicationInfo);

//    @Insert("INSERT INTO dss_onestop_menu_application(id,application_id,onestop_menu_id,title_en,title_cn,desc_en,desc_cn,labels_en,labels_cn,is_active,access_button_en,access_button_cn) VALUES" +
//            "('0',#{id},#{dssCreateApplicationData.onestopMenuId},#{dssCreateApplicationData.titleEn},#{dssCreateApplicationData.titleCn},#{dssCreateApplicationData.descEn},#{dssCreateApplicationData.descCn}," +
//            "#{dssCreateApplicationData.labelsEn},#{dssCreateApplicationData.labelsCn},#{dssCreateApplicationData.isActive},#{dssCreateApplicationData.accessButtonEn},#{dssCreateApplicationData.accessButtonCn})")
    void insertMenuApplication(@Param("dssCreateApplicationData")  DssCreateApplicationData dssCreateApplicationData, @Param("id") int id);
//
//    @Update("UPDATE dss_application SET name=#{dssApplicationInfo.name},url=#{dssApplicationInfo.url},project_url=#{dssApplicationInfo.projectUrl},if_iframe=#{dssApplicationInfo.ifIframe},homepage_url=#{dssApplicationInfo.homepageUrl},redirect_url=#{dssApplicationInfo.redirectUrl} WHERE id=#{dssApplicationInfo.id}")
    void updateApplication( DssApplicationInfo dssApplicationInfo);

//    @Update("UPDATE dss_onestop_menu_application SET onestop_menu_id=#{dssCreateApplicationData.onestopMenuId},title_en=#{dssCreateApplicationData.titleEn},title_cn=#{dssCreateApplicationData.titleCn},desc_en=#{dssCreateApplicationData.descEn},desc_cn=#{dssCreateApplicationData.descCn},labels_en=#{dssCreateApplicationData.labelsEn},labelsCn=#{dssCreateApplicationData.labelsCn},is_active=#{dssCreateApplicationData.isActive},access_button_en=#{dssCreateApplicationData.accessButtonEn},access_button_cn=#{dssCreateApplicationData.accessButtonCn} WHERE application_id=#{dssCreateApplicationData.id}")
    void updateMenuApplication( DssCreateApplicationData dssCreateApplicationData);
}
