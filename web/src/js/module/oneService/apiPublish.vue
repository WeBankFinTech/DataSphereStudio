<template>
  <div class="data-service">
    <div
      v-if="!script.readOnly && isSupport"
      class="workbench-body-navbar-item">
      <Icon type="ios-build" />
      <Dropdown
        trigger="click"
        placement="bottom-end"
        transfer
        @on-click="publishApiPanel">
        <span class="navbar-item-name">{{ $t('message.oneService.apiPublish.title') }}</span>
        <DropdownMenu slot="list">
          <DropdownItem v-if="0 == hasApi" name="addApi">{{ $t('message.oneService.apiPublish.button.addApi') }}</DropdownItem>
          <DropdownItem v-if="1 == hasApi" name="updateApi">{{ $t('message.oneService.apiPublish.button.updateApi') }}</DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </div>

    <Modal
      :width="addApiModalWidth"
      v-model="addApiModalShow">
      <div
        class="api-module-title"
        slot="header">
        {{$t('message.oneService.apiPublish.addApiModal.modalTitle')}}
      </div>
      <div
        v-show="addApiBaseInfoShow"
        class="api-module-content-title"
        slot="header">
        {{$t('message.oneService.apiPublish.addApiModal.contentTitle')}}
      </div>
      <div
        v-show="addApiParamInfoShow"
        class="api-module-content-title"
        slot="header">
        {{$t('message.oneService.apiPublish.addApiModal.paramConfirmTitle')}}
      </div>
      <Form
        ref="addApi"
        :model="addApiData"
        :label-width="85" >
        <div v-show="addApiBaseInfoShow">
          <FormItem
            prop="apiName"
            :label="$t('message.oneService.apiPublish.addApiModal.apiName')"
            :rules="[
              {
                required: true,
                message: $t('message.oneService.apiPublish.rule.nameRule')
              },{
                message: $t('message.oneService.apiPublish.rule.contentLengthLimit'),
                max: 255
              }, {
                validator: checkApiName,
                trigger: 'blur'
              }
            ]">
            <Input v-model="addApiData.apiName"></Input>
          </FormItem>
          <FormItem
            prop="apiPath"
            :label="$t('message.oneService.apiPublish.addApiModal.apiPath')"
            :rules="[
              {
                required: true,
                message: $t('message.oneService.apiPublish.rule.pathRule')
              }, {
                message: $t('message.oneService.apiPublish.rule.contentLengthLimit'),
                max: 255
              }, {
                pattern: /^([\/][\w-]+)*$/,
                message: $t('message.oneService.apiPublish.rule.pathRegRule'),
              }, {
                validator: checkApiPath,
                trigger: 'blur'
              }
            ]">
            <Input v-model="addApiData.apiPath" placeholder="例如: /user/info"></Input>
          </FormItem>
          <FormItem
            prop="protocol"
            :label="$t('message.oneService.apiPublish.addApiModal.protocol')"
            :rules="[{ required: true, message: $t('message.oneService.apiPublish.rule.protocolRule') }]">
            <RadioGroup v-model="addApiData.protocol">
              <Radio :key="protocol.label" :label="protocol.value" v-for="protocol in protocolList">
                <span>{{protocol.label}}</span>
              </Radio>
            </RadioGroup>
          </FormItem>
          <FormItem
            prop="requestType"
            :label="$t('message.oneService.apiPublish.addApiModal.requestType')"
            :rules="[{ required: true, message: $t('message.oneService.apiPublish.rule.requestTypeRule') }]">
            <Select v-model="addApiData.requestType">
              <Option v-for="item in requestTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="tag"
            :label="$t('message.oneService.apiPublish.addApiModal.tag')">
            <tags-input :source.sync='addApiData.tagArr' :placeholder="$t('message.oneService.apiPublish.tagPlaceholder')"/>
          </FormItem>
          <FormItem
            prop="visible"
            :label="$t('message.oneService.apiPublish.addApiModal.visible')">
            <Select v-model="addApiData.visible">
              <Option v-for="item in visibleList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="describe"
            :label="$t('message.oneService.apiPublish.addApiModal.describe')">
            <Input v-model="addApiData.describe" show-word-limit type="textarea"></Input>
          </FormItem>
        </div>
        <div v-show="addApiParamInfoShow">
          <Table :columns="paramInfoColumns" :data="addApiData.paramList"></Table>
        </div>
      </Form>
      <div slot="footer">
        <Button
          v-show="addApiBaseInfoShow"
          @click="nextStep">{{$t('message.oneService.apiPublish.addApiModal.nextStep')}}</Button>
        <Button
          v-show="addApiParamInfoShow"
          @click="cancel">{{$t('message.oneService.apiPublish.addApiModal.cancel')}}</Button>
        <Button
          v-show="addApiParamInfoShow"
          type="primary"
          :disabled="loadding"
          @click="saveApiOk">{{$t('message.oneService.apiPublish.addApiModal.confirm')}}</Button>
      </div>
    </Modal>
    <Modal
      width="680"
      v-model="updateApiModalShow">
      <div
        class="api-module-title"
        slot="header">
        {{$t('message.oneService.apiPublish.updateApiModal.modalTitle')}}
      </div>
      <Form
        ref="updateApi"
        :model="updateApiData"
        :label-width="100" >
        <FormItem
          :label="$t('message.oneService.apiPublish.updateApiModal.selectApi')">
          <Input v-model="updateApiData.apiName" disabled></Input>
        </FormItem>
        <FormItem
          prop="apiPath"
          :label="$t('message.oneService.apiPublish.updateApiModal.apiPath')"
          :rules="[
            {
              required: true,
              message: $t('message.oneService.apiPublish.rule.pathRule')
            }, {
              message: $t('message.oneService.apiPublish.rule.contentLengthLimit'),
              max: 255
            }, {
              pattern: /^([\/][\w-]+)*$/,
              message: $t('message.oneService.apiPublish.rule.pathRegRule'),
            }, {
              validator: checkApiPath,
              trigger: 'blur'
            }
          ]">
          <Input v-model="updateApiData.apiPath"></Input>
        </FormItem>
        <FormItem
          :label="$t('message.oneService.apiPublish.updateApiModal.apiVersion')">
          <Checkbox v-model="updateApiData.apiVersionUpgrade">{{$t('message.oneService.apiPublish.updateApiModal.apiVersionUpgrade')}}</Checkbox>
        </FormItem>
        {{$t('message.oneService.apiPublish.updateApiModal.paramConfirm')}}
        <Table :columns="paramInfoColumns" :data="updateApiData.paramList"></Table>
      </Form>
      <div slot="footer">
        <Button
          @click="updateApiCancel">{{$t('message.oneService.apiPublish.addApiModal.cancel')}}</Button>
        <Button
          type="primary"
          :disabled="loadding"
          @click="updateApiOk">{{$t('message.oneService.apiPublish.addApiModal.confirm')}}</Button>
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
import api from '@/js/service/api';
import util from '@/js/util';
import tag from '@/js/component/tag'
import TagsInput from '@/js/component/TagsInput'
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
    },
    scriptType: {
      type: String
    },
  },
  components: {
    // tag,
    TagsInput
  },
  data() {
    let _this = this;
    return {
      addApiModalShow: false,
      addApiBaseInfoShow: false,
      addApiParamInfoShow: false,
      updateApiModalShow: false,
      saveLoading: false,
      addApiModalWidth: 450,
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
        }
      ],
      requireList: [
        {
          label: this.$t('message.oneService.apiPublish.paramTable.require.yes'),
          value: 1
        },
        {
          label: this.$t('message.oneService.apiPublish.paramTable.require.no'),
          value: 0
        }
      ],
      paramInfoColumns: [
        {
          title: this.$t('message.oneService.apiPublish.paramTable.paramName'),
          key: 'paramName'
        },
        {
          title: this.$t('message.oneService.apiPublish.paramTable.paramType'),
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
                    // params.row.paramType = value;
                    if (_this.addApiParamInfoShow) {
                      _this.addApiData.paramList[params.row._index].paramType = value;
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].paramType = value;
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
          title: this.$t('message.oneService.apiPublish.paramTable.require.title'),
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
                    if (_this.addApiParamInfoShow) {
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
        // {
        //   title: this.$t('message.oneService.apiPublish.paramTable.defaultValue'),
        //   key: 'defaultValue',
        //   render: (h, params) => {
        //     return h('div', [
        //       h('Input', {
        //         props: {
        //           value: params.row.defaultValue
        //         },
        //         on: {
        //           'on-blur'(event) {
        //             if (_this.addApiParamInfoShow) {
        //               _this.addApiData.paramList[params.row._index].defaultValue = event.target.value;
        //             } else if (_this.updateApiModalShow) {
        //               _this.updateApiData.paramList[params.row._index].defaultValue = event.target.value;
        //             }
        //           }
        //         }
        //       })
        //     ]);
        //   }
        // },
        {
          title: this.$t('message.oneService.apiPublish.paramTable.describe'),
          key: 'describe',
          width: '200',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  type: 'textarea',
                  value: params.row.describe
                },
                on: {
                  'on-blur'(event) {
                    // params.row.describe = event.target.value;
                    if (_this.addApiParamInfoShow) {
                      _this.addApiData.paramList[params.row._index].describe = event.target.value;
                    } else if (_this.updateApiModalShow) {
                      _this.updateApiData.paramList[params.row._index].describe = event.target.value;
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
        {
          value: 'PUT',
          label: 'PUT'
        },
        {
          value: 'DELETE',
          label: 'DELETE'
        }
      ],
      visibleList: [
        {
          value: 'workspace',
          label: this.$t('message.oneService.apiPublish.visible.workspace')
        },
        {
          value: 'private',
          label: this.$t('message.oneService.apiPublish.visible.personal')
        },
        {
          value: 'public',
          label: this.$t('message.oneService.apiPublish.visible.public')
        }
      ],
      apiList: [

      ],
      addApiData: {
        apiName: null,
        apiPath: null,
        protocol: 1,
        requestType: 'GET',
        tag: '',
        visible: 'workspace',
        describe: '',
        paramList: [

        ],
        tagArr: []
      },
      updateApiData: {

      },
      hasApi: -1,
      remoteParamList: [

      ],
      loadding: false
    }
  },
  computed: {
    isSupport() {
      return this.script.executable;
    },
  },
  mounted() {
    this.initApiInfo();
  },
  methods: {
    findParamItem(name, list) {
      var paramItem = undefined;
      list.forEach(function (item) {
        if (name === item.name) {
          paramItem = item;
        }
      })
      return paramItem
    },
    initApiInfo() {
      let _this = this;
      api.fetch('/oneservice/query', {
        scriptPath: this.work.filepath
      }, 'get').then((rst) => {
        if (rst.result) {
          _this.hasApi = 1;

          if (rst.result.params) {
            _this.remoteParamList = rst.result.params;
          }

          _this.updateApiData = {
            id: rst.result.id,
            apiName: rst.result.name,
            apiPath: rst.result.path,
            protocol: rst.result.protocol,
            visible: rst.result.scope,
            requestType: rst.result.method,
            tag: rst.result.tag,
            tagArr: rst.result.tag.split(','),
            describe: rst.result.description,
            resourceId: rst.result.resourceId,
            version: rst.result.version
          }

          _this.initUpdateApiParamList();
        } else {
          this.hasApi = 0;
        }
      }).catch((err) => {
        this.saveLoading = false;
        this.hasApi = -1;
      });
    },
    clear() {
      this.addApiData.paramList = []
      this.addApiData.tagArr = []

      this.addApiModalShow = false
      this.addApiBaseInfoShow = false
      this.addApiParamInfoShow = false
      this.addApiModalWidth = 450

      this.$refs['addApi'].resetFields();
      this.$refs['updateApi'].resetFields();
    },
    publishApiPanel(name) {
      let _this = this;

      this.clear()

      if ('addApi' === name) {
        this.addApiModalShow = true
        this.addApiBaseInfoShow = true
        this.addApiParamInfoShow = false
        this.addApiModalWidth = 450
      } else if ('updateApi' === name) {

        this.initApiInfo();

        this.updateApiModalShow = true
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
            describe: paramItem.description
          })
        } else {
          paramList.push({
            paramName: item.key,
            paramType: 1,
            require: 0,
            describe: ''
          })
        }
      })
      this.updateApiData.paramList = paramList;
    },
    cancel() {
      this.addApiModalShow = false
    },
    nextStep() {
      let _this = this;

      _this.$refs['addApi'].validate((valid) => {
        if (valid) {
          this.addApiBaseInfoShow = false
          this.addApiParamInfoShow = true
          this.addApiModalWidth = 680

          if (this.script.params.variable) {
            this.script.params.variable.forEach(function (item) {
              _this.addApiData.paramList.push({
                paramName: item.key,
                paramType: 1,
                require: 0
              })
            })
          }
        }
      });
    },
    saveApiOk() {

      this.$emit('on-save');

      this.$refs['addApi'].validate((valid) => {
        if (valid) {
          var params = []

          this.addApiData.paramList.forEach(function (item) {
            params.push({
              name: item.paramName,
              type: item.paramType,
              required: item.require,
              // defaultValue: item.defaultValue,
              description: item.describe
            })
          })

          this.saveLoading = true;
          api.fetch('/oneservice/api', {
            name: this.addApiData.apiName,
            path: this.addApiData.apiPath,
            protocol: this.addApiData.protocol,
            method: this.addApiData.requestType,
            scope: this.addApiData.visible,
            description: this.addApiData.describe,
            type: this.script.scriptType,
            tag: this.addApiData.tagArr.join(','),
            params: params,
            content: this.script.data,
            scriptPath: this.work.filepath,
            metadata: this.convertMetadata(this.script.params)
          }, 'post').then((rst) => {
            this.addApiModalShow = false;
            this.saveLoading = false;
            this.hasApi = 1;
            this.loadding = false;
            this.$Message.success(this.$t('message.constants.success.save'));
          }).catch((err) => {
            this.saveLoading = false;
            this.loadding = false;
          });
        }
      });
    },
    updateApiCancel() {
      this.updateApiModalShow = false
    },
    updateApiOk() {

      this.$emit('on-save');

      this.$refs['updateApi'].validate((valid) => {
        if (valid) {
          var params = []

          this.updateApiData.paramList.forEach(function (item) {
            params.push({
              name: item.paramName,
              type: item.paramType,
              required: item.require,
              // defaultValue: item.defaultValue,
              description: item.describe
            })
          })

          this.saveLoading = true;
          this.loadding = true;
          api.fetch('/oneservice/api/' + this.updateApiData.id, {
            name: this.updateApiData.apiName,
            path: this.updateApiData.apiPath,
            protocol: this.updateApiData.protocol,
            method: this.updateApiData.requestType,
            scope: this.updateApiData.visible,
            description: this.updateApiData.describe,
            resourceId: this.updateApiData.resourceId,
            version: this.updateApiData.version,
            type: this.script.scriptType,
            tag: this.updateApiData.tagArr.join(','),
            params: params,
            content: this.script.data,
            scriptPath: this.work.filepath,
            metadata: this.convertMetadata(this.script.params)
          }, 'put').then((rst) => {
            this.updateApiModalShow = false
            this.saveLoading = false;
            this.loadding = false;
            this.$Message.success(this.$t('message.oneService.apiPublish.notice.publishSuccess'));
          }).catch((err) => {
            this.saveLoading = false;
            this.loadding = false;
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
        datasource: params.configuration.runtime.datasource ? params.configuration.runtime.datasource : {}
      };
      return {
        variable,
        configuration,
      };
    },
    checkApiPath(rule, value, callback) {
      let _this = this;
      if (value) {
        api.fetch('/oneservice/checkPath/', {
          scriptPath: this.work.filepath,
          path: value
        }, 'get').then((rst) => {
          if (rst && !rst.result) {
            callback();
          } else {
            return callback(new Error(_this.$t('message.oneService.apiPublish.rule.pathRepeat')));
          }
        }).catch((err) => {
          return callback(new Error(_this.$t('message.oneService.apiPublish.rule.pathRepeat')));
        });
      } else {
        callback();
      }
    },
    checkApiName(rule, value, callback) {
      let _this = this;
      if (value) {
        api.fetch('/oneservice/checkName/', {
          name: value
        }, 'get').then((rst) => {
          if (rst && !rst.result) {
            callback();
          } else {
            return callback(new Error(_this.$t('message.oneService.apiPublish.rule.nameRepeat')));
          }
        }).catch((err) => {
          return callback(new Error(_this.$t('message.oneService.apiPublish.rule.pathRepeat')));
        });
      } else {
        callback();
      }
    }
  }
}
</script>

<style src="./apiPublish.scss" lang="scss"></style>

<style scoped>

</style>
