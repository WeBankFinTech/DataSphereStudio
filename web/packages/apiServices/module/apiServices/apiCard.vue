<template>
  <div>
    <div class="content_item">
      <div class="title_item">
        <slot name="title">
          <div class="title" :title="title.aliasName + '\n' + title.name">
            <SvgIcon
              :style="'font-size: ' + titleIconSizeRes"
              :color="title.iconColor ? title.iconColor : '#1296db'"
              :icon-class="title.icon ? title.icon : 'api'"
            />
            <span>{{ title.aliasName ? title.aliasName : title.name }}</span>
          </div>
        </slot>
        <slot name="runStatus">
          <div class="runStatus" :style="`color: ${status.color}`">
            <SvgIcon
              :style="'font-size: ' + statusIconSizeRes"
              :color="status.color"
              :icon-class="status.icon ? title.icon : 'fi-radio-on2'"
            />
            <span>{{ status.text }}</span>
          </div>
        </slot>
      </div>
      <slot>
        <div class="desc" :title="desc">描述：{{ desc ? desc : '--' }}</div>
        <div class="version" v-if="title.versionVos">当前运行中版本：{{title.versionVos && title.versionVos[0] && title.versionVos[0].version ? title.versionVos[0].version : '--'}} <Tag  v-if="title.existNewerVersion" color="red">有新版本</Tag></div>
      </slot>
      <slot name="floor">
        <div class="floor">
          <Button :disabled="disabled" @click="holderButton" class="floor_button" :type="button.type">{{ button.text }}</Button>
          <div class="dropdown">
            <slot name="dropdown">
            </slot>
          </div>
        </div>
      </slot>
    </div>
  </div>
</template>
<script>
import i18n from '@dataspherestudio/shared/common/i18n';
export default {
  props: {
    title: {
      // 标题
      type: Object,
      default: () => {
        return {
          name: i18n.t('message.apiServices.istitle'),// 标题名称
          icon: "api",// 标题icon
          iconColor: "#1296db",// icon颜色
          iconSize: "2rem"// icon大小
        }
      }
    },
    disabled: {
      // 按钮
      type: Boolean,
      default: false,
    },
    status: {
      // 状态
      type: Object,
      default: () => {
        return {
          color: "#1296db",// 运行状态颜色
          text: "Running",// 运行状态描述
          icon: "fi-radio-on2",// 运行状态icon
          iconSize: "1rem",// 运行状态icon大小
        }
      }
    },
    desc: {
      // 描述
      type: String,
      default: i18n.t('message.apiServices.isdesc')
    },
    button: {
      // 底部按钮
      type: Object,
      default: () => {
        return {
          type: "primary",// 按钮类型
          text: i18n.t('message.apiServices.confirm'),// 按钮文本
        }
      }
    },
  },
  data() {
    return {};
  },
  computed: {
    titleIconSizeRes() {
      if(!this.title.iconSize) return '2rem';
      if (typeof this.title.iconSize === "number") {
        return `${this.title.iconSize}px`;
      } else {
        return `${this.title.iconSize}`;
      }
    },
    statusIconSizeRes() {
      if(!this.status.iconSize) return '1rem';
      if (typeof this.status.iconSize === "number") {
        return `${this.status.iconSize}px`;
      } else {
        return `${this.status.iconSize}`;
      }
    }
  },
  methods: {
    // 点击主按钮
    holderButton() {
      this.$emit('onButton')
    },
    // 点击下拉菜单项
    dropdownSelect(e) {
      this.$emit('onDropdownSelect', e)
    }
  }
};
</script>

<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.content_item {
  max-width: 375px;
  box-sizing: border-box;
  box-shadow: 0 0 5px 3px $shadow-color;
  padding: 10px 15px 15px;
  .title_item {
    display: flex;
    align-items: $align-center;
    justify-content: space-between;
    .runStatus,
    .title {
      display: flex;
      align-items: $align-center;
      > span:last-of-type {
        margin-left: 5px;
      }
    }
    .runStatus {
      flex: none;
    }
    .title {
      font-size: $font-size-base;
      font-weight: 700;
      box-sizing: border-box;
      padding-right: 5px;
      overflow: hidden;
      flex: 1;
      > span:last-of-type {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
      }
    }
  }
  .desc {
    width: 100%;
    // font-size: 0.65rem;
    // margin: 8px 0;
    // word-break: break-all; /* 单词内部也可断行 */
    // word-wrap: break-word; /* 允许单词内部断行，并自动溢出，即换行 */
    // text-indent: 2em;

    // 最多两行 多的在第二行最后显示...
    // white-space: nowrap;
    // overflow: hidden;
    // text-overflow: ellipsis;
    // display: -webkit-box;
    // -webkit-line-clamp: 2;
    // -webkit-box-orient: vertical;

    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-bottom: 4px;
    margin-top: 4px;
    height: 36px;
    word-wrap: break-word;   /* 在需要时断开字符 */
    word-break: break-all;  /* 在需要时断开单词 */
  }
  .version {
    font-size: 0.65rem;
    padding: 0px 0 8px 0;
    height: 34px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    word-break: break-all; /* 单词内部也可断行 */
    word-wrap: break-word; /* 允许单词内部断行，并自动溢出，即换行 */
  }
  .floor {
    text-align: center;
    position: relative;
    .floor_button {
      width: $percent-half;
    }
    .dropdown {
      position: absolute;
      top: $percent-half;
      right: 0;
      z-index: $zindex-select;
      transform: translateY(-50%);
      ::v-deep.ivu-select-dropdown {
        max-width: none;
      }
    }
  }
}
</style>
