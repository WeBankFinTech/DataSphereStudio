<template>
  <div class="field-info-wrap">
    <!-- top -->
    <div class="field-info-t">
      <!-- 编辑 + 按钮组 -->
      <div class="field-info-t-edit">
        <div v-if="!isEdit">
          <Button @click="createSelect">生成select</Button>
          <Button @click="createDDL">生成DDL</Button>
          <Button type="primary" @click="edit">编辑</Button>
        </div>
        <div v-else>
          <Button @click="onCancel">取消</Button>
          <Button type="primary" @click="onSave">保存</Button>
        </div>
      </div>

      <div class="field-info-table">
        <Table
          :columns="fieldColumns"
          :data="fieldInfoData"
          size="large"
        ></Table>
      </div>

      <!-- 生成select 和 DDL 弹窗 -->
      <Modal v-model="selectFlag" title="生成select语句" width="576">
        <template v-slot:footer>
          <div>
            <Button
              data-name="select"
              type="primary"
              @click="e => copy(e, selectSQL)"
            >复制</Button
            >
          </div>
        </template>
        <div class="field-info-rich-text">
          <div v-html="selectSql"></div>
        </div>
      </Modal>
      <Modal v-model="DDLflag" title="生成DDL语句" width="576">
        <template v-slot:footer>
          <div>
            <Button
              data-name="ddl"
              type="primary"
              id="copy-button"
              data-clipboard-action="copy"
              data-clipboard-target="#copy-button"
              @click="e => copy(e, DDLsql)"
            >复制</Button
            >
          </div>
        </template>
        <div class="field-info-rich-text">
          <div v-html="ddlSql"></div>
        </div>
      </Modal>
    </div>

    <!-- bottom -->
    <div class="field-info-b">
      <div class="field-info-b-header">
        <span>分区字段信息</span>
      </div>
      <div class="field-info-table">
        <Table
          :columns="rangeColumns"
          :data="rangeInfoData"
          size="large"
        ></Table>
      </div>
    </div>
  </div>
</template>

<script>
import clipboard from "../../utils/clipboard";
import { fomatSqlForShow, fomatSqlForCopy } from "../../utils/fomatSQL";
import { getSelectSql, getSelectDdl, putCommetBulk } from "../../service/api";
export default {
  name: "fieldInfo",
  props: {
    fieldInfo: {
      type: Array,
      default: () => []
    },
    rangeFieldInfo: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      isEdit: false,
      fieldColumns: [
        { title: "序号", key: "id" },
        { title: "字段名称", key: "name" },
        { title: "类型", key: "type" },
        {
          title: "描述",
          key: "comment",
          render: (h, params) => {
            let that = this;
            if (that.isEdit) {
              return h("input", {
                domProps: {
                  value: params.row.comment
                },
                on: {
                  input: function(event) {
                    params.row.comment = event.target.value;
                    that.fieldInfoData[params.index].comment =
                      event.target.value;
                  }
                },
                style: {
                  border: "1px solid #dee4ec",
                  "border-radius": "4px",
                  "padding-left": "12px"
                }
              });
            } else {
              return h("span", {}, params.row.comment);
            }
          }
        }
      ],
      rangeColumns: [
        { title: "序号", key: "id" },
        { title: "字段名称", key: "name" },
        { title: "类型", key: "type" },
        {
          title: "描述",
          key: "comment",
          render: (h, params) => {
            let that = this;
            if (that.isEdit) {
              return h("input", {
                domProps: {
                  value: params.row.comment
                },
                on: {
                  input: function(event) {
                    params.row.comment = event.target.value;
                    that.rangeInfoData[params.index].comment =
                      event.target.value;
                  }
                },
                style: {
                  border: "1px solid #dee4ec",
                  "border-radius": "4px",
                  "padding-left": "12px"
                }
              });
            } else {
              return h("span", {}, params.row.comment);
            }
          }
        }
      ],
      fieldInfoData: this.fieldInfo,
      rangeInfoData: this.rangeFieldInfo,
      selectFlag: false,
      DDLflag: false,
      selectSQL: "",
      DDLsql: ""
    };
  },
  watch: {
    fieldInfo: {
      handler(newVal) {
        this.fieldInfoData = newVal;
      },
      deep: true
    },
    rangeFieldInfo: {
      handler(newVal) {
        this.rangeInfoData = newVal;
      },
      deep: true
    }
  },
  computed: {
    selectSql() {
      return fomatSqlForShow(this.selectSQL);
    },
    ddlSql() {
      return fomatSqlForShow(this.DDLsql);
    }
  },
  methods: {
    edit() {
      this.isEdit = true;
    },
    onCancel() {
      this.isEdit = false;
    },
    onSave() {
      this.isEdit = false;
      let that = this;
      let fieldInfoData = this.fieldInfoData.slice(0);
      let rangeInfoData = this.rangeFieldInfo.slice(0);
      let resMap = Object.create(null);
      if (fieldInfoData.length) {
        fieldInfoData.forEach(item => {
          resMap[item.guid] = item.comment;
        });
      }
      if (rangeInfoData.length) {
        rangeInfoData.forEach(item => {
          resMap[item.guid] = item.comment;
        });
      }
      putCommetBulk(resMap)
        .then(data => {
          if (data.result) {
            that.$Message.success(data.result);
          }
        })
        .catch(err => {
          console.log("putCommetBulk", err);
        });
    },
    createSelect() {
      this.selectFlag = true;
      // 请求 Select语句数据
      let guid = this.$route.params.guid;
      getSelectSql(guid)
        .then(data => {
          if (data.result) {
            this.selectSQL = data.result;
          }
        })
        .catch(err => {
          console.log("getSelectSql", err);
        });
    },
    createDDL() {
      this.DDLflag = true;
      // 请求 DDL语句数据
      let guid = this.$route.params.guid;
      getSelectDdl(guid)
        .then(data => {
          if (data.result) {
            this.DDLsql = data.result;
          }
        })
        .catch(err => {
          console.log("getSelectSql", err);
        });
    },
    // 复制
    copy(e, sql) {
      let str = "";
      str = fomatSqlForCopy(sql);
      clipboard(str, e);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.field-info-wrap {
  padding-left: 24px;
  padding-right: 24px;
  .field-info-t {
    &-edit {
      min-height: 56px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      button {
        margin-right: 8px;
      }
    }
  }

  .field-info-b {
    margin-top: 25px;
    &-header {

      font-size: 14px;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
      line-height: 22px;
      font-weight: bold;
      margin-bottom: 15px;
    }
  }
}
.field-info-rich-text {
  margin: 8px 24px;
  padding: 5px 12px;
  @include bg-color(#fff, $dark-base-color);
  border: 1px solid #dee4ec;
  @include border-color(#dee4ec, $dark-border-color-base);
  border-radius: 4px;
  font-size: 14px;
  @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
  line-height: 22px;
  overflow-y: auto;
  max-height: 200px;
  div {
    ::v-deep span {
      font-weight: bold;
      margin-right: 6px;
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
