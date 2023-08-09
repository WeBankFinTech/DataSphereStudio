<template>
  <div class="dataServicesContent">
    <nav class="title">
      <Breadcrumb class="breadcrumb" separator="/">
        <Icon type="md-arrow-round-back" @click="goBack" class="back"></Icon>
        <BreadcrumbItem :to="{ name: 'Apiservices', query:{ workspaceId: $route.query.workspaceId }}">
          <!-- <Icon type="ios-home-outline" size="16"></Icon> -->
          {{$t('message.apiServices.title')}}
        </BreadcrumbItem>
        <BreadcrumbItem>
          <!-- <Icon type="logo-buffer" size="16"></Icon> -->
          {{$t('message.apiServices.apiTable.operation.manager')}}
        </BreadcrumbItem>
      </Breadcrumb>
    </nav>
    <div class="service-content">
      <div class="top">
        <span class="name">
          {{ apiData.name }}
        </span>
        <span class="status" v-if="apiData.status == 0" style="color:red;">
          {{ $t('message.apiServices.disable') }}
        </span>
        <span class="status" style="color:rgb(18, 150, 219);" v-else>
          {{ $t('message.apiServices.enable') }}
        </span>
      </div>
      <p class="buttom">
        {{ apiData.description }}
      </p>
    </div>
    <section class="section">
      <Tabs>
        <TabPane :label="$t('message.apiServices.apiInfo.title')">
          <!-- 基础信息组件 -->
          <APIInfo :dataList='apiInfoData'>
            <h3 style="padding: 10px 0;">{{$t('message.apiServices.query.comment')}}</h3>
            <div class="remarks">
              <Alert class="remarksContent" v-if="!isRemarksEdit">{{ apiData.comment }}</Alert>
              <Input class="remarksContent" v-else v-model="apiData.comment" type="textarea" :rows="4" placeholder="Enter something..." />
              <Button class="remarksButton" @click="getEditAndConfirm" type="primary">{{ isRemarksEdit ? $t('message.apiServices.confirm') : $t('message.apiServices.modifyRemarks') }}</Button>
            </div>
            <h3 style="padding: 10px 0;">{{ $t('message.apiServices.label.parameter') }}</h3>
            <Table :columns="paramInfoColumns" :data="apiData.params"></Table>
          </APIInfo>
        </TabPane>
        <TabPane :label="$t('message.apiServices.apiInfo.set')">
          <Form :label-width="150" ref="formValidate" :model="formValidate" :rules="ruleValidate">
            <!-- 设置后续功能开放，误删！2020.09.23 -->
            <!-- <h3 class="from-title">{{$t('message.apiServices.apiSetForm.flowLimitation')}}</h3>
            <FormItem prop="rps" :label="$t('message.apiServices.apiSetForm.form.MaxConcurrentNumber')" style="width: 500px">
              <Input :model="formValidate.rps" placeholder="">
              <span slot="append">RPS</span>
              </Input>
            </FormItem>
            <FormItem prop="pcr" :label="$t('message.apiServices.apiSetForm.form.MaxSimultaneousQuery')" style="width: 500px">
              <Input :model="formValidate.pcr" placeholder="">
              <span slot="append">PCR</span>
              </Input>
              </Row>
            </FormItem>
            <FormItem prop="runMaxTime" :label="$t('message.apiServices.apiSetForm.form.MaxTimeQuery')" style="width: 500px">
              <TimePicker :model="formValidate.runMaxTime" placeholder=""></TimePicker>
            </FormItem>
            <h3 class="from-title">{{$t('message.apiServices.apiSetForm.defaultSetting')}}</h3>
            <FormItem prop="defaultVersion" :label="$t('message.apiServices.apiSetForm.form.defaultVersion')" style="width: 500px">
              <Input :model="formValidate.defaultVersion" placeholder="">
              <Button icon="ios-arrow-forward" slot="append"></Button>
              </Input>
            </FormItem>
            <FormItem prop="parameter" :label="$t('message.apiServices.apiSetForm.form.defaultConfigurationParameter')" style="width: 500px">
              <Input :model="formValidate.parameter" placeholder="">
              <Button icon="md-add" slot="append"></Button>
              </Input>
            </FormItem>
            <FormItem prop="defaultVar" :label="$t('message.apiServices.apiSetForm.form.defaultVariable')" style="width: 500px">
              <Input :model="formValidate.defaultVar" placeholder="">
              <Button icon="md-add" slot="append"></Button>
              </Input>
            </FormItem> -->
            <h3 class="from-title">{{$t('message.apiServices.apiSetForm.advancedSetting')}}</h3>
            <FormItem class="approvalNoItem" prop="approvalNo" :label="$t('message.apiServices.apiSetForm.form.approvalNo')" style="width: 600px">
              <Input class="approvalNo-input" disabled v-model="formValidate.approvalNo">
              </Input>
              <Button type="primary" @click="refreshAction">{{ $t('message.apiServices.buttonText.refresh') }}</Button>
            </FormItem>
            <!-- <FormItem class="submit">
              <Button @click="handleSubmit('formValidate')" type="primary">{{$t('message.apiServices.save')}}</Button>
            </FormItem> -->
          </Form>
        </TabPane>
        <TabPane :label="$t('message.apiServices.apiInfo.authorize')">
          <TokenAuth ref="tokenAuth"></TokenAuth>
        </TabPane>
        <TabPane :label="$t('message.apiServices.apiVersionInfo.title')">
          <APIVersions></APIVersions>
        </TabPane>
      </Tabs>
    </section>
  </div>
</template>
<script>
import APIVersions from './component/APIVersions.vue';
import TokenAuth from './component/TokenAuthorization.vue';
import APIInfo from './component/APIInfo.vue';
import api from '@dataspherestudio/shared/common/service/api';
export default {
  components: {
    APIVersions,
    TokenAuth,
    APIInfo
  },
  data() {
    return {
      isRemarksEdit: false,
      // api的基础数据
      apiData: {},
      // 加工后在APIInfo组件渲染的数据
      apiInfoData: [],
      formValidate: {
        approvalNo: "",
        // runMaxTime: "",
        // rps: "",
        // pcr: "",
        // defaultVersion: "",
        // parameter: "",
        // defaultVar: "",
      },
      loading: false,
      ruleValidate: {
        approvalNo: [
          { required: true, message: this.$t('message.apiServices.tip.cannotBeEmpty') }
        ]
      },
      // api信息tab参数展示表格格式
      paramInfoColumns: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t('message.apiServices.paramTable.paramName'),
          key: 'name'
        },
        {
          title: this.$t('message.apiServices.paramTable.displayName'),
          key: 'displayName'
        },
        {
          title: this.$t('message.apiServices.paramTable.paramType'),
          key: 'type',
          render: (h, params) => {
            return h('div', this.getParamType(params.row.type));
          }
        },
        {
          title: this.$t('message.apiServices.paramTable.require.title'),
          key: 'required',
          render: (h, params) => {
            return h('div', this.getParamRequire(params.row.required));
          }
        },
        {
          title: this.$t('message.apiServices.paramTable.describe'),
          key: 'description'
        }
      ],
    }
  },
  computed: {

  },
  created() {
    // 获取api相关数据
    api.fetch('/dss/apiservice/queryById', {
      id: this.$route.query.id,
    }, 'get').then((rst) => {
      if (rst.result) {
        // api的基础信息
        this.apiData = rst.result;
        this.formValidate.approvalNo = this.apiData.approvalVo.approvalNo;
        // 更改网页title
        document.title = rst.result.aliasName || rst.result.name;
        // 加工api信息tab的数据
        this.apiInfoData = [
          { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
          { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
          { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
        ]
      }
    }).catch((err) => {
      console.error(err)
    });
  },
  methods: {
    // 刷新获取itsm单号
    refreshAction() {
      if (this.loading) return;
      this.loading = true;
      api.fetch('/dss/apiservice/approvalRefresh', {
        approvalNo: this.apiData.approvalVo.approvalNo
      }, 'get').then((res) => {
        this.loading = false;
        // 根据审批状态提示用户
        // 1，2，3，4分别表示提单成功，审批中，审批通过，审批不通过
        const status = res.approvalStatus
        this.$Message.success(status)
        this.$refs.tokenAuth.hanlderSearch()
      }).catch(() => {
        this.loading = false;
      })
    },
    goBack() {
      this.$router.go(-1)
    },
    handleSubmit(name) {
      this.$refs[name].validate((valid) => {
        if(valid) {
          api.fetch('/dss/apiservice/set', {
            ApprovalNo: this.formValidate.approvalNo
          }, 'post').then(() => {
            this.$Message.seccess(this.$t('message.apiServices.tip.success'))
          })
        } else {
          this.$Message.error(this.$t('message.apiServices.tip.failed'))
        }
      })
    },
    // 转换参数的显示
    getParamType(e) {
      let type = "String";
      e = Number(e);
      if(e === 3) {
        type = "Date"
      } else if(e === 2) {
        type = "Number"
      } else if(e === 4) {
        type = "Array"
      } else {
        type = "String"
      }
      return type;
    },
    getParamRequire(e) {
      let requrired;
      e = Number(e);
      if(e === 2) {
        requrired = this.$t('message.apiServices.paramTable.require.hide');
      } else if(e === 0) {
        requrired = this.$t('message.apiServices.paramTable.require.no');
      } else {
        requrired = this.$t('message.apiServices.paramTable.require.yes');
      }
      return requrired;
    },
    // 编辑备注
    getEditAndConfirm() {
      let flag = this.isRemarksEdit;
      // 如果为true则表示为编辑状态
      if(flag) {
        // 修改成功则改变按钮状态
        if(this.apiData.comment.length > 1024) return this.$Message.error({ content: this.$t('message.apiServices.more1024') });
        api.fetch(`/dss/apiservice/apiCommentUpdate`, {
          id: this.apiData.id,
          comment: this.apiData.comment
        }, 'post').then(() => {
          this.isRemarksEdit = false;
          this.$Message.success("Success")
        }).catch(() => {
          this.$Message.error("Failed")
        });
      } else {
        this.isRemarksEdit = true;
      }
    },
  },
}
</script>

<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';

  .dataServicesContent {
    display: flex;
    flex-direction: column;
    width: $percent-all;
    height: $percent-all;
    padding: 25px 40px;
    .back {
      margin-right: $margin;
      padding-right: $padding;
      font-size: 1.5rem;
      position: relative;
      color: $subsidiary-color;
      cursor: pointer;
      font-weight: 700;
      &::after {
        content: "";
        position: absolute;
        border-right: $border-width-base $border-style-base $border-color-base;
        height: 60%;
        top: 50%;
        right: 0;
        transform: translateY(-50%);
        width: 0;
      }
    }
    .service-content {
      margin-top: 15px;
      margin-left: 20px;
      .top {
        .name {
          font-size: $font-size-large;
          font-weight: 600;
          color: $title-color;
        }
        .status {
          margin-left: 30px;
          font-size: 14px;
        }
      }
      .buttom {
        margin-top: 15px;
        font-size: 14px;
      }
    }
    .section {
      margin-top: 10px;
      .from-title {
        margin: 10px 0;
        color: $title-color;
      }
      .submit {
        display: flex;
        justify-content: center;
        align-items: $align-center;
      }
      .approvalNo-input {
        width: 355px;
        margin-right: 15px;
      }
    }
  }
  .title {
    flex: none;
    color: #080808;
    .breadcrumb {
      display: inline-block;
      font-size: 14px;
      vertical-align: middle;
      @include bg-color($light-base-color, $dark-base-color);
      /deep/.ivu-breadcrumb-item-separator {
        color: #080808;
      }
    }
  }
  .remarks {
    display: flex;
    align-items: flex-start;
    .ivu-alert {
      min-height: 150px;
    }
    .remarksContent {
      flex: 1;
    }
    .remarksButton {
      margin-left: 20px;
      flex: none;
    }
  }
  .approvalNoItem {
    /deep/.ivu-form-item-content {
      display: flex;
      align-items: center;
    }
  }
</style>

