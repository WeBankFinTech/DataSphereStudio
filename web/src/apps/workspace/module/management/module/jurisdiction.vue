<template>
  <div class="juri-serchbar-box">
    <h3 style="width:80%;display: inline-block;" >{{$t('message.workspaceManagemnet.permissionsManagement')}}</h3>
    <Button
      type="success"
      @click="creater"
    >{{$t('message.workspaceManagemnet.create')}}</Button>
    <!-- <formserch
      @click-serach="search"
      @click-creater="creater"
      :searchBar="searchBar"
      :optionType="optionType"
    ></formserch> -->
    <juristable v-if="tablearr && workspaceMenu" :tablearr="tablearr" :workspaceMenu="workspaceMenu" ref="menutable"></juristable>
  </div>
</template>
<script>
import api from "@/common/service/api";
// import formserch from "../component/formsechbar";
import juristable from '../component/juristable'
export default {
  components: {
    // formserch,
    juristable
  },
  data() {
    return {
      workspaceMenu: null,
      workspaceId: '',
      tablearr: [],
      slotlist: [],
      menudata: {
        columns: [],
        datalist: [],
        title: this.$t('message.workspaceManagemnet.menuPermissions'),
      },
      assembly: {
        columns: [],
        datalist: [],
        title: this.$t('message.workspaceManagemnet.componentAccessPermissions'),
      },
      searchBar: {
        title: this.$t('message.workspaceManagemnet.permissionsName'),
        searchText: "",
        engine: 0,
        status: 0
      },
      optionType: {
        title: this.$t('message.workspaceManagemnet.permissionsName') + ': ',
        status: [
        // {
        //   label: "全部",
        //   value: 0
        // },
        // {
        //   label: "管理员",
        //   value: 1
        // },
        // {
        //   label: "领导",
        //   value: 2
        // }
        ],
      },
    };
  },
  created(){
    this.workspaceId = parseInt(this.$route.query.workspaceId)
  },

  mounted() {
    this.init();
  },
  methods: {
    init() {
      api.fetch(`${this.$API_PATH.WORKSPACE_PATH}getWorkspaceMenuPrivs`, {
        workspaceId: this.workspaceId,
      }, 'get').then((rst) => {
        this.workspaceMenu = rst.workspaceMenuPrivs
        this.menudata.columns = this.assembly.columns = this.tabletittle(rst.workspaceMenuPrivs.roleVOS)
        this.menudata.datalist = this.tablelist(rst.workspaceMenuPrivs.menuPrivVOS,'menuPrivs')
        this.assembly.datalist = this.tablelist(rst.workspaceMenuPrivs.componentPrivVOS,'componentPrivs')
        this.tablearr=[this.menudata,this.assembly]
      }).catch(() => {
      });
    },
    tablelist(data,tablename){
      let values = []
      data.forEach((item)=>{
        let signArr = Object.keys(item[tablename])
        item[tablename].namesign = signArr
        item[tablename].name = item.name
        item[tablename].id = item.id
        item[tablename].type = tablename
        values.push(item[tablename])
      })
      return values
    },
    tabletittle(roles){
      let obj={}
      let arr=[]
      roles.forEach((item)=>{
        obj = {
          title: item.roleFrontName,
          key: item.roleName,
          align: 'center',
          render(h,params) {
            return (h('Icon', {
              style: {
                color: params.row[item.roleName] ? "#4ACA6D" : "red"
              },
              props: {
                type: params.row[item.roleName] ? 'md-checkmark' : 'md-close',
              },
            }, ''))
          },
        }
        arr.push(obj)
      })
      arr.unshift({ title: this.$t('message.workspaceManagemnet.permissionsName'), key: "name", align: "center"})
      arr.push({title: this.$t('message.workspaceManagemnet.action'), slot: "action", align: "center"})
      return arr
    },
    search() {
    },
    creater() {
      this.$refs.menutable.createrchange()
    },
  }
};
</script>
<style lang="scss" scoped>
.juri-serchbar-box {
  padding:10px 15px;
}
</style>
