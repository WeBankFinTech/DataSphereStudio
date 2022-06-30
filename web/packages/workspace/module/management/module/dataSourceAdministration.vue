<template>
  <div class="dataSourceAddition-box">
    <h4>{{$t('message.workspaceManagement.dataSourceAdministration')}}</h4>
    <!-- 条件查询 -->
    <Form class="dataSource-serchbar" ref="searchBar" :model="searchBar" :label-width="120" inline>
      <FormItem :label="$t('message.workspaceManagement.dataSourceType')">
        <Select v-model="searchBar.type" style="width:300px" filterable :clearable="true" @on-change="change">
          <Option v-for="item in typeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
        </Select>
      </FormItem>
      <FormItem :label="$t('message.workspaceManagement.dataSourceName')">
        <Input v-model="searchBar.name" style="width:300px" search @on-search="search" clearable @on-clear="search"></Input>
      </FormItem>
      <FormItem>
        <Button type="primary" @click="add">{{ $t('message.workspaceManagement.addDataSour')}}</Button>
      </FormItem>
    </Form>
    <Table border highlight-row align="center" :columns="data.columns" :data="data.datalist">
      <template slot-scope="{ row }" slot="connectionInfo">
        <div>
          <span>url : {{ row.url }}</span>
        </div>
        <div>
          <span>username : {{ row.username }}</span>
        </div>
      </template>
      <template slot-scope="{ row }" slot="createTime">
        <!-- <span>{{ row.createTime }}</span> -->
        <span>{{ row.createTime | timestampToTime }}</span>
      </template>
      <template slot-scope="{ row }" slot="action">
        <Button
          type="error"
          size="small"
          @click="edit(row)">
          {{$t('message.workspaceManagement.edit')}}
        </Button>
        <Button
          type="primary"
          size="small"
          style="margin-left: 10px"
          @click="remove(row)"
        >{{$t('message.workspaceManagement.delete')}}</Button>
      </template>
    </Table>
    <!-- 新增数据源 -->
    <Modal v-model="addshow" :title="$t('message.workspaceManagement.addDataSour')" :closable="false">
      <Form :model="addDataSource" :label-width="130" ref="addDataSource" :rules="addrule">
        <FormItem :label="$t('message.workspaceManagement.dataSourceType')" prop="type">
          <Select v-model="addDataSource.type" style="width:300px" filterable>
            <Option v-for="item in typeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.dataSourceName')" prop="name">
          <Input v-model="addDataSource.name" :placeholder="$t('message.workspaceManagement.dataSourceNamePlaceHolder')"></Input>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.dataSourceDesc')" prop="note">
          <Input v-model="addDataSource.note" :placeholder="$t('message.workspaceManagement.dataSourceDescPlaceHolder')"></Input>
        </FormItem>
        <FormItem label="JDBC URL" prop="url">
          <Input v-model="addDataSource.url" :placeholder="$t('message.workspaceManagement.JdbcUrlFormat')"></Input>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.userName')" prop="username">
          <Input v-model="addDataSource.username" :placeholder="$t('message.workspaceManagement.userNamePlaceHolder')"></Input>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.password')" prop="pwd">
          <Input v-model="addDataSource.pwd" :placeholder="$t('message.workspaceManagement.passwordPlaceHolder')"></Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <div style="display: inline-block; width: 64%; text-align: left">
          <Button type="info" size="large" @click="test('add', 'addDataSource')">{{
            $t("message.workspaceManagement.test")
          }}</Button>
        </div>
        <Button type="text" size="large" @click="cancel('add', 'addDataSource')">{{
          $t("message.workspaceManagement.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="ok('add', 'addDataSource')">{{
          $t("message.workspaceManagement.ok")
        }}</Button>
      </div>
    </Modal>
    <!-- 编辑数据源 -->
    <Modal v-model="editshow" :title="$t('message.workspaceManagement.editDataSour')">
      <Form :model="editDataSource" :label-width="130" ref="editDataSource" :rules="editrule">
        <FormItem :label="$t('message.workspaceManagement.dataSourceType')" prop="type">
          <Select v-model="editDataSource.type" style="width:300px" disabled filterable>
            <Option v-for="item in typeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.dataSourceName')" prop="name">
          <Input v-model="editDataSource.name" disabled :placeholder="$t('message.workspaceManagement.dataSourceNamePlaceHolder')"></Input>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.dataSourceDesc')" prop="note">
          <Input v-model="editDataSource.note" :placeholder="$t('message.workspaceManagement.dataSourceDescPlaceHolder')"></Input>
        </FormItem>
        <FormItem label="JDBC URL" prop="url">
          <Input v-model="editDataSource.url" :placeholder="$t('message.workspaceManagement.JdbcUrlFormat')"></Input>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.userName')" prop="username">
          <Input v-model="editDataSource.username" :placeholder="$t('message.workspaceManagement.userNamePlaceHolder')"></Input>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.password')" prop="pwd">
          <Input v-model="editDataSource.pwd" :placeholder="$t('message.workspaceManagement.passwordPlaceHolder')"></Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <div style="display: inline-block; width: 64%; text-align: left">
          <Button type="info" size="large" @click="test('edit', 'editDataSource')">{{
            $t("message.workspaceManagement.test")
          }}</Button>
        </div>
        <Button type="text" size="large" @click="cancel('edit', 'editDataSource')">{{
          $t("message.workspaceManagement.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="ok('edit', 'editDataSource')">{{
          $t("message.workspaceManagement.ok")
        }}</Button>
      </div>
    </Modal>
    <!-- 删除数据源 -->
    <Modal v-model="delshow" width="360" class-name="delete-modal" :title="$t('message.workspaceManagement.delDataSour')">
      <div style="text-align: center">
        <div>
          <Icon type="ios-information-circle" />
          <span>{{ $t("message.workspaceManagement.warningMsg.isSure", {name: delDataSource.name}) }}</span>
        </div>
        <div>({{ $t("message.workspaceManagement.warningMsg.info") }})</div>
      </div>
      <div slot="footer">
        <Button type="error" size="large" long @click="del(delDataSource.datasourceId)">{{$t('message.workspaceManagement.comfirmDelete')}}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';

export default {
  name: "dataSourceAddition",
  data() {
    return {
      data: {
        columns: [],
        datalist: []
      },
      searchBar: {
        type: "MYSQL",
        name: ""
      },
      typeList: [
        {
          value: 'MYSQL',
          label: 'MYSQL'
        }
      ],
      editDataSource: {},
      delDataSource: {},
      addDataSource: {
        type: "MYSQL",
        name: "",
        note: "",
        url: "",
        username: "",
        pwd: ""
      },
      addrule: {
        type: [
          {required: true, message: this.$t('message.workspaceManagement.selectDataSourceMsg'), trigger: "change"}
        ],
        name: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.dataSourceName")}), trigger: "blur"},
          {max: 50, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.dataSourceName"), len: 50}), trigger: "blur"},
          {pattern: /^[a-zA-Z]{1}\w*$/, message: this.$t('message.workspaceManagement.dataSourceNameValidateMsg'), trigger: "blur"}
        ],
        note: [
          {required: false,  message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.dataSourceDesc")}), trigger: "blur"},
          {max: 200, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.dataSourceDesc"), len: 200}), trigger: "blur"}
        ],
        url: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: "URL"}), trigger: "blur"}
        ],
        username: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.userName")}), trigger: "blur"},
          {max: 200, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.userName"), len: 200}), trigger: "blur"}
        ],
        pwd: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.password")}), trigger: "blur"},
          {max: 200, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.password"), len: 200}), trigger: "blur"}
        ],
      },
      editrule: {
        type: [
          {required: true, message: this.$t('message.workspaceManagement.selectDataSourceMsg'), trigger: "change"}
        ],
        name: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.dataSourceName")}), trigger: "blur"},
          {max: 50, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.dataSourceName"), len: 50}), trigger: "blur"},
          {pattern: /^[a-zA-Z]{1}\w*$/, message: this.$t('message.workspaceManagement.dataSourceNameValidateMsg'), trigger: "blur"}
        ],
        note: [
          {required: false,  message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.dataSourceDesc")}), trigger: "blur"},
          {max: 200, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.dataSourceDesc"), len: 200}), trigger: "blur"}
        ],
        url: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: "URL"}), trigger: "blur"}
        ],
        username: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.userName")}), trigger: "blur"},
          {max: 200, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.userName"), len: 200}), trigger: "blur"}
        ],
        pwd: [
          {required: true, message: this.$t('message.workspaceManagement.NotNullMsg', {name: this.$t("message.workspaceManagement.password")}), trigger: "blur"},
          {max: 200, message: this.$t('message.workspaceManagement.lengthValidateMsg', {name: this.$t("message.workspaceManagement.password"), len: 200}), trigger: "blur"}
        ],
      },
      editshow: false,
      delshow: false,
      addshow: false
    }
  },
  created() {
    this.workspaceId =parseInt(this.$route.query.workspaceId);
    this.init()
    this.data.columns = this.getColumns()
  },
  filters: {
    // 时间戳转日期格式
    timestampToTime(timestamp){
      let date = new Date(timestamp),
        Y = date.getFullYear() + '-',
        M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-',
        D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ',
        h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':',
        m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':',
        s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
      return Y + M + D + h + m + s
    }
  },
  methods: {
    init(){
      api.fetch(`${this.$API_PATH.DATASOURCE_PATH}list`,{
        workspaceId: this.workspaceId,
        type: this.searchBar.type,
        name: "%" + this.searchBar.name.trim() + "%"
      },'get').then((ret)=>{
        this.data.datalist = ret.allDs
      })
    },
    getColumns(){
      const columns = [
        {title: this.$t('message.workspaceManagement.dataSourceName'), key: "name", align: "center"},
        {title: this.$t('message.workspaceManagement.dataSourceType'), key: "type", align: "center"},
        {title: this.$t('message.workspaceManagement.dataSourceDesc'), key: "note", align: "center"},
        {title: this.$t('message.workspaceManagement.connectionInfo'), slot: "connectionInfo", align: "center", width: 280},
        {title: this.$t('message.workspaceManagement.createTime'), slot: "createTime", align: "center"},
        {title: this.$t('message.workspaceManagement.operation'), slot: "action", align: "center"},
      ]
      return columns;
    },
    change(){
      this.init()
    },
    search(){
      this.init()
    },
    remove(row){
      this.delshow = true
      this.delDataSource = {...row}
    },
    edit(row){
      this.editDataSource = {...row}
      this.editshow = true
    },
    add(){
      this.addshow = true
    },
    del(id){
      this.delshow = false
      api.fetch(`${this.$API_PATH.DATASOURCE_PATH}delete/${id}`, {id}).then(()=>{
        this.init()
        this.$Message.success(this.$t('message.workspaceManagement.deleteSuccess'))
      })
    },
    ok(action, ref){
      this.$refs[ref].validate((valid) => {
        if (valid) {
          if(action === "edit"){
            let params = {
              datasourceId: this.editDataSource.datasourceId,
              note: this.editDataSource.note,
              url: this.editDataSource.url,
              username: this.editDataSource.username,
              pwd: this.editDataSource.pwd
            }
            api.fetch(`${this.$API_PATH.DATASOURCE_PATH}edit`, params).then(()=>{
              this.init()
              this.$Message.success(this.$t('message.workspaceManagement.editSuccess'))
            })
            this.editshow = false
          }else if(action === "add"){
            let params = {
              workspaceId: this.workspaceId,
              name: this.addDataSource.name,
              username: this.addDataSource.username,
              type: this.addDataSource.type,
              note: this.addDataSource.note,
              url: this.addDataSource.url,
              pwd: this.addDataSource.pwd
            }
            api.fetch(`${this.$API_PATH.DATASOURCE_PATH}add`, params).then(()=>{
              this.init()
              this.$Message.success(this.$t('message.workspaceManagement.addSuccess'))
            })
            this.$refs[ref].resetFields()
            this.addDataSource.type = "MYSQL"
            this.addshow = false
          }
        } else {
          this.$Message.warning(this.$t("message.workspaceManagement.failedNotice"))
        }
      })
    },
    cancel(action, ref){
      this.$refs[ref].resetFields()
      if(action === 'add'){
        this.addshow = false
      }else if(action === "edit"){
        this.editshow = false
      }
    },
    test(action, ref){
      this.$refs[ref].validate((valid) => {
        if (valid) {
          let params = {}
          if(action === "add"){
            params = {
              workspaceId: this.workspaceId,
              name: this.addDataSource.name,
              username: this.addDataSource.username,
              type: this.addDataSource.type,
              note: this.addDataSource.note,
              url: this.addDataSource.url,
              pwd: this.addDataSource.pwd
            }
          }else{
            params = {
              workspaceId: this.workspaceId,
              name: this.editDataSource.name,
              username: this.editDataSource.username,
              type: this.editDataSource.type,
              note: this.editDataSource.note,
              url: this.editDataSource.url,
              pwd: this.editDataSource.pwd
            }
          }
          api.fetch(`${this.$API_PATH.DATASOURCE_PATH}test`, params).then(()=>{
            this.$Message.success(this.$t('message.workspaceManagement.testSuccess'))
          })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
  .dataSourceAddition-box {
    padding: 10px 15px;
    min-width: 1217px;
  }
  .dataSource-serchbar{
    margin: 30px 0 5px 0;
  }
  .delete-modal{
    .ivu-modal-body{
      text-align: center;
    }
  }
</style>
