<template>
  <div class="page">
    <div class="page-header">
      <div class="header-info">
        <h1>欢迎来到数据门户开发中心！</h1>
        <div class="search-wrap">
          <Input
            search
            v-model="portalName"
            class="search-input"
            placeholder="数据门户名称"
            @on-search="searchPortal"
            @on-enter="searchPortal"
          />
          <Button type="primary" class="add-button" icon="md-add" @click="addPortal">新建数据门户</Button>
        </div>
      </div>
    </div>
    <div class="page-content">
      <card dis-hover class="card-wrap" ref="card">
        <div class="card-title">
          <ul class="menu">
            <li
              class="menu-item"
              :class="{'menuActive': currentMenu === item}"
              v-for="(item, index) in menulist"
              :key="index"
              @click="changeMenu(item)"
            >{{ item}}</li>
          </ul>
          <Dropdown class="drop-bar" @on-click="chooseShowType">
            <span class="show-type">展示方式</span>
            <Dropdown-menu slot="list">
              <Dropdown-item
                v-for="(item, index) in itemList"
                :key="index"
                :name="item.key"
              >{{ item.text }}</Dropdown-item>
            </Dropdown-menu>
          </Dropdown>
        </div>
        <div class="card-content">
          <tableList
            v-show="type === 'table'"
            :tableData="portalList"
            @handleOperate="handleOperate"
          />
          <Row v-show="type === 'graph'">
            <i-col
              :xs="8"
              :xxl="6"
              v-for="(item, index) in portalList"
              :key="index"
              :cardValue="item"
            >
              <CardItem :cardValue="item"
                :notEditable="notEditable(item)" @handleOperate="handleOperate" :workspaceId="workspaceId"/>
            </i-col>
          </Row>
        </div>
        <div class="card-footer"></div>
      </card>
      <Modal v-model="addOrEditlModal" :title="modalTitle" :mask-closable="false">
        <Form ref="addForm" :model="formValue" :rules="addPortalRule" :label-width="100">
          <Form-item prop="name" label="名称">
            <Input v-model="formValue.name" placeholder="请输入名称" :maxlength="20"/>
          </Form-item>
          <span class="icon">*</span>
          <Form-item prop="tag" label="标签" class="tag-input">
            <Input
              v-model="formValue.tag"
              placeholder="标签，按enter键"
              @on-enter="enterTag"
              v-if="showInput"
            />
            <Icon v-else type="md-create" :size="20" title=" 添加标签" @click="showTagInput"></Icon>
          </Form-item>
          <div class="input-value-show" v-if="tagList.length">
            <div class="value-item" v-for="(item, index) in tagList" :key="index">
              {{item}}
              <Icon type="ios-close" :size="20" @click="deletetTag(index)" class="delTag"></Icon>
            </div>
          </div>
          <Form-item prop="editUser" label="可编辑用户">
            <Select v-model="formValue.editUser" multiple :disabled="canEditUser">
              <Option
                v-for="(item, index) in userList"
                :key="index"
                :value="item.name"
              >{{item.name}}</Option>
            </Select>
          </Form-item>
          <Form-item prop="accessUser" label="可查看用户">
            <Select
              v-model="formValue.accessUser"
              multiple
              @click.native="getAccessUserList"
              :disabled="canEditUser"
            >
              <Option
                v-for="(item, index) in accessuserList"
                :key="index"
                :value="item.name"
                :disabled="item.disable"
              >{{item.name}}</Option>
            </Select>
          </Form-item>
          <Form-item prop="description" label="描述">
            <Input
              v-model="formValue.description"
              placeholder="请输入描述"
              type="textarea"
              :autosize="{minRows: 2,maxRows: 5}"
            />
          </Form-item>
        </Form>
        <div slot="footer">
          <Button @click="cancel">取消</Button>
          <Button type="primary" @click="confirmAdd">确定</Button>
        </div>
      </Modal>
    </div>

    <Spin fix size="large" v-if="showLoading">
      <Icon type="ios-loading" size="40" class="demo-spin-icon-load"></Icon>
    </Spin>
  </div>
</template>
<script>
import api from "@/common/service/api";
import CardItem from '../../components/portalHome/cardItem';
import TableList from '../../components/portalHome/tableList';
import storage from '@/common/helper/storage';
import { GetWorkspaceUserManagement } from '@/common/service/apiCommonMethod.js';
export default {
  components: {
    CardItem,
    TableList,
  },
  data() {
    return {
      addOrEditlModal: false,
      showInput: true,
      portalName: '',
      currentMenu: '',
      modalType: '',
      showLoading: true,
      tagList: [],//新增和添加数据门户标签列表
      spanNum: 6,
      formValue: {
        name: '',
        tag: '',
        editUser: [],
        accessUser: [],
        description: '',
      },
      canEditUser: false,
      accessuserList: [],
      userList: [],
      editPortalObj: {},//编辑的数据门户对象
      portalList: [],
      menulist: [],
      workspaceId: '',
      itemList: [
        {
          key: 'table',
          text: '列表展示'
        },
        {
          key: 'graph',
          text: '图标展示'
        }
      ],
      type: 'graph', //展示的类型：列表或者图标
      addPortalRule: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度不超过20个字符', trigger: 'change' },
          { type: 'string', pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/, message: '仅允许输入中文、英文和数字、下划线、下划线', trigger: 'change' }
        ],
        description: [
          // {required: true, message: '请输入描述', trigger: 'blur'},
          { min: 1, max: 256, message: '不超过256个字符', trigger: 'blur' }
        ],
        tag: [
          { min: 1, max: 20, message: '长度不超过32个字符', trigger: 'change' },
          { type: 'string', pattern: /^[\u4E00-\u9FA5A-Za-z0-9]+$/, message: '仅允许输入中文、英文和数字', trigger: 'change' }
        ]
      },
    }
  },
  computed: {
    modalTitle() {
      return this.modalType === 'add' ? '新增数据门户' : '编辑数据门户'
    },
  },
  watch: {
    //监听当用户清空了数据门户名称的搜索为空的时候再次请求数据
    portalName(val) {
      if (val === '') {
        this.init()
      }
    }
  },
  created() {
    this.getWorkSpaceId();
    this.init();
  },
  methods: {
    // 特定demo数据不让修改
    notEditable(data) {
      // 只有强哥的账号下的243数据门户id不能编辑
      return this.getUserName() === 'enjoyyin' && data.id === 243
    },
    init() {
      //获取标签列表后请求相应的数据门户列表
      this.getMenuList().then(res => {
        this.menulist = res.tagList;
        this.currentMenu = this.menulist[0];
        this.getPortalList({ tag: this.currentMenu }).then();
      })
      this.getUserList();
    },
    getWorkSpaceId() {
      let id = this.$route.query.workspaceId;
      if ( id !== undefined) {
        storage.set('workSpaceId', id);
        this.workspaceId = id;
      } else {
        this.workspaceId = storage.get('workSpaceId')
      }
    },
    getAccessUserList() {
      this.accessuserList = JSON.parse(JSON.stringify(this.userList));
      this.accessuserList.forEach(it => {
        this.formValue.editUser.forEach(item => {
          if (it.name === item) it.disable = true;
        })
      })
    },
    getCurrentTagportal(tag) {
      this.getMenuList().then(res => {
        this.menulist = res.tagList;
        this.currentMenu = tag;
        this.getPortalList({ tag: this.currentMenu }).then();
      })
    },
    getUserName() {
      return storage.get('userInfo', 'session');
    },
    chooseShowType(value) {
      this.type = value;
    },
    changeMenu(value) {
      this.showLoading = true
      this.currentMenu = value;
      //渲染不同标签对应下的数据门户
      let tag = this.currentMenu;
      this.getPortalList({ tag }).then();
    },
    handleOperate(operateObj) {
      switch (operateObj.operateType) {
        case 'deliver':
          this.deliverPortal(operateObj.value);
          break;
        case 'set':
          this.setPortal(operateObj.value);
          break;
        case 'delete':
          this.deletePortal(operateObj.value);
          break;
        default:
          break;
      }
    },

    getPortalList(params) {
      //根据数据门户名称和标签获取数据门户列表
      return new Promise((resolve, reject) => {
        api.fetch('/dataportal/search', params, 'get').then(res => {
          this.portalList = res.portalList;
          resolve(res)
        }).catch(err => {
          reject(err)
        }).finally(() => {
          this.showLoading = false;
        });
      })
    },
    //获取数据门户标签
    getMenuList() {
      return new Promise((resolve, rejcet) => {
        api.fetch('/dataportal/tagList', 'get').then(res => {
          resolve(res)
        }).catch(err => {
          rejcet(err)
        }).finally(() => {
          this.showLoading = false
        })
      })
    },
    //todo 得到用户的接口需要改（需要绑定工作空间）
    getUserList() {
      let params = {
        workspaceId: this.workspaceId
      };

      GetWorkspaceUserManagement(params).then(res => {
        this.userList = res.workspaceUsers;
        this.userList = this.userList.filter((item) => item.name !== this.getUserName());
      })
    },
    copy(value) {
      const testArea = document.createElement('textarea');
      testArea.value = value;
      document.body.appendChild(testArea);
      testArea.select();
      document.execCommand('copy');
      document.body.removeChild(testArea);
    },
    //转发操作
    deliverPortal(obj) {
      let url = '/dataportal/share';
      //api默认请求是post
      api.fetch(url, { portalId: obj.id }, 'get').then(res => {
        this.copy(res.QuickAccessAddress)
      })
    },
    //编辑数据门户操作(判断是否有权限编辑)
    setPortal(cardItemValue) {
      let userName = this.getUserName();
      let editUser = cardItemValue.editUser;
      let creator = cardItemValue.creator;
      let flag = editUser.indexOf(creator);
      if (flag === -1) editUser.push(creator);
      let index = editUser.indexOf(userName);
      if (index === -1) {
        this.$Message.warning('没有权限编辑该数据门户');
        return;
      }
      //同时可编辑的用户有查看的权限，创建者有修改可查看和可编辑用户的权限
      if (creator === userName) {
        this.canEditUser = false;
      } else {
        this.canEditUser = true;
      }
      this.getAccessUserList();
      this.addOrEditlModal = true;
      this.editPortalObj = JSON.parse(JSON.stringify(cardItemValue));
      this.formValue.name = this.editPortalObj.name;
      this.formValue.editUser = this.editPortalObj.editUser;
      this.formValue.accessUser = this.editPortalObj.accessUser;
      this.formValue.description = this.editPortalObj.description;
      this.tagList = this.editPortalObj.tag.slice(0);
      this.showInput = false;
      this.modalType = 'edit';
    },
    //删除门户操作
    deletePortal(cardItemValue) {
      let creator = cardItemValue.creator;
      let userName = this.getUserName();
      if (creator !== userName) {
        this.$Message.warning('只有数据门户的创建者有删除的权限');
        return;
      }
      if (this.formValue)
        this.$Modal.confirm({
          title: '删除',
          content: `<p>是否删除${cardItemValue.name}</p>`,
          onOk: () => {
            let portalId = cardItemValue.id;
            //删除数据门户的操作
            let url = `/dataportal/portal/${portalId}`;
            api.fetch(url, 'delete').then(() => {
              this.$Message.success('删除数据门户成功');
              this.init();
            })
          },
        })
    },
    confirmAdd() {
      this.$refs.addForm.validate(valid => {
        if (valid) {
          if (!this.tagList.length && this.formValue.tag === '') {
            this.$Message.error('请输入数据门户的标签');
            return;
          }
          //保存修改后的数据门户
          let obj = JSON.parse(JSON.stringify(this.formValue));
          //对可查看用户进行去重（去掉可编辑的用户）
          this.formValue.editUser.forEach(it => {
            this.formValue.accessUser.forEach(item => {
              if (it !== item) return item;
            })
          })
          //当用户没有enter的时候
          if (this.tagList.length === 0) {
            obj.tag = this.formValue.tag.split(',')
          } else {
            obj.tag = this.tagList;
          }
          if (this.modalType === 'add') {
            //添加数据门户的操作
            this.addOrDelPortal(obj, 'post', () => {
              this.$Message.success('添加数据门户成功');
              this.addOrEditlModal = false;
              this.init();
            });
          } else {
            //编辑数据门户操作
            let params = {
              id: this.editPortalObj.id,
            };
            this.addOrDelPortal({ ...obj, ...params }, 'put', () => {
              this.$Message.success('编辑数据门户成功');
              this.addOrEditlModal = false;
              this.getCurrentTagportal(obj.tag[0]);
            });
          }
        } else {
          this.addOrEditlModal = true;
        }
      })
    },
    //对数据门户进行添加和删除,修改
    addOrDelPortal(params, method, cb) {
      let url = '/dataportal/portal';
      api.fetch(url, { ...params }, method).then(() => {
        cb && cb();
      })
    },
    searchPortal() {
      let params = {};
      if (this.portalName === '') {
        params = {
          portalName: this.portalName,
        };
        this.currentMenu = ''
      } else {
        params = {
          portalName: this.portalName,
        };
      }
      this.getPortalList(params).then();
    },
    initFormValue() {
      this.formValue.name = '';
      this.formValue.tag = '';
      this.formValue.editUser = [];
      this.formValue.accessUser = [];
      this.formValue.description = '';
      this.$refs.addForm.resetFields();
    },
    cancel() {
      this.initFormValue();
      this.addOrEditlModal = false;
    },
    //添加数据门户的操作
    addPortal() {
      this.addOrEditlModal = true;
      this.tagList = [];
      this.showInput = true;
      this.modalType = 'add';
      this.initFormValue();
    },
    deletetTag(index) {
      this.tagList.splice(index, 1)
    },
    enterTag() {
      let regTag = /^[\u4E00-\u9FA5A-Za-z0-9]+$/;
      if (this.formValue.tag === '' || !regTag.test(this.formValue.tag) || this.formValue.tag.trim().length > 32) {
        this.$Message.error('仅允许输入中文、英文和数字，长度在32个字符之内');
      } else {
        let flag = 0;
        for (let i = 0; i < this.tagList.length; i++) {
          if (this.tagList[i] === this.formValue.tag) {
            flag++;
            break;
          }
        }
        if (flag) {
          this.$Message.error(`${this.formValue.tag}已存在，请重新输入`);
        } else {
          this.tagList.push(this.formValue.tag);
          this.formValue.tag = '';
        }
      }
    },
    showTagInput() {
      this.showInput = true;
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
<style scoped lang="scss">
@import "@/common/style/variables.scss";
.tag-input {
  flex: 2;
}
.input-value-show {
  flex: 1;
  margin-left: 100px;
  margin-bottom: 10px;
  .value-item {
    padding: 5px;
    border: 1px solid $border-color-base;
    margin-right: 3px;
    border-radius: 10px;
    display: inline-block;
    margin-bottom: 3px;
  }
  .delTag {
    cursor: pointer;
    color: $link-color;
  }
}
.card-wrap {
  min-width: 900px;
}
.icon {
  position: absolute;
  left: 62px;
  top: 132px;
  color: $text-red-color;
  font-size: 18px;
}
</style>
