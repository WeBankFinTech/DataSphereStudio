<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{$t('message.project.infoHeader')}}</h1>
        <p>{{$t('message.project.infoBodyFirstRow')}}</p>
        <p>{{$t('message.project.infoBodySecondRow')}}</p>
      </div>
      <feature @add-project="addProject"></feature>
    </div>
    <template v-if="dataList.length > 0">
      <project-content-item
        v-for="item in dataList"
        :key="item.id"
        :hide-button-bar="false"
        :hide-publish-andcopy="false"
        :data-list="item.dssProjectList"
        :current-data="item"
        :precent-list="precentList"
        tag-prop="business"
        @goto="gotoWorkflow"
        @modify="projectModify"
        @delete="deleteProject"
        @copy="copyProject"
        @publish="publishProject"
        @detail="versionDetail">
        <div class="header-search">
          <Input
            search
            class="search-input"
            :placeholder="$t('message.project.search')"
            @on-change="searchProject($event, item.id)"/>
          <Dropdown class="sort-icon" @on-click="sortTypeChange($event, item.id)">
            {{ sortType[item.id] }}
            <svg t="1569743752958" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3388" width="16" height="16"><path d="M0 90.4h361.5v90.4h-361.5v-90.4z" fill="" p-id="3389"></path><path d="M0 512h512.1v90.4h-512.1v-90.4z" fill="" p-id="3390"></path><path d="M0 928.267h994.1v90.4h-994.1v-90.4z" fill="" p-id="3391"></path><path d="M753.1 0h90.4v722.8h-90.4v-722.8z" fill="" p-id="3392"></path><path d="M1024 575.801l-61.3-63.7-164.4 194.7-164.5-194.7-61.3 63.7 225.801 267.3 57.5-68.1z" fill="" p-id="3393"></path></svg>
            <DropdownMenu slot="list">
              <DropdownItem
                v-for="(item) in sortTypeList"
                :name="item.value"
                :key="item.value">
                {{ item.lable}}
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </div>
      </project-content-item>
    </template>
    <template v-else>
      <div class="no-data-bgc">
        <!-- <img
          class="no-data-img"
          src="../../../assets/images/no-data.svg"
          alt="暂无数据请添加"/>
        <div>暂无数据请添加</div> -->
        <img
          class="no-data-img"
          src="../../../assets/images/appmap.png"
          :alt="$t('message.project.noData')"/>
      </div>
    </template>
    <Modal
      v-model="deleteProjectShow"
      :title="$t('message.project.deleteProject')"
      @on-ok="deleteProjectConfirm">
      {{$t('message.project.confirmDeleteProject')}}{{ deleteProjectItem.name }}?
      <br/>
      <br/>
      <div class="radio-box">
        <Radio
          size="large"
          v-model="deleteProjectItem.ifDelScheduler">{{$t('message.project.deleteWtss')}}</Radio>
      </div>
    </Modal>
    <ProjectForm
      ref="projectForm"
      :action-type="actionType"
      :project-data="currentProjectData"
      :classify-list="cacheData"
      :add-project-show="ProjectShow"
      @show="ProjectShowAction"
      @confirm="ProjectConfirm"></ProjectForm>
    <Modal
      v-model="projectModelShow"
      :title="commonTitle"
      :width="currentForm === 'VersionDetail' ? '910' : '520'"
      :footer-hide="currentForm === 'VersionDetail'"
      @on-ok="commonComfirm"
      @on-cancel="commonCancel">
      <component
        ref="publish"
        :is="currentForm"
        :current-project-data="currentProjectData"
        v-if="projectModelShow"
        @modelHidden="modelHidden"></component>
    </Modal>
    <!-- <Modal
      title="工程资源"
      :footer-hide="true"
      v-model="showResourceView">
      <resource
        v-show="showResourceView"
        :resources="projectResources"
        @update-resources="updateResources"></resource>
    </Modal> -->
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import projectContentItem from '@js/component/workflowContentItem';
import ProjectForm from './module/projectForm.vue';
import copyForm from './module/copyForm.vue';
import commonModule from '@js/module/common';
import VersionDetail from './module/versionDetail.vue';
import resource from './module/resource.vue';
import feature from './feature.vue'
import storage from '@/js/helper/storage';
import api from '@/js/service/api';
import pinyin from 'pinyin';
export default {
  components: {
    projectContentItem,
    ProjectForm,
    VersionDetail,
    copyForm,
    publishForm: commonModule.component.publish,
    resource,
    feature,
  },
  data() {
    return {
      ProjectShow: false, // 添加工程展示
      deleteProjectShow: false, // 删除工程弹窗展示
      deleteProjectItem: '', // 删除的工程项
      actionType: '', // add || modify
      loading: false,
      projectModelShow: false, // 发布,复制，版本的弹窗
      currentProjectClassify: {
        name: '',
        descipte: '',
      },
      currentProjectData: {
        name: '',
        description: '',
        taxonomyID: 1,
        business: '',
        applicationArea: '',
        product: '',
      },
      dataList: [],
      cacheData: [],
      currentForm: 'publishForm',
      commonTitle: '',
      precentList: [],
      sortType: {},
      showResourceView: false, // 是否展示资源文件上传
      projectResources: [], // 工程级别资源文件
      activeItem: {},
    };
  },
  computed: {
    sortTypeList() {
      return [
        { lable: this.$t('message.project.sortUpdateTime'), value: 'updateTime' },
        { lable: this.$t('message.project.sortName'), value: 'name' },
      ]
    },
    tips() {
      return this.$t('message.project.tips');
    }
  },
  created() {
    // 获取所有分类和工程
    this.getclassListData();
  },
  mounted() {
    this.$nextTick(() => {
      this.precentList = storage.get('precentList') || [];
    });
  },
  methods: {
    getclassListData() {
      this.loading = true;
      return api.fetch(`/dss/tree`, {}, 'get').then((res) => {
        this.cacheData = res.data;
        this.dataList = this.cacheData;
        this.activeItem = this.dataList[0];
        this.dataList.forEach(item => {
          this.sortType[item.id] = this.$t('message.project.updteTime');
        })
        this.sortTypeChange();
        storage.set('projectList', this.cacheData, 'local');
        this.loading = false;
        return this.cacheData;
      });
    },
    // 新增工程
    addProject() {
      this.ProjectShow = true;
      this.actionType = 'add';
      this.init();
    },
    // 确认新增工程 || 确认修改
    ProjectConfirm(projectData) {
      let projectList = this.cacheData.filter((item) => {
        return item.id === projectData.taxonomyID;
      });
      if (this.checkName(projectList[0].dssProjectList, projectData.name, projectData.id)) return this.$Message.warning(this.$t('message.project.nameUnrepeatable'));
      this.loading = true;
      if (this.actionType === 'add') {
        api.fetch('/dss/addProject', projectData, 'post').then(() => {
          this.$Message.success(`${this.$t('message.project.createProject')}${this.$t('message.newConst.success')}`);
          this.getclassListData().then((data) => {
            // 新建完工程进到工作流页
            const currentProject = data[0].dssProjectList.filter((project) => project.name === projectData.name)[0];
            this.$router.push({
              name: 'Workflow',
              query: {
                projectTaxonomyID: 1,
                projectID: currentProject.latestVersion.projectID,
                projectVersionID: currentProject.latestVersion.id,
              }
            });
          });
        }).catch(() => {
          this.loading = false;
        });
      } else {
        api.fetch('/dss/updateProject', projectData, 'post').then(() => {
          this.$Message.success(this.$t('message.project.eidtorProjectSuccess', { name: projectData.name }));
          this.getclassListData();
        }).catch(() => {
          this.loading = false;
          this.currentProjectData.business = this.$refs.projectForm.originBusiness;
        });
      }
    },
    // 删除单项工程
    deleteProject(params) {
      this.deleteProjectShow = true;
      this.deleteProjectItem = params;
      this.deleteProjectItem.ifDelScheduler = false;
    },
    // 确认删除单项工程
    deleteProjectConfirm() {
      // 调用删除接口
      this.loading = true;
      const params = {
        id: this.deleteProjectItem.id,
        sure: false,
        ifDelScheduler: this.deleteProjectItem.ifDelScheduler,
      };
      api.fetch('/dss/deleteProject', params, 'post').then((res) => {
        this.loading = false;
        if (res.warmMsg) {
          this.$Modal.confirm({
            title: this.$t('message.project.deleteTitle'),
            content: res.warmMsg,
            onOk: () => {
              params.sure = true;
              this.loading = true;
              api.fetch('/dss/deleteProject', params, 'post').then(() => {
                this.$Message.success(`${this.$t('message.project.deleteProject')}${this.deleteProjectItem.name}${this.$t('message.newConst.success')}`);
                this.getclassListData();
              }).catch(() => {
                this.loading = false;
              });
            },
            onCancel: () => {
            },
          });
        } else {
          this.$Message.success(this.$t('message.project.deleteProjectSuccess', { name: this.deleteProjectItem.name }));
          this.getclassListData();
        }
      }).catch(() => {
        this.loading = false;
      });
    },
    init() {
      this.currentProjectClassify = {
        name: '',
        description: '',
      };
      this.currentProjectData = {
        name: '',
        description: '',
        taxonomyID: 1,
        business: '',
        applicationArea: '',
        product: '',
      };
    },
    // 修改工程
    projectModify(classifyId, project) {
      this.init();
      this.ProjectShow = true;
      this.actionType = 'modify';
      this.currentProjectData = project;
      this.currentProjectData.taxonomyID = classifyId;
    },
    ProjectShowAction(val) {
      this.ProjectShow = val;
    },
    // 点击工程跳转到工作流
    gotoWorkflow(item, subItem) {
      storage.set('currentProject', subItem);
      const query = {
        projectTaxonomyID: item.id,
        projectID: subItem.id,
        projectVersionID: subItem.latestVersion.id,
        projectName: subItem.name
      }
      this.$router.push({
        name: 'Workflow',
        query,
      });
      this.dispatch('IndexedDB:clearProjectCache');
      this.dispatch('IndexedDB:addProjectCache', {
        projectID: subItem.id,
        value: {
          projectData: query
        }
      });
    },
    // 搜索对应的工程
    searchProject(event, id) {
      if (event.target.value) {
        let tepArray = storage.get('projectList', 'local');
        this.dataList = tepArray.map((item) => {
          if (id === item.id) {
            item.dssProjectList = item.dssProjectList.filter((subItem) => {
              return subItem.name.indexOf(event.target.value) != -1;
            });
          }
          return item;
        })
      } else {
        this.dataList = storage.get('projectList', 'local');
      }
      this.sortTypeChange();
    },
    // 分类重名检查
    checkName(data, name, id) {
      let flag = false;
      data.map((item) => {
        if (item.name === name && item.id !== id) {
          flag = true;
        }
      });
      return flag;
    },
    // 点击左侧单项分类
    active(item) {
      this.activeItem = item;
      if (item) { // 查单项
        this.loading = true;
        api.fetch(`/dss/tree?projectTaxonomyID=${item.id}`, 'get').then((res) => {
          this.dataList = res.data;
          this.sortTypeChange();
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
      } else { // 查所有
        this.getclassListData();
      }
    },
    // 复制工程
    copyProject(classifyId, project) {
      this.init();
      this.currentForm = 'copyForm';
      this.currentProjectData = project;
      this.currentProjectData.taxonomyID = classifyId;
      this.commonTitle = this.$t('message.project.projectCopy');
      this.projectModelShow = true;
    },
    publishProject(classifyId, project) {
      this.init();
      this.currentForm = 'publishForm';
      this.currentProjectData = project;
      this.currentProjectData.taxonomyID = classifyId;
      this.commonTitle = this.$t('message.project.projectPublish');
      this.projectModelShow = true;
    },
    // 版本详情展示
    versionDetail(classifyId, project) {
      this.init();
      this.currentForm = 'VersionDetail';
      this.currentProjectData = project;
      this.currentProjectData.taxonomyID = classifyId;
      this.commonTitle = this.$t('message.project.projectVersion'); ;
      this.projectModelShow = true;
    },
    commonComfirm() {
      if (this.currentForm === 'copyForm') {
        const copyCheckName = (name) => {
          let projectList = this.cacheData.filter((item) => {
            return item.id === this.currentProjectData.taxonomyID;
          });
          if (this.checkName(projectList[0].dssProjectList, name, this.currentProjectData.id)) return this.$Message.warning(this.$t('message.project.nameUnrepeatable'));
        };
        this.dispatch('Project:copy', copyCheckName);
      } else if (this.currentForm === 'publishForm') {
        this.$refs.publish.publish();
        this.currentProjectData.percent = '2%';
        this.precentList.push(this.currentProjectData);
        storage.set('precentList', this.precentList);
      }
    },
    commonCancel() {
      if (this.currentForm === 'copyForm') {
        this.dispatch('Project:copyReset');
      } else if (this.currentForm === 'publishForm') {
        // this.dispatch('Common:publish');
      }
    },
    modelHidden(val) {
      this.projectModelShow = val;
      this.getclassListData();
    },
    'Project:loading'(val) {
      this.loading = val;
    },
    'Project:getData'() {
      this.getclassListData();
      this.precentList = storage.get('precentList') || [];
    },
    'Project:updataPercent'(currentProjectData) {
      this.precentList = this.precentList.map((item) => {
        if (item.id === currentProjectData.id) {
          item = currentProjectData;
        }
        return item;
      });
      storage.set('precentList', this.precentList);
    },
    // 将传入的数组根据当前系统语言，按照中文或英文名重新排序，会影响原数组
    arraySortByName(list) {
      if (list === undefined || list === null) return [];
      list.sort((a, b) => {
        let strA = a.name;
        let strB = b.name;
        // 谁为非法值谁在前面
        if (strA === undefined || strA === null || strA === '' || strA === ' ' || strA === '　') {
          return -1;
        }
        if (strB === undefined || strB === null || strB === '' || strB === ' ' || strB === '　') {
          return 1;
        }
        const charAry = strA.split('');
        for (const i in charAry) {
          if ((this.charCompare(strA[i], strB[i]) !== 0)) {
            return this.charCompare(strA[i], strB[i]);
          }
        }
        // 如果通过上面的循环对比还比不出来，就无解了，直接返回-1
        return -1;
      });
      return list.sort((a) => {
        if (a.name === '我的工程') {
          return -1;
        }
        if (a.name === '我参与的工程') {
          return -1;
        }
      });
    },
    charCompare(charA, charB) {
      // 谁为非法值谁在前面
      if (charA === undefined || charA === null || charA === '' || charA === ' ' || charA === '　') {
        return -1;
      }
      if (charB === undefined || charB === null || charB === '' || charB === ' ' || charB === '　') {
        return 1;
      }
      if (!this.notChinese(charA)) {
        charA = pinyin(charA)[0][0];
      }
      if (!this.notChinese(charB)) {
        charB = pinyin(charB)[0][0];
      }
      return charA.localeCompare(charB);
    },
    notChinese(char) {
      const charCode = char.charCodeAt(0);
      return charCode >= 0 && charCode <= 128;
    },
    sortTypeChange(name = 'updateTime', id) {
      this.sortType[id] = name === 'updateTime' ? this.$t('message.project.updteTime') : this.$t('message.project.name')
      this.dataList = this.dataList.map((item) => {
        if (!id || id === item.id) {
          item.dssProjectList = item.dssProjectList.sort((a, b) => {
            if (name === 'updateTime') {
              return b.latestVersion[name] - a.latestVersion[name];
            } else {
              return this.charCompare(a[name], b[name]);
            }
          });
        }
        return item;
      });
    },
    // 展示资源上传组件
    showResourceViewAction(classifyId, project) {
      this.showResourceView = true;
      /**
       * 待确认问题
       * 1.资源文件上传后存储位置
       * 2.已上传的如何获取
      */
      this.projectResources = project.projectResources = [];
    },
    // 资源上传后的回调
    updateResources(res) {
      this.projectResources = res.map((item) => {
        return {
          fileName: item.fileName,
          resourceId: item.resourceId,
          version: item.version,
        };
      });
    },
    gotoScriptis() {
      this.$router.push('/home');
    },
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
