/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.release.restful;

import com.webank.wedatasphere.dss.framework.common.utils.RestfulUtils;
import com.webank.wedatasphere.dss.framework.release.conf.ReleaseConstant;
import com.webank.wedatasphere.dss.framework.release.entity.request.*;
import com.webank.wedatasphere.dss.framework.release.service.ReleaseService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * created by cooperyang on 2020/11/17
 * Description:
 */


@Component
@Path("/dss/framework/release")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FrameworkReleaseRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkReleaseRestful.class);


    @Autowired
    private ReleaseService releaseService;

    @PostConstruct
    private void init(){

    }


    @POST
    @Path("/releaseOrchestrator")
    public Response releaseOrchestrator(@Context HttpServletRequest request,
                                        @Valid ReleaseOrchestratorRequest releaseOrchestratorRequest){
        String username = SecurityFilter.getLoginUsername(request);
        Workspace workspace = SSOHelper.getWorkspace(request);
        Long orchestratorId = releaseOrchestratorRequest.getOrchestratorId();
        Long orchestratorVersionId = releaseOrchestratorRequest.getOrchestratorVersionId();
        String comment = releaseOrchestratorRequest.getComment();
        String dssLabel = releaseOrchestratorRequest.getDssLabel();
        try{
            Long releaseTaskId =  releaseService.releaseOrchestrator(username,
                    orchestratorVersionId,
                    orchestratorId, dssLabel, workspace);
            LOGGER.info("Succeed to submit orcId {}, orcVersionId {} to release server, and taskId is {}",
                    orchestratorId, orchestratorVersionId, releaseTaskId);
            return RestfulUtils.dealOk("成功提交发布任务", new Pair<>("releaseTaskId", releaseTaskId));
        }catch(final Exception e){
            LOGGER.error("Failed to release orchestrator id : {}, orcVersionId : {} for user {}",
                    orchestratorId, orchestratorVersionId, username, e);
            return RestfulUtils.dealError("提交发布任务失败");
        }
    }


    @GET
    @Path("/getReleaseStatus")
    public Response getReleaseStatus(@Context HttpServletRequest request,
                                     @NotNull(message = ReleaseConstant.DSSLABEL_NOT_NULL) @QueryParam("dssLabel") String dssLabel,
                                     @NotNull(message = "查询的发布id不能为空") @QueryParam("releaseTaskId") Long releaseTaskId
                                     ){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            Tuple2<String, String> releaseStatus = releaseService.getStatus(releaseTaskId);
            if (null == releaseStatus){
                LOGGER.error("get releaseStatus is null for id {}", releaseTaskId);
                return RestfulUtils.dealError("获取发布进度失败");
            }
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("get {} status ok {} ", releaseTaskId, releaseStatus._1);
            }
            return RestfulUtils.dealOk("获取任务进度成功", new Pair<>("status", releaseStatus._1), new Pair<>("errorMsg", releaseStatus._2));
        }catch(final Exception e){
            LOGGER.error("Failed to get release status for {}", releaseTaskId, e);
            return RestfulUtils.dealError("获取发布进度失败");
        }
    }

    /**
     * release batch是为了进行批量发布编排模式
     * @param request
     * @param releaseBatchRequest
     * @return
     */
    @POST
    @Path("/releaseBatch")
    public Response releaseBatch(@Context HttpServletRequest request, @Valid ReleaseBatchRequest releaseBatchRequest){
        return null;
    }


    /**
     * 导出orchestrator是为了将orchestrator进行导出，在网络不同的环境中,是必须先导出然后进行导入
     * 所以要预留一个接口用来进行导出的orchestrator进行下载
     * @param request
     * @param exportOrchestratorRequest
     * @return
     */
    @POST
    @Path("/exportOrchestrator")
    public Response exportOrchestrator(@Context HttpServletRequest request, @Valid ExportOrchestratorRequest exportOrchestratorRequest){
        return null;
    }


    /**
     * 批量导出
     * @param request
     * @param exportBatchRequest
     * @return
     */
    @POST
    @Path("/exportBatch")
    public Response exportBatch(@Context HttpServletRequest request, @Valid ExportBatchRequest exportBatchRequest){
        return null;
    }

    @POST
    @Path("/importOrchestrator")
    public Response importOrchestrator(@Context HttpServletRequest request, @Valid ImportOrchestratorRequest importOrchestratorRequest){
        return null;
    }





}
