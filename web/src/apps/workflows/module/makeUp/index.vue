<template>
  <div>
    <!-- 头部工具栏 -->
    <div class="header-tool">
      <div
        class="button"
        :title="$t('message.workflow.process.params')"
        ref="paramButton"
        @click.stop="showParamView">
        <SvgIcon class="icon" icon-class="params" color="#666"/>
        <span>{{$t('message.workflow.process.params')}}</span>
      </div>
      <div class="devider" />
      <div
        class="button"
        ref="resourceButton"
        :title="$t('message.workflow.process.resource')"
        @click.stop="showResourceView">
        <SvgIcon class="icon" icon-class="fi-export" color="#666"/>
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
      <div
        v-if="workflowIsExecutor"
        :title="$t('message.workflow.process.stop')"
        class="button"
        @click="clickswitch">
        <SvgIcon class="icon" className='stop' icon-class="stop-2"/>
        <span>{{$t('message.workflow.process.stop')}}</span>
      </div>
      <div class="devider" />
      <div
        :title="$t('message.workflow.process.save')"
        class="button"
        @click="handleSave">
        <SvgIcon class="icon" icon-class="save-2" color="#666"/>
        <span>{{$t('message.workflow.process.save')}}</span>
      </div>
      <div class="devider" />
      <div
        :title="$t('message.workflow.process.publish')"
        class="button"
        @click="workflowPublishIsShow">
        <template v-if="!isFlowPubulish">
          <SvgIcon class="icon" icon-class="publish" color="#666"/>
          <span>{{$t('message.workflow.process.publish')}}</span>
        </template>
        <Spin v-else class="public_loading">
          <Icon type="ios-loading" size=18 class="public-splin-load"></Icon>
          <span>{{$t('message.workflow.publishing')}}</span>
        </Spin>
      </div>
    </div>
    <!-- 组合编排有步骤条 -->
    <div v-if="currentVal.orchestratorMode === ORCHESTRATORMODES.CONSTSTORCHESTRATOR" class="mutiple">
      <Steps class="step-bar" :current="1">
        <Step title="visualis"></Step>
        <Step title="qualitis"></Step>
        <Step title="邮件发送"></Step>
      </Steps>
      <ButtonGroup class="button-bar">
        <Button type="primary" disabled>上一步</Button>
        <Button type="primary">下一步</Button>
      </ButtonGroup>
    </div>
    <iframe></iframe>
  </div>
</template>
<script>
import { ORCHESTRATORMODES } from '@/common/config/const.js';
export default {
  props: {
    currentVal: {
      type: Object,
      defalut: () => {}
    }
  },
  data() {
    return {
      isFlowPubulish: false,
      workflowIsExecutor: false,
      ORCHESTRATORMODES
    }
  },
  methods: {
    showParamView() {},
    showResourceView() {},
    clickswitch() {},
    handleSave() {},
    workflowPublishIsShow(){},
  }
}
</script>
<style src="./index.scss" lang="scss" scoped>

</style>
