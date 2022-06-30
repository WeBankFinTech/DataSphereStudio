<template>
  <div class="console-page">
    <div class="top-line">
      <Input
        search
        enter-button
        clearable
        placeholder="输入名称搜索"
        style="width: 300px"
        v-model="searchVal"
        @on-search="handleSearch"
        @on-clear="handleSearch"
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
      <template slot-scope="{ row }" slot="principalName">
        <Tag v-for="name of row.principalName.split(',')" :key="name">
          {{ name }}
        </Tag>
      </template>
      <!--<template slot-scope="{ row }" slot="isAvailable">
        {{ row.isAvailable ? '启用' : '禁用' }}
      </template>-->
      <template slot-scope="{ row }" slot="createTime">
        {{ row.createTime | formatDate }}
      </template>
      <template slot-scope="{ row }" slot="updateTime">
        {{ row.updateTime | formatDate }}
      </template>
      <template slot-scope="{ row }" slot="action">
        <Button
          size="small"
          @click="handleEdit(row.name)"
          style="margin-right: 5px"
        >
          编辑
        </Button>
        <!--<Button
          size="small"
          @click="handleDisable(row.id)"
          style="margin-right: 5px"
          v-if="row.isAvailable"
        >
          禁用
        </Button>
        <Button
          type="primary"
          size="small"
          @click="handleEnable(row.id)"
          style="margin-right: 5px"
          v-else
        >
          启用
        </Button>-->

        <Button type="error" size="small" @click="handleDelete(row.name)">
          删除
        </Button>
      </template>
    </Table>
    <EditModal
      v-model="modalCfg.visible"
      :id="modalCfg.name"
      :mode="modalCfg.mode"
      @finish="handleModalFinish"
    />
  </div>
</template>

<script>
import EditModal from './editModal.vue'
import formatDate from '../../utils/formatDate'
import {
  getThemedomains,
  deleteThemedomains,
  enableThemedomains,
  disableThemedomains,
} from '../../service/api'
export default {
  components: { EditModal },
  methods: {
    handleModalFinish() {
      this.handleGetData()
    },
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: 'create',
      }
    },
    async handleDelete(id) {
      this.loading = true
      await deleteThemedomains(id).then(() => {
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
      this.handleGetData()
    },
    handleEdit(name) {
      this.modalCfg = {
        visible: true,
        mode: 'edit',
        name,
      }
    },
    async handleEnable(id) {
      this.loading = true
      await enableThemedomains(id)
      this.loading = false
      this.handleGetData()
    },
    async handleDisable(id) {
      this.loading = true
      await disableThemedomains(id)
      this.loading = false
      this.handleGetData()
    },
    handleSearch() {
      this.handleGetData()
    },
    async handleGetData() {
      this.loading = true
      let data = await getThemedomains(
        this.searchVal.trim()
      )
      this.loading = false
      this.datalist = data.result
    }
  },
  filters: {
    formatDate,
  },
  mounted() {
    this.handleGetData()
  },
  watch: {
    pageCfg: {
      handler: 'handleGetData',
      deep: true,
    },
  },

  data() {
    return {
      searchVal: '',
      columns: [
        {
          title: '名称',
          key: 'name',
        },
        /*{
          title: '状态',
          key: 'isAvailable',
          slot: 'isAvailable',
        },*/
        {
          title: '描述',
          key: 'description',
          ellipsis: true,
        },
        /*{
          title: '选择权限',
          key: 'principalName',
          slot: 'principalName',
        },
        {
          title: '负责人',
          key: 'owner',
        },*/
        {
          title: '创建时间',
          key: 'createTime',
          slot: 'createTime',
        },
        {
          title: '更新时间',
          key: 'updateTime',
          slot: 'updateTime',
        },
        {
          title: '操作',
          slot: 'action',
          minWidth: 60,
        },
      ],
      datalist: [],
      // 弹窗参数
      modalCfg: {
        mode: '',
        id: NaN,
        visible: false,
      },
      // 是否加载中
      loading: false,
      // 分页配置
      pageCfg: {
        page: 1,
        pageSize: 10,
        total: 10,
      },
    }
  },
}
</script>

<style lang="scss" scoped>
.console-page {
  margin: 0 24px;
}
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
