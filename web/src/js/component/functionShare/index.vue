<template>
  <Modal
    v-if="node"
    v-model="show"
    width="420">
    <div slot="header">
      <p :title="node.name">{{$t('message.functionShare.ZZGX', {name: node.name})}}</p>
    </div>
    <div>
      <Form
        ref="shareForm"
        :model="shareForm"
        :rules="shareRule"
        :label-width="80"
        size="small">
        <Form-item
          v-if="!node.shared"
          :label="$t('message.functionShare.HSLX')"
          prop="fnType">
          <!--label-in-value 搭配onChange事件返回值是label和value-->
          <Select
            v-model="shareForm.fnType"
            label-in-value
            style="width: 300px;"
            @on-change="handleChange">
            <Option
              v-for="(item) in shareForm.list"
              :label="item.name"
              :value="item.id"
              :key="item.id" />
          </Select>
        </Form-item>
        <Form-item
          :label="$t('message.functionShare.GXYH')"
          prop="users">
          <Input
            v-model="shareForm.users"
            :disabled="shareEditing"
            type="textarea"
            :placeholder="$t('message.functionShare.SRGXYH')"
            style="width: 300px;">
          </Input>
        </Form-item>
      </Form>
    </div>
    <div slot="footer">
      <Button
        v-if="shareEditing"
        :loading="loading"
        type="primary"
        @click="modifyShare">{{$t('message.newConst.editor')}}</Button>
      <Button
        v-else
        :loading="loading"
        type="primary"
        @click="submit">{{$t('message.newConst.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import { uniq } from 'lodash';
export default {
  props: {
    loading: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      show: false,
      node: null,
      shareEditing: false,
      // 记录当前被选中的函数文件夹的label和key
      selectedItem: null,
      shareForm: {
        fnType: null,
        list: [],
        users: '',
      },
      shareRule: {
        fnType: [{
          required: true,
          type: 'number',
          message: this.$t('message.functionShare.XZGXHSLX'),
          trigger: 'change',
        }],
        users: [{
          required: true,
          message: this.$t('message.functionShare.ZSXZYWGXYH'),
          trigger: 'change',
        }],
      },
    };
  },
  watch: {
    loading(val) {
      if (!val) {
        this.show = false;
      }
    },
    show(val) {
      if (!val) {
        this.shareEditing = false;
        this.selectedItem = null;
        this.shareForm = {
          fnType: null,
          list: [],
          users: '',
        };
        this.$refs.shareForm.resetFields();
      }
    },
  },
  methods: {
    open(data) {
      let {
        shareUser,
        tree,
        node,
      } = data;
      if (data.isView) {
        this.shareEditing = true;
        this.shareForm.users = shareUser;
      }
      this.shareForm.list = tree;
      this.node = node.data;
      this.show = true;
    },
    handleChange(item) {
      this.selectedItem = item;
    },
    modifyShare() {
      this.shareEditing = false;
    },
    submit() {
      this.$refs.shareForm.validate((valid) => {
        if (!valid) return this.$Message.warning(this.$t('message.newConst.failedNotice'));
        let {
          fnType,
          users,
        } = this.shareForm;
        let option = {
          sharedUsers: users.split(','),
        };
        let method = 'update-share';
        if (!this.node.shared) {
          option = {
            shareParentId: fnType,
            // uniq去重
            sharedUsers: uniq(users.split(',')),
          };
          method = 'add-share';
        }
        this.$emit(method, option);
        this.show = false;
      });
    },
  },
};
</script>
