<template>
  <div>
    <Table
      :columns="columns"
      :data="datalist"
      :loading="loading"
      style="margin-bottom: 16px"
    >
      <template slot="name" slot-scope="{ row }">
        <router-link :to="`/datamodelcenter/tableManage/tableInfo/${row.id}`">
          {{ row.name }}
        </router-link>
      </template>
      <template slot-scope="{}" slot="action">
        <Button type="primary" size="small" style="margin-right: 5px">
          修改
        </Button>
        <Button type="error" size="small"> 删除 </Button>
      </template>
    </Table>
    <div class="page-line">
      <Page :total="100" show-sizer />
    </div>
  </div>
</template>

<script>
const data = [
  {
    id: 123,
    name: "表名",
    alias: "别名",
    size: "1G",
    owner: "enjoyyin",
    warehouseAttr: "主题域1-主题1",
    tableAttr: "压缩:SNAPPY",
    ltime: "2020-08-10",
    ctime: "2020-08-10",
    version: "版本列表",
    des: "描述",
    action: "操作",
  },
];
export default {
  methods: {
    handleDelete() {},
    handleEdit() {},
  },
  data() {
    return {
      columns: [
        {
          title: "表名",
          key: "name",
          slot: "name",
        },
        {
          title: "表别名",
          key: "alias",
        },
        {
          title: "表大小",
          key: "size",
        },
        {
          title: "属主",
          key: "owner",
        },
        {
          title: "数仓属性",
          key: "warehouseAttr",
        },
        {
          title: "表属性",
          key: "tableAttr",
        },
        {
          title: "版本",
          key: "version",
        },
        {
          title: "最后访问时间",
          key: "ltime",
        },
        {
          title: "创建时间",
          key: "ctime",
        },
        {
          title: "描述",
          key: "des",
        },
        {
          title: "操作",
          key: "action",
          slot: "action",
          minWidth: 80,
        },
      ],

      // 数据列表
      datalist: data,
      // 是否加载中
      loading: false,
      // 分页配置
      pageCfg: {
        current: 1,
        pageSize: 10,
      },
    };
  },
};
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
