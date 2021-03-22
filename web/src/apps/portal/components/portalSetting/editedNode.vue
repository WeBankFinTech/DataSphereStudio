<template>
  <div class="add-menu">
    <div class="set-title">修改节点</div>
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

            <template>
              <Form-item prop="content" label="菜单内容">
                <Select @on-change="changeContent" v-model="formValue.content">
                  <Option
                    v-for="(item, index) in contentList"
                    :key="index"
                    :value="item.nameEn"
                  >{{item.name}}</Option>
                </Select>
              </Form-item>
              <!-- 根据请求来的菜单内容进行循环渲染 -->
            </template>
          </Form>
          <Form ref="formDynamic" :model="formDynamic" :label-width="130">
            <template v-for="(item, index) in formDynamic.items">
              <FormItem
                v-if="item.type === 'input'"
                :key="index"
                :label="item.labelName"
                :prop="'items.' + index + '.value'"
                :rules="{required: true, message: item.labelName, trigger: 'blur'}">
                <Input type="text" v-model="item.value" :placeholder="item.labelName" v-if="item.type === 'input'"/>
              </FormItem>
              <FormItem
                v-if="item.type === 'select'"
                :key="index"
                :label="item.labelName"
                :prop="'items.' + index + '.value'"
                :rules="{required: true, message: item.labelName, trigger: 'blur', type: 'number'}">
                <Select
                  v-model="item.value"
                  :placeholder="item.labelName"
                  clearable
                  @click.native="handleComponentList(index, item)">
                  <Option v-for="(it, i) in item.list" :key="i" :value="it.id">{{it.name}}</Option>
                </Select>
              </FormItem>
            </template>
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
import api from "@/common/service/api";
import menuMixin from '@/apps/portal/components/mixin.js';
import bus from '../bus'
export default {
  mixins: [menuMixin],
  data() {
    return {
      formValue: {
        title: '',
        menuIcon: '',
        order: '',
        accessUser: [],
        content: '',
      },
      canSave: false,
      portalId: '',
      accessUerList: [],
      componentList: [],
      contentList: [],
      formDynamic: {},
      addMenuRule: {
        title: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度不超过20个字符', trigger: 'change' },
          { type: 'string', pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/, message: '仅允许输入中文、英文和数字、下划线', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入名称', trigger: 'change' },
        ],
        order: [
          { pattern: /^[1-9]\d{0,2}$/,  message: '仅允许输入1-999之间的正整数', trigger: 'change' },
        ]
      },
    }
  },
  props: {
    currentMenuId: Number,
    flag: Number,
    saveNode: {
      type: Boolean,
      default: false,
    }
  },
  created() {
    this.init();
    this.portalId = this.$route.query.portalId;
  },
  watch: {
    currentMenuId() {
      this.getNodeContent();
      this.getAccessUserList();
    },
    saveNode(val) {
      if (val) {
        this.initValue();
      }
    }
  },
  methods: {
    init() {
      this.getNodeContent();
      this.getContentList();
    },
    initValue() {
      this.formValue =  {
        title: '',
        menuIcon: '',
        order: '',
        accessUser: [],
        content: '',
      };
      this.contentList = [];
      this.formDynamic = {};
      this.$refs.addMenuForm.resetFields();
    },
    async getAccessUserList() {
      let url = '/dataportal/menu/user';
      let params = {
        portalId: this.portalId,
        menuId: this.currentMenuId || 0,
        menuType: 'node',
        flag: this.flag
      };
      let res = await api.fetch(url, params, 'get');
      this.accessUerList = res.accessUserList;
    },
    saveMenuSet() {
      this.$refs.addMenuForm.validate(valid => {
        if (valid) {
          //点击保存后不允许再保存，判断是否选中了组件工程等
          // 检验下面表单
          this.$refs.formDynamic.validate(v => {
            if (v) {
              bus.$emit('saveEidtNode', this.formValue, this.componentList);
            }
          })
        }
      })
    },
    async getNodeContent() {
      let url = '/dataportal/menu';
      let res = await api.fetch(url, { menuId: this.currentMenuId }, 'get');
      let result = res.menu;
      this.formValue.title = result.name;
      this.formValue.order = result.ranking;
      this.formValue.content = result.preContent.application;
      this.formValue.accessUser = result.accessUser;
      this.getAccessUserList();
      if (this.formValue.content === '') return
      this.getComponentList(this.formValue.content, () => {
        this.componentList.forEach(it => {
          for (let i in result.preContent) {
            if (it.key === i) {
              //判断是object还是string
              if (typeof (result.preContent[i]) === 'string') {
                if (result.preContent[i] === 'Display') {
                  it.value = 0
                } else if (result.preContent[i] === 'Dashboard') {
                  it.value = 1
                } else if (i === 'urllink/link') {
                  it.value = result.preContent[i];
                }
                else {
                  it.value = 2;
                }
              } else {
                it.value = result.preContent[i].id;
                if (i === 'easyide/category') {
                  this.categoryNodeId = result.preContent[i].id;
                }
              }
            }
          }
        })
        this.componentList.forEach((item, index) => {
          item.index = index + 1
        })
        this.formDynamic = Object.assign({}, {items: this.componentList});
        this.handleComponentList(2)
      })
    },
    changeContent() {
      if (this.formValue.content === '' || this.formValue.content === undefined) return
      this.getComponentList(this.formValue.content,  () => {
        this.formDynamic = Object.assign({}, {items: this.componentList});
      });
    },
    handleComponentList(index) {
      if (index === 0) {
        this.componentList.forEach(it => {
          it.value = '';
        })
      }
      if (index === 1) {
        this.componentList.forEach((item, index) => {
          if (index === 2) item.value = '';
        })
      }
      switch (this.formValue.content) {
        case 'Visualis': this.handleVisualisContent(index);
          break;
        case 'easyIDE': this.handEasyIDEContent(index);
          break;
      }
    },
    handleVisualisContent(index) {
      //判断点击的是请选择组件
      if (index === 2) {
        //拿到组件和工程value
        this.componentList.forEach((item) => {
          if (item.key === 'visualis/component') {
            this.componentId = item.returnList[item.value];
          } else if (item.key === 'visualis/project') {
            this.projectId = item.value
          }
        })
        //判断是否选择了请选择工程和请选择组再次请求请选择内容
        let emptyProject = this.componentList[0].value === '' || this.componentList[0].value === null;
        let emptyComponet = this.componentList[1].value === '' || this.componentList[1].value === null;
        if (!(emptyProject || emptyComponet)) {
          this.getVisualisContent();
        }
      }
    },
    handEasyIDEContent(index) {
      //判断点击的是选择报表
      if (index === 1) {
        this.componentList.forEach((item) => {
          if (item.key === 'easyide/category') {
            this.categoryNodeId = item.value;
          }
        })
      }
      //判断点是选择了请选择业务再去请求相关的报表
      let emptyBusiness = this.componentList[0].value === '' || this.componentList[0].value === null;
      if (!emptyBusiness) {
        this.getEasyIDEContent();
      }
    },
    getVisualisContent() {
      let url = '/dataportal/menu/visualis/content';
      let params = {
        projectId: this.projectId,
        component: this.componentId
      };
      api.fetch(url, params, 'get').then(res => {
        if (!res.contentList.length) {
          res.contentList = [
            { id: '', name: '' }
          ];
        }
        this.$set(this.componentList[2], 'list', res.contentList);
        this.$forceUpdate()
      })
    },

    getEasyIDEContent() {
      let url = '/dataportal/menu/easyide/chart';
      let params = {
        categoryNodeId: this.categoryNodeId
      };
      api.fetch(url, params, 'get').then(res => {
        if (!res.returnList.length) {
          res.returnList = [
            { id: '', name: '' }
          ]
        }
        this.$set(this.componentList[1], 'list', res.returnList);
        this.$forceUpdate()
      })
    },
  },
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
