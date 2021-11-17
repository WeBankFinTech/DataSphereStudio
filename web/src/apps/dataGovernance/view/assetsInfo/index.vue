<template>
  <div class="assets-info-wrap">
    <!-- top -->
    <div class="assets-info-top-t"></div>
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
            <label for="owner">负责人</label>
            <span>{{ basicData.owner }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="createTime">创建时间</label>
            <span>{{ basicData.createTime }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="lifeCircle">生命周期</label>
            <span>{{ basicData.lifeCircle }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="store">存储量</label>
            <span>{{ basicData.store }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="comment">描述</label>
            <span v-show="!isCommentEdit">{{ basicData.comment }}</span>
            <Input
              v-show="isCommentEdit"
              v-model="basicData.comment"
              size="small"
              style="width: 140px"
              placeholder=""
            />
            <Icon
              type="md-create"
              v-if="!isCommentEdit"
              @click="isCommentEdit = true"
              style="float:right;cursor: pointer;margin-right: 110px;margin-top: 2px;"
            ></Icon>
            <Icon
              type="md-checkmark"
              v-else
              @click="editSingleComment"
              style="float:right;cursor: pointer;"
            ></Icon>
          </div>
          <div class="assets-info-b-l-content-item" style="overflow: hidden">
            <label for="labels">标签</label>
            <div
              style="display: inline-block;width: calc(100% - 70px);float: right;line-height: 32px"
            >
              <Button
                v-show="!isLabelEdit"
                type="dashed"
                size="large"
                style="padding: 5px;"
                @click="() => (isLabelEdit = true)"
                >＋添加标签</Button
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
              >
                {{ label }}
                <span
                  style="cursor: pointer;margin-left: 5px;margin-right: 5px"
                  @click="removeLabel(label)"
                  >×</span
                ></span
              >
            </div>
          </div>
          <div class="assets-info-b-l-content-item">
            <label>主题域</label>
            <Select
              v-model="classification.subject"
              v-if="editable"
              :disabled="isChangingClassifications"
              clearable
              style="width:140px"
            >
              <Option
                v-for="(item, idx) in subjectList"
                :value="item.name"
                :key="idx"
                >{{ item.name }}</Option
              >
            </Select>
            <span v-else>{{ classification.subject }}</span>
          </div>
          <div class="assets-info-b-l-content-item" style="position: relative">
            <Icon
              type="md-create"
              v-if="!editable"
              @click="editable = true"
              style="position: absolute; right:16px; top: -15px;cursor: pointer;"
            ></Icon>
            <Icon
              type="md-checkmark"
              v-else
              @click="changeClassifications"
              style="position: absolute; right:0; top: -15px;cursor: pointer;"
            ></Icon>
            <label>分层</label>
            <Select
              v-model="classification.layer"
              v-if="editable"
              :disabled="isChangingClassifications"
              clearable
              style="width:140px"
            >
              <Option
                v-for="(item, idx) in layerList"
                :value="item.name"
                :key="idx"
                >{{ item.name }}</Option
              >
            </Select>
            <span v-else>{{ classification.layer }}</span>
          </div>
        </div>
      </div>
      <!-- right -->

      <div class="assets-info-b-r">
        <Tabs type="card" class="assets-tabs">
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
  putRemoveLabel,
  postSetComment,
  getThemedomains,
  getLayersAll,
  updateClassifications
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

      isCommentEdit: false,

      classification: {
        subject: "",
        layer: ""
      },
      subjectList: [],
      layerList: [],
      isChangingClassifications: false,
      editable: false
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
    getThemedomains().then(res => {
      let { result } = res;
      this.subjectList = result;
    });
    getLayersAll().then(res => {
      let { result } = res;
      this.layerList = result;
    });
  },
  methods: {
    init() {
      this.getLineageData();
      this.getTblBasic();
      this.getTblPartition();
    },
    changeClassifications() {
      const guid = this.$route.params.guid;
      let classifications = [];
      if (this.classification.subject) {
        classifications.push(this.classification.subject);
      }
      if (this.classification.layer) {
        classifications.push(this.classification.layer);
      }
      this.isChangingClassifications = true;
      updateClassifications(guid, { newClassifications: classifications })
        .then(res => {
          this.isChangingClassifications = false;
          this.editable = false;
          this.$Message.success(res.result);
        })
        .catch(err => {
          console.log(err);
          this.isChangingClassifications = false;
          this.editable = false;
        });
    },
    // 获取基本字段信息
    getTblBasic() {
      let guid = this.$route.params.guid;
      getHiveTblBasic(guid)
        .then(data => {
          if (data.result) {
            const {
              basic,
              columns,
              partitionKeys,
              classifications
            } = data.result;
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
            if (classifications && classifications.length) {
              classifications.forEach(classification => {
                if (
                  classification.superTypeNames &&
                  classification.superTypeNames.length
                ) {
                  if (classification.superTypeNames[0] === "subject") {
                    this.classification.subject = classification.typeName;
                  } else if (
                    classification.superTypeNames[0] === "layer" ||
                    classification.superTypeNames[0] === "layer_system"
                  ) {
                    this.classification.layer = classification.typeName;
                  }
                }
              });
            }
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
    // 删除标签
    removeLabel(label) {
      putRemoveLabel(this.$route.params.guid, { labels: [label] }).then(res => {
        this.$Message.success("删除成功");
        this.getTblBasic();
      });
    },
    // 编辑标签
    editSingleLabel() {
      let that = this;
      if (that.singleLabel) {
        that.labelOptions.push(that.singleLabel);
        let params = that.labelOptions.slice(0);
        let guid = this.$route.params.guid;
        postSetLabel(guid, { labels: params })
          .then(data => {
            console.log(data);
            that.isLabelEdit = false;
          })
          .catch(err => {
            console.log(err);
            that.isLabelEdit = false;
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

  .assets-info-top-t {
    background-color: rgba(#f8f9fc, 1);
    height: 22px;
    border-top: 1px solid #dee4ec;
    border-bottom: 1px solid #dee4ec;
  }
  .assets-info-top {
    min-height: 80px;
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
      margin-right: -1px;

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
            display: inline-block;
            width: 70px;
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
      .assets-tabs {
        height: 100%;
        ::v-deep .ivu-tabs-content {
          height: 90%;
        }

        ::v-deep .ivu-tabs-tab {
          border-radius: 0px;
        }
      }
    }
  }

  .assets-info-label {
    background: #f4f7fb;
    border: 1px solid #dee4ec;
    border-radius: 2px;
    display: inline-block;
    min-width: 58px;
    min-height: 22px;
    text-align: center;
    line-height: 22px;
    margin-right: 8px;
    margin-left: 5px;
    padding-left: 3px;
  }
}
</style>
