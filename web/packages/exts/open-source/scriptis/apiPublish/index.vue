<template>
  <div v-if="isApiPublish && showScriptsType && work.filepath">
    <div
      v-if="!script.readOnly && isSupport">
      <Icon type="ios-hammer" />
      <Dropdown
        trigger="click"
        placement="bottom-end"
        transfer
        @on-click="publishApiPanel">
        <span class="navbar-item-name">{{ $t('message.scripts.apiPublish.title') }}</span>
        <DropdownMenu slot="list">
          <DropdownItem v-if="0 == hasApi" name="addApi">{{ $t('message.scripts.apiPublish.button.addApi') }}</DropdownItem>
          <DropdownItem v-if="-1 !== hasApi" name="updateApi">{{ $t('message.scripts.apiPublish.button.updateApi') }}</DropdownItem>
          <div style="text-align: center;" v-else>No API Data</div>
        </DropdownMenu>
      </Dropdown>
    </div>

    <Modal
      :mask-closable="false"
      :width="addApiModalWidth"
      v-model="addApiModalShow">
      <div
        class="api-module-title"
        slot="header">
        {{$t('message.scripts.apiPublish.addApiModal.modalTitle')}}
      </div>
      <div
        v-if="step === 1"
        class="api-module-content-title"
        slot="header">
        {{$t('message.scripts.apiPublish.addApiModal.contentTitle')}}
      </div>
      <div
        v-if="step === 2"
        class="api-module-content-title"
        slot="header">
        {{$t('message.scripts.apiPublish.addApiModal.paramConfirmTitle')}}
      </div>
      <Form
        ref="addApi"
        :model="addApiData"
        :rules="ruleValidate"
        :label-width="90" >
        <div v-if="step === 1">
          <FormItem
            prop="apiName"
            :label="$t('message.scripts.apiPublish.addApiModal.apiName')">
            <Input v-model="addApiData.apiName"/>
          </FormItem>
          <FormItem
            prop="aliasName"
            :label="$t('message.scripts.apiPublish.rule.chineseName')">
            <Input v-model="addApiData.aliasName"/>
          </FormItem>
          <FormItem
            prop="apiPath"
            :label="$t('message.scripts.apiPublish.addApiModal.apiPath')">
            <Input v-model="addApiData.apiPath" placeholder="Rule: /user/info"/>
          </FormItem>
          <FormItem
            prop="protocol"
            :label="$t('message.scripts.apiPublish.addApiModal.protocol')"
            :rules="[{ required: true, message: $t('message.scripts.apiPublish.rule.protocolRule') }]">
            <RadioGroup v-model="addApiData.protocol">
              <Radio :key="protocol.label" :label="protocol.value" v-for="protocol in protocolList">
                <span>{{protocol.label}}</span>
              </Radio>
            </RadioGroup>
          </FormItem>
          <FormItem
            prop="requestType"
            :label="$t('message.scripts.apiPublish.addApiModal.requestType')"
            :rules="[{ required: true, message: $t('message.scripts.apiPublish.rule.requestTypeRule') }]">
            <Select v-model="addApiData.requestType">
              <Option v-for="item in requestTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="tag"
            :label="$t('message.scripts.apiPublish.addApiModal.tag')">
            <tags-input :source.sync='addApiData.tagArr' :placeholder="$t('message.scripts.apiPublish.tagPlaceholder')"/>
          </FormItem>
          <FormItem
            prop="visible"
            :label="$t('message.scripts.apiPublish.addApiModal.visible')">
            <Select v-model="addApiData.visible">
              <Option v-for="item in visibleList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="describe"
            :rules="[{ message: $t('message.scripts.apiPublish.rule.contentLengthLimitTwo'), max: 200 }]"
            :label="$t('message.scripts.apiPublish.addApiModal.describe')">
            <Input v-model="addApiData.describe" show-word-limit type="textarea"/>
          </FormItem>
          <FormItem
            prop="comment"
            :rules="[{ message: $t('message.scripts.apiPublish.rule.contentLengthLimitMax'), max:1024 }]"
            :label="$t('message.scripts.apiPublish.addApiModal.comment')">
            <Input v-model="addApiData.comment" show-word-limit type="textarea"/>
          </FormItem>
        </div>
        <div v-if="step === 2">
          <Table :columns="paramInfoColumns" :data="addApiData.paramList">
            <template slot-scope="{row, index}" slot="defaultValue">
              <Input :class="{ verificationValue: tip[row.paramName] }" :title="addApiData.paramList[index].defaultValue" @on-blur="verificationValue(row)" :type="inputType(row.paramType)" v-model="addApiData.paramList[index].defaultValue"/>
            </template>
          </Table>
        </div>
        <div v-if="step === 3">
          <FormItem
            prop="approvalName"
            :label="$t('message.scripts.apiPublish.addApiModal.approvalName')">
            <Input v-model="addApiData.approvalName"/>
          </FormItem>
          <FormItem
            prop="applyUser"
            :label="$t('message.scripts.apiPublish.addApiModal.applyUser')">
            <Select v-model="addApiData.applyUser" multiple filterable placeholder="Select your user">
              <Option v-for="item in applyUserList" :key="item.name" :value="item.name">{{item.name}}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="proxyUser"
            :label="$t('message.scripts.apiPublish.addApiModal.proxyUser')">
            <Input v-model="addApiData.proxyUser"/>
          </FormItem>
        </div>
      </Form>
      <div slot="footer">
        <!-- 取消按钮 -->
        <Button :disabled="saveLoading" @click="cancel">{{$t('message.scripts.apiPublish.addApiModal.cancel')}}</Button>
        <!-- 上一步按钮 -->
        <Button v-if="step === 2 || step === 3" :disabled="saveLoading" @click="backStep">{{$t('message.scripts.apiPublish.addApiModal.back')}}</Button>
        <!-- 下一步按钮 -->
        <Button v-if="step === 1 || step === 2" @click="nextStep(step)">{{$t('message.scripts.apiPublish.addApiModal.nextStep')}}</Button>
        <!-- 保存提交按钮 -->
        <Button v-if="step === 3" type="primary" :loading="saveLoading" @click="saveApiOk">
          <span v-if="saveLoading">{{$t('message.scripts.publishing')}}</span>
          <span v-else>{{$t('message.scripts.apiPublish.addApiModal.confirm')}}</span>
        </Button>
      </div>
    </Modal>
    <Modal
      width="1030"
      :mask-closable="false"
      v-model="updateApiModalShow">
      <div
        class="api-module-title"
        slot="header">
        {{$t('message.scripts.apiPublish.updateApiModal.modalTitle')}}
      </div>
      <Form
        ref="updateApi"
        :model="updateApiData"
        :label-width="120"
      >
        <template v-if="step === 1">
          <FormItem
            prop="id"
            label="请选择服务API:"
            :rules="[
              {
                required: true,
                message: 'Please select the API'
              }
            ]">
            <Select v-model="updateApiData.id" placeholder="Select your api">
              <Option v-for="item in apiList" :key="item.id" :value="item.id">{{item.name}}</Option>
            </Select>
          </FormItem>
          {{$t('message.scripts.apiPublish.updateApiModal.paramConfirm')}}
          <Table :columns="paramInfoColumns" :data="updateApiData.paramList">
            <template slot-scope="{row, index}" slot="defaultValue">
              <Input :class="{ verificationValue: tip[row.paramName] }" @on-blur="verificationValue(row)" :title="updateApiData.paramList[index].defaultValue" :type="inputType(row.paramType)" v-model="updateApiData.paramList[index].defaultValue"/>
            </template>
          </Table>
        </template>
        <template v-if="step === 2">
          <FormItem
            prop="approvalName"
            :label="$t('message.scripts.apiPublish.addApiModal.approvalName')"
            :rules="[{ required: true, message: 'The name cannot be empty', trigger: 'blur' }]"
          >
            <Input v-model="updateApiData.approvalName"/>
          </FormItem>
          <FormItem
            prop="applyUser"
            :label="$t('message.scripts.apiPublish.addApiModal.applyUser')"
            :rules="[
              { required: true, message: 'Please select the user', type: 'array' }
            ]"
          >
            <Select v-model="updateApiData.applyUser" multiple filterable placeholder="Select your user">
              <Option v-for="item in applyUserList" :key="item.name" :value="item.name">{{item.name}}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="proxyUser"
            :label="$t('message.scripts.apiPublish.addApiModal.proxyUser')"
            :rules="[
              { required: false }
            ]">
            <Input v-model="updateApiData.proxyUser"/>
          </FormItem>
          <FormItem
            prop="describe"
            :rules="[{ message: $t('message.scripts.apiPublish.rule.contentLengthLimitTwo'), max: 200 }]"
            :label="$t('message.scripts.apiPublish.addApiModal.describe')">
            <Input v-model="updateApiData.describe" show-word-limit type="textarea"/>
          </FormItem>
          <FormItem
            prop="comment"
            :rules="[{ message: $t('message.scripts.apiPublish.rule.contentLengthLimitMax'), max:1024 }]"
            :label="$t('message.scripts.apiPublish.addApiModal.comment')">
            <Input v-model="updateApiData.comment" show-word-limit type="textarea"/>
          </FormItem>
        </template>
      </Form>
      <div slot="footer">
        <Button :disabled="saveLoading" @click="updateApiCancel">{{$t('message.scripts.apiPublish.addApiModal.cancel')}}</Button>
        <!-- 上一步按钮 -->
        <Button v-if="step === 2" :disabled="saveLoading" @click="backStep">{{$t('message.scripts.apiPublish.addApiModal.back')}}</Button>
        <Button v-if="step === 1" @click="updateNextStep(step)">{{$t('message.scripts.apiPublish.addApiModal.nextStep')}}</Button>
        <Button
          v-if="step === 2"
          type="primary"
          :loading="saveLoading"
          @click="updateApiOk">
          <span v-if="saveLoading">{{$t('message.scripts.publishing')}}</span>
          <span v-else>{{$t('message.scripts.apiPublish.addApiModal.confirm')}}</span>
        </Button>
      </div>
    </Modal>

    <Spin
      v-if="saveLoading"
      size="large"
      class="new-sidebar-spin"
      fix/>
  </div>
</template>

<script>
import { isEmpty} from 'lodash';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import TagsInput from './TagsInput'
import storage from '@dataspherestudio/shared/common/helper/storage';
import { GetWorkspaceUserManagement } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';

export default {
  name: "apiPublish",
  props: {
    script: {
      type: Object,
      required: true,
    },
    work: {
      type: Object,
      required: true,
    }
  },
  components: {
    // tag,
    TagsInput
  },
  data() {
    let _this = this;
    return {
      isApiPublish: false,
      tip: {},
      step: 0,
      addApiModalShow: false,
      updateApiModalShow: false,
      saveLoading: false,
      addApiModalWidth: 450,
      ruleValidate: {
        approvalName: [
          { required: true, message: this.$t('message.scripts.apiPublish.rule.approvalName'), trigger: 'blur' }
        ],
        apiName: [
          {
            required: true,
            message: this.$t('message.scripts.apiPublish.rule.nameRule')
          },
          {
            message: this.$t('message.scripts.apiPublish.rule.contentLengthLimit'),
            max: 150
          },
          {
            validator: this.checkApiName,
            trigger: 'blur'
          }
        ],
        aliasName: [
          {
            required: true,
            message: this.$t('message.scripts.apiPublish.rule.nameRule')
          },
          {
            message: this.$t('message.scripts.apiPublish.rule.contentLengthLimit'),
            max: 150
          }
        ],
        apiPath: [
          {
            required: true,
            message: this.$t('message.scripts.apiPublish.rule.pathRule')
          },
          {
            message: this.$t('message.scripts.apiPublish.rule.contentLengthLimit'),
            max: 150
          },
          {
            pattern: /^([\/][\w-]+)*$/,
            message: this.$t('message.scripts.apiPublish.rule.pathRegRule'),
          },
          {
            validator: this.checkApiPath,
            trigger: 'blur'
          }
        ],
        applyUser: [
          { required: true, message: 'Please select the user' }
        ],
        gender: [
          { required: true, message: 'Please select gender', trigger: 'change' }
        ],
      },
      paramTypeList: [
        {
          label: 'String',
          value: 1
        },
        {
          label: 'Number',
          value: 2
        },
        {
          label: 'Date',
          value: 3
        },
        {
          label: 'Array',
          value: 4
        }
      ],
      requireList: [
        {
          label: this.$t('message.scripts.apiPublish.paramTable.require.yes'),
          value: 1
        },
        // 待开启状态
        // {
        //   label: this.$t('message.scripts.apiPublish.paramTable.require.no'),
        //   value: 0
        // },
        // {
        //   label: this.$t('message.scripts.apiPublish.paramTable.require.hide'),
        //   value: 2
        // }
      ],
      paramInfoColumns: [
        {
          title: this.$t('message.scripts.apiPublish.paramTable.paramName'),
          key: 'paramName'
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.displayName'),
          key: 'displayName',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  value: params.row.displayName
                },
                on: {
                  'on-blur'(event) {
                    if (_this.addApiModalShow) {
                      _this.addApiData.paramList[params.row._index].displayName = event.target.value;
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].displayName = event.target.value;
                    }
                  }
                }
              })
            ]);
          }
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.paramType'),
          key: 'paramType',
          // width: '100',
          render: (h, params) => {
            return h('div', [
              h('Select', {
                props: {
                  transfer: true,
                  value: params.row.paramType
                },
                on: {
                  'on-change'(value) {
                    params.row.paramType = value;
                    if (_this.addApiModalShow) {
                      _this.addApiData.paramList[params.row._index].paramType = value;
                      // 切换类型清空默认值
                      _this.addApiData.paramList[params.row._index].defaultValue = '';
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].paramType = value;
                      // 切换类型清空默认值
                      _this.updateApiData.paramList[params.row._index].defaultValue = '';
                    }
                  }
                }
              },
              this.paramTypeList.map((item) =>{
                return h('Option', {
                  props: {
                    value: item.value,
                    label: item.label
                  }
                })
              }))
            ]);
          }
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.require.title'),
          key: 'require',
          // width: '100',
          render: (h, params) => {
            return h('div', [
              h('Select', {
                props: {
                  transfer: true,
                  value: params.row.require
                },
                on: {
                  'on-change'(value) {
                    // params.row.require = value;
                    if (_this.addApiModalShow) {
                      _this.addApiData.paramList[params.row._index].require = value;
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].require = value;
                    }

                  }
                }
              },
              this.requireList.map((item) =>{
                return h('Option', {
                  props: {
                    value: item.value,
                    label: item.label
                  }
                })
              }))
            ]);
          }
        },
        {
          title: this.$t('message.ext.opensource.defalut'),
          width: '180',
          slot: 'defaultValue',
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.describe'),
          key: 'describe',
          width: '200',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  type: 'text',
                  value: params.row.describe
                },
                on: {
                  'on-blur'(event) {
                    // params.row.describe = event.target.value;
                    if (_this.addApiModalShow) {
                      _this.addApiData.paramList[params.row._index].describe = event.target.value;
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].describe = event.target.value;
                    }

                  }
                }
              })
            ]);
          }
        },
        {
          title: this.$t('message.ext.opensource.detail'),// TODO 国际化待合并后修改
          key: 'details',
          width: '200',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  type: 'textarea',
                  value: params.row.details,
                  maxlength: 512
                },
                on: {
                  'on-blur'(event) {
                    // params.row.details = event.target.value;
                    if (_this.addApiModalShow) {
                      _this.addApiData.paramList[params.row._index].details = event.target.value;
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].details = event.target.value;
                    }

                  }
                }
              })
            ]);
          }
        }
      ],
      protocolList: [
        {
          label: 'HTTP',
          value: 1
        },
        {
          label: 'HTTPS',
          value: 2
        }
      ],
      requestTypeList: [
        {
          value: 'GET',
          label: 'GET'
        },
        {
          value: 'POST',
          label: 'POST'
        },
        // {
        //   value: 'PUT',
        //   label: 'PUT'
        // },
        // {
        //   value: 'DELETE',
        //   label: 'DELETE'
        // }
      ],
      visibleList: [
        // {
        //   value: 'workspace',
        //   label: this.$t('message.scripts.apiPublish.visible.workspace')
        // },
        // {
        //   value: 'private',
        //   label: this.$t('message.scripts.apiPublish.visible.personal')
        // },
        // {
        //   value: 'public',
        //   label: this.$t('message.scripts.apiPublish.visible.public')
        // },
        {
          value: 'grantView',
          label: this.$t('message.scripts.apiPublish.visible.grantView')
        }
      ],
      apiList: [

      ],
      addApiData: {
        comment: '',
        apiName: null,
        aliasName: null,
        apiPath: null,
        protocol: 1,
        requestType: 'POST',
        tag: '',
        visible: 'grantView',
        describe: '',
        approvalName: 'default',
        applyUser: [],
        proxyUser: '',
        paramList: [

        ],
        tagArr: []
      },
      updateApiData: {
        id: '',
        apiName: '',
        aliasName: '',
        apiPath: '',
        protocol: '',
        visible: '',
        requestType: '',
        tag: '',
        tagArr: [],
        describe: '',
        latestVersionId: '',
        approvalName: 'default',
        applyUser: [],
        proxyUser: '',
        paramList: [],
        comment: ''
      },
      hasApi: -1,
      remoteParamList: [

      ],
      applyUserList: []
    }
  },
  created() {
  },
  computed: {
    isSupport() {
      return this.script.executable;
    },
    // 判断是否符合脚本类型
    showScriptsType() {
      return [ 'spark', 'hive' ].includes(this.script.scriptType)
    }
  },
  watch: {
    'addApiData.apiName'(val) {
      this.addApiData.apiPath = `/${val || ''}`
    },
    // 当更新是选择了api时，回填当前选中的用户信息
    'updateApiData.id'(val) {
      this.apiDataFill(val);
    }
  },
  mounted() {
    // 获取用户工作空间权限
    const workspaceRoles = storage.get(`workspaceRoles`) || [];
    if (workspaceRoles.includes('apiUser')) {
      this.isApiPublish = true;
      if (this.showScriptsType && this.work.filepath) this.initApiInfo();
    }
  },
  methods: {
    apiDataFill(id) {
      const currentApi = this.apiList.find((item) => item.id == id);
      if (currentApi) {
        this.updateApiData.approvalName = currentApi.approvalVo.approvalName;
        // 如果之前绑定的用户被删除，需要清掉
        this.updateApiData.applyUser = currentApi.approvalVo.applyUser.split(',').filter((item) => this.applyUserList.some((i) => i.name === item));
        this.updateApiData.proxyUser = currentApi.approvalVo.executeUser;
        this.updateApiData.comment = currentApi.comment || '';
      }
    },
    // 验证发布和更新的默认值是否满足条件
    verificationValue (row) {
      let flag;
      if(row.defaultValue.length > 1024) {
        this.$Message.error({ content: this.$t('message.ext.opensource.longer1024') });
        flag = true;
      } else if(row.paramType === 4 && row.defaultValue.split('\n').length > 1000) {
        this.$Message.error({ content: this.$t('message.ext.opensource.rowlimit') });
        flag = true;
      } else {
        flag = false
      }
      this.$set(this.tip, row.paramName, flag)
    },
    // 参数默认值输入框类型
    inputType(typeNumer) {
      if (typeNumer === 1) {
        return 'text';
      } else if (typeNumer === 2) {
        return 'number';
      } else if (typeNumer === 3) {
        return 'text';
      } else if (typeNumer === 4) {
        return 'textarea';
      }
    },
    // 获取当前工作空间当前用户的api列表
    getApiList() {
      api.fetch('/dss/apiservice/getUserServices', {
        workspaceId: this.$route.query.workspaceId
      }, {
        method: 'get',
        cacheOptions: {time: 3000}
      }).then((res) => {
        // 只有是正常的数据服务才能被选择0-禁用，1-正常，2-删除
        if (res.query_list) {
          this.apiList = res.query_list.filter((item) => item.status === 1);
        }
      })
    },
    updateNextStep() {
      if(Object.values(this.tip).some(i => i)) return this.$Message.error({ content: this.$t('message.ext.opensource.outlimit') });
      this.$refs['updateApi'].validate((valid) => {
        if (valid) {
          this.step += 1;
        }
      })
    },
    // 获取所有申请用户列表
    getApplyUserList() {
      if (this.$route.query.workspaceId) {
        GetWorkspaceUserManagement( {
          workspaceId: this.$route.query.workspaceId
        }).then((res) => {
          this.applyUserList = res.workspaceUsers;
        })
      }
    },
    findParamItem(name, list) {
      var paramItem = undefined;
      list.forEach(function (item) {
        if (name === item.name) {
          paramItem = item;
        }
      })
      return paramItem
    },
    // 初始化创建数据
    initCreateData() {
      this.addApiData = {
        apiName: null,
        comment: '',
        apiPath: null,
        protocol: 1,
        requestType: 'POST',
        tag: '',
        visible: 'grantView',
        describe: '',
        paramList: [

        ],
        tagArr: []
      }
    },
    initApiInfo() {
      let _this = this;
      return api.fetch('/dss/apiservice/query', {
        scriptPath: this.work.filepath
      }, 'get').then((rst) => {
        if (rst.result) {
          // 如果是已经禁用或删除提示用户不能修改
          this.hasApi = 1;
          if (rst.result.status === 1) {
            if (rst.result.params) {
              _this.remoteParamList = rst.result.params;
            }
            this.updateApiData = {
              id: rst.result.id,
              apiName: rst.result.name,
              aliasName: rst.result.aliasName,
              apiPath: rst.result.path,
              protocol: rst.result.protocol,
              visible: rst.result.scope,
              requestType: rst.result.method,
              tag: rst.result.tag,
              tagArr: rst.result.tag.split(','),
              describe: rst.result.description,
              resourceId: rst.result.resourceId,
              latestVersionId: rst.result.latestVersionId,
              approvalName: 'default',
              applyUser: [],
              proxyUser: '',
              comment: ''
            }
          }
        } else {
          this.hasApi = 0;
        }
        this.initUpdateApiParamList();
      }).catch(() => {
        this.saveLoading = false;
        this.hasApi = -1;
      });
    },
    clear() {
      this.addApiData.paramList = [];
      this.addApiData.tagArr = [];
      this.step = 1;
      this.addApiModalShow = false;
      this.addApiModalWidth = 450;
      this.tip = {};
      this.$nextTick(() => {
        this.$refs['addApi'].resetFields();
        this.$refs['updateApi'].resetFields();
        this.initCreateData()
      })
    },
    publishApiPanel(name) {
      if (this.script.params.variable.some((item) => !item.value)) return this.$Message.warning(this.$t('message.ext.opensource.cnanotnull'))
      this.getApplyUserList();
      this.getApiList();
      this.clear()
      if ('addApi' === name) {
        this.addApiModalShow = true
        this.addApiModalWidth = 450
      } else if ('updateApi' === name) {
        this.initApiInfo().then(() => {
          this.apiDataFill(this.updateApiData.id);
          this.updateApiModalShow = true;
        });
        this.step = 1;
      }
    },
    initUpdateApiParamList() {
      let _this = this;
      var paramList = []
      this.script.params.variable.forEach(function (item) {
        var paramItem = undefined;
        if (_this.remoteParamList) {
          paramItem = _this.findParamItem(item.key, _this.remoteParamList)
        }
        if (paramItem) {
          paramList.push({
            paramName: paramItem.name,
            paramType: parseInt(paramItem.type),
            require: parseInt(paramItem.required),
            defaultValue: paramItem.defaultValue || '',
            describe: paramItem.description,
            displayName: paramItem.displayName,
            details: paramItem.details
          })
        } else {
          paramList.push({
            paramName: item.key,
            paramType: 1,
            require: 1,
            defaultValue: '',
            describe: '',
            displayName: '',
            details: ''
          })
        }
      })
      this.updateApiData.paramList = paramList;
    },
    cancel() {
      // 隐藏模态框并将步骤归零
      this.addApiModalShow = false;
      this.step = 1;
    },
    backStep() {
      this.step-=1
      if(this.step === 2) {
        this.addApiModalWidth = 1030
      } else {
        this.addApiModalWidth = 450
        this.step = 1
      }

    },
    nextStep(e) {
      if(e === 1) {
        this.$refs['addApi'].validate((valid) => {
          if (valid) {
            this.addApiModalWidth = 1030
            this.step+=1 // 当前步骤加1
            // 判断当前脚本的配置是否存在并且addApiData的属性列表是否已经存在
            if (this.script.params.variable && !this.addApiData.paramList.length) {
              this.addApiData.paramList = [];
              this.script.params.variable.forEach((item) =>{
                this.addApiData.paramList.push({
                  paramName: item.key,
                  paramType: 1,
                  defaultValue: '',
                  require: 1,
                  displayName: '',
                })
              })
            }
          }
        });
      } else {
        if(Object.values(this.tip).some(i => i)) return this.$Message.error({ content: this.$t('message.ext.opensource.outlimit') });
        this.step = 3
        this.addApiModalWidth = 450;
      }
    },
    saveApiOk() {
      if (this.saveLoading) return;
      this.$emit('on-save');
      this.$refs['addApi'].validate((valid) => {
        if (valid) {
          var params = []
          this.addApiData.paramList.forEach(function (item) {
            params.push({
              name: item.paramName,
              type: item.paramType,
              required: item.require,
              defaultValue: item.defaultValue,
              displayName: item.displayName,
              description: item.describe,
              details: item.details
            })
          })

          this.saveLoading = true;
          api.fetch('/dss/apiservice/api', {
            comment: this.addApiData.comment,
            name: this.addApiData.apiName,
            aliasName: this.addApiData.aliasName,
            path: this.addApiData.apiPath,
            protocol: this.addApiData.protocol,
            method: this.addApiData.requestType,
            scope: this.addApiData.visible,
            description: this.addApiData.describe,
            type: this.script.application,
            runType: this.script.runType, //
            tag: this.addApiData.tagArr.join(','),
            params: params,
            content: this.script.data,
            scriptPath: this.work.filepath,
            workspaceId: this.$route.query.workspaceId,
            metadata: this.convertMetadata(this.script.params),
            approvalVo: {
              approvalName: this.addApiData.approvalName,
              applyUser: this.addApiData.applyUser.join(','),
              executeUser: this.addApiData.proxyUser,
              creator: this.getUserName()
            }
          }, 'post').then(() => {
            this.addApiModalShow = false;
            this.saveLoading = false;
            this.hasApi = 1;
            // 发布成功后更新API列表
            this.getApiList();
            this.$Message.success(this.$t('message.scripts.constants.success.save'));
          }).catch(() => {
            this.saveLoading = false;
          });
        }
      });
    },
    getUserName() {
      return  storage.get("baseInfo", 'local') ? storage.get("baseInfo", 'local').username : null;
    },
    updateApiCancel() {
      this.step = 1;
      this.updateApiModalShow = false
    },
    updateApiOk() {
      if (this.saveLoading) return;
      this.$emit('on-save');

      this.$refs['updateApi'].validate((valid) => {
        if (valid) {
          var params = []
          this.updateApiData.paramList.forEach(function (item) {
            params.push({
              name: item.paramName,
              type: item.paramType,
              required: item.require,
              defaultValue: item.defaultValue,
              displayName: item.displayName,
              description: item.describe,
              details: item.details
            })
          })

          this.saveLoading = true;
          api.fetch('/dss/apiservice/api/' + (this.updateApiData.latestVersionId || '0'), {
            targetServiceId: this.updateApiData.id,
            name: this.updateApiData.apiName,
            aliasName: this.updateApiData.aliasName,
            path: this.updateApiData.apiPath,
            protocol: this.updateApiData.protocol,
            method: this.updateApiData.requestType,
            scope: this.updateApiData.visible,
            description: this.updateApiData.describe,
            type: this.script.application,
            runType: this.script.runType,
            tag: this.updateApiData.tagArr.join(','),
            params: params,
            content: this.script.data,
            scriptPath: this.work.filepath,
            metadata: this.convertMetadata(this.script.params),
            workspaceId: this.$route.query.workspaceId,
            comment: this.updateApiData.comment,
            approvalVo: {
              approvalName: this.updateApiData.approvalName,
              applyUser: this.updateApiData.applyUser.join(','),
              executeUser: this.updateApiData.proxyUser,
              creator: this.getUserName()
            }
          }, 'put').then(() => {
            this.updateApiModalShow = false
            this.saveLoading = false;
            this.getApiList();
            this.$Message.success(this.$t('message.scripts.apiPublish.notice.publishSuccess'));
          }).catch(() => {
            this.saveLoading = false;
          });
        }
      });
    },
    convertMetadata(params) {
      const variable = isEmpty(params.variable) ? {} : util.convertArrayToObject(params.variable);

      const configuration = isEmpty(params.configuration) ? {} : {
        special: params.configuration.special,
        runtime: {
          args: params.configuration.runtime.args,
          env: isEmpty(params.configuration.runtime.env) ? {} : util.convertArrayToObject(params.configuration.runtime.env),
        },
        startup: params.configuration.startup,
        datasource: params.configuration.datasource ? params.configuration.datasource : {}
      };
      return {
        variable,
        configuration,
      };
    },
    checkApiPath(rule, value, callback) {
      let _this = this;
      if (value) {
        api.fetch('/dss/apiservice/checkPath/', {
          scriptPath: this.work.filepath,
          path: value
        }, 'get').then((rst) => {
          if (rst && !rst.result) {
            callback();
          } else {
            return callback(new Error(_this.$t('message.scripts.apiPublish.rule.pathRepeat')));
          }
        }).catch(() => {
          return callback(new Error(_this.$t('message.scripts.apiPublish.rule.pathRepeat')));
        });
      } else {
        callback();
      }
    },
    checkApiName(rule, value, callback) {
      let _this = this;
      if (value) {
        if(!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(value)) return callback(new Error(_this.$t('message.scripts.apiPublish.rule.valueRule')));
        api.fetch('/dss/apiservice/checkName/', {
          name: value
        }, 'get').then((rst) => {
          if (rst && !rst.result) {
            callback();
          } else {
            return callback(new Error(_this.$t('message.scripts.apiPublish.rule.nameRepeat')));
          }
        }).catch(() => {
          return callback(new Error(_this.$t('message.scripts.apiPublish.rule.pathRepeat')));
        });
      } else {
        callback();
      }
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.workbench-body-navbar-item {
  margin: 0 16px;
  cursor: pointer;
  color: rgba($text-color, 0.8);
  &.disabled {
      color: rgba($text-color, 0.5);
  }
  &:hover {
      color: $link-active-color;
      &.disabled {
          color: rgba($text-color, 0.5);
      }
  }
  .ivu-icon {
      font-size: 16px;
  }
  .navbar-item-name {
      margin-left: 1px;
  }
}
</style>
