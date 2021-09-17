<template>
  <Modal
    title="新建/编辑分层"
    v-model="visible"
    @on-ok="handleOk"
    @on-cancel="$emit('update:visible', false)"
  >
    <Form :model="formState" label-position="top">
      <FormItem label="名称">
        <Input v-model="formState.name" placeholder="名称"></Input>
      </FormItem>
      <FormItem label="英文名">
        <Input v-model="formState.ename" placeholder="英文名"></Input>
      </FormItem>
      <FormItem label="负责人">
        <Input v-model="formState.created" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="可用角色">
        <Select v-model="formState.authority" multiple placeholder="可用角色">
          <Option
            v-for="item in authorityList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="可用库">
        <Select v-model="formState.usablelib" placeholder="可用库">
          <Option
            v-for="item in authorityList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="描述">
        <Input
          type="textarea"
          v-model="formState.des"
          placeholder="描述"
        ></Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
  </Modal>
</template>

<script>
export default {
  props: {
    // 是否可见
    visible: {
      type: Boolean,
      required: true,
    },
    // 模式
    mode: {
      type: String,
      required: true,
    },
    id: {
      type: String,
      default: '',
    },
  },
  emits: ['finish', 'cancel', 'update:visible'],
  data() {
    return {
      // 是否加载中
      loading: false,
      // 表单数据
      formState: {
        name: '',
        ename: '',
        created: '',
        authority: [],
        des: '',
      },
      usablelib: [
        {
          value: 'New York',
          label: 'New York',
        },
        {
          value: 'London',
          label: 'London',
        },
      ],
      authorityList: [
        {
          value: 'New York',
          label: 'New York',
        },
        {
          value: 'London',
          label: 'London',
        },
      ],
    }
  },
  methods: {
    async handleOk() {
      this.loading = true
      this.loading = false
      this.$emit('update:visible', false)
      this.$emit('finish')
    },
  },
}
</script>

<style scoped lang="less"></style>
