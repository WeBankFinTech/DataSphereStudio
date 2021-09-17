<template>
  <div class="console-page">
    <div class="top-line">
      <Input
        search
        enter-button
        placeholder="输入名称搜索"
        style="width: 300px"
      />
      <Button type="primary" icon="md-add" @click="handleCreate">
        创建主题域
      </Button>
    </div>
    <Table
      :columns="columns"
      :data="datalist"
      :loading="loading"
      style="margin-bottom: 16px"
    >
      <template slot-scope="{}" slot="action">
        <Button type="primary" size="small" style="margin-right: 5px">
          编辑
        </Button>
        <Button type="error" size="small">删除</Button>
      </template>
    </Table>
    <div class="page-line">
      <Page :total="100" show-sizer />
    </div>
    <EditModal
      :visible.sync="modalCfg.visible"
      :id="modalCfg.id"
      :mode="modalCfg.mode"
    />
  </div>
</template>

<script>
import EditModal from './editModal.vue'
const data = [
  {
    name: '主题域名字',
    ename: 'cname',
    des: '描述描述描述',
    created: '创建人',
    ctime: '2010-1-1',
    authority: '权限1,权限2',
    action: '操作',
  },
]
export default {
  components: { EditModal },
  methods: {
    handleModalFinish() {},
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: 'create',
      }
    },
    handleDelete() {},
    handleEdit(id) {
      this.modalCfg = {
        visible: true,
        mode: 'edit',
        id,
      }
    },
    handleSearch() {},
  },
  data() {
    return {
      columns: [
        {
          title: '主题域名称',
          key: 'name',
        },
        {
          title: '主题域英文名',
          key: 'ename',
        },
        {
          title: '描述',
          key: 'des',
        },
        {
          title: '主题域选择权限',
          key: 'authority',
        },
        {
          title: '创建人',
          key: 'created',
        },
        {
          title: '创建时间',
          key: 'ctime',
        },
        {
          title: '操作',
          key: 'action',
          slot: 'action',
        },
      ],
      datalist: data,
      // 弹窗参数
      modalCfg: {
        mode: '',
        id: '',
        visible: false,
      },
      // 是否加载中
      loading: false,
      // 分页配置
      pageCfg: {
        current: 1,
        pageSize: 10,
      },
    }
  },
}
</script>

<style lang="scss" scoped>
.top-line {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
}
.page-line {
  display: flex;
  justify-content: flex-end;
}
</style>
