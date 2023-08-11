package com.webank.wedatasphere.dss.linkis.node.execution.parser;

import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.AppConnLinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;

import java.util.Map;

public class BML2LinkisJobParser extends CodeParser {

    @Override
    public void parseJob(Job job) throws Exception {
        if(!(job instanceof AppConnLinkisJob)) {
            return;
        }
        AppConnLinkisJob appConnLinkisJob = (AppConnLinkisJob) job;
        String runType = appConnLinkisJob.getRunType();
        // 只处理包含 bml2linkis 的 AppConnLinkisJob，例如：linkis.appconn.<appconnName>.bml2linkis
        if(!runType.toLowerCase().contains("bml2linkis")) {
            return;
        }
        job.getLogObj().info(String.format("AppConn %s try to generate Linkis jobContent from code %s.", runType,
                job.getCode()));
        Map<String, Object> script = LinkisJobExecutionUtils.gson.fromJson(job.getCode(), new TypeToken<Map<String, Object>>() {}.getType());
        if(!script.containsKey("resourceId") || !script.containsKey("version") || !script.containsKey("fileName")) {
            job.getLogObj().error("the code error, resourceId, version or fileName is not exists.");
            throw new LinkisJobExecutionErrorException(90100, "cannot recognize fileName from jobContent.");
        }
        BMLResource bmlResource = new BMLResource();
        bmlResource.setResourceId((String) script.get("resourceId"));
        bmlResource.setVersion((String) script.get("version"));
        // fileName 的格式为 ${resourceId}.${engineType}.${runType}
        bmlResource.setFileName((String) script.get("fileName"));
        getAndSetCode(bmlResource, appConnLinkisJob);
        String[] fileNameArray = bmlResource.getFileName().split("\\.");
        if(fileNameArray.length < 3) {
            job.getLogObj().error(String.format("cannot recognize fileName %s, the fileName format must be ${resourceId}.${engineType}.${runType}", bmlResource.getFileName()));
            throw new LinkisJobExecutionErrorException(90100, "cannot recognize fileName from jobContent.");
        }
        String realEngineType = fileNameArray[fileNameArray.length - 2];
        String realRunType = fileNameArray[fileNameArray.length - 1];
        setEngineType(job, realEngineType, realRunType);
    }

    protected void setEngineType(Job job, String realEngineType, String realRunType) {
        job.getLogObj().warn(String.format("switch job from engineType %s with runType %s to engineType %s with runType %s",
                job.getEngineType(), job.getRunType(), realEngineType, realRunType));
        job.setEngineType(realEngineType);
        job.setRunType(realRunType);
    }

}
