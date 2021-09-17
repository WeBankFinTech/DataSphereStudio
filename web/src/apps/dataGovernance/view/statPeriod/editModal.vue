<template>
  <Modal
    title="新建/编辑"
    v-model="visible"
    @on-ok="handleOk"
    @on-cancel="$emit('update:visible', false)"
  >
    <Form :model="formState" label-position="top">
      <FormItem label="名称">
        <Input v-model="formState.name" placeholder="建议输入中文名称"></Input>
      </FormItem>
      <FormItem label="英文缩写">
        <Input
          v-model="formState.ename"
          placeholder="只支持英文数据及下划线"
        ></Input>
      </FormItem>
      <h3 style="margin-bottom: 12px"><b>计算公式</b></h3>
      <Row :gutter="12">
        <Col span="12">
          <FormItem label="开始时间">
            <Input type="textarea" placeholder="例如：${runDate}"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="结束时间">
            <Input type="textarea" placeholder="例如：${runDate}"></Input>
          </FormItem>
        </Col>
      </Row>
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
      <FormItem label="描述">
        <Input
          type="textarea"
          v-model="formState.des"
          placeholder="描述"
        ></Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix />
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
