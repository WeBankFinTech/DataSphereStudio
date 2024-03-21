<template>
  <div class="information-box">
    <div
      v-if="workspaceData">
      <h4>{{$t('message.workspaceManagement.baseInfo')}}</h4>
      <div class="information-text">
        <label>{{$t('message.workspaceManagement.productsetting.productName')}}<span>{{ workspaceData.name }}</span></label>
        <label>{{$t('message.workspaceManagement.productsetting.createTime')}}<span>{{ dateFormatter(workspaceData.createTime) }}</span></label>
        <label>{{$t('message.workspaceManagement.productsetting.createBy')}}<span>{{ workspaceData.createBy}}</span></label>
        <label>{{$t('message.workspaceManagement.productsetting.department')}}<span>{{ workspaceData.department }}</span></label>
        <!-- <label>{{$t('message.workspaceManagement.productsetting.admin')}}<span>enjoyyin</span></label> -->
        <label>{{$t('message.workspaceManagement.productsetting.status')}}<span>{{ $t('message.workspace.normal') }}</span></label>
        <label>{{$t('message.workspaceManagement.productsetting.desc')}}<span>{{ workspaceData.description}}</span></label>
        <label>{{$t('message.workspaceManagement.productsetting.workspaceType')}}<span>{{workspaceData.workspaceType === 'department' ? $t('message.workspace.departmentOrientation') : $t('message.workspace.projectOrientation')}}</span></label>
        <label>{{$t('message.workspaceManagement.productsetting.adminviewall')}}<span>{{workspaceData.adminPermission ? $t('message.workspaceManagement.productsetting.yes') : $t('message.workspaceManagement.productsetting.no')}}</span></label>
      </div>
    </div>
    <!-- <div style="padding: 10px 0;">
      <h3>安全设置</h3>
      <div class="information-setting">
        <label>允许下载结果集：<i-switch size="small"  v-model="switch1" @on-change="change($event, 1)"/></label>
        <label>允许导入数据：<i-switch size="small" v-model="switch2" @on-change="change($event, 2)"/></label>
        <label>允许放完Api接口：<i-switch size="small" v-model="switch3" @on-change="change($event, 3)"/></label>
      </div>
    </div>
    <div style="padding: 10px 0;">
      <h3>计算机引擎信息</h3>
      <div class="information-table">
        <Table :columns="columns1" :data="data1"></Table>
      </div>
    </div> -->
  </div>
</template>
<script>
import moment from 'moment';
import { GetWorkspaceData, GetDepartments } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';
export default {
  data() {
    return {
      workspaceData: null,
      departments: [],
      switch1: false,
      switch2: false,
      switch3: false,
      columns1: [
        {
          title: this.$t('message.workspace.engineType'),
          key: 'type',
          className: 'columnClass'
        },
        {
          title: this.$t('message.workspace.clusterEnv'),
          key: 'env',
          className: 'columnClass'
        },
        {
          title: this.$t('message.workspace.instance'),
          key: 'instance',
          className: 'columnClass'
        },
        {
          title: this.$t('message.workspace.allocatedResources'),
          key: 'resource',
          className: 'columnClass'
        },
        {
          title: this.$t('message.workspaceManagement.action'),
          key: 'action',
          fixed: 'right',
          width: 120,
          align: 'center',
          className: 'columnClass',
          render: (h) => {
            return h('div', [
              h('Button', {
                props: {
                  type: 'text',
                  size: 'small'
                }
              }, this.$t('message.workspaceManagement.delete')),
              h('Button', {
                props: {
                  type: 'text',
                  size: 'small'
                }
              }, this.$t('message.workspaceManagement.editor'))
            ])
          }
        }
      ],
      data1: [
        {
          type: 'Spark',
          env: `BDP ${this.$t('message.workspace.test')}`,
          instance: 'bdp01',
          resource: '<20vcores 10G>'
        },
        {
          type: 'Spark',
          env: `BDP ${this.$t('message.workspace.production')}`,
          instance: 'bdp02',
          resource: '<20vcores 20G>'
        },
        {
          type: 'Spark',
          env: `BDP ${this.$t('message.workspace.test')}`,
          instance: 'bdp03',
          resource: '<20vcores 40G>'
        }
      ]
    }
  },
  created() {
    this.init();
  },
  watch: {
    $route() {
      this.init();
    }
  },
  methods: {
    filterDepartment(id) {
      const a = this.departments.find((item) => id === String(item.id));
      return a ? a.deptName : '';
    },
    dateFormatter(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    },
    init(){
      GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
        this.workspaceData = data.workspace;
      });
      GetDepartments().then((res) => {
        this.departments = res.deptList;
      });
    },
    change (status, flag) {
      if(flag) this[`switch${flag}`] = status;
      this.$Message.info(`${this.$t('message.workspace.switchStatus')}：` + status);
    }
  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .information-box{
    width: 75%;
    padding: 10px 15px;
    @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
    h3 {
      @include font-color($workspace-title-color, $dark-workspace-title-color);
    }
    .information-table {
      width: 100%;
      text-align: center;
      padding: 10px 19px;
      ::v-deep th.columnClass {
        // background-color: #515a6e;
        @include bg-color(#515a6e, #f3f3f3);
        // color: #FFF;
        @include font-color(#FFFFFF, #000000);
      }
    };
    .information-setting,
    .information-text{
      display: flex;
      flex-wrap: wrap;
      margin-left: 19px;
      @include font-color($light-text-color, $dark-text-color);
      label{
        width: 50%;
        padding: 10px 0;
        display: flex;
        align-items: center;
      }
    }
    .information-img{
      display: flex;
      margin-left: 19px;
      padding-top: 8px;
      .information-img-box{
      width: 100px;
      text-align: center;
    }
  }

  }
</style>
