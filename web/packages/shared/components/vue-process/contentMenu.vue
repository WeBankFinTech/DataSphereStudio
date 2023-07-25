<template lang="html">
  <div v-clickoutside="close" :style="getStyle" class="designer-menu" @contextmenu.prevent.stop>
    <ul class="designer-menu-ul">
      <li v-for="(item, index) in data" :key="index" class="designer-menu-li" @click="choose(item, index)">
        <Icon v-if="item.icon" :name="item.icon" class="designer-menu-li-icon" />
        <img v-else-if="item.img" :src="item.img" class="designer-menu-li-icon menu-img">
        <span v-else class="designer-menu-li-icon" />
        <span class="designer-menu-li-text">
          {{ item.text }}
        </span>
        <span v-if="item.children"> > </span>
        <ul class="designer-menu-ul sub-menu-ul" v-if="item.children">
          <li v-for="(itemChild, indexChild) in item.children" :key="indexChild" class="designer-menu-li" @click="choose(itemChild, indexChild)">
            <Icon v-if="itemChild.icon" :name="itemChild.icon" class="designer-menu-li-icon" />
            <img v-else-if="itemChild.img" :src="itemChild.img" class="designer-menu-li-icon menu-img">
            <span v-else class="designer-menu-li-icon" />
            <span class="designer-menu-li-text">
              {{ itemChild.text }}
            </span>
          </li>
        </ul>
      </li>
    </ul>
  </div>
</template>
<script>
import Icon from './icon.vue'
import clickoutside from './clickoutside.js';
export default {
  components: {
    Icon
  },
  directives: {
    clickoutside
  },
  props: {
    data: {
      type: Array,
      required: true
    },
    left: {
      type: [Number, String],
      default: 0
    },
    top: {
      type: [Number, String],
      default: 0
    }
  },
  computed: {
    getStyle() {
      let left = Number(this.left);
      let top = Number(this.top);
      left = (left.toString() == 'NaN') ? this.left : this.left + 'px';
      top = (top.toString() == 'NaN') ? this.top : this.top + 'px';
      return {
        left,
        top
      };
    }
  },
  methods: {
    choose(item, index) {
      this.$emit('choose', item, index);
      this.$emit('close');
    },
    close() {
      this.$emit('close');
    }
  }
};
</script>
