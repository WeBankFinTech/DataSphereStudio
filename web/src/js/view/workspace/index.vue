<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{$t('message.workSpace.home.welcome', {text: workspaceData.name})}}</h1>
        <p>{{workspaceData.description}}</p>
      </div>
    </div>

   

    <div class="workspace-main">
      
      <Card class="left">
        <h3 class="item-header">
          <span >{{this.$t('message.console.sideNavList.function.name')}}</span>
        </h3>
        <div class="app-list">
          <div class="nodata-tips" v-if="favoriteApps.length===0">{{$t('message.workSpace.home.tips')}}</div>
          <div v-else v-for="(item, index) in favoriteApps" :key="item.title" class="app-item-wrap" :class="{shadow:setting}" @click="setting?null:linkTo(item, item.url)">
            <div v-if="setting" class="close-wrap" @click.stop="deleteFavoriteApp(item.id, index)"><i class="fi-cross1"></i></div>
            <i class="app-icon" :class="iconSplit(item.icon)[0]" :style="`color: ${iconSplit(item.icon)[1]}`"></i>
            <span class="label">{{$t('message.workSpace.home.enter', {text: item.title})}}</span>
          </div>
          

          <div v-if="setting" class="app-item-add" @click="show = true">
            <i class="fi-plus add"></i>
            <!-- <Button type="text" icon="scriptis" style="font-size: 30px;"></Button> -->
          </div>
        </div>

        <div class="setting-bt-wrap" v-if="!setting">
          <Button type="text" size="large" icon="md-settings" @click="setting=!setting">{{this.$t('message.workSpace.home.setting')}}</Button>
        </div>
        <div class="setting-bt-wrap" v-else>
          <Button type="text" size="large" icon="md-settings" @click="setting=!setting">{{this.$t('message.workSpace.home.exit')}}</Button>
        </div>
      </Card>

      

      <Card class="right">
        <div class="nodata-tips" v-if="!adminApps.title">{{$t('message.workSpace.home.tips')}}</div>
        <h3 v-if="adminApps.title" class="item-header">
          <span>{{adminApps.title}}</span>
        </h3>
        <div v-if="adminApps.title" class="app-list">
          <div v-for="item in adminApps.appInstances" :key="item.title" class="app-item-wrap" @click="navTo(item, item.accessButtonUrl)">
            <i class="app-icon" :class="iconSplit(item.icon)[0]" :style="`color: ${iconSplit(item.icon)[1]}`"></i>
            <span class="label">{{item.accessButton}}</span>
          </div>
        </div>

       
      </Card>     
    </div>

    <div class="app-list-main">
      <div class="app-list-tabs" :class="{hideBar: search}">
        <div class="nodata-tips" v-if="tabsApplication.length===0">{{$t('message.workSpace.home.tips')}}</div>
        <Tabs v-else active-key=0>
          <Tab-pane v-for="(type, index) in tabsApplication" :label="type.title" :key="index">
            <div class="pane-wrap">
              <Card v-for="item in tabsApplication[index].appInstances" :key="item.name" class="pane-item">
                <div class="app-entrance">
                  <div class="app-title-wrap">

                    <div class="app-title">
                      <i class="app-icon title-sub" :class="iconSplit(item.icon)[0]" :style="`color: ${iconSplit(item.icon)[1]}`"></i>
                      <span class="label title-sub">{{item.title}}</span>
                      <Tag class="app-tag title-sub" v-for="tag in (item.labels ? item.labels.split(','):[])" :key="tag">{{tag}}</Tag>
                    </div>
                    

                    <p>{{item.description}}</p>
                  </div>

                  <div v-if="item.active" class="app-status-wrap-active">
                    <i class="status-icon fi-radio-on2"></i>
                    <span>{{$t('message.workSpace.home.running')}}</span>
                  </div>
                  <div v-else class="app-status-wrap-disable">
                    <i class="status-icon fi-radio-on2"></i>
                    <span>{{$t('message.workSpace.home.stop')}}</span>
                  </div>

                </div>

                <div v-if="item.active" class="button-wrap">
                  <Button class="entrace-btn" size="small" type="primary" @click="linkTo(item, item.accessButtonUrl)">{{item.accessButton}}</Button>
                  <Button class="entrace-btn" size="small" @click="navTo(item, item.manualButtonUrl)">{{item.manualButton}}</Button>
                </div>
                <div v-else class="button-wrap">
                  <Button class="entrace-btn" size="small" type="primary" disabled @click="navTo(item, item.accessButtonUrl)">{{item.accessButton}}</Button>
                  <Button class="entrace-btn" size="small" @click="navTo(item, item.manualButtonUrl)">{{item.manualButton}}</Button>
                </div>
              </Card>    

            </div>
            
          </Tab-pane>
        </Tabs>
        
        <div class="input-wrap">
          <Input icon="ios-search" :placeholder="$t('message.workSpace.home.searchPlaceholder')" style="width: 200px" @on-change="onSearch" />
        </div>
        
      </div>
    </div>
    <Modal
      v-model="show"
      :title="$t('message.workSpace.home.dlgTitle')">
      <Form
        ref="dynamicForm"
        :model="formDynamic"
        :label-width="100"
        :rules="rules"
      >
        <FormItem :label="$t('message.workSpace.home.selectType')"
          prop="selectType">
          <Select v-model="formDynamic.selectType">
            <Option v-for="item in types" :value="`${item.id}`" :key="`${item.id}`">{{ item.title }}</Option>
          </Select>
        </FormItem>

        <FormItem :label="$t('message.workSpace.home.selectApp')"
          prop="selectApp"> 
          <Select v-model="formDynamic.selectApp">
            <Option v-for="item in apps" :value="`${item.id}`" :key="`${item.id}`" :disabled="item.had">{{ item.title }}</Option>
          </Select>
        </FormItem>
      </Form>

      <div slot="footer">
        <Button
          @click="show = false"
        >{{$t('message.workSpace.home.cancel')}}</Button>
        <Button
          :loading="addAppLoading"
          type="primary"
          @click="addFavoriteApp"
        >{{$t('message.workSpace.home.save')}}</Button>
      </div>
          
    </Modal>  
  </div>
</template>

<script>
import api from '@/js/service/api';
export default {
  created(){
    this.workspaceId = this.$route.query.workspaceId;
    this.init();
  },

  
  watch: {
    $route() {
      this.workspaceId= this.$route.query.workspaceId; //获取传来的参数 
      this.init();
    }
  },
  data: function() {
    return {
      workspaceId: null,
      workspaceData: {
        name: "",
        description: ""
      },
      favoriteApps: [],
      adminApps: {},
      setting: false,
      show: false,
      applications: [],
      searchResult: [],
      
      formDynamic: {
        selectType: "",
        selectApp: "",
      },
      addAppLoading: false,
      rules: {
        selectApp: [
          { required: true, message: this.$t('message.workSpace.home.selectApp'), trigger: 'change' },
        ],
        selectType: [
          { required: true, message: this.$t('message.workSpace.home.selectType'), trigger: 'change' },
        ],
      },
      search: false,
    };
  },

  methods: {
    iconSplit(icon){
      if(icon){
        return icon.split('|')
      }
      return ['','']
    },
    init(){
    
      api.fetch(`/dss/workspaces/${this.workspaceId}`, 'get').then(data=>{
        this.workspaceData = data.workspace;
      })

      api.fetch(`/dss/workspaces/${this.workspaceId}/favorites`, 'get').then(data=>{
        this.favoriteApps = data.favorites;
      })

      api.fetch(`/dss/workspaces/${this.workspaceId}/managements`, 'get').then(data=>{
        this.adminApps = data.managements[0] ? data.managements[0]: {};
      })

      api.fetch(`/dss/workspaces/${this.workspaceId}/applications`, 'get').then(data=>{
        this.applications = data.applications || [];
      })
    },
    deleteFavoriteApp(favoriteId, index){
      api.fetch(`/dss/workspaces/${this.workspaceId}/favorites/${favoriteId}`, 'delete').then(data=>{
        this.favoriteApps.splice(index, 1);
      })
    },
    addFavoriteApp(){
      
      this.$refs.dynamicForm.validate((valid) => {
        if (valid) {
          // this.addAppLoading = true;
          const menuApplicationId = parseInt(this.formDynamic.selectApp, 10);
          const favoriteApp = this.findFavoriteAppsByMenuId(menuApplicationId);
          if(favoriteApp) {
            return this.$Message.error(`${this.$t('message.workSpace.home.repeat')}`);
          }
          this.show = false;
          api.fetch(`/dss/workspaces/${this.workspaceId}/favorites`, {menuApplicationId: menuApplicationId},'post').then(data=>{
            const app = this.findAppByApplicationId(this.formDynamic.selectApp)
            this.favoriteApps.push({
              ...app,
              "id": data.favoriteId,
              "menuApplicationId": app.id, //application表里的id
            });

          })
        }
      });
    },

    findAppByApplicationId(id){
      const arr = this.flatApps();
      const result = arr.find(item=>item.id==id);
      return result;
    },

    findFavoriteAppsByMenuId(menuId){
      const arr = this.favoriteApps;
      const result = arr.find(item=>item.menuApplicationId==menuId);
      return result;
    },

    flatApps(){
      const arr = []; 
      this.applications.forEach(item=>{
        arr.push(...item.appInstances)
      })
      return arr;
    },
    
    onSearch(event){
      const value = event.target.value;
      if(value){
        const arr = this.flatApps();
        this.searchResult = arr.filter(item=>{
          if(item.title.indexOf(value)!==-1 || item.labels.indexOf(value)!==-1){
            return true;
          }
          return false;
        })
        this.searchResult = [{title: 'result', appInstances: [...this.searchResult]}];
        this.search = true;
      }else{
        this.search = false;
      }
    },
    linkTo(item, path){
      this.gotoCommonIframe(item.name, {workspaceId: this.workspaceId})
    },
    navTo(item, path){
      if(path){
        if(path.startsWith('http')){
          window.open(path);
        }else {
          this.$router.push({path: path, query: Object.assign({}, this.$route.query)});
        }
      }else {
        console.warn('path error', path);
      }
    }
  },  
  
  computed: {
    types: function(){
      return this.applications.map(item=>({title: item.title, id: item.id}))
    },
    apps: function(){
      if(this.formDynamic.selectType){
        const result = this.applications.find(item=>item.id==this.formDynamic.selectType);
        result.appInstances.map(item=>{
          item.had = false;
          if(this.favoriteApps.find(fItem=>item.id===fItem.menuApplicationId)){
            item.had = true;
          }
        })
        return result.appInstances;
      }
      return [];
    },
    tabsApplication: function(){
      if(this.search){
        return this.searchResult;
      }
      return this.applications;
    }
  }
}
</script>
<style lang="scss" scoped src="../../../assets/styles/workspace.scss"></style>

<style>
   .hideBar > .ivu-tabs > .ivu-tabs-bar {
      visibility: hidden;
      
      padding-top: 35px;
    }

    .ivu-tabs > .ivu-tabs-bar > .ivu-tabs-nav-container{
      display: block;
    }
    .hideBar > .ivu-tabs > .ivu-tabs-bar > .ivu-tabs-nav-container {
      display: none;
    }

  .app-tag > .ivu-tag-text {
    color: #fff;
    border-radius: 2px;
    border-radius: 2px;
  }
</style>