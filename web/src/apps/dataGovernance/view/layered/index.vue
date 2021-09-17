<template>
  <div class="console-page">
    <div>
      <div class="title-line">
        <span class="title">
          <span>系统预置分层 </span>
        </span>
      </div>
      <Table
        :columns="columns"
        :data="datalist"
        :loading="loading"
        style="margin-bottom:16px"
      >
        <template slot="status">
          <Tag>状态</Tag>
        </template>
        <template slot-scope="{}" slot="action">
          <Button type="primary" size="small" style="margin-right: 5px">
            编辑
          </Button>
          <Button type="error" size="small">禁用</Button>
        </template>
      </Table>
    </div>
    <div>
      <div class="title-line">
        <span class="title">
          <span>自定义分层 </span>
        </span>
        <Button type="primary" icon="md-add" @click="handleCreate">
          创建自定义分层
        </Button>
      </div>
      <Table
        :columns="columns"
        :data="datalist"
        :loading="loading"
        style="margin-bottom:16px"
      >
        <template slot="status">
          <Tag>状态</Tag>
        </template>
        <template slot-scope="{}" slot="action">
          <Button type="primary" size="small" style="margin-right: 5px">
            编辑
          </Button>
          <Button type="error" size="small" style="margin-right: 5px">
            禁用
          </Button>
          <Button type="error" size="small">删除</Button>
        </template>
      </Table>
      <div class="page-line">
        <Page :total="100" show-sizer />
      </div>
    </div>
    <edit-modal
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
    name: '分层名字',
    cname: 'cname',
    des: '描述描述描述',
    created: '创建人',
    status: '可用',
    authority: '所有用户',
    action: '操作'
  }
]
export default {
  components: { EditModal },
  methods: {
    handleModalFinish() {},
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: 'create'
      }
    },
    handleDelete() {},
    handleEdit(id) {
      this.modalCfg = {
        visible: true,
        mode: 'edit',
        id
      }
    },
    handleDisable() {}
  },
  data() {
    return {
      columns: [
        {
          title: '分层名称',
          key: 'name'
        },
        {
          title: '英文名',
          key: 'cname'
        },
        {
          title: '描述',
          key: 'des'
        },
        {
          title: '负责人',
          key: 'created'
        },
        {
          title: '分层选择权限',
          key: 'authority'
        },
        {
          title: '启用状态',
          key: 'status',
          slot: 'status'
        },
        {
          title: '操作',
          key: 'action',
          slot: 'action'
        }
      ],
      datalist: data,
      // 弹窗参数
      modalCfg: {
        mode: '',
        id: '',
        visible: false
      },
      // 是否加载中
      loading: false,
      // 分页配置
      pageCfg: {
        current: 1,
        pageSize: 10
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  .title {
    border-left: 6px solid #1890ff;
    padding-left: 6px;
  }
}
.page-line {
  display: flex;
  justify-content: flex-end;
}
</style>
