package com.webank.wedatasphere.dss.framework.admin.restful;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.domain.PageDomain;
import com.webank.wedatasphere.dss.framework.admin.common.domain.ResponseEnum;
import com.webank.wedatasphere.dss.framework.admin.common.domain.TableDataInfo;
import com.webank.wedatasphere.dss.framework.admin.common.utils.SqlUtil;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.common.utils.TableSupport;

import java.util.HashMap;
import java.util.List;

public class BaseController {


    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setStatus(ResponseEnum.SUCCESS.getStatus());
        HashMap<String, Object> data = (HashMap<String, Object>) Message.ok().data("total" , new PageInfo(list).getTotal()).data("userList" , list).getData();
        rspData.setData(data);
        rspData.setMessage("查询成功");

        return rspData;
    }

}
