<template>
  <div class="we-tag">
    <div
      class="we-tag-content"
      v-for="(item, index) in formattedTag()"
      :key="index">
      <span class="we-input-label">{{item}}</span>
      <Icon
        type="ios-close"
        @click="handleDelete(item)"
        class="we-tag-content-close"></Icon>
    </div>
    <button
      class="we-tag-add-btn"
      @click="changeAddTag"
      v-if="!isAddInputShow">{{newLabel}}</button>
    <input
      v-else
      ref="addInput"
      class="we-tag-add-input"
      v-model="addLabel"
      type="text"
      @blur="handleBlur"
      @keyup.enter.stop.prevent="handleEnter"
    />
  </div>
</template>
<script>
export default {
  props: {
    tagList: String,
    newLabel: {
      type: String,
      default: '新增标签',
    },
  },
  data() {
    return {
      isAddInputShow: false,
      addLabel: '',
    }
  },
  methods: {
    changeAddTag() {
      this.isAddInputShow = true;
      this.$nextTick(() => {
        this.$refs.addInput.focus();
      })
    },
    handleBlur() {
      if (this.addLabel) {
        this.$emit('add-tag', this.addLabel);
      }
      this.isAddInputShow = false;
      this.addLabel = '';
    },
    handleEnter() {
      this.isAddInputShow = false;
    },
    handleDelete(item) {
      this.$emit('delete-tag', item);
    },
    formattedTag() {
      if (this.tagList) {
        return this.tagList.split(',');
      }
      return [];
    }
  }
}
</script>
<style lang="scss" src="./index.scss">
</style>
