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
          <div class="assets-info-b-l-content-item" style="overflow: hidden; width: 100%">
            <label for="comment">描述</label>
            <span v-show="!isCommentEdit">{{ basicData.comment }}</span>
            <!-- <Input
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
              style="float:right;cursor: pointer;margin-top: 2px;"
            ></Icon>
            <Icon
              type="md-checkmark"
              v-else
              @click="editSingleComment"
              style="float:right;cursor: pointer;"
            ></Icon> -->
          </div>
          <div class="assets-info-b-l-content-item" style="overflow: hidden; width: 100%">
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
                style="width: 135px;"
                size="small"
                placeholder=""
                @on-enter="editSingleLabel"
              />
              <Icon
                type="md-checkmark"
                v-show="isLabelEdit"
                @click="editSingleLabel"
                style="margin-left: 8px; cursor: pointer;"
              ></Icon>
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
              @click.once="changeClassifications"
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

          <div class="assets-info-b-l-content-item">
            <label for="tableType">表类别</label>
            <span>{{ basicData.tableType == 'EXTERNAL_TABLE' ? '外部表' : '内部表' }}</span>
          </div>
          <div class="assets-info-b-l-content-item" v-if=" basicData.tableType == 'EXTERNAL_TABLE' && basicData.location">
            <label for="location">表路径</label>
            <span :style="{'word-break': 'break-all'}">{{ basicData.location }}</span>
          </div>
        </div>
      </div>
      <!-- right -->

      <div class="assets-info-b-r">
        <Tabs type="card" class="assets-tabs" v-model="curTab">
          <TabPane label="字段信息" name="info"
          ><field-info
            :fieldInfo="fieldInfo"
            :rangeFieldInfo="rangeFieldInfo"
          ></field-info
          ></TabPane>
          <!-- <TabPane label="分区信息" v-show="isParTbl"
            ><range-info :rangeInfo="rangeInfo"></range-info
          ></TabPane> -->
          <!-- <TabPane label="数据预览">标签三的内容</TabPane> -->
          <TabPane label="数据血缘" name="lineage">
            <div class="dagreLayout-page" v-if="lineageData">
              <lineage
                class="flow-canvas"
                id='dag-canvas'
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
/* eslint-disable */
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
import util from '@dataspherestudio/shared/common/util';
import { EventBus } from "../../module/common/eventBus/event-bus";

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
      editable: false,
      curTab: "info"
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

    util.Hub.$on("register_click_hive_table", data => {
      this.$nextTick(() => {
        $(`#${data.guid}`).on("click", () => {
          this.curTab = "info";
          EventBus.$emit("on-choose-card", data);
          const workspaceId = this.$route.query.workspaceId,
            guid = data.guid;
          this.$router.push({
            name: "dataGovernance/assets/info",
            params: { guid },
            query: { workspaceId }
          });
        });
      });
    });
    util.Hub.$on("register_hover", data => {
      this.$nextTick(() => {
        $(`#${data.guid}`).hover(() => {
          let hoverId = data.guid,
            needHoverArr = [hoverId]
          this.lineageData.relations.forEach(relation => {
            if (relation.fromEntityId === hoverId) {
              needHoverArr.push(relation.toEntityId)
            } else if (relation.toEntityId === hoverId) {
              needHoverArr.push(relation.fromEntityId)
            } else {
              $(`#${relation.relationshipId}`).addClass('should-hide')
              $(`#${relation.relationshipId}`).next().addClass('should-hide')
            }
          })
          const keys = Object.keys(this.lineageData.guidEntityMap)
          keys.forEach(item => {
            if (needHoverArr.indexOf(item) === -1) {
              $(`#${item}`).addClass('should-lower')
            }
          })
        }, () => {
          this.lineageData.relations.forEach(relation => {
            $(`#${relation.relationshipId}`).removeClass('should-hide')
            $(`#${relation.relationshipId}`).next().removeClass('should-hide')
          })
          const keys = Object.keys(this.lineageData.guidEntityMap)
          keys.forEach(item => {
            $(`#${item}`).removeClass('should-lower')
          })
        });
      });
    })
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
            if (parseInt(basic.store)) {
              basic.store = basic.store + ""
              let tempLen = Math.floor(basic.store.length / 4);
              let len = tempLen > 2 ? 3 : tempLen;
              basic.store = (basic.store / Math.pow(1024, len + 1)).toFixed(2);
              switch (len) {
                case 0:
                  basic.store = basic.store + "KB";
                  break;
                case 1:
                  basic.store = basic.store + "MB";
                  break;
                case 2:
                  basic.store = basic.store + "GB";
                  break;
                default:
                  basic.store = basic.store + "TB";
                  break;
              }
            }
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
            this.rangeInfo.forEach(item => {
              item.store = item.store + "";
              let tempLen = Math.floor(item.store.length / 4);
              let len = tempLen > 2 ? 3 : tempLen;
              item.store = (item.store / Math.pow(1024, len + 1)).toFixed(2);
              switch (len) {
                case 0:
                  item.store = item.store + "KB";
                  break;
                case 1:
                  item.store = item.store + "MB";
                  break;
                case 2:
                  item.store = item.store + "GB";
                  break;
                default:
                  item.store = item.store + "TB";
                  break;
              }
            });
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
@import "@dataspherestudio/shared/common/style/variables.scss";
.assets-info-wrap {
  flex: 1;
  flex-direction: column;
  display: flex;

  .assets-info-top-t {
    @include bg-color(rgba(#f8f9fc, 1), $dark-base-color);
    height: 22px;
    border-top: 1px solid #dee4ec;
    border-bottom: 1px solid #dee4ec;
    @include border-color(#dee4ec, $dark-border-color-base);
  }
  .assets-info-top {
    min-height: 80px;
    border-bottom: 1px solid #dee4ec;
    @include border-color(#dee4ec, $dark-border-color-base);
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
      @include border-color(#dee4ec, $dark-border-color-base);
      margin-right: -1px;

      &-title {

        font-size: 14px;
        @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
        font-weight: bold;
      }

      &-content {
        &-item {

          font-size: 14px;
          margin-top: 16px;
          padding-right: 8px;
          label {
            font-weight: normal;
            @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
            display: inline-block;
            width: 70px;
          }
          span {
            @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
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
    @include bg-color(#f4f7fb, $dark-base-color);
    border: 1px solid #dee4ec;
    @include border-color(#dee4ec, $dark-border-color-base);
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
