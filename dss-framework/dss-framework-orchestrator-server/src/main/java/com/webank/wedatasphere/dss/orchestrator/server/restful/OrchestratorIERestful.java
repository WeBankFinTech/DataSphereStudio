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

package com.webank.wedatasphere.dss.orchestrator.server.restful;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.core.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.publish.OrchestratorPublishService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.CommonDSSLabel;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
@Component
@Path("/dss/framework/orchestrator")
public class OrchestratorIERestful {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorIERestful.class);

    @Autowired
    private OrchestratorPublishService orchestratorPublishService;

    @Autowired
    private BMLService bmlService;

    @Autowired
    OrchestratorService orchestratorService;


    @POST
    @Path("/importOrchestratorFile")
    public Response importOrcFile(@Context HttpServletRequest req,
                                  @FormDataParam("projectName") String projectName,
                                  @FormDataParam("projectID") Long projectID,
                                  @FormDataParam("dssLabels") String dssLabels,
                                  FormDataMultiPart form) throws DSSErrorException, UnsupportedEncodingException {
        List<FormDataBodyPart> files = form.getFields("file");
        if(null == files || files.size()==0){
            throw new DSSErrorException(100788, "Import orchestrator failed for files is empty");
        }
        Long importOrcId=0L;
        for (FormDataBodyPart p : files) {

            InputStream inputStream = p.getValueAs(InputStream.class);
            FormDataContentDisposition fileDetail = p.getFormDataContentDisposition();
            String fileName = new String(fileDetail.getFileName().getBytes("ISO8859-1"), "UTF-8");



            String userName = SecurityFilter.getLoginUsername(req);
            List<DSSLabel> dssLabelList = Arrays.asList(dssLabels.split(",")).stream().map(label -> {
                DSSLabel dssLabel = new CommonDSSLabel();
                dssLabel.setLabel(label);
                return dssLabel;
            }).collect(Collectors.toList());

//            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/downloadFile/")
//                    .path(fileName)
//                    .toUriString();
//            java.nio.file.Path targetLocation = DSSFileService.fileStorageLocation.resolve(fileName);
            Workspace workspace = SSOHelper.getWorkspace(req);
            //3、打包新的zip包上传BML
//            InputStream inputStream = bmlService.readLocalResourceFile(userName, targetLocation.toAbsolutePath().toString());
            Map<String, Object> resultMap = bmlService.upload(userName, inputStream,
                    fileName, projectName);

            try {
                importOrcId = orchestratorPublishService.importOrchestrator(userName,
                        workspace.getWorkspaceName(),
                        projectName,
                        projectID,
                        resultMap.get("resourceId").toString(),
                        resultMap.get("version").toString(),
                        dssLabelList,
                        workspace);
            } catch (Exception e) {
                logger.error("Import orchestrator failed for ", e);
                throw new DSSErrorException(100789, "Import orchestrator failed for " + e.getMessage());
            }
        }
        return Message.messageToResponse(Message.ok().data("importOrcId", importOrcId));
    }

    @GET
    @Path("/exportOrchestrator")
    public void exportOrcFile(@Context HttpServletRequest req,
                              @Context HttpServletResponse resp,
                              @DefaultValue("exportOrc") @QueryParam("outputFileName") String outputFileName,
                              @DefaultValue("utf-8") @QueryParam("charset") String charset,
                              @DefaultValue("zip") @QueryParam("outputFileType") String outputFileType,
                              @QueryParam("projectName") String projectName,
                              @QueryParam("orchestratorId") Long orchestratorId,
                              @QueryParam("orcVersionId") Long orcVersionId,
                              @DefaultValue("false") @QueryParam("addOrcVersion") Boolean addOrcVersion,
                              @DefaultValue("dev") @QueryParam("dssLabels") String dssLabels) throws DSSErrorException, IOException {

        resp.addHeader("Content-Disposition", "attachment;filename="
                + new String(outputFileName.getBytes("UTF-8"), "ISO8859-1") + "." + outputFileType);
        resp.setCharacterEncoding(charset);

        Workspace workspace = SSOHelper.getWorkspace(req);
        String userName = SecurityFilter.getLoginUsername(req);

        List<DSSLabel> dssLabelList = Arrays.asList(dssLabels.split(",")).stream().map(label -> {
            DSSLabel dssLabel = new CommonDSSLabel();
            dssLabel.setLabel(label);
            return dssLabel;
        }).collect(Collectors.toList());
        Map<String, Object> res = null;
        OrchestratorVo orchestratorVo = orchestratorService.getOrchestratorVoById(orchestratorId);
        orcVersionId =orchestratorVo.getDssOrchestratorVersion().getId();
        logger.info("export orchestrator orchestratorId " + orchestratorId+",orcVersionId:"+orcVersionId);
        try {
            res = orchestratorPublishService.exportOrchestrator(userName,
                    workspace.getWorkspaceName(),
                    orchestratorId,
                    orcVersionId,
                    projectName,
                    dssLabelList,
                    addOrcVersion,
                    workspace);
        } catch (Exception e) {
            logger.error("export orchestrator failed for ", e);
            throw new DSSErrorException(100789, "export orchestrator failed for " + e.getMessage());
        }
        if (null != res) {
            Map<String, Object> downRes = bmlService.download(userName,
                    res.get("resourceId").toString(),
                    res.get("version").toString());

            InputStream inputStream = (InputStream) downRes.get("is");
            try {
                IOUtils.copy(inputStream, resp.getOutputStream());
                resp.getOutputStream().flush();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }

        }
    }

}