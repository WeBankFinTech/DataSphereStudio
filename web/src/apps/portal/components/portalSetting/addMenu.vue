<template>
  <div class="add-menu">
    <div class="set-title">{{title}}</div>
    <div class="set-content">
      <Row>
        <i-col span="12">
          <Form ref="addMenuForm" :model="formValue" :rules="addMenuRule" :label-width="130" style="min-width: 500px">
            <Form-item prop="title" label="菜单名称">
              <Input v-model="formValue.title" placeholder="请输入名称" :maxlength="20"/>
            </Form-item>
            <!-- <Form-item prop="logo" label="菜单Icon">
              无
            </Form-item>-->
            <Form-item prop="order" label="顺序">
              <Input v-model="formValue.order" placeholder="菜单顺序"></Input>
            </Form-item>

            <!-- 在新建菜单的时候 -->
            <Form-item prop="menuType" label="菜单类型">
              <RadioGroup v-model="formValue.menuType" @on-change="changeMenutype(formValue.menuType)">
                <Radio label="directory">目录</Radio>
                <Radio label="node">节点</Radio>
              </RadioGroup>
            </Form-item>
            <template v-if="formValue.menuType === 'directory' && this.isShowSubDirectory">
              <Form-item prop="directoryType" label="目录类型">
                <RadioGroup v-model="formValue.directoryType">
                  <Radio label="same">目录</Radio>
                  <Radio label="sub">子目录</Radio>
                </RadioGroup>
              </Form-item>
              <Form-item prop="flod" label="是否折叠">
                <RadioGroup v-model="formValue.flod">
                  <Radio label="0">否</Radio>
                  <Radio label="1">是</Radio>
                </RadioGroup>
              </Form-item>
            </template>
            <Form-item prop="accessUser" label="可查看用户">
              <Select
                v-model="formValue.accessUser"
                multiple
                filterable
                @click.native="getAccessUserList"
              >
                <Option v-for="(item, index) in accessUerList" :key="index" :value="item">{{item}}</Option>
              </Select>
            </Form-item>
            <template v-if="formValue.menuType === 'node'">
              <Form-item prop="content" label="菜单内容">
                <Select @on-change="changeContent" v-model="formValue.content">
                  <Option
                    v-for="(item, index) in contentList"
                    :key="index"
                    :value="item.nameEn"
                  >{{item.name}}</Option>
                </Select>
              </Form-item>
            </template>
          </Form>
          <Form v-if="formValue.menuType === 'node'" ref="formDynamic" :model="formDynamic" :label-width="130">
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
import bus from '../bus'
export default {
  data() {
    return {
      formValue: {
        menuName: '',
        menuIcon: '',
        order: '',
        accessUser: [],
        menuType: 'directory',
        menuContent: '',
        project: '',
        component: '',
        content: '',
        openType: '',
        flod: '1',
        componentId: '',
        projectId: '',
        directoryType: '',
        workspaceId: 104,
      },
      title: '添加菜单',//目录的类型
      canSave: false,
      isShowSubDirectory: true,
      formDynamic: {},
      categoryNodeId: '',
      componentList: [],
      accessUerList: [],
      contentList: [],
      portalId: '',
      addMenuRule: {
        title: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度不超过20个字符', trigger: 'change' },
          { type: 'string', pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/, message: '仅允许输入中文、英文和数字、下划线', trigger: 'change' }
        ],
        menuType: [
          { required: true, message: '请输选择菜单类型', trigger: 'blur' },
        ],
        directoryType: [
          { required: true, message: '请选择目录类型', trigger: 'blur' },
        ],
        content: [
          { required: true, message: '请选择菜单内容', trigger: 'change' },
        ],
        order: [
          { type: 'string', pattern: /^[1-9]\d{0,2}$/,  message: '仅允许输入1-999之间的正整数', trigger: 'change' },
        ]
        // component: [
        //   {required: true, message: '请选择', trigger: 'change'},
        // ]
        // link: [
        //   {required: true, message: '请输入可编辑用户', trigger: 'blur'},
        //   { type: 'string', pattern: /^[a-zA-Z\u4e00-\u9fa5][a-zA-Z0-9_\u4e00-\u9fa5]*$/, message: '必须以字母或汉字开头，且只支持字母、数字、下划线和中文！', trigger: 'blur'}
        // ],
      },
    }
  },
  props: {
    flag: Number,
    currentMenuId: Number,
    nodeData: Object,
    parentId: Number,
    newMenu: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    flag() {
      this.isShowSubDirectory = true;
      this.initMenuForm();
    },
    parentId(val) {
      //flag是1修改,flag2是新建
      if (val === 0) {
        this.isShowSubDirectory = true;
      } else {
        this.isShowSubDirectory = false;
      }
      this.initMenuForm();
    },
    newMenu(val) {
      if (val) {
        this.initMenuForm();
      }
    }
  },
  created() {
    if (this.parentId === 0) {
      this.isShowSubDirectory = true;
    } else {
      this.isShowSubDirectory = false;
    }
    //监听点击添加节点，初始化表单的内容
    bus.$on('initMenuForm', (data) => {
      this.initMenuForm(data)
    })
    //监听点击新建门户的操作
    bus.$on('init', () => {
      this.canSave = false;
      this.initMenuForm();
    })
  },
  mounted() {
    this.init();
    this.initMenuForm();
    bus.$on('editMenuContent', (data) => {
      //编辑某个节点或者目录，请求该节点和目录的内容，屏蔽目录类型和菜单类型
      if (data.menuType === 'directory') {
        this.title = '修改目录内容';
      } else {
        this.title = '修改节点内容';
      }
      let url = '/dataportal/menu';
      api.fetch(url, { menuId: data.id }, 'get').then(() => {
      })
    })
  },
  methods: {
    init() {
      this.getContentList();
      this.portalId = this.$route.query.portalId;
    },
    initMenuForm() {
      for (let key in this.formValue) {
        if (key === 'menuType') {
          this.formValue[key] = 'category';
        } else if (key === 'flod') {
          this.formValue[key] = '1'
        } else {
          this.formValue[key] = '';
        }
      }
      this.title = '添加新菜单';
      this.canSave = false;
      this.formDynamic = {}
    },
    getAccessUserList() {
      //创建的时候menuType
      let url = '/dataportal/menu/user';
      let params = {
        portalId: this.portalId,
        menuId: this.currentMenuId || 0,
        flag: this.flag,
        menuType: this.formValue.menuType,
        directoryType: this.formValue.directoryType
      };
      api.fetch(url, params, 'get').then(res => {
        this.accessUerList = res.accessUserList;
      })
    },
    handleComponentList(index) {
      if (index === 0 ) {
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
    changeMenutype(value) {
      if (value === 'node') {
        this.componentList.forEach(it => {
          it.value = '';
        })
        this.formValue.content = '';
      }
    },
    getComponentList() {
      //todo 获取相应的组件
      let url = '/dataportal/menu/systemProjects';
      let params = {
        system: this.formValue.content,
        workspaceId: this.workspaceId
      };
      api.fetch(url, params, 'get').then(res => {
        this.componentList = res.uiList;
        //将下拉列表统一处理成[{id: , name: ''}]
        this.componentList.forEach((item) => {
          //处理组件下面最后一个下拉列表后需要选择前面两个获得，为null的情况
          if (item.returnList === null) {
            let obj = {
              name: '',
              id: '',
            };
            item.list = [obj];
          } else {
            let list = [];
            item.returnList.forEach((it, index) => {
              if (typeof (it) === 'string') {//下拉列表中元素不是对象的情况
                let obj = {
                  name: it,
                  id: index,
                };
                list.push(obj)
              } else {
                list.push(it)
              }
            })
            item.list = list;
          }
        })
        this.componentList.forEach((item, index) => {
          item.index = index + 1
        })
        this.formDynamic = Object.assign({}, {items: this.componentList});
      })
    },
    //保存设置的菜单
    saveMenuSet() {
      this.$refs.addMenuForm.validate(valid => {
        if (valid) {
          //点击保存后不允许再保存，判断是否选中了组件工程等
          // 检验下面表单
          if (this.formDynamic.items === undefined) {
            bus.$emit('setLeftMenu', this.formValue, this.componentList);
            return
          }
          this.$refs.formDynamic.validate(v => {
            if (v) {
              bus.$emit('setLeftMenu', this.formValue, this.componentList);
            }
          })
        }
      })
    },
    getContentList() {
      //todo 获取相应的菜单内容
      let url = '/dataportal/menuContent';
      api.fetch(url, 'get').then(res => {
        this.contentList = res.menuContent;
      })
    },
    changeContent() {
      this.getComponentList();

    },

  },
  beforeDestroy() {
    bus.$off('initMenuForm');
    bus.$off('init')
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
  .icon {
  color: red;
  font-size: 16px;
  position: absolute;
  left: 33px;
  top: 235px;
}
</style>
