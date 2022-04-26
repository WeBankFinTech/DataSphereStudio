<template>
  <div class="overview-wrap">
    <!-- top -->
    <div class="overview-t">
      <Title :title="'总体计量'"></Title>

      <div class="overview-t-card">
        <div v-for="model in models" :key="model.title" style="flex: 1;">
          <ICard :model="model"></ICard>
        </div>
      </div>
    </div>

    <!-- bottom -->
    <div class="overview-b">
      <!-- bottom left -->
      <div class="overview-b-l">
        <Title :title="'表占有存储 Top10'"></Title>
        <Table
          :context="tableSelf"
          :columns="columns1"
          :data="data1"
          size="large"
        ></Table>
      </div>

      <!-- bottom right -->
      <!-- <div class="overview-b-r">
        <Title :title="'表读取次数 Top10'"></Title>
        <Table
          :columns="columns2"
          :data="data2"
          width="558"
          size="large"
        ></Table>
      </div> -->
    </div>
  </div>
</template>
<script>
import { getHiveSummary, getTopStorage } from "../../service/api";
import Title from "../common/title.vue";
import ICard from "../common/iCard.vue";
export default {
  components: {
    Title,
    ICard
  },
  data() {
    return {
      models: [
        {
          title: "数据库总数",
          iconName: "shujukuzongshu",
          content: 0,
          unit: "个"
        },
        { title: "表总数", iconName: "biaozongshu", content: 0, unit: "个" },
        {
          title: "总存容量",
          iconName: "zongcunchuliang",
          content: 0,
          unit: ""
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
                  "icon-class": "biao"
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
                    color: "#2E92F7",
                    cursor: params.row.guid ? "pointer" : "not-allowed"
                  },
                  on: {
                    click: () => {
                      this.toTableInfo(params.row.guid);
                    }
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
      let hiveStore_unit = "";
      getHiveSummary()
        .then(data => {
          if (data.result) {
            const { hiveStore, hiveDb, hiveTable } = data.result;
            let len = hiveStore.toString().length;
            if (len <= 7) {
              data.result["hiveStore"] = (hiveStore / 1024 / 1024).toFixed(2);
              hiveStore_unit = "MB";
            } else if (len > 7 && len < 10) {
              data.result["hiveStore"] = (
                hiveStore /
                1024 /
                1024 /
                1024
              ).toFixed(2);
              hiveStore_unit = "GB";
            } else {
              data.result["hiveStore"] = (
                hiveStore /
                1024 /
                1024 /
                1024 /
                1024
              ).toFixed(2);
              hiveStore_unit = "TB";
            }
            const models = that.models.slice(0);
            models[0].content = hiveDb;
            models[1].content = hiveTable;
            models[2].content = data.result["hiveStore"];
            models[2].unit = hiveStore_unit;
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
            data.result.forEach(item => {
              item.storage = item.storage + ""
              let tempLen = Math.floor(item.storage.length / 4)
              let len = tempLen > 2 ? 3 : tempLen
              item.storage = (item.storage / Math.pow(1024, len + 1)).toFixed(2)
              switch(len){
                case 0:
                  item.storage = item.storage + 'KB'
                  break
                case 1:
                  item.storage = item.storage + 'MB'
                  break
                case 2:
                  item.storage = item.storage + 'GB'
                  break
                default:
                  item.storage = item.storage + 'TB'
                  break
              }
            })
            that.data1 = data.result
          }
        })
        .catch(err => {
          that.$Message.error(err);
        });
    },
    // 根據{guid}跳轉到表詳情
    toTableInfo(guid) {
      if (!guid) return;
      const workspaceId = this.$route.query.workspaceId;
      this.$router.push({
        name: "dataGovernance/assets/info",
        params: { guid },
        query: { workspaceId }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.overview-wrap {
  min-height: 100%;
  padding-left: 24px;
  padding-top: 24px;
  padding-right: 3px;
  border-top: 24px solid #dee4ec;
  @include border-color(#dee4ec, $dark-border-color-base);
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
    margin-right: 24px;
    margin-bottom: 24px;
    &-l {
      width: 100%;
    }
  }
}
::v-deep .ivu-table-wrapper {
  border: none;
}
::v-deep .ivu-table:after {
  width: 0;
}

::v-deep .ivu-table-large th {
  height: 0px;
}

::v-deep .ivu-table-large td {
  height: 0px;
}

::v-deep .ivu-table th {
  height: 0px;
}

::v-deep .ivu-table td {
  height: 0px;
}

::v-deep .ivu-table-cell {
  padding-left: 24px;
  padding-right: 24px;
  padding-top: 8px;
  padding-bottom: 8px;
}
</style>
