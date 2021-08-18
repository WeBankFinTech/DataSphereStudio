<template>
  <div class="user-serchbar-box">
    <h3>{{$t('message.workspaceManagemnet.userManagement')}}</h3>
    <formserch
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
      <template slot-scope="{ row, index }" slot="action">
        <Button type="error" size="small" @click="remove(row, index)"
        >{{$t('message.workspaceManagemnet.delete')}}</Button
        >
        <Button
          type="primary"
          size="small"
          style="margin-left: 10px"
          @click="edit(row, index)"
        >{{$t('message.workspaceManagemnet.editor')}}</Button
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
        <span>{{ $t("message.workspaceManagemnet.waring") }}</span>
      </p>
      <div>
        <div>
          {{$t('message.workspaceManagemnet.deleteHintMsg')}}
        </div>
      </div>
      <div slot="footer">
        <Button type="error" size="large" long @click="del">{{$t('message.workspaceManagemnet.comfirmDelete')}}</Button>
      </div>
    </Modal>
    <Modal class-name="adduser-box" :closable="false" v-model="creatershow" :title="$t('message.workspaceManagemnet.addUser')">
      <Form ref="addUser" :model="useradd" :rules="addrule" :label-width="80">
        <FormItem :label="$t('message.workspaceManagemnet.user')" prop="name">
          <Row>
            <Col span="12" style="width: 196px" size="small">
            <Select
              v-model="useradd.name"
              filterable
              remote
              :remote-method="remoteMethod1"
              :loading="loading1">
              <Option v-for="(option, index) in options" :value="option.value" :key="index">{{option.label}}</Option>
            </Select>
            </Col>
          </Row>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagemnet.role')" prop="role">
          <CheckboxGroup v-model="useradd.role">
            <Checkbox
              v-for="item in workspaceRoles"
              :key="item.roleId"
              :label="item.roleId"
            >{{item.roleFrontName}}</Checkbox>
          </CheckboxGroup>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="Cancel">{{
          $t("message.workspaceManagemnet.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="Ok('addUser')">{{
          $t("message.workspaceManagemnet.ok")
        }}</Button>
      </div>
    </Modal>
    <Modal :closable="false" v-model="editusershow" :title="$t('message.workspaceManagemnet.editUser')">
      <Form
        ref="editUser"
        :model="edituser"
        :rules="editrule"
        :label-width="80"
      >
        <FormItem :label="$t('message.workspaceManagemnet.role')" prop="role">
          <CheckboxGroup v-model="edituser.role">
            <Checkbox
              v-for="item in workspaceRoles"
              :key="item.roleId"
              :label="item.roleId"
            >{{item.roleFrontName}}</Checkbox>
          </CheckboxGroup>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="Cancel">{{
          $t("message.workspaceManagemnet.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="Ok('editUser')">{{
          $t("message.workspaceManagemnet.ok")
        }}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import storage from "@/common/helper/storage";
import api from "@/common/service/api";
import moment from 'moment';
import formserch from "../component/formsechbar";
import { GetWorkspaceUserManagement } from '@/common/service/apiCommonMethod.js';

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
      usernamelist: [],
      options: [],
      pageSetting: {
        total: 0,
        pageSize: 10,
        current: 1,
      },
      workspaceRoles: [],
      data: {
        columns: [],
        datalist: []
      },
      useradd: {},
      edituser: {},
      editrule: {
        role: [{ required: true, type: 'array', min: 1, message: this.$t('message.workspaceManagemnet.selectRoleMsg'), trigger: 'change' }]
      },
      addrule: {
        name: [
          { required: true, message: this.$t('message.workspaceManagemnet.addruleMsg'), trigger: "blur" },
        ],
        role: [
          { required: true, type: 'array', min: 1, message: this.$t('message.workspaceManagemnet.selectRoleMsg'), trigger: 'change' },
        ]
      },
      searchBar: {
        title: this.$t('message.workspaceManagemnet.umAccount'),
        searchText: "",
        engine: 0,
        status: 0
      },
      optionType: {
        title: this.$t('message.workspaceManagemnet.role') + ':',
        status: [],
      },
    };
  },

  created(){
    this.workspaceId =parseInt(this.$route.query.workspaceId);
  },
  mounted() {
    this.username()
    this.init();
  },
  methods: {
    remoteMethod1(query) {
      if (query !== "") {
        this.loading1 = true;
        setTimeout(() => {
          this.loading1 = false;
          const list = this.usernamelist.map(item => {
            return {
              value: item,
              label: item
            };
          });
          this.options = list.filter(
            item => item.label.toLowerCase().indexOf(query.toLowerCase()) > -1
          );
        }, 200);
      } else {
        this.options = [];
      }
    },
    init() {
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getWorkspaceRoles`,{
        workspaceId: this.workspaceId,
      },'get').then((rst)=>{
        this.workspaceRoles =  rst.workspaceRoles;
        this.optionType.status = this.statuslist(rst.workspaceRoles)
      })
      // api.fetch('/dss/framework/workspace/listDepartments',{
      //   workspaceId: this.workspaceId,
      // },'get').then((rst)=>{
      //   this.optionType.engine = this.departmentlist(rst.departments)
      // })
      this.search()
    },
    search() {
      const params = this.getParams();
      this.data.columns  = this.getColumns();
      GetWorkspaceUserManagement(params).then((rst) => {
        this.pageSetting.total = rst.total;
        this.data.datalist = this.renderFormatTime(rst.workspaceUsers);
      }).catch(() => {

      });
    },
    username(){
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}listAllUsers`,{
      },'get').then((rst)=>{
        rst.users.forEach(item=>{
          this.usernamelist.push(item.username)
        })
      })
    },
    getColumns(){
      const column = [
        { title: this.$t('message.workspaceManagemnet.name'), key: "name", align: "center" },
        { title: this.$t('message.workspaceManagemnet.role'), slot: "role", align: "center",width: 250 },
        { title: this.$t('message.workspaceManagemnet.department'), key: "department", align: "center" },
        { title: this.$t('message.workspaceManagemnet.office'), key: "office", align: "center" },
        { title: this.$t('message.workspaceManagemnet.creator'), key: "creator", align: "center" },
        { title: this.$t('message.workspaceManagemnet.joinTime'), key: "joinTime", align: "center" },
        { title: this.$t('message.workspaceManagemnet.action'), slot: "action", width: 150, align: "center" }
      ];
      return column;
    },
    getParams(page){
      const params =  {
        workspaceId: this.workspaceId,
        username: this.searchBar.searchText,
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
        delete params.username;
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
      const userInfoName = storage.get("baseInfo", 'local') ? storage.get("baseInfo", 'local').username : null;
      const username = userInfoName
      if(username === this.row.name){
        return this.$Message.error(this.$t('message.workspaceManagemnet.notDeleteMsg'));
      }
      this.delshow = false;
      const params = {
        workspaceId: this.workspaceId,
        username: this.row.name
      };
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}deleteWorkspaceUser`, params).then(() => {
        this.init()
        this.$Message.success(this.$t('message.workspaceManagemnet.deleteSuccess'));
      });
    },
    edit(row) {
      // 如果是当前用户修改自己的权限，提示用户不能修改
      if (this.getUserName() === row.name) return this.$Message.warning(this.$t("message.workspaceManagemnet.notsupported"));
      this.edituser = {
        role: [...row.roles]
      }
      this.row = row;
      this.editusershow = true;
    },
    creater() {
      this.useradd = {
        name: "",
        role: []
      }
      this.creatershow = true;
    },
    Ok(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          if(name === 'addUser')  this.createuser()
          if(name === 'editUser') this.editrole()
        } else {
          this.$Message.warning(this.$t("message.workspaceManagemnet.failedNotice"));
        }
      });
    },
    Cancel() {
      this.creatershow = false;
      this.editusershow = false;
      this.options = [];
    },
    createuser(){
      let name = this.useradd.name
      const params = {
        roles: this.useradd.role,
        workspaceId: this.workspaceId,
        username: name,
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}addWorkspaceUser`, params).then(() => {
        this.init()
        this.$Message.success(this.$t('message.workspaceManagemnet.addSuccess'));
      }).catch(() => {
        this.$Message.warning(this.$t('message.workspaceManagemnet.addFail'));
      });
      this.creatershow = false;
    },
    editrole(){
      let name = this.row.name
      const params = {
        roles: this.edituser.role,
        workspaceId: this.workspaceId,
        username: name,
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}updateWorkspaceUser`, params).then(() => {
        this.init()
        // 如果用户自己改了自己的权限，需要去更新左侧菜单栏
        if (name === this.getUserName()) {
          this.dispatch('WorkspaceHome:updateSidebar');
        }
        this.$Message.success(this.$t('message.workspaceManagemnet.updataSuccess'));
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
      arr.unshift({label: this.$t('message.workspaceManagemnet.all'), value: 0})
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
      arr.unshift({label: this.$t('message.workspaceManagemnet.all'), value: 0})
      return arr
    }
  }
};
</script>
<style lang="scss" scoped>
.user-serchbar-box {
  padding: 10px 15px;
  min-width: 1217px;
  .user-table-page {
    text-align: center;
    padding-top: 10px;
  }
}
</style>
