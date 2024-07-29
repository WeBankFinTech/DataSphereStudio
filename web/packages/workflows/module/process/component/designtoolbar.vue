<template>
  <div>
    <template>
      <!-- 这里需要做控制，只读和发布的区别-->
      <div>
        <div v-if="!readonly">
          <div
            class="button"
            :title="$t('message.workflow.process.params')"
            ref="paramButton"
            @click.stop="handleClick('showParamView')"
          >
            <SvgIcon class="icon" icon-class="canshu" style="opacity: 0.65" />
            <span>{{ $t('message.workflow.process.params') }}</span>
          </div>
          <div class="devider" />
          <div
            class="button"
            ref="resourceButton"
            :title="$t('message.workflow.process.resource')"
            @click.stop="handleClick('showResourceView')"
          >
            <SvgIcon class="icon" icon-class="ziyuan" style="opacity: 0.65" />
            <span>{{ $t('message.workflow.process.resource') }}</span>
          </div>
          <div class="devider" />
          <div
            v-if="!workflowIsExecutor && viewMode !== 'table'"
            :title="$t('message.workflow.process.run')"
            class="button"
            @click="handleClick('clickswitch')"
          >
            <SvgIcon class="icon" icon-class="stop" color="#666" />
            <span>{{ $t('message.workflow.process.run') }}</span>
          </div>
          <div class="devider" v-if="!workflowIsExecutor && viewMode !== 'table'" />
          <div
            v-if="!workflowIsExecutor && viewMode !== 'table'"
            :title="$t('message.workflow.process.run')"
            class="button"
            @click="handleClick('clickswitch', 'select')"
          >
            <SvgIcon class="icon" icon-class="stop" color="#666" />
            <span>{{ $t('message.workflow.SelectRun') }}</span>
          </div>
          <div
            v-if="workflowIsExecutor && viewMode !== 'table'"
            :title="$t('message.workflow.process.stop')"
            class="button"
            @click="handleClick('clickswitch')"
          >
            <SvgIcon
              class="icon"
              className="stop"
              icon-class="stop-2"
              color="#666"
            />
            <span>{{ $t('message.workflow.process.stop') }}</span>
          </div>
          <div class="devider" v-if="needReRun && viewMode !== 'table'" />
          <div
            v-if="!workflowIsExecutor && needReRun && viewMode !== 'table'"
            class="button"
            @click="handleClick('reRun')"
          >
            <Icon class="icon" type="ios-refresh" color="#666" size="18" />
            <span>{{ $t('message.workflow.Rerun') }}</span>
          </div>
          <div class="devider" v-if="viewMode !== 'table'" />
          <div
            :title="$t('message.workflow.process.save')"
            class="button"
            @click="handleClick('handleSave')"
          >
            <SvgIcon class="icon" icon-class="baocun" style="opacity: 0.65" />
            <span>{{ $t('message.workflow.process.save') }}</span>
          </div>
          <div v-if="flowType === 'flow' && isMainFlow" class="devider" />
          <div
            v-if="associateGit && flowType === 'flow' && isMainFlow"
            :title="$t('message.workflow.process.submitgit')"
            class="button"
            @click="handleClick('handleSubmitGit')"
          >
            <SvgIcon v-if="!isFlowSubmit" class="icon" icon-class="commit" style="opacity: 0.65" />
            <Icon
                v-else
                type="ios-loading"
                size="18"
                class="public-splin-load"
              ></Icon>
            <span>{{ $t('message.workflow.process.submitgit') }}</span>
          </div>
          <div v-if="associateGit && flowType === 'flow' && isMainFlow" class="devider" />
        </div>
        <!-- 预留运维发布的区别-->
        <div v-if="publish">
          <div
            v-if="flowType === 'flow' && isMainFlow && !readonly"
            :title="isFlowSubmited ? $t('message.workflow.process.publish') : '请先提交代码后再发布'"
            :class="['button', isFlowSubmited ? '' : 'toolbar-diabled-item']"
            @click="handleClick('workflowPublishIsShow')"
          >
            <template v-if="!isFlowPubulish">
              <SvgIcon class="icon" icon-class="fabu" style="opacity: 0.65" />
              <span class="icon-label">{{ $t('message.workflow.process.publish') }}</span>
            </template>
            <Spin v-else class="public_loading">
              <Icon
                type="ios-loading"
                size="18"
                class="public-splin-load"
              ></Icon>
              <span>{{ $t('message.workflow.publishing') }}</span>
            </Spin>
          </div>
          <div v-for="toolItem in extraToolbar" :key="toolItem.name">
            <div class="devider"></div>
            <div class="button" @click.stop="clickToolItem(toolItem)">
              <SvgIcon class="icon" icon-class="ds-center" />
              <span>{{
                $t('message.workflow.process.gotoScheduleCenter')
              }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
    <!-- 生产中心保留执行-->
    <template v-if="readonly">
      <template v-if="product && isLatest">
        <div
          v-if="!workflowIsExecutor"
          :title="$t('message.workflow.process.run')"
          class="button"
          @click="handleClick('clickswitch')"
        >
          <SvgIcon class="icon" icon-class="stop" />
          <span>{{ $t('message.workflow.process.run') }}</span>
        </div>
        <div
          v-if="workflowIsExecutor"
          :title="$t('message.workflow.process.stop')"
          class="button"
          @click="handleClick('clickswitch')"
        >
          <SvgIcon class="icon" className="stop" icon-class="stop-2" />
          <span>{{ $t('message.workflow.process.stop') }}</span>
        </div>
      </template>
    </template>
    <div
      class="button view_mode_btn table_mode"
      :class="{active: viewMode === 'table'}"
      title="表格模式"
      @click.stop="handleClick('changeViewMode','table')"
    >
      <SvgIcon class="icon" icon-class="listview" style="opacity: 0.65" />
    </div>
    <div
      class="button view_mode_btn"
      :class="{active: viewMode !=='table'}"
      title="拖拽模式"
    >
      <SvgIcon class="icon" icon-class="dragmode" style="opacity: 0.65" @click.stop="handleClick('changeViewMode','')" />
    </div>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api'
import util from '@dataspherestudio/shared/common/util'

export default {
  props: {
    readonly: {
      type: Boolean,
      default: false,
    },
    workflowIsExecutor: {
      type: Boolean,
      default: false,
    },
    needReRun: {
      type: Boolean,
      default: false,
    },
    isFlowPubulish: {
      type: Boolean,
      default: false,
    },
    isFlowSubmit: {
      type: Boolean,
      default: false,
    },
    publish: {
      type: Boolean,
      default: false,
    },
    isLatest: {
      type: Boolean,
      default: true,
    },
    flowId: {
      type: Number,
    },
    type: {
      type: String,
    },
    viewMode: {
      type: String
    },
    product: {
      type: [Boolean],
      default: false,
    },
    flowStatus: {
      type: String,
      default: ''
    },
    associateGit: {
      type: Boolean,
      default: false,
    },
    isFlowSubmited: {
      type: Boolean,
      default: false,
    },
    isMainFlow: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      extraToolbar: [],
      flowType: 'flow'
    }
  },
  watch: {
    type(v) {
      this.flowType = v;
    }
  },
  mounted() {
    console.log(this, this.readonly, this.flowType, this.isMainFlow)
    if (!this.readonly) {
      this.getToolbarsConfig()
    }
  },
  methods: {
    getToolbarsConfig() {
      api
        .fetch(
          `/dss/workflow/getExtraToolBars`,
          {
            projectId: +this.$route.query.projectID,
            workflowId: this.flowId,
            labels: { route: this.getCurrentDsslabels() || 'dev' },
          },
          'post'
        )
        .then((res) => {
          this.extraToolbar = res.extraBars
        })
    },
    handleClick(action, arg) {
      switch (action) {
        case 'showParamView':
          this.$emit('click-itembar', 'showParamView')
          break
        case 'showResourceView':
          this.$emit('click-itembar', 'showResourceView')
          break
        case 'clickswitch':
          this.$emit('click-itembar', 'clickswitch', arg)
          break
        case 'handleSave':
          this.$emit('click-itembar', 'handleSave')
          break
        case 'reRun':
          this.$emit('click-itembar', 'reRun')
          break
        case 'workflowPublishIsShow':
          if(this.isFlowSubmited) {
            this.$emit('click-itembar', 'workflowPublishIsShow')
          }
          break
        case 'changeViewMode':
          this.$emit('click-itembar', 'changeViewMode', arg)
          break
        case 'handleSubmitGit':
          this.$emit('click-itembar', 'showSubmitGit', arg)
          break
        default:
          break
      }
    },
    clickToolItem(item) {
      if (item.url) {
        if (item.url.startsWith('http')) {
          util.windowOpen(item.url)
        } else {
          this.$router.push({
            path: item.url,
            query: Object.assign({}, this.$route.query),
          })
        }
      }
    },
  },
}
</script>
<style lang="less" scoped>
.designer .designer-toolbar .toolbar-diabled-item {
  color: #d2d3d5;
  .icon-label {
    color: #d2d3d5;
  }
}
</style>
