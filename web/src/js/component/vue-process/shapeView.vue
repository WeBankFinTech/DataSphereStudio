<template lang="html">
  <div :style="getBaseStyle" class="designer-shape">
    <input v-model="searchValue" :placeholder="t('vue-process.search')" class="shape-search">
    <div v-for="(shape, index) in search" :key="index" :class="{ 'top': index==0 }" class="shape-container">
      <div :class="{open: shape.show}" class="shape-title" @click="toggleTitle(shape)">
        <Icon v-show="!shape.show" name="turn_right" />
        <Icon v-show="shape.show" name="turn_top" />
        {{ shape.title }}
      </div>
      <div v-show="shape.show" v-if="shape.children && shape.children.length>0" class="shape-content">
        <div v-for="(item, index) in shape.children" :key="index" :title="item.title" class="shape-box"
          @mousedown="beginDragShap(item, $event)">
          <img :src="item.image" :alt="item.title" class="shape-box-img" @error="error" :style="getBoxStyle" />
          <div :title="item.title" class="shape-title-txt">{{ item.title }}</div>
        </div>
      </div>
    </div>
    <div v-show="state.draging.type=='shape'" ref="dragShapBox" :style="getBoxStyle" class="shape-box-draging">
      <img v-if="state.draging.type=='shape'" :src="state.draging.data.image" class="shape-box-img">
    </div>
  </div>
</template>
<script>
import Icon from './icon.vue'
import { mapActions, commit, mixin } from './store';
export default {
  components: {
    Icon
  },
  mixins: [mixin],
  props: {
    shapes: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      searchValue: ''
    };
  },
  computed: {
    getBaseStyle() {
      return {
        width: this.state.shapeOptions.viewWidth + 'px'
      };
    },
    getBoxStyle() {
      return {
        'height': this.state.shapeOptions.height + 'px',
        'width': this.state.shapeOptions.width + 'px',
        'line-height': this.state.shapeOptions.height + 'px'
      };
    },
    search() {
      let arr = [];
      if (this.searchValue) {
        this.shapes.forEach(item => {
          if (Array.isArray(item.children)) {
            let childrens = item.children.filter(child => {
              return child.title && child.title.indexOf(this.searchValue) != -1
            })
            if (childrens.length > 0) {
              arr.push(Object.assign({}, item, { children: childrens }))
            }
          }
        })
      } else {
        arr = this.shapes
      }
      return arr
    }
  },
  mounted() {
    this.$watch('state.fullScreen', function () {
      this.initView();
    });
    this.$nextTick(this.initView)
  },
  methods: {
    ...mapActions(['setDraging', 'clearDraging']),
    initView() {
      let rect = this.$el.getBoundingClientRect();
      commit(this.$store, 'UPDATE_SHAPEVIEW_OFFSET', {
        x: rect.left,
        y: rect.top
      });
    },
    toggleTitle(shape) {
      shape.show = !shape.show;
    },
    beginDragShap(shape, e) {
      if (this.state.disabled) return;
      this.changeDragShapOffset(e);
      this.setDraging({
        type: 'shape',
        data: shape
      });
    },
    changeDragShapOffset(e) {
      // 这里计算的是在shapeView的位置
      let dragShapEle = this.$refs.dragShapBox;
      requestAnimationFrame(() => {
        dragShapEle.style.left = e.pageX - this.state.baseOptions.shapeViewOffsetX - this.state.shapeOptions.width / 2 + 'px';
        dragShapEle.style.top = e.pageY - this.state.baseOptions.shapeViewOffsetY - this.state.shapeOptions.height / 2 + 'px';
      });
    },
    error(e) {
      console.log(e)
    }
  }
}
</script>
