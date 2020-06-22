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
          <div v-for="(item, index) in favoriteApps" :key="item.title" class="app-item-wrap" :class="{shadow:setting}" @click="setting?null:linkTo(item, item.url)">
            <div v-if="setting" class="close-wrap" @click.stop="deleteFavoriteApp(item.favouritesId, index)"><i class="fi-cross1"></i></div>
            <i class="app-icon" :class="item.icon"></i>
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
        <h3 class="item-header">
          <span>{{adminApps.title}}</span>
        </h3>
        <div class="app-list">
          <div v-for="item in adminApps.appInstances" :key="item.title" class="app-item-wrap">
            <i class="app-icon" :class="item.icon" ></i>
            <span class="label">{{item.title}}</span>
          </div>
        </div>
      </Card>     
    </div>

    <div class="app-list-main">
      <div class="app-list-tabs" :class="{hideBar: search}">
        <Tabs>
          <Tab-pane v-for="(type, index) in tabsApplication" :label="type.title" :key="type.title">
            <div class="pane-wrap">
              <Card v-for="item in tabsApplication[index].appInstances" :key="item.name" class="pane-item">
                <div class="app-entrance">
                  <div class="app-title-wrap">

                    <div class="app-title">
                      <i class="app-icon title-sub" :class="item.icon"></i>
                      <span class="label title-sub">{{item.title}}</span>
                      <Tag class="app-tag title-sub" v-for="tag in (item.labels.split(','))" :key="tag">{{tag}}</Tag>
                    </div>
                    

                    <p>{{item.description}}</p>
                  </div>

                  <div v-if="item.isActive" class="app-status-wrap-active">
                    <i class="status-icon fi-radio-on2"></i>
                    <span>{{$t('message.workSpace.home.running')}}</span>
                  </div>
                  <div v-else class="app-status-wrap-disable">
                    <i class="status-icon fi-radio-on2"></i>
                    <span>{{$t('message.workSpace.home.stop')}}</span>
                  </div>

                </div>

                <div v-if="item.isActive" class="button-wrap">
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
            <Option v-for="item in apps" :value="`${item.id}`" :key="`${item.id}`">{{ item.title }}</Option>
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
      favoriteApps: [{icon: 'fi-exchange', url: ""}],
      adminApps: [],
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
    init(){
    
      api.fetch(`/mock/dss/workspaces/${this.workspaceId}`, 'get').then(data=>{
        this.workspaceData = data;
      })

      api.fetch(`/mock/dss/workspaces/${this.workspaceId}/favorites`, 'get').then(data=>{
        this.favoriteApps = data;
      })

      api.fetch(`/mock/dss/workspaces/${this.workspaceId}/managements`, 'get').then(data=>{
        this.adminApps = data.managements ? data.managements[0]: [];
      })

      api.fetch(`/mock/dss/workspaces/${this.workspaceId}/applications`, 'get').then(data=>{
        this.applications = data.applications;
      })
    },
    deleteFavoriteApp(favouritesId, index){
      api.fetch(`/mock/dss/workspaces/${this.workspaceId}/favorites`, 'delete').then(data=>{
        this.favoriteApps.splice(index, 1);
      })
    },
    addFavoriteApp(){
      
      this.$refs.dynamicForm.validate((valid) => {
        if (valid) {
          // this.addAppLoading = true;
          this.show = false;
          api.fetch(`/mock/dss/workspaces/${this.workspaceId}/favorites`, {applicationId: this.formDynamic.selectApp},'post').then(data=>{
            console.log('添加成功')
            const app = this.findAppByApplicationId(this.formDynamic.selectApp)
            this.favoriteApps.push({
              ...app,
              "favouritesId": data.favouritesId,
              "applicationId": app.id, //application表里的id
            })
          })
        }
      });
    },

    findAppByApplicationId(id){
      const arr = this.flatApps();
      const result = arr.find(item=>item.id==id);
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
        this.searchResult = [{title: '搜索结果', appInstances: [...this.searchResult]}];
        this.search = true;
      }else{
        this.search = false;
      }
    },
    linkTo(item, path){
      this.gotoCommonIframe(item.name, {workspaceId: this.workspaceId})
    },
    navTo(item, path){
      if(path.startsWith('http')){
        window.open(path);
      }else {
        this.$route.push(path);
      }
    }
  },  
  
  computed: {
    types: function(){
      return this.applications.map(item=>({title: item.title, id: item.id}))
    },
    apps: function(){
      console.log('this.formDynamic.selectType', this.formDynamic.selectType);
      if(this.formDynamic.selectType){
        const arr = this.applications.filter(item=>item.id==this.formDynamic.selectType);
        return arr[0].appInstances;
      }
      return [];
    },
    tabsApplication: function(){
      if(this.search){
        console.log('dsadad', this.searchResult, this.applications)
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