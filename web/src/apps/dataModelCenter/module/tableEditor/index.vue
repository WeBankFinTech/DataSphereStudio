<template>
  <div>
    <Spin fix v-if="loading"></Spin>
    <Row :gutter="15" style="margin-bottom: 16px">
      <Col span="18">
        <div style="display: flex; justify-content: space-between">
          <div>
            <Icon type="ios-home" :size="26" />
            <span style="font-size: 16px"> 表信息 / {{ extraInfo.name }}</span>
            &nbsp;
            <Tag color="primary">V{{ extraInfo.version || 0 }}</Tag>
          </div>
          <div>
            <Button
              type="primary"
              @click="() => $refs['ColumnEditor'].checkColumnData()"
              style="margin-right: 15px"
            >
              验证规范性
            </Button>
            <Button
              type="primary"
              @click="() => $refs['ColumnEditor'].handleAddColumn()"
            >
              添加字段
            </Button>
          </div>
        </div>
      </Col>
      <Col span="6">
        <div style="display: flex; justify-content: flex-end">
          <Button
            type="success"
            style="margin-right: 15px"
            @click="handleTableCreate()"
            v-if="config.mode === 'update' && config.id && !checkTableData"
          >
            执行建表
          </Button>
          <Button
            type="primary"
            style="margin-right: 15px"
            @click="handleFormFinish"
            v-if="
              config.mode === 'create' ||
                config.mode === 'copy' ||
                !checkTableData
            "
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
        <ColumnEditor ref="ColumnEditor" v-model="formState.columns" />
      </Col>
      <Col span="6">
        <Card title="基本信息" dis-hover style="margin-bottom: 15px">
          <Form
            :model="formState"
            ref="baseicInfoForm"
            :rules="ruleValidateBaseicInfo"
            :label-width="90"
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
                :loading="layersLoading"
                v-model="formState._warehouseLayer"
                placeholder="选择分层"
              >
                <Option
                  v-for="item in layersList"
                  :key="item.id"
                  :value="`${item.name}|${item.enName}`"
                >
                  {{ item.name }}
                </Option>
              </Select>
            </FormItem>
            <FormItem label="主题域" prop="warehouseThemeName">
              <Select
                v-model="formState._warehouseTheme"
                placeholder="请选择主题域"
              >
                <Option
                  v-for="item in themesList"
                  :key="item.id"
                  :value="`${item.name}|${item.enName}`"
                >
                  {{ item.name }}
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
                  v-for="item in rolesList"
                  :value="item.roleFrontName"
                  :key="item.roleId"
                >
                  {{ item.roleFrontName }}
                </Option>
              </Select>
            </FormItem>

            <FormItem label="标签" prop="label">
              <Select
                multiple
                placeholder="请选择标签"
                :value="(formState.label || '').split(',')"
                @input="formState.label = $event.join()"
              >
                <Option
                  v-for="item in labelList"
                  :value="item.name"
                  :key="item.id"
                >
                  {{ item.name }}
                </Option>
              </Select>
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
            :label-width="90"
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
              <Select v-model="formState._lifecycle">
                <Option
                  v-for="item in lifecycleList"
                  :key="item.id"
                  :value="`${item.name}|${item.enName}`"
                >
                  {{ item.name }}
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
  addTable,
  bindTable,
  checkTableData,
  createTable,
  generatorNewVersion,
  getDataBasesList,
  getDictionariesList,
  getTableInfoById,
  getTableInfoByName,
  updateTable,
} from "@/apps/dataModelCenter/service/api/tableManage";
import {
  getCyclesList,
  getLayersList,
  getThemesList,
  getRolesList,
} from "@/apps/dataModelCenter/service/api/common";
import ColumnEditor from "./columnEditor.vue";
import { getLabelList } from "@/apps/dataModelCenter/service/api/labels";
import mixin from "@/common/service/mixin";
export default {
  components: { ColumnEditor },
  mixins: [mixin],
  data() {
    return {
      // 验证基本信息
      ruleValidateBaseicInfo: {
        // 表名
        name: [
          {
            required: true,
            message: "表名必填",
            trigger: "submit",
          },
          {
            message: "仅支持小写英文，数字，长度在100字符以内并必须以字母开头",
            pattern: /^[a-z][a-z0-9]{0,99}$/g,
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
        // 所属库
        dataBase: [
          {
            required: true,
            message: "所属库必选",
            trigger: "submit",
          },
        ],
        // 分层
        _warehouseLayer: [
          {
            required: true,
            message: "分层必选",
            trigger: "submit",
          },
        ],
        // 主题
        _warehouseTheme: [
          {
            required: true,
            message: "主题必选",
            trigger: "submit",
          },
        ],
      },
      ruleValidateCoreInfo: {
        // 生命周期
        _lifecycle: [
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
        // 分层 名字|英文名 warehouseLayerName|warehouseLayerNameEn
        _warehouseLayer: "",
        // 主题 名字|英文名 warehouseThemeName|warehouseThemeNameEn
        _warehouseTheme: "",
        // 生命周期 lifecycle|lifecycleEn
        _lifecycle: "永久",
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
        location: "", //
        // 标签
        label: "",
        // 字段信息
        columns: [],
      },
      // 角色列表
      rolesList: [],
      // 主题列表
      themesList: [],
      // 分层列表
      layersList: [],
      // 数据库列表
      dataBasesList: [],
      // 生命周期列表
      lifecycleList: [],
      // 标签列表
      labelList: [],
      // 压缩格式列表
      compressList: [],
      // 文件格式列表
      fileTypeList: [],
      // 存储引擎列表
      storageTypeList: [],
      // 是否加载中
      loading: false,
      // 主题加载中
      layersLoading: [],
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
  watch: {
    // 监听库变化 实时获取分层
    "formState.dataBase": {
      immediate: true,
      handler(value) {
        if (value) {
          this.handlegetLayers(value);
        }
      },
    },
  },
  mounted() {
    // 用户操作时需要的数据
    this.handlePreFetchData();
    // 检查当前表是否有数据
    if (this.config.mode === "update") {
      this.handleCheckTableData();
    }
    // 如果有id或者guid就去获取数据
    if (this.config.id || (this.config.name && this.config.guid)) {
      this.handleGetData();
    }
  },

  methods: {
    /**
     * 获取分层
     * @param dbName {String} 可用库
     */
    async handlegetLayers(dbName) {
      this.layersLoading = true;
      let { list } = await getLayersList(dbName);
      this.layersLoading = false;
      this.layersList = list;
    },
    /**
     * 检查当前表是否存在数据，有数据的情况下某些操作是不允许的
     */
    async handleCheckTableData() {
      this.loading = true;
      return checkTableData(this.config.name)
        .then((res) => {
          this.loading = false;
          this.checkTableData = !!res.status;
        })
        .catch(() => {
          this.loading = false;
          this.checkTableData = true;
        });
    },
    /**
     * 获取当前表数据
     */
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
      // 如果有id更新id
      if (detail.id) {
        this.config.id = detail.id;
      }
      // 额外信息
      this.extraInfo = {
        name: detail.name,
        version: detail.version,
      };
      // 表单数据
      this.formState = {
        // 所属库
        dataBase: detail.dataBase || detail.name.split(".")[0] || "",
        // 表名
        name: detail.name.split(".")[1] || "",
        // 表别名
        alias: detail.alias,
        // 备注
        comment: detail.comment,
        // 是否外部表
        isExternal: detail.isExternal,
        // 分层
        _warehouseLayer: `${detail.warehouseLayerName}|${detail.warehouseLayerNameEn}`,
        // 主题
        _warehouseTheme: `${detail.warehouseThemeName}|${detail.warehouseThemeNameEn}`,
        // 生命周期
        _lifecycle: `${detail.lifecycle}|${detail.lifecycleEn}`,
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
      // 如果是 copy 模式，则清空一些信息
      if (this.config.mode === "copy") {
        this.formState.name = "";
        this.extraInfo.name = "";
        this.extraInfo.version = 0;
      }
    },
    /**
     * @description 新增版本
     */
    handleGeneratorNewVersion() {
      this.loading = true;
      return generatorNewVersion(this.config.id, this.handleGetFormatData())
        .then(() => {
          this.loading = false;
          this.$Message.success("新增版本成功");
          this.$router.push({
            path: "/dataModelCenter/tableManage/tableSearch",
          });
        })
        .catch(() => {
          this.loading = false;
        });
    },
    /**
     * 执行表创建
     */
    async handleTableCreate(id) {
      this.loading = true;
      try {
        await createTable(id || this.config.id);
        await bindTable(id || this.config.id);
        this.loading = false;
        this.$Message.success("建表成功");
      } catch (error) {
        this.loading = false;
      }
    },
    /**
     * 获取前置选填数据
     */
    handlePreFetchData() {
      this.loading = true;
      Promise.all([
        // 主题
        getThemesList(),
        // 数据库
        getDataBasesList(),
        // 字典
        getDictionariesList(),
        // 周期
        getCyclesList(),
        // 标签
        getLabelList({ isAvailable: 1, pageNum: 1, pageSize: 2147483647 }),
        // 可选角色
        getRolesList(this.getCurrentWorkspaceId()),
      ]).then(
        ([themeRes, dbRes, dictionariesRes, cyclesRes, labelRes, roleRes]) => {
          this.loading = false;
          this.themesList = themeRes.list;
          this.dataBasesList = dbRes.list;
          this.lifecycleList = cyclesRes.list;
          this.labelList = labelRes.list;
          this.compressList = dictionariesRes.list.filter(
            (item) => item.type === "COMPRESS"
          );
          this.fileTypeList = dictionariesRes.list.filter(
            (item) => item.type === "FILE_STORAGE"
          );
          this.storageTypeList = dictionariesRes.list.filter(
            (item) => item.type === "STORAGE_ENGINE"
          );
          this.rolesList = roleRes.users;
        }
      );
    },
    /**
     * 获取格式化之后的数据
     * @return Object
     */
    handleGetFormatData() {
      let [warehouseLayerName, warehouseLayerNameEn] =
        this.formState._warehouseLayer.split("|");
      let [warehouseThemeName, warehouseThemeNameEn] =
        this.formState._warehouseTheme.split("|");
      let [lifecycle, lifecycleEn] = this.formState._lifecycle.split("|");
      return Object.assign({}, this.formState, {
        _warehouseLayer: undefined,
        _warehouseTheme: undefined,
        _lifecycle: undefined,
        name: `${this.formState.dataBase}.${this.formState.name}`,
        warehouseLayerName,
        warehouseLayerNameEn,
        warehouseThemeName,
        warehouseThemeNameEn,
        lifecycle,
        lifecycleEn,
      });
    },
    /**
     * 表单完成逻辑
     */
    handleFormFinish() {
      // 表单验证
      if (this.$refs["ColumnEditor"].isEditMode()) {
        return this.$Message.warning("请先保存字段");
      }
      if (!this.$refs["ColumnEditor"].checkColumnData()) {
        return false;
      }
      Promise.all([
        this.$refs["baseicInfoForm"].validate(),
        this.$refs["coreInfoForm"].validate(),
      ]).then(([valid1, valid2]) => {
        if (valid1 && valid2) {
          if (this.config.mode === "create" || this.config.mode === "copy") {
            addTable(this.handleGetFormatData()).then((res) => {
              this.$Message.success("创建成功");
              this.$Modal.confirm({
                title: "提示",
                content: "是否立即执行建表？",
                onOk: async () => {
                  await this.handleTableCreate(res.id);
                  this.$router.push({
                    path: "/dataModelCenter/tableManage/tableSearch",
                  });
                },
                onCancel: () => {
                  this.$router.push({
                    path: "/dataModelCenter/tableManage/tableSearch",
                  });
                },
              });
            });
            return;
          }
          if (this.config.mode === "update") {
            updateTable(this.config.id, this.handleGetFormatData()).then(() => {
              this.$Message.success("更新成功");
              this.$router.push({
                path: "/dataModelCenter/tableManage/tableSearch",
              });
            });
          }
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
