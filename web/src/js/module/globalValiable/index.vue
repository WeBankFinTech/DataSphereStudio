<template>
  <div class="global-valiable">
    <Row class="global-valiable-control">
      <Button
        type="primary"
        @click="handleSubmit('formDynamic')"
        :loading="loading">{{ !isEdit ? $t('message.constants.edit') : $t('message.constants.save') }}</Button>
      <Button
        @click="handleCancel">{{ $t('message.constants.cancel') }}</Button>
    </Row>
    <Form
      ref="formDynamic"
      :model="formDynamic"
      :label-width="100"
      class="global-valiable-form"
      :style="{'max-height': (height - 94) + 'px'}"
    >
      <FormItem
        v-for="(item, index) in formDynamic.items"
        :key="index"
        :label="$t('message.globalValiable.globalValiable') + (index + 1) + '：'">
        <Row>
          <Col span="10">
          <FormItem
            :prop="'items.' + index + '.key'"
            :rules="[
              {required: true,message: $t('message.globalValiable.rules.first.required', {text: index+1}),trigger: 'blur'},
              {pattern: /^[a-zA-z][^\s\u4e00-\u9fa5]*$/, message: `${$t('message.globalValiable.rules.first.letterTypeLimit')}`},
              {validator:validateKey,trigger: 'blur'},
            ]">
            <Input
              v-model="item.key"
              type="text"
              :maxlength="50"
              :placeholder="$t('message.globalValiable.rules.first.placeholder')"
              :disabled="!isEdit"></Input>
          </FormItem>
          </Col>
          <Col
            span="1"
            class="global-valiable-tc">
          ：
          </Col>
          <Col span="10">
          <FormItem
            :prop="'items.' + index + '.value'"
            :rules="[{required: true, message: $t('message.globalValiable.rules.second.required', {text: index+1}), trigger: 'blur'}]">
            <Input
              v-model="item.value"
              type="text"
              :maxlength="64"
              :placeholder="$t('message.globalValiable.rules.second.placeholder')"
              :disabled="!isEdit"></Input>
          </FormItem>
          </Col>
          <Col
            span="3"
            class="global-valiable-remove">
          <Button
            type="error"
            size="small"
            @click="handleRemove(item, index)"
            v-if="isEdit">{{ $t('message.constants.remove') }}</Button>
          </Col>
        </Row>
      </FormItem>
    </Form>
    <Row class="global-valiable-add">
      <Col span="12">
      <Button
        type="dashed"
        long
        icon="md-add"
        @click="handleAdd"
        v-if="isEdit">{{ $t('message.globalValiable.addArgs') }}</Button>
      </Col>
    </Row>
    <div
      class="global-valiable-empty"
      :style="{'height': (height - 52) + 'px'}"
      v-if="!formDynamic.items.length">{{ $t('message.globalValiable.emptyDataText') }}</div>
    <detele-modal
      ref="killModal"
      :loading="loading"
      @delete="handleRemove"></detele-modal>
  </div>
</template>
<script>
import api from '@/js/service/api';
import storage from '@/js/helper/storage';
import module from './index';
import deteleModal from '@js/component/deleteDialog';
export default {
  components: {
    deteleModal,
  },
  mixins: [module.mixin],
  data() {
    return {
      current: null,
      currentIndex: 0,
      formDynamic: {
        items: [],
      },
      loading: false,
      isEdit: false,
      height: 0,
      validateKey: (rule, value, callback) => {
        const prop = rule.field;
        // 拿到当前编辑的item的index
        const current = prop.slice(prop.indexOf('.') + 1, prop.lastIndexOf('.'));
        // 必须当key相等，而且index不等的时候才是repeat
        const findRepeat = this.formDynamic.items.find((item, index) => {
          return item.key === value && current != index;
        });
        if (findRepeat) {
          callback(new Error(this.$t('message.globalValiable.sameName')));
        }
        callback();
      },
    };
  },
  mounted() {
    this.height = this.$route.query.height;
    this.getGlobalValiableList();
  },
  methods: {
    getGlobalValiableList() {
      api.fetch('/variable/listGlobalVariable', 'get').then((res) => {
        this.formDynamic.items = res.globalVariables.map((item, index) => {
          return Object.assign(item);
        });
        if (storage.get('isGlobalVariableChange')) {
          this.dispatch('IndexedDB:updateGlobalCache', {
            id: this.getUserName(),
            variableList: res.globalVariables,
          });
        }
      });
    },
    handleSubmit(name) {
      if (this.isEdit) {
        if (this.formDynamic.items.length) {
          this.$refs[name].validate((valid) => {
            if (valid) {
              this.save();
            } else {
              this.$Message.error(this.$t('message.globalValiable.error.validate'));
            }
          });
        } else {
          this.save();
        }
      } else {
        this.isEdit = true;
      }
    },
    save() {
      this.loading = true;
      api.fetch('/variable/saveGlobalVariable ', {
        globalVariables: this.formDynamic.items,
      }).then((res) => {
        this.loading = false;
        this.isEdit = false;
        this.$Message.success(this.$t('message.globalValiable.success.update'));
        this.getGlobalValiableList();
        storage.set('isGlobalVariableChange', true);
      }).catch((err) => {
        this.loading = false;
      });
    },
    handleAdd() {
      this.formDynamic.items.push({
        key: '',
        value: '',
      });
    },
    handleRemove(item, index) {
      this.formDynamic.items.splice(index, 1);
    },
    handleCancel() {
      this.isEdit = false;
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
