<template>
  <div>
    <div style="margin-bottom: 16px">
      <Table :columns="columnTableColumn" :data="columnData">
        <template slot-scope="{ row, index }" slot="name">
          <Input
            type="text"
            v-model="editData.name"
            v-if="editIndex === index"
          />
          <span v-else>{{ row.name }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="alias">
          <Input
            type="text"
            v-model="editData.alias"
            v-if="editIndex === index"
          />
          <span v-else>{{ row.alias }}</span>
        </template>

        <template slot-scope="{ row, index }" slot="type">
          <Select v-model="editData.type" v-if="editIndex === index" transfer>
            <Option v-for="type in columnTypeOption" :value="type" :key="type">
              {{ type }}
            </Option>
          </Select>
          <span v-else>{{ row.type }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="comment">
          <Input
            type="text"
            v-model="editData.comment"
            v-if="editIndex === index"
          />
          <span v-else>{{ row.comment }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="isPartitionField">
          <RadioGroup
            v-model="editData.isPartitionField"
            v-if="editIndex === index"
          >
            <Radio :label="1">
              <span>是</span>
            </Radio>
            <Radio :label="0">
              <span>否</span>
            </Radio>
          </RadioGroup>
          <span v-else>{{ row.isPartitionField ? "是" : "否" }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="isPrimary">
          <RadioGroup v-model="editData.isPrimary" v-if="editIndex === index">
            <Radio :label="1">
              <span>是</span>
            </Radio>
            <Radio :label="0">
              <span>否</span>
            </Radio>
          </RadioGroup>
          <span v-else>{{ row.isPrimary ? "是" : "否" }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="length">
          <InputNumber
            :max="100"
            :min="1"
            v-model="editData.length"
            v-if="editIndex === index"
          />
          <span v-else>{{ row.length }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="rule">
          <Input
            type="text"
            v-model="editData.rule"
            v-if="editIndex === index"
          />
          <span v-else>{{ row.rule }}</span>
        </template>
        <template slot-scope="{ row, index }" slot="model">
          <span>
            <span v-if="editIndex === index">
              {{ editData.modelType == 0 ? "维度" : "" }}
              {{ editData.modelType == 1 ? "指标" : "" }}
              {{ editData.modelType == 2 ? "度量" : "" }}
              ：
              {{ editData.modelName }}
            </span>
            <span v-else>
              {{ row.modelType == 0 ? "维度" : "" }}
              {{ row.modelType == 1 ? "指标" : "" }}
              {{ row.modelType == 2 ? "度量" : "" }}
              ：
              {{ row.modelName }}
            </span>
            <a @click="handleSelectModel" v-if="editIndex === index">
              <Icon type="md-create" />
            </a>
          </span>
        </template>
        <template slot-scope="{ row, index }" slot="action">
          <div v-if="editIndex === index">
            <Button @click="handleSave(index)">保存</Button>
            <Button @click="editIndex = -1">取消</Button>
          </div>
          <div v-if="editIndex === -1">
            <Button size="small" @click="handleEdit(index, row)"> 操作 </Button>
            <Button size="small" @click="handleDelete(index)"> 删除 </Button>
            <Button size="small" @click="handleMoveUp(index)"> 上移 </Button>
            <Button size="small" @click="handleMoveDown(index)"> 下移 </Button>
          </div>
        </template>
      </Table>
    </div>
    <div style="display: flex; justify-content: flex-end">
      <Button type="primary" @click="handleAddColumn"> 添加字段 </Button>
    </div>
    <SelectModel
      v-model="selectModelCfg.visible"
      @finish="handleSelectModelFinish"
    />
  </div>
</template>

<script>
import SelectModel from "./selectModel.vue";
export default {
  components: { SelectModel },
  model: {
    prop: "column",
    event: "changeColumn",
  },
  props: {
    column: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    columnData: {
      // getter
      get: function () {
        return this.column;
      },
      // setter
      set: function (newValue) {
        this.$emit("changeColumn", newValue);
      },
    },
  },
  data() {
    return {
      selectModelCfg: {
        visible: false,
      },
      // 正在编辑的行
      editIndex: -1,
      // 编辑中的数据
      editData: {
        name: "",
        alias: "",
        type: "",
        comment: "",
        isPartitionField: 0,
        isPrimary: 1,
        length: 20,
        rule: "",
        modelType: 0,
        modelName: "",
      },
      // 字段类型
      columnTypeOption: [
        "string",
        "number",
        "varchar(255)",
        "int",
        "bigint",
        "varchar(64)",
      ],
      // 数据
      columnTableColumn: [
        {
          title: "字段名称",
          key: "name",
          slot: "name",
          width: 100,
        },
        {
          title: "字段别名",
          key: "alias",
          slot: "alias",
          width: 100,
        },
        {
          title: "字段类型",
          key: "type",
          slot: "type",
          width: 100,
        },
        {
          title: "长度",
          key: "length",
          slot: "length",
          width: 100,
        },
        {
          title: "是否主键",
          key: "isPrimary",
          slot: "isPrimary",
          width: 100,
        },
        {
          title: "是否分区字段",
          key: "isPartitionField",
          slot: "isPartitionField",
          width: 110,
        },
        {
          title: "关联数仓",
          key: "model",
          slot: "model",
          width: 100,
        },
        {
          title: "校验规则",
          key: "rule",
          slot: "rule",
          width: 100,
        },
        {
          title: "字段说明",
          key: "comment",
          slot: "comment",
          width: 100,
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          slot: "action",
          width: 200,
        },
      ],
    };
  },
  methods: {
    // handleSelectModelFinish
    handleSelectModelFinish(arg) {
      this.editData.modelType = arg.modelType;
      this.editData.modelName = arg.modelName;
    },
    // 修改模型动作
    handleSelectModel() {
      this.selectModelCfg = {
        visible: true,
      };
    },
    // 编辑
    handleEdit(index, row) {
      this.editData.name = row.name;
      this.editData.alias = row.alias;
      this.editData.type = row.type;
      this.editData.comment = row.comment;
      this.editData.isPartitionField = row.isPartitionField;
      this.editData.isPrimary = row.isPrimary;
      this.editData.length = row.length;
      this.editData.rule = row.rule;
      this.editData.modelType = row.modelType;
      this.editData.modelName = row.modelName;

      this.editIndex = index;
    },
    // 保存修改
    handleSave(index) {
      this.columnData[index].name = this.editData.name;
      this.columnData[index].alias = this.editData.alias;
      this.columnData[index].type = this.editData.type;
      this.columnData[index].comment = this.editData.comment;
      this.columnData[index].isPartitionField = this.editData.isPartitionField;
      this.columnData[index].isPrimary = this.editData.isPrimary;
      this.columnData[index].length = this.editData.length;
      this.columnData[index].rule = this.editData.rule;
      this.columnData[index].modelType = this.editData.modelType;
      this.columnData[index].modelName = this.editData.modelName;

      this.editIndex = -1;
    },
    // 删除
    handleDelete(index) {
      this.columnData.splice(index, 1);
      this.columnData = [...this.columnData];
    },
    // 上移
    handleMoveUp(index) {
      if (index === 0) return;
      let a = this.columnData[index - 1];
      let b = this.columnData[index];
      this.columnData[index] = a;
      this.columnData[index - 1] = b;
      this.columnData = [...this.columnData];
    },
    // 下移
    handleMoveDown(index) {
      if (index === this.columnData.length - 1) return;
      let a = this.columnData[index + 1];
      let b = this.columnData[index];
      this.columnData[index] = a;
      this.columnData[index + 1] = b;
      this.columnData = [...this.columnData];
    },
    // 添加字段
    handleAddColumn() {
      this.columnData.push({
        name: "字段名",
        alias: "别名",
        type: "string",
        comment: "备注",
        isPartitionField: 0,
        isPrimary: 1,
        length: 20,
        rule: "rule",
        modelType: 0,
        modelName: "",
      });
    },
  },
};
</script>

<style>
</style>
