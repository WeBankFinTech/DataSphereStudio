<template>
  <div class="access-component-wrap">
    <div class="access-component-container">
      <Form
        :model="formItem"
        label-position="top"
        :rules="ruleValidate"
        ref="formValidate"
      >
        <FormItem prop="onestopMenuId">
          <span class="form-item-label">{{ $t('message.workspace.AccessCategories') }}</span>
          <Select v-model="formItem.onestopMenuId">
            <Option
              v-for="item in menuOptions"
              :value="item.value"
              :key="item.value"
            >{{ item.label }}</Option
            >
          </Select>
        </FormItem>
        <FormItem prop="titleCn">
          <span class="form-item-label">组件/应用名</span>
          <Input v-model="formItem.titleCn"></Input>
        </FormItem>
        <FormItem prop="titleEn">
          <span class="form-item-label">组件/应用名英文</span>
          <Input v-model="formItem.titleEn"></Input>
        </FormItem>
        <FormItem prop="url">
          <span class="form-item-label">baseurl</span>
          <Input v-model="formItem.url"></Input>
        </FormItem>
        <FormItem prop="homepageUrl">
          <span class="form-item-label">{{ $t('message.workspace.Homepage') }}</span>
          <Input v-model="formItem.homepageUrl"></Input>
        </FormItem>
        <FormItem prop="projectUrl">
          <span class="form-item-label">{{ $t('message.workspace.Projectpage') }}</span>
          <Input v-model="formItem.projectUrl"></Input>
        </FormItem>
        <FormItem prop="redirectUrl">
          <span class="form-item-label">{{ $t('message.workspace.Singleinter') }}</span>
          <Input v-model="formItem.redirectUrl"></Input>
        </FormItem>
        <FormItem prop="ifIframe">
          <span class="swith-label">iframe</span>
          <i-switch v-model="formItem.ifIframe" />
        </FormItem>
        <FormItem prop="isActive">
          <span class="swith-label">{{ $t('message.workspace.Activate') }}</span>
          <i-switch v-model="formItem.isActive" />
        </FormItem>
        <FormItem prop="descCn">
          <span class="form-item-label">{{ $t('message.workspace.Description') }}</span>
          <Input
            v-model="formItem.descCn"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 10 }"
            :placeholder="$t('message.workspace.Pleaseinputdesc')"
          ></Input>
        </FormItem>
        <FormItem prop="descEn">
          <span class="form-item-label">{{ $t('message.workspace.Engilish') }}</span>
          <Input
            v-model="formItem.descEn"
            type="textarea"
            :autosize="{ minRows: 6, maxRows: 18 }"
            :placeholder="$t('message.workspace.Pleaseenputendesc')"
          ></Input>
        </FormItem>
        <!-- <FormItem>
          <span class="form-item-label">进入按钮描述</span>
          <Input v-model="formItem.access_button_cn" type="textarea" :autosize="{minRows: 2,maxRows: 5}" :placeholder="$t('message.workspace.Pleaseenputendesc')"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">进入按钮英文描述</span>
          <Input v-model="formItem.access_button_en" type="textarea" :autosize="{minRows: 2,maxRows: 5}" :placeholder="$t('message.workspace.Pleaseenputendesc')"></Input>
        </FormItem> -->
        <FormItem>
          <Button @click="handleCancel">{{ $t('message.workspace.Cancel') }}</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            @click="handleSave(formItem)"
          >{{ $t('message.workspace.Submit') }}</Button
          >
        </FormItem>
      </Form>
    </div>
  </div>
</template>

<script>
import { formatComponentData } from "../../util/fomat";
import i18n from '@dataspherestudio/shared/common/i18n';
const tempFormItem = {
  onestopMenuId: 1,
  titleCn: i18n.t('message.workspace.AddComp'),
  titleEn: "",
  url: "",
  homepageUrl: "",
  projectUrl: "",
  redirectUrl: "",
  ifIframe: 1,
  isActive: 1,
  descCn: "",
  descEn: ""
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
        onestopMenuId: [
          { required: true, message: this.$t('message.workspace.Access'), trigger: "blur" }
        ],
        titleCn: [
          { required: true, message: "组件/应用名不能为空", trigger: "blur" }
        ],
        titleEn: [
          {
            required: true,
            message: "组件/应用名英文不能为空",
            trigger: "blur"
          }
        ],
        url: [{ required: true, message: "baseurl不能为空", trigger: "blur" }],
        homepageUrl: [
          { required: true, message: this.$t('message.workspace.Home'), trigger: "blur" }
        ],
        projectUrl: [
          { required: true, message: this.$t('message.workspace.ProjectEmpty'), trigger: "blur" }
        ],
        descCn: [{ required: true, message: this.$t('message.workspace.DescriptionEmpty'), trigger: "blur" }],
        descEn: [
          { required: true, message: this.$t('message.workspace.EnDescEmpty'), trigger: "blur" }
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
        o.value = item.titleEn;
        o.label = item.titleCn;
        _menuOptions.push(o);
      });
      this.menuOptions = _menuOptions;
      console.log("this.menuOptions", this.menuOptions);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.access-component-wrap {
  padding-left: 59px;
  padding-top: 24px;
  .access-component-container {
    width: 360px;
  }
  .form-item-label {
    font-size: 14px;

    line-height: 22px;
    @include font-color(#515a6e, $dark-text-color);
    font-weight: bold;
  }
  .swith-label {
    width: 50px;
    display: inline-block;
    margin-right: 8px;
    font-size: 14px;

    line-height: 22px;
    font-weight: bold;
    @include font-color(#515a6e, $dark-text-color);
  }
}
</style>
