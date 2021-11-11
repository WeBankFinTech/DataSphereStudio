<template>
  <Modal
    title="关联数仓"
    :value="_visible"
    @input="$emit('_changeVisible', $event)"
  >
    <Spin fix v-if="loading"></Spin>
    <Form>
      <FormItem label="类型：">
        <RadioGroup v-model="currentModeType">
          <Radio :label="0">维度</Radio>
          <Radio :label="1">指标</Radio>
          <Radio :label="2">度量</Radio>
        </RadioGroup>
      </FormItem>
      <FormItem label="选择：">
        <Row :gutter="15">
          <Col :span="9">
            <div style="border: 1px solid #e8eaec">
              <CellGroup @on-click="handleChangeTheme">
                <Cell
                  v-for="theme in themesList"
                  :key="theme.id"
                  :title="theme.name"
                  :selected="theme.name === currentTheme"
                  :name="theme.name"
                />
              </CellGroup>
            </div>
          </Col>
          <Col :span="9">
            <div style="border: 1px solid #e8eaec">
              <CellGroup @on-click="handleChangeItem">
                <Cell
                  v-for="item in rightDataList"
                  :key="item.id"
                  :title="item.name"
                  :selected="item.name === currentRightItem"
                  :name="item.name"
                />
              </CellGroup>
            </div>
          </Col>
        </Row>
      </FormItem>
    </Form>
    <template slot="footer">
      <Button type="primary" @click="handleOk">确定</Button>
    </template>
  </Modal>
</template>

<script>
import { getThemesList } from "@dataModelCenter/service/tableManageApi";
import {
  getMeasures,
  getIndicators,
  getDimensions,
} from "@dataModelCenter/service/api";
export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  props: {
    _visible: {
      type: Boolean,
    },
  },
  emits: ["finish", "_changeVisible"],
  data() {
    return {
      // 当前类型
      currentModeType: 0,
      // 主题列表
      themesList: [],
      // 当前选择主题
      currentTheme: "",
      // 当前右侧数据
      rightDataList: [],
      // 当前选中的右侧数据f
      currentRightItem: "",
      // 加载中
      loading: false,
    };
  },
  watch: {
    // 类型改变
    currentModeType: {
      handler(value) {
        if (this.currentTheme) {
          if (value === 0) {
            return this.handleGetDimensions(this.currentTheme);
          }
          if (value === 1) {
            return this.handleGetIndicators(this.currentTheme);
          }
          if (value === 2) {
            return this.handleGetMeasures(this.currentTheme);
          }
        }
      },
    },
    // 主题改变
    currentTheme: {
      handler(value) {
        if (this.currentModeType === 0) {
          return this.handleGetDimensions(value);
        }
        if (this.currentModeType === 1) {
          return this.handleGetIndicators(value);
        }
        if (this.currentModeType === 2) {
          return this.handleGetMeasures(value);
        }
      },
    },
  },
  mounted() {
    this.handleGetThemesList();
  },
  methods: {
    handleOk() {
      if (this.currentRightItem === "" && this.currentRightItem === 0) {
        return this.$Message.info("必须选择一项哦！");
      }
      this.$emit("_changeVisible", false);
      this.$emit("finish", {
        modelType: this.currentModeType,
        modelName: this.currentRightItem,
      });
    },
    // 主题选择
    handleChangeTheme(name) {
      this.currentTheme = name;
    },
    // 右侧选项改变
    handleChangeItem(name) {
      this.currentRightItem = name;
    },
    // 获取主题列表
    handleGetThemesList() {
      this.loading = true;
      getThemesList().then((res) => {
        this.loading = false;
        this.themesList = res.list;
      });
    },
    // 获取维度
    handleGetDimensions(theme) {
      this.loading = true;
      getDimensions({ warehouseThemeName: theme }).then((res) => {
        this.loading = false;
        this.rightDataList = res.list;
      });
    },
    // 获取度量
    handleGetMeasures(theme) {
      this.loading = true;
      getMeasures({ warehouseThemeName: theme }).then((res) => {
        this.loading = false;
        this.rightDataList = res.list;
      });
    },
    // 获取指标
    handleGetIndicators(theme) {
      this.loading = true;
      getIndicators({ warehouseThemeName: theme }).then((res) => {
        this.loading = false;
        this.rightDataList = res.list;
      });
    },
  },
};
</script>

<style>
</style>
