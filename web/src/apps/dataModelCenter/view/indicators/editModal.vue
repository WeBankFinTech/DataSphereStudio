<template>
  <Drawer
    title="新建/编辑指标"
    v-model="visible"
    width="920"
    :styles="styles"
    @on-cancel="$emit('update:visible', false)"
  >
    <Form :model="formState" label-position="left" :label-width="120">
      <h2 class="form-block-title">基本信息</h2>
      <FormItem label="名称">
        <Input v-model="formState.name" placeholder="指标名"></Input>
      </FormItem>
      <FormItem label="英文名">
        <Input v-model="formState.ename" placeholder="字段标识"></Input>
      </FormItem>
      <FormItem label="可用角色">
        <Select v-model="formState.authority" multiple placeholder="可用角色">
          <Option
            v-for="item in authorityList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="作用范围">
        <Row>
          <Col :span="12">
            <Select
              v-model="formState.authority"
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
          </Col>
          <Col :span="12">
            <Select
              v-model="formState.authority"
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
          </Col>
        </Row>
      </FormItem>
      <FormItem label="是否核心指标">
        <i-switch v-model="formState.ename">
          <span slot="open">是</span>
          <span slot="close">否</span>
        </i-switch>
      </FormItem>
      <FormItem label="主题域">
        <Input v-model="formState.ename" placeholder="可用角色"></Input>
      </FormItem>
      <h2 class="form-block-title">指标内容</h2>
      <FormItem label="指标类型">
        <RadioGroup v-model="formState.indexType">
          <Radio :label="0">原子指标</Radio>
          <Radio :label="1">衍生指标</Radio>
          <Radio :label="2">派生指标</Radio>
          <Radio :label="3">复杂指标</Radio>
          <Radio :label="4">自定义指标</Radio>
        </RadioGroup>
      </FormItem>
      <!-- 原子指标 -->
      <Row v-show="formState.indexType === 0">
        <Col :span="24">
          <FormItem label="关联度量">
            <Input v-model="formState.ename" placeholder="关联度量"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="计算公式">
            <Input v-model="formState.ename" placeholder="计算公式"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度">
            <Input v-model="formState.ename" placeholder="指定维度"></Input>
          </FormItem>
        </Col>
      </Row>
      <!-- 衍生指标 -->
      <Row v-show="formState.indexType === 1">
        <Col :span="24">
          <FormItem label="依赖的原子指标">
            <Input
              v-model="formState.ename"
              placeholder="依赖的原子指标"
            ></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="计算公式">
            <Input v-model="formState.ename" placeholder="计算公式"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度">
            <Input v-model="formState.ename" placeholder="指定维度"></Input>
          </FormItem>
        </Col>
      </Row>
      <!-- 负责指标 -->
      <Row v-show="formState.indexType === 2">
        <Col :span="24">
          <FormItem label="依赖的指标">
            <Input v-model="formState.ename" placeholder="依赖的指标"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="时间周期">
            <Input v-model="formState.ename" placeholder="时间周期"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="修饰词">
            <Input v-model="formState.ename" placeholder="修饰词"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度">
            <Input v-model="formState.ename" placeholder="指定维度"></Input>
          </FormItem>
        </Col>
      </Row>
      <!-- 复杂指标 -->
      <Row v-show="formState.indexType === 3">
        <Col :span="24">
          <FormItem label="依赖的指标">
            <Input v-model="formState.ename" placeholder="依赖的指标"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="修饰词">
            <Input v-model="formState.ename" placeholder="修饰词"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="计算公式">
            <Input v-model="formState.ename" placeholder="计算公式"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度">
            <Input v-model="formState.ename" placeholder="指定维度"></Input>
          </FormItem>
        </Col>
      </Row>
      <!-- 自定义指标 -->
      <Row v-show="formState.indexType === 4">
        <Col :span="24">
          <FormItem label="计算公式">
            <Input v-model="formState.ename" placeholder="计算公式"></Input>
          </FormItem>
        </Col>
        <Col :span="24">
          <FormItem label="指定维度">
            <Input v-model="formState.ename" placeholder="指定维度"></Input>
          </FormItem>
        </Col>
      </Row>
      <h2 class="form-block-title">口径定义</h2>
      <FormItem label="业务口径">
        <Input v-model="formState.created" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="业务负责人">
        <Input v-model="formState.created" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="技术口径">
        <Input v-model="formState.created" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="技术负责人">
        <Input v-model="formState.created" placeholder="负责人"></Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <div class="drawer-footer">
      <Button type="primary" @click="value3 = false">确定</Button>
      <Button style="margin-right: 8px" @click="value3 = false">取消</Button>
    </div>
  </Drawer>
</template>

<script>
export default {
  model: {
    prop: "_visible",
    event: "_changeVisible",
  },
  computed: {
    visible: {
      get() {
        return this._visible;
      },
      set(val) {
        this.$emit("_changeVisible", val);
      },
    },
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
    },
  },
  emits: ["finish", "_changeVisible"],
  data() {
    return {
      // 是否加载中
      loading: false,
      // 表单数据
      formState: {
        name: "",
        ename: "",
        created: "",
        authority: [],
        des: "",
        indexType: 0,
      },
      styles: {
        height: "calc(100% - 55px)",
        overflow: "auto",
        paddingBottom: "53px",
        position: "static",
      },
      usablelib: [
        {
          value: "New York",
          label: "New York",
        },
        {
          value: "London",
          label: "London",
        },
      ],
      authorityList: [
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
  methods: {
    async handleOk() {
      this.loading = true;
      this.loading = false;
      this.$emit("_changeVisible", false);
      this.$emit("finish");
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
.form-block-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
</style>
