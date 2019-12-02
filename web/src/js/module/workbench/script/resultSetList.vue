<template>
  <div
    class="set ivu-select ivu-select-single ivu-select-small"
    v-clickoutside="handleOutsideClick">
    <div
      tabindex="0"
      @click="showList"
      class="ivu-select-selection">
      <div class="">
        <span class="ivu-select-selected-value">{{$t('message.container.resultList')}}{{ current }}</span>
        <i class="ivu-icon ivu-icon-ios-arrow-down ivu-select-arrow"></i>
      </div>
    </div>
    <div
      v-if="show"
      class="ivu-select-dropdown"
      x-placement="bottom-start"
      @click="changeSet">
      <virtual-list
        class="ivu-select-dropdown-list list"
        v-if="list.length>1"
        ref="vsl"
        :size="18"
        :remain="list.length > 8 ? 8 : list.length"
        wtag="ul"
        style="overflow-x:hidden">
        <li
          v-for="(item, index) in list"
          :class="{current: current === index+ 1}"
          :data-index="index"
          :key="item.path">{{$t('message.container.resultList')}}{{ index+1 }}</li>
      </virtual-list>
    </div>
  </div>
</template>
<script>
import virtualList from '@js/component/virtualList';
import clickoutside from '@js/helper/clickoutside';
export default {
  name: 'ResultSetList',
  directives: {
    clickoutside,
  },
  components: {
    virtualList,
  },
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      resultList: this.list,
      show: false,
      current: 1,
    };
  },
  watch: {
    list(v) {
      this.resultList = v || [];
    },
  },
  methods: {
    changeSet(e) {
      if (e.target) {
        let index = e.target.getAttribute('data-index');
        if (index) {
          this.$emit('change', index);
          this.show = false;
          this.current = index - 0 + 1;
        }
      }
    },
    showList() {
      this.show = !this.show;
    },
    handleOutsideClick() {
      this.show = false;
    },
  },
};
</script>
<style lang="scss" scoped>
.ivu-select-dropdown {
  min-width: 150px;
  position: absolute;
  will-change: bottom, left;
  transform-origin: center top;
  bottom: 21px;
  left: 0px;
  z-index: 9999;
  overflow: hidden;
}
.list {
  font-size: 12px;
  height:18px;
  line-height: 18px;
  li {
    padding-left: 8px;
  }
  li:hover {
    background-color: #e9e9e9;
  }
  .current{
    color: #2d8cf0;
  }
}
</style>
