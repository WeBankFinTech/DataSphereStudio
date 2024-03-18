package com.webank.wedatasphere.dss.datamodel.dimension.restful;

import com.webank.wedatasphere.dss.datamodel.dimension.service.DimensionService;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionAddVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionEnableVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionQueryVO;
import com.webank.wedatasphere.dss.datamodel.dimension.vo.DimensionUpdateVO;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;


@RestController
@RequestMapping(value = "datamodel", produces = {"application/json;charset=utf-8"})
public class DimensionRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(DimensionRestfulApi.class);

    @Autowired
    private DimensionService dimensionService;

    /**
     * 新增
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/dimensions", method = RequestMethod.POST)
    public Message add(HttpServletRequest req, @Valid @RequestBody DimensionAddVO vo) throws ErrorException {
        LOGGER.info("dimensionAddVO : {}", vo);
        return Message.ok().data("id", dimensionService.addDimension(vo));
    }

    /**
     * 启用/禁用
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @RequestMapping( value = "/dimensions/enable/{id}", method = RequestMethod.PUT)
    public Message enable(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody DimensionEnableVO vo) {
        LOGGER.info("enable id : {}, vo : {}", id, vo);
        return Message.ok().data("count", dimensionService.enableDimension(id, vo));
    }

    /**
     * 修改
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @RequestMapping( value = "/dimensions/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody DimensionUpdateVO vo) throws ErrorException{
        LOGGER.info("update id : {}, vo : {}", id, vo);
        return Message.ok().data("count",dimensionService.updateDimension(id,vo));
    }

    /**
     * 删除
     * @param req
     * @param id
     * @return
     */
    @RequestMapping( value = "/dimensions/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("delete id : {}", id);
        return Message.ok().data("count",dimensionService.deleteDimension(id));
    }

    /**
     * 分页搜索
     * @param req
     * @return
     */
    @RequestMapping( value = "/dimensions/list", method = RequestMethod.POST)
    public Message list(HttpServletRequest req,@Valid @RequestBody DimensionQueryVO vo){
        LOGGER.info("list vo : {}",vo);
        return dimensionService.listDimensions(vo);
    }


    /**
     * 查看
     * @param req
     * @param id
     * @return
     */
    @RequestMapping( value = "/dimensions/{id}", method = RequestMethod.GET)
    public Message query(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.ok().data("detail",dimensionService.queryById(id));
    }
}
