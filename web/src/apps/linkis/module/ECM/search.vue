<template>
  <Form :model="searchBar" :rules="ruleInline" inline>
    <FormItem prop="engineInstance" label="JobID">
      <Input :maxlength="50" v-model="searchBar.engineInstance" :placeholder="$t('message.linkis.searchName')"/>
    </FormItem>
    <FormItem prop="owner" :label="$t('message.linkis.formItems.engine.label')">
      <Select  v-model="searchBar.owner">
        <Option
          v-for="(item) in statusType"
          :label="item.label"
          :value="item.value"
          :key="item.value"/>
      </Select>
    </FormItem>
    <FormItem prop="status" :label="$t('message.linkis.formItems.status.label')">
      <Select v-model="searchBar.status">
        <Option
          v-for="(item) in statusType"
          :label="item.label"
          :value="item.value"
          :key="item.value"/>
      </Select>
    </FormItem>
    <FormItem>
      <Button type="primary" @click="search">
        {{ $t('message.linkis.search') }}
      </Button>
    </FormItem>
  </Form>

</template>
<script>

export default {
  components: {
  },
  props: {

  },
  data() {
    return {
      ruleInline: {},
      statusType: [
        { label: "open", value: 1 },
        { label: "close", value: 0 },
      ],
      searchBar: {
        engineInstance: "",
        status: "",
        owner: "",
      },
    };
  },
  computed: {
  },
  created() {

  },
  mounted() {

  },
  activated() {
  },
  methods: {
    search() {
      this.$emit("search", this.searchBar)
    }
  },
};
</script>
<style lang="scss" scoped>
  .ivu-form {
    display: flex;
    .ivu-form-item {
      display: flex;
      margin-right: 30px;
      flex: none;
    }
  }
</style>
