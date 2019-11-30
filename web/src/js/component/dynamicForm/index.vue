<template>
  <div class="dynamic-form">
    <div class="dynamic-form-header-wrap">
      <h4 class="dynamic-form-header-title">{{ title }}</h4>
      <Icon
        type="md-add"
        class="icon-add"
        size="16"
        @click="handleAdd"></Icon>
    </div>
    <Form
      ref="dynamicForm"
      :model="formDynamic"
      class="dynamic-form-content">
      <FormItem
        v-for="(item, index) in formDynamic.list"
        :key="index">
        <Row>
          <Col
            span="1"
            class="tc">{{ index + 1 }}
          </Col>
          <Col span="10">
          <!-- 这里必须和上面的model挂钩，prop是以model为基础的路径
                例如第一个key的实际路径是formDynamic.list[0].key，他的prop就是'list.' + index + '.key'
                当一个FormItem里面有两个需要验证表单元素时，每一个子表单元素需要用FormItem包着
                且prop和rules需要写在子FormItem上，rules可以是object或array -->
          <FormItem
            :prop="'list.' + index + '.key'"
            :rules="[{required: true,message: $t('message.dynamicForm.rule.emptyKey'),trigger: 'blur'},{min: 1, max: 128, message: $t('message.dynamicForm.rule.lengthLimit'), trigger: 'blur'},{type: 'string', pattern: /^[a-zA-z][^\s\u4e00-\u9fa5]*$/, message: $t('message.dynamicForm.rule.nameVaild'), trigger: 'blur'},{validator:validateKey,trigger: 'blur'}]">
            <Input
              v-model="item.key"
              type="text"
              :placeholder="$t('message.dynamicForm.namePlaceholder', {title})"
              @on-change="onInputChange"></Input>
          </FormItem>
          </Col>
          <Col
            span="1"
            class="tc">
          =
          </Col>
          <Col span="10">
          <FormItem
            :prop="'list.' + index + '.value'"
            :rules="[{required: true, message: $t('message.dynamicForm.rule.emptyValue'), trigger: 'blur'},{min: 1, max: 128, message: $t('message.dynamicForm.rule.lengthLimit'), trigger: 'blur'}]">
            <Input
              v-model="item.value"
              type="text"
              :placeholder="$t('message.dynamicForm.placeholderInput')"
              @on-change="onInputChange"></Input>
          </FormItem>
          </Col>
          <Col
            span="2"
            class="tc">
          <Icon
            type="ios-close"
            class="dynamic-form-close"
            @click.native="handleDelete(index)"></Icon>
          </Col>
        </Row>
      </FormItem>
    </Form>
  </div>
</template>
<script>
export default {
  props: {
    title: String,
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      formDynamic: {
        list: [],
      },
      validateKey: (rule, value, callback) => {
        const prop = rule.field;
        // 拿到当前编辑的item的index
        const current = prop.slice(prop.indexOf('.') + 1, prop.lastIndexOf('.'));
        // 必须当key相等，而且index不等的时候才是repeat
        const findRepeat = this.formDynamic.list.find((item, index) => {
          return item.key === value && current != index;
        });
        if (findRepeat) {
          callback(new Error(this.$t('message.dynamicForm.someKey')));
        }
        callback();
      },
    };
  },
  watch: {
    list(val) {
      this.formDynamic.list = this.list;
    },
  },
  mounted() {
    this.formDynamic.list = this.list;
  },
  methods: {
    handleAdd() {
      if (this.formDynamic.list.length) {
        this.$refs.dynamicForm.validate((valid) => {
          if (valid) {
            this.formDynamic.list.push({
              key: '',
              value: '',
            });
          } else {
            this.$Message.warning(this.$t('message.newConst.failedNotice'));
          }
        });
      } else {
        this.formDynamic.list.push({
          key: '',
          value: '',
        });
      }
    },
    handleDelete(index) {
      this.formDynamic.list.splice(index, 1);
      this.$emit('change', this.formDynamic.list);
    },
    onInputChange() {
      this.$emit('change', this.formDynamic.list);
    },
  },
};

</script>
<style lang="scss" src="./index.scss"></style>
