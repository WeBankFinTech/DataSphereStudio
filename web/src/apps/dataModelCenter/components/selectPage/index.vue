<template>
  <Select
    :value="value"
    @input="$emit('input', $event)"
    :loading="loading"
    :placeholder="placeholder"
    :multiple="multiple"
    :filterable="searchMode"
    :remote="searchMode"
    :remote-method="handleGetData"
  >
    <Option
      v-for="(item, index) in dataList"
      :value="item.value"
      :label="item.label"
      :key="index"
    >
    </Option>
  </Select>
</template>

<script>
export default {
  props: {
    // value
    value: {},
    // 是否多选
    multiple: {},
    // 请求方法
    fetch: {
      type: Function,
      require: true,
    },
    // 是否远程搜索
    searchMode: {
      type: Boolean,
    },
    // 提示
    placeholder: {
      type: String,
    },
  },
  watch: {
    value: {
      handler(value) {
        if (value === undefined) return;
        if (value.length === 0) return;
        let nowData = [];
        if (Object.prototype.toString.call(value) === "[object Array]") {
          nowData.push(...value);
        } else if (typeof value === "string") {
          nowData.push(value);
        }
        if (nowData.length) {
          for (let i = 0; i < nowData.length; i++) {
            const element = nowData[i];
            if (element) {
              let isExist = this.dataList.find(
                (item) => item.value === element
              );
              if (!isExist) {
                this.dataList.unshift({
                  label: element,
                  value: element,
                });
              }
            }
          }
        }
      },
      immediate: true,
    },
  },
  data() {
    return {
      // 时间标记
      sign: null,
      // 数据
      dataList: [],
      // 是否加载中
      loading: false,
    };
  },
  mounted() {
    this.loading = true;
    this.fetch("")
      .then((data) => {
        this.loading = false;
        this.dataList = data.list;
      })
      .catch(() => {
        this.loading = false;
      });
  },
  methods: {
    // 搜索方法
    handleGetData(query = "") {
      // 结束之前的任务
      if (this.sign) clearTimeout(this.sign);
      // 创建新任务
      this.sign = setTimeout(() => {
        clearTimeout(this.sign);
        this.loading = true;
        this.fetch(query)
          .then((data) => {
            let value = this.value;
            let nowData;
            if (Object.prototype.toString.call(value) === "[object Array]") {
              nowData = value;
            } else if (typeof value === "string") {
              nowData = [value];
            }
            if (nowData.length) {
              for (let i = 0; i < nowData.length; i++) {
                const element = nowData[i];
                if (element) {
                  let isExist = data.list.find(
                    (item) => item.value === element
                  );
                  if (!isExist) {
                    data.list.unshift({
                      label: element,
                      value: element,
                    });
                  }
                }
              }
            }
            this.loading = false;
            this.dataList = data.list;
          })
          .catch(() => {
            this.loading = false;
          });
      }, 300);
    },
  },
};
</script>

<style>
</style>
