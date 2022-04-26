<template>
  <div>
    <ul class="we-progress-toolbar">
      <li
        @click="openPanel('log')">
        <Icon
          type="md-list-box"
          size="14"/>
        <span class="v-toolbar-icon">{{ $t('message.common.log') }}</span>
      </li>
      <!-- 扩展 -->
      <template v-for="(comp, index) in extComponents">
        <component
          :key="comp.name + index"
          :is="comp.component"
          :status="status"
          :application="application"
          :progress="progress"
          :execute="execute"
          @event-from-ext="eventFromExt"
        />
      </template>
    </ul>
  </div>
</template>
<script>
import plugin from '@dataspherestudio/shared/common/util/plugin'

const extComponents = plugin.emitHook('script_progress_toolbar') || []
export default {
  props: {
    status: String,
    application: String,
    progress: Object,
    execute: Object
  },
  data() {
    return {
      extComponents
    };
  },
  methods: {
    openPanel(type) {
      this.$emit('open-panel', type);
    },
    eventFromExt(evt) {
      if (evt && evt.callFn && typeof this[evt.callFn] === 'function') {
        this[evt.callFn](...evt.params)
      }
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .we-progress-toolbar {
    width: 40px;
    height: 100%;
    position: absolute;
    margin-left: -40px;
    padding-top: 10px;
    @include bg-color($light-base-color, $dark-submenu-color);
    border-right: 1px solid #dcdee2;
    @include border-color($border-color-base, $dark-submenu-color);
    li {
      @include font-color($light-text-color, $dark-text-color);
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 6px 2px;
      cursor: pointer;
      &:hover {
        @include font-color($primary-color, $dark-primary-color);
      }
    }
  }
</style>

