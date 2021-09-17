<template>
  <Modal
    title="新建/编辑"
    v-model="visible"
    @on-ok="handleOk"
    @on-cancel="$emit('update:visible', false)"
  >
    <Form :model="formState" label-position="top">
      <h3 style="margin-bottom: 12px"><b>基本信息</b></h3>
      <FormItem label="修饰词类别">
        <Input v-model="formState.name" placeholder="建议为中文名" />
      </FormItem>
      <FormItem label="描述">
        <Input type="textarea" v-model="formState.des" placeholder="描述" />
      </FormItem>
      <h3 style="margin-bottom: 12px"><b>作用范围</b></h3>
      <Row :gutter="12">
        <Col span="12">
          <FormItem>
            <Select v-model="formState.subjectDomain" placeholder="主题域">
              <Option
                v-for="item in subjectDomainList"
                :value="item.value"
                :key="item.value"
              >
                {{ item.label }}
              </Option>
            </Select>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem>
            <Select v-model="formState.layereds" placeholder="分层">
              <Option
                v-for="item in layeredList"
                :value="item.value"
                :key="item.value"
              >
                {{ item.label }}
              </Option>
            </Select>
          </FormItem>
        </Col>
      </Row>
      <h3 style="margin-bottom: 12px"><b>修饰词列表</b></h3>
      <Table
        style="margin-bottom: 12px"
        :columns="tokenListColumns"
        :data="formState.tokenList"
      >
        <template slot-scope="{ index }" slot="tokenName">
          <Input type="text" v-model="formState.tokenList[index].tokenName" />
        </template>
        <template slot-scope="{ index }" slot="tokenKey">
          <Input type="text" v-model="formState.tokenList[index].tokenKey" />
        </template>
        <template slot-scope="{ index }" slot="formula">
          <Input type="text" v-model="formState.tokenList[index].formula" />
        </template>
        <template slot-scope="{ index }" slot="action">
          <Button type="error" @click="handleDeleteOneToken(index)">
            删除
          </Button>
        </template>
      </Table>
      <div style="display: flex; justify-content: flex-end">
        <Button type="primary" @click="handleAddToken()">新增</Button>
      </div>
    </Form>
    <Spin v-if="loading" fix></Spin>
  </Modal>
</template>

<script>
const tokenListColumns = [
  {
    title: '修饰词名称',
    key: 'tokenName',
    slot: 'tokenName',
  },
  {
    title: '字段表示',
    key: 'tokenKey',
    slot: 'tokenKey',
  },
  {
    title: '计算公式',
    key: 'formula',
    slot: 'formula',
  },
  {
    title: '操作',
    slot: 'action',
  },
]
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
      tokenListColumns,
      // 是否加载中
      loading: false,
      // 主题域列表
      subjectDomainList: [
        {
          value: 'key1',
          label: '主题域1',
        },
      ],
      // 分层列表
      layeredList: [
        {
          value: 'key1',
          label: '分层1',
        },
      ],
      // 表单数据
      formState: {
        name: '',
        des: '',
        layereds: [],
        subjectDomain: [],
        tokenList: [
          {
            tokenName: '第一个词',
            tokenKey: 'tokenKey',
            formula: '1+1=2',
          },
        ],
      },
    }
  },
  methods: {
    async handleOk() {
      this.loading = true
      this.loading = false
      this.$emit('update:visible', false)
      this.$emit('finish')
    },
    handleDeleteOneToken(index) {
      this.formState.tokenList.splice(index, 1)
    },
    handleAddToken() {
      this.formState.tokenList.push({
        tokenName: '',
        tokenKey: '',
        formula: '',
      })
    },
  },
}
</script>

<style scoped lang="less"></style>
