<template>
  <div
    class="list-view list-header"
    ref="list">
    <list-body
      ref="body"
      :cache="cache"
      :item-size-getter="itemSizeGetter"
      :estimated-item-size="30"
      :data="data">
      <template
        slot-scope="{col, index}">
        <div
          @click.stop="handleSortClick($event, {col, index})"
          @dblclick.prevent.stop="handleDblClick(col)">
          <span
            class="we-table-header-content"
            :title="col.columnType">{{ col.content }}</span>
          <span
            v-if="col.sortable"
            class="caret-wrapper">
            <i
              :class="computeSortClass(col, 'ascending')"
              @click.stop="handleSortClick($event, {col, index}, 'ascending')"/>
            <i
              :class="computeSortClass(col, 'descending')"
              @click.stop="handleSortClick($event, {col, index}, 'descending')"/>
          </span>
        </div>
      </template>
    </list-body>
  </div>
</template>
<script>
import listBody from './body.vue';
const prefixCls = 'we-table';
export default {
  components: {
    listBody,
  },
  props: {
    cache: {
      type: Object,
      default: () => {
        return {};
      },
    },
    isListenScroll: {
      type: Boolean,
      default: false,
    },
    data: {
      type: Array,
      required: true,
    },

    estimatedItemSize: {
      type: Number,
      default: 30,
    },

    itemSizeGetter: {
      type: Function,
    },
  },

  data() {
    return {
      sort: {
        sorting: false,
        column: null,
        type: 'normal',
        start: 0,
      },
      dblClickTimer: null,
    };
  },
  methods: {
    computeSortClass(currentHead, type) {
      return [
        `${prefixCls}-sort-caret`,
        type,
        {
          [`${prefixCls}-sort`]: (this.sort.column === currentHead && this.sort.type === type),
        },
      ];
    },
    handleScroll(v, h) {
      this.$refs.body.handleScroll(v, h);
    },
    // sorting 中不允许再点击
    // nextTick更新视图
    // deepFreeze
    // 如果是已经当前的排序不需要再重排, 1.记录当前排序列，当前的排序方向 2.如果是normal，如果下一次点击还是normal状态就不需要管了
    // lastScrollLeft不用频繁读取dom节点left
    handleSortClick(event, { col, index }, sortOrder) {
      clearTimeout(this.dblClickTimer);
      this.dblClickTimer = setTimeout(() => {
        const sortType = sortOrder || 'normal';
        if (this.sort.sorting && !col.sortable) return;
        if ((sortType === 'normal' && this.sort.type === 'normal') ||
        (this.sort.column === col && this.sort.type === sortType)) return;
        this.sort.sorting = true;
        this.sort.type = sortType;
        this.sort.column = col;
        let reverse = 0;
        if (sortType !== 'normal') {
          reverse = sortType === 'descending' ? -1 : 1;
        }
        this.$emit('sort-click', {
          reverse,
          col,
          colIndex: index,
          cb: () => {
            this.sort.sorting = false;
          },
        });
      }, 300);
    },
    handleDblClick(col) {
      clearTimeout(this.dblClickTimer);
      this.$emit('dbl-click', col);
    },
  },
};
</script>
