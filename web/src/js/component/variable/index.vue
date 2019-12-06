<template>
  <div
    v-if="variable"
    class="we-variable">
    <div class="we-variable-header">
      <div>
        <div class="we-variable-header-title">
          <span>{{ variable.name }}</span>
          <span>({{ variable.settings.length }})</span>
        </div>
        <span
          class="we-variable-header-control"
          @click="handleControl">{{ controlLabel }}</span>
      </div>
    </div>
    <div
      v-show="!isHide"
      class="we-variable-content">
      <template v-for="(item, index) in variable.settings">
        <div
          :key="index"
          :title="item.description"
          class="we-variable-content-item"
          v-if="getItemIsShow(item)">
          <span class="we-variable-content-label-group">
            <span>{{ item.name }}</span>
            <span class="we-variable-content-label-key">[{{ item.key }}]</span>
          </span>
          <select
            v-if="item.validateType === 'OFT'"
            class="we-variable-content-input"
            v-model="item.value">
            <option
              v-for="(item, index) in item.validateRangeList"
              :key="index"
              :value="item.value">{{ item.name }}</option>
          </select>
          <input
            v-model="item.value"
            :placeholder="item.defaultValue ? `默认值:${item.defaultValue}` : '无默认值'"
            type="text"
            class="we-variable-content-input"
            :class="{'un-valid': unValid && unValid.key === item.key}"
            v-else>
          <span
            v-if="unValid && unValid.key === item.key"
            class="we-warning-bar">{{ unValid.msg }}</span>
        </div>
      </template>
    </div>
    <!--<div
      v-show="!isHide"
      class="we-variable-bottom">
      <div @click="add">
        <Icon
          type="ios-add"
          color="#2d8cf0"
          size="20"/>
        <span class="we-variable-bottom-add">新增变量</span>
      </div>
    </div>
    -->
  </div>
</template>
<script>
export default {
  props: {
    variable: Object,
    unValidMsg: Object,
    isAdvancedShow: Boolean,
  },
  data() {
    return {
      isHide: false,
      controlLabel: '收起',
      unValid: null,
    };
  },
  watch: {
    unValidMsg(val, oldval) {
      this.setUnValidMsg(val);
    },
  },
  methods: {
    handleControl() {
      this.isHide = !this.isHide;
      this.controlLabel = this.isHide ? '展开' : '收起';
    },
    isNewAdd(item) {
      return item.hasOwnProperty('isNew');
    },
    add() {
    },
    handleOk(item) {
      this.$emit('add-item', item, this.variable, (flag) => {
        item.isNew = false;
      });
    },
    handleDelete(item) {
      this.$emit('remove-item', item, this.variable, (flag) => {
      });
    },
    setUnValidMsg({ key, msg }) {
      this.unValid = {
        key,
        msg,
      };
    },
    getItemIsShow(item) {
      if (item.hidden) {
        return !item.hidden;
      }
      if (item.advanced && this.isAdvancedShow) {
        return true;
      } else if (item.advanced && !this.isAdvancedShow) {
        return false;
      }
      return true;
    },
  },
};
</script>
<style lang="scss" src="./index.scss">

</style>
