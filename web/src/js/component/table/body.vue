<template>
  <div>
    <div
      class="list-view-phantom"
      :style="{
        width: contentWidth + 'px'
      }">
    </div>
    <div
      ref="content"
      class="list-view-content">
      <div
        class="list-view-item"
        :style="{
          width: getItemSizeAndOffset(startIndex + index).size + 'px'
        }"
        v-for="(col, index) in visibleData"
        :key="index">
        <slot
          :col="col"
          :index="index"></slot>
      </div>
    </div>
  </div>
</template>
<script>
export default {
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
      viewWidth: 0,
      lastMeasuredIndex: -1,
      startIndex: 0,
      sizeAndOffsetCahce: {},
      visibleData: [],
      ops: {
        bar: {
          background: 'rgb(24, 144, 255)',
          keepShow: true,
          minSize: 0.1,
        },
        rail: {
          border: '1px solid #cecece',
          size: '20px',
        },
        scrollButton: {
          enable: true,
          background: '#cecece',
        },
      },
      cacheVH: {
        v: {},
        h: {
          scrollLeft: 0,
        },
      },
    };
  },
  computed: {
    contentWidth() {
      const { data, lastMeasuredIndex, estimatedItemSize } = this;
      let itemCount = data.length;
      if (lastMeasuredIndex >= 0) {
        const lastMeasuredSizeAndOffset = this.getLastMeasuredSizeAndOffset();
        return lastMeasuredSizeAndOffset.offset + lastMeasuredSizeAndOffset.size + (itemCount - 1 - lastMeasuredIndex) * estimatedItemSize;
      } else {
        return itemCount * estimatedItemSize;
      }
    },
  },
  watch: {
    data: {
      handler() {
        let { v, h } = this.cacheVH;
        this.handleScroll(v, h);
      },
      deep: true,
    },
  },
  mounted() {
    this.updateVisibleData();
    this.viewWidth = this.$refs.content.clientWidth;
  },
  methods: {
    getItemSizeAndOffset(index) {
      const { lastMeasuredIndex, sizeAndOffsetCahce, data, itemSizeGetter } = this;
      if (lastMeasuredIndex >= index) {
        return sizeAndOffsetCahce[index];
      }
      let offset = 0;
      if (lastMeasuredIndex >= 0) {
        const lastMeasured = sizeAndOffsetCahce[lastMeasuredIndex];
        if (lastMeasured) {
          offset = lastMeasured.offset + lastMeasured.size;
        }
      }
      for (let i = lastMeasuredIndex + 1; i <= index; i++) {
        const item = data[i];
        let size;
        if (this.cache[i]) {
          size = this.cache[i];
        } else {
          size = itemSizeGetter.call(null, item, i);
          this.cache[i] = size;
        }
        sizeAndOffsetCahce[i] = {
          size,
          offset,
        };
        offset += size;
      }
      if (index > lastMeasuredIndex) {
        this.lastMeasuredIndex = index;
      }
      return sizeAndOffsetCahce[index];
    },

    getLastMeasuredSizeAndOffset() {
      return this.lastMeasuredIndex >= 0 ? this.sizeAndOffsetCahce[this.lastMeasuredIndex] : { offset: 0, size: 0 };
    },

    findNearestItemIndex(scrollLeft) {
      const { data } = this;
      let total = 0;
      for (let i = 0, j = data.length; i < j; i++) {
        const size = this.getItemSizeAndOffset(i).size;
        total += size;
        if (total >= scrollLeft || i === j - 1) {
          return i;
        }
      }

      return 0;
    },

    updateVisibleData(scrollLeft) {
      let canScrollWidth = this.contentWidth - this.viewWidth;
      scrollLeft = scrollLeft || 0;
      if (scrollLeft > canScrollWidth) {
        scrollLeft = canScrollWidth;
      }
      const start = this.findNearestItemIndex(scrollLeft);
      const end = this.findNearestItemIndex(scrollLeft + (this.$el.clientWidth || 1400));
      this.visibleData = this.data.slice(start, Math.min(end + 3, this.data.length));
      this.startIndex = start;
      this.$refs.content.style.webkitTransform = `translate3d(${this.getItemSizeAndOffset(start).offset}px, 0, 0)`;
    },

    handleScroll(v, h) {
      const { scrollLeft } = h;
      this.cacheVH = { v, h };
      this.$emit('on-scroll', { v, h });
      this.updateVisibleData(scrollLeft);
    },
  },
};
</script>
<style>
.list-view-phantom {
position: absolute;
left: 0;
top: 0;
right: 0;
z-index: -1;
height: 100%;
}

.list-view-content {
display: flex;
left: 0;
right: 0;
top: 0;
position: absolute;
height: 100%;
}

.list-view-item {
height: 100%;
color: #666;
box-sizing: border-box;
flex-shrink: 0;
text-align: center;
}
</style>
