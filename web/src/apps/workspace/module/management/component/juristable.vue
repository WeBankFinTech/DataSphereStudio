<template>
  <div>
    <div v-for="item in tablearr" :key="item.title">
      <h3 style="margin:20px 0px 10px;">{{item.title}}</h3>
      <div style="overflow: auto;">
        <Table :style="width(item.columns)" border highlight-row  :columns="item.columns" :data="item.datalist">
          <template style="color:#4ACA6D" slot-scope="{ row, index }" slot="action">
            <Button type="warning" size="small" @click="modify(row,index)">{{$t('message.workspaceManagemnet.editor')}}</Button>
          </template>
        </Table>
      </div>
    </div>
    <div  v-if="homepagedata && homepagedata.datalist.length" class="hoempage-table">
      <h3 style="margin:10px 0px;">{{$t('message.workspaceManagemnet.homeSetting')}}</h3>
      <Table border highlight-row :columns="homepagedata.column" :data="homepagedata.datalist">
        <template style="color:#4ACA6D"  slot="action">
          <Button type="warning" size="small" disabled>{{$t('message.workspaceManagemnet.editor')}}</Button>
        </template>
      </Table>
    </div>
    <Modal
      v-model="creatershow"
      :title="$t('message.workspaceManagemnet.addRole')">
      <Form ref="addUser" :model="useradd" :rules="addrule" :label-width="80">
        <FormItem :label="$t('message.workspaceManagemnet.roleName') + ': '" prop="roleName">
          <Input size="small" v-model="useradd.roleName" style="width: 200px"/>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagemnet.menuPermissions')" :label-width="85">
          <Checkbox-group v-model="useradd.menuPrivs">
            <Checkbox v-for="item in workspaceMenu.menuPrivVOS" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
          </Checkbox-group>
        </FormItem>
        <FormItem v-if="workspaceMenu.componentPrivVOS.length" :label="$t('message.workspaceManagemnet.componentAccessPermissions')" :label-width="85">
          <Checkbox-group v-model="useradd.componentPrivs">
            <Checkbox  v-for="item in workspaceMenu.componentPrivVOS" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
          </Checkbox-group>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="cancel">{{
          $t("message.workspaceManagemnet.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="roleadd">{{
          $t("message.workspaceManagemnet.ok")
        }}</Button>
      </div>
    </Modal>
    <Modal
      v-model="modifyshow"
      :title="titlename"
      @on-ok="jurisdictionchange"
      @on-cancel="cancel">
      <div class="modify-model">
        <Checkbox v-for="item in userlist.namesign"
          :key="item" style="display:block"
          v-model="userlist[item]"
          :disabled="item==='admin'">
          {{username(item)}}
        </Checkbox>
      </div>
    </Modal>
  </div>
</template>
<script>
import api from "@/common/service/api";
export default {
  props: {
    workspaceMenu: Object,
    tablearr: Array
  },
  data() {
    return {
      modifyshow: false,
      creatershow: false,
      userlist: [],
      homepagedata: null,
      workspaceId: '',
      useradd: {
        roleName: '',
        menuPrivs: [],
        componentPrivs: []
      },
      originBusiness: {},
      addrule: {
        roleName: [
          { required: true, message: this.$t('message.workspaceManagemnet.addRoleMsg'), trigger: "blur" },
          { message: this.$t('message.workspaceManagemnet.nameLongMsg'), max: 8 },
          { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.workspaceManagemnet.ruleMsg'), trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    titlename(){
      let title = `${this.$t('message.workspaceManagemnet.permissionsEditor')}·${this.userlist.name}`
      return title
    },
  },
  created(){
    this.workspaceId =parseInt(this.$route.query.workspaceId)
  },
  mounted() {
    this.gethomepagedata()
  },
  methods: {
    gethomepagedata(){
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getWorkspaceHomepageSettings`, {
        workspaceId: this.workspaceId,
      }, 'get').then((rst) => {
        let data = rst.homepageSettings.roleHomepages
        this.homepagedata = this.gethomepagetable(data)
      })
    },
    gethomepagetable(data){
      let columns = [];
      let datas = [];
      let columobj = {};
      let dataobj = {};
      data.forEach(item=>{
        dataobj[item.roleName] = item.homepageName
        columobj = {
          title: item.roleFrontName,
          key: item.roleName,
          align: 'center'
        }
        columns.push(columobj)
      })
      datas.push(dataobj)
      columns.push({title: this.$t('message.workspaceManagemnet.action'), slot: "action", align: "center"})
      let obj ={}
      this.$set(obj, 'column', columns);
      this.$set(obj, 'datalist', datas);
      return obj;
    },
    modify(row){
      this.modifyshow = true;
      this.userlist = JSON.parse(JSON.stringify(row))
    },
    username(name){
      let strname = ''
      this.workspaceMenu.roleVOS.forEach(item=>{
        if(item.roleName === name){
          strname = item.roleFrontName
        }
      })
      return strname
    },
    createrchange(){
      this.creatershow = true;
    },
    width(v){
      return `min-width:${v.length*100}px`
    },
    jurisdictionchange() {
      let type = this.userlist.type;
      let url = type.substring(0, type.length - 1);
      url = url.charAt(0).toUpperCase() + url.slice(1)
      let id =type.replace("Privs","Id")
      let menuPrivs={}
      let menuId= this.userlist.id
      this.userlist.namesign.forEach((item)=>{
        menuPrivs[item]=this.userlist[item]
      })
      const params ={
        [id]: menuId,
        [type]: menuPrivs,
        workspaceId: this.workspaceId
      }
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}updateRole${url}`, params).then(() => {
        this.$Message.success(this.$t('message.workspaceManagemnet.editorSuccess'));
        this.$parent.init()
      }).catch(() => {
      });

    },
    cancel () {
      this.creatershow = false;
      this.useradd={};
    },
    roleadd(){
      let arr = []
      let name = this.useradd.roleName;
      this.workspaceMenu.roleVOS.forEach(item => {
        arr.push(item.roleFrontName)
      });
      let bool = arr.indexOf(name)> -1
      if(bool){return this.$Message.warning(this.$t('message.workspaceManagemnet.nameRepeat'))}
      this.$refs.addUser.validate(valid=>{
        if(valid){
          const params = {
            menuIds: this.useradd.menuPrivs,
            componentIds: this.useradd.componentPrivs,
            workspaceId: this.workspaceId,
            roleName: this.useradd.roleName
          }
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}addWorkspaceRole`,params).then(()=>{
            this.$Message.success(this.$t('message.workspaceManagemnet.addSuccess'));
            this.$parent.init()
            this.useradd={};
            this.creatershow = false;
          })
        }else{
          this.$Message.warning(this.$t("message.workspaceManagemnet.failedNotice"));
        }
      })
    },

  },
}
</script>
<style lang="scss" scoped>
  .modify-model{
    display: flex;
    flex-wrap: wrap;
    height: 64px;
    align-items: center;
    label{
      width: 100px
    }
  }
</style>
