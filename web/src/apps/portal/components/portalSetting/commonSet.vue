<template>
  <div class="common-set">
    <div class="set-title">门户设置</div>
    <div class="set-content">
      <Row>
        <i-col span="12">
          <Form ref="addCommonForm" :model="formValue" :rules="addPortalRule" :label-width="100">
            <Form-item prop="name" label="门户标题">
              <Input v-model="formValue.name" placeholder="请输入名称" :maxlength="20"/>
            </Form-item>
            <Form-item prop="logo" label="门户logo">
              <uploadPicture :url="formValue.logo" @uploadlogo="uploadlogo"></uploadPicture>
            </Form-item>
            <Form-item prop="color" label="主题颜色">
              <RadioGroup v-model="formValue.color">
                <Radio label="#FFFFFF">浅色</Radio>
                <Radio label="#000000">深色</Radio>
              </RadioGroup>
            </Form-item>
            <Form-item prop="shortcut" label="门户快捷方式" class="linkWrap">
              <Row>
                <i-col span="10">
                  <div class="link">
                    <a :href="link">{{domain}}/{{link}}</a>
                  </div>
                </i-col>
                <i-col span="14">
                  <Input
                    class="linkInput"
                    v-model="formValue.shortcut"
                    placeholder="按enter健，生成快捷访问链接"
                    @on-enter="generateLink"
                  />
                </i-col>
              </Row>
            </Form-item>
            <Form-item prop="footer" label="页脚">
              <Input v-model="formValue.footer" placeholder="请输入页脚" disabled />
            </Form-item>
            <!-- <Form-item prop="watermarkShown" label="展示水印">
              <RadioGroup v-model="formValue.watermarkShown">
                <Radio label="是" />
                <Radio label="否" />
              </RadioGroup>
            </Form-item>
            <Form-item prop="menuCached" label="缓存菜单">
              <RadioGroup v-model="formValue.menuCached">
                <Radio label="是" />
                <Radio label="否" />
              </RadioGroup>
            </Form-item> -->
          </Form>
          <div class="buttonWrap">
            <Button type="primary" @click="saveCommonSet" :loading="loading" :disabled="disablePortalSave">
              保存
            </Button>
          </div>
        </i-col>
      </Row>
    </div>
  </div>
</template>
<script>
import bus from '../bus';
import api from '@/common/service/api';
import uploadPicture from '../components/uploadPictures/index.vue';
export default {
  components: {
    uploadPicture
  },
  data() {
    return {
      formValue: {
        name: '',
        logo: '',
        color: '#000000',
        footer: '@版权所有，违者必究',
        shortcut: '',
        watermarkShown: '否',
        menuCached: '否',
      },
      link: '',
      disablePortalSave: false,
      loading: false,
      domain: '',
      addPortalRule: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度不超过20个字符', trigger: 'change' },
          { type: 'string', pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/, message: '仅允许输入中文、英文和数字、下划线', trigger: 'change' }
        ],
        shortcut: [
          { required: true, message: '请输入自定义快捷访问方式', trigger: 'blur' },
          // { type: 'string', pattern: /^[a-zA-Z\u4e00-\u9fa5][a-zA-Z0-9_\u4e00-\u9fa5]*$/, message: '必须以字母或汉字开头，且只支持字母、数字、下划线和中文！', trigger: 'blur'}
        ],
      },
    }
  },
  created() {
    this.dataPortalId = this.$route.query.portalId;
    this.domain = window.location.host;
    //监听数据门户设置成功后,loading隐藏
    bus.$on('setPortalSuccess', () => {
      this.loading = false;
    }),
    //监听点击了左侧的门户设置按钮
    bus.$on('init', (type) => {
      this.initMenu(type)
    })
    this.getPortalList();
  },
  methods: {
    saveCommonSet() {
      this.$refs.addCommonForm.validate(valid => {
        if (valid) {
          this.link = this.formValue.shortcut;
          this.loading = true;
          bus.$emit('setPoralBasic', this.formValue);
        }
      })
    },
    initMenu(type) {
      if (type === 'common') {
        this.$Modal.confirm({
          title: '门户设置',
          content: '<p>是否修改该门户设置</p>',
          onOk: () => {
            this.initFormValue();
          },
          onCancel: () => {}
        })
      }
    },
    initFormValue() {
      this.disablePortalSave = false;
      this.loading = false;
      this.formValue =  {
        name: '',
        logo: '',
        color: '#000000',
        footer: '@版权所有，违者必究',
        shortcut: '',
        watermarkShown: '否',
        menuCached: '否',
      }
    },
    getPortalList() {
      let url = '/dataportal/configuration';
      api.fetch(url, {dataPortalId: this.dataPortalId}, 'get').then((res) => {
        let Configuration = res.Configuration;
        if (JSON.stringify(Configuration) === '{}' || Configuration === null) return;
        this.formValue =  {
          name: Configuration.name,
          logo: Configuration.logo,
          color: Configuration.color,
          footer: Configuration.footer,
          shortcut: Configuration.shortcut,
          watermarkShown: Configuration.shortcut ? '是' : '否',
          menuCached: Configuration.menuCached ? '是' : '否',
        }
      })
    },
    generateLink() {
      this.link = this.formValue.shortcut;
    },
    uploadlogo(data) {
      this.formValue.logo = data[0] ? data[0].url : '';
    }
  }
}
</script>
<style scoped lang="scss">
.common-set {
  .set-title {
    font-size: 16px;
    font-weight: bold;
  }
  .set-content {
    margin-top: 20px;
    min-width: 500px;
  }
  .buttonWrap {
    text-align: center;
  }
  .flex {
    display: flex;
  }
}
</style>
