import { request } from '@dataspherestudio/shared';

// 工作流引用列表获取工作流名称下拉框
export function fetchworkFlowNames(templateId: string) {
  return request
    .fetch(
      'dss/framework/orchestrator/getTemplateflowNames?templateId=' + templateId
    )
    .then((res: any) => {
      if (!res) {
        return {};
      }
      return {
        orchestratorNames: res.data.orchestratorNames,
      };
    })
    .catch((error: any) => {
      //
    });
}
// 工作流引用列表获取项目名称下拉框
export function fetchworkFlowProjectNames(templateId: string) {
  return request
    .fetch(
      'dss/framework/orchestrator/getTemplateProjectNames?templateId=' +
        templateId
    )
    .then((res: any) => {
      if (!res) {
        return {};
      }
      return {
        projectNames: res.data.projectNames,
      };
    })
    .catch((error: any) => {
      //
    });
}

// 指定模板资源参数
export function fetchResParams(paramString: string): Promise<{ conf: any }> {
  const url =
    'dss/framework/workspace/engineconf/getConfTemplateParamDetail?' +
    paramString;
  return new Promise((resolve, reject) => {
    request
      .fetch(url)
      .then((res: any) => {
        if (!res) {
          resolve({ conf: {} });
          return;
        }
        resolve({
          conf: res.data.conf,
        });
      })
      .catch((error: any) => {
        reject(error);
      });
  });
}
// 模板列表数据
export function fetchTemplateTableData(paramsString = {}) {
  const url =
    'dss/framework/workspace/engineconf/getConfTemplateList?' + paramsString;
  return request
    .fetch(url)
    .then((res: any) => {
      if (!res) {
        return { data: {} };
      }
      return res;
    })
    .catch((error: any) => {
      //
    });
}

// 引擎类型下拉框数据
export function fetchEngineTypes() {
  return request
    .fetch('dss/framework/workspace/engineconf/getEngineTypeList')
    .then((res: any) => {
      if (!res) {
        return { engineTypes: {} };
      }
      return {
        engineTypes: res.data.engineTypes,
      };
    })
    .catch((error: any) => {
      //
    });
}

// 获取所有部门科室
export function fetchDepartments() {
  const url = 'dss/framework/workspace/getAllDepartments';
  return request
    .fetch(url)
    .then((res: any) => {
      if (!res) {
        return { departments: [] };
      }
      return {
        departments: res.data.departments,
      };
    })
    .catch((error: any) => {
      //
    });
}

export function fetchRelateUsersDepts(workspaceId: string) {
  const url =
    'dss/framework/workspace/getAllWorkspaceUsersWithDepartment?workspaceId=' +
    workspaceId;
  return request
    .fetch(url)
    .then((res: any) => {
      if (!res) {
        return { users: {} };
      }
      return {
        users: res.data.users,
      };
    })
    .catch((error: any) => {
      //
    });
}

// 指定工作空间下所有用户下拉框数据
export function fetchRelateUsers(workspaceId: string) {
  const url =
    'dss/framework/workspace/getAllWorkspaceUsers?workspaceId=' + workspaceId;
  return request
    .fetch(url)
    .then((res: any) => {
      if (!res) {
        return { users: {} };
      }
      return {
        users: res.data.users,
      };
    })
    .catch((error: any) => {
      //
    });
}

// 指定模板下可见用户列表数据 表格和下拉框均用该接口
export function fetchTemplateUser(paramsString: string) {
  const url =
    'dss/framework/workspace/engineconf/getConfTemplateUserList?' +
    paramsString;
  return request.fetch(url);
}
// 查询工作流引用列表数据
export function fetchWorkFlows(paramsString: string): Promise<{ data: any }> {
  const url =
    'dss/framework/orchestrator/getTemplateWorkflowPageInfo?' + paramsString;
  return new Promise((resolve, reject) => {
    request
      .fetch(url)
      .then((res: any) => {
        if (!res) {
          resolve({ data: {} });
          return;
        }
        resolve({
          data: res.data,
        });
      })
      .catch((error: any) => {
        reject(error);
      });
  });
}
// 查询应用引用列表数据
export function fetchApps(paramsString: string): Promise<{ data: any }> {
  const url =
    'dss/framework/workspace/engineconf/getConfTemplateApplyInfo?' +
    paramsString;
  return new Promise((resolve, reject) => {
    request
      .fetch(url)
      .then((res: any) => {
        if (!res) {
          resolve({ data: {} });
          return;
        }
        resolve({
          data: res.data,
        });
      })
      .catch((error: any) => {
        reject(error);
      });
  });
}

// 删除模板
export function deleteTemplate(templateId: string) {
  const url =
    'dss/framework/workspace/engineconf/deleteConfTemplate/' + templateId;
  return new Promise((resolve, reject) => {
    request
      .fetch(url, {}, { method: 'post' })
      .then((res: any) => {
        if (!res) {
          resolve({ data: {} });
          return;
        }
        resolve({
          data: res.data,
        });
      })
      .catch((error: any) => {
        reject(error);
      });
  });
}

// 模板名称唯一性校验
export function fetchUniqueName(name: string) {
  const url =
    'dss/framework/workspace/engineconf/checkConfTemplateName?name=' + name;
  return new Promise((resolve, reject) => {
    request
      .fetch(url)
      .then((res: any) => {
        if (!res) {
          resolve({ data: {} });
          return;
        }
        resolve({
          data: res.data,
        });
      })
      .catch((error: any) => {
        reject(error);
      });
  });
}

// 创建或编辑模板
export function fetchSaveTemplate(params: object) {
  const url = 'dss/framework/workspace/engineconf/saveConfTemplate';
  return new Promise((resolve, reject) => {
    request
      .fetch(url, JSON.stringify(params), { method: 'put' })
      .then((res: any) => {
        if (!res) {
          resolve({ data: {} });
          return;
        }
        resolve({
          data: res.data,
        });
      })
      .catch((error: any) => {
        reject(error);
      });
  });
}
