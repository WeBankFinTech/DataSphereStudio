package com.webank.wedatasphere.dss.datamodel.measure.restful;


import com.webank.wedatasphere.dss.datamodel.measure.service.MeasureService;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureAddVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureEnableVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureQueryVO;
import com.webank.wedatasphere.dss.datamodel.measure.vo.MeasureUpdateVO;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequestMapping(value = "datamodel", produces = {"application/json;charset=utf-8"})
public class MeasureRestfulApi {


    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureRestfulApi.class);

    @Autowired
    private MeasureService measureService;

    /**
     * 新增
     *
     * @param req
     * @param vo
     * @return
     * @throws IOException
     */
    @RequestMapping( value = "/measures", method = RequestMethod.POST)
    public Message add(HttpServletRequest req, @RequestBody MeasureAddVO vo) throws ErrorException{
        LOGGER.info("measureAddVO : {}", vo);
        return Message.ok().data("id", measureService.addMeasure(vo));
    }

    /**
     * 启用/禁用
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @RequestMapping( value = "/measures/enable/{id}", method = RequestMethod.PUT)
    public Message enable(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody MeasureEnableVO vo) {
        LOGGER.info("enable id : {}, vo : {}", id, vo);
        return Message.ok().data("count", measureService.enableMeasure(id, vo));
    }

    /**
     * 修改
     * @param req
     * @param id
     * @param vo
     * @return
     */
    @RequestMapping( value = "/measures/{id}", method = RequestMethod.PUT)
    public Message update(HttpServletRequest req, @PathVariable("id") Long id, @RequestBody MeasureUpdateVO vo) throws ErrorException{
        LOGGER.info("update id : {}, vo : {}", id, vo);
        return Message.ok().data("count",measureService.updateMeasure(id,vo));
    }


    /**
     * 查看
     * @param req
     * @param id
     * @return
     */

    @RequestMapping( value = "/measures/{id}", method = RequestMethod.GET)
    public Message query(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("query id : {}", id);
        return Message.ok().data("detail",measureService.queryById(id));
    }

    /**
     * 删除
     * @param req
     * @param id
     * @return
     */
    @RequestMapping( value = "/measures/{id}", method = RequestMethod.DELETE)
    public Message delete(HttpServletRequest req, @PathVariable("id") Long id) throws ErrorException {
        LOGGER.info("delete id : {}", id);
        return Message.ok().data("count",measureService.deleteMeasure(id));
    }

    /**
     * 分页搜索
     * @param req
     * @return
     */
    @RequestMapping( value = "/measures/list", method = RequestMethod.POST)
    public Message list(HttpServletRequest req,@RequestBody MeasureQueryVO vo){
        LOGGER.info("list vo : {}",vo);
        return measureService.listMeasures(vo);
    }
}
