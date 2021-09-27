<template>
  <Modal
    title="版本列表"
    v-model="visible"
    :width="700"
    @on-ok="handleOk"
    @on-cancel="$emit('update:visible', false)"
  >
    <Table :columns="columns" :data="data">
      <template v-slot:action>
        <Button size="small">打开</Button>
        <Button size="small">回退</Button>
      </template>
    </Table>
    <Spin v-if="loading" fix></Spin>
  </Modal>
</template>

<script>
export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
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
  emits: ["finish", "_changeVisible"],
  data() {
    return {
      columns: [
        {
          title: "版本",
          key: "versionId",
        },
        {
          title: "创建者",
          key: "created",
        },
        {
          title: "版本注释",
          key: "notes",
        },
        {
          title: "创建时间",
          key: "createTime",
        },
        {
          title: "操作",
          key: "action",
          slot: "action",
        },
      ],
      data: [
        {
          versionId: "v0002",
          created: "enjoyyin",
          notes: "更新内容",
          createTime: "2016-10-03",
        },
      ],
      // 是否加载中
      loading: false,
    };
  },
  methods: {
    async handleOk() {
      this.loading = true;
      this.loading = false;
      this.$emit("_changeVisible", false);
      this.$emit("finish");
    },
  },
};
</script>

<style scoped lang="less"></style>
