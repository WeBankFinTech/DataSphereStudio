<template>
  <div class="editor">
    <div style="height: 32px">
      <div class="workbench-body-navbar">
        <div class="workbench-body-navbar-item" @click="undo">
          <Icon type="ios-undo" />
          <span class="navbar-item-name">{{
            $t('message.scripts.editorDetail.navBar.undo')
          }}</span>
        </div>
        <div class="workbench-body-navbar-item" @click="redo">
          <Icon type="ios-redo" />
          <span class="navbar-item-name">{{
            $t('message.scripts.editorDetail.navBar.redo')
          }}</span>
        </div>
        <div
          v-if="
            scriptType !== 'hdfsScript' &&
              scriptType !== 'historyScript' &&
              !readonly
          "
          class="workbench-body-navbar-group"
        >
          <div
            v-show="!script.running"
            v-if="script.executable"
            class="workbench-body-navbar-item"
            title="F3"
            :class="{ disabled: loading }"
            @click.stop="run"
          >
            <Icon type="ios-play" />
            <span class="navbar-item-name">{{
              $t('message.scripts.editorDetail.navBar.play')
            }}</span>
          </div>
          <div
            v-show="script.running"
            v-if="script.executable"
            class="workbench-body-navbar-item"
            @click.stop="stop"
          >
            <Icon type="md-square" style="color: red" />
            <span class="navbar-item-name">{{
              $t('message.scripts.editorDetail.navBar.stop')
            }}</span>
          </div>
          <div
            v-if="!script.readOnly && !isHdfs"
            class="workbench-body-navbar-item"
            title="Ctrl+S"
            @click="save"
          >
            <Icon type="md-checkmark" />
            <span class="navbar-item-name">{{
              $t('message.scripts.editorDetail.navBar.save')
            }}</span>
          </div>
          <div
            v-if="!script.readOnly && !isHdfs && isSupport"
            class="workbench-body-navbar-item"
            @click="config"
          >
            <Icon type="ios-build" />
            <span class="navbar-item-name">{{
              $t('message.scripts.editorDetail.navBar.config')
            }}</span>
          </div>
          <!-- 数据源按钮 -->
          <div
            class="workbench-body-navbar-item"
            v-if="
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
              v-model="dataSetValue"
              style="width: 150px; margin-left: 3px"
            >
              <Option
                v-for="item in dataSetList"
                :value="item.dataSourceName"
                :key="item.id"
              >{{ item.dataSourceName }}</Option
              >
            </Select>
          </div>
          <div class="workbench-body-navbar-item" @click="topFullScreen">
            <Icon
              :type="
                containerInstance && containerInstance.isTopPanelFull
                  ? 'md-contract'
                  : 'md-expand'
              "
            />
            <span class="navbar-item-name">{{
              containerInstance && containerInstance.isTopPanelFull
                ? $t('message.scripts.constants.logPanelList.releaseFullScreen')
                : $t('message.scripts.constants.logPanelList.fullScreen')
            }}</span>
          </div>
          <template v-for="(comp, index) in extComponents">
            <component
              :key="comp.name + index"
              :is="comp.component"
              :script="script"
              :work="work"
            />
          </template>
        </div>
      </div>
    </div>
    <div class="editor-content">
      <we-editor
        ref="editor"
        v-model="script.data"
        :language="script.lang"
        :id="script.id"
        :read-only="script.readOnly"
        :script-type="scriptType"
        :application="script.application"
        type="code"
        @on-operator="heartBeat"
        @on-run="run"
        @on-save="save"
        @is-parse-success="changeParseSuccess"
      />
      <setting
        ref="setting"
        v-show="showConfig"
        :script="script"
        :work="work"
        @setting-close="settingClose"
      />
    </div>
  </div>
</template>
<script>
import setting from './setting.vue'
import api from '@dataspherestudio/shared/common/service/api'
import plugin from '@dataspherestudio/shared/common/util/plugin'
import { throttle } from 'lodash'
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent'

const extComponents = plugin.emitHook('script_editor_top_tools') || []

export default {
  components: {
    setting,
  },
  inject: {
    containerInstance: {
      defaule: undefined,
    },
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
    },
  },
  data() {
    return {
      showConfig: false,
      loading: false,
      isParseSuccess: true,
      dataSetValue: '',
      scriptUnsave: false,
      oldDataSetValue: null,
      dataSetList: [],
      extComponents,
    }
  },
  computed: {
    listenResource() {
      return this.script.running
    },
    isHdfs() {
      return this.work.filepath.indexOf('hdfs') === 0
    },
    isSupport() {
      return this.script.executable
    },
  },
  watch: {
    listenResource(val) {
      if (!val) {
        api
          .fetch(
            '/jobhistory/list',
            {
              pageSize: 100,
              status: 'Running,Inited,Scheduled',
            },
            'get'
          )
          .then((rst) => {
            this.dispatch('Footer:updateRunningJob', rst.tasks.length)
          })
          .catch(() => {})
      }
    },
    'work.unsave'(val) {
      if (!val) {
        this.$refs.setting.origin = JSON.stringify(this.script.params)
      }
    },
  },
  mounted() {
    elementResizeEvent.bind(this.$el, this.layout)
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
  },
  methods: {
    dataSetSelect() {
      // 模拟未保存状态 旧数据源为初始化时的或保存后的数据源，新数据源双向绑定的dataSetValue
      // 当新旧数据源不同时，用scriptUnsave暂存此时保存状态（可能为true或false），然后切换保存状态为未保存
      // 当切换新旧数据源相同时，切换保存状态为scriptUnsave
      if (this.dataSetValue === this.oldDataSetValue) {
        this.work.unsave = this.scriptUnsave
      } else {
        this.scriptUnsave = this.work.unsave
        this.work.unsave = true
      }
    },
    'Workbench:insertValue'(args) {
      if (args.id === this.script.id) {
        this.$refs.editor.insertValueIntoEditor(args.value)
      }
    },
    // 点击全屏展示
    topFullScreen() {
      if (this.containerInstance) {
        let isFull = this.containerInstance.isTopPanelFull
        this.containerInstance.panelControl(
          !isFull ? 'fullScreen' : 'releaseFullScreen'
        )
      }
    },
    undo() {
      this.$refs.editor.undo()
    },
    redo() {
      this.$refs.editor.redo()
    },
    async run() {
      // if (!this.isParseSuccess) return this.$Message.warning('代码中有语法错误，请检查后再试！');
      if (this.script.running)
        return this.$Message.warning(
          this.$t('message.scripts.editorDetail.warning.running')
        )
      let selectCode = this.$refs.editor.getValueInRange() || this.script.data
      let validRepeat = await this.validateRepeat()
      this.$refs.editor.deltaDecorations(selectCode, () => {
        // if (!flag) {
        //     return this.$Message.warning('代码中有语法错误，请检查后再试！');
        // }
        if (!validRepeat)
          return this.$Message.warning(
            this.$t('message.scripts.editorDetail.warning.invalidArgs')
          )
        if (this.loading)
          return this.$Message.warning(
            this.$t('message.scripts.constants.warning.api')
          )
        if (!selectCode) {
          return this.$Message.warning(
            this.$t('message.scripts.editorDetail.warning.emptyCode')
          )
        }
        this.loading = true
        this.$emit(
          'on-run',
          {
            code: selectCode,
            id: this.script.id,
            dataSetValue: this.dataSetValue,
          },
          (status) => {
            // status是start表示已经开始执行
            let list = ['execute', 'error', 'start', 'downgrade']
            if (list.indexOf(status) > -1) {
              this.loading = false
            }
          }
        )
      })
    },
    stop() {
      if (this.loading)
        return this.$Message.warning(
          this.$t('message.scripts.constants.warning.api')
        )
      this.loading = true
      this.$emit('on-stop', () => {
        this.loading = false
      })
    },
    async save() {
      // debugger
      let valid = await this.validateRepeat()
      if (!valid)
        return this.$Message.warning(
          this.$t('message.scripts.editorDetail.warning.invalidArgs')
        )
      this.$refs.editor.save()
      this.$emit('on-save', '', this.dataSetValue)
      this.oldDataSetValue = this.dataSetValue
      this.scriptUnsave = this.work.unsave
    },
    config() {
      this.showConfig = !this.showConfig
    },
    settingClose() {
      this.showConfig = false
    },
    heartBeat: throttle(() => {
      api.fetch('/user/heartbeat', 'get')
    }, 60000),
    // 对this.script.params.variable里面是否存在key重复性进行校验
    // 这里要注意，html结构不能用v-if，只能用v-show，让设置模块处于要渲染状态
    // 否则会需要使用nextTick中await，会发生打开后没渲染的情况。
    async validateRepeat() {
      const setting = this.$refs.setting
      let valid = true
      // 当没有自定义参数的时候不做判断
      if (this.script.params.variable.length) {
        // 表单验证返回是个promise，所以需要用await
        valid =
          await setting.$refs.customVariable.$refs.form.$refs.dynamicForm.validate(
            (valid) => {
              // 如果验证没通过，要打开setting页面
              if (!valid) {
                this.showConfig = true
              }
              return valid
            }
          )
      }
      if (
        setting.$refs.envVariable &&
        this.script.params.configuration.runtime.env.length
      ) {
        valid =
          await setting.$refs.envVariable.$refs.form.$refs.dynamicForm.validate(
            (valid) => {
              // 如果验证没通过，要打开setting页面
              if (!valid) {
                this.showConfig = true
              }
              return valid
            }
          )
      }
      return valid
    },
    changeParseSuccess(flag) {
      this.isParseSuccess = flag
    },

    layout() {
      if (this.$refs.editor && this.$refs.editor.editor) {
        this.$refs.editor.editor.layout()
      }
    },
  },
  beforeDestroy: function () {
    elementResizeEvent.unbind(this.$el)
  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.editor {
  height: 100%;
  .editor-content {
    height: calc(100% - 32px);
    display: flex;
    @include bg-color($light-base-color, $dark-menu-base-color);
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
