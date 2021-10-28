<template>
  <Select
    :value="_value"
    @input="$emit('_change', $event)"
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
  model: {
    prop: "_value",
    event: "_change",
  },
  props: {
    _value: {
      type: String,
    },
    // 请求方法
    fetch: {
      type: Function,
      require: true,
    },
    // 是否多选
    multiple: {},
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
    _value: {
      handler(value) {
        if (value === undefined) return;
        let nowData = [];
        if (this.multiple) {
          nowData.push(...value);
        } else {
          nowData.push(value);
        }
        if (nowData.length) {
          for (let i = 0; i < nowData.length; i++) {
            const element = nowData[i];
            if (element !== "") {
              let isExist = this.dataList.find(
                (item) => item.value === element
              );
              if (isExist === undefined) {
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
            let nowData = [];
            if (this.multiple) {
              nowData.push(...this._value);
            } else {
              nowData.push(this._value);
            }
            if (nowData.length) {
              for (let i = 0; i < nowData.length; i++) {
                const element = nowData[i];
                if (element !== "") {
                  let isExist = data.list.find(
                    (item) => item.value === element
                  );
                  if (isExist === undefined) {
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
