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

    <!-- bottom -->
    <div class="overview-b">
      <!-- bottom left -->
      <div class="overview-b-l">
        <i-title :title="'表占有存储 Top10'"></i-title>
        <Table
          :context="tableSelf"
          :columns="columns1"
          :data="data1"
          width="558"
          size="large"
        ></Table>
      </div>

      <!-- bottom right -->
      <div class="overview-b-r">
        <!-- <Title :title="'表读取次数 Top10'"></Title>
        <Table
          :columns="columns2"
          :data="data2"
          width="558"
          size="large"
        ></Table> -->
      </div>
    </div>
  </div>
</template>
<script>
import { getHiveSummary, getTopStorage } from "../../service/api";
import iTitle from "../common/title.vue";
import ICard from "../common/iCard.vue";
export default {
  components: {
    iTitle,
    ICard
  },
  data() {
    return {
      models: [
        {
          title: "数据库总数",
          iconName: "shujukuzongshu",
          content: 2,
          unit: "个"
        },
        { title: "表总数", iconName: "biaozongshu", content: 6, unit: "个" },
        {
          title: "总存容量",
          iconName: "zongcunchuliang",
          content: 296.25,
          unit: "MB"
        }
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
                  color: "#3495F7"
                },
                style: {
                  fontSize: "15px"
                }
              }),
              h(
                "span",
                {
                  style: {
                    marginLeft: "4.8px",
                    fontSize: "14px",
                    color: "#3495F7"
                  }
                },
                params.row.tableName
              )
            ]);
          }
        },
        {
          title: "存储量",
          key: "storage"
        }
      ],
      columns2: [
        {
          title: "表名",
          key: "tableName",
          render: (h, params) => {
            return h("div", [
              h("SvgIcon", {
                props: {
                  "icon-class": "biao",
                  color: "#3495F7"
                },
                style: {
                  fontSize: "15px"
                }
              }),
              h(
                "span",
                {
                  style: {
                    marginLeft: "4.8px",
                    fontSize: "14px",
                    color: "#3495F7"
                  }
                },
                params.row.tableName
              )
            ]);
          }
        },
        {
          title: "读取次数",
          key: "readTimes"
        }
      ],
      data1: [],
      data2: [
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" },
        { tableName: "Table2", readTimes: "36次" }
      ]
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

    // 获取 数据资产概要
    getDataAssetsSummary() {
      let that = this;
      getHiveSummary()
        .then(data => {
          if (data.result) {
            const { hiveStore, hiveDb, hiveTable } = data.result;
            data.result["hiveStore"] = (hiveStore / 1024 / 1024).toFixed(2);
            const models = that.models.slice(0);
            models[0].content = hiveDb;
            models[1].content = hiveTable;
            models[2].content = data.result["hiveStore"];
            that.models = models;
          }
          console.log(data);
        })
        .catch(err => {
          console.log("getDataAssetsSummary", err);
        });
    },
    getTblTopStorage() {
      let that = this;
      getTopStorage()
        .then(data => {
          if (data.result) {
            that.data1 = data.result.slice(0);
          }
        })
        .catch(err => {
          that.$Message.error(err);
        });
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.overview-wrap {
  min-height: 100%;
  padding-left: 24px;
  padding-top: 24px;
  padding-right: 3px;
  border-top: 24px solid #dee4ec;
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
