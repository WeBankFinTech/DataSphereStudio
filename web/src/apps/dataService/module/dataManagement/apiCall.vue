<template>
  <div class="manage-wrap">
    <div class="manage-head">API调用</div>
    <div class="filter-box">
      <div class="filter-area">
        <Button type="primary" size="large" icon="ios-search" @click="addAuthorize">{{ $t("message.dataService.addAuthorize") }}</Button>
      </div>
    </div>
    <Table :columns="columns" :data="data" size="large">
      <template slot-scope="{ row, index }" slot="operation">
        <div class="operation-wrap">
          <a class="operation" @click="edit(row, index)">
            {{ $t("message.dataService.edit") }}
          </a>
          <a class="operation" @click="deleteApi(row)">
            {{ $t("message.dataService.delete") }}
          </a>
        </div>
      </template>
    </Table>
    <div class="pagebar">
      <Page
        :total="pageData.total"
        :current="pageData.pageNum"
        show-elevator
        show-sizer
        @on-change="handlePageChange"
        @on-page-size-change="handlePageSizeChange"
      />
    </div>

    <!--确认删除-->
    <Modal v-model="modelConfirm" width="480" :closable="false">
      <div class="modal-confirm-body">
        <div class="confirm-title">
          <Icon custom="iconfont icon-project" size="26"></Icon>
          <span>{{ $t("message.dataService.deleteConfirm") }}</span>
        </div>
        <div class="confirm-desc">删除后，获得权限调用的用户将不能继续调用该API</div>
      </div>
      <div slot="footer">
        <Button type="default" @click="deleteCancel">取消</Button>
        <Button type="primary" @click="deleteConfirm">确定</Button>
      </div>
    </Modal>

    <!--授权弹窗-->
    <Modal
      v-model="modalAuthShow"
      :title="$t('message.dataService.modalAuthTile')"
      :footer-hide="true"
    >
      <Form
        :label-width="120"
        ref="projectForm"
        :model="formData"
        :rules="formValid">
        <FormItem
          :label="$t('message.dataService.authName')"
          prop="authName">
          <Input
            v-model="formData.name"
            :placeholder="$t('message.dataService.inputAuthName')"
          ></Input>
        </FormItem>
        <FormItem :label="$t('message.dataService.authFlow')" prop="authFlow">
          <Select v-model="formData.authFlow">
            <Option v-for="item in selectOrchestratorList" :key="item.dicKey" :value="item.dicKey">
              {{ item.dicName}}
            </Option>
          </Select>
        </FormItem>
        <Form-item>
          <Button size="large">{{$t('message.dataService.cancel')}}</Button>
          <Button type="primary" size="large" style="margin-left: 20px;">{{$t('message.dataService.ok')}}</Button>
        </Form-item>
      </Form>
    </Modal>
  </div>
</template>
<script>
// import api from "@/common/service/api";
export default {
  data() {
    return {
      columns: [
        {
          title: '姓名',
          key: 'name'
        },
        {
          title: '年龄',
          key: 'age'
        },
        {
          title: '地址',
          key: 'address'
        },
        {
          title: this.$t("message.permissions.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      data: [
        {
          name: '李小红',
          age: 30,
          address: '上海市浦东新区世纪大道'
        },
        {
          name: '周小伟',
          age: 26,
          address: '深圳市南山区深南大道'
        }
      ],
      pageData: {
        total: 20,
        pageNum: 1,
        pageSize: 10
      },

      modalAuthShow: false,
      formData: {
        name: '',
      },
      modelConfirm: false,
      selectedApi: null
    }
  },
  computed: {
    formValid() {
      return {
        authName: [
          { required: true, message: this.$t('message.workflow.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}128`, max: 128 }
        ],
        description: [
          { required: true, trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}200`, max: 200 },
        ],
        authFlow: [
          { required: true, trigger: 'blur' }
        ]
      }
    },
  },
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // api的基础信息
    //     this.apiData = rst.result;
    //     this.formValidate.approvalNo = this.apiData.approvalVo.approvalNo;
    //     // 更改网页title
    //     document.title = rst.result.aliasName || rst.result.name;
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
    addAuthorize() {
      this.modalAuthShow = true;
    },
    deleteApi(row) {
      this.selectedApi = row;
      this.modelConfirm = true;
    },
    deleteCancel() {
      this.selectedApi = null;
      this.modelConfirm = false;
    },
    deleteConfirm() {
      console.log('confirm', this.selectedApi)
      this.modelConfirm = false;
    },
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNum = page;
    },
  },
}
</script>

<style lang="scss" scoped>
.manage-wrap {
  padding: 0 24px;
  background: #fff;
  .manage-head {
    margin-bottom: 15px;
    padding-bottom: 10px;
    font-size: 16px;
    color: #333;
    font-weight: bold;
    border-bottom: 1px solid #DEE4EC;
  }
  .filter-box {
    margin-bottom: 20px;
    overflow: hidden;
    .filter-area {
      float: right;
    }
  }
  .operation-wrap {
    margin-left: -10px;
  }
  .operation {
    font-size: 14px;
    line-height: 20px;
    color: #2E92F7;
    padding: 0 10px;
    border-right: 1px solid #D8D8D8;
    &:last-child {
      border-right: 0;
    }
  }
  .pagebar {
    float: right;
    margin-top: 15px;
    padding: 10px 0;
  }
}

.modal-confirm-body {
  .confirm-title {
    margin-top: 10px;
    display: flex;
    align-items: center;
    color: #FF9F3A;
    span {
      margin-left: 15px;
      font-size: 16px;
      line-height: 24px;
      color: #333;
    }
  }
  .confirm-desc {
    margin-top: 15px;
    margin-bottom: 10px;
    margin-left: 42px;
    font-size: 14px;
    line-height: 20px;
    color: #666;
  }
}
</style>

