<template>
  <div v-if="isExternal" :style="styleExternalIcon" class="svg-external-icon svg-icon" v-on="$listeners" />
  <span v-else class="svg-container" :style="{color}">
    <svg :class="svgClass" :style="svgVerticalAlign" aria-hidden="true" v-on="$listeners">
      <use :xlink:href="iconName" />
    </svg>
  </span>

</template>

<script>
// doc: https://panjiachen.github.io/vue-element-admin-site/feature/component/svg-icon.html#usage

/**
 * @param {string} path
 * @returns {Boolean}
 */
function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export default {
  name: 'SvgIcon',
  props: {
    iconClass: {
      type: String,
      required: true
    },
    className: {
      type: String,
      default: ''
    },
    color: {
      type: String,
      default: 'currentColor'
    },
    verticalAlign: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: ''
    },
    height: {
      type: String,
      default: ''
    }
  },
  computed: {
    isExternal() {
      return isExternal(this.iconClass)
    },
    iconName() {
      return `#icon-${this.iconClass}`
    },
    svgClass() {
      if (this.className) {
        return 'svg-icon ' + this.className
      } else {
        return 'svg-icon'
      }
    },
    styleExternalIcon() {
      if (this.verticalAlign) {
        return {
          backgroundColor: this.color,
          mask: `url(${this.iconClass}) no-repeat 50% 50%`,
          '-webkit-mask': `url(${this.iconClass}) no-repeat 50% 50%`,
          'vertical-align': this.verticalAlign
        }
      } else {
        return {
          backgroundColor: this.color,
          mask: `url(${this.iconClass}) no-repeat 50% 50%`,
          '-webkit-mask': `url(${this.iconClass}) no-repeat 50% 50%`
        }
      }
    },
    svgVerticalAlign() {
      if (this.width && this.height) {
        // 某些特殊svg需要定制宽高
        return {
          'vertical-align': this.verticalAlign,
          'width': this.width,
          'height': this.height
        }
      } else {
        return {
          'vertical-align': this.verticalAlign
        }
      }
    }
  }
}
</script>

<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}

.svg-external-icon {
  mask-size: cover!important;
  display: inline-block;
}
</style>
