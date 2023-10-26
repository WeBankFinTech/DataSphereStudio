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
              :src="item.node.image || defaultNodeIcon"
              alt
            />
            <div :title="item.title" class="process-tab-name">{{ item.title }}</div>
            <SvgIcon v-show="!item.isHover && item.node && item.node.isChange && checkEditable(query)"
              class="process-tab-unsave-icon"
              :style="{ color: item.node && item.node.isChange ? '#ed4014' : '' }"
              icon-class="fi-radio-on2"/>
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
            :active-tab-key="item.key"
            :import-replace="false"
            :flow-id="item.data.appId"
            :version="item.data.version"
            :product="query.product"
            :readonly="!checkEditable(query)"
            :publish="query.releasable"
            :isLatest="query.isLatest === 'true'"
            :tabs="tabs"
            :open-files="openFiles"
            :orchestratorId="item.data.orchestratorId"
            :orchestratorVersionId="item.data.orchestratorVersionId"
            @node-dblclick="dblclickNode(index, arguments)"
            @isChange="isChange(index, arguments)"
            @save-node="saveNode"
            @check-opened="checkOpened"
            @deleteNode="deleteNode"
            @saveBaseInfo="saveBaseInfo"
            @updateWorkflowList="$emit('updateWorkflowList')"
            @release="release"
            @open="$emit('open')"
            @close="$emit('close')"
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
            :query="query"
            @save="saveNode"
          ></commonIframe>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty, isArguments } from "lodash";
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import Process from "./module.vue";
import Ide from '@/workflows/module/ide';
import commonModule from '@/workflows/module/common';
import { NODETYPE } from '@/workflows/service/nodeType';
import defaultNodeIcon from './images/flow.svg';
export default {
  components: {
    Process,
    Ide: Ide.component,
    commonIframe: commonModule.component.iframe
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
      defaultNodeIcon,
      tabs: [
        {
          title: this.$t("message.workflow.process.index.BJMS"),
          type: "Process",
          close: false,
          data: this.query,
          node: {
            image: defaultNodeIcon,
            isChange: false,
            type: "workflow.subflow"
          },
          key: '工作流',
          isHover: false
        }
      ],
      active: 0,
      setIntervalID: "",
      setTime: 40,
      showTip: true,
      openFiles: {}
    }
  },
  mounted() {
    this.changeTitle();
  },
  methods: {
    release(obj) {
      this.$emit('release', obj)
    },
    // 判断是否有意编辑权限
    // 没有权限的和历史的都不可编辑
    checkEditable(item) {
      if (item.editable && this.query.readonly !== 'true') {
        return true
      } else {
        return false
      }
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
    },
    remove(index) {
      // 删掉子工作流得删掉当前打开的子节点
      const currentTab = this.tabs[index];
      // 找到当前关闭项对应的子类
      const subArray = this.openFiles[currentTab.key] || [];
      const changeList = this.tabs.filter(item => {
        return subArray.includes(item.key) && item.node.isChange;
      });
      // 子工作流关闭时，查询是否有子节点没有保存，是否一起关闭
      if (changeList.length > 0 || (currentTab.node &&currentTab.node.isChange)) {
        let text = `<p>${this.$t("message.workflow.process.index.WBCSFGB")}</p>`;
        this.$Modal.confirm({
          title: this.$t("message.workflow.process.index.GBTS"),
          content: text,
          okText: this.$t("message.workflow.process.index.QRGB"),
          cancelText: this.$t("message.workflow.process.index.QX"),
          onOk: () => {
            let active = this.active
            if (this.active === index) {
              // 删除的就是当前打开的
              this.tabs.splice(index, 1);
              active = this.tabs.length - 1;
            } else {
              this.tabs.splice(index, 1);
              active = index < active ? active - 1 : active
            }
            this.choose(active);
          },
          onCancel: () => {}
        });
      } else {
        let active = this.active
        if (this.active === index) {
          // 删除的就是当前打开的
          this.tabs.splice(index, 1);
          active = this.tabs.length - 1;
        } else {
          this.tabs.splice(index, 1);
          active = index < active ? active - 1 : active
        }
        this.choose(active);
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
      // 如果节点已打开，则选择
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === node.key) return this.choose(i);
      }
      // 目前的scriptis内部节点的supportJump为true, jumpType: 2,且不需要创建弹窗
      if (node.supportJump && !node.shouldCreationBeforeNode && node.jumpType == 2) {
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
          // 如果节点是导入进来的，可能存在脚本内容
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
      // iframe打开的节点
      if (node.supportJump && node.jumpType == 1) {
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
      // 记录打开的tab的依赖关系
      this.openFileAction(node);
      this.choose(this.tabs.length - 1);
    },
    openFileAction(node) {
      // 判断当前打开的节点的父工作过流是否已经有打开的节点s
      const currnentTab = this.tabs[this.active];
      if (Object.keys(this.openFiles).includes(currnentTab.key)) {
        Object.keys(this.openFiles).map(key => {
          // 找到同一父节点下是否曾今已经打开过
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
      // scriptisSave用来判断是否是脚本保存的触发和关联触发
      // 这个地方注意：在关联脚本、scriptis保存脚本、qualitis保存都会调用，参数不一样，关联脚本args是对象，scriptis保存是arguments, qualitis保存过来的是空对象，所以要处理下
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
      // qualitis 过来是没有值的, 空对象传给后台会报错
      if (Object.keys(resource).length > 0) {
        if (
          node.resources.length > 0 &&
          node.resources[0].resourceId === resource.resourceId
        ) {
          // 已保存过的直接替换，没有保存的首项追加
          node.resources[0] = resource;
        } else {
          node.resources.unshift(resource);
        }
      }
      this.$refs.process.forEach(item => {
        item.json.nodes.forEach(subitem => {
          if (subitem.key === currentNode.key) {
            // 在这里直接改originalData值，组件里并没有相应，所以改为触发组件事件
            item.updateOriginData(node, scriptisSave);
          }
        });
      });
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
    changeTitle() {
      if (this.query.readonly === "true") {
        this.tabs[0].title = this.$t("message.workflow.process.index.ZDMS");
      } else {
        this.tabs[0].title = this.$t("message.workflow.process.index.BJMS");
      }
    }
  }
};
</script>
<style lang="scss" src="../../assets/styles/process.scss"></style>
