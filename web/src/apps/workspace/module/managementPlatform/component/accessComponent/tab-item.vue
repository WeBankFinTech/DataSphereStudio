<template>
  <div class="access-component-wrap">
    <div class="access-component-container">
      <Form
        :model="formItem"
        label-position="top"
        :rules="ruleValidate"
        ref="formValidate"
      >
        <FormItem prop="onestop_menu_id">
          <span class="form-item-label">接入类别</span>
          <Select v-model="formItem.onestop_menu_id">
            <Option
              v-for="item in menuOptions"
              :value="item.value"
              :key="item.value"
              >{{ item.label }}</Option
            >
          </Select>
        </FormItem>
        <FormItem prop="title_cn">
          <span class="form-item-label">组件/应用名</span>
          <Input v-model="formItem.title_cn"></Input>
        </FormItem>
        <FormItem prop="title_en">
          <span class="form-item-label">组件/应用名英文</span>
          <Input v-model="formItem.title_en"></Input>
        </FormItem>
        <FormItem prop="url">
          <span class="form-item-label">baseurl</span>
          <Input v-model="formItem.url"></Input>
        </FormItem>
        <FormItem prop="homepage_url">
          <span class="form-item-label">首页</span>
          <Input v-model="formItem.homepage_url"></Input>
        </FormItem>
        <FormItem prop="project_url">
          <span class="form-item-label">项目页</span>
          <Input v-model="formItem.project_url"></Input>
        </FormItem>
        <FormItem prop="redirect_url">
          <span class="form-item-label">单点接口</span>
          <Input v-model="formItem.redirect_url"></Input>
        </FormItem>
        <FormItem prop="if_iframe">
          <span class="swith-label">iframe</span>
          <i-switch v-model="formItem.if_iframe" />
        </FormItem>
        <FormItem prop="is_active">
          <span class="swith-label">激活</span>
          <i-switch v-model="formItem.is_active" />
        </FormItem>
        <FormItem prop="desc_cn">
          <span class="form-item-label">描述</span>
          <Input
            v-model="formItem.desc_cn"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 10 }"
            placeholder="请输入描述"
          ></Input>
        </FormItem>
        <FormItem prop="desc_en">
          <span class="form-item-label">英文描述</span>
          <Input
            v-model="formItem.desc_en"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 18 }"
            placeholder="请输入英文描述"
          ></Input>
        </FormItem>
        <!-- <FormItem>
          <span class="form-item-label">进入按钮描述</span>
          <Input v-model="formItem.access_button_cn" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入英文描述"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">进入按钮英文描述</span>
          <Input v-model="formItem.access_button_en" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入英文描述"></Input>
        </FormItem> -->
        <FormItem>
          <Button @click="handleCancel">取消</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            @click="handleSave(formItem)"
            >提交</Button
          >
        </FormItem>
      </Form>
    </div>
  </div>
</template>

<script>
import { formatComponentData } from "../../util/fomat";
const tempFormItem = {
  onestop_menu_id: 1,
  title_cn: "新增组件",
  title_en: "",
  url: "",
  homepage_url: "",
  project_url: "",
  redirect_url: "",
  if_iframe: 1,
  is_active: 1,
  desc_cn: "",
  desc_en: ""
  // access_button_cn: 'not null',
  // access_button_en: 'not null',
};
export default {
  props: {
    componentData: {
      type: Object,
      default: () => tempFormItem
    }
  },
  data() {
    return {
      formItem: formatComponentData(this.componentData),
      ruleValidate: {
        onestop_menu_id: [
          { required: true, message: "接入类别不能为空", trigger: "blur" }
        ],
        title_cn: [
          { required: true, message: "组件/应用名不能为空", trigger: "blur" }
        ],
        title_en: [
          {
            required: true,
            message: "组件/应用名英文不能为空",
            trigger: "blur"
          }
        ],
        url: [{ required: true, message: "baseurl不能为空", trigger: "blur" }],
        homepage_url: [
          { required: true, message: "首页不能为空", trigger: "blur" }
        ],
        project_url: [
          { required: true, message: "项目页不能为空", trigger: "blur" }
        ],
        redirect_url: [
          { required: true, message: "单点接口不能为空", trigger: "blur" }
        ],
        desc_cn: [{ required: true, message: "描述不能为空", trigger: "blur" }],
        desc_en: [
          { required: true, message: "英文描述不能为空", trigger: "blur" }
        ]
      },
      menuOptions: []
    };
  },
  mounted() {
    this.getMenuOption();
  },
  watch: {
    // 通过watch使得 父传子 的数据响应化
    componentData(newValue) {
      this.formItem = formatComponentData(newValue);
    }
  },
  methods: {
    handleCancel() {
      this.formItem = formatComponentData(this.componentData);
    },
    handleSave(componentItem) {
      // 提交前验证
      const postData = Object.assign({}, componentItem);
      let _this = this;
      _this.$refs["formValidate"].validate(valid => {
        if (valid) {
          _this.$Message.success("Success!");
          // 提交
          _this.$emit("on-save", postData);
        } else {
          _this.$Message.error("Fail!");
        }
      });
    },
    getMenuOption() {
      let temp = JSON.parse(sessionStorage.getItem("menuOptions"));
      let _menuOptions = [];
      temp.forEach(item => {
        let o = Object.create(null);
        o.value = item.title_en;
        o.label = item.title_cn;
        _menuOptions.push(o);
      });
      this.menuOptions = _menuOptions;
      console.log("this.menuOptions", this.menuOptions);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.access-component-wrap {
  padding-left: 59px;
  padding-top: 24px;
  .access-component-container {
    width: 360px;
  }
  .form-item-label {
    font-size: 14px;
    font-family: PingFangSC-Regular;
    line-height: 22px;
    @include font-color(#515a6e, $dark-text-color);
    font-weight: bold;
  }
  .swith-label {
    width: 50px;
    display: inline-block;
    margin-right: 8px;
    font-size: 14px;
    font-family: PingFangSC-Regular;
    line-height: 22px;
    font-weight: bold;
    @include font-color(#515a6e, $dark-text-color);
  }
}
</style>
