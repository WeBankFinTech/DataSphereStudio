<template>
  <div class="page-process">
    <div
      class="process-tabs">
      <div
        class="process-tab">
        <div
          v-for="(item, index) in tabs"
          :key="index"
          class="process-tab-item"
          :class="{active: index===active}"
          @click="choose(index)"
          @mouseenter.self="item.isHover = true"
          @mouseleave.self="item.isHover = false">
          <div
          >
            <img class="tab-icon" :class="nodeImg[item.node.type].class" :src="nodeImg[item.node.type].icon" alt="">
            <div
              :title="item.title"
              class="process-tab-name">{{ item.title }}</div>
            <span
              v-show="!item.isHover && item.node && item.node.isChange"
              class="process-tab-unsave-icon fi-radio-on2"/>
            <Icon
              v-if="item.isHover && item.close"
              type="md-close"
              @click.stop="remove(index)"/>
          </div>
        </div>
      </div>
      <div class="process-container">
        <template v-for="(item, index) in tabs">
          <Process
            ref="process"
            v-if="item.type === 'Process'"
            v-show="index===active"
            :key="index"
            :import-replace="false"
            :flow-id="item.data.flowId"
            :version="item.data.version"
            :project-version-i-d="query.projectVersionID"
            :readonly="item.data.readonly"
            :tabs="tabs"
            :open-files="openFiles"
            @node-dblclick="dblclickNode(index, arguments)"
            @isChange="isChange(index, arguments)"
            @save-node="saveNode"
            @check-opened="checkOpened"
            @deleteNode="deleteNode"
            @saveBaseInfo="saveBaseInfo"
            @updateWorkflowList="$emit('updateWorkflowList')">
          </Process>
          <Ide
            v-if="item.type === 'IDE'"
            v-show="index===active"
            :key="index"
            :parameters="item.data"
            :node="item.node"
            :in-flows-index="index"
            @save="saveIDE(index, arguments)"></Ide>
          <commonIframe
            v-if="item.type === 'Iframe'"
            v-show="index===active"
            :key="index"
            :parametes="item.data"
            :node="item.node"
            @save="saveNode"></commonIframe>
          <div
            v-if="item.type === 'DiaoDu'"
            v-show="index===active"
            :key="index"
            style="width:100%; height:100%">
          </div>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty, isArguments } from 'lodash';
import api from '@/js/service/api';
import util from '@/js/util';
import Process from './module.vue';
import Ide from '@js/module/ide';
import commonModule from '@js/module/common';
import { NODETYPE, NODEICON } from '@/js/service/nodeType'
export default {
  components: {
    Process,
    Ide: Ide.component,
    commonIframe: commonModule.component.iframe,
  },
  props: {
    query: {
      type: Object,
      default: () => {},
    }
  },
  data() {
    return {
      tabs: [{
        title: this.$t('message.workflow.workflow'),
        type: 'Process',
        close: false,
        data: this.query,
        node: {
          isChange: false,
          type: 'workflow.subflow'
        },
        key: '工作流',
        isHover: false,
      }],
      active: 0,
      openFiles: {},
      nodeImg: NODEICON
    };
  },
  mounted() {
    this.getCache().then((tabs) => {
      if (tabs) {
        this.tabs = tabs;
      }
    });
    this.updateProjectCacheByActive();
  },
  methods: {
    gotoAction(back = -1) {
      if (back) {
        this.$router.go(back);
      }
    },
    choose(index) {
      this.active = index;
      this.updateProjectCacheByActive();
    },
    remove(index) {
      // 删掉子工作流得删掉当前打开的子节点
      const currentTab = this.tabs[index];
      // 找到当前关闭项对应的子类
      const subArray = this.openFiles[currentTab.key] || [];
      const cahngeList = this.tabs.filter((item) => {
        return subArray.includes(item.key) && item.node.isChange;
      });
      if (cahngeList.length > 0 && currentTab.node.type === NODETYPE.FLOW) {
        let text = '<p>该工作流下还存在子节点未保存，是否关闭？</p>';
        if (currentTab.node.isChange) {
          text = '<p>该工作流未保存并且还存在子节点未保存，是否关闭？</p>';
        }
        this.$Modal.confirm({
          title: '关闭提示',
          content: text,
          okText: '确认关闭',
          cancelText: '取消',
          onOk: () => {
            this.tabs.splice(index, 1);
            this.choose(this.tabs.length - 1);
            this.updateProjectCacheByTab();
          },
          onCancel: () => {
          },
        });
      } else {
        this.tabs.splice(index, 1);
        this.choose(this.tabs.length - 1);
        this.updateProjectCacheByTab();
      }
    },
    check(node) {
      if (node) {
        let boolean = true;
        this.tabs.map((item) => {
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
          this.$Message.warning('您已打开10个节点（超出最大限额），请关闭其他节点再打开！');
        }
        return boolean;
      } else {
        if (this.tabs.length > 10) {
          this.$Message.warning('您已打开10个节点（超出最大限额），请关闭其他节点再打开！');
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
        if (node.type == NODETYPE.SHELL) {
          if (this.tabs[i].node && this.tabs[i].node.createTime === node.createTime && this.tabs[i].node.key === node.key) {
            return this.choose(i);
          }
        } else {
          if (this.tabs[i].key === node.key) {
            return this.choose(i);
          }
        }
      }
      if ([NODETYPE.SPARKSQL, NODETYPE.HQL, NODETYPE.SPARKPY, NODETYPE.SHELL, NODETYPE.SCALA].indexOf(node.type) !== -1) {
        const len = node.resources ? node.resources.length : 0;
        if (len && node.jobContent && node.jobContent.script) { // 判断是否有保存过脚本
          const resourceId = node.resources[0].resourceId;
          const fileName = node.resources[0].fileName;
          const version = node.resources[0].version;
          api.fetch('/filesystem/openScriptFromBML', {
            fileName,
            resourceId,
            version,
          }, 'get').then((res) => {
            let content = res.scriptContent;
            let params = {};
            params.variable = this.convertSettingParamsVariable(res.metadata);
            params.configuration = (!node.params || isEmpty(node.params.configuration)) ? {
              special: {},
              runtime: {},
              startup: {},
            } : {
              special: node.params.configuration.special || {},
              runtime: node.params.configuration.runtime || {},
              startup: node.params.configuration.startup || {},
            };
            this.getTabsAndChoose({
              type: 'IDE',
              node,
              data: {
                content,
                params,
              },
            });
          });
        } else {
          // 如果节点是导入进来的，可能存在脚本内容
          let content = node.jobContent && node.jobContent.code ? node.jobContent.code : '';
          let params = {};
          params.variable = this.convertSettingParamsVariable({});
          params.configuration = (!node.params || isEmpty(node.params.configuration)) ? {
            special: {},
            runtime: {},
            startup: {},
          } : {
            special: node.params.configuration.special || {},
            runtime: node.params.configuration.runtime || {},
            startup: node.params.configuration.startup || {},
          };
          this.getTabsAndChoose({
            type: 'IDE',
            node,
            data: {
              content,
              params,
            },
          });
        }
      }
      if (node.type == NODETYPE.FLOW) {
        // 子流程必须已保存, 才可以被打开
        let flowId = node.jobContent ? node.jobContent.embeddedFlowId : '';
        this.getTabsAndChoose({
          type: 'Process',
          node,
          data: {
            flowId,
          },
        });
      }
      // iframe打开的节点
      if ([NODETYPE.DISPLAY, NODETYPE.DASHBOARD, NODETYPE.QUALITIS].includes(node.type)) {
        let id = node.jobContent ? node.jobContent.id : '';
        this.getTabsAndChoose({
          type: 'Iframe',
          node,
          data: {
            id,
          },
        });
      }
    },
    getTabsAndChoose({ type, node, data }) {
      this.$set(node, 'isChange', false);
      this.tabs.push({
        type,
        key: node.key,
        title: node.title,
        close: true,
        // 把节点的引用放到这里
        node,
        data,
        isHover: false,
      });
      // 记录打开的tab的依赖关系
      this.openFileAction(node);
      this.choose(this.tabs.length - 1);
      this.updateProjectCacheByTab();
    },
    openFileAction(node) {
      // 判断当前打开的节点的父工作过流是否已经有打开的节点s
      const currnentTab = this.tabs[this.active];
      if (Object.keys(this.openFiles).includes(currnentTab.key)) {
        Object.keys(this.openFiles).map((key) => {
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
      // 取到节点
      let node = this.tabs[index].node;
      this.saveNode(args, node);
    },
    saveNode(args, node) {
      let resource = args;
      let currentNode = node;
      if (isArguments(args)) {
        resource = args[0];
        currentNode = args[1];
      }
      if (!node.jobContent) {
        node.jobContent = {};
      }
      if (node.createTime !== currentNode.createTime && node.key !== currentNode.key) return;
      node.jobContent.script = resource.fileName;
      delete node.jobContent.code; // code提交生成script之后删除code
      if (!node.resources) {
        node.resources = [];
      }
      if (node.resources.length > 0 && node.resources[0].fileName === resource.fileName) { // 已报保存过的直接替换，没有保存的首项追加
        node.resources[0] = resource;
      } else {
        node.resources.unshift(resource);
      }
      this.$refs.process.map((item) => {
        item.json.nodes.forEach((item) => {
          if (item.createTime === currentNode.createTime && item.key === currentNode.key) {
            item.jobContent = node.jobContent;
            item.resources = node.resources;
            item.params = node.params;
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
        title: '返回提醒',
        content: `<p>该工作流已发生变化还未保存, 是否保存？</p>`,
        okText: '保存',
        cancelText: '不保存',
        onOk: cb,
        onCancel: cancel,
      });
    },
    isChange(index, val) {
      if (this.tabs[index].node) {
        this.tabs[index].node.isChange = val[0];
      }
    },
    beforeLeaveHook() {
    },
    checkOpened(node, cb) {
      const isOpened = this.tabs.find((item) => item.key === node.key);
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
      this.tabs = this.tabs.map((item) => {
        if (item.key === node.key) {
          item.title = node.title;
        }
        return item;
      });
    },
    updateProjectCacheByTab() {
      this.dispatch('IndexedDB:updateProjectCache', {
        projectID: this.$route.query.projectID,
        key: 'tabList',
        value: {
          tab: this.tabs,
          token: 'flowId',
          sKey: 'tab',
          sValue: this.query.flowId,
        },
        isDeep: true,
      })
    },
    updateProjectCacheByActive() {
      this.dispatch('IndexedDB:updateProjectCache', {
        projectID: this.$route.query.projectID,
        key: 'tabList',
        value: {
          active: this.active,
          token: 'flowId',
          sKey: 'active',
          sValue: this.query.flowId,
        },
        isDeep: true,
      })
    },
    getCache() {
      return new Promise((resolve) => {
        this.dispatch('IndexedDB:getProjectCache', {
          projectID: this.$route.query.projectID,
          cb: (cache) => {
            const list = cache && cache.tabList || [];
            let tabs = null;
            list.forEach((item) => {
              if (+item.flowId === +this.query.flowId) {
                tabs = item.tab;
                this.active = item.active || 0;
              }
            })
            resolve(tabs);
          }
        })
      })
    },
  },
};
</script>
<style lang="scss" src="@assets/styles/process.scss"></style>
