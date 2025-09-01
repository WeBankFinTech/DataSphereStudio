<template>
  <div class="editor" ref="editorId">
    <div style="height: 32px;">
      <div class="workbench-body-navbar">
        <div
          class="workbench-body-navbar-item"
          @click="undo">
          <Icon type="ios-undo" />
          <span class="navbar-item-name">{{ $t('message.scripts.editorDetail.navBar.undo') }}</span>
        </div>
        <div
          class="workbench-body-navbar-item"
          @click="redo">
          <Icon type="ios-redo" />
          <span class="navbar-item-name">{{ $t('message.scripts.editorDetail.navBar.redo') }}</span>
        </div>
        <div
          v-if="scriptType!=='hdfsScript' && scriptType!=='historyScript' && !readonly"
          class="workbench-body-navbar-group">
          <div
            v-show="!script.running"
            v-if="script.executable"
            class="workbench-body-navbar-item"
            title="F3"
            :class="{'disabled':loading}"
            @click.stop="run">
            <Icon type="ios-play" />
            <span class="navbar-item-name">{{ $t('message.scripts.editorDetail.navBar.play') }}</span>
          </div>
          <div
            v-show="script.running"
            v-if="script.executable"
            class="workbench-body-navbar-item"
            @click.stop="stop">
            <Icon
              type="md-square"
              style="color:red"/>
            <span class="navbar-item-name">{{ $t('message.scripts.editorDetail.navBar.stop') }}</span>
          </div>
          <div
            v-if="!script.readOnly && !isHdfs"
            class="workbench-body-navbar-item"
            title="Ctrl+S"
            @click="save">
            <Icon type="md-checkmark" />
            <span class="navbar-item-name">{{ $t('message.scripts.editorDetail.navBar.save') }}</span>
          </div>
          <div
            v-if="!script.readOnly && !isHdfs && isSupport"
            class="workbench-body-navbar-item"
            @click="config">
            <Icon type="ios-build" />
            <span class="navbar-item-name">{{ $t('message.scripts.editorDetail.navBar.config') }}</span>
          </div>
          <!-- 数据源按钮 -->
          <div
            class="workbench-body-navbar-item"
            v-if="
              $route.name == 'Home' &&
              !script.readOnly &&
                !isHdfs &&
                isSupport &&
                (script.application == 'jdbc' || script.application == 'ck')
            "
          >
            <Select
              placeholder="切换数据源"
              @on-change="dataSetSelect"
              class="dataSetSelect"
              clearable
              filterable
              v-model="dataSetValue"
              style="width: 250px; margin-left: 3px"
            >
              <Option
                v-for="item in dataSetList"
                :value="item.dataSourceName"
                :key="item.id"
              >{{ item.dataSourceName }}</Option
              >
            </Select>
          </div>
          <div
            class="workbench-body-navbar-item"
            @click="topFullScreen">
            <Icon :type="(containerInstance && containerInstance.isTopPanelFull)?'md-contract':'md-expand'" />
            <span class="navbar-item-name">{{ (containerInstance && containerInstance.isTopPanelFull) ? $t('message.scripts.constants.logPanelList.releaseFullScreen') : $t('message.scripts.constants.logPanelList.fullScreen') }}</span>
          </div>
          <template v-for="(comp, index) in extComponents">
            <component
              :key="comp.name + index"
              :is="comp.component"
              :script="script"
              :work="work" />
          </template>
        </div>
      </div>
    </div>
    <we-panel diretion="horizontal" @on-move-end="resizePanel" :class='["editor-content", {"margin-right-300": showConfig} ]'>
      <we-panel-item
        :index="1"
        :min="300"
      >
        <we-editor
          ref="editor"
          v-model="script.data"
          :language="script.lang"
          :id="script.id"
          :read-only="script.readOnly"
          :script-type="scriptType"
          :ext="script.ext"
          :file-path="work.filepath || script.id + work.filename"
          :application="script.application"
          :is-scriptis="$route.name == 'Home'"
          type="code"
          @on-operator="heartBeat"
          @on-run="run"
          @on-save="save"
          @open-db-table-suggest="openDbTableSuggest"
          @is-parse-success="changeParseSuccess"/>
      </we-panel-item>
      <we-panel-item
        :index="2"
        :disable="!showConfig"
        class="setting-panel"
        v-show="showConfig"
        :min="showConfig ? 100 : 8"
        :width="showConfig ? showConfigWidth : 8"
      >
        <setting
          ref="setting"
          :script="script"
          :work="work"
          @setting-close="settingClose"/>
      </we-panel-item>
    </we-panel>
  </div>
</template>
<script>
import { debounce } from 'lodash';
import setting from './setting.vue';
import api from '@dataspherestudio/shared/common/service/api';
import plugin from '@dataspherestudio/shared/common/util/plugin'
import { throttle } from 'lodash';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent';
import storage from '@dataspherestudio/shared/common/helper/storage';

const extComponents = plugin.emitHook('script_editor_top_tools') || []

export default {
  components: {
    setting,
  },
  inject: {
    containerInstance: {
      defaule: undefined
    }
  },
  props: {
    script: {
      type: Object,
      required: true,
    },
    work: {
      type: Object,
      required: true,
    },
    scriptType: {
      type: String,
      default: 'workspaceScript',
    },
    readonly: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      curTipKey: '',
      showConfig: false,
      showConfigWidth:8,
      loading: false,
      isParseSuccess: true,
      dataSetValue: this.work.dataSetValue,
      scriptUnsave: false,
      oldDataSetValue: null,
      dataSetList: this.work.dataSetList || [],
      extComponents
    };
  },
  computed: {
    isHdfs() {
      return this.work.filepath.indexOf('hdfs') === 0;
    },
    isSupport() {
      return this.script.executable;
    },
  },
  watch: {
    'work.unsave'(val) {
      if (!val) {
        this.$refs.setting.origin = JSON.stringify(this.script.params);
      }
    },
    showConfig() {
      // 更新代码宽度
      this.resizePanel()
    }
  },
  mounted() {
    elementResizeEvent.bind(this.$el, this.layout);
    if (this.work.dataSetList) {
      this.dataSetList = this.work.dataSetList
    }
    if (
      this.work.data &&
      this.work.data.params &&
      this.work.data.params.configuration &&
      this.work.data.params.configuration.runtime &&
      this.work.data.params.configuration.runtime[
        'wds.linkis.engine.runtime.datasource'
      ]
    ) {
      this.dataSetValue =
        this.work.data.params.configuration.runtime[
          'wds.linkis.engine.runtime.datasource'
        ]

      this.oldDataSetValue = this.dataSetValue
    }
    setTimeout(() => {
      let linenum = storage.get("revealline") || 0
      if (linenum && this.$refs.editor && this.$refs.editor.editor) {
        this.$refs.editor.editor.setPosition({lineNumber: linenum, column: 1});
        this.$refs.editor.editor.focus();
        this.$refs.editor.editor.revealLine(linenum);
        storage.remove("revealline")
      }
      this.handleConfigTip()
    },50)
    this.handleResize = debounce(this.handleResize, 200);
    window.addEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      this.showConfigWidth = this.getMaxKeyOrValueLength(this.script.params.variable)
    },

  getTextWidth(str = '') {
      if (!window.textWidthCache) {
        window.textWidthCache = document.createElement('span');
        window.textWidthCache.style.position = 'absolute';
        window.textWidthCache.style.visibility = 'hidden';
        window.textWidthCache.style.whiteSpace = 'nowrap'; // 防止换行影响宽度
        document.body.appendChild(window.textWidthCache);
      }
      const dom = window.textWidthCache;
      dom.textContent = str;
      const width = dom.offsetWidth;
      return width;
    },
    getMaxKeyOrValueLength(arr) {
      let maxLength = 0;
      arr.forEach(obj => {
        Object.keys(obj).forEach(key => {
          const keyLength = this.getTextWidth(key);
          const valueLength = this.getTextWidth(obj[key]);
          maxLength = Math.max(maxLength, keyLength, valueLength);
        });
      });
      const clientWidth = this.$refs.editorId.clientWidth || 1681;
      if (maxLength <= 40) {
        return Math.min(200, clientWidth * 0.8)
      }
      return Math.min(60 + maxLength*3.5, clientWidth * 0.8)
// 6    });
    },
    openDbTableSuggest() {
      let len = storage.get('all-db-tables-length', 'local')
      if (len && len > 30000) {
        this.$Notice.close('show-db-table-many-tip')
        this.$Notice.warning({
          duration: 0,
          name: 'show-db-table-many-tip',
          title: this.$t('message.scripts.propmpt'),
          desc: this.$t('message.scripts.largedatatip')
        })
      }
      eventbus.emit('open-db-table-suggest')
    },
    handleConfigTip () {
      if (this.script.params && this.script.params.variable && this.script.params.variable.length > 0) {
        let curName = this.script.fileName
        if (this.script.params.configuration && this.script.params.configuration.runtime && this.script.params.configuration.runtime.nodeName) {
          curName = this.script.params.configuration.runtime.nodeName
        }
        // console.log('this.script.params.variable', this.script.params.variable)
        this.showConfigWidth = this.getMaxKeyOrValueLength(this.script.params.variable)
        this.showConfig = true;
      }
    },
    // handleConfigTip () {
    //   if (this.script.params && this.script.params.variable && this.script.params.variable.length > 0) {
    //     let curName = this.script.fileName
    //     if (this.script.params.configuration && this.script.params.configuration.runtime && this.script.params.configuration.runtime.nodeName) {
    //       curName = this.script.params.configuration.runtime.nodeName
    //     }
    //     this.showConfig = true;
    //     this.curTipKey =  'showConfigTip_' + curName + '_'+ this.script.id
    //     let curTipData = null
    //     if(sessionStorage.getItem(this.curTipKey)) {
    //         curTipData = JSON.parse(sessionStorage.getItem(this.curTipKey))
    //     }
    //     let curTipNames = [];
    //     if(sessionStorage.getItem('showConfigName')) {
    //         curTipNames = JSON.parse(sessionStorage.getItem('showConfigName'))
    //     }
    //     // 当前展示提示数等于3条后，不再追加提示
    //     // 初次打开，缓存数据为空，提示
    //     // 没有关闭提示，关闭脚本后再次打开，不重复提示
    //     // 关闭提示，再次打开，不提示
    //     if (curTipNames.length <=2 && (curTipData === null ||  (curTipData.curShowStatus !== '1' && curTipData.showable === '1'))) {
    //       const tipData = {
    //         curShowStatus: '1',  // 1：当前正在展示，0：当前未展示
    //         showable: '1', // 1：可以展示，0：不能展示
    //       }
    //       sessionStorage.setItem(this.curTipKey, JSON.stringify(tipData));
    //       curTipNames.push(this.curTipKey);
    //       sessionStorage.setItem('showConfigName', JSON.stringify(curTipNames));
    //       this.$Notice.open({
    //           desc: `${curName}脚本存在已配置的自定义参数，请知悉`,
    //           duration: 0,
    //           name: this.curTipKey,
    //           onClose: (v) => {
    //             tipData.curShowStatus = '0';
    //             tipData.showable = '0';
    //             sessionStorage.setItem(this.curTipKey, JSON.stringify(tipData));
    //             curTipNames = JSON.parse(sessionStorage.getItem('showConfigName'))
    //             const newArray = curTipNames.filter(item => item !== this.curTipKey);
    //             sessionStorage.setItem('showConfigName', JSON.stringify(newArray));
    //           }
    //       });
    //     }
    //   }
    // },
    dataSetSelect(v) {
      // 模拟未保存状态 旧数据源为初始化时的或保存后的数据源，新数据源双向绑定的dataSetValue
      // 当新旧数据源不同时，用scriptUnsave暂存此时保存状态（可能为true或false），然后切换保存状态为未保存
      // 当切换新旧数据源相同时，切换保存状态为scriptUnsave
      this.work.dataSetValue = v
      if (this.dataSetValue === this.oldDataSetValue) {
        this.work.unsave = this.scriptUnsave
      } else {
        this.scriptUnsave = this.work.unsave
        this.work.unsave = true
      }
    },
    'Workbench:insertValue'(args) {
      if (args.id === this.script.id) {
        this.$refs.editor.insertValueIntoEditor(args.value);
      }
    },
    resizePanel() {
      this.$nextTick(() => {
        this.$refs.editor.layout()
      })
    },
    // 点击全屏展示
    topFullScreen() {
      if(this.containerInstance) {
        let isFull = this.containerInstance.isTopPanelFull;
        this.containerInstance.panelControl(!isFull ? 'fullScreen' : 'releaseFullScreen')
      }
    },
    undo() {
      this.$refs.editor.undo();
    },
    redo() {
      this.$refs.editor.redo();
    },
    async run() {
      if (this.loading || this.killing) return this.$Message.warning(this.$t('message.scripts.constants.warning.api'));
      if (this.script.running) return this.$Message.warning(this.$t('message.scripts.editorDetail.warning.running'));
      let selectCode = this.$refs.editor.getValueInRange() || this.script.data;
      if (!selectCode) {
        return this.$Message.warning(this.$t('message.scripts.editorDetail.warning.emptyCode'));
      }
      try {
        this.loading = true;
        let validRepeat = await this.validateRepeat();
        if (!validRepeat) {
          this.loading = false;
          return this.$Message.warning(this.$t('message.scripts.editorDetail.warning.invalidArgs'));
        }
      } catch (error) {
      }
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.loading = false;
      }, 5000);
      this.$refs.editor.deltaDecorations(selectCode, () => {
        this.loading = true;
        this.$emit('on-run', {
          code: selectCode,
          id: this.script.id,
          dataSetValue: this.dataSetValue,
        }, (status) => {
          // status是start表示已经开始执行
          let list = ['execute', 'error', 'start', 'downgrade'];
          if (list.indexOf(status) > -1) {
            this.loading = false;
          }
        });
      });
    },
    stop() {
      if (this.killing) return this.$Message.warning(this.$t('message.scripts.constants.warning.api'));
      this.killing = true;
      this.$emit('on-stop', () => {
        this.killing = false;
      });
    },
    save: debounce(async function() {
      if (this.work && this.work.unsave) {
        let valid = await this.validateRepeat();
        if (!valid) return this.$Message.warning(this.$t('message.scripts.editorDetail.warning.invalidArgs'));
        this.$refs.editor.save();
        this.$emit('on-save', '', this.dataSetValue)
        this.oldDataSetValue = this.dataSetValue
        this.scriptUnsave = this.work.unsave
      } else {
        this.$Message.warning(this.$t('message.scripts.editorDetail.warning.unchange'));
      }
    }, 500),
    config() {
      this.showConfigWidth = this.getMaxKeyOrValueLength(this.script.params.variable)
      this.showConfig = !this.showConfig;
    },
    settingClose() {
      this.showConfig = false;
    },
    heartBeat: throttle(() => {
      api.fetch('/user/heartbeat', 'get');
    }, 60000),
    // 对this.script.params.variable里面是否存在key重复性进行校验
    // 这里要注意，html结构不能用v-if，只能用v-show，让设置模块处于要渲染状态
    // 否则会需要使用nextTick中await，会发生打开后没渲染的情况。
    async validateRepeat() {
      const setting = this.$refs.setting;
      let valid = true;
      // 当没有自定义参数的时候不做判断
      if (this.script.params.variable.length) {
        // 表单验证返回是个promise，所以需要用await
        valid = await setting.$refs.customVariable.$refs.form.$refs.dynamicForm.validate((valid) => {
          // 如果验证没通过，要打开setting页面
          if (!valid) {
            this.showConfig = true;
          }
          return valid;
        });
      }
      if (setting.$refs.envVariable && this.script.params.configuration.runtime.env.length) {
        valid = await setting.$refs.envVariable.$refs.form.$refs.dynamicForm.validate((valid) => {
          // 如果验证没通过，要打开setting页面
          if (!valid) {
            this.showConfig = true;
          }
          return valid;
        });
      }
      return valid;
    },
    changeParseSuccess(flag) {
      this.isParseSuccess = flag;
    },

    layout() {
      if (this.$refs.editor&&this.$refs.editor.editor) {
        this.$refs.editor.editor.layout();
      }
    }
  },
  beforeDestroy: function() {
    // const curTipNames = JSON.parse(sessionStorage.getItem('showConfigName'))
    // const curTipData = JSON.parse(sessionStorage.getItem(this.curTipKey))
    // if(this.$Notice && this.curTipKey && curTipData && curTipData['curShowStatus'] === '1') {
    //     this.$Notice.close(this.curTipKey);
    //     const tipData = {
    //       curShowStatus: '0',
    //       showable: '1',
    //     }
    //     sessionStorage.setItem(this.curTipKey, JSON.stringify(tipData));
    //     const newArray = curTipNames.filter(item => item !== this.curTipKey);
    //     sessionStorage.setItem('showConfigName', JSON.stringify(newArray));
    // }
    elementResizeEvent.unbind(this.$el);
    window.removeEventListener('resize', this.handleResize)
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .editor {
    height: 100%;
    ::v-deep.we-panel.we-panel-horizontal .we-panel-dash  {
      // 防止被外层.we-panel.we-panel-vertical .we-panel-dash覆盖
      cursor: ew-resize;
    }
    .editor-content {
      height: calc(100% - 32px);
      display: flex;
      @include bg-color($light-base-color, $dark-menu-base-color);
    }
    .setting-panel {
      overflow-x: auto;
    }
  }
  .workbench-body-navbar {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 2;
    @include bg-color($light-base-color, $dark-menu-base-color);
    padding: 0 4px;
    border-bottom: $border-width-base $border-style-base $border-color-base;
    @include border-color($border-color-base, $dark-menu-base-color);
    height: 32px;
    line-height: 32px;
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
  }
  .workbench-body-navbar-group {
    display: flex;
  }
  .workbench-body-navbar-item {
    margin: 0 16px;
    cursor: pointer;
    @include font-color($light-text-color, $dark-text-color);
    &.disabled {
      @include font-color($light-text-desc-color, $dark-text-desc-color);
    }
    &:hover {
      @include font-color($primary-color, $dark-primary-color);
        &.disabled {
          @include font-color($light-text-desc-color, $dark-text-desc-color);
      }
    }
    .ivu-icon {
      font-size: 16px;
    }
    .navbar-item-name {
      margin-left: 1px;
    }
  }
</style>

