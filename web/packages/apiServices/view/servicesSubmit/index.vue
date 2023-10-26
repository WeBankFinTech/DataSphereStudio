<template>
  <div class="submit-wrapper">
    <div style="font-size: 16px;">
      <SvgIcon style="font-size: 16px;display: inline-block;transform: rotate(180deg);"
        color="#444444"
        @click="goBack"
        icon-class="fi-expand-right"/> 批量提交审批</div>
    <Row class="main">
      <Col span="14">
        <div class="title">数据API信息</div>
        <Form
          ref="submitForm"
          :model="formData"
          :rules="formValid"
          :label-width="120"
        >
          <FormItem
            prop="id"
            label="选择API"
            class="api-select"
          >
            <Select v-model="formData.id"
              filterable
              clearable
              multiple>
              <Option v-for="item in apiList" :key="item.id" :value="item.id+'_-_'+item.name">{{item.name}}</Option>
            </Select>
            <div class="tag-list">
              <Tag v-for="item of selectedApi"
                closable
                @click.native="toggleInfo(item)"
                @on-close="delectSelect(item)"
                :class="{'active': currentApi.id == item.id}"
                :key="item.id">
                {{ item.name }}
              </Tag>
            </div>
          </FormItem>
          <div class="title">审批信息</div>
          <FormItem
            prop="approvalName"
            label="审批单名称">
            <Input v-model="formData.approvalName" />
          </FormItem>
          <FormItem
            prop="applyUser"
            label="授权用户">
            <Select v-model="formData.applyUser" multiple filterable>
              <Option v-for="item in applyUserList" :key="item.name" :value="item.name">{{item.name}}</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="duration"
            label="授权期限"
          >
            <Input v-model="formData.duration" placeholder="取值范围1-7300,永久请输入星号：*">
              <template #append><span>天</span></template>
            </Input>
          </FormItem>
          <FormItem
            prop="sensitive"
            label="是否涉及一级数据"
          >
            <Select v-model="formData.sensitive" placeholder="请检查API的查询结果是否包含一级敏感数据：微众卡号，出生日期">
              <Option value="1">是</Option>
              <Option value="0">否</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="importance"
            label="重要程度">
            <Select v-model="formData.importance">
              <Option value="1">高</Option>
              <Option value="2">中</Option>
              <Option value="3">低</Option>
            </Select>
          </FormItem>
          <FormItem
            prop="backgroundDesc"
            label="背景描述">
            <Input v-model="formData.backgroundDesc" type="textarea" />
          </FormItem>
          <FormItem
            prop="attentionUser"
            label="关注人">
            <Select v-model="formData.attentionUser" multiple filterable>
              <Option v-for="item in applyUserList" :key="item.name" :value="item.name">{{item.name}}</Option>
            </Select>
          </FormItem>
        </Form>
        <Button style="margin-left:100px" type="primary" @click="confirm" :loading="isConfirmLoading">提交审批</Button>
        <Button style="margin-left:20px" type="default" @click="cancel">取消</Button>
      </Col>
      <Col span="15" class="info-detail" v-show="showInfo">
        <div class="title">API信息
          <SvgIcon style="float:right;padding:2px" @click="toggleInfo()" icon-class="close2"/>
        </div>
        <div class="info-item"><span class="label">API英文名</span> {{currentApi.name}}</div>
        <div class="info-item"><span class="label">API中文名</span> {{currentApi.aliasName}}</div>
        <div class="info-item"><span class="label">API路径</span> {{currentApi.path}}</div>
        <div class="info-item"><span class="label">协议</span> {{currentApi.protocol === 1 ? 'HTTP' : 'HTTPS'}}</div>
        <div class="info-item"><span class="label">请求方式</span> {{currentApi.method}}</div>
        <div class="info-item"><span class="label">可见范围</span> {{currentApi.scope === 'grantView' ? '授权可见' : ''}}</div>
        <div class="info-item"><span class="label">标签</span> {{currentApi.tag}}</div>
        <div class="info-item"><span class="label">描述</span> {{currentApi.description}}</div>
        <div class="info-item"><span class="label">备注</span> {{currentApi.comment}}</div>
        <div class="title" style="margin-top:10px">参数信息</div>
        <Table :columns="paramInfoColumns" :data="currentApi.params">
        </Table>
      </Col>
    </Row>
  </div>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
import { GetWorkspaceUserManagement } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
import storage from '@dataspherestudio/shared/common/helper/storage';

export default {
  data() {
    return {
      formData: {
        approvalName: '',
        id: [],
        backgroundDesc: '',
        applyUser: [],
        duration: '',
        importance: '',
        sensitive: '',
        attentionUser: []
      },
      applyUserList: [],
      showInfo: false,
      currentApi: {
        params: []
      },
      // 提交按钮的loading
      isConfirmLoading: false,
      paramInfoColumns: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.paramName'),
          key: 'name'
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.displayName'),
          key: 'displayName',
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.paramType'),
          key: 'type',
          render: (h, params) => {
            return h('div', this.getType(params.row.type));
          }
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.require.title'),
          key: 'required',
          render: (h, params) => {
            return h('div', params.row.required == '1' ? '是' : '否');
          }
        },
        {
          title: this.$t('message.scripts.apiPublish.paramTable.describe'),
          key: 'description',
        }
      ],
      apiList: [],
      formValid: {
        id: [
          {
            type: 'array',
            required: true,
            message: "请选择数据服务API",
            trigger: "change",
          }
        ],
        approvalName: [
          {
            required: true,
            message: "请填写审批单名称",
            trigger: "blur",
          },
          { message: "审批单名称最长200字符", max: 200 }
        ],
        applyUser: [
          {
            type: 'array',
            required: true,
            message: "请选择授权用户",
            trigger: "change",
          }
        ],
        duration: [
          {
            required: true,
            validator: (rule, value, callback) => {
              value = value.trim()
              if (value === '*') {
                callback()
              }
              if (!value) {
                return callback(new Error('请填写授权期限'))
              }
              if (value <=0 || value > 7300 || value % 1 !== 0) {
                callback(new Error('授权期限取值范围1-7300整数天，或*'))
              }
              return callback()
            }
          }
        ],
        sensitive: [
          {
            required: true,
            trigger: "change",
            validator: (rule, value, callback) => {
              value = value.trim()
              if (!value) {
                return callback(new Error('请选择是否涉及一级数据'))
              }
              if (value == '1') {
                callback(new Error('查询结果中涉及一级数据的API不允许提交审批'))
              }
              return callback()
            }
          }
        ],
        importance: [
          {
            required: true,
            message: "请选择重要程度",
            trigger: "change",
          }
        ],
        backgroundDesc: [
          {
            required: true,
            message: "请填写背景描述",
            trigger: "blur",
          },
          { message: "背景描述最长500字符", max: 500 }
        ]

      }
    }
  },
  computed: {
    selectedApi() {
      return this.formData.id.map(it => {
        return {
          id: it.split('_-_')[0],
          name: it.split('_-_')[1],
        }
      })
    }
  },
  methods: {
    toggleInfo(info) {
      if (info) {
        this.currentApi = {
          ...this.apiList.find(it => it.id == info.id)
        }
        this.showInfo = true
      } else {
        this.currentApi = {
          params: []
        }
        this.showInfo = false
      }
    },
    getApiData() {
      api.fetch('/dss/apiservice/availableSubmitApi', {
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.apiList = res.availableSubmitApiList;
      }).finally(()=> {
      })
    },
    confirm() {
      this.$refs.submitForm.validate(valid=>{
        if(valid) {
          const submitApiInfos = this.formData.id.map(it => {
            const api = this.apiList.find(item => item.id == it.split('_-_')[0])
            return api && {
              apiId: api.id,
              apiVersionId: api.latestVersionId
            }
          })
          const params = {
            approvalName: this.formData.approvalName,
            backgroundDesc: this.formData.backgroundDesc,
            applyUser: this.formData.applyUser.join(','),
            duration: this.formData.duration.trim() === '*' ? '*' : this.formData.duration.trim() - 0 + '',
            importance: this.formData.importance,
            sensitive: this.formData.sensitive,
            attentionUser: this.formData.attentionUser.join(','),
            creator: this.getUserName(),
            submitApiInfos,
            workspaceId: this.$route.query.workspaceId
          }
          this.isConfirmLoading = true
          api.fetch('/dss/apiservice/submit', params, 'post').then(() => {
            this.$Message.success('提交成功')
            this.isConfirmLoading = false
            this.$router.push({ name: 'Apiservices', query: { workspaceId: this.$route.query.workspaceId} })
          }).catch(() => {
            this.isConfirmLoading = false
          })
        }
      })
    },
    cancel() {
      this.$refs.submitForm.resetFields();
      this.goBack();
    },
    delectSelect(item) {
      this.showInfo = false;
      this.formData.id = this.formData.id.filter(it => {
        return it.split('_-_')[0] !== item.id
      })
    },
    getType(type) {
      switch (type) {
        case '1':
          return 'String';
        case '2':
          return 'Number';
        case '3':
          return 'Date';
        case '4':
          return 'Array';
        default:
          return '';
      }
    },
    getApplyUserList() {
      if (this.$route.query.workspaceId) {
        GetWorkspaceUserManagement( {
          workspaceId: this.$route.query.workspaceId
        }).then((res) => {
          this.applyUserList = res.workspaceUsers;
        })
      }
    },
    goBack() {
      this.$router.go(-1)
    },
    getUserName() {
      return storage.get("baseInfo", "local")
        ? storage.get("baseInfo", "local").username
        : '';
    }
  },
  mounted() {
    this.getApiData()
    this.getApplyUserList()
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';

.submit-wrapper {
  height: 100%;
  padding: 20px;
  background: #eee;
}
.main {
  background: #fff;
  padding: 20px;
  margin-top: 10px;
  height: calc(100% - 30px);
  overflow: auto;
}
.title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
}
.info-item {
  padding: 4px 0;
}
.label {
  display: inline-block;
  width: 120px;
}
.info-detail {
  padding: 20px;
  position: fixed;
  right: 10px;
  bottom: 0;
  border: 1px solid #eee;
  box-shadow: 0 0 5px 3px #eee;
  background: #fff;
  top: 59px;
  z-index: 999;
  overflow: auto;
}
.submit-wrapper {
  .api-select {
    ::v-deep .ivu-select-multiple .ivu-tag-checked {
      display: none;
    }
  }

  .active {
    ::v-deep .ivu-tag-text, ::v-deep .ivu-icon {
      color: $primary-color;
    }
  }
}
.tag-list {
  margin-top: 5px;
}

</style>
