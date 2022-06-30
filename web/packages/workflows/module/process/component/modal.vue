<template>
  <div v-if="show" class="mask" @click.capture.self="close(closable)">
    <div class="modal-content" :style="{width:contentWidth, height:contentHeight}">
      <div class="modal-header">
        <span class="ui-modal-title-main">
          {{ title }}
        </span>
        <Icon type="md-close" size="18" @click="close" />
      </div>
      <div class="modal-body">
        <slot />
      </div>
      <div v-if="footerDisable" class="modal-footer">
        <Button type="primary" @click="comfirm">
          {{ comfirmText }}
        </Button>
        <Button v-if="closeText" type="error" @click="close">
          {{ closeText }}
        </Button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  model: {
    prop: 'show',
    event: 'parent-event'
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '400'
    },
    height: {
      type: String,
      default: 'auto'
    },
    show: {
      type: Boolean,
      default: true
    },
    comfirmText: {
      type: String,
      default: 'OK'
    },
    closeText: {
      type: String,
      default: ''
    },
    footerDisable: {
      type: Boolean,
      default: true
    },
    closable: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    contentWidth() {
      if (this.width) {
        return `${this.width}px`
      } else {
        return `400px`
      }
    },
    contentHeight() {
      if (this.height) {
        return `${this.height}px`
      } else {
        return `auto`
      }
    }
  },
  methods: {
    close(closable = true) {
      if (closable) {
        this.$emit('parent-event', false);
        this.$emit('onClose');
      }
    },
    comfirm() {
      this.$emit('parent-event', false);
      this.$emit('onConfirm');
    }
  }
}
</script>
<style lang="scss" scoped>
.mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.6);
    z-index: 999;
    overflow-y: auto;
    .modal-content {
        position: absolute;
        background-color: #fff;
        border-radius: 8px;
        padding: 6px 15px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -20%);
        margin-bottom: 50px;
        .modal-header {
            height: 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #e3e8ee;
        }
        .modal-body {
            padding: 12px 0;
            min-height: 60px;
            padding-bottom: 42px;
        }
        .modal-footer {
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            text-align: center;
            padding-bottom: 10px;
        }
    }
}
</style>

