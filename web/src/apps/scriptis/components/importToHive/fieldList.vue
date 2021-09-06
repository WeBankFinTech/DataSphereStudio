<template>
  <virtual-list
    ref="fieldList"
    :size="54"
    :remain="list.length > 2 ? (isXls ? 5 : 5) : list.length"
    wtag="ul"
    class="field-list-wrap"
    :onscroll="onListScroll">
    <li
      v-for="(item, index) in list"
      :key="index"
      class="field-list-item">
      <FormItem
        :label="$t('message.scripts.importToHive.MC')"
        :prop="'fields.' + index + '.fieldName'"
        :rules="[{
          required: true,
          message: $t('message.scripts.importToHive.ZDMC'),
        }, {
          pattern: /^[a-zA-Z0-9_]+$/,
          message: $t('message.scripts.importToHive.JZCZM'),
        }]"
        :label-width="50"
        class="field-list-item-child">
        <Input
          v-model="item.fieldName"
          :placeholder="$t('message.scripts.importToHive.QSRZDM')"
          class="field-list-item-child-name"
          :style="{'width' : ['char', 'varchar', 'decimal', 'date'].indexOf(item.type) === -1 ? '336px' : '166px'}"
        ></Input>
      </FormItem>
      <div
        class="field-list-item-child">
        <span class="field-list-item-child-label">{{ $t('message.scripts.importToHive.LX') }}</span>
        <Select
          v-model="item.type"
          @on-change="$emit('fields-type-click' ,item)"
          class="field-list-item-child-type"
          transfer>
          <Option
            v-for="(field) in staticData.fieldsType"
            :label="field.value"
            :value="field.value"
            :key="field.value"/>
        </Select>
      </div>
      <div
        class="field-list-item-child"
        v-if="item.type === 'date'">
        <span
          class="field-list-item-child-label">{{ $t('message.scripts.importToHive.GS') }}</span>
        <Select
          v-model="item.dateFormat"
          clearable
          :placeholder="$t('message.scripts.importToHive.QXZGS')"
          class="field-list-item-child-time"
          transfer>
          <Option
            v-for="(field, index) in staticData.dateType"
            :label="field.label"
            :value="field.value"
            :key="index"/>
        </Select>
      </div>
      <div
        class="field-list-item-child"
        v-if="item.type === 'char' || item.type === 'varchar'">
        <Input
          v-model="item.length"
          :placeholder="$t('message.scripts.importToHive.CD')"
          class="field-list-item-child-length"></Input>
      </div>
      <div
        class="field-list-item-child"
        v-if="item.type=='decimal'">
        <Input
          v-model="item.precision"
          :placeholder="$t('message.scripts.importToHive.JD')"
          class="field-list-item-child-option"></Input>
      </div>
      <div
        class="field-list-item-child"
        v-if="item.type=='decimal'">
        <Input
          v-model="item.scale"
          :placeholder="$t('message.scripts.importToHive.XSW')"
          class="field-list-item-child-option"></Input>
      </div>
      <Icon
        type="md-menu"
        class="step-form-extras"
        size="16"
        @click="$emit('comment-click', item)"/>
      <div
        class="field-list-item-child"
        v-show="item.commentShow">
        <Input
          v-model="item.comment"
          :placeholder="$t('message.scripts.importToHive.SRZDZS')"
          class="field-list-item-child-comment"></Input>
      </div>
      <Icon
        type="md-close"
        class="step-form-close"
        size="16"
        @click="$emit('delete-click', item)"/>
    </li>
  </virtual-list>
</template>
<script>
import virtualList from '@/components/virtualList';
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
      type: Object,
      default: () => {},
    },
    isXls: Boolean,
  },
  methods: {
    onListScroll(ev, data) {
      this.$emit('on-scroll', data);
    }
  },
};
</script>
