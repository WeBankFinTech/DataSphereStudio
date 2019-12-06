<template>
  <div class="field-table">
    <div class="field-table-header">
      <div
        class="field-table-item"
        v-for="(item, index) in tableColumns"
        :key="index"
        v-show="!item.isHide"
        :style="{'width': item.width, 'min-width': item.minWidth}">{{ item.title }}</div>
      <div
        class="field-table-item"
        style="min-width:60px;"
        v-if="canBeDelete">{{$t('message.createTable.action')}}</div>
      <div :style="{'width': fields.length > remain ? '18px' : '0'}"></div>
    </div>
    <virtual-list
      ref="columnTables"
      :size="46"
      :remain="remain"
      wtag="ul"
      class="field-table">
      <li
        v-for="(item, index) in fields"
        :key="index"
        class="field-table-body"
        :class="{'actived': item.name === current}"
        @click="handleItemClick(item, index)">
        <div
          class="field-table-item"
          :class="{'nowrap': !field.wrap}"
          :title="formatValue(item, field)"
          v-for="(field, index2) in tableColumns"
          v-show="!field.isHide"
          :key="index2"
          :style="{'width': field.width, 'min-width': field.minWidth}">
          <Input
            v-if="field.inputType === 'input'"
            v-model="item[field.key]"
            :key="field.key"
          ></Input>
          <Select
            transfer
            v-model="item[field.key]"
            v-else-if="field.inputType === 'select'"
            :key="field.key"
            :multiple="field.multiple"
            :max-tag-count="1"
            :max-tag-placeholder="maxTagPlaceholder"
            @click.prevent.stop
            @on-change="handleChange(arguments, field.key)"
            @on-open-change="handleOpen(arguments, field.key, index)">
            <Option
              v-for="(type) in field.opt"
              :key="type.value"
              :value="type.value">{{ type.label }}</Option>
          </Select>
          <div
            v-else-if="field.inputType === 'tag'"
            class="field-table-item-tag"
            @click.stop="handleTagClick(item, index)">
            <span class="field-table-item-tag-item">{{ item[field.key][0] }}</span>
            <span
              class="field-table-item-tag-item"
              v-show="item[field.key].length > 1">...</span>
            <Icon
              type="ios-arrow-down"
              class="field-table-item-tag-arrow"></Icon>
          </div>
          <span v-else>{{ formatValue(item, field) }}</span>
        </div>
        <div
          v-if="canBeDelete"
          class="field-table-item-action">
          <Button
            type="error"
            size="small"
            @click="handleDelete(item, index)">{{$t('message.createTable.delete')}}</Button>
        </div>
      </li>
    </virtual-list>
  </div>
</template>
<script>
import utils from './utils.js';
import virtualList from '@js/component/virtualList';
export default {
  components: {
    virtualList,
  },
  props: {
    fields: {
      type: Array,
    },
    maxSize: {
      type: Number,
    },
    tableColumns: {
      type: Array,
    },
    canBeDelete: {
      type: Boolean,
      default: false,
    },
    specialFieldKey: String,
  },
  data() {
    return {
      current: '',
    };
  },
  computed: {
    remain() {
      return this.fields.length > this.maxSize ? this.maxSize : this.fields.length;
    },
  },
  methods: {
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
    handleItemClick(item, index) {
      this.current = item.name;
      this.$emit('click', item, index);
    },
    handleOpen(args, key, index) {
      this.$emit('on-open-change', args[0], key, index);
    },
    handleDelete(item, index) {
      this.$emit('on-delete', item, index);
    },
    handleChange(args, key) {
      this.$emit('on-change', args, key);
    },
    maxTagPlaceholder() {
      return '...';
    },
    handleTagClick(item, index) {
      this.$emit('on-tag-click', item, index);
    },
  },
};
</script>
<style lang="scss" src="./fieldsTable.scss">
</style>
