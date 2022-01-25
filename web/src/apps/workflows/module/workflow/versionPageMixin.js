export default {
  data() {
    return {
      versionPage: {
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      }
    }
  },
  computed: {
    versionPageData() {
      return this.versionData.filter((item, index) => {
        return (this.versionPage.pageNow - 1) * this.versionPage.pageSize <= index && index < this.versionPage.pageNow * this.versionPage.pageSize;
      })
    }
  },
  methods: {
    // 切换分页
    change(val) {
      this.versionPage.pageNow = val;
    },
    // 页容量变化
    changeSize(val) {
      this.versionPage.pageSize = val;
      this.versionPage.pageNow = 1;
    },
  }
}