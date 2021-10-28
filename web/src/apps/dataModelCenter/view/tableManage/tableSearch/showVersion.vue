<template>
  <Drawer
    title="查看版本"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    width="1400"
    @on-cancel="cancelCallBack"
  >
    <div class="page-content">
      <Spin fix v-if="loading"></Spin>
      <Row :gutter="15">
        <Col span="18">
          <Table :columns="columnTableColumn" :data="formState.columns">
            <template slot-scope="{ row }" slot="isPartitionField">
              <span>{{ row.isPartitionField ? "是" : "否" }}</span>
            </template>
            <template slot-scope="{ row }" slot="isPrimary">
              <span>{{ row.isPrimary ? "是" : "否" }}</span>
            </template>
            <template slot-scope="{ row }" slot="length">
              <span>{{ row.length }}</span>
            </template>
            <template slot-scope="{ row }" slot="model">
              <span>
                {{ row.modelType == 0 ? "维度" : "" }}
                {{ row.modelType == 1 ? "指标" : "" }}
                {{ row.modelType == 2 ? "度量" : "" }}
                ：
                {{ row.modelName }}
              </span>
            </template>
          </Table>
        </Col>
        <Col span="6">
          <Card title="基本信息" dis-hover style="margin-bottom: 15px">
            <Form
              ref="baseicInfoForm"
              :model="formState"
              :label-width="80"
              label-position="left"
            >
              <FormItem label="表名" prop="name">
                <Input
                  v-model="formState.name"
                  placeholder="关联数仓将自动填充"
                >
                  <span slot="prepend">{{ formState.dataBase }}.</span>
                </Input>
              </FormItem>
              <FormItem label="表别名" prop="alias">
                <Input v-model="formState.alias" placeholder="表别名"> </Input>
              </FormItem>
              <FormItem label="路径" prop="location">
                <Input v-model="formState.location" placeholder="路径"> </Input>
              </FormItem>
              <FormItem label="所属库" prop="dataBase">
                <Select v-model="formState.dataBase" placeholder="选择所属库">
                  <Option
                    v-for="dataBase in dataBasesList"
                    :key="dataBase.guid"
                    :value="dataBase.name"
                  >
                    {{ dataBase.name }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem label="所属分层" prop="warehouseLayerName">
                <Select
                  v-model="formState.warehouseLayerName"
                  placeholder="选择分层"
                >
                  <Option
                    v-for="layer in layersList"
                    :key="layer.id"
                    :value="layer.name"
                  >
                    {{ layer.name }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem label="主题域" prop="warehouseThemeName">
                <Select
                  v-model="formState.warehouseThemeName"
                  placeholder="请选择主题域"
                >
                  <Option
                    v-for="theme in themesList"
                    :key="theme.id"
                    :value="theme.name"
                  >
                    {{ theme.name }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem label="可用角色" prop="principalName">
                <Select
                  multiple
                  :value="(formState.principalName || '').split(',')"
                  @input="formState.principalName = $event.join()"
                  placeholder="可用角色"
                >
                  <Option
                    v-for="item in principalNameList"
                    :value="item.value"
                    :key="item.value"
                  >
                    {{ item.label }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem label="标签" prop="label">
                <Input v-model="formState.label" placeholder="自动填充">
                </Input>
              </FormItem>
              <FormItem label="描述" prop="comment">
                <Input
                  v-model="formState.comment"
                  type="textarea"
                  placeholder="请输入表的详细描述"
                >
                </Input>
              </FormItem>
            </Form>
          </Card>
          <Card title="核心属性" dis-hover>
            <Form
              :model="formState"
              ref="coreInfoForm"
              :label-width="80"
              label-position="left"
            >
              <FormItem label="是否分区表" prop="isPartitionTable">
                <Select v-model="formState.isPartitionTable">
                  <Option :value="1">是</Option>
                  <Option :value="0">否</Option>
                </Select>
              </FormItem>
              <FormItem label="表类型" prop="isExternal">
                <RadioGroup v-model="formState.isExternal">
                  <Radio :label="0">内部表</Radio>
                  <Radio :label="1">外部表</Radio>
                </RadioGroup>
              </FormItem>
              <FormItem label="生命周期" prop="lifecycle">
                <Select v-model="formState.lifecycle">
                  <Option
                    v-for="item in lifecycleList"
                    :key="item.id"
                    :value="item.code"
                  >
                    {{ item.description }}
                  </Option>
                </Select>
              </FormItem>
              <FormItem label="压缩格式" prop="compress">
                <RadioGroup v-model="formState.compress">
                  <Radio label="Snappy">Snappy</Radio>
                  <Radio label="无">无</Radio>
                </RadioGroup>
              </FormItem>
              <FormItem label="文件格式" prop="fileType">
                <RadioGroup v-model="formState.fileType">
                  <Radio label="ORC">ORC</Radio>
                  <Radio label="Parquet">Parquet</Radio>
                  <Radio label="无">无</Radio>
                </RadioGroup>
              </FormItem>
              <FormItem label="存储格式" prop="storageType">
                <Select v-model="formState.storageType">
                  <Option value="Hive">Hive</Option>
                </Select>
              </FormItem>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  </Drawer>
</template>

<script>
import {
  getLayersList,
  getThemesList,
  getDataBasesList,
  getDictionariesList,
} from "../../../service/tableManageApi";
export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  props: {
    _visible: {
      type: Boolean,
      required: true,
    },
    bodyData: {
      type: Object,
    },
  },

  data() {
    return {
      // 数据
      columnTableColumn: [
        {
          title: "字段名称",
          key: "name",
        },
        {
          title: "字段别名",
          key: "alias",
        },
        {
          title: "字段类型",
          key: "type",
        },
        {
          title: "长度",
          key: "length",
        },
        {
          title: "是否主键",
          key: "isPrimary",
          slot: "isPrimary",
        },
        {
          title: "是否分区字段",
          key: "isPartitionField",
          slot: "isPartitionField",
        },
        {
          title: "关联数仓",
          key: "model",
          slot: "model",
        },
        {
          title: "校验规则",
          key: "rule",
        },
        {
          title: "字段说明",
          key: "comment",
        },
      ],
      // 表单数据
      formState: {
        // 所属库
        dataBase: "",
        // 表名
        name: "",
        // 表别名
        alias: "",
        // 备注
        comment: "",
        // 是否外部表
        isExternal: 0,
        // 分层
        warehouseLayerName: "",
        // 主题
        warehouseThemeName: "",
        // 生命周期
        lifecycle: "永久",
        // 是否分区表
        isPartitionTable: 1,
        // 是否启用
        isAvailable: 1, //
        // 存储格式
        storageType: "Hive",
        // 可用角色
        principalName: "ALL", //
        // 压缩格式
        compress: "Snappy",
        // 文件格式
        fileType: "ORC",
        // 地址
        location: "/data/", //
        // 标签
        label: "",
        // 字段信息
        columns: [],
      },
      // 角色列表
      principalNameList: [
        {
          value: "ALL",
          label: "ALL",
        },
        {
          value: "角色1",
          label: "角色1",
        },
        {
          value: "角色2",
          label: "角色2",
        },
      ],
      // 主题列表
      themesList: [],
      // 分层列表
      layersList: [],
      // 数据库列表
      dataBasesList: [],
      // 生命周期列表
      lifecycleList: [],
      // 是否加载中
      loading: false,
    };
  },
  watch: {
    bodyData() {
      this.formatPropsToFormState();
    },
  },
  mounted() {
    this.handleGetFrontData();
  },
  methods: {
    // 获取前置数据 主题，分层，库
    handleGetFrontData() {
      this.loading = true;
      Promise.all([
        getLayersList(),
        getThemesList(),
        getDataBasesList(),
        getDictionariesList("LIFECYCLE"),
      ]).then(([res1, res2, res3, res4]) => {
        this.loading = false;
        this.layersList = res1.list;
        this.themesList = res2.list;
        this.dataBasesList = res3.list;
        this.lifecycleList = res4.list;
      });
    },
    // 格式化数据
    formatPropsToFormState() {
      let detail = JSON.parse(this.bodyData.tableParams);
      detail.columns = JSON.parse(this.bodyData.columns);
      let newFormState = {
        // 所属库
        dataBase: detail.dataBase || detail.name.split(".")[0] || "",
        // 表名
        name: detail.name.split(".")[1] || "",
        // 表别名
        alias: detail.alias,
        // 备注
        comment: detail.comment,
        // 是否外部表
        isExternal: 0,
        // 分层
        warehouseLayerName: detail.warehouseLayerName,
        // 主题
        warehouseThemeName: detail.warehouseThemeName,
        // 生命周期
        lifecycle: detail.lifecycle,
        // 是否分区表
        isPartitionTable: detail.isPartitionTable,
        // 是否启用
        isAvailable: detail.isAvailable || 1, //
        // 存储格式
        storageType: detail.storageType,
        // 可用角色
        principalName: detail.principalName, //
        // 压缩格式
        compress: detail.compress,
        // 文件格式
        fileType: detail.fileType,
        // 地址
        location: detail.location, //
        // 标签
        label: detail.label,
        // 字段信息
        columns: detail.columns.map((item) => {
          return {
            name: item.name,
            alias: item.alias || "",
            type: item.type,
            comment: item.comment || "",
            isPartitionField: item.isPartitionField || 0,
            isPrimary: item.isPrimary || 0,
            length: item.length,
            rule: item.rule,
            modelType: item.modelType,
            modelName: item.modelName,
          };
        }),
      };
      this.formState = newFormState;
    },
    // 关闭回调
    cancelCallBack() {
      this.$$refs["baseicInfoForm"].resetFields();
      this.$$refs["coreInfoForm"].resetFields();
    },
  },
};
</script>

<style  lang="scss" scoped>
@import "../../../assets/styles/common.scss";
</style>
