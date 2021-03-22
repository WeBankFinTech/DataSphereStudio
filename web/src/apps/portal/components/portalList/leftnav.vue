<template>
  <div>
    <div class="left-menu">
      <!-- 控制“全部收起”和“全部展开” -->
      <div class="toggle-none" :style="{left: getIconLeft()}" @click="toggle('none')">
        <Icon :type="isAllMode ? 'md-arrow-back' : 'md-arrow-forward'" />
      </div>
      <!-- 控制“展示部分”和“全部展开” -->
      <div class="toggle-part" :style="{left: getIconLeft()}" @click="toggle('part')">
        <Icon :type="isAllMode ? 'ios-arrow-back' : 'ios-arrow-forward'" />
      </div>
      <Menu v-if="leftNavData.items && leftNavData.items.length > 0" class="side-menu" :open-names="openMenus()" :style="{width: getIconLeft(), display: isNoneMode ? 'none' : 'inline-block'}" @on-select="setCurrentPage">
        <!-- 判断是否有子节点 -->
        <template v-for="items in leftNavData.items">
          <MenuItem v-if="items.subItems.length <= 0" :title="items.title" :name="items.title" :key="items.title">
            <Icon class="nemu-icon" :type="items.icon" />
            <span class="menu-text">{{isAllMode ? items.name : ''}}</span>
          </MenuItem>
          <Submenu v-if="items.subItems.length > 0" :title="items.title" :name="items.title" :key="items.title" >
            <template slot="title">
              <Icon class="nemu-icon" :type="items.icon" />
              <span class="menu-text">{{isAllMode ? items.name : ''}}</span>
            </template>
            <template v-for="item in items.subItems">
              <MenuItem v-if="item.subItems.length <= 0" :title="item.title" :name="item.title" :key="item.title">
                <Icon class="nemu-icon" :type="item.icon" />
                <span class="menu-text">{{isAllMode ? item.name : ''}}</span>
              </MenuItem>
              <Submenu v-if="item.subItems.length > 0" :title="item.title" :name="item.title" :key="item.title" >
                <template slot="title">
                  <Icon class="nemu-icon" :type="item.icon" />
                  <span class="menu-text">{{isAllMode ? item.name : ''}}</span>
                </template>
                <MenuItem :name="i.title" :key="i.title" :title="i.title" v-for="i in item.subItems">
                  <Icon class="nemu-icon" :type="i.icon" />
                  <span class="menu-text">{{isAllMode ? i.name : ''}}</span>
                </MenuItem>
              </Submenu>
            </template>
          </Submenu>
        </template>
      </Menu>
    </div>
  </div>
</template>

<script>
// 三种展示形式的宽度
const allWidth = 240
const partWidth = 100
const noneWidth = 0

export default {
  name: 'LeftNav',
  props: {
    leftNavData: Object,
  },
  data () {
    return {
      // 展示类型，共有三种：1、all 全部展开 2、part 展示部分 3、none 全部收起
      mode: 'all'
    }
  },
  computed: {
    isAllMode () {
      return this.mode === 'all'
    },
    isPartMode () {
      return this.mode === 'part'
    },
    isNoneMode () {
      return this.mode === 'none'
    },
  },
  created() {
    this.workspaceId = this.$route.query.workspaceId;
  },
  methods: {
    openMenus() {
      const arrayMenu = [];
      const setNode = function(array) {
        array.forEach((item) => {
          if (item.preContent.isHidden === false && item.title) {
            arrayMenu.push(item.title)
          }
          setNode(item.childMenus);
        })
      }
      if (this.leftNavData.items) {

        setNode(this.leftNavData.items)
      }
      return arrayMenu;
    },
    // 获取两个控制按钮的css里的left值
    getIconLeft () {
      switch (this.mode) {
        case 'all':
          return allWidth + 'px'
        case 'part':
          return partWidth + 'px'
        case 'none':
          return noneWidth + 'px'
      }
    },
    // 切换导航栏展示形式
    toggle (mode) {
      if (this.isAllMode) {
        this.mode = mode
      } else if (this.isPartMode) {
        this.mode = 'all'
      } else {
        if (mode === 'part') {
          this.mode = 'part'
          return
        }
        this.mode = 'all'
      }
    },
    setCurrentPage (val) {
      // 目前名称是唯一的
      this.$emit('setCurrentPage', val)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
@import '@/common/style/variables.scss';

$maxIndex: 999;
$iconBorder: 1px solid #d7d7d7;
$iconHeight: 22px;
$iconLineHeight: 16px;

.left-menu {
    height: 100%;
    position: relative;
    .side-menu {
      height: 100%;
      .nemu-icon {
        margin-top: -10px;
      }
      .menu-text {
        max-width: 120px;
        overflow: hidden;
        display: inline-block;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    .toggle-none,
    .toggle-part {
        position: absolute;
        font-size: $font-size-large;
        z-index: $maxIndex;
        cursor: pointer;
        &:hover {
            color: $link-active-color;
        }
        height: $iconHeight;
        line-height: $iconLineHeight;
        border: $iconBorder;
        border-left: none;
        border-top: none;
        width: 18px;
    }
    .toggle-part {
        top: $iconHeight;
    }
    .menu {
        height: 100%;
        overflow-x: hidden;
    }
}
</style>

<style lang="scss">
@import '@/common/style/variables.scss';

.left-menu {

  /deep/
    .ivu-icon {
        // 这里是统一优化一下iview的icon对齐方式
        margin-right: 6px;
    }
    .toggle-none,
    .toggle-part {
        color: $legend-color ;
    }
    .menu{
    /deep/
      .ivu-menu-item {
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
      }
      .ivu-menu-submenu {
          /deep/
           .ivu-menu-submenu-title {
             div {
                text-overflow: ellipsis;
                white-space: nowrap;
                overflow: hidden;
                width: 130px;
              }
              .ivu-icon {
                margin-right: -2px;
              }
          }
        }
  }
}
.content-title {
  width: 240px;
  display: flex;
  justify-content: space-between;
  padding-top: 20px;
  border-right: 1px solid #dcdee2;
  p {
    flex: 3;
    font-size: 16px;
    font-weight: bold;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;
    overflow: hidden;
  }
  .back {
    flex: 1;
  }
}
</style>
