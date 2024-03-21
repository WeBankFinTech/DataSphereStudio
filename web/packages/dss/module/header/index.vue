<template>
  <div class="layout-header">
    <div class="layout-header-menu-icon">
      <product-menu
        v-if="isNavShowMenu"
        :apps="menuList"
        :favorites="favorites"
        :collections="collections"
        :use-default-action="false"
        @menu-click="handleMenuClick"
        @favorite-remove="removeFavorite"
        @favorite-add="addFavorite"
        @collect-add="addCollection"
        @collect-remove="removeCollection"
        ref="vueLubanMenu"
      >
        <div class="luban-menu-trigger">
          <img src="../../assets/images/luban-menu-trigger.png" />
        </div>
      </product-menu>

      <div class="logo">
        <img
          @click.stop="goHome"
          class="logo-img"
          :style="{ cursor: 'pointer' }"
          :src="$APP_CONF.app_logo"
          :alt="$APP_CONF.app_name"
        />

        <span class="version">{{ sysVersion }}</span>
      </div>
    </div>
    <Dropdown
      class="project-dro"
      v-if="showWorkspaceNav && !currentProject.id"
    >
      <div class="project">
        {{ currentWorkspace.name }}
        <Icon type="ios-arrow-down" style="margin-left: 5px"></Icon>
      </div>
      <DropdownMenu slot="list" class="proj-list">
        <div class="proj-name">{{ $t('message.common.dss.worklist') }}</div>
        <div class="name-bar">
          <span
            v-for="p in workspaceList"
            :key="p.id"
            :class="{ active: p.id == currentWorkspace.id }"
            class="proj-item"
            @click="changeWorkspace(p)"
          >{{ p.name }}</span
          >
        </div>
      </DropdownMenu>
    </Dropdown>

    <Dropdown class="project-dro" v-if="currentProject.id">
      <div class="project">
        {{ currentProject.name }}
        <Icon type="ios-arrow-down" style="margin-left: 5px"></Icon>
      </div>
      <DropdownMenu slot="list" class="proj-list">
        <div v-for="proj in projectList" :key="proj.id">
          <div class="proj-name">{{ proj.name }}</div>
          <div class="name-bar">
            <span
              v-for="p in proj.dwsProjectList"
              @click="changeProj(proj, p)"
              :key="proj.id + p.id"
              :class="{ active: p.id == currentProject.id }"
              class="proj-item"
            >{{ p.name }}</span
            >
          </div>
        </div>
      </DropdownMenu>
    </Dropdown>
    <div
      v-clickoutside="handleOutsideClick"
      :class="{ selected: isUserMenuShow }"
      class="user"
      @click="handleUserClick"
    >
      <div class="userName">
        <span>{{ userName || "Null" }}</span>
        <Icon
          v-show="!isUserMenuShow"
          type="ios-arrow-down"
          class="user-icon"
        />
        <Icon v-show="isUserMenuShow" type="ios-arrow-up" class="user-icon" />
      </div>
      <userMenu v-show="isUserMenuShow" @clear-session="clearSession" />
    </div>
    <!-- 需要用户可以手动自定义 -->
    <ul class="menu" v-if="$route.path !== '/newhome' && $route.path !== '/bankhome' && $route.query.workspaceId">
      <li
        class="menu-item"
        @click="goSpaceHome"
        :class="isHomePage ? 'header-actived' : '' "
      >
        {{ $t("message.common.home") }}
      </li>
      <li
        class="menu-item"
        v-if="$route.query.workspaceId"
        @click="goConsole"
        :class="isConsolePage ? 'header-actived' : '' "
      >
        {{$t("message.common.management")}}
      </li>
      <li
        v-for="app in comCollections"
        :key="app.id"
        class="menu-item"
        :class="app.menuApplicationId == currentId ? 'header-actived' : '' "
      >
        <Dropdown
          class="menu-item-dropdown"
          v-if="app.appInstances && app.appInstances.length > 1"
          @on-click="handleMenuClick(app, $event)"
        >
          <span>{{ app.title }}</span>
          <DropdownMenu slot="list" class="list">
            <Dropdown-item
              v-for="(it, idx) in app.appInstances"
              :key="idx"
              :name="idx"
            >{{ it.name || item.title }}</Dropdown-item
            >
          </DropdownMenu>
        </Dropdown>
        <span v-else  @click="goCollectedUrl(app)">{{ app.title }}</span>
      </li>
    </ul>
    <div class="icon-group">
      <Icon
        v-if="isSandbox"
        :title="$t('message.common.home')"
        class="book"
        type="ios-chatboxes"
        @click="linkTo('freedback')"
      ></Icon>
    </div>
  </div>
</template>
<script>
import storage from '@dataspherestudio/shared/common/helper/storage';
import clickoutside from '@dataspherestudio/shared/common/helper/clickoutside';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import util from '@dataspherestudio/shared/common/util';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
import tree from '@dataspherestudio/scriptis/service/db/tree.js';
import userMenu from "./userMenu.vue";
import productMenu from "./productMenu.vue";
import {
  GetWorkspaceApplications,
  GetWorkspaceList,
  GetWorkspaceData,
  GetFavorites,
  AddFavorite,
  RemoveFavorite,
  GetCollections
} from '@dataspherestudio/shared/common/service/apiCommonMethod.js';

export default {
  directives: {
    clickoutside,
  },
  components: {
    userMenu,
    productMenu
  },
  data() {
    return {
      isAdmin: false,
      sysVersion: process.env.VUE_APP_VERSION,
      isUserMenuShow: false,
      userName: "",
      currentProject: {},
      projectList: [],
      isSandbox: this.$APP_CONF.isSandbox,
      workspaceList: [],
      currentWorkspace: {},
      menuList: [],
      // luban-nav-menu
      favorites: [],
      collections: [],
      currentId: -1,
      isHomePage: false,
      isConsolePage: false,
    };
  },
  mixins: [mixin],
  created() {
    this.getWorkspacesRoles()
      .then((res) => {
        // cookies改变最新后再执行其他方法
        if (res) {
          this.init();
          this.getApplications();
          this.getWorkspaces();
          this.getWorkspaceFavorites();
          this.getWorkspaceCollections()
        }
      })
      .catch((err) => {
        console.error(err);
      });
  },
  mounted() {
    this.init();
    this.getCurrentProject();
  },
  computed: {
    isNavShowMenu() {
      return !!this.$route.query.workspaceId;
    },
    showWorkspaceNav() {
      return (
        this.$route.path.indexOf("/workspaceHome") !== -1 ||
        this.$route.path.indexOf("/dataService") !== -1 ||
        this.$route.path.indexOf("/dataManagement") !== -1 ||
        this.$route.path === "/project"
      );
    },
    comCollections() {
      const list = []
      this.collections.forEach(c => {
        const cItem = {...c}
        this.menuList.forEach(it => {
          if (it.appconns) {
            const inst = it.appconns.find(item => item.id == c.menuApplicationId)
            if (inst) cItem.appInstances = inst.appInstances
          }
        })
        list.push(cItem)
      })
      return list
    }
  },
  watch: {
    "$route.query.projectID"(newValue) {
      this.projectID = newValue;
      this.getCurrentProject();
    },
    // 将以前的'$route.query.workspaceId'监听换成'$route'，之前的无法监听组件之间的切换触发
    $route(v) {
      // 设定条件只有切换在工作空间首页时才触发
      if (v.name === "workspaceHome") {
        this.currentId = -1;
        this.init();
        this.getWorkspacesRoles()
          .then((res) => {
            // cookies改变最新后再执行其他方法
            if (res) {
              this.getApplications();
              this.getWorkspaces();
              this.getWorkspaceFavorites();
              this.getWorkspaceCollections();
            }
          })
          .catch((err) => {
            console.error(err);
          });
      }
      this.isHomePage = v.name === "workspaceHome"
      if (v.query.menuApplicationId) {
        this.isHomePage = false
        this.isConsolePage = false
        this.currentId = +v.query.menuApplicationId
      }
    },
  },
  methods: {
    init() {
      this.userName = this.getUserName();
      this.isAdmin = this.getIsAdmin();
    },
    // 获取进入工作空间的用户权限
    getWorkspacesRoles() {
      return new Promise((resolve, reject) => {
        if (this.$route.query.workspaceId) {
          GetWorkspaceData(this.$route.query.workspaceId || "")
            .then((res) => {
              // 缓存数据，供其他页面判断使用
              storage.set(`workspaceRoles`, res.roles || [], "session");
              // roles主动触发，防止接口请求和sessionstorge之间的时间差导致角色没有及时转换
              eventbus.emit("workspace.change", res.roles || []);
              // 同步改变cookies在请求中的附带
              resolve(true);
              // 改变了cookies通知其他地方
              this.$router.app.$emit("getChangeCookies");
            })
            .catch((err) => {
              reject(err);
            });
        } else {
          resolve(false);
        }
      });
    },
    isShowWorkspaceMenu() {
      return (
        !this.currentProject.id &&
        this.$route.query &&
        this.$route.query.workspaceId &&
        this.$route.path.indexOf("workflow") === -1
      );
    },
    getApplications() {
      if (this.$route.query.workspaceId) {
        GetWorkspaceApplications(this.$route.query.workspaceId).then((data) => {
          this.menuList = data.menus || [];
          // 展示有权限的实例
          const applications = [];
          this.menuList = this.menuList.map((app) => {
            if (app.appconns && app.appconns.length) {
              applications.push({...app})
              return {
                ...app,
                appconns: app.appconns.filter(
                  (item) => item.accessable
                ),
              };
            }
            return app;
          });
          // 类别下没有实例则隐藏
          this.menuList = this.menuList.filter(
            (app) => app.appconns.length > 0
          );
          storage.set('applications', applications)
        });
      }
    },
    getWorkspaces() {
      GetWorkspaceList({}, "get")
        .then((res) => {
          this.workspaceList = res.workspaces;
          this.getCurrentWorkspace();
        })
        .catch(() => {});
    },
    getWorkspaceFavorites() {
      if (this.$route.query.workspaceId) {
        GetFavorites(this.$route.query.workspaceId).then((data) => {
          this.favorites = (data.favorites || []).map(item => {
            return {
              ...item,
              id: item.menuApplicationId
            }
          });
        });
      }
    },
    getWorkspaceCollections() {
      if (this.$route.query.workspaceId) {
        GetCollections(this.$route.query.workspaceId).then(data => {
          let collections = data.favorites || [];
          while( collections.length > 5 ) {
            collections.pop();
          }
          this.collections = collections.map(item => {
            if (this.$route.name == item.name) {
              this.currentId = item.menuApplicationId
            }
            return {
              ...item,
              id: item.menuApplicationId,
              collectedId: item.menuApplicationId
            }
          })
        })
      }
    },
    addFavorite(app) {
      if (this.$route.query.workspaceId) {
        AddFavorite(this.$route.query.workspaceId, {
          menuApplicationId: app.id,
        }).then(() => {
          this.favorites = this.favorites.concat({
            ...app,
            id: app.id,
            menuApplicationId: app.id,
          });
        });
      }
    },
    removeFavorite(app) {
      if (this.$route.query.workspaceId) {
        RemoveFavorite({
          workspaceId: this.$route.query.workspaceId,
          applicationId: app.menuApplicationId,
        }).then(() => {
          this.favorites = this.favorites.filter(
            (i) => i.menuApplicationId !== app.menuApplicationId
          );
        });

        this.collections = this.collections.filter(
          (i) => i.menuApplicationId !== app.menuApplicationId
        );
      }
    },
    addCollection(app) {
      if (this.$route.query.workspaceId) {
        if ( this.collections.length < 5 ) {
          AddFavorite(this.$route.query.workspaceId, {
            menuApplicationId: app.menuApplicationId,
            type: 'dingyiding'
          }).then(() => {
            app.collectedId = app.menuApplicationId
            this.collections = this.collections.concat(app);
          })
        } else {
          this.$Message.warning(this.$t('message.common.dss.fiveshortcut'))
        }
      }
    },
    removeCollection(app) {
      app.collectedId = app.menuApplicationId
      if (this.$route.query.workspaceId) {
        RemoveFavorite({
          workspaceId: this.$route.query.workspaceId,
          applicationId: app.menuApplicationId,
          type: 'dingyiding'
        }).then(() => {
          this.collections = this.collections.filter(
            (i) => i.collectedId !== app.collectedId
          );
        })
      }
    },
    handleMenuClick(item, index = 0) {
      this.gotoCommonFunc({app: item, index}, {
        workspaceId: this.$route.query.workspaceId,
      });
    },
    goto(name) {
      this.$router.push({
        name: name,
      });
    },
    getClass(path) {
      let arr = [];
      if (this.$route.name === path || this.$route.meta.parent === path) {
        arr.push("selected");
      }
      return arr;
    },
    handleOutsideClick() {
      if (
        this.$parent.$refs.newGuidance &&
        this.$parent.$refs.newGuidance.currentStep !== 6
      ) {
        this.isUserMenuShow = false;
      }
    },
    handleUserClick() {
      this.isUserMenuShow = !this.isUserMenuShow;
    },
    clearSession() {
      tree.remove('scriptTree');
      tree.remove('hdfsTree');
      tree.remove('hiveTree');
      tree.remove('udfTree');
      tree.remove('functionTree');
      this.$emit("clear-session");
    },
    getCurrentWorkspace() {
      let workspaceId = +this.$route.query.workspaceId;
      if (workspaceId) {
        this.workspaceList.forEach((item) => {
          if (item.id === workspaceId) {
            this.currentWorkspace = item;
            storage.set("currentWorkspace", item);
          }
        });
      }
    },
    getCurrentProject() {
      let projList = storage.get("projectList", "local");
      this.projectList = projList;
      let projId = this.$route.query.projectID;
      let proj = {};
      if (projId && this.projectList) {
        this.projectList.forEach((item) => {
          if (item.dwsProjectList) {
            item.dwsProjectList.forEach((p) => {
              if (p.id == projId) {
                proj = { ...p };
              }
            });
          }
        });
      }
      this.currentProject = proj;
    },
    // 切换工作空间
    changeWorkspace(workspace) {
      if (workspace.id === this.currentWorkspace.id) return;
      if (
        this.$route.path.indexOf("/dataService") !== -1 ||
        this.$route.path.indexOf("/dataManagement") !== -1
      ) {
        // 数据服务切换workspace通过一个redirect路由来实现页面的刷新，避免在每个页面都watch route
        this.currentWorkspace = workspace;
        storage.set("currentWorkspace", workspace);
        this.$router.replace({
          path: "/redirect" + this.$route.path,
          query: {
            ...this.$route.query,
            workspaceId: workspace.id,
          },
        });
      } else {
        this.$router.replace({
          path: this.$route.path,
          query: {
            ...this.$route.query,
            workspaceId: workspace.id,
          },
        });
      }
    },
    changeProj(proj, p) {
      if (
        p.id == this.currentProject.id &&
        proj.id == this.$route.query.projectTaxonomyID
      )
        return;
      // 得考虑在流程图页面和知画的情况, 在此情况下跳转到工程页
      if (["/process"].includes(this.$route.path)) {
        this.$router.replace({ path: "/project" });
      } else {
        this.$router.replace({
          path: this.$route.path,
          query: {
            workspaceId: this.$route.query.workspaceId,
            ...this.$route.query,
            projectTaxonomyID: proj.id,
            projectID: p.id,
            projectName: p.name,
            notPublish: p.notPublish,
          },
        });
      }
    },
    goHome() {
      this.isHomePage = true;
      this.isConsolePage = false;
      if (this.isAdmin) {
        this.$router.push("/newhome");
      } else {
        this.goSpaceHome();
      }
    },
    linkTo(type) {
      let url = "";
      if (type === "book") {
        url = `https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch3/DSS_User_Manual.md`;
      } else if (type === "github") {
        url = `https://github.com/WeBankFinTech/DataSphereStudio`;
      } else if (type === "freedback") {
        url = "https://wj.qq.com/s2/4943071/c037/ ";
        if (localStorage.getItem("locale") === "en") {
          url = "https://wj.qq.com/s2/4943706/5a8b";
        }
      }
      util.windowOpen(url);
    },
    goSpaceHome() {
      this.isHomePage = true;
      this.isConsolePage = false;
      let workspaceId = this.$route.query.workspaceId;
      this.currentId = -1;
      if (!workspaceId) {
        // workspaceId为空，说明一定是admin，进入了admin的页面，因为workspaceId是一直伴随的
        // 就无须在调goHome，防止在控制台页面因为isAdmin失效而导致goHome和goSpaceHome来回调用而报错RangeError: Maximum call stack size exceeded
        this.$router.push("/newhome");
      } else {
        this.$router.push({ path: "/workspaceHome", query: { workspaceId } });
        this.currentProject = {};
      }
    },
    goConsole() {
      this.isHomePage = false;
      this.isConsolePage = true;
      this.currentId = -1;
      const url =
        location.origin + "/dss/linkis/?noHeader=1&noFooter=1#/console";
      this.$router.push({
        path: '/commonIframe/linkis',
        query: {
          workspaceId: this.$route.query.workspaceId,
          url
        }
      });
    },
    goCollectedUrl(app) {
      this.isHomePage = false;
      this.isConsolePage = false;
      this.currentId = app.menuApplicationId || -1;
      this.gotoCommonFunc({app, index: 0}, {
        workspaceId: this.$route.query.workspaceId,
      });
    }
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
