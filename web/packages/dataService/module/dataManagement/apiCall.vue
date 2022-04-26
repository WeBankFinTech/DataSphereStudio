<template>
  <div class="manage-wrap">
    <div class="manage-head">{{ $t("message.dataService.apiCall.apiCall") }}</div>
    <div class="filter-box">
      <div class="filter-input">
        <Input v-model="apiCaller" icon="ios-search" :placeholder='$t("message.dataService.apiCall.col_caller")'
          @on-click="handleSearch" @on-enter="handleSearch" clearable @on-clear="handleSearch" />
      </div>
      <div class="filter-area">
        <Button type="primary" size="large" @click="addAuthorize">
          <SvgIcon icon-class="xinzeng" />
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
    <Spin v-show="loading" size="large" fix/>
    <div class="pagebar" v-if="pageData.total">
      <Page
        :total="pageData.total"
        :current="pageData.pageNow"
        show-elevator
        show-sizer
        show-total
        @on-change="handlePageChange"
        @on-page-size-change="handlePageSizeChange"
      />
    </div>

    <!--确认删除-->
    <Modal v-model="modalConfirm" width="480" :closable="false">
      <div class="modal-confirm-body">
        <div class="confirm-title">
          <SvgIcon class="icon" icon-class="project-workflow" />
          <span class="text">{{ $t("message.dataService.apiCall.deleteApiCallTitle") }}</span>
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
            :maxlength=21
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
          <Select filterable v-model="authFormData.groupId" :disabled="!!authFormData.id">
            <Option v-for="item in groups" :key="item.groupId" :value="`${item.groupId}`">{{ item.groupName}}</Option>
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
import util from '../common/util';
import api from '@dataspherestudio/shared/common/service/api';
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
          title: this.$t("message.dataService.apiCall.col_group"),
          key: 'groupName'
        },
        {
          title: this.$t("message.dataService.apiCall.col_token"),
          key: 'token',
          width: '300'
        },
        {
          title: this.$t("message.dataService.apiCall.col_expire"),
          key: 'expire'
        },
        {
          title: this.$t("message.dataService.apiCall.col_updateTime"),
          key: 'updateTime'
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
      loading: true,
      apiCaller: '',
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
      modalConfirm: false,
      selectedApi: null
    }
  },
  computed: {
    formValid() {
      return {
        caller: [
          { required: true, message: this.$t('message.dataService.apiCall.authForm.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.dataService.apiCall.authForm.enterNameLength')}20`, max: 20 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.dataService.apiCall.authForm.enterNameDesc'), trigger: 'blur' }
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
    getApiGroup() {
      api.fetch('/dss/data/api/apiauth/apigroup', {
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
      this.loading = true;
      api.fetch('/dss/data/api/apiauth/list', {
        workspaceId: this.$route.query.workspaceId,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
        caller: this.apiCaller.trim()
      }, 'get').then((res) => {
        if (res.list) {
          this.loading = false;
          this.apiCallList = res.list;
          this.pageData.total = res.total;
        }
      }).catch((err) => {
        console.error(err)
        this.loading = false;
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
            data.expire = `${util.dateFormat(this.authFormData.expireDate, '23:59:59')}`;
          } else if (this.authFormData.expire == 'long') {
            const date = new Date(Date.now() + 365*86400*1000)
            data.expire = `${util.dateFormat(date, '23:59:59')}`;
          }
          if (this.authFormData.id) {
            data.id = this.authFormData.id;
          }
          api.fetch('/dss/data/api/apiauth/save', data, 'post').then(() => {
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
        groupId: `${auth.groupId}`,
        expire: 'short', // 统一归属到短期
        expireDate: auth.expire
      }
    },
    handleSearch() {
      this.pageData.pageNow = 1;
      this.getApiCallList();
    },
    deleteApi(row) {
      this.selectedApi = row;
      this.modalConfirm = true;
    },
    deleteCancel() {
      this.selectedApi = null;
      this.modalConfirm = false;
    },
    deleteConfirm() {
      this.modalConfirm = false;
      api.fetch(`/dss/data/api/apiauth/${this.selectedApi.id}`, {}, 'post').then(() => {
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
@import "@dataspherestudio/shared/common/style/variables.scss";
.manage-wrap {
  position: relative;
  padding: 0 24px;
  overflow: hidden;
  min-height: calc(100% - 78px);
  @include bg-color(#fff, $dark-base-color);
  .manage-head {
    margin-bottom: 15px;
    padding-bottom: 10px;
    font-size: 16px;
    @include font-color(#333, $dark-text-color);
    font-weight: bold;
    border-bottom: 1px solid #dee4ec;
    @include border-color(#dee4ec, $dark-border-color);
  }
  .filter-box {
    margin-bottom: 20px;
    overflow: hidden;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    .filter-input{
      width: 200px;
      margin-right: 12px;
    }
    //.filter-area {
    //  float: right;
    //}
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
    @include border-color(#D8D8D8, $dark-border-color);
    &:last-child {
      border-right: 0;
    }
  }
  .pagebar {
    float: right;
    margin: 15px 0;
    padding: 10px 0;
  }
}

.modal-confirm-body {
  .confirm-title {
    margin-top: 10px;
    color: #FF9F3A;
    .text {
      margin-left: 15px;
      font-size: 16px;
      line-height: 24px;
      @include font-color(#333, $dark-text-color);
    }
    .icon {
      font-size: 26px;
    }
  }
  .confirm-desc {
    margin-top: 15px;
    margin-bottom: 10px;
    margin-left: 42px;
    font-size: 14px;
    line-height: 20px;
    @include font-color(#666, $dark-text-color);
  }
}
</style>

