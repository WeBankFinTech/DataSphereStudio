<template>
  <div class="nav-menu">
    <div class="nav-menu-left">
      <template v-for="(item, index) in menuList">
        <div
          class="nav-menu-left-item"
          :key="index"
          :class="{'actived':current && item.subTitle === current.subTitle}"
          @mouseover.stop="onMouseOver(item)">
          <!-- <p class="nav-menu-left-sub-title">{{item.subTitle}}</p> -->
          <p class="nav-menu-left-main-title">{{item.mainTitle}}</p>
          <Icon
            size="14"
            type="ios-arrow-forward"
            class="nav-menu-left-icon"
            v-if="item.children"></Icon>
        </div>
      </template>
    </div>
    <div
      class="nav-menu-right"
      v-if="current">
      <div
        class="nav-menu-right-item"
        v-for="(item, index) in current.children"
        :key="index">
        <div class="nav-menu-right-item-title">{{item.classify}}</div>
        <div
          class="nav-menu-right-item-child"
          v-for="(child) in item.children"
          :key="child.title"
          @click.stop="handleClick(child)">
          <i
            :class="child.icon"
            class="nav-menu-right-item-icon"
          ></i>
          <span>{{child.title}}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>

export default {
  data() {
    return {
      menuList: [{
        mainTitle: this.$t('message.headerNavBar.Workflow'),
        subTitle: 'Workflow Development',
        children: [{
          classify: this.$t('message.headerNavBar.Workflow'),
          children: [{
            title: 'Workflow',
            path: 'workflow',
            icon: 'fi-workflow1',
          }]
        }]
      }, {
        mainTitle: this.$t('message.headerNavBar.Exchangis'),
        subTitle: 'Exchangis',
        children: [{
          classify: this.$t('message.headerNavBar.Exchangis'),
          children: [{
            title: 'Exchangis',
            path: '',
            icon: 'fi-exchange',
          }]
        }]
      },{
        mainTitle: this.$t('message.headerNavBar.Scriptis'),
        subTitle: 'Application Development',
        children: [{
          classify: this.$t('message.headerNavBar.Scriptis'),
          children: [{
            title: 'Scriptis',
            path: 'linkis',
            icon: 'fi-scriptis',
          }]
        }]
      }, {
        mainTitle: this.$t('message.headerNavBar.Visualis'),
        subTitle: 'Data Visualization',
        children: [{
          classify: this.$t('message.headerNavBar.Visualis'),
          children: [{
            title: 'Visualis',
            path: 'visualis',
            icon: 'fi-visualis',
          }]
        }]
      }, {
        mainTitle: this.$t('message.headerNavBar.Qualitis'),
        subTitle: 'Data Development',
        children: [{
          classify: this.$t('message.headerNavBar.Qualitis'),
          children: [{
            title: 'Qualitis',
            path: 'qualitis',
            icon: 'fi-qualitis',
          }]
        }]
      }, {
        mainTitle: this.$t('message.headerNavBar.Schedulis'),
        subTitle: 'Schedulis',
        children: [{
          classify: this.$t('message.headerNavBar.Schedulis'),
          children: [{
            title: 'Schedulis',
            path: 'schedulis',
            icon: 'fi-schedule',
          }]
        }]
      }, {
        mainTitle: this.$t('message.headerNavBar.LinkisConsole'),
        subTitle: 'Linkis Console',
        children: [{
          classify: this.$t('message.headerNavBar.LinkisConsole'),
          children: [{
            title: 'Linkis Console',
            path: 'console',
            icon: 'fi-resource',
          }]
        }]
      }],
      current: null
    }
  },
  methods: {
    onMouseOver(item) {
      if (item.children) {
        return this.current = item;
      }
      this.current = null;
    },
    handleClick(child) {
      if (child.path) {
        let query = this.$route.query;
        this.gotoCommonIframe(child.path, query);
      } else {
        this.$Message.warning(this.$t('message.constants.warning.comingSoon'));
      }
      this.$emit('click', child);
    }
  },
}
</script>
<style lang="scss" src="./index.scss">
</style>
