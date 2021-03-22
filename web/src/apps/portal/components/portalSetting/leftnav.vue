<template>
  <div class="set-left">
    <div class="left-header">
      <Icon type="md-arrow-round-back" :size="24" @click="goHome" />
      <Icon type="md-settings" :size="24" title="门户设置" @click="changeSetType('common')" />
    </div>
    <div class="left-content">
      <div v-if="configuration.logo" class="logo-img">
        <img :src="configuration.logo">
      </div>
      <h3 :title="configuration.name">{{configuration.name}}</h3>
    </div>

    <!-- 展示保存的菜单 -->
    <div class="left-footer">
      <Tree :data="menuList" class="demo-tree-render" @on-select-change="changeTree"></Tree>
      <Button class="new" type="primary" icon="md-add" @click="changeSetType('menu')">新建菜单</Button>
    </div>
  </div>
</template>
<script>
import bus from '../bus'
import api from '@/common/service/api';
export default {
  data() {
    return {
      currentRoot: '',
      currentNode: '',
      currentData: {},
      currentPortalId: '',
      menuId: '',
      configuration: {
        name: '请输入门户标题',
        logo: ''
      },
      isNewBuild: '',
      menuList: [],
      nodeKey: '',
      editedMenu: '',
      portalId: parseInt(this.$route.query.portalId)
    }
  },
  computed: {
  },
  created() {
    this.init();
    this.workspaceId = this.$route.query.workspaceId;
    //监听编辑节点的操作
    bus.$on('saveEidtNode', (value, componentList) => {
      this.setNode(value, componentList);
    })
    //监听编辑目录的操作
    bus.$on('saveDirecoty', (value) => {
      this.setDirectory(value)
    })
  },
  mounted() {
    //监听保存设置门户的操作
    bus.$on('setPoralBasic', (value) => {
      this.setPortal(value)
    });
    //监听保存设置菜单的操作
    bus.$on('setLeftMenu', (value, componentList) => {
      this.setMenu(value, componentList);
    })
  },
  methods: {
    init() {
      this.getMenuList();
      this.getPortalSetting();
    },
    //判断是更改数据门户还是设置数据门户
    setPortal(value) {
      let url = '/dataportal/configuration';
      let obj = JSON.parse(JSON.stringify(value));
      for (let key in obj) {
        if (obj[key] === '否') {
          obj[key] = false;
        } else if (obj[key] === '是') {
          obj[key] = true;
        }
      }
      let dataPortalId = this.$route.query.portalId;
      let params = {
        dataPortalId,
        ...obj
      };
      let method = '';
      if (this.configuration.shortcut) {
        method = 'put'
      } else {
        method = 'post'
      }
      api.fetch(url, params, method).then(() => {
        this.$Message.success('门户设置成功');
        bus.$emit('setPortalSuccess')
        this.getPortalSetting();
      }).catch(() => {
        bus.$emit('setPortalSuccess')
      })
    },
    setMenu(value, componentList) {
      this.editedMenu = JSON.parse(JSON.stringify(value));
      let dataPortalId = parseInt(this.$route.query.portalId);
      let isHidden = this.editedMenu.flod === '1' ? true : false;
      let directoryType = this.editedMenu.directoryType === '' ? 'same' : this.editedMenu.directoryType;
      let directoryPreContent = Object.assign({}, { directoryType: directoryType, isHidden })
      let application = value.content;
      let componentContent = JSON.parse(JSON.stringify(componentList));
      let nodePreContent = this.getPreContent(componentContent);
      Object.assign(nodePreContent, { application });
      let obj = {
        name: this.editedMenu.title,
        dataPortalId,
        parent: this.menuId,
        type: this.editedMenu.menuType,
        accessUser: this.editedMenu.accessUser === '' ? [] : this.editedMenu.accessUser,
        ranking: parseInt(this.editedMenu.order)
      };
      let params = {};
      if (this.editedMenu.menuType === 'directory') {
        params = {
          ...obj,
          preContent: directoryPreContent
        };
      } else {
        params = {
          ...obj,
          preContent: nodePreContent
        };
      }
      let url = '/dataportal/menu';
      //根据不同的添加类型。来判断提示语
      let menuType =  this.editedMenu.menuType;
      let message = menuType === 'directory' ? '新建目录成功' : '新建节点成功'
      api.fetch(url, params, 'post').then(() => {
        this.$Message.success(message);
        this.getMenuList();
        this.$emit('newMenuSuccess', true);
      })
    },
    changeTree() {
    },
    setNode(value, componentList) {
      let application = value.content;
      let  nodePreContent = this.getPreContent(componentList, application)
      let obj = {
        name: value.title,
        dataPortalId: this.portalId,
        id: this.currentData.id,
        type: 'node',
        accessUser: value.accessUser,
        ranking: value.order,
        preContent: Object.assign(nodePreContent, {application}),
      };
      let url = '/dataportal/menu';
      api.fetch(url, obj, 'put').then(() => {
        this.$Message.success('编辑节点成功');
        this.getMenuList();
        this.$emit('saveNodeSuccess', true)
      })
    },
    setDirectory(value) {
      let obj = {
        name: value.title,
        dataPortalId: this.portalId,
        id: this.currentData.id,
        type: 'directory',
        accessUser: value.accessUser,
        preContent: {
          directoryType: 'sub',
          isHidden: value.flod === '1' ? true : false
        },
        ranking: value.order
      };
      let url = '/dataportal/menu';
      api.fetch(url, obj, 'put').then(() => {
        this.$Message.success('编辑目录成功')
        //更新左侧的门户列表
        this.getMenuList();
        this.$emit('saveDirSuccess', true)

      })
    },
    getPreContent(componentContent) {
      let nodePreContent = {};
      componentContent.forEach(it => {
        let componentObj = {};
        for (let p in it) {
          // debugger
          let nameObj = it.list.find(p => {
            return p.id === it.value
          });
          let name = nameObj ? nameObj.name : '';
          let obj = {
            name,
            id: it.value
          }
          if (p === 'key') {
            if (it[p] === 'visualis/component') {
              let componentName = it.list.find(i => i.id === it.value) ? it.list.find(i => i.id === it.value).name : '';
              componentObj = {
                [it[p]]: componentName
              }
            } else if (it[p] === 'urllink/link') {
              componentObj = {
                [it[p]]: it.value
              }
            } else {
              componentObj = {
                [it[p]]: obj
              };
            }
          }
          Object.assign(nodePreContent, { ...componentObj })
        }
      })
      return nodePreContent;
    },
    //得到左侧的菜单列表
    getMenuList() {
      this.currentPortalId = this.$route.query.portalId
      let url = '/dataportal/menuList';
      let params = {
        portalId: this.currentPortalId,
      };
      api.fetch(url, params, 'get').then(res => {
        this.menuList = res.menuList;
        this.getMenuListFromat();
      })
    },
    //得到左侧的门户设置
    getPortalSetting() {
      //如果请求的结果是null，显示的默认的 --- '请输入门户标题'
      let url = '/dataportal/configuration';
      let dataPortalId = this.$route.query.portalId;
      api.fetch(url, { dataPortalId }, 'get').then(res => {
        if (res.Configuration !== null) {
          this.configuration = res.Configuration;
        }
      })
    },
    getMenuListFromat() {
      let directoryRenderObj = [
        { type: 'ios-add', func: (root, node, data) => { this.append(root, node, data) } },
        { type: 'ios-settings-outline', func: (root, node, data) => { this.set(root, node, data) } },
        { type: 'ios-remove', func: (root, node, data) => { this.remove(root, node, data) } }
      ];
      let directoryRender = (h, { root, node, data }) => {
        return h('span', {
          style: {
            display: 'inline-block',
            width: '80%',
          }
        }, [
          h('span', [
            h('Icon', {
              props: {
                type: 'ios-folder-outline'
              },
              style: {
                marginRight: '5px',
              }
            }),
            h('div', {
              class: 'menuOverflow',
              attrs: {
                title: data.title
              },
            }, data.title)
          ]),
          h('span', {
            style: {
              display: 'inline-block',
              float: 'right',
            }
          }, [
            directoryRenderObj.map(item => {
              return h('Icon', {
                props: {
                  type: item.type,
                  size: '20',
                },
                style: {
                  cursor: 'pointer',
                  marginRight: '5px',

                },
                on: {
                  click: () => {
                    item.func(root, node, data)
                  }
                }
              })
            })
          ])
        ]);
      };

      let nodeRender = (h, { root, node, data }) => {
        return h('span', {
          style: {
            display: 'inline-block',
            width: '80%'
          }
        }, [
          h('span', [
            h('Icon', {
              props: {
                type: 'md-document'
              },
              style: {
                marginRight: '5px'
              }
            }),
            h('span', {
              attrs: {
                title: data.title
              },
              style: {
                width: '50px',
                display: 'inline-block',
              },
              class: 'menuOverflow',
            }, data.title)
          ]),
          h('span', {
            style: {
              display: 'inline-block',
              float: 'right',
            }
          }, [
            directoryRenderObj.map(item => {
              return h('Icon', {
                props: {
                  type: item.type,
                  size: '20',
                },
                style: {
                  marginRight: '5px',
                  cursor: 'pointer'
                },
                on: {
                  click: () => {
                    item.func(root, node, data)
                  }
                }
              })
            })
          ])
        ]);
      };
      const setNode = function(array) {
        return array.map((item) => {
          let obj = {...item}
          obj.title = item.name;
          obj.render = item.menuType === 'node' ? nodeRender : directoryRender;
          obj.expand = !item.preContent.isHidden;
          obj.children = setNode(item.childMenus);
          return obj
        })
      }
      this.menuList = setNode(this.menuList);
    },
    goHome() {
      let pathObj = {
        path: '/portal/portalhome',
        query: {workspaceId: this.workspaceId}
      };
      this.$router.push(pathObj);
    },
    //如果点击的是新建菜单，menuId是0
    changeSetType(type) {
      this.menuId = 0;
      //1用来判断是新建菜单
      let typeObj = {
        type,
        flag: 1,
        parentId: 0,
      };
      this.$emit('changeSetType', typeObj);
      bus.$emit('init', type)
    },
    append(root, node, data) {
      this.currentData = data;
      this.currentRoot = root;
      this.currentNode = node;
      //判断点击的是哪个节点：
      //父目录提示：是否添加同级目录
      //子目录提示： 是否添加子目录
      //节点：是否添加同级节点
      this.menuId = data.id;
      //判断当前是节点还是目录
      if (data.menuType === 'node') {
        return this.$Message.success('无法对当前的节点进行添加操作');
      }
      this.$Modal.confirm({
        title: '新建菜单',
        content: '<p>是否确认新建菜单</p>',
        onOk: () => {
          //2用来判断是修改菜单
          let typeObj = {
            type: 'menu',
            flag: 1,
          };
          this.$emit('changeSetType', {...typeObj, ...data});
          //点击新建菜单初始化菜单，传递menuId
          bus.$emit('initMenuForm', data);
        },
        onCancel: () => {
        }
      });
    },
    remove(root, node, data) {
      let url = `dataportal/menu/${data.id}`;
      //权限控制
      if (data.menuType === 'directory' && data.childMenus.length) {
        this.$Message.error('该目录下存在节点，无法删除该目录');
      } else {
        api.fetch(url, 'delete').then(() => {
          this.$Message.success(`删除${data.name}成功`);
          this.getMenuList();
        })
      }
    },
    set(root, node, data) {
      this.currentData = data;
      let obj = {
        currentData: data,
        flag: 2
      };
      this.$emit('initTree', true);
      bus.$emit('editMenu', obj)
    }
  },
  beforeDestroy () {
    bus.$off('setPoralBasic');
    bus.$off('saveEidtNode');
    bus.$off('setLeftMenu');
    bus.$off('setDirectory');
    bus.$off('saveDirecoty');
  }
}
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.set-left {
  height: 100%;
  position: absolute;
  background: #fff;
  padding: 15px 25px;
  width: 300px;
  border-right: 1px solid $scrollbar-color;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  .left-header {
    display: flex;
    justify-content: space-between;
    flex: 1;
  }
  .left-footer {
    text-align: center;
    height: 300px;
    flex: 12;
  }
  .showPointer {
    cursor: pointer;
    &:hover {
      color: $link-color;
    }
  }
  .new {
    margin-top: 30px;
  }
  .left-content {
    flex: 1;
    display: flex;
    .logo-img {
      width: 30px;
      height: 30px;
      line-height: 30px;
      img {
        width: 100%;
        height: 100%;
      }
    }
    h3 {
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      margin-left: 5px;
      line-height: 30px;
    }
  }
}
</style>
