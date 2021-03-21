<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{$t('message.workflow.infoHeader')}}</h1>
      </div>
      <!-- <feature @add-project="addProject"></feature> -->
    </div>
    <div>
      <template v-if="dataList.length > 0">
        <project-content-item
          v-for="item in dataList"
          :key="item.id"
          :hide-button-bar="false"
          :hide-publish-andcopy="true"
          :current-data="item"
          :data-list="item.dssFlowList"
          :readonly="myReadonly"
          source="workflow.createWorkflow"
          tag-prop="uses"
          @add="addProject"
          @goto="gotoWorkflow"
          @detail="versionDetail"
          @modify="projectModify"
          @delete="deleteWorkflow">
          <div class="header-search">
            <Input
              search
              class="search-input"
              :placeholder="$t('message.workflow.searchWorkflow')"
              @on-change="searchWorkflow($event, item.id)"/>
          </div>
        </project-content-item>
      </template>
      <template v-else>
        <div class="no-data-bgc">
          <img
            class="no-data-img"
            src="../../../assets/images/no-data.svg"
            :alt="$t('message.workflow.searchWorkflow')" />
          <div>{{$t('message.workflow.searchWorkflow')}}</div>
        </div>
      </template>
    </div>
    <Modal
      v-model="deleteClassifyShow"
      title="删除工作流分类"
      @on-ok="deleteProjectClassifyConfirm">
      确认删除工作流分类{{ deleteClassifyItem.name }}?</Modal>
    <Modal
      v-model="deleteProjectShow"
      :title="$t('message.workflow.deleteWorkflow')"
      @on-ok="deleteProjectConfirm">{{$t('message.workflow.confirmDelete', {name: deleteProjectItem.name})}}</Modal>
    <WorkflowForm
      ref="workflowForm"
      :workflow-data="currentWorkflowData"
      :classify-list="cacheData"
      :add-project-show="ProjectShow"
      :action-type="actionType"
      @show="ProjectShowAction"
      @confirm="ProjectConfirm"></WorkflowForm>
    <VersionDetail
      :version-detail-show="versionDetailShow"
      :version-data="versionData"
      @versionDetailShow="versionDetailAction"
      @goto="versionGotoWorkflow"></VersionDetail>
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <!-- <Modal
      v-model="publishModelShow"
      :title="$t('message.workflow.publishProject')"
      width="520"
      @on-ok="publishProject">
      <publish-form
        ref="publishForm"
        :current-project-data="currentWorkflowData"
        v-show="publishModelShow"
        @modelHidden="modelHidden"
        @updateWorkflow="updateWorkflow"></publish-form>
    </Modal> -->
  </div>
</template>
<script>
import commonModule from '@js/module/common';
import projectContentItem from '@js/component/workflowContentItem';
import WorkflowForm from './module/workflowForm.vue';
import VersionDetail from './module/versionDetail.vue';
import storage from '@/js/helper/storage';
import api from '@/js/service/api';
import pinyin from 'pinyin';
export default {
  components: {
    projectContentItem,
    WorkflowForm,
    VersionDetail,
    // publishForm: commonModule.component.publish,
  },
  data() {
    return {
      addClassifyShow: false, // 添加工作流分类展示
      deleteClassifyShow: false, // 删除分类弹窗展示
      ProjectShow: false, // 添加工作流展示
      versionDetailShow: false,
      deleteProjectShow: false, // 删除工作流弹窗展示
      deleteClassifyItem: '',
      deleteProjectItem: '', // 删除的工作流项
      isRootFlow: false,
      actionType: '', // add || modify
      loading: false,
      projectClassify: {
        name: '',
        descipte: '',
      },
      dataList: [], // 右侧数据过滤后的
      cacheData: [], // 工作流初始树结构，用于搜索过滤
      params: '',
      versionData: [], // 工作流版本信息
      flowTaxonomyID: 0, // 工作流分类的id
      readonly: 'false',
      currentWorkflowData: null,
      workspaceId: null
      // publishModelShow: false,
    };
  },
  computed: {
    myReadonly() {
      if (this.readonly === 'true') {
        return true;
      } else if (this.readonly === 'false') {
        return false;
      } else {
        return false;
      }
    },
  },
  watch: {
    isRootFlow(val) {
      this.getParams(val);
      this.getFlowData(this.params);
    },
    '$route'() {
      this.fetchFlowData();
    }
  },
  created() {
    this.workspaceId = this.$route.query.workspaceId;
    this.fetchFlowData();
  },
  methods: {
    fetchFlowData() {
      this.getParams(this.isRootFlow);
      this.getFlowData(this.params);
      this.readonly = this.$route.query.readonly || 'false';
    },
    // 获取工作流的参数
    getParams(isRootFlow, flowTaxonomyID) {
      if (flowTaxonomyID) {
        this.params = `?projectTaxonomyID=${this.$route.query.projectTaxonomyID}&projectVersionID=${this.$route.query.projectVersionID}&flowTaxonomyID=${flowTaxonomyID}&isRootFlow=${!isRootFlow}&workspaceId=${this.workspaceId}`;
      } else {
        this.params = `?projectTaxonomyID=${this.$route.query.projectTaxonomyID}&projectVersionID=${this.$route.query.projectVersionID}&isRootFlow=${!isRootFlow}&workspaceId=${this.workspaceId}`;
      }
    },
    // 获取所有分类和工作流
    getFlowData(params = '') {
      this.loading = true;
      api.fetch(`/dss/tree${params}`, {}, 'get').then((res) => {
        this.cacheData = res.data;
        this.dataList = this.cacheData;
        storage.set('flowsList', this.cacheData);
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 新增工作流
    addProject() {
      this.ProjectShow = true;
      this.actionType = 'add';
      this.init();
    },
    // 确认新增工作流
    ProjectConfirm(projectData) {
      projectData.projectVersionID = +this.$route.query.projectVersionID;
      let flowList = this.cacheData.filter((item) => {
        return item.id === projectData.taxonomyID;
      });
      if (this.checkName(flowList[0].dssFlowList, projectData.name, projectData.id)) return this.$Message.warning(this.$t('message.workflow.nameUnrepeatable'));
      this.loading = true;
      if (this.actionType === 'add') {
        api.fetch('/dss/addFlow', projectData, 'post').then(() => {
          this.$Message.success(this.$t('message.workflow.createdSuccess'));
          this.getParams(this.isRootFlow);
          this.getFlowData(this.params);
        }).catch(() => {
          this.loading = false;
        });
      } else {
        api.fetch('/dss/updateFlowBaseInfo', projectData, 'post').then(() => {
          this.loading = false;
          this.$Message.success(this.$t('message.workflow.eitorSuccess', { name: projectData.name }));
          this.getParams(this.isRootFlow);
          this.getFlowData(this.params);
        }).catch(() => {
          this.loading = false;
          this.currentWorkflowData.uses = this.$refs.workflowForm.originBusiness;
        });
      }
    },
    // 新增工作流分类展示
    addWorkflowClassifyShow() {
      this.init();
      this.actionType = 'add';
      this.addClassifyShow = true;
    },
    // 修改工作流分类
    modifyClassify(params) {
      this.init();
      this.actionType = 'modify';
      this.addClassifyShow = true;
      this.projectClassify.name = params.name;
      this.projectClassify.description = params.description;
      this.projectClassify.id = params.id;
    },
    // 确认添加分类 || 确认修改
    addProjectClassifyConfirm(projectData) {
      // 调添加接口
      if (this.checkName(this.cacheData, projectData.name, projectData.id)) return this.$Message.warning('分类名不可重复！');
      this.loading = true;
      if (this.actionType === 'add') {
        projectData.projectVersionID = +this.$route.query.projectVersionID;
        api.fetch('/dss/addFlowTaxonomy', projectData, 'post').then(() => {
          this.$Message.success(this.$t('message.workflow.createdSuccess'));
          this.getParams(this.isRootFlow);
          this.getFlowData(this.params);
        }).catch(() => {
          this.loading = false;
        });
      } else {
        projectData.projectVersionID = +this.$route.query.projectVersionID;
        api.fetch('/dss/updateFlowTaxonomy', projectData, 'post').then(() => {
          this.$Message.success(`修改分类${projectData.name}成功！`);
          this.getParams(this.isRootFlow);
          this.getFlowData(this.params);
        }).catch(() => {
          this.loading = false;
        });
      }
    },
    // 删除单项工作流分类
    projectClassifyDelete(params) {
      this.deleteClassifyShow = true;
      this.deleteClassifyItem = params;
    },
    // 确认删除
    deleteProjectClassifyConfirm() {
      // 调用删除接口
      this.loading = true;
      const params = {
        id: this.deleteClassifyItem.id,
        projectVersionID: +this.$route.query.projectVersionID,
      };
      api.fetch('/dss/deleteFlowTaxonomy', params, 'post').then(() => {
        this.$Message.success(`删除分类${this.deleteClassifyItem.name}成功！`);
        this.getParams(this.isRootFlow);
        this.getFlowData(this.params);
      }).catch(() => {
        this.loading = false;
      });
    },
    // 删除单项工作流
    deleteWorkflow(params) {
      this.deleteProjectShow = true;
      this.deleteProjectItem = params;
    },
    // 确认删除单项工作流
    deleteProjectConfirm() {
      if (!this.deleteProjectItem.rootFlow) return this.$Message.error(this.$t('message.workflow.notDelete'));
      // 调用删除接口
      this.loading = true;
      const params = {
        id: this.deleteProjectItem.id,
        sure: false,
        projectVersionID: +this.$route.query.projectVersionID,
      };
      api.fetch('/dss/deleteFlow', params, 'post').then((res) => {
        this.loading = false;
        if (res.warmMsg) {
          this.$Modal.confirm({
            title: this.$t('message.workflow.mandatoryDelete'),
            content: res.warmMsg,
            onOk: () => {
              params.sure = true;
              this.loading = true;
              api.fetch('/dss/deleteProject', params, 'post').then(() => {
                this.$Message.success(this.$t('message.workflow.deleteSuccess', { name: this.deleteProjectItem.name }));
                this.getParams(this.isRootFlow);
                this.getFlowData(this.params);
              }).catch(() => {
                this.loading = false;
              });
            },
          });
        } else {
          this.$Message.success(this.$t('message.workflow.deleteSuccess', { name: this.deleteProjectItem.name }));
          this.getParams(this.isRootFlow);
          this.getFlowData(this.params);
        }
      }).catch(() => {
        this.loading = false;
      });
    },
    init() {
      this.projectClassify = {
        name: '',
        description: '',
      };
      this.currentWorkflowData = {
        name: '',
        description: '',
        taxonomyID: 1,
        uses: '',
      };
    },
    // 修改工作流
    projectModify(classifyId, project) {
      this.init();
      this.ProjectShow = true;
      this.actionType = 'modify';
      this.currentWorkflowData = project;
      this.currentWorkflowData.taxonomyID = classifyId;
    },
    ClassifyShow(val) {
      this.addClassifyShow = val;
    },
    ProjectShowAction(val) {
      this.ProjectShow = val;
    },
    // 点击工程跳转到工作流编辑
    gotoWorkflow(item, subItem) {
      // 这里的一定是最新版本
      this.$emit('open-workflow', {
        ...this.$route.query,
        flowTaxonomyID: item.id,
        flowId: subItem.id,
        readonly: this.$route.query.readonly || 'false',
        parentReadonly: this.readonly,
        name: subItem.name,
        version: ''
      })
    },
    // 查看版本详情
    versionDetail(classifyId, project) {
      this.versionDetailShow = true;
      const prams = {
        id: project.id,
        projectVersionID: +this.$route.query.projectVersionID,
      };
      for (let i = 0; i < this.dataList.length; i++) {
        for (let j = 0; j < this.dataList[i].dssFlowList.length; j++) {
          if (this.dataList[i].dssFlowList[j].id === project.id) {
            this.flowTaxonomyID = this.dataList[i].id;
            break;
          }
        }
      }
      api.fetch(`/dss/listAllFlowVersions`, prams, 'get').then((res) => {
        // 新加tab功能需要工作流名称
        this.versionData = res.versions.map((item) => {
          item.name = project.name
          return item});
      });
    },
    versionDetailAction(val) {
      this.versionDetailShow = val;
    },
    searchWorkflow(event, id) {
      if (event.target.value) {
        let tepArray = storage.get('flowsList');
        this.dataList = tepArray.map((item) => {
          if (id === item.id) {
            item.dssFlowList = item.dssFlowList.filter((subItem) => {
              return subItem.name.indexOf(event.target.value) != -1;
            });
          }
          return item;
        })
      } else {
        this.dataList = storage.get('flowsList');
      }
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
      if (item) { // 查单项
        this.getParams(this.isRootFlow, item.id);
        this.loading = true;
        api.fetch(`/dss/tree${this.params}`, {}, 'get').then((res) => {
          this.dataList = res.data;
          this.loading = false;
        }).catch(() => {
          this.loading = false;
        });
      } else { // 查所有
        this.getParams(this.isRootFlow);
        this.getFlowData(this.params);
      }
    },
    // 面包屑的点击事件
    breadcrumbClick(item) {
      if (item.goBack) {
        this.goBack()
      } else if(item.link) {
        //
      }
    },
    // 点击版本的打开跳转工作流编辑
    versionGotoWorkflow(row) {
      this.$emit('open-workflow',
        {
          ...this.$route.query,
          flowId: row.flowID,
          version: row.version,
          readonly: row._index === 0 && !this.myReadonly ? 'false' : 'true',
          parentReadonly: this.readonly,
          name: `${row.name}(历史版本)`
        });
    },
    goBack() {
      this.$router.go(-1);
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
    gotoVisualis() {
      const query = this.$route.query;
      this.$router.push({ path: '/kanban', query });
    },
    // openPublishModal() {
    //   // 发布之前先判断是否已经在发布
    //   const precentList = storage.get('precentList') || [];
    //   if (precentList.map((item) => item.id).includes(this.$route.query.projectID)) return this.$Message.warning("工程正在发布中！")
    //   this.publishModelShow = true;
    //   this.currentWorkflowData = storage.get('currentProject');
    //   if (!this.currentWorkflowData) {
    //     this.$router.push('/project');
    //   }
    // },
    // publishProject() {
    //   this.$refs.publishForm.publish();
    //   this.currentWorkflowData.percent = '2%';
    //   const precentList = storage.get('precentList') || [];
    //   precentList.push(this.currentWorkflowData);
    //   storage.set('precentList', precentList);
    // },
    modelHidden(val) {
      this.projectModelShow = val;
    },
    // 工作流发布成功更细页面
    updateWorkflow() {
      api.fetch(`/dss/tree`, {}, 'get').then((res) => {
        const projectTaxonomyID = this.$route.query.projectTaxonomyID;
        const projectId = this.$route.query.projectID;
        res.data.forEach((Taxonomy) => {
          if (Taxonomy.id === projectTaxonomyID) {
            Taxonomy.dssProjectList.forEach((project) => {
              if (project.id === projectId) {
                const newProjectVersionId = project.latestVersion.id;
                this.$router.push({
                  name: 'Workflow',
                  query: {
                    projectTaxonomyID: projectTaxonomyID,
                    projectID: projectId,
                    projectVersionID: newProjectVersionId,
                  }
                });
              }
            })
          }
        })
      });
    }
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
