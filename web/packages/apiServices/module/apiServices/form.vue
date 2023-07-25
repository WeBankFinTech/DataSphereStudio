<template>
  <div class="dialogFromContent" v-if="formMake.items.length">
    <div class="mask"></div>
    <Form :style="mainWidth" :label-width="labelWidth"  class="dialogFromMain" ref="formValidate" :model="formMake">
      <h3 class="title">{{title}}</h3>
      <template v-for="(item, index) in formMake.items">
        <!-- input框 -->
        <FormItem :rules="formRules[item.prop]" :label="item.label" :prop="'items.' + index + '.value'" :key="index" v-if="item.type === 'input'">
          <slot name="select" :currentData="item">
            <Input v-bind="{...item.property}" v-model="formMake.items[index].value" />
          </slot>
        </FormItem>
        <!-- 选择菜单 -->
        <FormItem :rules="formRules[item.prop]" :label="item.label" :prop="'items.' + index + '.value'" :key="index" v-if="item.type === 'select'">
          <slot name="select" :currentData="item">
            <Select v-bind="{...item.property}" v-model="formMake.items[index].value">
              <Option v-for="(optionItem) in item.option" :key="optionItem.value" v-bind="{...optionItem}"></Option>
            </Select>
          </slot>
        </FormItem>
        <!-- 时间 -->
        <FormItem :rules="formRules[item.prop]" :label="item.label" :prop="'items.' + index + '.value'" :key="index" v-if="item.type === 'datePicker'">
          <slot name="datePicker" :currentData="item">
            <DatePicker v-bind="{...item.property}" v-model="formMake.items[index].value"></DatePicker>
          </slot>
        </FormItem>
        <!-- 单选 -->
        <FormItem :rules="formRules[item.prop]" :label="item.label" :prop="'items.' + index + '.value'" :key="index" v-if="item.type === 'radio'">
          <slot name="radio" :currentData="item">
            <RadioGroup v-bind="{...item.property}" v-model="formMake.items[index].value">
              <Radio v-for="(optionItem) in item.option" :key="optionItem.value" v-bind="{...optionItem}">{{optionItem.label}}</Radio>
            </RadioGroup>
          </slot>
        </FormItem>
        <!-- 多选 -->
        <FormItem :rules="formRules[item.prop]" :label="item.label" :prop="'items.' + index + '.value'" :key="index" v-if="item.type === 'checkbox'">
          <slot name="checkbox" :currentData="item">
            <CheckboxGroup v-bind="{...item.property}" v-model="formMake.items[index].value">
              <Checkbox v-for="(optionItem) in item.option" :key="optionItem.value" v-bind="{...optionItem}"></Checkbox>
            </CheckboxGroup>
          </slot>
        </FormItem>
      </template>
      <!-- 扩展 -->
      <div v-if="openExtend">
        <slot></slot>
      </div>
      <FormItem class="buttonTextAlign">
        <slot name="floor">
          <Button v-if="isShowCancel"  @click="handleCancel('formValidate')">{{cancelText}}</Button>
          <Button style="margin-left: 8px" v-if="isShowConfirm" type="primary" @click="handleSubmit('formValidate')">{{submitText}}</Button>
        </slot>
      </FormItem>
    </Form>
  </div>
</template>
<script>
import i18n from '@dataspherestudio/shared/common/i18n';

export default {
  props: {
    labelWidth: {
      type: [Number, String],
      default: 80
    },
    formMake: {
      // 表单布置结构
      // formMake.type (显示的表单项类型input，select，datePicker，radio，checkbox)
      type: Object,
      default: () => { return { item: [] } },
    },
    formRules: {
      // 表单验证规则（和iview的数据格式一样）
      type: Object,
      default: () => {},
    },
    isShowConfirm: {
      // 是否显示确认按钮
      type: Boolean,
      default: true,
    },
    isShowCancel: {
      // 是否显示取消按钮
      type: Boolean,
      default: true,
    },
    submitText: {
      // 确认文本
      type: String,
      default: i18n.t('message.apiServices.confirm'),
    },
    cancelText: {
      // 取消文本
      type: String,
      default: i18n.t('message.apiServices.cancel'),
    },
    "label-width": {
      type: Number
    },
    openExtend: {
      type: Boolean,
      default: false,
    },
    width: {
      type: [Number, String],
      default: 680
    },
    title: {
      type: String,
      default: i18n.t('message.apiServices.istitle')
    }
  },
  mounted() {
  },
  computed: {
    mainWidth () {
      const width = parseInt(this.width);
      const styleWidth = width <= 100 ? `${width}%` : `${width}px`;
      return { width: styleWidth };
    },
  },
  data() {
    return {
    };
  },
  methods: {
    handleSubmit(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          let obj = {};
          this.formMake.items.forEach(item => {
            obj[item.prop] = item.value;
          })
          this.$emit("onConfirm", obj);
        } else {
          this.$emit("onConfirm", false);
        }
      });
    },
    handleCancel() {
      this.$emit("onCancel")
    },
    // 可添加重置功能
    handleReset(name) {
      this.$refs[name].resetFields();
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .dialogFromContent {
    .title {
      margin-bottom: 20px;
    }
    .mask {
      position: $fixed;
      z-index: $zindex-modal;
      width: $percent-all;
      height: $percent-all;
      background-color: rgba(0,0,0,.5);
      top:0;
      left: 0;
    }
    .dialogFromMain {
      position: $fixed;
      top: $percent-half;
      left: $percent-half;
      transform: translate(-50%,-50%);
      height: auto;
      margin: auto;
      z-index: $zindex-modal;
      padding: 20px 20px 0;
      border-radius: 5px;
      background-color: $background-color-white;
      box-sizing: border-box;
      .buttonTextAlign {
        text-align: $align-right
      }
    }
  }
</style>
