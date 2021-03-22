<template>
  <div style="height: 100%" class="page">
    <LeftNav
      :leftNavData="leftNavData"
      @setCurrentPage="setCurrentPage"
    ></LeftNav>
    <div class="content">
      <wbIframe :url="currentUrl" />
    </div>
    <Spin fix size="large" v-if="showLoading">
      <Icon type="ios-loading" size="40" class="demo-spin-icon-load"></Icon>
    </Spin>
  </div>
</template>
<script>
import LeftNav from '../../components/portalList/leftnav';
import wbIframe from '../../components/components/wbIframe.vue'
import api from "@/common/service/api";
import storage from '@/common/helper/storage';
export default {
  components: {
    LeftNav,
    wbIframe
  },
  data() {
    return {
      leftNavData: {
        // 各项的数据，必传
        items: [],
        // 左侧导航栏的默认选中的name，必传
        activeName: '',
        // 左侧导航栏的默认打开的Submenu的names，必传
        openNames: [],
        currentPage: '',
      },
      currentUrl: '',
      showLoading: true,
      portalId: this.$route.query.portalId,
      menuList: []
    }
  },
  created() {
    this.getItems();
  },
  methods: {
    getUserName() {
      return storage.get('userInfo', 'session');
    },
    getItems() {
      let url = '/dataportal/show';
      let params = {
        portalId: this.$route.query.portalId
      };
      return new Promise((resolve, reject) => {
        api.fetch(url, params, 'get').then(res => {
          let result = res.menuList;
          if (!result.length) return;
          const setNode = function(array) {
            return array.map((item) => {
              let obj = {...item}
              obj.title = item.name;
              obj.icon = item.type === 'directory' ? 'ios-folder-outline' : 'md-document';
              obj.subItems = setNode(item.childMenus);
              return obj
            })
          }
          this.leftNavData.items = setNode(result);
          resolve(res);
        }).catch(err => {
          reject(err)
        }).finally(() => {
          this.showLoading = false
        })
      })
    },
    setCurrentPage(currentPage) {
      this.currentPage = currentPage;
      this.getDataPortalContent();
    },
    //点击的时候获取数据门户内容
    getDataPortalContent() {
      //根据currentPage来判选择的menuId
      let menuId = '';
      this.getItems().then(res => {
        this.menuList = res.menuList;
        menuId = this.getMenuId(this.menuList);
        //权限控制
        this.getMenuIdContent(menuId);
      })
    },
    //得到当前的menuId
    getMenuId(menuList) {
      let menuId = '';
      if (!menuList.length) return;
      menuList.forEach(it => {
        if (it.name === this.currentPage) {
          menuId = it.id;
          // this.currentUrl = it.menuURL;
        } else if (it.childMenus.length) {
          it.childMenus.forEach(item => {
            if (item.name === this.currentPage) {
              menuId = item.id;
              // this.currentUrl = item.menuURL;
            } else if (item.childMenus.length) {
              item.childMenus.forEach(p => {
                if (p.name === this.currentPage) {
                  menuId = item.id;
                  // this.currentUrl = p.menuURL;
                }
              })
            }
          })
        }
      })
      return menuId;
    },
    //根据menuId得到当前的url
    getCurrentUrl(menuList) {
      if (!menuList.length) return;
      menuList.forEach(it => {
        if (it.name === this.currentPage) {
          this.currentUrl = it.menuURL;
        } else if (it.childMenus.length) {
          it.childMenus.forEach(item => {
            if (item.name === this.currentPage) {
              this.currentUrl = item.menuURL;
            } else if (item.childMenus.length) {
              item.childMenus.forEach(p => {
                if (p.name === this.currentPage) {
                  this.currentUrl = p.menuURL;
                }
              })
            }
          })
        }
      })
    },
    //得到menuId下的内容
    getMenuIdContent(menuId) {
      let url = '/dataportal/menu';
      api.fetch(url, { menuId }, 'get').then(() => {
        // this.accessUser = res.menu.accessUser;
        // let creator = res.menu.creator;
        // let accessUser = this.accessUser.concat(creator);
        // let userName = this.getUserName();
        // let index = accessUser.indexOf(userName);
        // if (index === -1) return this.$Message.error('您无权限访问该内容');
        this.getCurrentUrl(this.menuList)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.page {
  display: flex;
  overflow-y: hidden;
  .content {
    width: 100%;
  }
}

</style>
