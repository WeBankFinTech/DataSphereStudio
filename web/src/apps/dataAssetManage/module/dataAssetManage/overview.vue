<template>
  <div class="overview-wrap">
    <!-- top -->
    <div class="overview-t">
      <i-title :title="'总体计量'"></i-title>
      <div class="overview-t-card">
        <div v-for="model in models" :key="model.title">
          <ICard :model="model"></ICard>
        </div>
      </div>
    </div>
    <div class="overview-b">
      <div class="overview-b-l">
        <i-title :title="'表占有存储 Top10'"></i-title>
        <Table
          :context="tableSelf"
          :columns="columns1"
          :data="data1"
          width="558"
          size="large"
        >
        </Table>
      </div>
    </div>
  </div>
</template>
<script>
import { getHiveSummary, getTopStorage } from "../../service/api";
import iTitle from "../common/title.vue";
import ICard from "../common/iCard.vue";
export default {
  components: { iTitle, ICard },
  data() {
    return {
      models: [
        {
          title: "数据库总数",
          iconName: "shujukuzongshu",
          content: 0,
          unit: "个",
        },
        {
          title: "表总数",
          iconName: "biaozongshu",
          content: 0,
          unit: "个",
        },
        {
          title: "总存容量",
          iconName: "zongcunchuliang",
          content: 0,
          unit: "MB",
        },
      ],
      tableSelf: this,
      columns1: [
        {
          title: "表名",
          key: "tableName",
          render: (h, params) => {
            return h("div", [
              h("SvgIcon", {
                props: {
                  "icon-class": "biao",
                  color: "#3495F7",
                },
                style: {
                  fontSize: "15px",
                },
              }),
              h(
                "span",
                {
                  style: {
                    marginLeft: "4.8px",
                    fontSize: "14px",
                    color: "#3495F7",
                  },
                },
                params.row.tableName
              ),
            ]);
          },
        },
        {
          title: "存储量",
          key: "storage",
        },
      ],
      data1: [],
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.getDataAssetsSummary();
      this.getTblTopStorage();
    },
    /**
     * 获取 数据资产概要
     */
    getDataAssetsSummary() {
      let that = this;
      getHiveSummary()
        .then((data) => {
          if (data.result) {
            const { hiveStore, hiveDb, hiveTable } = data.result;
            let n = this.transformCompany(hiveStore);
            const models = that.models.slice(0);
            models[0].content = hiveDb;
            models[1].content = hiveTable;
            models[2].content = n.num;
            models[2].unit = n.unit;
            that.models = models;
          }
          console.log(data);
        })
        .catch((err) => {
          console.log("getDataAssetsSummary", err);
        });
    },
    /**
     * 转换存储单位
     */
    transformCompany(number) {
      if (!number) {
        return { num: 0, unit: "B", str: "0B" };
      }
      let unitArr = ["B", "KB", "MB", "GB", "TB"];
      for (let i = unitArr.length - 1; i >= 0; i--) {
        const unit = unitArr[i];
        let res = number / Math.pow(1024, i);
        if (res > 1) {
          return { num: res.toFixed(2), unit, str: res.toFixed(2) + unit };
        }
      }
    },
    /**
     * 获取排行
     */
    getTblTopStorage() {
      getTopStorage()
        .then((data) => {
          if (data.result) {
            console.log(data.result);
            for (let i = 0; i < data.result.length; i++) {
              let vlaue = this.transformCompany(data.result[i].storage);
              data.result[i].storage = vlaue ? vlaue.str : "";
            }
            this.data1 = data.result;
          }
        })
        .catch((err) => {
          this.$Message.error(err);
        });
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.overview-wrap {
  min-height: 100%;
  padding-left: 24px;
  padding-top: 24px;
  padding-right: 3px;
  .overview-t {
    &-card {
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
    }
  }
  .overview-b {
    margin-top: 24px;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    &-l {
      margin-right: 25px;
    }
  }
}
</style>
