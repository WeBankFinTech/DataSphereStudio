<template>
  <div class="manage-wrap">
    <div class="manage-head">{{ $t("message.dataService.apiCall.apiCall") }}</div>
    <div class="filter-box">
      <div class="filter-area">
        <Button type="primary" size="large" @click="addAuthorize">
          <Icon custom="iconfont icon-xinzeng1" size="12"></Icon>
          {{ $t("message.dataService.apiCall.addAuthorize") }}
        </Button>
      </div>
    </div>
    <Table :columns="columns" :data="apiCallList" size="large">
      <template slot-scope="{ row }" slot="operation">
        <div class="operation-wrap">
          <a class="operation" @click="editApiAuth(row)">
            {{ $t("message.dataService.apiCall.edit") }}
          </a>
          <a class="operation" @click="deleteApi(row)">
            {{ $t("message.dataService.apiCall.delete") }}
          </a>
        </div>
      </template>
    </Table>
    <div class="pagebar" v-if="pageData.total">
      <Page
        :total="pageData.total"
        :current="pageData.pageNow"
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
          <span>{{ $t("message.dataService.apiCall.deleteApiCallTitle") }}</span>
        </div>
        <div class="confirm-desc">{{ $t("message.dataService.apiCall.deleteApiCallDesc") }}</div>
      </div>
      <div slot="footer">
        <Button type="default" @click="deleteCancel">{{$t('message.dataService.cancel')}}</Button>
        <Button type="primary" @click="deleteConfirm">{{$t('message.dataService.ok')}}</Button>
      </div>
    </Modal>

    <!--授权弹窗-->
    <Modal
      v-model="modalAuthShow"
      @on-cancel="authCancel"
      :title="$t('message.dataService.apiCall.authForm.modalAuthTile')"
    >
      <Form
        :label-width="120"
        ref="authForm"
        :model="authFormData"
        :rules="formValid">
        <FormItem
          :label="$t('message.dataService.apiCall.authForm.labelName')"
          prop="caller">
          <Input
            v-model="authFormData.caller"
            :placeholder="$t('message.dataService.apiCall.authForm.holderName')"
          ></Input>
        </FormItem>
        <Form-item
          :label="$t('message.dataService.apiCall.authForm.labelExpire')"
          prop="expire"
        >
          <Radio-group v-model="authFormData.expire">
            <Radio label="short">{{$t('message.dataService.apiCall.authForm.short')}}</Radio>
            <Radio label="long">{{$t('message.dataService.apiCall.authForm.long')}}</Radio>
          </Radio-group>
        </Form-item>
        <Form-item
          v-if="authFormData.expire == 'short'"
          prop="expireDate"
        >
          <Date-picker 
            type="date"
            format="yyyy-MM-dd"
            :options="dateOptions"
            :placeholder="$t('message.dataService.apiCall.authForm.holderExpire')"
            v-model="authFormData.expireDate"></Date-picker>
        </Form-item>
        <Form-item :label="$t('message.dataService.apiCall.authForm.labelFlow')" prop="groupId">
          <Select v-model="authFormData.groupId">
            <Option v-for="item in groups" :key="item.groupId" :value="`${item.groupId}`">
              {{ item.groupName}}
            </Option>
          </Select>
        </Form-item>
      </Form>
      <div slot="footer">
        <Button @click="authCancel">{{$t('message.dataService.cancel')}}</Button>
        <Button type="primary" @click="authSubmit">{{$t('message.dataService.ok')}}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import api from "@/common/service/api";
export default {
  data() {
    return {
      columns: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t("message.dataService.apiCall.col_caller"),
          key: 'caller'
        },
        {
          title: 'Token',
          key: 'token'
        },
        {
          title: this.$t("message.dataService.apiCall.col_expire"),
          key: 'expire'
        },
        {
          title: this.$t("message.dataService.apiCall.col_createTime"),
          key: 'createTime'
        },
        {
          title: this.$t("message.dataService.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      apiCallList: [],
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10
      },
      groups: [],
      modalAuthShow: false,
      authFormData: {
        caller: '',
        groupId: '',
        expire: '',
        expireDate: ''
      },
      dateOptions: {
        disabledDate (date) {
          return date && date.valueOf() < Date.now() - 86400000;
        }
      },
      modelConfirm: false,
      selectedApi: null
    }
  },
  computed: {
    formValid() {
      return {
        caller: [
          { required: true, message: this.$t('message.dataService.apiCall.authForm.enterName'), trigger: 'blur' },
        ],
        groupId: [
          { required: true, message: this.$t('message.dataService.apiCall.authForm.enterFlow'), trigger: 'change' }
        ],
        expire: [
          { required: true, message: this.$t('message.dataService.apiCall.authForm.enterExpire'), trigger: 'change' }
        ],
        expireDate: [
          { 
            validator: (rule, value, callback) => {
              if (this.authFormData.expire === 'short') {
                if (!value) {
                  callback(new Error(this.$t('message.dataService.apiCall.authForm.enterExpire')));
                } else {
                  callback();
                }
              } else {
                callback();
              }
            }, 
            trigger: 'blur' 
          }
        ]
      }
    },
  },
  created() {
    this.getApiGroup();
    this.getApiCallList();
  },
  methods: {
    dateFormat(date) {
      const dt = date ? date : new Date();
      const format = [
        dt.getFullYear(), dt.getMonth() + 1, dt.getDate()
      ].join('-').replace(/(?=\b\d\b)/g, '0'); // 正则补零
      return `${format} 00:00:00`;
    },
    getApiGroup() {
      api.fetch('/dss/framework/dbapi/apiauth/apigroup', {
        workspaceId: this.$route.query.workspaceId,
      }, 'get').then((res) => {
        if (res.list) {
          this.groups = res.list;
        }
      }).catch((err) => {
        console.error(err)
      });
    },
    getApiCallList() {
      api.fetch('/dss/framework/dbapi/apiauth/list', {
        workspaceId: this.$route.query.workspaceId,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        if (res.list) {
          this.apiCallList = res.list;
          this.pageData.total = res.total;
        }
      }).catch((err) => {
        console.error(err)
      });
    },
    addAuthorize() {
      this.modalAuthShow = true;
    },
    authCancel() {
      this.modalAuthShow = false;
      this.authFormData = {
        caller: '',
        groupId: '',
        expire: '',
        expireDate: ''
      }
      this.$refs['authForm'].resetFields();
    },
    authSubmit() {
      this.$refs['authForm'].validate((valid) => {
        if (valid) {
          const data = {
            workspaceId: this.$route.query.workspaceId,
            caller: this.authFormData.caller,
            groupId: this.authFormData.groupId,
          }
          if (this.authFormData.expire == 'short') {
            data.expire = `${this.dateFormat(this.authFormData.expireDate)} 00:00:00`;
          } else if (this.authFormData.expire == 'long') {
            const date = new Date(Date.now() + 365*86400*1000)
            data.expire = `${this.dateFormat(date)} 00:00:00`;
          }
          if (this.authFormData.id) {
            data.id = this.authFormData.id;
          }
          api.fetch('/dss/framework/dbapi/apiauth/save', data, 'post').then((res) => {
            this.authCancel();
            this.pageData = {
              total: 0,
              pageNow: 1,
              pageSize: 10
            }
            this.getApiCallList();
          }).catch((err) => {
            console.error(err)
          });
        }
      })
    },
    editApiAuth(auth) {
      this.modalAuthShow = true;
      this.authFormData = {
        id: auth.id,
        caller: auth.caller,
        groupId: auth.groupId
      }
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
      this.modelConfirm = false;
      api.fetch(`/dss/framework/dbapi/apiauth/${this.selectedApi.id}`, {}, 'delete').then((res) => {
        this.getApiCallList();
      }).catch((err) => {
        console.error(err)
      });
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getApiCallList();
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
      this.getApiCallList();
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

