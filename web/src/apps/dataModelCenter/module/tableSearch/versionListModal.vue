<template>
  <Modal
    title="版本列表"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    :width="800"
  >
    <Table :columns="columns" :data="data">
      <template slot-scope="{ row }" slot="version">
        V{{ row.version }}
      </template>
      <template slot-scope="{ row }" slot="isMaterialized">
        {{ row.isMaterialized ? "以执行建表" : "未执行建表" }}
      </template>
      <template slot-scope="{ row }" slot="createTime">
        {{ row.createTime | formatDate }}
      </template>
      <template slot-scope="{ row }" slot="action">
        <Button
          size="small"
          @click="handleOpenVersion(row)"
          style="margin-right: 5px"
        >
          打开
        </Button>
        <Button size="small" @click="handleGoBackVersion(row.version)">
          回退
        </Button>
      </template>
    </Table>
    <Spin v-if="loading" fix></Spin>
    <div slot="footer"></div>
  </Modal>
</template>

<script>
import {
  getVersionListByName,
  tableVersionRollback,
} from "@/apps/dataModelCenter/service/api/tableManage";
import formatDate from "@/apps/dataModelCenter/utils/formatDate";

export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  filters: { formatDate },
  props: {
    _visible: {
      type: Boolean,
      required: true,
    },
    name: {
      type: String,
    },
  },
  emits: ["_changeVisible", "finish", "open"],
  watch: {
    _visible(val) {
      if (val && this.name) this.handleGetByName(this.name);
    },
  },
  data() {
    return {
      columns: [
        {
          title: "版本",
          key: "version",
          slot: "version",
        },
        {
          title: "状态",
          key: "isMaterialized",
          slot: "isMaterialized",
        },
        {
          title: "创建者",
          key: "creator",
        },
        {
          title: "版本注释",
          key: "comment",
        },
        {
          title: "创建时间",
          key: "createTime",
          slot: "createTime",
        },
        {
          title: "操作",
          key: "action",
          slot: "action",
        },
      ],
      data: [],
      loading: false,
    };
  },
  methods: {
    // 根据表名获取版本列表
    async handleGetByName(name) {
      this.loading = true;
      let { list } = await getVersionListByName(name);
      this.loading = false;
      this.data = list;
    },
    // 表版本回退
    async handleGoBackVersion(version) {
      this.loading = true;
      try {
        await tableVersionRollback(this.name, version);
        this.loading = false;
        this.$emit("_changeVisible", false);
        this.$emit("finish");
      } catch (error) {
        this.loading = false;
      }
    },
    // 打开版本
    async handleOpenVersion(data) {
      this.$emit("_changeVisible", false);
      this.$emit("open", data);
    },
  },
};
</script>

<style scoped lang="less"></style>
