<template>
  <div class="page-content">
    <Spin fix v-if="loading"></Spin>
    <Row :gutter="15" style="margin-bottom: 16px">
      <Col span="18">
        <div>
          <Icon type="ios-home" :size="24" />
          <span> 表信息 </span>
          /
          <span>{{ extraInfo.name }}</span>
          &nbsp;
          <Tag color="primary">V{{ extraInfo.version }}</Tag>
        </div>
      </Col>
      <Col span="6">
        <div style="display: flex; justify-content: flex-end">
          <Button type="primary" style="margin-right: 15px">验证规范性</Button>
          <Button
            type="success"
            style="margin-right: 15px"
            @click="handleTableCreate"
            v-if="config.mode === 'update' && config.id && !checkTableData"
          >
            执行建表
          </Button>
          <Button
            type="primary"
            style="margin-right: 15px"
            @click="handleFormFinish"
            v-if="!checkTableData"
          >
            保存
          </Button>
          <Button
            type="primary"
            @click="handleGeneratorNewVersion"
            v-if="config.mode === 'update' && config.id && !checkTableData"
          >
            生成新版本
          </Button>
        </div>
      </Col>
    </Row>

    <Row :gutter="15">
      <Col span="18">
        <ColumnEditor v-model="formState.columns" />
      </Col>
      <Col span="6">
        <Card title="基本信息" dis-hover style="margin-bottom: 15px">
          <Form
            :model="formState"
            ref="baseicInfoForm"
            :rules="ruleValidateBaseicInfo"
            :label-width="80"
            label-position="left"
          >
            <FormItem label="表名" prop="name">
              <Input v-model="formState.name" placeholder="关联数仓将自动填充">
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
              <Select
                v-model="formState.dataBase"
                placeholder="选择所属库"
                :disabled="config.mode === 'update'"
              >
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
              <Input v-model="formState.label" placeholder="自动填充"> </Input>
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
            :rules="ruleValidateCoreInfo"
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
                <Radio
                  v-for="item in compressList"
                  :key="item.code"
                  :label="item.code"
                >
                  {{ item.description }}
                </Radio>
              </RadioGroup>
            </FormItem>
            <FormItem label="文件格式" prop="fileType">
              <RadioGroup v-model="formState.fileType">
                <Radio
                  v-for="item in fileTypeList"
                  :key="item.code"
                  :label="item.code"
                >
                  {{ item.description }}
                </Radio>
              </RadioGroup>
            </FormItem>
            <FormItem label="存储格式" prop="storageType">
              <Select v-model="formState.storageType">
                <Option
                  v-for="item in storageTypeList"
                  :key="item.code"
                  :value="item.code"
                >
                  {{ item.description }}
                </Option>
              </Select>
            </FormItem>
          </Form>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import {
  getTableInfoById,
  getTableInfoByName,
  getLayersList,
  getThemesList,
  getDataBasesList,
  getDictionariesList,
  addTable,
  updateTable,
  createTable,
  generatorNewVersion,
  checkTableData,
} from "../../../service/tableManageApi";
import ColumnEditor from "./columnEditor.vue";
export default {
  components: { ColumnEditor },
  data() {
    return {
      ruleValidateBaseicInfo: {
        // 表名
        name: [
          {
            required: true,
            message: "表名必填",
            trigger: "submit",
          },
        ],
        // 表别名
        alias: [
          {
            required: true,
            message: "表别名必填",
            trigger: "submit",
          },
        ],
        // 地址
        location: [
          {
            required: true,
            message: "地址必填",
            trigger: "submit",
          },
        ],
        // 所属库
        dataBase: [
          {
            required: true,
            message: "所属库必选",
            trigger: "submit",
          },
        ],
        // 分层
        warehouseLayerName: [
          {
            required: true,
            message: "分层必选",
            trigger: "submit",
          },
        ],
        // 主题
        warehouseThemeName: [
          {
            required: true,
            message: "主题必选",
            trigger: "submit",
          },
        ],
      },
      ruleValidateCoreInfo: {
        // 生命周期
        lifecycle: [
          {
            required: true,
            message: "必选",
            trigger: "submit",
          },
        ],
        // 是否分区表
        isPartitionTable: [
          {
            required: true,
            type: "number",
            message: "必选",
            trigger: "submit",
          },
        ],
        // 压缩格式
        compress: [
          {
            required: true,
            message: "必选",
            trigger: "submit",
          },
        ],
        // 文件格式
        fileType: [
          {
            required: true,
            message: "必选",
            trigger: "submit",
          },
        ],
        // 存储格式
        storageType: [
          {
            required: true,
            message: "存储格式必选",
            trigger: "submit",
          },
        ],
        // 是否外部表
        isExternal: [
          {
            required: true,
            type: "number",
            message: "必选",
            trigger: "submit",
          },
        ],
      },
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
      // 压缩格式列表
      compressList: [],
      // 文件格式列表
      fileTypeList: [],
      // 存储引擎列表
      storageTypeList: [],

      // 是否加载中
      loading: false,
      // 一些额外的信息
      extraInfo: {
        name: "",
        version: "",
      },
      // 一些配置信息
      config: {
        mode: this.$route.query.mode,
        id: this.$route.query.id,
        name: this.$route.query.name,
        guid: this.$route.query.guid,
      },
      // 当前表是否有数据
      checkTableData: true,
    };
  },
  mounted() {
    this.handleGetFrontData();
    // 检查当前表是否有数据
    this.handleCheckTableData();
    if (this.config.id || (this.config.name && this.config.guid)) {
      this.handleGetData();
    }
  },
  methods: {
    // 检查当前是否有数据
    async handleCheckTableData() {
      try {
        let { status } = await checkTableData(this.config.name).catch(() => {});
        this.checkTableData = !!status;
      } catch (error) {}
    },
    // 获取数据
    async handleGetData() {
      this.loading = true;
      let data;
      if (this.config.id) {
        data = await getTableInfoById(this.config.id);
      } else {
        data = await getTableInfoByName(this.config.name, this.config.guid);
      }
      this.loading = false;
      let { detail } = data;
      this.extraInfo = {
        name: detail.name,
        version: detail.version,
      };
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
    // 生成新版本
    handleGeneratorNewVersion() {
      generatorNewVersion(this.config.id, this.handlegetFormatData()).then(
        () => {
          this.$Message.success("新增版本成功");
        }
      );
    },
    // 执行表创建
    handleTableCreate() {
      createTable(this.config.id).then(() => {
        this.$Message.success("创建成功");
      });
    },
    // 获取前置选填数据
    handleGetFrontData() {
      this.loading = true;
      Promise.all([
        getLayersList(),
        getThemesList(),
        getDataBasesList(),
        getDictionariesList(),
      ]).then(([res1, res2, res3, res4]) => {
        this.loading = false;
        // 分层
        this.layersList = res1.list;
        // 主题
        this.themesList = res2.list;
        // 库
        this.dataBasesList = res3.list;
        // 生命周期列表
        this.lifecycleList = res4.list.filter(
          (item) => item.type === "LIFECYCLE"
        );
        // 压缩格式列表
        this.compressList = res4.list.filter(
          (item) => item.type === "COMPRESS"
        );
        // 文件格式列表
        this.fileTypeList = res4.list.filter(
          (item) => item.type === "FILE_STORAGE"
        );
        // 存储引擎列表
        this.storageTypeList = res4.list.filter(
          (item) => item.type === "STORAGE_ENGINE"
        );
      });
    },
    // 获取格式化之后的数据
    handlegetFormatData() {
      return Object.assign({}, this.formState, {
        name: `${this.formState.dataBase}.${this.formState.name}`,
      });
    },
    // 表单完成逻辑
    handleFormFinish() {
      Promise.all([
        this.$refs["baseicInfoForm"].validate(),
        this.$refs["coreInfoForm"].validate(),
      ]).then(([valid1, valid2]) => {
        if (valid1 && valid2) {
          if (this.config.mode === "create") {
            addTable(this.handlegetFormatData()).then(() => {
              this.$Message.success("创建成功");
              this.$router.push({
                path: "/dataModelCenter/tableManage/tableSearch",
              });
            });
            return;
          }
          if (this.config.mode === "update") {
            updateTable(this.config.id, this.handlegetFormatData()).then(() => {
              this.$Message.success("更新成功");
              this.$router.push({
                path: "/dataModelCenter/tableManage/tableSearch",
              });
            });
            return;
          }
        }
      });
    },
  },
};
</script>

<style  lang="scss" scoped>
@import "../../../assets/styles/common.scss";
</style>
