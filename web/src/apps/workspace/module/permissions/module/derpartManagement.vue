<template>
  <div>
    <div class="formWrap">
      <Form ref="queries" :model="queries" inline>
        <FormItem
          :label="$t('message.permissions.derpartName')"
          :label-width="100"
        >
          <Input
            type="text"
            v-model="queries.departName"
            :placeholder="$t('message.permissions.derpartPlaceholder')"
            style="width: 272px"
          >
          </Input>
        </FormItem>
        <FormItem>
          <Button @click="handleReset()" style="margin-left: 20px">{{
            $t("message.permissions.reset")
          }}</Button>
          <Button
            type="primary"
            @click="handleQuery()"
            style="margin-left: 8px"
            >{{ $t("message.permissions.query") }}</Button
          >
        </FormItem>
      </Form>
    </div>
    <div class="tableWrap">
      <div class="addWrap">
        <Button type="primary" @click="handleAdd()" icon="md-add">{{
          $t("message.permissions.add")
        }}</Button>
      </div>
      <Table :columns="columns" :data="data" show-header="false"></Table>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      queries: {
        departName: ""
      },
      columns: [
        {
          title: this.$t("message.permissions.deptName"),
          key: "deptName",
        },
        {
          title: this.$t("message.permissions.delFlag"),
          key: "delFlag"
        },
        {
          title: this.$t("message.permissions.createBy"),
          key: "createBy"
        },
        {
          title: this.$t("message.permissions.phone"),
          key: "phone"
        },
        {
          title: this.$t("message.permissions.email"),
          key: "email"
        },
        {
          title: this.$t("message.permissions.createTime"),
          key: "createTime"
        },
        {
          title: this.$t("message.permissions.operation"),
          key: "operation",
          fixed: "right",
          width: 200
        }
      ],
      data: [
        {
          searchValue: null,
          createBy: "admin",
          createTime: "2021-05-26 10:23:41",
          updateBy: null,
          updateTime: null,
          remark: null,
          params: {},
          deptId: 112,
          parentId: 102,
          ancestors: "0,100,102",
          deptName: "销售一部",
          orderNum: "2",
          leader: null,
          phone: null,
          email: null,
          status: "0",
          delFlag: "0",
          parentName: null,
          children: []
        },
        {
          searchValue: null,
          createBy: "admin",
          createTime: "2021-05-24 16:00:08",
          updateBy: null,
          updateTime: null,
          remark: null,
          params: {},
          deptId: 113,
          parentId: 102,
          ancestors: "0,100,102",
          deptName: "销售部",
          orderNum: "3",
          leader: "aaa",
          phone: null,
          email: null,
          status: "1",
          delFlag: "0",
          parentName: null,
          children: []
        }
      ]
    };
  },
  methods: {
    handleReset() {},
    handleQuery() {},
    handleAdd() {}
  }
};
</script>
<style lang="scss" scoped>
.formWrap {
  width: 100%;
  padding: 20px;
  padding-bottom: 0px;
  border-bottom: 1px solid #dee4ec;
}
.tableWrap {
  width: 100%;
  padding: 0 24px;
  .addWrap {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 100%;
    height: 72px;
  }
}
</style>
