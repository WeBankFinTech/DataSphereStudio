<template>
  <div
    class="setting"
    style="height: 100%;">
    <Menu
      :active-name="activeMenu || '1'"
      :open-names="[activeMenu || '1']"
      @on-select="getAppVariable"
      ref="settingMenu"
      class="setting-menu">
      <template v-for="menu in menuList">
        <template v-if="menu.childCategory && menu.childCategory.length" >
          <Submenu
            :name="menu.categoryName"
            :key="menu.categoryId">
            <template slot="title">{{ menu.categoryName }}</template>
            <MenuItem v-for="item in menu.childCategory" :key="item.categoryId" :name="`${menu.categoryName}-${item.categoryName}`">{{item.categoryName}}</MenuItem>
          </Submenu>
        </template>
        <MenuItem v-else :key="menu.categoryId" :name="menu.categoryName">{{menu.categoryName}}</MenuItem>
      </template>
    </Menu>
    <div
      v-if="fullTree && fullTree.length"
      class="setting-content">
      <div class="setting-content-header">
        <Button
          @click="toggleAdvance">{{ isAdvancedShow ? $t('message.linkis.setting.hide') : $t('message.linkis.setting.show') }}{{ $t('message.linkis.setting.advancedSetting') }}</Button>
        <Button
          :loading="loading"
          type="primary"
          class="setting-content-btn"
          @click="save">{{ $t('message.linkis.save') }}</Button>
      </div>
      <div
        class="setting-content-variable"
        style="height:calc(100% - 45px)">
        <variable
          v-for="(item, index) in fullTree"
          ref="variable"
          :key="index"
          :variable="item"
          @add-item="handleAdd"
          @remove-item="handleDelete"
          :un-valid-msg="unValidMsg"
          :is-advanced-show="isAdvancedShow"/>
      </div>
    </div>
  </div>
</template>
<script>
import { orderBy } from 'lodash';
import api from '@/common/service/api';
import variable from '@/apps/linkis/components/variable';
export default {
  name: 'Setting',
  components: {
    variable,
  },
  data() {
    return {
      menuList: [],
      activeMenu: '', // 当前打开目录
      fullTree: [],
      loading: false,
      unValidMsg: {},
      isAdvancedShow: false,
      height: 0,
    };
  },
  mounted() {
    this.height = this.$route.query.height;
    // 获取设置目录
    api.fetch('/configuration/getCategory', 'get').then((rst) => {
      this.menuList = rst.Category || [];
      this.$nextTick(() => {
        this.getAppVariable(this.menuList[0].categoryName || '1');
        this.$refs.settingMenu.updateActiveName();
      });
    })

  },
  methods: {
    getAppVariable(type) {
      this.activeMenu = type;
      let parameter = type.split('-'); // 切割目录name
      // 如果只有一级目录则直接返回['creator'],如果为二级目录则['creator', 'engineType', 'version']
      api.fetch('/configuration/getFullTreesByAppName', {
        creator: parameter[0], // 指定一级目录
        engineType: parameter[1], // 指定引擎（二级目录）如果只有一级目录则自动为undefined不会传参
        version: parameter[2], // 对应的引擎目前只支持对应的版本，如spark就传version-2.4.3，如果只有一级目录则自动为undefined不会传参
      }, 'get').then((rst) => {
        this.loading = false;
        this.fullTree = rst.fullTree;
        this.fullTree.forEach((item) => {
          item.settings = orderBy(item.settings, ['level'], ['asc']);
          if (item.settings.length) {
            item.settings.forEach((set) => {
              if (set.validateType === 'OFT') {
                set.validateRangeList = this.formatValidateRange(set.validateRange, set.key);
              }
              if (set.key === 'spark.application.pyFiles' || set.key === 'python.application.pyFiles') {
                set.placeholder = '请输入工作空间python包路径（只支持zip）';
              }
            });
          }
        });
      }).catch(() => {
        this.loading = false;
      });
    },
    formatValidateRange(value, type) {
      let formatValue = [];
      let tmpList = [];
      try {
        tmpList = JSON.parse(value);
      } catch (e) {
        tmpList = value.slice(1, value.length - 1).split(',');
      }
      tmpList.forEach((item) => {
        formatValue.push({
          name: item === 'BLANK' && type === 'pipeline.out.null.type' ? '空字符串' : item,
          value: item,
        });
      });
      return formatValue;
    },
    handleAdd(item, parent, cb) {
      setTimeout(() => {
        cb(true);
      }, 200);
    },
    handleDelete(item, parent, cb) {
      setTimeout(() => {
        cb(true);
      }, 200);
    },
    save() {
      console.log(this.fullTree)
      this.loading = true;
      this.checkValid();
      api.fetch('/configuration/saveFullTree', {
        fullTree: this.fullTree,
      }).then(() => {
        this.getAppVariable(this.activeMenu);
        this.unValidMsg = {};
        this.$Message.success(this.$t('message.linkis.save'));
      }).catch((err) => {
        this.loading = false;
        if (err.message) {
          let key = '';
          let msg = '';
          this.fullTree.forEach((item) => {
            if (item.settings) {
              item.settings.forEach((s) => {
                if (s.validateType === 'OFT' && Object.prototype.hasOwnProperty.call(s, 'validateRangeList')) {
                  delete s.validateRangeList;
                }
                if (err.message.indexOf(s.key) > -1) {
                  msg = s.description;
                  key = s.key;
                }
              });
            }
          });
          this.unValidMsg = { key, msg };
        }
      });
    },
    checkValid() {
      if (this.activeMenu === '全局设置') {
        this.fullTree.forEach((item) => {
          item.settings.forEach((set) => {
            if (set.key === 'bdp.dwc.yarnqueue.memory.max' || set.key === 'bdp.dwc.client.memory.max') {
              const unit = set.defaultValue[set.defaultValue.length - 1];
              if (set.configValue) {
                if (set.configValue[set.configValue.length - 1].toLowerCase() !== unit.toLowerCase()) {
                  set.configValue += unit;
                }
              }
            }
          });
        });
      }
    },
    toggleAdvance() {
      this.isAdvancedShow = !this.isAdvancedShow;
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
