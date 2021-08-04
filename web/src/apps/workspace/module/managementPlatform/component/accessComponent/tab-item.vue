<template>
  <div class="access-component-wrap">
    <div class="access-component-container">
      <Form :model="formItem"  label-position="top">
        <FormItem >
          <span class="form-item-label">接入类别</span>
          <Select v-model="formItem.catagory">
            <Option value="dataAccess">数据接入</Option>
            <Option value="dataDevelopment">数据开发</Option>
            <Option value="dataGovernance">数据治理</Option>
          </Select>
        </FormItem>
        <FormItem>
          <span class="form-item-label">组件/应用名</span>
          <Input v-model="formItem.name"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">组件/应用名英文</span>
          <Input v-model="formItem.engName"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">baseurl</span>
          <Input v-model="formItem.baseurl"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">首页</span>
          <Input v-model="formItem.homePage"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">项目页</span>
          <Input v-model="formItem.projectPage"></Input>
        </FormItem>
        <FormItem>
          <span class="form-item-label">单点接口</span>
            <Input v-model="formItem.spInterface"></Input>
        </FormItem>
        <FormItem>
          <span class="swith-label">iframe</span>
          <i-switch v-model="formItem.iframe"/>
        </FormItem>
        <FormItem>
          <span class="swith-label">激活</span>
          <i-switch v-model="formItem.activate"/>
        </FormItem>
        <FormItem>
          <span class="form-item-label">描述</span>
          <Input v-model="formItem.description" type="textarea" :autosize="{minRows: 6,maxRows: 10}" placeholder="请输入描述"></Input>
        </FormItem>
         <FormItem>
          <span class="form-item-label">英文描述</span>
          <Input v-model="formItem.engDescription" type="textarea" :autosize="{minRows: 6,maxRows: 18}" placeholder="请输入英文描述"></Input>
        </FormItem>
        <FormItem>
            <Button @click="handleCancel">取消</Button>
            <Button type="primary" style="margin-left: 8px" @click="handleSave(formItem)">保存</Button>
        </FormItem>
      </Form>
    </div>
  </div>
</template>

<script>
const tempFormItem = {
  catagory: 'dataAccess',
  name: '',
  engName: '',
  baseurl: '',
  homePage: '',
  projectPage: '',
  spInterface: '',
  iframe: true,
  activate: true,
  description: '',
  engDescription: '',
}
export default {
  props: {
    componentData: {
      type: Object,
      default: () => tempFormItem,
    }
  },
  data() {
    return {
      formItem: this.componentData,
    }
  },
  watch: {
    // 通过watch使得 父传子 的数据响应化
    componentData(newValue) {
      this.formItem = newValue
    }
  },
  methods: {
    handleCancel() {

    },
    handleSave(componentItem) {
      // 提交前验证

      // 提交
      this.$emit('on-save', componentItem)
    },
  }
}
</script>

<style lang="scss" scoped>
@import '@/common/style/variables.scss';
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
    color: #515a6e;
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
    color: #515a6e;
  }
}

</style>
