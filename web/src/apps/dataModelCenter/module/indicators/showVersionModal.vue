<template>
  <Drawer
    title="查看"
    width="920"
    @on-cancel="cancelCallBack"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
    :inner="true"
    :transfer="false"
  >
    <div v-if="_visible">
      <Form
        ref="formRef"
        :model="formState"
        label-position="left"
        :label-width="120"
      >
        <h2 class="form-block-title">基本信息</h2>
        <FormItem label="名称">
          <Input v-model="formState.name" />
        </FormItem>
        <FormItem label="字段标识">
          <Input v-model="formState.fieldIdentifier" />
        </FormItem>
        <FormItem label="负责人">
          <Input v-model="formState.owner" />
        </FormItem>
        <FormItem label="可用角色">
          <Input v-model="formState.principalName" />
        </FormItem>
        <FormItem label="是否核心指标">
          <i-switch
            :value="Boolean(formState.isCoreIndicator)"
            @input="formState.isCoreIndicator = Number($event)"
          >
            <span slot="open">是</span>
            <span slot="close">否</span>
          </i-switch>
        </FormItem>
        <FormItem label="描述">
          <Input
            type="textarea"
            v-model="formState.comment"
            placeholder="描述"
          />
        </FormItem>
        <h2 class="form-block-title">作用范围</h2>
        <FormItem label="主题域">
          <Input v-model="formState.themeArea" />
        </FormItem>
        <FormItem label="分层">
          <Input v-model="formState.layerArea" />
        </FormItem>
        <h2 class="form-block-title">指标内容</h2>
        <FormItem label="指标类型">
          <RadioGroup v-model="formState.content.indicatorType">
            <Radio :label="0">原子指标</Radio>
            <Radio :label="1">衍生指标</Radio>
            <Radio :label="2">派生指标</Radio>
            <Radio :label="3">复杂指标</Radio>
            <Radio :label="4">自定义指标</Radio>
          </RadioGroup>
        </FormItem>
        <!-- 原子指标 -->
        <Row v-if="formState.content.indicatorType === 0" key="yzzb">
          <Col :span="24">
            <FormItem label="关联度量">
              <Input v-model="formState._sourceInfo.info_0.measure" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="计算公式">
              <Input v-model="formState._sourceInfo.info_0.formula" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="指定维度">
              <Input v-model="formState._sourceInfo.info_0.dimension" />
            </FormItem>
          </Col>
        </Row>
        <!-- 衍生指标 -->
        <Row v-if="formState.content.indicatorType === 1" key="yszb">
          <Col :span="24">
            <FormItem label="依赖的原子指标">
              <Input v-model="formState._sourceInfo.info_1.indicatorName" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="计算公式">
              <Input v-model="formState._sourceInfo.info_1.formula" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="指定维度">
              <Input v-model="formState._sourceInfo.info_1.dimension" />
            </FormItem>
          </Col>
        </Row>
        <!-- 派生指标 -->
        <Row v-if="formState.content.indicatorType === 2" key="pszb">
          <Col :span="24">
            <FormItem label="依赖的指标">
              <Input v-model="formState._sourceInfo.info_2.indicatorName" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="时间周期">
              <Input v-model="formState._sourceInfo.info_2.cycle" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="修饰词">
              <Input v-model="formState._sourceInfo.info_2.modifier" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="指定维度">
              <Input v-model="formState._sourceInfo.info_2.dimension" />
            </FormItem>
          </Col>
        </Row>
        <!-- 复杂指标 -->
        <Row v-if="formState.content.indicatorType === 3" key="fzzb">
          <Col :span="24">
            <FormItem label="依赖的指标">
              <Input v-model="formState._sourceInfo.info_3.indicatorName" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="修饰词">
              <Input v-model="formState._sourceInfo.info_3.modifier" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="计算公式">
              <Input
                type="textarea"
                v-model="formState._sourceInfo.info_3.formula"
                placeholder="计算公式"
              ></Input>
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="指定维度">
              <Input v-model="formState._sourceInfo.info_3.dimension" />
            </FormItem>
          </Col>
        </Row>
        <!-- 自定义指标 -->
        <Row v-if="formState.content.indicatorType === 4" key="zdyzb">
          <Col :span="24">
            <FormItem label="计算公式">
              <Input
                type="textarea"
                v-model="formState._sourceInfo.info_4.formula"
              />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="指定维度">
              <Input v-model="formState._sourceInfo.info_4.dimension" />
            </FormItem>
          </Col>
        </Row>
        <h2 class="form-block-title">口径定义</h2>
        <FormItem label="业务口径">
          <Input v-model="formState.content.business" />
        </FormItem>
        <FormItem label="业务负责人">
          <Input v-model="formState.content.businessOwner" />
        </FormItem>
        <FormItem label="技术口径">
          <Input v-model="formState.content.calculation" />
        </FormItem>
        <FormItem label="技术负责人">
          <Input v-model="formState.content.calculationOwner" />
        </FormItem>
      </Form>
    </div>
  </Drawer>
</template>

<script>
import { extend } from "@/common/util/object";
import { sourceInfoMap } from "./statement";

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
  emits: ["_changeVisible"],
  data() {
    return {
      // 表单数据
      formState: {
        name: "",
        fieldIdentifier: "",
        comment: "",
        warehouseThemeName: "",
        owner: "",
        principalName: "ALL",
        isCoreIndicator: 0,
        isAvailable: 1,
        themeArea: "",
        layerArea: "",
        content: {
          indicatorType: 0,
          sourceInfo: {},
          business: "",
          businessOwner: "",
          calculation: "",
          calculationOwner: "",
        },
        _sourceInfo: {
          info_0: Object.assign({}, sourceInfoMap[0]),
          info_1: Object.assign({}, sourceInfoMap[1]),
          info_2: Object.assign({}, sourceInfoMap[2]),
          info_3: Object.assign({}, sourceInfoMap[3]),
          info_4: Object.assign({}, sourceInfoMap[4]),
        },
      },
    };
  },
  watch: {
    bodyData() {
      this.formatPropsToFormState();
    },
  },
  methods: {
    // 关闭回调
    cancelCallBack() {
      this.$refs["formRef"].resetFields();
    },
    async formatPropsToFormState() {
      try {
        let detail = this.bodyData.essential;
        detail.content = this.bodyData.content;
        detail.content.indicatorSourceInfo = JSON.parse(
          detail.content.indicatorSourceInfo
        );
        let newFormState = {
          name: detail.name,
          fieldIdentifier: detail.fieldIdentifier,
          comment: detail.comment,
          warehouseThemeName: detail.warehouseThemeName,
          owner: detail.owner,
          principalName: detail.principalName,
          isCoreIndicator: detail.isCoreIndicator,
          themeArea: detail.themeArea,
          layerArea: detail.layerArea,
          content: {
            indicatorType: detail.content.indicatorType,
            sourceInfo: {},
            business: detail.content.business,
            businessOwner: detail.content.businessOwner,
            calculation: detail.content.calculation,
            calculationOwner: detail.content.calculationOwner,
          },
          _sourceInfo: {
            info_0: Object.assign({}, sourceInfoMap[0]),
            info_1: Object.assign({}, sourceInfoMap[1]),
            info_2: Object.assign({}, sourceInfoMap[2]),
            info_3: Object.assign({}, sourceInfoMap[3]),
            info_4: Object.assign({}, sourceInfoMap[4]),
          },
        };
        extend(
          newFormState._sourceInfo[`info_${detail.content.indicatorType}`],
          detail.content.indicatorSourceInfo
        );
        this.formState = newFormState;
      } catch (error) {}
    },
  },
};
</script>

<style scoped lang="scss">
.form-block-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
</style>
