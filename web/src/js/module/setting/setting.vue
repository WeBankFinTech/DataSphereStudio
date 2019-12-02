<template>
  <div
    class="setting"
    :style="{'height': height + 'px'}">
    <Menu
      :active-name="$t('message.setting.global')"
      :open-names="['1', '2']"
      @on-select="getAppVariable"
      ref="settingMenu"
      class="setting-menu">
      <MenuItem :name="$t('message.setting.global')"><Icon type="ios-paper" />{{ $t('message.setting.globalSetting') }}</MenuItem>
      <Submenu
        :name="menu.name"
        v-for="(menu, index) in menuList"
        :key="index">
        <template slot="title">
          <Icon :type="menu.icon" />
          {{ menu.title }}
        </template>
        <MenuItem
          v-for="(item, index2) in menu.children"
          :name="item.name"
          :key="index2"
        >{{ item.title }}</MenuItem>
      </Submenu>
    </Menu>
    <div
      v-if="fullTree && fullTree.length"
      class="setting-content">
      <div class="setting-content-header">
        <Button
          @click="toggleAdvance">{{ isAdvancedShow ? $t('message.setting.hide') : $t('message.setting.show') }}{{ $t('message.setting.advancedSetting') }}</Button>
        <Button
          :loading="loading"
          type="primary"
          class="setting-content-btn"
          @click="save">{{ $t('message.constants.save') }}</Button>
      </div>
      <div
        class="setting-content-variable"
        :style="{'height': height - 40 + 'px'}">
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
import api from '@/js/service/api';
import variable from '@/js/component/variable';
export default {
  name: 'Setting',
  components: {
    variable,
  },
  data() {
    return {
      menuList: [{
        name: '2',
        title: this.$t('message.setting.dataDev'),
        icon: 'ios-people',
        children: [{
          name: 'IDE-spark',
          title: 'Spark',
        }, {
          name: 'IDE-hive',
          title: 'Hive',
        },
        {
          name: 'IDE-python',
          title: 'Python',
        }, {
          name: 'IDE-pipeline',
          title: 'PipeLine',
        }],
      }, {
        name: '3',
        title: 'Visualis',
        icon: 'md-analytics',
        children: [{
          name: 'visualis-spark',
          title: 'Spark',
        }],
      }],
      activeMenu: '',
      fullTree: [],
      loading: false,
      unValidMsg: {},
      isAdvancedShow: false,
      height: 0,
    };
  },
  mounted() {
    this.height = this.$route.query.height;
    this.$nextTick(() => {
      this.$refs.settingMenu.updateActiveName();
      this.getAppVariable('全局');
    });
  },
  methods: {
    getAppVariable(type) {
      this.activeMenu = type;
      const IDE = 'IDE';
      const VSBI = 'visualis';
      let appName = '通用设置';
      let creator = '通用设置';
      if (type.match(IDE)) {
        creator = IDE;
        appName = type.slice(IDE.length + 1, type.length);
      } else if (type.match(VSBI)) {
        creator = VSBI;
        appName = type.slice(VSBI.length + 1, type.length);
      }
      api.fetch('/configuration/getFullTreesByAppName', {
        appName,
        creator,
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
      this.loading = true;
      this.checkValid();
      api.fetch('/configuration/saveFullTree', {
        fullTree: this.fullTree,
      }).then(() => {
        this.getAppVariable(this.activeMenu);
        this.unValidMsg = {};
        this.$Message.success(this.$t('message.constants.save'));
      }).catch((err) => {
        this.loading = false;
        if (err.message) {
          let key = '';
          let msg = '';
          this.fullTree.forEach((item) => {
            if (item.settings) {
              item.settings.forEach((s) => {
                if (s.validateType === 'OFT' && s.hasOwnProperty('validateRangeList')) {
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
      if (this.activeMenu === '全局') {
        this.fullTree.forEach((item) => {
          item.settings.forEach((set) => {
            if (set.key === 'bdp.dwc.yarnqueue.memory.max' || set.key === 'bdp.dwc.client.memory.max') {
              const unit = set.defaultValue[set.defaultValue.length - 1];
              if (set.value) {
                if (set.value[set.value.length - 1].toLowerCase() !== unit.toLowerCase()) {
                  set.value += unit;
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
