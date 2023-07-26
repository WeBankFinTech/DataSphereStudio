<template>
  <div ref="processModule" class="process-module" :class="{'is-publishing': isFlowPubulish, 'locked': locked}">
    <vueProcess
      ref="process"
      :shapes="shapes"
      :value="originalData"
      :ctx-menu-options="nodeMenuOptions"
      :view-options="viewOptions"
      :disabled="workflowIsExecutor || myReadonly"
      @toggle-shape="toggleShape"
      @change="change"
      @message="message"
      @node-click="click"
      @node-dblclick="dblclick"
      @node-baseInfo="saveNodeBaseInfo"
      @node-param="saveNodeParam"
      @node-delete="nodeDelete"
      @add="addNode"
      @on-ctx-menu="onContextMenu"
      @link-delete="linkDelete"
      @link-add="linkAdd">
      <template >
        <!-- 这里需要做控制，只读和发布的区别-->
        <div>
          <div v-if="!myReadonly">
            <div
              class="button"
              :title="$t('message.workflow.process.params')"
              ref="paramButton"
              @click.stop="showParamView">
              <SvgIcon class="icon" icon-class="canshu" style="opacity: 0.65"/>
              <span>{{$t('message.workflow.process.params')}}</span>
            </div>
            <div class="devider" />
            <div
              class="button"
              ref="resourceButton"
              :title="$t('message.workflow.process.resource')"
              @click.stop="showResourceView">
              <SvgIcon class="icon" icon-class="ziyuan" style="opacity: 0.65"/>
              <span>{{$t('message.workflow.process.resource')}}</span>
            </div>
            <div class="devider" />
            <div
              v-if="!workflowIsExecutor"
              :title="$t('message.workflow.process.run')"
              class="button"
              @click="clickswitch">
              <SvgIcon class="icon" icon-class="play-2" color="#666"/>
              <span>{{$t('message.workflow.process.run')}}</span>
            </div>
            <div class="devider" v-if="!workflowIsExecutor" />
            <div
              v-if="!workflowIsExecutor"
              :title="$t('message.workflow.process.run')"
              class="button"
              @click="clickswitch('select')">
              <SvgIcon class="icon" icon-class="play-2" color="#666"/>
              <span>{{ $t('message.workflow.SelectRun') }}</span>
            </div>
            <div
              v-if="workflowIsExecutor"
              :title="$t('message.workflow.process.stop')"
              class="button"
              @click="clickswitch">
              <SvgIcon class="icon" className='stop' icon-class="stop-2" color="#666"/>
              <span>{{$t('message.workflow.process.stop')}}</span>
            </div>
            <div class="devider" v-if="needReRun" />
            <div
              v-if='!workflowIsExecutor && needReRun'
              class="button"
              @click="reRun">
              <Icon class="icon" type="ios-refresh" color="#666" size="18"/>
              <span>{{ $t('message.workflow.Rerun') }}</span>
            </div>
            <div class="devider" />
            <div
              :title="$t('message.workflow.process.save')"
              class="button"
              @click="handleSave">
              <SvgIcon class="icon" icon-class="baocun" style="opacity: 0.65"/>
              <span>{{$t('message.workflow.process.save')}}</span>
            </div>
            <div v-if="type==='flow'" class="devider" />
          </div>
          <!-- 预留运维发布的区别-->
          <div v-if="publish">
            <div
              v-if="type==='flow'"
              :title="$t('message.workflow.process.publish')"
              class="button"
              @click="workflowPublishIsShow">
              <template v-if="!isFlowPubulish">
                <SvgIcon class="icon" icon-class="fabu" style="opacity: 0.65"/>
                <span>{{$t('message.workflow.process.publish')}}</span>
              </template>
              <Spin v-else class="public_loading">
                <Icon type="ios-loading" size=18 class="public-splin-load"></Icon>
                <span>{{$t('message.workflow.publishing')}}</span>
              </Spin>
            </div>
            <div v-for="toolItem in extraToolbar" :key="toolItem.name">
              <div class="devider"></div>
              <div
                class="button"
                @click.stop="clickToolItem(toolItem)">
                <SvgIcon class="icon" icon-class="ds-center" />
                <span>{{$t('message.workflow.process.gotoScheduleCenter')}}</span>
              </div>
            </div>
          </div>
        </div>
      </template>
      <!-- 生产中心保留执行和地图模式 -->
      <template v-if="myReadonly">
        <template v-if="product && isLatest">
          <div
            v-if="!workflowIsExecutor"
            :title="$t('message.workflow.process.run')"
            class="button"
            @click="clickswitch">
            <SvgIcon class="icon" icon-class="play-2" />
            <span>{{$t('message.workflow.process.run')}}</span>
          </div>
          <div
            v-if="workflowIsExecutor"
            :title="$t('message.workflow.process.stop')"
            class="button"
            @click="clickswitch">
            <SvgIcon class="icon" className='stop' icon-class="stop-2" />
            <span>{{$t('message.workflow.process.stop')}}</span>
          </div>
        </template>
      </template>
    </vueProcess>
    <div
      class="process-module-param"
      v-clickoutside="handleOutsideClick"
      v-show="isParamModalShow">
      <div class="process-module-param-modal-header">
        <h5>{{ isResourceShow ? $t('message.workflow.process.resource') : isDispatch ? $t('message.workflow.process.schedule') : $t('message.workflow.process.params') }}{{$t('message.workflow.process.seting')}}</h5>
      </div>
      <div class="process-module-param-modal-content">
        <argument
          v-show="!isResourceShow"
          :props="props"
          :isDispatch="isDispatch"
          :scheduleParamsProp="scheduleParams"
          @change-schedule="onScheduleChange"
          @change-props="onPropsChange"></argument>
        <resource
          v-show="isResourceShow"
          :resources="resources"
          :flow-name="name"
          @update-resources="updateResources"></resource>
      </div>
    </div>
    <div
      class="process-module-param"
      v-show="nodebaseinfoShow"
      @click="clickBaseInfo">
      <div class="process-module-param-modal-header">
        <h5>{{$t('message.workflow.process.baseInfo')}}</h5>
        <div class="save-button">
          <Button  v-if="!myReadonly" size="small" @click.stop="saveNodeParameter"
            :disabled="false">{{$t('message.workflow.process.nodeParameter.BC')}}
          </Button>
        </div>
        <Icon class="close-icon" type="md-close" size="16" @click.stop="closeParamsBar"></Icon>
      </div>
      <div class="process-module-param-modal-content">
        <nodeParameter
          ref="nodeParameter"
          :node-data="clickCurrentNode"
          :name="name"
          :isShowSaveButton="false"
          :readonly="myReadonly"
          :nodes="json && json.nodes"
          :consoleParams="consoleParams"
          @saveNode="saveNode"
        ></nodeParameter>
      </div>
    </div>
    <Modal
      width="450"
      v-model="saveModal">
      <div
        class="process-module-title"
        slot="header">
        {{$t('message.workflow.process.save')}}
      </div>
      <Form
        ref="formSave"
        :model="saveModel"
        :label-width="85" >
        <FormItem
          :label="$t('message.workflow.comment')"
          prop="comment"
          :rules="[{ required: true, message: $t('message.workflow.process.inputComment') },{message: $t('message.workflow.process.commentLengthLimit'), max: 255}]">
          <Input
            v-model="saveModel.comment"
            type="textarea"
            :placeholder="$t('message.workflow.process.inputComment')"
            style="width: 300px;" />
        </FormItem>
      </Form>
      <div slot="footer">
        <Button
          type="primary"
          @click="handleSave">{{$t('message.workflow.process.confirmSave')}}</Button>
      </div>
    </Modal>
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <Modal
      v-model="repetitionNameShow"
      :title="$t('message.workflow.process.nodeNameNotice')"
      class="repetition-name">
      {{$t('message.workflow.process.repeatNode')}}{{ repeatTitles.join(', ') }}
      <div slot="footer">
        <Button
          type="primary"
          @click="repetitionName">{{$t('message.workflow.ok')}}</Button>
      </div>
    </Modal>
    <associate-script
      ref="associateScript"
      @click="associateScript"/>
    <!-- 创建节点弹窗 -->
    <Modal
      :title="addNodeTitle"
      v-model="addNodeShow"
      :closable="false"
      :mask-closable="false"
    >
      <Form
        v-if="createNodeParamsList.length > 0"
        label-position="left"
        :label-width="130"
        ref="addFlowfoForm"
        :model="clickCurrentNode"
        :rules="formRules">
        <template v-for="item in createNodeParamsList">
          <FormItem v-if="['Input', 'Text', 'Disable'].includes(item.uiType)" :key="item.key" :label="item.lableName" :prop="item.key" >
            <Input v-model="clickCurrentNode[item.key]" :type="filterFormType(item.uiType)"
              :placeholder="item.desc" :disabled="item.uiType === 'Disable'"
            />
          </FormItem>
          <FormItem v-if="item.uiType === 'Select'" :key="item.key" :label="item.lableName" :prop="item.key" >
            <Select
              v-model="clickCurrentNode[item.key]"
              :placeholder="item.desc">
              <Option v-for="subItem in JSON.parse(item.value)" :value="subItem" :key="subItem">{{subItem}}</Option>
            </Select>
          </FormItem>
          <FormItem v-if="item.uiType === 'Binding'" :key="item.key" :label="item.lableName" :prop="item.key" >
            <Select
              v-model="clickCurrentNode[item.key]"
              :placeholder="item.desc">
              <Option v-for="subItem in conditionBindList(item)" :value="subItem.key" :key="subItem.key">{{subItem.name}}</Option>
            </Select>
          </FormItem>
        </template>
      </Form>
      <div slot="footer">
        <Button
          type="text"
          size="large"
          @click="addFlowCancel">{{$t('message.workflow.cancel')}}</Button>
        <Button
          type="primary"
          size="large"
          @click="addFlowOk">{{$t('message.workflow.ok')}}</Button>
      </div>
    </Modal>
    <!-- 发布弹窗 -->
    <Modal
      :title="$t('message.workflow.publishingWorkflow')"
      v-model="pubulishShow">
      <Form
        :label-width="100"
        label-position="left">
        <FormItem
          :label="$t('message.workflow.desc')">
          <Input
            :rows="4"
            type="textarea"
            v-model="pubulishFlowComment"
            :placeholder="$root.$t('message.workflow.publish.inputDesc')"></Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button
          v-if="$APP_CONF && $APP_CONF.showVersionDiff !== false"
          @click="showDiff">{{$t('message.workflow.showVersionDiff')}}</Button>
        <Button
          type="primary"
          :loading="saveingComment"
          :disabled="saveingComment"
          @click="workflowPublish">{{$t('message.workflow.ok')}}</Button>
      </div>
    </Modal>
    <!-- 导出弹窗 -->
    <Modal
      v-model="workflowExportShow"
      :title="$t('message.workflow.exportWorkflow')"
      @on-ok="workflowExportOk">
      <Form
        :label-width="100"
        label-position="left"
        ref="exportForm"
      >
        <FormItem :label="$t('message.workflow.desc')" porp="desc">
          <Input
            v-model="exportDesc"
            type="textarea"
            :placeholder="$t('message.workflow.inputWorkflowDesc')"></Input>
        </FormItem>
        <FormItem porp="changeVersion">
          <Checkbox v-model="exporTChangeVersion">{{$t('message.workflow.synchronousPublishing')}}</Checkbox>
        </FormItem>
      </Form>
    </Modal>
    <!-- 运行控制台 -->
    <console
      v-if="openningNode"
      ref="currentConsole"
      :node="openningNode"
      :stop="workflowIsExecutor"
      class="process-console"
      :height="consoleHeight"
      :style="getConsoleStyle"
      @close-console="closeConsole"></console>
    <BottomTab
      ref="bottomTab"
      :orchestratorId="orchestratorId"
      :orchestratorVersionId="orchestratorVersionId"
      :flowId="flowId"
      :product="product"
      :readonly="readonly"
      @release="release"
    />
  </div>
</template>
<script>
import argument from './component/arguments.vue';
import resource from './component/resource.vue';
import nodeParameter from './component/nodeparameter.vue';
import vueProcess from '@dataspherestudio/shared/components/vue-process';
import console from './component/console.vue';
import api from '@dataspherestudio/shared/common/service/api';
import clickoutside from '@dataspherestudio/shared/common/helper/clickoutside';
import associateScript from './component/associateScript.vue';
import { throttle, debounce } from 'lodash';
import { NODETYPE, ext } from '@/workflows/service/nodeType';
import storage from '@dataspherestudio/shared/common/helper/storage';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import util from '@dataspherestudio/shared/common/util';
import eventbus from "@dataspherestudio/shared/common/helper/eventbus";
import moment from 'moment';
import { getPublishStatus } from '@/workflows/service/api.js';
import module from './index';
import nodeIcons from './nodeicon';
import BottomTab from './component/bottomTab.vue'

export default {
  components: {
    vueProcess,
    argument,
    resource,
    nodeParameter,
    associateScript,
    console,
    BottomTab
  },
  mixins: [mixin],
  directives: {
    clickoutside,
  },
  props: {
    workspaceId: {
      type: [String, Number],
      default: ''
    },
    flowId: {
      type: [String, Number],
      default: '',
    },
    version: {
      type: [String, Number],
      default: '',
    },
    readonly: {
      type: [String, Boolean],
      default: false,
    },
    publish: {
      type: Boolean,
      default: false
    },
    product: {
      type: [Boolean],
      default: false,
    },
    importReplace: {
      type: Boolean,
      default: false,
    },
    openFiles: {
      type: Object,
      default: () => {},
    },
    tabs: {
      type: Array,
      default: () => [],
    },
    activeTabKey: {
      type: String
    },
    isLatest: {
      type: Boolean,
      default: true
    },
    orchestratorId: {
      type: [Number, String],
      default: null
    },
    orchestratorVersionId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    const username = this.getUserName()
    return {
      // 发布前保存
      saveingComment: false,
      // 是否为父工作流
      isRootFlow: true,
      name: '',
      shapes: [],
      // 原始数据
      originalData: null,
      // 插件返回的json数据
      json: null,
      // 工作流级别的参数
      props: [
        {'user.to.proxy': username}
      ],
      // 调度设置参数
      scheduleParams: {},
      // 工作流级别的资源
      resources: [],
      // 是否显示保存模态框
      saveModal: false,
      saveModel: {
        comment: '',
      },
      // 控制参数模态框是否显示
      isParamModalShow: false,
      isResourceShow: false,
      // 是否有改变
      jsonChange: false,
      loading: false,
      repetitionNameShow: false,
      repeatTitles: [],
      nodebaseinfoShow: false, // 自定义节点信息弹窗展示
      clickCurrentNode: {}, // 当前点击的节点
      viewOptions: {
        showBaseInfoOnAdd: false, // 不显示默认的拖拽添加节点弹出的基础信息面板
        shapeView: true, // 左侧shape列表
        control: true,
      },
      addNodeShow: false, // 创建节点的弹窗显示
      cacheNode: null,
      addNodeTitle: this.$t('message.workflow.process.createSubFlow'), // 创建节点时弹窗的title
      workflowIsExecutor: false, // 当前工作流是否再执行
      openningNode: null, // 上一次打开控制台的节点
      shapeWidth: 0, // 流程图插件左侧工具栏的宽度
      workflowExeteId: '',
      workflowTaskId: '',
      excuteTimer: '',
      executorStatusTimer: '',
      workflowExecutorCache: [],
      isDispatch: false,
      contextID: '',
      pubulishFlowComment: '',
      pubulishShow: false,
      flowVersion: '',
      isFlowPubulish: false,
      workflowExportShow: false,
      exportDesc: '',
      exporTChangeVersion: false,
      changeNum: 0,
      consoleParams: [],
      consoleHeight: 250,
      needReRun: false,
      locked: false,
      extraToolbar: [],
    };
  },
  computed: {
    getConsoleStyle() {
      return {
        'left': this.shapeWidth + 'px',
        'width': `calc(100% - ${this.shapeWidth}px)`
      }
    },
    // 获取新建节点时需要的参数列表
    createNodeParamsList() {
      return this.clickCurrentNode.nodeUiVOS ? this.clickCurrentNode.nodeUiVOS.filter((item) => item.baseInfo) : [];
    },
    formRules() {
      let rules = {};
      this.createNodeParamsList.map((item) => {
        rules[item.key] = this.paramsValid(item);
      })
      return rules;
    },
    showDispatchHistoryButton() {
      return this.$route.query.notPublish === 'true' || this.$route.query.notPublish === true
    },
    myReadonly() {
      return JSON.parse(this.readonly);
    },
    type() {
      return !this.isRootFlow ? 'subFlow' : 'flow'; // flow工作流， subFlow子工作流
    },
    nodeMenuOptions() {
      return {
        defaultMenu: {
          config: false, // 不展示默认的基础信息菜单项
          param: false, // 不展示默认的参数配置菜单项
          copy: false,
          delete: !this.workflowIsExecutor && !this.myReadonly
        },
        userMenu: [],
        beforeShowMenu: (node, arr, type) => {
          if (this.myReadonly) arr = []
          // type : 'node' | 'link' | 'view' 分别是节点右键，边右键，画布右键
          // 如果有runState说明已经执行过
          if (node && node.runState) {
            if (node.runState.showConsole && node.runState.taskID) {
              arr.push({
                text: this.$t('message.workflow.process.console'),
                value: 'console',
                icon: 'xitongguanlitai'
              })
            }
          }
          if (!this.workflowIsExecutor&& !this.myReadonly) {
            if (type === 'node') {
              if ([NODETYPE.SPARKSQL, NODETYPE.HQL, NODETYPE.SPARKPY, NODETYPE.SCALA, NODETYPE.PYTHON].includes(node.type)) {
                arr.push({
                  text: this.$t('message.workflow.process.associate'),
                  value: 'associate',
                  icon: 'associate', // 图标资源文件，也可以通过icon配置内置字体文件支持的className
                });
              }
              arr.push({
                text: this.$t('message.workflow.process.relySelect'),
                value: 'relySelect',
                icon: 'depselect',
                children: [{
                  text: this.$t('message.workflow.process.upstreamLevelOne'),
                  value: 'relySelectUpOne',
                  icon: 'depselect',
                }, {
                  text: this.$t('message.workflow.process.downstreamLevelOne'),
                  value: 'relySelectDownOne',
                  icon: 'depselect',
                }, {
                  text: this.$t('message.workflow.process.upAllLevel'),
                  value: 'relySelectUp',
                  icon: 'depselect',
                }, {
                  text: this.$t('message.workflow.process.downAllLevel'),
                  value: 'relySelectDown',
                  icon: 'depselect',
                }
                ]
              });
              // 通过节点类型去判断是否支持复制
              if (this.nodeCopy(node)) {
                arr.push({
                  text: this.$t('message.workflow.copy'),
                  value: 'mycopy',
                  icon: 'fuzhi',
                });
              }
            }
          }
          if (type === 'view'&& !this.myReadonly) {
            arr.push({
              text: this.$t('message.workflow.paste'),
              value: 'mypaste',
              icon: 'zhantie',
            });
            arr.push({
              text: this.$t('message.workflow.process.allDelete'),
              value: 'allDelete',
              icon: 'delete'
            });
          }
          return arr;
        }
      }
    }
  },
  created() {
    this.viewOptions.shapeView = !this.myReadonly;
  },
  watch: {
    jsonChange(val) {
      this.$emit('isChange', val);
    },
    workflowExecutorCache() {
      storage.set("workflowExecutorCache", this.workflowExecutorCache, 'local');
    }
  },
  mounted() {
    this.workflowExecutorCache = storage.get('workflowExecutorCache', 'local') || [];
    // 查找缓存中是否有当前工作流
    const currentExecutorFlow = this.workflowExecutorCache.filter((item) => item.flowId === this.flowId)
    if (currentExecutorFlow.length > 0) {
      this.workflowIsExecutor = true;
      this.queryWorkflowExecutor(currentExecutorFlow[0].execID, currentExecutorFlow[0].taskID)
      this.workflowExeteId = currentExecutorFlow[0].execID
      this.workflowTaskId = currentExecutorFlow[0].taskID
    }
    if (!this.myReadonly) {
      this.getToolbarsConfig()
    }
    // 基础信息
    this.setShapes().then(() => {
      this.getBaseInfo();
    });
    this.shapeWidth = this.$refs.process && this.$refs.process.state.shapeOptions.viewWidth; // 自适应控制台宽度
    this.getConsoleParams();
    document.addEventListener('keyup', this.onKeyUp)
    this.consoleHeight = this.$el ? this.$el.clientHeight / 2 : 250
    eventbus.on('workflow.fold.left.tree', this.foldHandler);
    eventbus.on('workflow.copying', this.onCopying);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    if (this.excuteTimer) {
      clearTimeout(this.excuteTimer);
    }
    if (this.executorStatusTimer) {
      clearTimeout(this.executorStatusTimer);
    }
    if (this.updateLockTimer) {
      clearTimeout(this.updateLockTimer)
    }
    eventbus.off('workflow.fold.left.tree', this.foldHandler);
    eventbus.off('workflow.copying', this.onCopying);
    document.removeEventListener('keyup', this.onKeyUp)
  },
  methods: {
    foldHandler() {
      const refs = this.$refs
      refs.process && refs.process.layoutView()
    },
    onCopying(data) {
      if (data.source.orchestratorId == this.orchestratorId) {
        this.locked = !data.done
      }
    },
    eventFromExt(evt) {
      if (evt && evt.callFn && typeof this[evt.callFn] === 'function') {
        this[evt.callFn](...evt.params)
      }
    },
    getToolbarsConfig() {
      api.fetch(`/dss/workflow/getExtraToolBars`, {
        projectId: +this.$route.query.projectID,
        workflowId: this.flowId,
        labels: {route: this.getCurrentDsslabels()},
      },'post').then(res => {
        this.extraToolbar = res.extraBars
      })
    },
    release(obj) {
      this.$emit('release', obj);
    },
    // 保存node参数修改
    saveNodeParameter() {
      this.$refs.nodeParameter.save();
    },
    // 右键判断是否支持复制
    nodeCopy(node) {
      let flag = false;
      this.shapes.forEach((item) => {
        if (item.children) {
          item.children.forEach((subItem) => {
            if (subItem.type === node.type) {
              flag = subItem.enableCopy;
              return;
            }
          })
        }
      })
      return flag;
    },
    // 各参数的校验方法
    paramsValid(param) {
      // 自定义函数的方法先写这里
      const validatorTitle = (rule, value, callback) => {
        if (value === `${this.name}`) {
          callback(new Error(rule.message));
        } else {
          callback();
        }
      }
      let temRule = [];
      if (param.nodeUiValidateVOS) {
        param.nodeUiValidateVOS.map((item) => {
          // 如果是正则类型的就写成正则
          if (item.validateType === 'Required') {
            temRule.push({
              required: true,
              message: item.message,
              trigger: item.trigger,
              type: ['MultiBinding', 'MultiSelect'].includes(param.uiType) ? 'array' : 'string'
            })
          } else if (item.validateType === 'Regex') {
            temRule.push({
              type: 'string',
              pattern: new RegExp(item.validateRange),
              message: item.message,
              trigger: item.trigger
            })
          } else if (item.validateType === 'Function') {
            temRule.push({
              validator: ['validatorTitle'].includes(item.validateRange) ? validatorTitle : () => {},
              trigger: 'blur'
            })
          }
        })
      }
      return temRule;
    },
    // 根据返回的添加去获取需要绑定的列表
    conditionBindList(param) {
      let temArry = [];
      if (param.defaultValue === 'empty') {
        temArry.push({
          name: this.$t('message.workflow.process.notBinding'),
          key: 'empty'
        })
      }
      // 对绑定的参数进行过滤
      const conditionResult = (type) => {
        if (param.value && JSON.parse(param.value)) {
          // 如果是通配符就返回true
          const optionsList = JSON.parse(param.value);
          if (optionsList[0] === '*') {
            return true;
          } else {
            return optionsList.includes(type);
          }
        }
      }
      if (this.json.nodes && this.json.nodes.length) {
        this.json.nodes.forEach((node) => {
          if (node.key !== this.clickCurrentNode.key && conditionResult(node.type)) {
          // 当sql节点里面没内容时,resources属性值为[]，这种sql节点不放做选项
            const tempObj = {
              name: node.title,
              key: node.key,
            }
            temArry.push(tempObj)
          }
        })
      }
      return temArry;
    },
    filterFormType(val) {
      switch (val) {
        case 'Text':
          return 'textarea';
        default:
          return 'text';
      }
    },
    updateOriginData(node, scriptisSave) {
      this.json.nodes = this.json.nodes.map((item) => {
        if (item.key === node.key) {
          item.jobContent = node.jobContent;
          item.resources = node.resources;
          item.params = node.params;
          item.modifyUser = this.getUserName();
          item.modifyTime = Date.now();
        }
        return item;
      })
      // 避免在保存脚本时，已打开右侧参数栏，此时保存的会是旧值
      if (this.clickCurrentNode && this.clickCurrentNode.key === node.key) {
        this.clickCurrentNode.jobContent = node.jobContent;
        this.clickCurrentNode.resources = node.resources;
        this.clickCurrentNode.params = node.params;
      }
      this.originalData = this.json;
      // 更新节点之后自动保存json
      if (scriptisSave) {
        this.autoSave(this.$t('message.workflow.Save'), false);
      }
    },
    getBaseInfo() {
      this.loading = true;
      this.clickCurrentNode = {};
      this.nodebaseinfoShow = false;
      this.changeNum = 0;
      this.getOriginJson();
    },
    initAction(json) {
      // 创建工作流之后就有值
      this.contextID = json.contextID;
      // 保存节点才有的值
      this.schedulerAppConnName = json.schedulerAppConnName
      if (json && json.nodes) {
        this.originalData = this.json = JSON.parse(JSON.stringify(json));
        this.resources = json.resources;
        this.props = json.props;
        this.scheduleParams = json.scheduleParams || {};
      }
      this.$nextTick(() => {
        this.loading = false;
      });
      this.pollUpdateLock();
    },
    getOriginJson() {
      return api.fetch(`/dss/workflow/get`, {
        flowId: this.flowId,
        labels: this.getCurrentDsslabels()
      },'get').then((res) => {
        let json = this.convertJson(res.flow);
        let flowEditLock = res.flow.flowEditLock;
        if (flowEditLock && !this.myReadonly) {
          this.setFlowEditLock(flowEditLock);
        }
        this.initAction(json);
      }).catch(() => {
        this.loading = false;
        this.locked = true;
        if (this.updateLockTimer) {
          clearTimeout(this.updateLockTimer)
        }
        this.$emit('close')
      });
    },
    convertJson(flow) {
      this.name = flow.name;
      this.isRootFlow = flow.rootFlow;
      this.rank = flow.rank; // 工作流层级
      let json;
      // 没传version则默认取最新的
      json = flow.flowJson;
      // this.bmlVersion = flow.bmlVersion;

      // JSON:  优先缓存 > 通过id去查关联JSON数据
      if (json) {
        json = JSON.parse(json);
        if (json.nodes && Array.isArray(json.nodes)) {
          // 需要把 id -> key, jobType -> type
          json.nodes = json.nodes.map((node) => {
            node.type = node.jobType;
            delete node.jobType;
            return node;
          });
        }
        this.orcVersion = json.orcVersion
      }
      return json;
    },
    setShapes() {
      return api.fetch(`${this.$API_PATH.WORKFLOW_PATH}listNodeType`, {
        labels: this.getCurrentDsslabels()
      },'get').then((res) => {
        this.shapes = res.nodeTypes.map((item) => {
          if (item.children.length > 0) {
            item.children = item.children.map((subItem) => {
              // svg绘制的点太多，导致动画卡顿，使用图片代替
              if (nodeIcons[subItem.title]) {
                subItem.image = nodeIcons[subItem.title];
              } else if (subItem.image) {
                subItem.image = 'data:image/svg+xml;base64,' + window.btoa(unescape(encodeURIComponent(subItem.image)))
              }
              return subItem
            })
          }
          return item;
        });
      });
    },
    change(obj) {
      this.json = obj;
      this.changeNum++
      const change = this.checkChange(obj)
      this.lastObj = JSON.parse(JSON.stringify(obj))
      if (this.changeNum > 2 && change) {
        this.heartBeat();
        this.jsonChange = true;
      }
    },
    checkChange(obj) {
      // 剔除单击节点选中导致的change
      // createTime 流程图产生的数据
      // params 动态添加的，手动保存会更新jsonchange标志位
      const helpFn = function(obj = {}) {
        const temp = { nodes: [], edges: [] }
        if (obj.nodes) {
          obj.nodes.forEach(item => {
            const nodeItem = {}
            Object.keys(item).forEach(k => {
              if(['selected','createTime', 'updateTime', 'params'].indexOf(k) < 0) {
                nodeItem[k] = item[k]
              }
            })
            temp.nodes.push(nodeItem)
          })
        }
        if (obj.edges) {
          obj.edges.forEach(item => {
            const link = {}
            Object.keys(item).forEach(k => {
              if(['selected','createTime', 'updateTime', 'params'].indexOf(k) < 0) {
                link[k] = item[k]
              }
            })
            temp.edges.push(link)
          })
        }
        return JSON.stringify(temp)
      }
      return helpFn(obj) !== helpFn(this.lastObj)
    },
    initNode(arg) {
      if(this.clickCurrentNode.id && this.clickCurrentNode.id === arg.id) return; // 多出点击时，避免数据初始化
      arg = this.bindNodeBasicInfo(arg);
      this.clickCurrentNode = JSON.parse(JSON.stringify(arg));
    },
    click(arg) {
      clearTimeout(this.timerClick);
      this.timerClick = setTimeout(() => {
        if (this.workflowIsExecutor) return;
        this.initNode(arg);
        this.nodebaseinfoShow = true;
        this.$emit('node-click', arg);
      }, 200);
    },
    dblclick(...arg) {
      arg[0] = this.bindNodeBasicInfo(arg[0]);
      arg[0].contextID = this.contextID;
      // 由后台控制是否支持跳转
      clearTimeout(this.timerClick);
      // 执行过程中只有子工作流可双击打开
      if (!arg[0].supportJump || (this.workflowIsExecutor && arg[0].type !== NODETYPE.FLOW)) return;
      if ((!arg[0].jobContent || Object.keys(arg[0].jobContent).length === 0) && arg[0].shouldCreationBeforeNode) {
        this.addNodeShow = true;
        this.clickCurrentNode = JSON.parse(JSON.stringify(arg[0]));
        this.addNodeTitle = this.$t('message.workflow.process.createNode');
      } else {
        // 为node信息添加modelType字段方便脚本格式判断
        arg[0].modelType = ext[arg[0].type];
        this.$emit('node-dblclick', arg);
      }
    },
    message(obj) {
      let type = {
        warning: 'warn',
        error: 'error',
        info: 'info',
      };
      this.$Message[type[obj.type]]({
        content: obj.msg,
        duration: 2,
      });
    },
    async saveNodeBaseInfo(arg) {
      this.$emit('saveBaseInfo', arg);
      // 如果是可编辑脚本得改变打开的脚本得名称
      this.dispatch('Workbench:updateFlowsNodeName', arg);
      // 当保存子流程节点的基础信息时，如果子流程节点没有 embeddedFlowId:"flow_id" 则先创建子流程节点
      let node = arg;
      if (node.type == NODETYPE.FLOW) {
        if (this.rank >= 4) {
          return this.$Message.warning(this.$t('message.workflow.process.rankLimit'));
        }

        //  节点原始数据
        if (!node.jobContent) {
          node.jobContent = {};
        }
        // 如果子流程节点的node.jobContent.embeddedFlowId为空表明还未子流程还未创建生成flowID
        const reg = /^[a-zA-Z][a-zA-Z0-9_]*$/;
        if (!node.title.match(reg)) {
          return this.$Message.warning(this.$t('message.workflow.validNameDesc'));
        }

        if (!node.jobContent.embeddedFlowId) {
          // 调用接口创建
          const result = await api.fetch(`${this.$API_PATH.WORKFLOW_PATH}addFlow`, {
            name: node.title,
            description: node.desc,
            parentFlowID: Number(this.flowId),
            workspaceName: this.getCurrentWorkspaceName(),
            projectName: this.$route.query.projectName,
            version: this.version,
            userName: this.getUserName(),
            labels: {route: this.getCurrentDsslabels()},

          }).then((res) => {
            this.$Notice.success({
              desc: this.$t('message.workflow.process.createSubSuccess'),
            });
            node.jobContent.embeddedFlowId = res.flow.id;
            return true
          });
          if (result !== true) {
            return
          }
        } else {
          await api.fetch(`${this.$API_PATH.WORKFLOW_PATH}updateFlowBaseInfo`, {
            id: node.jobContent.embeddedFlowId,
            name: node.title,
            description: node.desc,
            labels: {route: this.getCurrentDsslabels()}
          }, 'post').then(() => {
          })
        }
      } else {
        // iframe节点
        await this.saveCommonIframe(node, () => {
          // flage = true;
        });
      }
      // 为了表单校验，基础信息弹窗保存的节点已不再是响应式，需重新赋值给json

      this.json.nodes = this.json.nodes.map((item) => {
        if (item.key === node.key) {
          item.title = node.title;
          item.desc = node.desc;
          item.jobContent = node.jobContent;
          item.resources = node.resources || [];
          item.params = node.params; // 节点参数现在存在这里，和jobparams一样
          item.bindViewKey = node.bindViewKey || "";
          item.appTag = node.appTag;
          item.businessTag = node.businessTag;
          item.modifyUser = this.getUserName();
          item.modifyTime = Date.now();
        }
        return item;
      });
      this.originalData = this.json;
      this.jsonChange = true;
      this.addNodeShow = false;
      // 保存工作流
      this.autoSave('paramsSave', false);
    },
    saveNodeParam(...arg) {
      this.$emit('saveParam', arg);
    },
    /**
     * 保存工作流
     */
    handleSave: debounce(function () {
      this.save()
    }, 1500),
    save() {
      if (this.workflowIsExecutor) return;
      // 检查JSON
      if (!this.validateJSON()) {
        return;
      }
      // 检查当前json是否有子节点未保存
      const subArray = this.openFiles[this.name] || [];
      const changeList = this.tabs.filter((item) => {
        return subArray.includes(item.key) && item.node.isChange;
      });
        // 保存时关闭控制台
      this.openningNode = null;
      if (changeList.length > 0) {
        this.$Modal.confirm({
          title: this.$t('message.workflow.process.cancelNotice'),
          content: this.$t('message.workflow.process.noSaveHtml'),
          okText: this.$t('message.workflow.process.confirmSave'),
          cancelText: this.$t('message.workflow.cancel'),
          onOk: () => {
            this.saveModal = false;
            let json = JSON.parse(JSON.stringify(this.json));
            json.nodes.forEach((node) => {
              this.$refs.process.setNodeRunState(node.key, {
                borderColor: '#6A85A7',
              })
            })
            this.autoSave(this.$t('message.workflow.Manually'), false);
          },
          onCancel: () => {
          },
        });
      } else {
        this.saveModal = false;
        let json = JSON.parse(JSON.stringify(this.json));
        json.nodes.forEach((node) => {
          this.$refs.process.setNodeRunState(node.key, {
            borderColor: '#6A85A7',
          })
        })
        this.autoSave(this.$t('message.workflow.Manually'), false);
      }
    },
    autoSave(comment, f) {
      // 检查JSON
      if (!this.validateJSON()) {
        this.loading = false;
        return false;
      }
      // 需要把 key -> id,  type -> jobType， title -> id
      let json = JSON.parse(JSON.stringify(this.json));
      json.edges.forEach((edge) => {
        let sources = json.nodes.filter((node) => {
          return node.key == edge.source;
        });
        if (sources.length > 0) {
          let source = sources[0];
          edge.source = source.key;
        }
        let targets = json.nodes.filter((node) => {
          return node.key == edge.target;
        });
        if (targets.length > 0) {
          let target = targets[0];
          edge.target = target.key;
        }
      });
      let flage = false;
      json.nodes.forEach((node) => {
        const reg = /^[a-zA-Z][a-zA-Z0-9_]*$/;

        if (!node.title.match(reg)) {
          return flage = true;
        }
        node.id = node.key;
        node.jobType = node.type;
        node.selected = false; // 保存之前初始化选中状态
        delete node.type;
        delete node.menu; // 删除菜单配置
        // 将用户保存的resources值为空字符串转为空数组
        if (!node.resources) {
          node.resources = [];
        }
        // 保存之前删掉执行的状态信息
        if(node.runState) {
          delete node.runState
        }

      });
      if (flage) return this.$Message.warning(this.$t('message.workflow.validNameDesc'));
      const isFiveNode = json.nodes.filter((item) => {
        return !item.jobContent && item.jobType === NODETYPE.FLOW && this.rank >= 4;
      });
      if (isFiveNode.length > 0) return this.$Message.warning(this.$t('message.workflow.process.deleteNodeSave'));
      return this.saveRequest(json, comment, f);
    },
    // 保存请求
    saveRequest(json, comment, f) {
      const updateTime = Date.now();
      const paramsJson = JSON.parse(JSON.stringify(Object.assign(json, {
        comment: comment,
        type: this.type,
        updateTime,
        updateUser: this.getUserName(),
        props: this.props,
        resources: this.resources,
        scheduleParams: this.scheduleParams,
        contextID: this.contextID,
        orcVersion: this.orcVersion
      })));
      if (this.schedulerAppConnName !== undefined) {
        paramsJson.schedulerAppConnName = this.schedulerAppConnName
      }

      paramsJson.nodes = paramsJson.nodes.map((node) => {
        // 删除节点里的contextID
        if(node.contextID) {
          delete node.contextID;
        }
        delete node.jobParams;
        return node;
      });
      return api.fetch(`${this.$API_PATH.WORKFLOW_PATH}saveFlow`, {
        id: Number(this.flowId),
        json: JSON.stringify(paramsJson),
        projectName: this.$route.query.projectName,
        workspaceName: this.getCurrentWorkspaceName(),
        labels: {
          route: this.getCurrentDsslabels()
        },
        flowEditLock: this.getFlowEditLock()
      }).then((res) => {
        this.loading = false;
        // 将更新的互斥锁的res.flowEditLock字段存储到本地
        let flowEditLock = res.flowEditLock;
        if (flowEditLock) {
          this.setFlowEditLock(flowEditLock)
          this.pollUpdateLock()
        }
        if (!f) {
          this.$Notice.success({
            desc: this.$t('message.workflow.process.saveWorkflowCuccess'),
          });
          this.saveModel.comment = '';
        } else {
          this.$Notice.success({
            desc: this.$t('message.workflow.process.autoSaveWorkflow'),
          });
        }
        this.jsonChange = false;
        // 保存成功后去更新tab的工作流数据
        this.$emit('updateWorkflowList');
        return res;
      }).catch(() => {
        this.loading = false;
      });
    },
    /**
     * 显示工作流参数配置页面
     */
    showParamView() {
      if (this.workflowIsExecutor) return;
      this.nodebaseinfoShow = false;
      if (!this.isResourceShow && !this.isDispatch) {
        this.isParamModalShow = !this.isParamModalShow;
      } else {
        this.isParamModalShow = true;
        this.isResourceShow = false;
        this.isDispatch = false;
      }
    },
    setFlowEditLock(flowEditLock) {
      let data = storage.get("flowEditLock") || {}
      const key = this.getUserName()
      const updateList = (data[key] || []).filter(it => it.projectId != this.$route.query.projectID)
      updateList.push({ flowId: this.flowId, lock: flowEditLock, projectId: this.$route.query.projectID })
      storage.set("flowEditLock", {
        [key]: updateList
      });
    },
    getFlowEditLock() {
      const data = storage.get("flowEditLock") || {}
      const key = this.getUserName()
      const item = (data[key] || []).find(it => it.flowId == this.flowId && it.projectId == this.$route.query.projectID)
      return item && item.lock
    },
    updateLock() {
      const flowEditLock = this.getFlowEditLock()
      if (!flowEditLock || this.myReadonly) return
      api.fetch(`${this.$API_PATH.WORKFLOW_PATH}updateFlowEditLock`, {
        flowEditLock,
        labels: this.getCurrentDsslabels()
      }, 'get')
        .then((rst) => {
          if (rst.flowEditLock) {
            this.setFlowEditLock(rst.flowEditLock)
            this.pollUpdateLock()
          }
        }).catch(()=>{
          //
        })
    },
    pollUpdateLock() {
      clearTimeout(this.updateLockTimer)
      this.updateLockTimer = setTimeout(()=>{
        this.updateLock()
      }, 2 * 60 * 1000)
    },
    /**
     * 显示资源导入页面
     */
    showResourceView() {
      if (this.workflowIsExecutor) return;
      this.isDispatch = false;
      this.nodebaseinfoShow = false;
      if (this.isResourceShow) {
        this.isParamModalShow = !this.isParamModalShow;
      } else {
        this.isParamModalShow = true;
        this.isResourceShow = true;
      }
    },
    /**
     * 检查JSON，是否符合规范
     * @return {Boolean}
     */
    validateJSON() {
      if (!this.json) {
        this.$Modal.warning({
          title: this.$t('message.workflow.process.notice'),
          content: this.$t('message.workflow.process.dragNode'),
        });
        return false;
      }
      let edges = this.json.edges;
      let nodes = this.json.nodes;
      if (!nodes || nodes.length === 0) {
        this.$Modal.warning({
          title: this.$t('message.workflow.process.notice'),
          content: this.$t('message.workflow.process.dragNode'),
        });
        return false;
      }
      let headers = [];
      let footers = [];
      let titles = [];
      let repeatTitles = [];
      nodes.forEach((node) => {
        if (titles.includes(node.title)) {
          repeatTitles.push(node.title);
        } else {
          titles.push(node.title);
        }
        if (!edges.some((edge) => {
          return edge.target == node.key;
        })) {
          headers.push(node);
        }
        if (!edges.some((edge) => {
          return edge.source == node.key;
        })) {
          footers.push(node);
        }
      });
      // 后台会把名称当做id处理，所以名称必须唯一
      if (repeatTitles.length > 0) {
        this.repeatTitles = repeatTitles;
        this.repetitionNameShow = true;
        return false;
      }
      return true;
    },
    onPropsChange(value) {
      this.props = value;
    },
    onScheduleChange(value) {
      this.scheduleParams = value;
    },
    handleOutsideClick(e) {
      let paramButton = this.$refs.paramButton;
      let resourceButton = this.$refs.resourceButton;
      let dispatchButton = this.$refs.dispatchButton;
      if ((paramButton && paramButton.contains(e.target)) || e.target === paramButton
                || (resourceButton && resourceButton.contains(e.target)) || e.target === resourceButton || (dispatchButton && dispatchButton.contains(e.target)) || e.target === dispatchButton) {
        return;
      }
      if (this.isParamModalShow) {
        this.isParamModalShow = false;
      }
    },
    handleOutsideClickNode(e) {
      if (e.target.className === 'node-box-content' || e.target.className === 'node-box') return;
      if (this.nodebaseinfoShow) {
        this.nodebaseinfoShow = false;
      }
    },
    updateResources(res) {
      this.resources = res.map((item) => {
        return {
          fileName: item.fileName,
          resourceId: item.resourceId,
          version: item.version,
        };
      });
      this.jsonChange = true;
    },
    async nodeDelete(node) {
      node = this.bindNodeBasicInfo(node);
      if (node.type === NODETYPE.FLOW && node.jobContent && node.jobContent.embeddedFlowId) {
        const params = {
          id: +node.jobContent.embeddedFlowId,
          sure: false,
          labels: {
            route: this.getCurrentDsslabels()
          }
        }
        await api.fetch(`${this.$API_PATH.WORKFLOW_PATH}deleteFlow`, params, 'post').then(() => {
          this.$Message.success(this.$t('message.workflow.deleteSuccess'));
          this.$emit('deleteNode', node);
          this.$refs.process.deleteNode(node.key)
          // 如果删除的是当前修改参数的节点，关闭侧边栏
          if (this.clickCurrentNode.key === node.key) {
            this.clickCurrentNode = {};
            this.nodebaseinfoShow = false;
          }

          // 删除事件比jsonchange时机早
          const timeId = setTimeout(() => {
            this.autoSave('deleteSave', false);
            clearTimeout(timeId);
          }, 500)
        })
      } else {
        if (node.jumpType == 1) {
          const params = {
            nodeType: node.type,
            name: node.title,
            description: node.desc,
            projectID: +this.$route.query.projectID,
            params: node.jobContent,
            labels: {
              route: this.getCurrentDsslabels()
            }
          }
          await api.fetch(`${this.$API_PATH.WORKFLOW_PATH}deleteAppConnNode`,params, 'post').then(() => {
            this.$Message.success(this.$t('message.workflow.deleteSuccess'));
            this.$emit('deleteNode', node);
            this.$refs.process.deleteNode(node.key)

            // 如果删除的是当前修改参数的节点，关闭侧边栏
            if (this.clickCurrentNode.key === node.key) {
              this.clickCurrentNode = {};
              this.nodebaseinfoShow = false;
            }

            // 删除事件比jsonchange时机早
            const timeId = setTimeout(() => {
              this.autoSave('deleteSave', false);
              clearTimeout(timeId);
            }, 500)
          })
        } else {
          this.$emit('deleteNode', node);
          this.$refs.process.deleteNode(node.key);
          // 如果删除的是当前修改参数的节点，关闭侧边栏
          if (this.clickCurrentNode.key === node.key) {
            this.clickCurrentNode = {};
            this.nodebaseinfoShow = false;
          }

          // 删除事件比jsonchange时机早
          const timeId = setTimeout(() => {
            this.autoSave('deleteSave', false);
            clearTimeout(timeId);
          }, 500)
        }
      }
    },
    repetitionName() {
      this.repetitionNameShow = false;
    },
    // 单击节点出来的右边的弹框的保存事件
    saveNode(node) { // 保存节点参数配置
      if (this.myReadonly) return this.$Message.warning(this.$t('message.workflow.process.readonly'));
      this.saveNodeBaseInfo(node);
    },
    onContextMenu(menu, data) {
      switch(menu) {
        case 'associate':
          this.checkAssociated(data);
          break;
        case 'console':
          this.openConsole(data);
          break;
        case 'mycopy':
          this.copyNode(data);
          break;
        case 'mypaste':
          this.pasteNode(data);
          break;
        case 'allDelete':
          this.allDelete();
          break;
        case 'relySelectUpOne':
          this.relySelect(data, 'up-one');
          break;
        case 'relySelectDownOne':
          this.relySelect(data, 'down-one');
          break;
        case 'relySelectUp':
          this.relySelect(data, 'up');
          break;
        case 'relySelectDown':
          this.relySelect(data, 'down');
          break;
      }
    },
    checkAssociated(node) {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.workflow.process.readonlyNoAssociated'));
      if ([NODETYPE.SPARKSQL, NODETYPE.HQL, NODETYPE.SPARKPY, NODETYPE.SCALA, NODETYPE.PYTHON].indexOf(node.type) === -1) {
        return this.$Notice.warning({
          desc: this.$t('message.workflow.process.noAssociated'),
        });
      } else if (node.jobContent && node.jobContent.script) {
        this.$Modal.confirm({
          title: this.$t('message.workflow.process.repeateAssociated'),
          content: this.$t('message.workflow.process.repeateAssociatedHtml'),
          okText: this.$t('message.workflow.process.confirmAssociated'),
          cancelText: this.$t('message.workflow.cancel'),
          onOk: () => {
            this.openAssociateScriptModal(node);
          },
          onCancel: () => {
            // this.$Modal.remove();
          },
        });
      } else {
        this.openAssociateScriptModal(node);
      }
    },
    openAssociateScriptModal(node) {
      this.$refs.associateScript.open(node);
    },
    associateScript(node, path, cb) {
      api.fetch('/filesystem/openFile', {
        path,
      }, 'get').then((rst) => {
        const supportModes = this.getSupportModes();
        const time = new Date();
        // 由于修改了节点类型所以之前获取方法不行
        const type = ext[node.type];
        const match = supportModes.find((item) => item.flowType === type);
        const fileName = `${time.getTime()}${match.ext}`;
        const params = {
          fileName,
          scriptContent: rst.fileContent[0][0],
          creator: node.creator,
          metadata: rst.metadata,
          projectName: this.$route.query.projectName || ''
        };
        if (params.metadata && params.metadata.configuration) delete params.metadata.configuration.startup;
        api.fetch('/filesystem/saveScriptToBML', params, 'post')
          .then((res) => {
            this.$Message.success(this.$t('message.workflow.process.associaSuccess'));
            node.params = {...node.params, ...rst.params};
            const params = {
              fileName,
              resourceId: res.resourceId,
              version: res.version,
              creator: node.creator || '',
              projectName: this.$route.query.projectName || ''
            };
            this.$emit('check-opened', node, (isOpened) => {
              this.$emit('save-node', params, node, true);
              if (isOpened) {
                api.fetch('/filesystem/openScriptFromBML', params, 'get').then((res) => {
                  this.dispatch('Workbench:updateFlowsTab', node, {
                    content: res.scriptContent,
                    params: res.metadata,
                  });
                });
              } else {
                // 如果没打开节点，是无法调取Workbench的方法的
                // 所以，直接调用IndexedDB清空缓存
                this.dispatch('IndexedDB:clearLog', node.key);
                this.dispatch('IndexedDB:clearResult', node.key);
                this.dispatch('IndexedDB:clearProgress', node.key);
              }
            });
            cb(true);
          }).catch(() => {
            cb(false);
            this.$Message.error(this.$t('message.workflow.process.associaError'));
          });
      }).catch(() => {
        cb(false);
      });
    },
    addNode(node) {
      // 关闭右侧弹窗
      this.nodebaseinfoShow = false;
      // 新拖入的节点，自动生成新的不重复名称,给名称后面加四位随机数
      node = this.bindNodeBasicInfo(node);
      this.clickCurrentNode = JSON.parse(JSON.stringify(node));
      this.clickCurrentNode.title = this.clickCurrentNode.title + '_' + Math.round(Math.random()*10000);
      // 弹窗提示由后台控制
      if (node.shouldCreationBeforeNode) {
        this.addNodeShow = true;
        this.addNodeTitle = this.$t('message.workflow.process.createNode');
      } else {
        // 还得同步更新json中的node
        this.json.nodes = this.json.nodes.map((subItem) => {
          if (subItem.key === this.clickCurrentNode.key) {
            subItem.title = this.clickCurrentNode.title;
            subItem.modifyUser = this.getUserName();
            subItem.modifyTime = Date.now();
          }
          return subItem;
        });
        this.originalData = this.json;
        this.autoSave(this.$t('message.workflow.AddNode'), false);
        return;
      }
    },
    validatorName(rule, value, callback) {
      if (value === `${this.name}_`) {
        callback(new Error(this.$t('message.workflow.process.nodeNameValid')));
      } else {
        callback();
      }
    },
    addFlowCancel() {
      this.addNodeShow = false;
      // 删除未创建成功的节点
      this.json.nodes = this.json.nodes.filter((subItem) => {
        return this.clickCurrentNode.key != subItem.key;
      });
      this.originalData = this.json;
    },
    // 创建节点时
    addFlowOk() {
      this.$refs.addFlowfoForm.validate((valid) => {
        if (valid) {
          this.addFlowOkFunction()
        }
      });
    },
    // addFlowOk函数里可以复用的操作
    addFlowOkFunction() {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.workflow.process.readonlyNoCeated'));
      this.saveNodeBaseInfo(this.clickCurrentNode);
    },
    relySelect(node, dir) {
      /**
       * 1.获取当前节点的key
       * 2.查找以当前key为source的节点
       * 3.遍历查找出来的节点数组，接着递归
       *  */
      let stepArray = [];
      const stepArrayAction = (nodeKey, level = 0) => {
        level++;
        this.json.edges.forEach((item) => {
          if ( dir === 'down' && item.source === nodeKey) {
            stepArray.push({...item, level});
            stepArrayAction(item.target, level);
          } else if(dir === 'up' && item.target === nodeKey) {
            stepArray.push({...item, level});
            stepArrayAction(item.source, level);
          } else if(dir === 'down-one' && item.source === nodeKey) {
            stepArray.push({...item, level});
          } else if(dir === 'up-one' && item.target === nodeKey) {
            stepArray.push({...item, level});
          }
        });
      };
      stepArrayAction(node.key);
      this.json.nodes = this.json.nodes.map((subItem) => {
        let runState
        const inDeps = stepArray.filter((item) => (item.target === subItem.key && dir.includes('down')) || (item.source === subItem.key && dir.includes('up')))
        if (subItem.key === node.key) subItem.selected = true;
        if (inDeps.length ) {
          subItem.selected = true;
          // runState = {
          //   outerText: Math.max(...inDeps.map(it => it.level)),
          //   outerStyle: {
          //     position: 'absolute',
          //     top: '12px',
          //     right: '-30px',
          //     width: '30px',
          //     textAlign: 'center',
          //     color: 'rgb(237, 64, 20)',
          //   }
          // }
        } else {
          subItem.selected = false;
        }
        return {...subItem, runState};
      });
      this.originalData = this.json;
    },
    heartBeat: throttle(() => {
      api.fetch('/user/heartbeat', 'get');
    }, 60000),
    copyNode(node) {
      node = this.bindNodeBasicInfo(node);
      if (node.enableCopy === false) return this.$Message.warning(this.$t('message.workflow.process.noCopy'));
      if (this.myReadonly) return this.$Message.warning(this.$t('message.workflow.process.readonlyNoCopy'));
      this.cacheNode = JSON.parse(JSON.stringify(node));
    },
    pasteNode(e) {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.workflow.process.noPaste'));
      if (!this.cacheNode) {
        return this.$Message.warning(this.$t('message.workflow.process.firstCopy'));
      }
      let tmpTitle = this.cacheNode.title+'_copy'
      const hasNodeTitle = this.json.nodes.filter(it => it.title.indexOf(tmpTitle) > -1)
      if (hasNodeTitle.length) {
        tmpTitle = tmpTitle + hasNodeTitle.length
      }
      if (tmpTitle.length > 150) {
        return this.$Message.warning(this.$t('message.workflow.process.namelength'));
      }
      // 获取屏幕的缩放值
      let pageSize = this.$refs.process.getState().baseOptions.pageSize;
      const key = '' + new Date().getTime() + Math.ceil(Math.random() * 100);
      this.cacheNode.key = key;
      this.cacheNode.id = key;
      this.cacheNode.selected = true;
      this.cacheNode.title = tmpTitle
      this.cacheNode.createTime = Date.now()
      this.cacheNode.layout = {
        height: this.cacheNode.height,
        width: this.cacheNode.width,
        x: (e.offsetX / pageSize),
        y: (e.offsetY / pageSize),
      };
      // 外部节点以限制复制，如果要复制要删除id
      if (this.cacheNode.shouldCreationBeforeNode) {
        delete this.cacheNode.jobContent;
      }
      // 删掉节点的执行信心
      if(this.cacheNode.runState) {
        delete this.cacheNode.runState;
      }
      this.json.nodes = this.json.nodes.map((subItem) => {
        subItem.selected = false;
        return subItem;
      });
      this.json.nodes.push(JSON.parse(JSON.stringify(this.cacheNode)));
      this.originalData = this.json;
      this.click(this.cacheNode)
    },
    // 由于插件的selected不是响应式，所以得手动改变
    nodeSelectedFalse(node = {}) {
      this.json.nodes = this.json.nodes.map((subItem) => {
        if (node.key && node.key === subItem.key) {
          subItem.selected = true;
        } else {
          subItem.selected = false;
        }
        return subItem;
      });
      this.originalData = this.json;
    },
    clickBaseInfo() {
      this.nodeSelectedFalse(this.clickCurrentNode);
    },    // 批量删除选中节点
    async allDelete() {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.workflow.process.noDelete'));
      let selectNodes = this.$refs.process.getSelectedNodes();
      const selectNodeLength = selectNodes.length
      if (selectNodeLength < 1) return
      // 批量删除跳过子工作流节点
      selectNodes = selectNodes.filter(node => node.type !== NODETYPE.FLOW)
      const selectNodeKeys = selectNodes.map((item) => item.key);
      selectNodes = selectNodes.map((item) => this.bindNodeBasicInfo(item));
      const nodes = [];
      selectNodes.forEach(node=>{
        if (node.supportJump && node.shouldCreationBeforeNode && node.jobContent) {
          nodes.push({
            nodeType: node.type,
            name: node.title,
            description: node.desc,
            projectID: +this.$route.query.projectID,
            params: node.jobContent,
            labels: {
              route: this.getCurrentDsslabels()
            }
          })
        }
      })
      await api.fetch(`${this.$API_PATH.WORKFLOW_PATH}batchDeleteAppConnNode`, {nodes}, 'post').then(() => {
        this.$Message.success(this.$t('message.workflow.deleteSuccess'));
      })
      selectNodes.forEach(node=>{
        this.$emit('deleteNode', node);
      })
      this.json.nodes = this.json.nodes.filter((item) => {
        if (selectNodeKeys.includes(item.key)) {
          this.json.edges = this.json.edges.filter((link) => {
            return !(link.source === item.key || link.target === item.key);
          });
        } else {
          item.selected = false;
          return true;
        }
      });
      this.originalData = this.json;
      if (selectNodeLength > selectNodes.length) {
        this.$Message.warning(this.$t('message.workflow.BatchDel'));
      }
      this.autoSave('allDelete', false);
    },
    /**
     * 右键菜单点击打开管理台
     */
    async openConsole(node) {
      this.nodeSelectedFalse(node); // 改变节点的选择状态
      // 将数据结构适配全局console组件
      node.runType = 'node'; // 新增运行类型字段
      node.taskID = node.runState.taskID; // 新增任务id
      node.execID = node.runState.execID; // 新增执行id
      this.openningNode = node; // 传给控制台的参数
      this.consoleHeight = this.$el ? this.$el.clientHeight / 2 : 250
      this.shapeWidth = this.$refs.process && this.$refs.process.state.shapeOptions.viewWidth; // 自适应控制台宽度
      this.$nextTick(() => {
        this.$refs.currentConsole.checkFromCache();
      })
    },
    closeConsole() {
      this.openningNode = null;
    },
    toggleShape(shapeFold) {
      // 工作流icon是否收起
      if (shapeFold) {
        this.shapeWidth = 0;
      } else {
        this.shapeWidth = this.$refs.process && this.$refs.process.state.shapeOptions.viewWidth;
      }
    },
    saveCommonIframe(node, cb) {
      if (!(node.supportJump && node.shouldCreationBeforeNode)) return;
      this.loading = true;
      if (!node.jobContent) {
        cb();
        const newCreateParams = this.getCreatePrams(node);
        const createParams = {
          flowID: this.flowId,
          projectID: +this.$route.query.projectID,
          nodeType: node.type,
          nodeID: node.key,
          params: {
            ...node.jobContent,
            ...newCreateParams
          },
          labels: {
            route: this.getCurrentDsslabels()
          }
        }
        return api.fetch(`${this.$API_PATH.WORKFLOW_PATH}createAppConnNode`, createParams).then((res) => {
          // 由于vsbi的错误信息返回的这里，所以得判断是否成功给予提示
          let commomData = {};
          try {
            commomData = JSON.parse(res.result);
          } catch {
            commomData = res.result;
          }
          if (commomData) {
            node.jobContent= commomData;
            this.$Message.success({
              content: this.$t('message.workflow.process.createdSuccess')
            });
          } else {
            this.$Message.error({
              content: commomData.msg,
              duration: 4,
            });
          }
          // 创建成功关闭右侧栏
          this.nodebaseinfoShow = false;
        }).catch(() => {
          this.json.nodes = this.json.nodes.filter((subItem) => {
            return node.key != subItem.key;
          });
          this.originalData = this.json;
        })
      } else {
        const params = {
          flowID: this.flowId,
          nodeType: node.type,
          projectID: +this.$route.query.projectID,
          params: {
            ...node.jobContent,
            title: node.title,
            desc: node.desc
          },
          labels: {
            route: this.getCurrentDsslabels()
          }
        }
        return api.fetch(`${this.$API_PATH.WORKFLOW_PATH}updateAppConnNode`, params, 'post').then(() => {
          this.$Message.success(this.$t('message.workflow.updataSuccess'))
        }).catch(() => {})
      }
    },
    // 获取需要在创建的时候填写的参数
    getCreatePrams(node) {
      const createParams = {}
      node.nodeUiVOS.filter((item) => item.baseInfo)
        .map(item => item.key).map(item => {
          createParams[item] = node[item];
        })
      return createParams;
    },
    // 根据节点类型将后台节点基础信息加入
    bindNodeBasicInfo(node) {
      const shapes = JSON.parse(JSON.stringify(this.shapes));
      shapes.map((item) => {
        if (item.children.length > 0) {
          item.children.map((subItem) => {
            if (subItem.type === node.type) {
              node = Object.assign(subItem, node);
            }
          })
        }
      })
      return node;
    },
    // 点击节流
    clickswitch: debounce(
      function(type){
        this.workflowIsExecutor ? this.workflowStop() : this.workflowRun(type)
      },1000
    ),
    // 失败重跑
    reRun() {
      this.workflowRun('rerun')
    },
    async workflowRun(runFlag) {
      let selectNodes = this.$refs.process.getSelectedNodes();
      const selectNodeLength = selectNodes.length
      if ( runFlag === 'select') {
        if (selectNodeLength < 1 ) {
          return this.$Message.error(this.$t('message.workflow.PleaseSelectNode'));
        }
      }
      this.dispatch('workflowIndexedDB:clearNodeCache');
      // 重新执行清掉上次的计时器
      clearTimeout(this.excuteTimer);
      clearTimeout(this.executorStatusTimer);
      this.needReRun = false;
      this.openningNode = null;
      // return this.$Message.warning('执行重构中，即将开源');
      /**
       * 1.执行之前先保存，执行改为停止
       * 2.禁用操作：左侧菜单，保存，参数修改，工具栏，更具状态来操作右键
       * 3.轮询接口获取节点状态
      */
      // 如果是生产中心的只读模式不需要保存
      let a = null;
      if (!this.myReadonly) {
        a = await this.autoSave(this.$t('message.workflow.Saving'), false);
        if (!a || !a.flowVersion) return;

      }
      // 保存成功后再调执行接口
      const parmas = {
        executeApplicationName: "flowexecution",
        executionCode: JSON.stringify({
          flowId: this.flowId,
          version: !this.myReadonly ? a.flowVersion : this.flowVersion
        }),
        runType: "json",
        params: {},
        labels: {
          route: this.getCurrentDsslabels()
        },
        source: {
          projectName: this.$route.query.projectName,
          flowName: this.name
        },
        requestApplicationName: "flowexecution"
      }
      if (runFlag === 'rerun') {
        parmas.isReExecute = true
      }
      const exeUrl = '/dss/flow/entrance/execute'
      if ( runFlag === 'select') {
        parmas.nodeID = selectNodes.map(item=>item.key).join(',')
        parmas.isSelectedExecute = true
      }
      api.fetch(exeUrl, parmas).then((res) => {
        // 每次执行之后缓存工作流，关闭重新打开再接着获取状态

        this.workflowExecutorCache.push({
          flowId: this.flowId,
          execID: res.execID,
          taskID: res.taskID
        })
        // 查询执行节点的状态
        let execID = res.execID;
        let taskID = res.taskID;
        this.workflowTaskId = res.taskID;
        this.workflowExeteId = execID;
        this.queryWorkflowExecutor(execID, taskID)
      }).catch(() => {
        this.workflowIsExecutor = false;
      })
      this.workflowIsExecutor = true;
    },
    workflowStop() {
      clearTimeout(this.excuteTimer);
      clearTimeout(this.executorStatusTimer);
      // 清掉当前工作流执行的缓存
      this.workflowExecutorCache = this.workflowExecutorCache.filter((item) => {
        item.flowId !== this.flowId;
      });
      if (this.task_killing) {
        return this.$Message.error('请求已发出，请勿重复点击');
      }
      this.task_killing = true
      api.fetch(`/dss/flow/entrance/${this.workflowExeteId}/kill`, {taskID: this.workflowTaskId, labels: this.getCurrentDsslabels()}, 'get').then(() => {
        this.workflowIsExecutor = false;
        this.flowExecutorNode(this.workflowExeteId, true);
        this.task_killing = false;
      }).catch(() => {
        this.workflowIsExecutor = false;
        this.task_killing = false;
      })
    },
    queryWorkflowExecutor(execID, taskID) {
      this.retryTimes = this.retryTimes || 0;
      api.fetch(`/dss/flow/entrance/${execID}/status`,
        {
          taskID,
          labels: this.getCurrentDsslabels()
        }, 'get').then((res) => {
        this.retryTimes = 0
        this.flowExecutorNode(execID);
        // 根据执行状态判断是否轮询
        const status = res.status;
        if (status === 3) { // 停止状态轮询
          if (res.message) {
            this.$Message.error(res.message);
          }
          this.workflowIsExecutor = false;
          return
        }
        if (['Inited', 'Scheduled', 'Running'].includes(status)) {
          if (this.excuteTimer) {
            clearTimeout(this.excuteTimer);
            this.excuteTimer = null;
          }
          this.excuteTimer = setTimeout(() => {
            this.queryWorkflowExecutor(execID, taskID);
          }, 1000)
        } else {
          // Succees, Failed, Cancelled, Timeout
          this.workflowIsExecutor = false;
          if (status === 'Succeed') {
            this.$Notice.success({desc: this.$t('message.common.projectDetail.workflowRunSuccess')})
          }
          if (status === 'Failed') {
            this.$Notice.error({desc: this.$t('message.common.projectDetail.workflowRunFail')})
            this.flowExecutorNode(execID, true);
          }
          if (status === 'Cancelled') {
            this.$Notice.error({desc: this.$t('message.common.projectDetail.workflowRunCanceled')})
            this.flowExecutorNode(execID, true);
          }
          if (status === 'Timeout') {
            this.$Notice.error({desc: this.$t('message.common.projectDetail.workflowRunOvertime')})
            this.flowExecutorNode(execID, true);
          }
          // 清掉当前工作流执行的缓存
          this.workflowExecutorCache = this.workflowExecutorCache.filter((item) => {
            return item.flowId !== this.flowId;
          });
        }
      }).catch(() => {
        // 失败重试5次
        if (this.retryTimes < 5) {
          clearTimeout(this.excuteTimer);
          this.excuteTimer = null;
          this.excuteTimer = setTimeout(() => {
            this.retryTimes = this.retryTimes + 1;
            this.queryWorkflowExecutor(execID, taskID);
          }, 1000)
        } else {
          this.retryTimes = 0
          this.flowExecutorNode(execID, true);
        }
        this.$Notice.error({desc: this.$t('message.common.projectDetail.workflowRunFail')})
      })
    },
    flowExecutorNode(execID, end = false) {
      api.fetch(`/dss/flow/entrance/${execID}/execution`, {labels: this.getCurrentDsslabels()}, 'get').then((res) => {
        // 【0：未执行；1：运行中；2：已成功；3：已失败；4：已跳过】
        const actionStatus = {
          pendingJobs: {color: '#6A85A7', status: 0, iconType: '',
            colorClass: '', isShowTime: false, title: this.$t('message.workflow.Scheduled'), showConsole: false},
          runningJobs: {color: '#2E92F7', status: 1, iconType: 'status-loading',
            colorClass: {'executor-loading': true}, isShowTime: true, title: this.$t('message.workflow.Running'), showConsole: true},
          succeedJobs: {color: '#52C41A', status: 2,iconType: 'status-success',
            colorClass: {'executor-success': true}, isShowTime: false, title: this.$t('message.workflow.ExecuteSuccess'), showConsole: true},
          failedJobs: {color: '#FF4D4F', status: 3, iconType: 'status-fail',
            colorClass: {'executor-faile': true}, isShowTime: false, title: this.$t('message.workflow.ExecuteFailed'), showConsole: true},
          skippedJobs: {color: '#B3C1D3', status: 4, iconType: 'status-skip',
            colorClass: {'executor-skip': true}, isShowTime: false, title: this.$t('message.workflow.Skip'), showConsole: false}
        };
        // 获取节点的状态，如果没有执行完成继续查询
        const  data = res;
        Object.keys(data).forEach((key) => {
          // 如果当前工作流已经执行结束，还得获取状态到没有执行的节点为止
          if(end && key === 'runningJobs' && data[key].length > 0) {
            // 手动停掉执行和切换页面停止调接口
            this.executorStatusTimer = setTimeout(() => {
              this.flowExecutorNode(execID, true);
            }, 2000)
          }
          data[key].forEach((node) => {
            if (this.$refs.process) {
              let time = 0
              if (node.startTime) {
                time = node.nowTime - node.startTime
              }
              this.$refs.process.setNodeRunState(node.nodeID, {
                time: this.timeTransition(time),
                status: actionStatus[key].status,
                borderColor: actionStatus[key].color,
                iconType: actionStatus[key].iconType,
                colorClass: actionStatus[key].colorClass,
                isShowTime: actionStatus[key].isShowTime,
                title: actionStatus[key].title,
                showConsole: actionStatus[key].showConsole,
                execID: node.execID,
                taskID: node.taskID,
                isSvg: true
              }, this.myReadonly && !this.product)
            }
          })
        })
        if (end && data.failedJobs && data.failedJobs.length) {
          this.needReRun = true
        }
      })
    },
    timeTransition(time) {
      time =Math.floor(time / 1000)
      let hour = 0;
      let minute = 0;
      let second = 0;
      // let str ="00:00:00";
      // if (time < 0) return str;
      if (time > 60) {
        minute = Math.floor(time / 60);
        second = Math.floor(time % 60);
        if (minute >= 60) {
          hour = Math.floor(minute / 60);
          minute = Math.floor(minute % 60);
        } else {
          hour = 0;
        }
      } else {
        hour = 0;
        if (time == 60) {
          minute = 1;
          second = 0;
        } else {
          minute = 0;
          second = time;
        }
      }
      const addZero = (num) => {
        let result = num;
        if (num < 10) {
          result = `0${num}`
        }
        return result;
      }
      const timeResult = `${addZero(hour)}:${addZero(minute)}:${addZero(second)}`;
      time=0;
      return timeResult;
    },
    workflowPublishIsShow() {
      // 已经在发布不能再点击
      if(this.isFlowPubulish) return this.$Message.warning(this.$t('message.workflow.publishing'))
      this.pubulishShow = true;
      this.saveingComment = false;
      this.pubulishFlowComment = ''
    },
    showDiff() {
      this.pubulishShow = false;
      this.$refs.bottomTab.showPanel('version');
    },
    async workflowPublish() {
      if (this.saveingComment) {
        return
      }
      this.saveingComment = true
      // 发布之前先保存
      let a
      try {
        a = await this.autoSave(this.$t('message.workflow.Publishwork'), false);
      } catch (e) {
        this.pubulishShow = false;
        this.isFlowPubulish = false;
      }
      if (!a) {
        this.pubulishShow = false;
        this.isFlowPubulish = false;
        this.saveingComment = false;
        return;
      }      // 调用发布接口
      const params = {
        orchestratorId: this.orchestratorId,
        orchestratorVersionId: this.orchestratorVersionId,
        dssLabel: this.getCurrentDsslabels(),
        workflowId: Number(this.flowId),
        labels: {route: this.getCurrentDsslabels()},
        comment: this.pubulishFlowComment
      }
      // 记录工作流是否在发布
      this.isFlowPubulish = true;
      api.fetch(`/dss/workflow/publishWorkflow`, params, 'post').then((res) => {
        this.pubulishShow = false;
        // 发布之后需要轮询结果
        let queryTime = 0;
        this.saveingComment = false;
        this.checkResult(res.releaseTaskId, queryTime, 'publish');
        this.setTaskId(res.releaseTaskId);
      }).catch(() => {
        this.pubulishShow = false;
        this.isFlowPubulish = false;
        this.saveingComment = false;
        this.$Message.error(this.$t('message.common.projectDetail.publishFailed'));
      })
    },
    clickToolItem(item) {
      if (item.url){
        if(item.url.startsWith('http')){
          util.windowOpen(item.url);
        } else {
          this.$router.push({path: item.url, query: Object.assign({}, this.$route.query)});
        }
      }
    },
    // 发布和导出共用查询接口
    checkResult(id, timeoutValue, type = 'publish') {
      let typeName = this.$t('message.workflow.export')
      if (type === 'publish') {
        typeName = this.$t('message.workflow.process.publish')
      }
      this.timer = setTimeout(() => {
        timeoutValue += 2000;
        getPublishStatus(id, this.getCurrentDsslabels()).then((res) => {
          if (timeoutValue <= (10 * 60 * 1000)) {
            if (res.status === 'init' || res.status === 'running') {
              clearTimeout(this.timer);
              this.checkResult(id, timeoutValue, type);
            } else if (res.status === 'success') {
              clearTimeout(this.timer);
              this.isFlowPubulish = false;
              // 如果是导出成功需要下载文件
              if (type === 'export' && res.msg) {
                const url = module.data.API_PATH + 'dss/downloadFile/' + res.msg;
                const link = document.createElement('a');
                link.setAttribute('href', url);
                link.setAttribute('download', '');
                const evObj = document.createEvent('MouseEvents');
                evObj.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, true, false, 0, null);
                const flag = link.dispatchEvent(evObj);
                this.$nextTick(() => {
                  if (flag) {
                    this.$Message.success(this.$t('message.workflow.downloadTolocal'));
                  }
                });
              }
              this.$Message.success(this.$t('message.workflow.workflowSuccess', { name: typeName }));
              // 发布成功后，根工作流id会变化，导致修改工作流后保存的还是旧id
              this.refreshOpen()
            } else if (res.status === 'failed') {
              clearTimeout(this.timer);
              this.isFlowPubulish = false;
              this.$Modal.error({
                title: this.$t('message.workflow.workflowFail', { name: typeName }),
                content: `<p style="word-break: break-all;">${res.errorMsg}</p>`,
                width: 500,
                okText: this.$root.$t('message.workflow.publish.cancel'),
              });
              // 发布成功后，根工作流id会变化，导致修改工作流后保存的还是旧id
              this.refreshOpen()
            }
          } else {
            clearTimeout(this.timer);
            this.isFlowPubulish = false;
            this.$Message.warning(this.$t('message.common.projectDetail.workflowRunOvertime'));
          }
          // 扩展插件发布历史列表更新
          eventbus.emit('get_publish_status', res)
        }).catch(()=> {
          this.isFlowPubulish = false;
          this.refreshOpen()
        });
      }, 2000);
    },
    refreshOpen() {
      this.$emit('close')
      this.$emit('open')
    },
    dateFormatter(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    },
    exportWorkflow() {
      /*
      1.导出时，添加描述，和选择是否同步发版
      2.在导出之前得先保存工作流
      3.先调用导出接口，成功后再下载到本地
      */
      if(this.isFlowPubulish) return this.$Message.warning(this.$t('message.workflow.warning.api'))
      this.workflowExportShow = true;

    },
    async workflowExportOk() {
      const a = await this.autoSave(this.$t('message.workflow.Export'), false);
      if (!a) return
      this.isFlowPubulish = true;
      const params = {
        rootFlowID: Number(this.flowId),
        // projectVersionID: +this.projectVersionID,
        IOType: 'FLOW',
        comment: this.exportDesc,
        needChangeVersion: this.exportChangeVersion
      }
      api.fetch("export", params, 'post').then(() => {
        // let queryTime = 0;
        // this.checkResult(+this.projectVersionID, queryTime, 'export');
      })
    },
    // 删除工作流的线触发自动保存
    linkDelete() {
      const timerId = setTimeout(() => {
        this.autoSave('deleteLink', true);
        clearTimeout(timerId);
      }, 500);
    },
    linkAdd() {
      const timerId = setTimeout(() => {
        this.autoSave('addLink', true);
        clearTimeout(timerId);
      }, 500);
    },
    closeParamsBar() {
      // 关闭参数参数窗口
      this.nodebaseinfoShow = false;
    },
    // 获取控制台设置的参数信息
    getConsoleParams() {
      Promise.all([api.fetch('/configuration/getFullTreesByAppName', {
        engineType: 'spark',
        creator: 'nodeexecution',
      }, 'get'),
      api.fetch('/configuration/getFullTreesByAppName', {
        engineType: '通用设置',
        creator: '通用设置',
      }, 'get')]).then((res) => {
        this.consoleParams = res;
      }).catch(() => {

      })
    },
    // 获取工作空间名称
    getCurrentWorkspaceName() {
      const workspaceData = storage.get("currentWorkspace");
      return workspaceData ? workspaceData.name : ''
    },
    getTaskKey(){
      const username = this.getUserName();
      const key = `${username}-workflow-${this.orchestratorId}-taskId`;
      return key
    },
    setTaskId(taskId) {
      const key = this.getTaskKey();
      storage.set(key, taskId);
    },
    getTaskId(){
      const key = this.getTaskKey();
      return storage.get(key);
    },
    removeTaskId() {
      const key = this.getTaskKey();
      storage.remove(key);
    },
    onKeyUp(e) {
      const isDel = e.keyCode === 46 || e.keyCode === 8 && navigator.userAgent.indexOf('Mac') !== -1
      if (e.altKey && e.ctrlKey) { return }
      if (isDel && this.tabs[this.$parent.active].key === this.activeTabKey && e.target.nodeName!='INPUT' && e.target.nodeName!='TEXTAREA') {
        if (this.myReadonly) return
        let selectNodes = this.$refs.process.getSelectedNodes();
        const selectNodeLength = selectNodes.length
        if (selectNodeLength > 0) {
          if (selectNodeLength > 1) {
            this.allDelete();
          } else {
            this.nodeDelete(selectNodes[0])
          }
        }
      }
    },
    async checkLastPublish(cb) {
      const publishTaskId = this.getTaskId()
      if (publishTaskId && this.orchestratorId == this.$route.query.flowId) {
        let res
        try {
          res = await getPublishStatus(publishTaskId, this.getCurrentDsslabels())
        } catch (error) {
          this.removeTaskId()
        }
        if (res && res.status === 'running') {
          this.isFlowPubulish = true
          this.checkResult(publishTaskId, 0, 'publish')
          // 打开发布历史panel
          if (cb) {
            cb()
          }
        }
      }
    }
  }
}
</script>
<style src="./index.scss" lang="scss"></style>
