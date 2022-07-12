<template>
  <div class="user-serchbar-box">
    <h4 style="margin-bottom: 20px;" class="user-table-title">{{$t('message.workspaceManagement.userManagement')}}</h4>
    <formserch
      v-if="canAdd()"
      @click-serach="search"
      @click-creater="creater"
      :searchBar="searchBar"
      :optionType="optionType"
    ></formserch>
    <Table
      border
      highlight-row
      align="center"
      :columns="data.columns"
      :data="data.datalist"
    >
      <template slot-scope="{ row }" slot="role">
        <span>{{ rolelist(row) }}</span>
      </template>
      <template slot-scope="{ row }" slot="department">
        <span>{{row.office + '-' + row.department}}</span>
      </template>
      <template slot-scope="{ row, index }" slot="action">
        <Button v-if="canDelete(row)" type="error" size="small" @click="remove(row, index)">{{$t('message.workspaceManagement.delete')}}</Button>
        <Button
          v-if="canEdit(row)"
          type="primary"
          size="small"
          style="margin-left: 10px"
          @click="edit(row, index)"
        >{{$t('message.workspaceManagement.editor')}}</Button
        >
      </template>
    </Table>
    <div class="user-table-page">
      <Page
        :total="pageSetting.total"
        :page-size="pageSetting.pageSize"
        :current="pageSetting.current"
        size="small"
        show-total
        show-elevator
        @on-change="changePage"
      />
    </div>
    <Modal v-model="delshow" width="360" class-name="delete-modal">
      <p slot="header" class="delete-modal-header">
        <Icon type="ios-information-circle" />
        <span>{{ $t("message.workspaceManagement.waring") }}</span>
      </p>
      <div>
        <div>
          {{$t('message.workspaceManagement.deleteHintMsg')}}
        </div>
      </div>
      <div slot="footer">
        <Button type="error" size="large" long @click="del">{{$t('message.workspaceManagement.comfirmDelete')}}</Button>
      </div>
    </Modal>
    <Modal class-name="adduser-box" :closable="false" v-model="creatershow" :title="$t('message.workspaceManagement.addUser')">
      <Form ref="addUser" :model="useradd" :rules="addrule" :label-width="80">
        <FormItem label="用户类型">
          <RadioGroup v-model="uType" @on-change="useradd.id=''">
            <Radio label="real"
            >实名用户</Radio
            >
            <Radio label="notreal"
            >非实名用户</Radio
            >
          </RadioGroup>
        </FormItem>
        <FormItem v-if="uType==='real'" :label="$t('message.workspaceManagement.user')" prop="id">
          <Row>
            <Col span="12" style="width: 196px" size="small">
              <Select
                v-model="useradd.id"
                filterable
                remote
                :remote-method="remoteMethod1"
                @on-open-change="queryWhenOpen"
                :loading="loading1">
                <Option v-for="(option, index) in options" :value="option.id" :key="index" :disabled="option.disabled">{{option.username}}</Option>
              </Select>
            </Col>
          </Row>
        </FormItem>
        <FormItem v-if="uType === 'notreal'" :label="$t('message.workspaceManagement.user')" prop="id">
          <Input v-model="useradd.id"  style="width: 196px" />
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.role')" prop="role">
          <CheckboxGroup v-model="useradd.role">
            <Checkbox
              v-for="item in workspaceRoles"
              :key="item.roleId"
              :label="item.roleId"
              :disabled="isSuperAdmin(item)"
            >{{item.roleFrontName}}</Checkbox>
          </CheckboxGroup>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="Cancel">{{
          $t("message.workspaceManagement.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="Ok('addUser')">{{
          $t("message.workspaceManagement.ok")
        }}</Button>
      </div>
    </Modal>
    <Modal :closable="false" v-model="editusershow" :title="$t('message.workspaceManagement.editUser')">
      <Form
        ref="editUser"
        :model="edituser"
        :rules="editrule"
        :label-width="80"
      >
        <FormItem :label="$t('message.workspaceManagement.role')" prop="role">
          <CheckboxGroup v-model="edituser.role">
            <Checkbox
              v-for="item in workspaceRoles"
              :key="item.roleId"
              :label="item.roleId"
              :disabled="isSuperAdmin(item)"
            >{{item.roleFrontName}}</Checkbox>
          </CheckboxGroup>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="Cancel">{{
          $t("message.workspaceManagement.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="Ok('editUser')">{{
          $t("message.workspaceManagement.ok")
        }}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import storage from '@dataspherestudio/shared/common/helper/storage';
import api from '@dataspherestudio/shared/common/service/api';
import moment from 'moment';
import formserch from "../component/formsechbar";
import { GetWorkspaceUserManagement } from '@dataspherestudio/shared/common/service/apiCommonMethod.js';

export default {
  components: {
    formserch
  },
  data() {
    return {
      delshow: false,
      creatershow: false,
      editusershow: false,
      loading: true,
      row: '',
      loading1: false,
      options: [],
      pageSetting: {
        total: 0,
        pageSize: 10,
        current: 1,
      },
      uType: 'real',
      workspaceRoles: [],
      data: {
        columns: [],
        datalist: [],
        datalistAll: [],
      },
      useradd: {},
      edituser: {},
      editrule: {
        role: [{ required: true, type: 'array', min: 1, message: this.$t('message.workspaceManagement.selectRoleMsg'), trigger: 'change' }]
      },
      addrule: {
        id: [
          { required: true, message: this.$t('message.workspaceManagement.addruleMsg'), trigger: "blur" },
        ],
        role: [
          { required: true, type: 'array', min: 1, message: this.$t('message.workspaceManagement.selectRoleMsg'), trigger: 'change' },
        ]
      },
      searchBar: {
        title: this.$t('message.workspaceManagement.umAccount'),
        searchText: "",
        engine: 0,
        status: 0
      },
      optionType: {
        title: this.$t('message.workspaceManagement.role') + ':',
        status: [],
      },
    };
  },

  created(){
    this.workspaceId =parseInt(this.$route.query.workspaceId);
  },
  mounted() {
    this.init()
    this.getUserList()
  },
  methods: {
    isSuperAdmin(item){
      const isAdmin = this.getIsAdmin();
      return item.roleId===1 && !isAdmin;
    },
    canAdd(){
      //管理员可以add
      const isAdmin = this.getIsAdmin();
      const workspaceRoles = storage.get(`workspaceRoles`) || [];
      if (isAdmin || workspaceRoles.indexOf('admin') > -1) {
        return true;
      } else {
        return false;
      }
    },
    canDelete(row = {}){
      //创建者和超级管理员可以删除
      const isAdmin = this.getIsAdmin();
      const username = this.getUserName();
      if (row.creator === username || isAdmin) {
        return true;
      } else {
        return false;
      }
    },
    canEdit(row = {}){
      //创建者,超级管理员isAdmin,空间管理员可以edit(非isAdmin的空间管理员帐号不能修改其他空间管理员)
      const isAdmin = this.getIsAdmin();
      const username = this.getUserName();
      if (row.creator === username || isAdmin) {
        return true;
      } else {
        // 非isAdmin的空间管理员帐号不能修改其他空间管理员
        const workspaceRoles = storage.get(`workspaceRoles`) || [];
        if (workspaceRoles.indexOf('admin') > -1 && row.roles.indexOf(1) == -1) {
          return true;
        }
        return false;
      }
    },
    queryWhenOpen(isOpen) {
      if (isOpen) {
        this.getUserList()
      }
    },
    getUserList(query) {
      this.loading1 = true
      if (this.list && query) {
        this.options = this.list.filter(item => {
          return item.username.toLowerCase().indexOf(query.toLowerCase()) > -1
        }).slice(0, 250);
        this.loading1 = false
        return
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}listAllUsers`, 'get').then((res)=>{
        this.loading1 = false
        let list = res.users
        this.data.datalist.forEach(item => {
          for (let i = 0; i < list.length; i++) {
            if (list[i].username === item.name) {
              list[i].disabled = true
              break
            }
          }
        })
        this.list = list;
        this.options = list.slice(0, 250);
      })
    },
    remoteMethod1(query) {
      if (query !== "") {
        this.getUserList(query)
      } else {
        this.options = []
      }
    },
    init() {
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getWorkspaceRoles`,{
        workspaceId: this.workspaceId,
      },'get').then((rst)=>{
        this.workspaceRoles =  rst.workspaceRoles;
        this.optionType.status = this.statuslist(rst.workspaceRoles)
      })
      this.pageSetting = {
        total: 0,
        pageSize: 10,
        current: 1
      }
      this.search();
    },
    search() {
      const params = this.getParams();
      this.data.columns  = this.getColumns();
      GetWorkspaceUserManagement(params).then((rst) => {
        this.pageSetting.total = rst.total;
        this.data.datalistAll = this.renderFormatTime(rst.workspaceUsers);
        this.data.datalist = this.data.datalistAll.slice(0, this.pageSetting.pageSize);
      }).catch(() => {

      });
    },
    getColumns(){
      const column = [
        { title: this.$t('message.workspaceManagement.name'), key: "name", align: "center" },
        { title: this.$t('message.workspaceManagement.role'), slot: "role", align: "center",width: 250 },
        { title: this.$t('message.workspaceManagement.department'), slot: "department", align: "center" },
        { title: this.$t('message.workspaceManagement.creator'), key: "creator", align: "center" },
        { title: this.$t('message.workspaceManagement.joinTime'), key: "joinTime", align: "center" },
        { title: this.$t('message.workspaceManagement.action'), slot: "action", width: 150, align: "center" }
      ];
      return column;
    },
    getParams(page){
      const params =  {
        workspaceId: this.workspaceId,
        userName: this.searchBar.searchText,
        roleName: this.searchBar.status,
        department: this.searchBar.engine,
        pageNow: page || this.pageSetting.current,
        pageSize: 10
      }
      if(this.searchBar.searchText){
        delete params.roleName;
        delete params.status;
      }else{
        let {engine,status} = this.searchBar;
        if(status===0){
          delete params.roleName;
        }
        if(engine===0){
          delete params.department;
        }
        delete params.userName;
      }
      return params
    },
    changePage(page){
      const params = this.getParams(page);
      this.column = this.getColumns();
      GetWorkspaceUserManagement(params).then((rst) => {
        this.pageSetting.total = rst.total;
        this.data.datalist = this.renderFormatTime(rst.workspaceUsers);
      }).catch(() => {
      });
    },
    remove(row) {
      this.delshow = true;
      this.row = row;
    },
    del() {
      const curUserName = this.getUserName();
      if(this.row.creator === this.row.name){
        return this.$Message.warning(this.$t("message.workspaceManagement.notsupported"));
      }
      if(curUserName === this.row.name){
        return this.$Message.error(this.$t('message.workspaceManagement.notDeleteMsg'));
      }
      this.delshow = false;
      const params = {
        workspaceId: this.workspaceId,
        userName: this.row.name
      };
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}deleteWorkspaceUser`, params).then(() => {
        this.init()
        this.$Message.success(this.$t('message.workspaceManagement.deleteSuccess'));
      });
    },
    edit(row) {
      // 如果是当前用户修改自己的权限，提示用户不能修改
      if (row.name === row.creator) return this.$Message.warning(this.$t("message.workspaceManagement.notsupported"));
      this.edituser = {
        role: [...row.roles]
      }
      this.row = row;
      this.editusershow = true;
    },
    creater() {
      this.useradd = {
        id: "",
        name: "",
        role: []
      }
      this.creatershow = true;
    },
    Ok(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          if(name === 'addUser') {
            this.useradd.id ? this.createuser() : this.$Message.error(this.$t("message.workspaceManagement.addruleMsg"));
          }
          if(name === 'editUser') {
            this.editrole()
          }
        } else {
          this.$Message.warning(this.$t("message.workspaceManagement.failedNotice"));
        }
      });
    },
    Cancel() {
      this.creatershow = false;
      this.editusershow = false;
      this.options = [];
    },
    createuser(){
      let id = this.useradd.id
      let userName
      for (let i = 0; i < this.options.length; i++) {
        let option = this.options[i]
        if (option.id === id) {
          userName = option.username
          break
        }
      }
      const params = {
        roles: this.useradd.role,
        workspaceId: this.workspaceId
      }
      if (this.uType === 'notreal') {
        params.userName = this.useradd.id
      } else {
        params.userName = userName
        params.userId = id
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}addWorkspaceUser`, params).then(() => {
        this.init()
        this.$Message.success(this.$t('message.workspaceManagement.addSuccess'))
        this.options = []
      }).catch(() => {
        this.$Message.warning(this.$t('message.workspaceManagement.addFail'))
        this.options = []
      })
      this.creatershow = false;
    },
    editrole(){
      let name = this.row.name
      const params = {
        roles: this.edituser.role,
        workspaceId: this.workspaceId,
        userName: name,
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}updateWorkspaceUser`, params).then(() => {
        this.init()
        // 如果用户自己改了自己的权限，需要去更新左侧菜单栏
        if (name === this.getUserName()) {
          this.dispatch('WorkspaceHome:updateSidebar');
        }
        this.$Message.success(this.$t('message.workspaceManagement.updataSuccess'));
      }).catch(() => {
        this.$Message.warning('');
      });
      this.editusershow = false;
    },
    renderFormatTime(data){
      data.forEach(item => {
        item.joinTime = moment.unix(item.joinTime / 1000).format('YYYY-MM-DD HH:mm:ss')
      });
      return data;
    },
    rolelist(data){
      let  str  = '';
      data.roles.forEach(element => {
        this.workspaceRoles.forEach(item => {
          if (item.roleId === element) {
            str += item.roleFrontName + "-"
          }
        })
      });
      str = str.substring(0, str.lastIndexOf('-'));
      return  str
    },
    statuslist(data){
      if (!data) return
      let arr = []
      let obj = {}
      data.forEach(item=>{
        obj={
          label: item.roleFrontName,
          value: item.roleName
        }
        arr.push(obj)
      })
      arr.unshift({label: this.$t('message.workspaceManagement.all'), value: 0})
      return arr
    },
    departmentlist(data){
      if (!data) return
      let arr = []
      let obj = {}
      data.forEach(item=>{
        obj={
          label: item.name,
          value: item.frontName
        }
        arr.push(obj)
      })
      arr.unshift({label: this.$t('message.workspaceManagement.all'), value: 0})
      return arr
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.user-serchbar-box {
  padding: 10px 15px;
  min-width: 1217px;
  .user-table-title {
    @include font-color($workspace-title-color, $dark-workspace-title-color);
  }

  .user-table-page {
    text-align: center;
    padding-top: 10px;
    @include font-color($light-text-desc-color, $dark-text-desc-color);
  }
}
</style>
