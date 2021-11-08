<template>
  <Drawer
    title="查看"
    width="920"
    @on-cancel="cancelCallBack"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
  >
    <Form
      ref="formRef"
      :model="formState"
      label-position="left"
      :label-width="120"
    >
      <h2 class="form-block-title">基本信息</h2>
      <FormItem label="名称" prop="name">
        <Input v-model="formState.name" placeholder="指标名"></Input>
      </FormItem>
      <FormItem label="字段标识" prop="fieldIdentifier">
        <Input v-model="formState.fieldIdentifier" placeholder="字段标识">
        </Input>
      </FormItem>
      <FormItem label="负责人" prop="owner">
        <Input v-model="formState.owner" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="可用角色" prop="principalName">
        <Select
          multiple
          :value="(formState.principalName || '').split(',')"
          @input="formState.principalName = $event.join()"
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
      <FormItem label="是否核心指标" prop="isCoreIndicator">
        <i-switch
          :value="Boolean(formState.isCoreIndicator)"
          @input="formState.isCoreIndicator = Number($event)"
        >
          <span slot="open">是</span>
          <span slot="close">否</span>
        </i-switch>
      </FormItem>
      <FormItem label="主题域" prop="warehouseThemeName">
        <SelectPage
          v-model="formState.warehouseThemeName"
          placeholder="请选择主题域和主题"
          :fetch="handleGetThemesList"
        />
      </FormItem>
      <FormItem label="描述" prop="comment">
        <Input type="textarea" v-model="formState.comment" placeholder="描述">
        </Input>
      </FormItem>
      <h2 class="form-block-title">作用范围</h2>
      <FormItem label="主题域" prop="themeArea">
        <SelectPage
          v-model="formState.themeArea"
          placeholder="主题域"
          :fetch="handleGetThemesList"
        />
      </FormItem>
      <FormItem label="分层" prop="layerArea">
        <SelectPage
          v-model="formState.layerArea"
          placeholder="分层"
          :fetch="handleGetLayersList"
        />
      </FormItem>
      <h2 class="form-block-title">指标内容</h2>
      <FormItem label="指标类型" prop="content.indicatorType">
        <RadioGroup v-model="formState.content.indicatorType">
          <Radio :label="0">原子指标</Radio>
          <Radio :label="1">衍生指标</Radio>
          <Radio :label="2">派生指标</Radio>
          <Radio :label="3">复杂指标</Radio>
          <Radio :label="4">自定义指标</Radio>
        </RadioGroup>
      </FormItem>
      <!-- 原子指标 -->
      <Row v-if="formState.content.indicatorType === 0">
        <Col :span="24">
          <FormItem label="关联度量" prop="_sourceInfo.info_0.measure">
            <SelectPage
              v-model="formState._sourceInfo.info_0.measure"
              searchMode
              placeholder="关联度量"
              :fetch="handleGetMeasures"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="计算公式" prop="_sourceInfo.info_0.formula">
            <Input
              v-model="formState._sourceInfo.info_0.formula"
              placeholder="计算公式"
            >
            </Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度" prop="_sourceInfo.info_0.dimension">
            <SelectPage
              multiple
              :value="(formState._sourceInfo.info_0.dimension || '').split(',')"
              @input="formState._sourceInfo.info_0.dimension = $event.join()"
              placeholder="指定维度"
              :fetch="handleGetDimensions"
            />
          </FormItem>
        </Col>
      </Row>
      <!-- 衍生指标 -->
      <Row v-if="formState.content.indicatorType === 1">
        <Col :span="24">
          <FormItem
            label="依赖的原子指标"
            prop="_sourceInfo.info_1.indicatorName"
          >
            <SelectPage
              searchMode
              multiple
              :value="
                (formState._sourceInfo.info_1.indicatorName || '').split(',')
              "
              @input="
                formState._sourceInfo.info_1.indicatorName = $event.join()
              "
              placeholder="请选择一个或多个原子指标"
              :fetch="handleGetIndicatorsTypeOne"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="计算公式" prop="_sourceInfo.info_1.formula">
            <Input
              v-model="formState._sourceInfo.info_1.formula"
              placeholder="计算公式"
            ></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度" prop="_sourceInfo.info_1.dimension">
            <SelectPage
              v-model="formState._sourceInfo.info_1.dimension"
              searchMode
              placeholder="指定维度"
              :fetch="handleGetDimensions"
            />
          </FormItem>
        </Col>
      </Row>
      <!-- 派生指标 -->
      <Row v-if="formState.content.indicatorType === 2">
        <Col :span="24">
          <FormItem label="依赖的指标" prop="_sourceInfo.info_2.indicatorName">
            <SelectPage
              v-model="formState._sourceInfo.info_2.indicatorName"
              searchMode
              placeholder="依赖的指标"
              :fetch="handleGetIndicators"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="时间周期" prop="_sourceInfo.info_2.cycle">
            <SelectPage
              v-model="formState._sourceInfo.info_2.cycle"
              placeholder="时间周期"
              :fetch="handleGetCyclesList"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="修饰词" prop="_sourceInfo.info_2.modifier">
            <SelectPage
              v-model="formState._sourceInfo.info_2.modifier"
              placeholder="修饰词"
              :fetch="handleGetModifiersList"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度" prop="_sourceInfo.info_2.dimension">
            <SelectPage
              v-model="formState._sourceInfo.info_2.dimension"
              searchMode
              placeholder="指定维度"
              :fetch="handleGetDimensions"
            />
          </FormItem>
        </Col>
      </Row>
      <!-- 复杂指标 -->
      <Row v-if="formState.content.indicatorType === 3">
        <Col :span="24">
          <FormItem label="依赖的指标" prop="_sourceInfo.info_3.indicatorName">
            <SelectPage
              v-model="formState._sourceInfo.info_3.indicatorName"
              searchMode
              placeholder="依赖的指标"
              :fetch="handleGetIndicators"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="修饰词" prop="_sourceInfo.info_3.modifier">
            <SelectPage
              v-model="formState._sourceInfo.info_3.modifier"
              placeholder="修饰词"
              :fetch="handleGetModifiersList"
            />
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="计算公式" prop="_sourceInfo.info_3.formula">
            <Input
              type="textarea"
              v-model="formState._sourceInfo.info_3.formula"
              placeholder="计算公式"
            ></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度" prop="_sourceInfo.info_3.dimension">
            <SelectPage
              v-model="formState._sourceInfo.info_3.dimension"
              searchMode
              placeholder="指定维度"
              :fetch="handleGetDimensions"
            />
          </FormItem>
        </Col>
      </Row>
      <!-- 自定义指标 -->
      <Row v-if="formState.content.indicatorType === 4">
        <Col :span="24">
          <FormItem label="计算公式" prop="_sourceInfo.info_4.formula">
            <Input
              type="textarea"
              v-model="formState._sourceInfo.info_4.formula"
              placeholder="计算公式"
            ></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度" prop="_sourceInfo.info_4.dimension">
            <SelectPage
              v-model="formState._sourceInfo.info_4.dimension"
              searchMode
              placeholder="指定维度"
              :fetch="handleGetDimensions"
            />
          </FormItem>
        </Col>
      </Row>
      <h2 class="form-block-title">口径定义</h2>
      <FormItem label="业务口径" prop="content.business">
        <Input v-model="formState.content.business" placeholder="业务口径">
        </Input>
      </FormItem>
      <FormItem label="业务负责人" prop="content.businessOwner">
        <Input
          v-model="formState.content.businessOwner"
          placeholder="业务负责人"
        >
        </Input>
      </FormItem>
      <FormItem label="技术口径" prop="content.calculation">
        <Input v-model="formState.content.calculation" placeholder="技术口径">
        </Input>
      </FormItem>
      <FormItem label="技术负责人" prop="content.calculationOwner">
        <Input
          v-model="formState.content.calculationOwner"
          placeholder="技术负责人"
        >
        </Input>
      </FormItem>
    </Form>
  </Drawer>
</template>

<script>
import {
  getMeasures,
  getDimensions,
  getIndicators,
  getThemesList,
  getCyclesList,
  getModifiersList,
  getLayersList,
} from "../../service/api";
import SelectPage from "../../components/selectPage";

/**
 * 根据现有key合并对象
 *  @param (Number)
 *  @return (String)
 */
function merge(obj, source) {
  let keys = Object.keys(obj);
  for (let i = 0; i < keys.length; i++) {
    let key = keys[i];
    if (source[key] !== undefined) obj[key] = source[key];
  }
  return obj;
}
let sourceInfoMap = {
  0: { measure: "", formula: "", dimension: "" },
  1: { indicatorName: "", formula: "", dimension: "" },
  2: { indicatorName: "", cycle: "", modifier: "", dimension: "" },
  3: { indicatorName: "", formula: "", modifier: "", dimension: "" },
  4: { formula: "", dimension: "" },
};
export default {
  components: { SelectPage },
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
      // 角色列表
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
  watch: {
    bodyData() {
      this.formatPropsToFormState();
    },
  },
  methods: {
    // 获取维度列表
    handleGetDimensions(name) {
      return getDimensions({
        name: name,
        pageNum: 1,
        pageSize: 30,
      }).then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
    // 获取度量列表
    handleGetMeasures(name) {
      return getMeasures({
        name: name,
        pageNum: 1,
        pageSize: 30,
      }).then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
    // 获取指标列表
    handleGetIndicators(name) {
      return getIndicators({
        name: name,
        pageNum: 1,
        pageSize: 30,
      }).then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
    // 获取原子指标列表
    handleGetIndicatorsTypeOne(name) {
      return getIndicators({
        name: name,
        pageNum: 1,
        pageSize: 30,
        indicatorType: 0,
      }).then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
    // 获取修饰词列表
    handleGetModifiersList() {
      return getModifiersList().then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.modifierType,
            value: item.modifierType,
          })),
        };
      });
    },
    // 获取分层列表
    handleGetLayersList() {
      return getLayersList().then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
    // 获取主题列表
    handleGetThemesList() {
      return getThemesList().then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
    // 获取周期列表
    handleGetCyclesList() {
      return getCyclesList().then((res) => {
        return {
          list: res.list.map((item) => ({
            label: item.name,
            value: item.name,
          })),
        };
      });
    },
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
        merge(
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
