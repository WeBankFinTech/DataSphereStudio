<template>
  <Select
    v-model="value"
    filterable
    remote
    @on-query-change="handleSearch"
    :remote-method="() => {}"
    :loading="loading"
    :placeholder="placeholder"
  >
    <Option
      v-for="(item, index) in dataList"
      :value="String(item.value)"
      :label="item.label"
      :key="index"
    >
    </Option>
    <Option :value="searchVal || ''">
      <div @click.stop>
        <Page
          :total="total"
          size="small"
          show-total
          :page-size="10"
          :current.sync="page"
        />
      </div>
    </Option>
  </Select>
</template>

<script>
export default {
  model: {
    prop: "_value",
    event: "_change",
  },
  props: {
    _value: {
      type: [String, Number, Array],
      require: true,
    },
    fetch: {
      type: Function,
      require: true,
    },
    placeholder: {
      type: String,
    },
  },
  watch: {},
  data() {
    return {
      searchVal: "",
      total: 0,
      page: 1,
      dataList: [],
      loading: false,
      value: "",
    };
  },
  watch: {
    page() {
      this.handleGetData();
    },
    _value(value) {
      if (this.value !== value) {
        this.value = String(value);
      }
    },
    value(value) {
      this.$emit("_change", value);
    },
  },
  mounted() {
    this.handleGetData();
  },
  methods: {
    handleGetData() {
      this.loading = true;
      try {
        this.fetch(this.searchVal, this.page, 10).then((data) => {
          this.loading = false;
          this.dataList = data.list;
          this.total = data.total;
        });
      } catch (error) {
        this.loading = false;
      }
    },
    handleSearch(query) {
      this.searchVal = query;
      this.handleGetData();
    },
  },
};
</script>

<style>
</style>
