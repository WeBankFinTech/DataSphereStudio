<template>
  <div class="assets-info-wrap">
    <!-- top -->
    <div class="assets-info-top">
      <div class="assets-info-top-bgs">
        <Button>
          <SvgIcon icon-class="star" />
          收藏表
        </Button>
        <Button>申请权限</Button>
        <Button>生成API</Button>
      </div>
    </div>

    <!-- bottom -->
    <div class="assets-info-b">
      <!-- left -->
      <div class="assets-info-b-l">
        <div class="assets-info-b-l-title">
          <span>表基础信息</span>
        </div>

        <div class="assets-info-b-l-content">
          <div class="assets-info-b-l-content-item">
            <label for="owner">负责人：</label>
            <span>{{ basicData.owner }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="createTime">创建时间：</label>
            <span>{{ basicData.createTime }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="lifeCircle">生命周期：</label>
            <span>{{ basicData.lifeCircle }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="store">存储量：</label>
            <span>{{ basicData.store }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="comment">描述：</label>
            <Button
              v-show="!isCommentEdit"
              type="text"
              size="large"
              @click="() => (isCommentEdit = true)"
              >{{ basicData.comment }}</Button
            >
            <Input
              v-show="isCommentEdit"
              v-model="basicData.comment"
              size="small"
              placeholder=""
              @on-enter="editSingleComment"
            />
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="labels">标签：</label>
            <Button
              v-show="!isLabelEdit"
              type="dashed"
              size="large"
              style="marginRight: 8px; marginBottom: 8px"
              @click="() => (isLabelEdit = true)"
              >添加标签</Button
            >
            <Input
              v-show="isLabelEdit"
              v-model="singleLabel"
              size="small"
              placeholder=""
              @on-enter="editSingleLabel"
            />
            <span
              v-for="label in labelOptions"
              :key="label"
              class="assets-info-label"
              >{{ label }}</span
            >
          </div>
        </div>
      </div>
      <!-- right -->

      <div class="assets-info-b-r">
        <Tabs type="card">
          <TabPane label="字段信息"
            ><field-info
              :fieldInfo="fieldInfo"
              :rangeFieldInfo="rangeFieldInfo"
            ></field-info
          ></TabPane>
          <!-- <TabPane label="分区信息" v-show="isParTbl"
            ><range-info :rangeInfo="rangeInfo"></range-info
          ></TabPane> -->
          <!-- <TabPane label="数据预览">标签三的内容</TabPane> -->
          <TabPane label="数据血缘">
            <div class="dagreLayout-page" v-if="lineageData">
              <lineage
                class="flow-canvas"
                id="dag-canvas"
                :lineageData="lineageData"
              ></lineage>
            </div>
          </TabPane>
          <TabPane label="分区信息" v-if="isParTbl"
            ><range-info :rangeInfo="rangeInfo"></range-info
          ></TabPane>
        </Tabs>
      </div>
    </div>
  </div>
</template>

<script>
import fieldInfo from "../fieldInfo/index.vue";
import rangeInfo from "../rangeInfo/index.vue";
import lineage from "./components/lineage";
import {
  getLineage,
  getHiveTblBasic,
  getHiveTblPartition,
  postSetLabel,
  postSetComment
} from "../../service/api";

export default {
  name: "assetsInfo",
  components: {
    fieldInfo,
    rangeInfo,
    lineage
  },
  data() {
    return {
      lineageData: null,
      basicData: {},
      isParTbl: false,
      fieldInfo: [],
      rangeFieldInfo: [],
      rangeInfo: [],

      isLabelEdit: false,
      singleLabel: "",
      labelOptions: [],

      isCommentEdit: false
    };
  },
  watch: {
    "$route.params.guid": {
      handler() {
        this.init();
      }
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.getLineageData();
      this.getTblBasic();
      this.getTblPartition();
    },
    // 获取基本字段信息
    getTblBasic() {
      let guid = this.$route.params.guid;
      getHiveTblBasic(guid)
        .then(data => {
          if (data.result) {
            const { basic, columns, partitionKeys } = data.result;
            this.basicData = basic;
            this.isParTbl = basic["isParTbl"];
            this.labelOptions = basic["labels"];
            columns.forEach((item, idx) => {
              item["id"] = idx + 1;
            });
            this.fieldInfo = columns.slice(0);
            partitionKeys.forEach((item, idx) => {
              item["id"] = idx + 1;
            });
            this.rangeFieldInfo = partitionKeys.slice(0);
          }
        })
        .catch(err => {
          console.log("getTblBasic", err);
        });
    },
    // 获取分区信息
    getTblPartition() {
      let guid = this.$route.params.guid;
      getHiveTblPartition(guid)
        .then(data => {
          if (data.result) {
            this.rangeInfo = data.result;
          }
        })
        .catch(err => {
          console.log("getTblPartition", err);
        });
    },
    // 获取血缘数据
    getLineageData() {
      let guid = this.$route.params.guid;
      getLineage(guid)
        .then(res => {
          if (res.result) {
            this.lineageData = res.result;
          }
        })
        .catch(err => {
          console.log("getLineageData", err);
        });
    },
    // 编辑标签
    editSingleLabel() {
      let that = this;
      if (that.singleLabel) {
        that.labelOptions.push(that.singleLabel);
        let params = that.labelOptions.slice(0);
        let guid = this.$route.params.guid;
        postSetLabel(guid, params)
          .then(data => {
            console.log(data);
            that.isLabelEdit = false;
          })
          .catch(err => {
            console.log(err);
          });
      }
    },
    // 编辑描述
    editSingleComment() {
      let that = this;
      let comment = that.basicData["comment"];
      let guid = this.$route.params.guid;
      postSetComment(guid, comment)
        .then(data => {
          console.log(data);
          that.isCommentEdit = false;
        })
        .catch(err => {
          console.log(err);
        });
    }
  }
};
</script>

<style lang="scss" scoped>
.assets-info-wrap {
  flex: 1;
  flex-direction: column;
  display: flex;
  .assets-info-top {
    min-height: 80px;
    border-top: 24px solid #f8f9fc;
    border-bottom: 1px solid #dee4ec;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    &-bgs {
      margin-right: 24px;
      button {
        margin-right: 8px;
      }
    }
  }

  .assets-info-b {
    display: flex;
    flex: 1;
    &-l {
      min-width: 249px;
      padding-left: 16px;
      padding-top: 16px;
      border-right: 1px solid #dee4ec;

      &-title {
        font-family: PingFangSC-Medium;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        font-weight: bold;
      }

      &-content {
        &-item {
          font-family: PingFangSC-Regular;
          font-size: 14px;
          margin-top: 16px;
          label {
            font-weight: normal;
            color: rgba(0, 0, 0, 0.85);
          }
          span {
            color: rgba(0, 0, 0, 0.65);
          }
        }
      }
    }

    &-r {
      flex: 1;
      max-width: calc(100% - 250px);
    }
  }

  .assets-info-label {
    background-color: burlywood;
    display: inline-block;
    min-width: 35px;
    text-align: center;
    margin-right: 8px;
  }
}
</style>
