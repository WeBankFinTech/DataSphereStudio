<template>
  <Modal
    title="新建/编辑"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    @on-cancel="cancelCallBack"
  >
    <Form
      ref="formRef"
      :rules="ruleValidate"
      :model="formState"
      label-position="top"
    >
      <FormItem label="名称" prop="name">
        <Input v-model="formState.name" placeholder="建议输入中文名称"></Input>
      </FormItem>
      <FormItem label="英文缩写" prop="enName">
        <Input
          v-model="formState.enName"
          placeholder="只支持英文数据及下划线"
        ></Input>
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
      <h3 style="margin-bottom: 12px"><b>计算公式</b></h3>
      <Row :gutter="12">
        <Col span="12">
          <FormItem label="开始时间" prop="statStartFormula">
            <Input
              type="textarea"
              v-model="formState.statStartFormula"
              placeholder="例如：${runDate}"
            ></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem label="结束时间" prop="statEndFormula">
            <Input
              type="textarea"
              v-model="formState.statEndFormula"
              placeholder="例如：${runDate}"
            ></Input>
          </FormItem>
        </Col>
      </Row>
      <FormItem label="负责人" prop="owner">
        <Input v-model="formState.owner" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="可用角色" prop="principalName">
        <Select
          v-model="formState.principalName"
          multiple
          placeholder="可用角色"
        >
          <Option
            v-for="item in authorityList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="描述" prop="description">
        <Input
          type="textarea"
          v-model="formState.description"
          placeholder="描述"
        ></Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <template slot="footer">
      <Button @click="handleCancel">取消</Button>
      <Button type="primary" @click="handleOk">确定</Button>
    </template>
  </Modal>
</template>

<script>
import {
  createStatisticalPeriods,
  getStatisticalPeriodsById,
  editStatisticalPeriods,
  getThemedomains,
  getLayersAll,
} from "../../service/api";
import storage from "@/common/helper/storage";
let userName = storage.get("baseInfo", "local").username;
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
      // 验证规则
      ruleValidate: {
        name: [
          {
            required: true,
            message: "统计周期名称必填",
            trigger: "submit",
          },
        ],
        enName: [
          {
            required: true,
            message: "英文缩写必填",
            trigger: "submit",
          },
        ],
        statStartFormula: [
          {
            required: true,
            message: "此项必填",
            trigger: "submit",
          },
        ],
        statEndFormula: [
          {
            required: true,
            message: "此项必填",
            trigger: "submit",
          },
        ],
      },
      // 是否加载中
      loading: false,
      // 表单数据
      formState: {
        name: "",
        enName: "",
        statStartFormula: "",
        statEndFormula: "",
        principalName: ["ALL"],
        description: "",
        owner: userName,
        layerId: "",
        themeDomainId: "",
      },
      // 主题列表
      subjectDomainList: [],
      // 分层列表
      layeredList: [],
      // 可用角色列表
      authorityList: [
        {
          value: "ALL",
          label: "ALL",
        },
        {
          value: "New York",
          label: "New York",
        },
        {
          value: "London",
          label: "London",
        },
      ],
    };
  },
  mounted() {
    this.handleGetLayerListAndSubjectDomainList();
  },
  methods: {
    async handleGetById(id) {
      this.loading = true;
      let { item } = await getStatisticalPeriodsById(id);
      this.loading = false;
      this.formState.name = item.name;
      this.formState.enName = item.enName;
      this.formState.owner = item.owner;
      this.formState.principalName = item.principalName.split(",");
      this.formState.statStartFormula = item.startTimeFormula;
      this.formState.statEndFormula = item.endTimeFormula;
      this.formState.layerId = item.layerId;
      this.formState.themeDomainId = item.themeDomainId;
      this.formState.description = item.description;
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
              await createStatisticalPeriods(
                Object.assign({}, this.formState, {
                  principalName: this.formState.principalName.join(","),
                })
              );
              this.loading = false;
            }
            if (this.mode === "edit") {
              await editStatisticalPeriods(
                this.id,
                Object.assign({}, this.formState, {
                  principalName: this.formState.principalName.join(","),
                })
              );
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

<style scoped lang="less"></style>
