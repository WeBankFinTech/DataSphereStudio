<template>
  <div class="page-process" ref="page_process">
    <Card
      v-if="!checkEditable(query) && showTip"
      shadow
      class="process-readonly-tip-card"
    >
      <div>
        {{$t("message.workflow.workflowItem.readonlyTip")}}
      </div>
      <Icon type="md-close" class="tipClose" @click.stop="closeTip" />
    </Card>
    <div class="process-tabs">
      <div class="process-tab">
        <div
          v-for="(item, index) in tabs"
          :key="index"
          class="process-tab-item"
          :class="{active: index===active}"
          @click="choose(index)"
          @mouseenter.self="item.isHover = true"
          @mouseleave.self="item.isHover = false"
        >
          <div>
            <img
              class="tab-icon"
              :class="nodeImg[item.node.type].class"
              :src="nodeImg[item.node.type].icon"
              alt
            />
            <div :title="item.title" class="process-tab-name">{{ item.title }}</div>
            <SvgIcon v-show="!item.isHover && item.node && item.node.isChange && checkEditable(query)" class="process-tab-unsave-icon" icon-class="fi-radio-on2"/>
            <Icon
              v-if="item.isHover && (item.close || query.product)"
              type="md-close"
              @click.stop="remove(index)"
            />
          </div>
        </div>
      </div>
      <div class="process-container">
        <template v-for="(item, index) in tabs">
          <Process
            ref="process"
            v-if="item.type === 'Process'"
            v-show="index===active"
            :key="item.key"
            :import-replace="false"
            :flow-id="item.data.appId"
            :version="item.data.version"
            :product="query.product"
            :readonly="!checkEditable(query)"
            :publish="query.releasable"
            :isLatest="query.isLatest === 'true'"
            :tabs="tabs"
            :open-files="openFiles"
            :orchestratorId="item.data.id"
            :orchestratorVersionId="item.data.orchestratorVersionId"
            @changeMap="changeTitle"
            @node-dblclick="dblclickNode(index, arguments)"
            @isChange="isChange(index, arguments)"
            @save-node="saveNode"
            @check-opened="checkOpened"
            @deleteNode="deleteNode"
            @saveBaseInfo="saveBaseInfo"
            @updateWorkflowList="$emit('updateWorkflowList')"
            @showDolphinscheduler="showDS"
          ></Process>
          <Ide
            v-if="item.type === 'IDE'"
            v-show="index===active"
            :key="item.title"
            :parameters="item.data"
            :node="item.node"
            :in-flows-index="index"
            :readonly="!checkEditable(query)"
            @save="saveIDE(index, arguments)"
          ></Ide>
          <commonIframe
            v-if="item.type === 'Iframe'"
            v-show="index===active"
            :key="item.title"
            :parametes="item.data"
            :node="item.node"
            @save="saveNode"
          ></commonIframe>
          <div
            v-if="item.type === 'DS'"
            v-show="index===active"
            :key="item.title"
          >
            <DS :query="query" :tab-name="query.name"></DS>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty, isArguments } from "lodash";
import api from "@/common/service/api";
import util from "@/common/util";
import Process from "./module.vue";
import Ide from "@/apps/workflows/module/ide";
import commonModule from "@/apps/workflows/module/common";
import { NODETYPE, NODEICON } from "@/apps/workflows/service/nodeType";

import DS from '@/apps/workflows/module/dispatch'

export default {
  components: {
    Process,
    Ide: Ide.component,
    commonIframe: commonModule.component.iframe,
    DS
  },
  props: {
    query: {
      type: Object,
      default: () => {}
    }
  },
  computed: {},
  data() {
    return {
      tabs: [
        {
          title: this.$t("message.workflow.process.index.BJMS"),
          type: "Process",
          close: false,
          data: this.query,
          node: {
            isChange: false,
            type: "workflow.subflow"
          },
          key: "工作�?",
          isHover: false
        }
      ],
      active: 0,
      setIntervalID: "",
      setTime: 40,
      showTip: true,
      openFiles: {},
      nodeImg: NODEICON
    }
  },
  mounted() {
    this.getCache().then(tabs => {
      if (tabs) {
        this.tabs = tabs;
      }
    });
    this.updateProjectCacheByActive();
    this.changeTitle(false);
  },
  methods: {
    getTaskInstanceList(data, cb, pageSize=10, pageNo=1) {
      if (!this.dagProcessId) return
      api.fetch(`dolphinscheduler/projects/${this.projectName}/task-instance/list-paging`, {
        processInstanceId: this.dagProcessId,
        pageSize,
        pageNo,
        name: data.label
      }, 'get').then((res) => {
        let list = res.totalList
        let thisTimeList = list.filter(item => item.flag === 'YES')
        for (let i = 0; i < thisTimeList.length; i++) {
          if (thisTimeList[i].name === data.label) {
            return cb && cb(thisTimeList[i].id)
          }
        }
        return cb && cb()
      }).catch(() => {
      })
    },
    // 判断是否有意编辑权限
    // 没有权限的和历史的都不可编辑
    checkEditable(item) {
      // 编排权限由后台的priv字段判断�?1-查看�? 2-编辑�? 3-发布
      if (item.editable && this.query.readonly !== 'true') {
        return true
      } else {
        return false
      }
      // if ([2,3].includes(item.priv) && this.query.readonly !== 'true') {
      //   return true
      // } else {
      //   return false
      // }
    },
    gotoAction(back = -1) {
      if (back) {
        this.$router.go(back);
      }
    },
    // 提示卡片关闭
    closeTip() {
      this.showTip = false;
    },
    choose(index) {
      this.active = index;
      this.updateProjectCacheByActive();
    },
    remove(index) {
      // 如果删掉的是第一个tab，就返回上一�?
      if (index === 0) {
        this.$router.go(-1);
      }
      // 删掉子工作流得删掉当前打�?的子节点
      const currentTab = this.tabs[index];
      // 找到当前关闭项对应的子类
      const subArray = this.openFiles[currentTab.key] || [];
      const changeList = this.tabs.filter(item => {
        return subArray.includes(item.key) && item.node.isChange;
      });
      // 子工作流关闭时，查询是否有子节点没有保存，是否一起关�?
      if (changeList.length > 0 && currentTab.node.type === NODETYPE.FLOW) {
        let text = `<p>${this.$t("message.workflow.process.index.WBCSFGB")}</p>`;
        if (currentTab.node.isChange) {
          text = `<p>${this.$t("message.workflow.process.index.GGZLWBC")}</p>`;
        }
        this.$Modal.confirm({
          title: this.$t("message.workflow.process.index.GBTS"),
          content: text,
          okText: this.$t("message.workflow.process.index.QRGB"),
          cancelText: this.$t("message.workflow.process.index.QX"),
          onOk: () => {
            // 删除线先判断删除的是否是当前正在打开的tab，如果打�?到最后一个tab，如果没有打�?还是在当前的tab
            if (this.active === index) {
              // 删除的就是当前打�?�?
              this.tabs.splice(index, 1);
              this.choose(this.tabs.length - 1);
            } else {
              this.tabs.splice(index, 1);
              // this.choose(this.tabs.length - 1);
            }
            this.updateProjectCacheByTab();
          },
          onCancel: () => {}
        });
      } else {
        // 删除线先判断删除的是否是当前正在打开的tab，如果打�?到最后一个tab，如果没有打�?还是在当前的tab
        if (this.active === index) {
          // 删除的就是当前打�?�?
          this.tabs.splice(index, 1);
          this.choose(this.tabs.length - 1);
        } else {
          this.tabs.splice(index, 1);
          // this.choose(this.tabs.length - 1);
        }
        this.updateProjectCacheByTab();
      }
    },
    check(node) {
      if (node) {
        let boolean = true;
        this.tabs.map(item => {
          if (node.key === item.key) {
            boolean = true;
          } else {
            if (this.tabs.length > 10) {
              boolean = false;
              return;
            }
            boolean = true;
          }
        });
        if (!boolean) {
          this.$Message.warning(this.$t("message.workflow.process.index.CCXE"));
        }
        return boolean;
      } else {
        if (this.tabs.length > 10) {
          this.$Message.warning(this.$t("message.workflow.process.index.CCXE"));
          return false;
        }
        return true;
      }
    },
    dblclickNode(index, args) {
      if (!this.check(args[0][0])) {
        return;
      }
      const node = args[0][0];
      // 如果节点已打�?，则选择
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === node.key) return this.choose(i);
      }
      // 目前的内部节点的supportJump为true，但是没有url,且不�?要创建弹�?
      if (node.supportJump && !node.shouldCreationBeforeNode && !node.jumpUrl) {
        const len = node.resources ? node.resources.length : 0;
        if (len && node.jobContent && node.jobContent.script) {
          // 判断是否有保存过脚本
          const resourceId = node.resources[0].resourceId;
          const fileName = node.resources[0].fileName;
          const version = node.resources[0].version;
          let config = {
            method: "get"
          };
          if (this.query.product) {
            config.headers = {
              "Token-User": this.getUserName()
            };
          }
          api.fetch(this.query.product ? "/filesystem/product/openScriptFromBML" : "/filesystem/openScriptFromBML", {
            fileName,
            resourceId,
            version,
            creator: node.creator || "",
            projectName: this.$route.query.projectName || ""
          }, config).then(res => {
            let content = res.scriptContent;
            let params = {};
            params.variable = this.convertSettingParamsVariable(res.metadata);
            params.configuration = !node.params || isEmpty(node.params.configuration) ? {
              special: {},
              runtime: {},
              startup: {}
            } : {
              special: node.params.configuration.special || {},
              runtime: node.params.configuration.runtime || {},
              startup: node.params.configuration.startup || {}
            };
            params.configuration.runtime.contextID = node.contextID;
            params.configuration.runtime.nodeName = node.title;
            this.getTabsAndChoose({
              type: "IDE",
              node,
              data: {
                content,
                params
              }
            });
          });
        } else {
          // 如果节点是导入进来的，可能存在脚本内�?
          let content = node.jobContent && node.jobContent.code ? node.jobContent.code : "";
          let params = {};
          params.variable = this.convertSettingParamsVariable({});
          params.configuration = !node.params || isEmpty(node.params.configuration) ? {
            special: {},
            runtime: {},
            startup: {}
          } : {
            special: node.params.configuration.special || {},
            runtime: node.params.configuration.runtime || {},
            startup: node.params.configuration.startup || {}
          };
          params.configuration.runtime.contextID = node.contextID;
          params.configuration.runtime.nodeName = node.title;
          this.getTabsAndChoose({
            type: "IDE",
            node,
            data: {
              content,
              params
            }
          });
        }
        return;
      }
      if (node.type == NODETYPE.FLOW) {
        // 子流程必须已保存, 才可以被打开
        let flowId = node.jobContent ? node.jobContent.embeddedFlowId : "";
        let {orchestratorVersionId, id} = {...this.query}
        this.getTabsAndChoose({
          type: "Process",
          node,
          data: {
            appId: flowId,
            orchestratorVersionId,
            id
          }
        });
        return;
      }
      // iframe打开的节�?
      if (node.supportJump && node.jumpUrl) {
        let id = node.jobContent ? node.jobContent.id : "";
        this.getTabsAndChoose({
          type: "Iframe",
          node,
          data: {
            id
          }
        });
      }
    },
    getTabsAndChoose({ type, node, data }) {
      this.$set(node, "isChange", false);
      this.tabs.push({
        type,
        key: node.key,
        title: node.title,
        close: true,
        // 把节点的引用放到这里
        node,
        data,
        isHover: false
      });
      // 记录打开的tab的依赖关�?
      this.openFileAction(node);
      this.choose(this.tabs.length - 1);
      this.updateProjectCacheByTab();
    },
    openFileAction(node) {
      // 判断当前打开的节点的父工作过流是否已经有打开的节点s
      const currnentTab = this.tabs[this.active];
      if (Object.keys(this.openFiles).includes(currnentTab.key)) {
        Object.keys(this.openFiles).map(key => {
          // 找到同一父节点下是否曾今已经打开�?
          if (key == currnentTab.key) {
            if (!this.openFiles[key].includes(node.key)) {
              this.openFiles[key].push(node.key);
            }
          }
        });
      } else {
        this.openFiles[currnentTab.key] = [node.key];
      }
    },
    saveIDE(index, args) {
      if (!this.check()) {
        return;
      }
      if (args[0].data) {
        this.tabs[index].data = args[0].data;
      }
      // 取到节点
      let node = this.tabs[index].node;
      this.saveNode(args, node, true);
    },
    saveNode(args, node, scriptisSave = true) {
      // scriptisSave用来判断是否是脚本保存的触发和关联触�?
      // 这个地方注意：在关联脚本、scriptis保存脚本、qualitis保存都会调用，参数不�?样，关联脚本args是对象，scriptis保存是arguments, qualitis保存过来的是空对象，�?以要处理�?
      let resource = args;
      let currentNode = node;
      if (isArguments(args)) {
        resource = args[0].resource;
        currentNode = args[0].node;
      }
      if (!node.jobContent) {
        node.jobContent = {};
      }
      if (!currentNode || node.key !== currentNode.key) return;
      node.jobContent.script = resource.fileName;
      delete node.jobContent.code; // code提交生成script之后删除code
      if (!node.resources) {
        node.resources = [];
      }
      // qualitis 过来是没有�?�的, 空对象传给后台会报错
      if (Object.keys(resource).length > 0) {
        if (
          node.resources.length > 0 &&
          node.resources[0].resourceId === resource.resourceId
        ) {
          // 已保存过的直接替换，没有保存的首项追�?
          node.resources[0] = resource;
        } else {
          node.resources.unshift(resource);
        }
      }
      this.$refs.process.forEach(item => {
        item.json.nodes.forEach(subitem => {
          if (subitem.key === currentNode.key) {
            // 在这里直接改originalData值，组件里并没有相应，所以改为触发组件事�?
            item.updateOriginData(node, scriptisSave);
          }
        });
      });
      // 更新节点的编辑器的内容也更新缓存的tabs
      this.updateProjectCacheByTab();
    },
    convertSettingParamsVariable(params) {
      const variable = isEmpty(params.variable) ? [] : util.convertObjectToArray(params.variable);
      return variable;
    },
    saveTip(cb, cancel) {
      this.$Modal.confirm({
        title: this.$t("message.workflow.process.index.FHTX"),
        content: `<p>${this.$t("message.workflow.process.index.WBCSFBC")}</p>`,
        okText: this.$t("message.workflow.process.index.BC"),
        cancelText: this.$t("message.workflow.process.index.QX"),
        onOk: cb,
        onCancel: cancel
      });
    },
    isChange(index, val) {
      if (this.tabs[index].node) {
        this.tabs[index].node.isChange = val[0];
        this.$emit("isChange", val[0]);
      }
    },
    beforeLeaveHook() {},
    checkOpened(node, cb) {
      const isOpened = this.tabs.find(item => item.key === node.key);
      cb(!!isOpened);
    },
    deleteNode(node) {
      let index = null;
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === node.key) {
          index = i;
        }
      }
      if (index) {
        this.remove(index);
      }
    },
    saveBaseInfo(node) {
      this.tabs = this.tabs.map(item => {
        if (item.key === node.key) {
          item.title = node.title;
        }
        return item;
      });
    },
    updateProjectCacheByTab() {
      this.dispatch("workflowIndexedDB:updateProjectCache", {
        projectID: this.$route.query.projectID,
        key: "tabList",
        value: {
          tab: this.tabs,
          ***REMOVED***",
          sKey: "tab",
          sValue: this.query.flowId
        },
        isDeep: true
      });
    },
    updateProjectCacheByActive() {
      this.dispatch("workflowIndexedDB:updateProjectCache", {
        projectID: this.$route.query.projectID,
        key: "tabList",
        value: {
          active: this.active,
          ***REMOVED***",
          sKey: "active",
          sValue: this.query.flowId
        },
        isDeep: true
      });
    },
    getCache() {
      return new Promise(resolve => {
        this.dispatch("workflowIndexedDB:getProjectCache", {
          projectID: this.$route.query.projectID,
          cb: cache => {
            const list = (cache && cache.tabList) || [];
            let tabs = null;
            list.forEach(item => {
              if (+item.flowId === +this.query.flowId) {
                tabs = item.tab;
                this.active = item.active || 0;
              }
            });
            resolve(tabs);
          }
        });
      });
    },
    changeTitle(val) {
      // 地图模式下，名字为地图模式；�?新工作流可编辑时，名字为编辑模式；历史版本进去时，为只读模式
      if (val) {
        this.tabs[0].title = this.$t("message.workflow.process.index.DTMS");
      } else {
        if (this.query.readonly === "true") {
          this.tabs[0].title = this.$t("message.workflow.process.index.ZDMS");
        } else {
          this.tabs[0].title = this.$t("message.workflow.process.index.BJMS");
        }
      }
    },
    showDS() {
      util.checkToken(() => {
        let tab = {
          title: this.query.name + '-' + this.$t("message.workflow.process.schedule"),
          type: "DS",
          close: true,
          data: this.query,
          node: {
            isChange: false,
            type: "workflow.subflow"
          },
          key: this.query.appId,
          isHover: false
        }
        for (let i = 0;i < this.tabs.length; i++) {
          let cur = this.tabs[i]
          // 已经打开
          if (cur.key === this.query.appId) {
            return  this.choose(i)
          }
        }
        this.tabs.push(tab)
        this.choose(this.tabs.length - 1)
      })
    }
  }
};
</script>
<style lang="scss" src="@/apps/workflows/assets/styles/process.scss"></style>
