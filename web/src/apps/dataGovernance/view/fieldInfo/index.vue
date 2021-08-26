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
      <Modal
        v-model="selectFlag"
        title="生成select语句"
        width="576"
        ok-text="复制"
        cancel-text=""
      >
        <div class="field-info-rich-text">
          <p>SELECT</p>
          <p>id</p>
          <p>name</p>
          <p>gender</p>
        </div>
      </Modal>
      <Modal
        v-model="DDLflag"
        title="生成DDL语句"
        width="576"
        ok-text="复制"
        cancel-text=""
      >
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
export default {
  name: "fieldInfo",
  props: {
    fieldInfo: {
      type: Array,
      default: () => []
    },
    rangeInfo: {
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
                }
              });
            } else {
              return h("span", {}, params.row.comment);
            }
          }
        }
      ],
      fieldInfoData: this.fieldInfo,
      rangeInfoData: this.rangeInfo,
      selectFlag: false,
      DDLflag: false
    };
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
      console.log("fieldInfoData", this.fieldInfoData);
      console.log("rangeInfoData", this.rangeInfoData);
    },
    createSelect() {
      this.selectFlag = true;
      // 请求 Select语句数据
    },
    createDDL() {
      this.DDLflag = true;
      // 请求 DDL语句数据
    }
  }
};
</script>

<style lang="scss" scoped>
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
      font-family: PingFangSC-Medium;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.85);
      line-height: 22px;
      font-weight: bold;
      margin-bottom: 15px;
    }
  }
}
.field-info-rich-text {
  margin: 8px 24px;
  padding: 5px 12px;
  background: #ffffff;
  border: 1px solid #dee4ec;
  border-radius: 4px;
  border-radius: 4px;
  font-family: PingFangSC-Regular;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  line-height: 22px;
}
</style>
