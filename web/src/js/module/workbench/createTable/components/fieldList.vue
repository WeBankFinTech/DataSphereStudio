<template>
  <div class="field-list">
    <div
      class="field-list-title"
      v-if="list.length">
      <div
        v-for="(item, index) in staticData"
        :key="index"
        class="field-list-title-item"
        :style="{'width': item.width}">
        <i
          v-if="item.require"
          class="field-list-title-item-require">*</i>
        <span>{{ item.name }}</span>
      </div>
      <div
        class="field-list-title-action"
        v-if="hasAction">{{$t('message.createTable.action')}}</div>
    </div>
    <virtual-list
      ref="fieldList"
      :size="54"
      :remain="Math.floor((height-70)/54)"
      wtag="ul"
      class="field-list-content">
      <li
        v-for="(item, index) in list"
        :key="index"
        class="field-list-content-item">
        <FormItem
          :prop="`fields.${index}.${info.key}`"
          v-for="(info) in staticData"
          :key="info.key"
          :rules="info.rules"
          class="field-list-content-item-item"
          :style="{'width': info.width}"
        >
          <Input
            v-if="info.inputType === 'input'"
            v-model="item[info.key]"
            :key="info.key"
            class="field-list-content-input"
          ></Input>
          <Select
            transfer
            v-model="item[info.key]"
            v-if="info.inputType === 'select'"
            :key="info.key"
            class="field-list-content-select">
            <Option
              v-for="(type) in info.opt"
              :key="type.value"
              :value="type.value">{{ type.label }}</Option>
          </Select>
          <InputNumber
            v-if="info.inputType === 'inputNumber'"
            v-model="item[info.key]"
            :key="info.key"
            :min="1"
            class="field-list-content-number"></InputNumber>
          <span v-if="info.inputType === 'string'">{{ item[info.key] }}</span>
        </FormItem>
        <div
          class="field-list-content-action"
          v-if="hasAction">
          <Button
            type="error"
            size="small"
            @click.stop="handleDelete(index)">{{$t('message.createTable.delete')}}</Button>
        </div>
      </li>
    </virtual-list>
  </div>
</template>
<script>
import virtualList from '@js/component/virtualList';
export default {
  components: {
    virtualList,
  },
  props: {
    list: {
      type: Array,
      default: () => [],
    },
    staticData: {
      type: Array,
      default: () => [],
    },
    hasAction: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      height: 0,
    };
  },
  mounted() {
    this.height = (window.innerHeight - 112) / 2 - 51;
  },
  methods: {
    handleDelete(index) {
      this.$emit('delete', index);
    },
  },
};
</script>
