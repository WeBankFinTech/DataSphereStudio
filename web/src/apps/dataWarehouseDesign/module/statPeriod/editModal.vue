<template>
  <Drawer
    title="新建/编辑统计周期"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    @on-close="cancelCallBack"
    width="920"
    :styles="styles"
    :inner="true"
    :transfer="false"
    :mask-closable="false"
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
        <Input v-model="formState.enName" placeholder="只支持英文数据及下划线">
        </Input>
      </FormItem>
      <h3 style="margin-bottom: 12px"><b>作用范围</b></h3>
      <Row :gutter="12">
        <Col span="12">
          <FormItem prop="themeDomainId">
            <Select v-model="formState.themeDomainId" placeholder="主题域">
              <Option
                v-for="item in themeList"
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
                v-for="item in layerList"
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
        <Select
          v-model="formState.owner"
          placeholder="默认为创建用户"
        >
          <Option
            v-for="item in usersList"
            :value="item.name"
            :key="item.name"
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
      <FormItem label="描述" prop="description">
        <Input
          type="textarea"
          v-model="formState.description"
          placeholder="描述"
        ></Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <div class="drawer-footer">
      <Button
        style="margin-right: 8px"
        type="primary"
        @click="handleOk"
        :disabled="referenced && mode === 'edit'"
      >
        确定
      </Button>
      <Button @click="handleCancel">取消</Button>
    </div>
  </Drawer>
</template>

<script>
import {getThemedomains} from "@/apps/dataWarehouseDesign/service/api/theme";
import {
  createStatisticalPeriods,
  getStatisticalPeriodsById,
  editStatisticalPeriods,
} from "@/apps/dataWarehouseDesign/service/api/statisticalPeriods";
import {getLayersAll} from "@/apps/dataWarehouseDesign/service/api/layer";
import mixin from "@/common/service/mixin";
import {getRolesList, getUsersList} from "@/apps/dataWarehouseDesign/service/api/common";

export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  mixins: [mixin],
  props: {
    _visible: {
      type: Boolean,
      required: true,
    },
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
      if (val) this.handlePreFetchData();
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
            message: "名称必填",
            trigger: "submit",
          },
          {
            message: "仅支持中文，下划线，数字",
            pattern: /^[0-9_\u4e00-\u9fa5]+$/g,
            trigger: "submit",
          },
        ],
        enName: [
          {
            required: true,
            message: "英文名必填",
            trigger: "submit",
          },
          {
            message: "仅支持英文，下划线，数字",
            pattern: /^[a-zA-Z0-9_]+$/g,
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
        principalName: "ALL",
        description: "",
        owner: this.getUserName(),
        layerId: "",
        themeDomainId: "",
      },
      // 是否有引用
      referenced: false,
      // 主题列表
      themeList: [],
      // 分层列表
      layerList: [],
      // 用户列表
      usersList: [],
      // 角色列表
      rolesList: [],
      // 底部样式
      styles: {
        height: "calc(100% - 55px)",
        overflow: "auto",
        paddingBottom: "53px",
        position: "static",
      },
    };
  },
  methods: {
    /**
     * 获取预置数据
     */
    handlePreFetchData() {
      this.loading = true
      let id = this.getCurrentWorkspaceId()
      Promise.all([
        getUsersList(id),
        getRolesList(id),
        getLayersAll({ isAvailable: true }),
        getThemedomains({ enabled: true }),
      ]).then(([userRes, roleRes, layersRes, themesRes]) => {
        this.loading = false
        this.usersList = userRes.list;
        this.rolesList = roleRes.list;
        this.layerList = layersRes.list;
        this.themeList = themesRes.page.items;
      }).catch(() => {
        this.loading = false
      })
    },
    /**
     * 根据id获取数据
     * @param id
     * @returns {Promise<void>}
     */
    async handleGetById(id) {
      this.loading = true;
      let {item} = await getStatisticalPeriodsById(id);
      this.loading = false;
      this.formState.name = item.name;
      this.formState.enName = item.enName;
      this.formState.owner = item.owner;
      this.formState.principalName = item.principalName;
      this.formState.statStartFormula = item.startTimeFormula;
      this.formState.statEndFormula = item.endTimeFormula;
      this.formState.layerId = item.layerId;
      this.formState.themeDomainId = item.themeDomainId;
      this.formState.description = item.description;
      this.referenced = item.referenced;
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
              await createStatisticalPeriods(this.formState);
              this.$Message.success("创建成功")
              this.loading = false;
            }
            if (this.mode === "edit") {
              await editStatisticalPeriods(this.id, this.formState);
              this.$Message.success("更新成功")
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
      let {page} = await getThemedomains({enabled: true});
      let {list} = await getLayersAll({isAvailable: true});
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
