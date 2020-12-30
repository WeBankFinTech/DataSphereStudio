package com.webank.wedatasphpere.dss.user.service;


/**
 * @program: luban-authorization
 * @description: 开通命令接口
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-10 14:24
 **/
public interface MacroCommand extends Command {

    public void add(AbsCommand command);


}
