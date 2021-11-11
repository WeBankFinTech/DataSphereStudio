<template>
  <Drawer
    title="新建/编辑"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    @on-close="cancelCallBack"
    width="920"
    :styles="styles"
  >
    <Form
      ref="formRef"
      :rules="ruleValidate"
      :model="formState"
      label-position="top"
    >
      <h3 style="margin-bottom: 12px"><b>基本信息</b></h3>
      <FormItem label="修饰词类别" prop="typeName">
        <Input v-model="formState.typeName" placeholder="建议为中文名" />
      </FormItem>
      <FormItem label="描述" prop="description">
        <Input
          type="textarea"
          v-model="formState.description"
          placeholder="描述"
        />
      </FormItem>
      <h3 style="margin-bottom: 12px"><b>作用范围</b></h3>
      <Row :gutter="12">
        <Col span="12">
          <FormItem prop="themeDomainId">
            <Select v-model="formState.themeDomainId" placeholder="主题域">
              <Option
                v-for="item in subjectDomainList"
                :value="item.id"
                :key="item.name"
              >
                {{ item.name }}
              </Option>
            </Select>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem prop="layerId">
            <Select v-model="formState.layerId" placeholder="分层">
              <Option
                v-for="item in layeredList"
                :value="item.id"
                :key="item.name"
              >
                {{ item.name }}
              </Option>
            </Select>
          </FormItem>
        </Col>
      </Row>
      <h3 style="margin-bottom: 12px"><b>修饰词列表</b></h3>
      <FormItem prop="list">
        <Table
          style="margin-bottom: 12px"
          :columns="tokenListColumns"
          :data="formState.list"
        >
          <template slot-scope="{ index }" slot="name">
            <Input
              type="text"
              placeholder="名字"
              v-model="formState.list[index].name"
            />
          </template>
          <template slot-scope="{ index }" slot="identifier">
            <Input
              type="text"
              placeholder="字段标识"
              v-model="formState.list[index].identifier"
            />
          </template>
          <template slot-scope="{ index }" slot="formula">
            <Input
              type="text"
              placeholder="字段计算公式"
              v-model="formState.list[index].formula"
            />
          </template>
          <template slot-scope="{ index }" slot="action">
            <Button type="error" @click="handleDeleteOneToken(index)">
              删除
            </Button>
          </template>
        </Table>
      </FormItem>
      <div style="display: flex; justify-content: flex-end">
        <Button type="primary" @click="handleAddToken()">新增</Button>
      </div>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <div class="drawer-footer">
      <Button style="margin-right: 8px" type="primary" @click="handleOk">
        确定
      </Button>
      <Button @click="handleCancel">取消</Button>
    </div>
  </Drawer>
</template>

<script>
import {
  getThemedomains,
  getLayersAll,
  createModifiers,
  editModifiers,
  getModifiersById,
} from "@dataWarehouseDesign/service/api";
export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  props: {
    // 是否可见
    _visible: {
      type: Boolean,
      required: true,
    },
    // 模式
    mode: {
      type: String,
      required: true,
    },
    // id
    id: {
      type: Number,
      default: 0,
    },
  },
  emits: ["finish", "_changeVisible"],
  watch: {
    _visible(val) {
      if (val && this.id) this.handleGetById(this.id);
    },
  },
  data() {
    return {
      // 底部样式
      styles: {
        height: "calc(100% - 55px)",
        overflow: "auto",
        paddingBottom: "53px",
        position: "static",
      },
      // 词列表列
      tokenListColumns: [
        {
          title: "修饰词名称",
          key: "name",
          slot: "name",
        },
        {
          title: "字段标识",
          key: "identifier",
          slot: "identifier",
        },
        {
          title: "计算公式",
          key: "formula",
          slot: "formula",
        },
        {
          title: "操作",
          slot: "action",
        },
      ],
      // 是否加载中
      loading: false,
      // 主题域列表
      subjectDomainList: [],
      // 分层列表
      layeredList: [],
      // 验证规则
      ruleValidate: {
        typeName: [
          {
            required: true,
            message: "修饰词类别必填",
            trigger: "submit",
          },
        ],
      },
      // 表单数据
      formState: {
        typeName: "",
        description: "",
        layerId: "",
        themeDomainId: "",
        list: [],
      },
    };
  },
  mounted() {
    this.handleGetLayerListAndSubjectDomainList();
  },
  methods: {
    async handleGetById(id) {
      this.loading = true;
      let { item } = await getModifiersById(id);
      this.loading = false;
      this.formState.description = item.description;
      this.formState.typeName = item.modifierType;
      this.formState.list = item.list.map((item) => {
        return {
          name: item.name,
          identifier: item.identifier,
          formula: item.formula,
        };
      });
      this.formState.layerId = item.layerId;
      this.formState.themeDomainId = item.themeDomainId;
    },
    cancelCallBack() {
      this.$refs["formRef"].resetFields();
    },
    handleCancel() {
      this.$refs["formRef"].resetFields();
      this.$emit("_changeVisible", false);
    },
    async handleOk() {
      this.$refs["formRef"].validate(async (valid) => {
        if (valid) {
          try {
            this.loading = true;
            if (this.mode === "create") {
              await createModifiers(Object.assign({}, this.formState));
              this.loading = false;
            }
            if (this.mode === "edit") {
              await editModifiers(this.id, Object.assign({}, this.formState));
              this.loading = false;
            }
            this.$refs["formRef"].resetFields();
            this.$emit("_changeVisible", false);
            this.$emit("finish");
          } catch (error) {
            this.loading = false;
            console.log(error);
          }
        }
      });
    },
    handleDeleteOneToken(index) {
      this.formState.list.splice(index, 1);
    },
    handleAddToken() {
      this.formState.list.push({
        name: "",
        identifier: "",
        formula: "",
      });
    },
    async handleGetLayerListAndSubjectDomainList() {
      this.loading = true;
      let { page } = await getThemedomains();
      let { list } = await getLayersAll();
      this.loading = false;
      this.subjectDomainList = page.items;
      this.layeredList = list;
    },
  },
};
</script>

<style scoped lang="scss">
.drawer-footer {
  width: 100%;
  position: absolute;
  bottom: 0;
  left: 0;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: left;
  background: #fff;
}
</style>
