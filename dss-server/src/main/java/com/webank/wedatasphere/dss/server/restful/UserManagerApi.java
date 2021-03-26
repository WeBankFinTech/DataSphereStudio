package com.webank.wedatasphere.dss.server.restful;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.SchedulerAppJoint;
import com.webank.wedatasphere.dss.application.conf.ApplicationConf;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.server.constant.DSSServerConstant;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.impl.UserAuthorizationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @program: user-authorization
 * @description: 鲁班对外交互的接口  包括施工 注册用户
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-12 14:24
 **/

@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManagerApi {

    private UserAuthorizationClient client = new UserAuthorizationClient();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationService applicationService;

    private SchedulerAppJoint schedulerAppJoint;

    @POST
    @Path("/user")
    public Response createUser(@Context HttpServletRequest req, AuthorizationBody body) {
        String username = SecurityFilter.getLoginUsername(req);
        String superUserName = ApplicationConf.SUPER_USER_NAME;
        if(!username.equals(superUserName)){
            return Message.messageToResponse(Message.error(DSSServerConstant.SUPER_USER_LOGIN_ERROR));
        }

        try {
            String result = client.authorization(body);

            if(result.equals(AbsCommand.SUCCESS)){
                schedulerAppJoint = getSchedulerAppJoint();
                if(schedulerAppJoint != null){
                    try{
                        schedulerAppJoint.getSecurityService().reloadToken();
                    }catch (Throwable throwable){
                        logger.warn("choose schedulies,don not care");
                    }

                }
                return Message.messageToResponse(Message.ok());
            }else {
                return Message.messageToResponse(Message.error(AbsCommand.ERROR));
            }
        } catch (Exception e) {
            return Message.messageToResponse(Message.error(e.getMessage()));
        }




    }

    private SchedulerAppJoint getSchedulerAppJoint(){

        if(schedulerAppJoint == null){
            try {
                schedulerAppJoint = (SchedulerAppJoint)applicationService.getAppjoint("schedulis");
            } catch (AppJointErrorException e) {
                logger.error("Schedule system init failed!", e);
            }
        }
        return schedulerAppJoint;
    }


}
