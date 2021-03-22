<template>
  <div class="page">
    <LeftNav @changeSetType="chagneRightVisual"
      @saveDirSuccess="saveDirSuccess"
      @saveNodeSuccess="saveNodeSuccess"
      @initTree="initTree"
      @newMenuSuccess="newMenuSuccess"/>
    <div class="page-right">
      <component
        :is="componentName"
        :currentMenuId="currentMenuId"
        :flag="flag"
        :parentId="parentId"
        :nodeData="nodeData"
        :saveDir="saveDir"
        :saveNode="saveNode"
        :newMenu="newMenu"
      />
    </div>
  </div>
</template>
<script>
import bus from '../../components/bus'
import LeftNav from '../../components/portalSetting/leftnav';
import AddMenu from '../../components/portalSetting/addMenu';
import CommonSet from '../../components/portalSetting/commonSet';
import EditDirectory from '../../components/portalSetting/editedDirectory'
import EditNode from '../../components/portalSetting/editedNode';
export default {
  components: {
    LeftNav,
    AddMenu,
    CommonSet,
    EditDirectory,
    EditNode,
  },
  data() {
    return {
      currentMenuId: '',
      flag: 0,
      nodeData: {},
      parentId: '',
      saveDir: null,
      saveNode: false,
      newMenu: null,
      type: 'common',//common:门户的外观设置，'addMenu':菜单设置
    }
  },
  computed: {
    componentName() {
      let currentComponent = '';
      switch (this.type) {
        case 'common': currentComponent = CommonSet;
          break;
        case 'menu': currentComponent = AddMenu;
          break;
        case 'directory': currentComponent = EditDirectory;
          break;
        case 'node': currentComponent = EditNode;
          break;
      }
      return currentComponent
    }
  },
  created() {
    bus.$on('editMenu', (data) => {
      if (data.currentData.menuType === 'directory') {
        this.type = 'directory'
      } else {
        this.type = 'node'
      }
      // this.menuType = data.
      this.nodeData = data.currentData;
      this.flag = data.flag;
      this.currentMenuId = this.nodeData.id;
    })
  },
  methods: {
    //通过点击左侧的图标进行相应的展示右边的内容
    chagneRightVisual(value) {
      this.type = value.type;
      this.flag = value.flag;
      this.currentMenuId = value.id;
      this.parentId = value.parentId;
      this.newMenu = false;
    },
    saveDirSuccess(value) {
      this.currentMenuId = null;
      this.saveDir = value;
    },
    saveNodeSuccess(value) {
      this.currentMenuId = null;
      this.saveNode = value;
    },
    initTree() {
      this.saveDir = false;
      this.saveNode = false;
    },
    newMenuSuccess(value) {
      this.newMenu = value;
      // 回复状态
      setTimeout(() => {
        this.newMenu = false
      }, 500)
    }
  }
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
