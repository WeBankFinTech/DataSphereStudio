<template>
  <div
    class="we-filter-view">
    <virtual-list
      ref="fieldList"
      wtag="ul"
      :size="size"
      :remain="remain"
      :item="item"
      :itemcount="total"
      :itemprops="getItemprops"/>
  </div>
</template>
<script>
import virtualList from '@dataspherestudio/shared/components/virtualList';
import item from './filterItem.vue'
export default {
  components: {
    virtualList,
  },
  props: {
    headRows: {
      type: Array,
      default: () => [],
    },
    height: { // 树高度
      type: Number,
      require: true
    },
  },
  data() {
    return {
      total: this.headRows.length,
      remain: 0,
      size: 28,
      item
    }
  },
  watch: {
    height() {
      this.layout()
    }
  },
  mounted(){
    this.layout()
  },
  methods: {
    hanlderCheck(index) {
      let item = this.headRows[index]
      let count = 0;
      this.headRows.forEach((item) => {
        if (item.checked) {
          count += 1;
        }
      });
      if (count > 1 || (count === 1 && !item.checked)) {
        this.$emit('on-check', item);
      } else {
        this.$Message.warning(this.$t('message.common.checkone'));
      }
    },
    getItemprops(index) {
      return {
        key: index,
        props: {
          item: this.headRows[index]
        },
        nativeOn: {
          click: (ev) => {
            ev.stopPropagation()
            this.hanlderCheck(index)
          },
        }
      }
    },
    layout() {
      let rows =  Math.floor((this.height - 70) / this.size) // 列删选浮框在结果集表格上，高度不包括分页、表头
      this.total = this.headRows.length
      this.remain = this.total > rows ? rows : this.total
      this.$refs.fieldList.forceRender();
    }
  },
};
</script>
<style lang="scss" scoped>
  @import "@dataspherestudio/shared/common/style/variables.scss";
  .we-filter-view {
    width: 160px;
    position: absolute;
    bottom: 90px;
    top: 40px;
    padding-top: 10px;
    @include bg-color(#fff, $dark-menu-base-color);
    border: 1px solid #dcdee2;
    @include border-color($border-color-base, #525354);
    border-left: none;
    padding: 10px;
    z-index: 2;
    opacity: 0.9;
  }
</style>

