package com.webank.wedatasphere.dss.framework.dbapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: dbApi
 * @description:
 * @author: jiangqiang
 * @create: 2021-01-20 09:52
 **/
@Data
@TableName(value = "datasource")
public class DataSource {

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField
    String name;

    @TableField
    String note;

    @TableField
    String url;

    @TableField
    String username;

    @TableField
    String password;

    @TableField
    String type;

    @TableField(exist = false)
    String className;

}
