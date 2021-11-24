<template>
  <div class="information-box">
    <div
      v-if="workspaceData">
      <h3>{{$t('message.workspaceManagemnet.baseInfo')}}</h3>
      <div class="information-text">
        <label>{{$t('message.workspaceManagemnet.productsetting.productName')}}<span>{{ workspaceData.name }}</span></label>
        <label>{{$t('message.workspaceManagemnet.productsetting.createTime')}}<span>{{ dateFormatter(workspaceData.createTime) }}</span></label>
        <label>{{$t('message.workspaceManagemnet.productsetting.createBy')}}<span>{{ workspaceData.createBy}}</span></label>
        <label>{{$t('message.workspaceManagemnet.productsetting.department')}}<span>{{ filterDepartment(workspaceData.department) }}</span></label>
        <!-- <label>{{$t('message.workspaceManagemnet.productsetting.admin')}}<span>enjoyyin</span></label> -->
        <label>{{$t('message.workspaceManagemnet.productsetting.status')}}<span>{{ $t('message.workspace.normal') }}</span></label>
        <label>{{$t('message.workspaceManagemnet.productsetting.desc')}}<span>{{ workspaceData.description}}</span></label>
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
import { GetWorkspaceData, GetDepartments } from '@/common/service/apiCommonMethod.js';
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
          title: this.$t('message.workspaceManagemnet.action'),
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
              }, this.$t('message.workspaceManagemnet.delete')),
              h('Button', {
                props: {
                  type: 'text',
                  size: 'small'
                }
              }, this.$t('message.workspaceManagemnet.editor'))
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
      return a ? a.name : '';
    },
    dateFormatter(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    },
    init(){
      GetWorkspaceData(this.$route.query.workspaceId).then(data=>{
        this.workspaceData = data.workspace;
      });
      GetDepartments().then((res) => {
        this.departments = res.departments;
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
  .information-box{
    width: 75%;
    padding: 10px 15px;
    .information-table {
      width: 100%;
      text-align: center;
      padding: 10px 19px;
      /deep/ th.columnClass {
        background-color: #515a6e;
        color: #FFF;
      }
    };
    .information-setting,
    .information-text{
      display: flex;
      flex-wrap: wrap;
      margin-left: 19px;
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
