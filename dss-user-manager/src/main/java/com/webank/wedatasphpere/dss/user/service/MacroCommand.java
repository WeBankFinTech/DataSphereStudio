/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.webank.wedatasphpere.dss.user.service;


/**
 * @program: user-authorization
 * @description: 开通命令接口
 *
 * @create: 2020-08-10 14:24
 **/
public interface MacroCommand extends Command {

    public void add(AbsCommand command) throws Exception;


}
