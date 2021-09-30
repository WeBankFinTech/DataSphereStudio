<template>
  <Modal
    title="版本列表"
    v-model="visible"
    :width="700"
    @on-cancel="$emit('_changeVisible', false)"
  >
    <Table :columns="columns" :data="data">
      <template slot-scope="{ row }" slot="version">
        V{{ row.version }}
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
  </Modal>
</template>

<script>
import {
  getIndicatorsVersionList,
  rollbackIndicatorsVersion,
} from "../../service/api";
import formatDate from "../../utils/formatDate";

export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  filters: {
    formatDate,
  },
  computed: {
    visible: {
      get() {
        return this._visible;
      },
      set(val) {
        this.$emit("_changeVisible", val);
      },
    },
  },
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
          title: "创建者",
          key: "owner",
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
    async handleGetByName(name) {
      this.loading = true;
      let { list } = await getIndicatorsVersionList(name);
      this.loading = false;
      this.data = list;
    },
    async handleGoBackVersion(version) {
      this.loading = true;
      try {
        await rollbackIndicatorsVersion(this.name, version);
        this.loading = false;
        this.$emit("_changeVisible", false);
        this.$emit("finish");
      } catch (error) {
        this.loading = false;
      }
    },
    async handleOpenVersion(data) {
      this.$emit("_changeVisible", false);
      this.$emit("open", data);
    },
  },
};
</script>

<style scoped lang="less"></style>
