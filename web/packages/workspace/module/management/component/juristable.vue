<template>
  <div class="juristable">
    <div v-for="item in tablearr" :key="item.title">
      <h4 class="menu-permission-title" style="margin:20px 0px 10px;">{{item.title}}</h4>
      <div style="overflow: auto;">
        <history-table
          :columns="item.columns"
          :data="item.datalist"
          :height="40*(item.datalist.length+1) + 10"
          :no-data-text="$t('message.scripts.emptyText')"
          size="small"
          border
          stripe>
          <template style="color:#4ACA6D" slot-scope="scope" slot="action">
            <Button v-if="isAdmin()" type="warning" size="small" @click="modify(scope.data.row,scope.data.index)">{{$t('message.workspaceManagement.editor')}}</Button>
          </template>
        </history-table>
      </div>
    </div>
    <div  v-if="homepagedata && homepagedata.datalist.length" class="hoempage-table">
      <h4 style="margin:10px 0px;" class="menu-permission-title">{{$t('message.workspaceManagement.homeSetting')}}</h4>
      <history-table
        :columns="homepagedata.column"
        :data="homepagedata.datalist"
        :height="40*(homepagedata.datalist.length + 1) + 10"
        :no-data-text="$t('message.scripts.emptyText')"
        size="small"
        border
        stripe>
        <template style="color:#4ACA6D;" slot="action">
          <Button type="warning" size="small" disabled>{{$t('message.workspaceManagement.editor')}}</Button>
        </template>
      </history-table>
    </div>
    <Modal
      v-model="creatershow"
      :title="$t('message.workspaceManagement.addRole')">
      <Form ref="addUser" :model="useradd" :rules="addrule" :label-width="80">
        <FormItem :label="$t('message.workspaceManagement.roleName') + ': '" prop="roleName">
          <Input size="small" v-model="useradd.roleName" style="width: 200px"/>
        </FormItem>
        <FormItem :label="$t('message.workspaceManagement.menuPermissions')" :label-width="85">
          <Checkbox-group v-model="useradd.menuPrivs">
            <Checkbox v-for="item in workspaceMenu.menuPrivVOS" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
          </Checkbox-group>
        </FormItem>
        <FormItem v-if="workspaceMenu.componentPrivVOS.length" :label="$t('message.workspaceManagement.componentAccessPermissions')" :label-width="85">
          <Checkbox-group v-model="useradd.componentPrivs">
            <Checkbox  v-for="item in workspaceMenu.componentPrivVOS" :key="item.id" :label="item.id">{{item.name}}</Checkbox>
          </Checkbox-group>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="cancel">{{
          $t("message.workspaceManagement.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="roleadd">{{
          $t("message.workspaceManagement.ok")
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
import api from '@dataspherestudio/shared/common/service/api';
import storage from '@dataspherestudio/shared/common/helper/storage';
import table from '@dataspherestudio/shared/components/virtualTable';
export default {
  components: {
    historyTable: table.historyTable,
  },
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
          { required: true, message: this.$t('message.workspaceManagement.addRoleMsg'), trigger: "blur" },
          { message: this.$t('message.workspaceManagement.nameLongMsg'), max: 8 },
          { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.workspaceManagement.ruleMsg'), trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    titlename(){
      let title = `${this.$t('message.workspaceManagement.permissionsEditor')}·${this.userlist.name}`
      return title
    }
  },
  created(){
    this.workspaceId =parseInt(this.$route.query.workspaceId)
  },
  mounted() {
    this.gethomepagedata()
  },
  methods: {
    isAdmin() {
      const isAdmin = this.getIsAdmin()
      const workspaceRoles = storage.get(`workspaceRoles`) || [];
      if (isAdmin || workspaceRoles.indexOf('admin') > -1) {
        return true;
      } else {
        return false;
      }
    },
    gethomepagedata() {
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
      columns.push({title: this.$t('message.workspaceManagement.action'), slot: "action", align: "center"})
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
        this.$Message.success(this.$t('message.workspaceManagement.editorSuccess'));
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
      if(bool){return this.$Message.warning(this.$t('message.workspaceManagement.nameRepeat'))}
      this.$refs.addUser.validate(valid=>{
        if(valid){
          const params = {
            menuIds: this.useradd.menuPrivs,
            componentIds: this.useradd.componentPrivs,
            workspaceId: this.workspaceId,
            roleName: this.useradd.roleName
          }
          api.fetch(`${this.$API_PATH.WORKSPACE_PATH}addWorkspaceRole`,params).then(()=>{
            this.$Message.success(this.$t('message.workspaceManagement.addSuccess'));
            this.$parent.init()
            this.useradd={};
            this.creatershow = false;
          })
        }else{
          this.$Message.warning(this.$t("message.workspaceManagement.failedNotice"));
        }
      })
    },

  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .menu-permission-title {
    @include font-color($workspace-title-color, $dark-workspace-title-color);
  }
  .modify-model{
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    label{
      width: 100px
    }
  }
   div.juristable {
    ::v-deep .we-table tbody td.we-table-row-cell {
      height: 38px;
    }
  }
</style>
