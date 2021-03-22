<template>
  <div class="add-menu">
    <div class="set-title">修改目录内容</div>
    <div class="set-content">
      <Row>
        <i-col span="12">
          <Form ref="addMenuForm" :model="formValue" :rules="addMenuRule" :label-width="120">
            <Form-item prop="title" label="菜单名称">
              <Input v-model="formValue.title" placeholder="请输入名称" :maxlength="20"/>
            </Form-item>
            <!-- <Form-item prop="logo" label="菜单Icon">
              无
            </Form-item>-->
            <Form-item prop="order" label="顺序">
              <Input v-model="formValue.order" placeholder="菜单顺序"></Input>
            </Form-item>

            <Form-item prop="accessUser" label="可查看用户">
              <Select v-model="formValue.accessUser" multiple filterable>
                <Option v-for="(item, index) in accessUerList" :key="index" :value="item">{{item}}</Option>
              </Select>
            </Form-item>

            <Form-item prop="flod" label="是否折叠">
              <RadioGroup v-model="formValue.flod">
                <Radio label="0">否</Radio>
                <Radio label="1">是</Radio>
              </RadioGroup>
            </Form-item>
          </Form>
          <div class="buttonWrap">
            <Button :disabled="canSave" type="primary" @click="saveMenuSet">保存</Button>
          </div>
        </i-col>
      </Row>
    </div>
  </div>
</template>
<script>
import bus from '../bus';
import menuMixin from '@/apps/portal/components/mixin.js';
export default {
  mixins: [menuMixin],
  data() {
    return {
      formValue: {
        title: '',
        order: '',
        accessUser: [],
        flod: '',
      },
      accessUerList: [],
      canSave: false,
      addMenuRule: {
        title: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度不超过20个字符', trigger: 'change' },
          { type: 'string', pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/, message: '仅允许输入中文、英文和数字、下划线', trigger: 'change' }
        ],
        order: [
          { pattern: /^[1-9]\d{0,2}$/,  message: '仅允许输入1-999之间的正整数', trigger: 'change' },
        ]
      },
      portalId: this.$route.query.portalId,
    }
  },
  props: {
    currentMenuId: Number,
    nodeData: {
      type: Object,
      default: () => ({})
    },
    flag: Number,
    saveDir: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    currentMenuId() {
      this.init();
    },
    saveDir(val) {
      if (val) {
        this.initFormValue()
      }
    }
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      let params = {
        portalId: this.portalId,
        menuId: this.currentMenuId || 0,
        menuType: this.nodeData.menuType,
        directoryType: 'sub',
        flag: this.flag
      };
      this.getAccessUserList(params, (res) => {
        this.accessUerList = res.accessUserList;
      });
      this.getPortalList(this.currentMenuId, (res) => {
        let result = res.menu;
        this.formValue = {
          title: result.name,
          order: result.ranking,
          accessUser: result.accessUser,
          flod: result.preContent.isHidden ? '1' : '0'
        };
      });
    },
    initFormValue() {
      this.formValue = {
        title: '',
        order: '',
        accessUser: [],
        flod: '',
      }
    },
    saveMenuSet() {
      this.$refs.addMenuForm.validate(valid => {
        if (valid) {
          bus.$emit('saveDirecoty', this.formValue)
        }
      })
    },
  }
}
</script>
<style scoped lang="scss">
.add-menu {
  .set-title {
    font-size: 16px;
    font-weight: bold;
  }
  .buttonWrap {
    text-align: center;
  }
}
.set-content {
  margin-top: 20px;
  min-width: 500px;
}
</style>

