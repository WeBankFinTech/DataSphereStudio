<template>
  <div class="nav-menu">
    <div class="nav-menu-left">
      <template v-for="(item, index) in menuList">
        <div
          class="nav-menu-left-item"
          :key="index"
          :class="{'actived':current && item.id === current.id}"
          @mouseover.stop="onMouseOver(item)">
          <!-- <p class="nav-menu-left-sub-title">{{item.subTitle}}</p> -->
          <p class="nav-menu-left-main-title">{{item.title}}</p>
          <Icon
            size="14"
            type="ios-arrow-forward"
            class="nav-menu-left-icon"
            v-if="item.appInstances"></Icon>
        </div>
      </template>
    </div>
    <div
      class="nav-menu-right"
      v-if="current">
      <div
        class="nav-menu-right-item"
        :key="current.id">
        <div class="nav-menu-right-item-title">{{current.title}}</div>
        <div
          v-for="item in current.appInstances"
          class="nav-menu-right-item-child"
          :key="item.title"
          @click.stop="handleClick(item)">
          <i
            class="nav-menu-right-item-icon"
            :class="iconSplit(item.icon)[0]" 
            :style="`color: ${iconSplit(item.icon)[1]}`"
          ></i>
          <span>{{item.title}}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import api from '@/js/service/api';
import storage from '@/js/helper/storage';
export default {
  data() {
    return {
      menuList: [
        {
          "id": 1,
          "title": "应用开发",
          "order": null,
          "appInstances": [
            {
              "id": 1,
              "title": "工作流开发",
              "description": "工作流开发是微众银行微数域(WeDataSphere)打造的数据应用开发工具，以任意桥(Linkis)做为内核，将满足从数据交换、脱敏清洗、分析挖掘、质量检测、可视化展现、定时调度到数据输出等数据应用开发全流程场景需求。",
              "labels": "工作流,数仓开发",
              "accessButton": "进入工作流开发",
              "accessButtonUrl": "/project",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "/workflow",
              "name": "workflow",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": true
            },
            {
              "id": 2,
              "title": "StreamSQL开发",
              "description": "实时应用开发是微众银行微数域(WeDataSphere)、Boss直聘大数据团队 和 中国电信天翼云大数据团队 社区联合共建的流式解决方案，以 Linkis 做为内核，基于 Flink Engine 构建的批流统一的 Flink SQL，助力实时化转型。",
              "labels": "流式,实时",
              "accessButton": "联合共建中",
              "accessButtonUrl": "/streamSQL",
              "manualButton": "相关资讯",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "/streamSQL/project",
              "name": "streamSQL",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": false
            },
            {
              "id": 3,
              "title": "数据服务开发",
              "description": "数据服务是微众银行微数域(WeDataSphere)与 艾佳生活大数据团队 社区联合共建的统一API服务，以 Linkis 和 DataSphere Studio 做为内核，提供快速将 Scriptis 脚本生成数据API的能力，协助企业统一管理对内对外的API服务。",
              "labels": "API,数据服务",
              "accessButton": "联合共建中",
              "accessButtonUrl": "/dataService",
              "manualButton": "相关资讯",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "/dataService/project",
              "name": "dataService",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": false
            }]
        },
        {
          "id": 2,
          "title": "数据分析",
          "order": null,
          "appInstances": [
            {
              "id": 4,
              "title": "Scriptis",
              "description": "Scriptis是微众银行微数域(WeDataSphere)打造的一站式交互式数据探索分析工具，以任意桥(Linkis)做为内核，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。",
              "labels": "脚本开发,IDE",
              "accessButton": "进入Scriptis",
              "accessButtonUrl": "/home",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "/home",
              "name": "scriptis",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": true
            },
            {
              "id": 5,
              "title": "Visualis",
              "description": "Visualis是基于宜信开源项目Davinci开发的数据可视化BI工具，以任意桥(Linkis)做为内核，支持拖拽式报表定义、图表联动、钻取、全局筛选、多维分析、实时查询等数据开发探索的分析模式，并做了水印、数据质量校验等金融级增强。",
              "labels": "可视化,报表",
              "accessButton": "进入Visualis",
              "accessButtonUrl": "http://42.123.106.20:8088/dss/visualis/#/projects ",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "http://42.123.106.20:8088/dss/visualis/#/project/${projectId}",
              "name": "visualis",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": true
            }]
        },
        {
          "id": 3,
          "title": "生产运维",
          "order": null,
          "appInstances": [
            {
              "id": 6,
              "title": "Schedulis",
              "description": "Scriptis是微众银行微数域(WeDataSphere)打造的一站式交互式数据探索分析工具，以任意桥(Linkis)做为内核，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。",
              "labels": "调度,工作流",
              "accessButton": "进入Schedulis",
              "accessButtonUrl": "http://127.0.0.1:8091/homepage",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "http://127.0.0.1:8091/manager?project=${projectName}",
              "name": "schedulis",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": true
            },
            {
              "id": 7,
              "title": "应用运维中心",
              "description": "Scriptis是微众银行微数域(WeDataSphere)打造的一站式交互式数据探索分析工具，以任意桥(Linkis)做为内核，提供多种计算存储引擎(如Spark、Hive、TiSpark等)、Hive数据库管理功能、资源(如Yarn资源、服务器资源)管理、应用管理和各种用户资源(如UDF、变量等)管理的能力。",
              "labels": "生产,运维",
              "accessButton": "进入应用运维中心",
              "accessButtonUrl": "/devops",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "/devops/project",
              "name": "devops",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": false
            }]
        },
        {
          "id": 4,
          "title": "数据质量",
          "order": null,
          "appInstances": [
            {
              "id": 8,
              "title": "Qualitis",
              "description": "Qualitis是一套金融级、一站式的数据质量管理平台，提供了数据质量模型定义，数据质量结果可视化、可监控等功能，并用一整套统一的流程来定义和检测数据集的质量并及时报告问题。",
              "labels": "生产,运维",
              "accessButton": "进入Qualitis",
              "accessButtonUrl": "http://127.0.0.1:8090/#/dashboard",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "http://127.0.0.1:8090/#/projects/list?id=${projectId}&flow=true",
              "name": "qualitis",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": true
            },
            {
              "id": 9,
              "title": "Exchangis",
              "description": "Exchangis是一个轻量级的、高扩展性的数据交换平台，支持对结构化及无结构化的异构数据源之间的数据传输，在应用层上具有数据权限管控、节点服务高可用和多租户资源隔离等业务特性，而在数据层上又具有传输架构多样化、模块插件化和组件低耦合等架构特点。",
              "labels": "生产,运维",
              "accessButton": "进入Exchangis",
              "accessButtonUrl": "http://42.123.106.20:9503",
              "manualButton": "用户手册",
              "manualButtonUrl": "https://www.baidu.com",
              "projectUrl": "http://42.123.106.20:19503",
              "name": "exchangis",
              "icon": "fi-scriptis|rgb(102, 102, 255)",
              "order": null,
              "active": true
            }]
        }]
      ,
      current: null
    }
  },
  watch: {
    '$route'(newValue) {
      const workspaceId = this.$route.query.workspaceId;
      const list = storage.get(`application-${workspaceId}`);
      if(list) {
        this.menuList = list
      }else {
        if(workspaceId){
          api.fetch(`dss/workspaces/${workspaceId}/applications`, 'get').then(data=>{
            this.menuList = data.applications || [];
            storage.set(`application-${workspaceId}`, this.menuList);
          })
        } 
      }
    }
  },
  methods: {
    iconSplit(icon){
      if(icon){
        return icon.split('|')
      }
      return ['','']
    },
    onMouseOver(item) {
      if (item.appInstances) {
        return this.current = item;
      }
      this.current = null;
    },
    handleClick(child) {
      if (child.name) {
        let query = this.$route.query;
        this.gotoCommonIframe(child.name, query);
      } else {
        this.$Message.warning(this.$t('message.constants.warning.comingSoon'));
      }
      this.$emit('click', child);
    }
  },
}
</script>
<style lang="scss" src="./index.scss">
</style>
