<template>
  <div>
    <slot></slot>
    <div class="content-item">
      <div
        class="project-item project-header"
        @click="addProject">
        <Icon class="add-icon" type="md-add" size="20">
        </Icon>
        <span>{{$t('message.Project.createProject')}}</span>
      </div>
      <template  v-if="dataList.length > 0">
        <div
          class="project-item project-card-item"
          @click="goto(currentData, subitem)"
          v-for="(subitem, index) in cachedataList"
          :key="subitem.id"
        >
          <div class="project-main" :ref="`card${index}`">
            <div class="top-bar">
              <span class="project-title" :title="subitem.name">
                <SvgIcon style="font-size: 16px;" color="#5580eb" icon-class="base"/>
                {{subitem.name}}
              </span>
              <div v-if="subitem.canWrite() || checkCopyable(subitem, getUserName())" class="menu-bar">
                <Button size="small" @click.stop>{{ $t('message.workspace.Management') }}</Button>
                <ul class="menu-list">
                  <li class="list-item" v-if="subitem.canDelete()" @click.stop="deleteProject(subitem)">{{ $t('message.workspace.Delete') }}</li>
                  <li class="list-item" v-if="subitem.canWrite()" @click.stop="modify(currentData.id, subitem)">{{ $t('message.workspace.Configuration') }}</li>
                  <li class="list-item" v-if="$APP_CONF.copy_project_enable && checkCopyable(subitem, getUserName())"  @click.stop="copy(currentData.id, subitem)">{{ $t('message.workspace.Copy') }}</li>
                  <!-- <li class="list-item" @click.stop="publish(currentData.id, subitem)">发布</li> -->
                </ul>
              </div>
            </div>
            <div class="mid-bar" :title="subitem.description">
              {{subitem.description || $t('message.workflow.workflowItem.noDesc')}}
            </div>
            <div v-if="subitem.business" class="bottom-bar">
              <span v-for="(item, i) in subitem.business.split(',')" :key="i" :title="item" class="tag-item">{{item}}</span>
            </div>
          </div>
          <Tooltip
            v-if="!hidePublishAndcopy && isPercent(subitem.id)"
            style="width:100%"
            :content="$t('message.workflow.workflowItem.publishing')"
            placement="top"
          >
            <span class="queue-manager-status">

            </span>
          </Tooltip>
        </div>

      </template>
      <div class="no-data" v-else>{{$t('message.workflow.workflowItem.nodata')}}</div>
    </div>
    <Page
      v-if="typelist > 0 && pagination.size < dataList.length "
      class="page-bar"
      :total="typelist"
      show-sizer
      :current="pagination.current"
      :page-size="pagination.size"
      :page-size-opts="pagination.opts"
      @on-change="pageChange"
      @on-page-size-change="pageSizeChange"
    ></Page>
  </div>
</template>
<script>
import mixin from '@dataspherestudio/shared/common/service/mixin';
import storage from '@dataspherestudio/shared/common/helper/storage';
import {canCreate} from '@dataspherestudio/shared/common/config/permissions.js';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
export default {
  name: "WorkflowContentItem",
  props: {
    dataList: {
      type: Array,
      default: null
    },
    hideButtonBar: {
      type: Boolean,
      default: true
    },
    hidePublishAndcopy: {
      type: Boolean,
      default: true
    },
    currentData: {
      type: Object,
      default() {
        return {};
      }
    },
    readonly: {
      type: Boolean,
      default: false
    },
    precentList: {
      type: Array,
      default: () => []
    },
    tagProp: {
      type: String,
      default: "business"
    },
    applicationAreaMap: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      pagination: {
        size: 10,
        current: 1,
        total: 0,
        opts: [10, 30, 45, 60]
      },
      isToolbarShow: false,
      cardShowNum: 4,
      canCreateProject: false
    };
  },
  mixins: [mixin],
  computed: {
    cachedataList() {
      return this.dataList.filter(item => item.showInCardList !== false).filter((item, index) => {
        return (
          (this.pagination.current - 1) * this.pagination.size <= index &&
          index < this.pagination.current * this.pagination.size
        );
      });
    },
    typelist() {
      return this.dataList.filter(item => item.showInCardList !== false).length
    }
  },
  watch: {
    dataList() {
      this.pagination.current = 1
    }
  },
  mounted() {
    this.checkCreate();
    eventbus.on('workspace.change', this.checkCreate);
  },
  beforeDestroy() {
    eventbus.off('workspace.change', this.checkCreate);
  },
  methods: {
    addProject() {
      this.$emit('addProject');
    },
    // 选择操作项
    selectAction(name) {
      console.log(name, 'name')
    },
    checkCreate(roles){
      const workspaceRoles = roles || storage.get(`workspaceRoles`) || [];
      if (canCreate(workspaceRoles)) {
        this.canCreateProject = true;
      } else {
        this.canCreateProject = false;
      }
    },
    checkCopyable(item, name) {
      if (item.releaseUsers && item.releaseUsers.length > 0) {
        return item.releaseUsers.some(e => e === name);
      } else {
        return false;
      }
    },
    modify(classifyId, project) {
      this.$emit("modify", classifyId, project);
    },
    goto(item, subItem) {
      if(this.hidePublishAndcopy && subItem.latestVersion.flowEditLockExist) return this.$Message.warning(this.$t("message.workflow.workflowItem.lockTip"))
      if (this.isPercent(subItem.id))
        return this.$Message.warning(this.$t("message.workflow.workflowItem.noView"));
      this.$emit("goto", item, subItem);
    },
    deleteProject(project) {
      this.$emit("delete", project);
    },
    copy(classifyId, project) {
      this.$emit("copy", classifyId, project);
    },
    isPercent(id) {
      let flag = false;
      this.precentList.map(item => {
        if (item.id === id) {
          flag = true;
        }
      });
      return flag;
    },
    percentVlaue(id) {
      let value = "";
      this.precentList.map(item => {
        if (item.id === id) {
          value = item.percent;
        }
      });
      return value;
    },
    pageChange(page) {
      this.pagination.current = page;
    },
    pageSizeChange(size) {
      this.pagination.current = 1
      this.pagination.size = size;
    },
    // showResourceView(classifyId, project) {
    //     this.$emit('showResourceView', classifyId, project);
    // },
    handleToolbarShow(item, flag) {
      this.$set(item, "isToolbarShow", flag);
    },
    tagInfo(subitem) {
      if (!Array.isArray(subitem[this.tagProp])) {
        return subitem[this.tagProp];
      }
      return subitem[this.tagProp].join(", ");
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.content-item {
    margin: 15px 0px 25px 0px;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    grid-row-gap: 20px;
    grid-column-gap: 20px;
    .project-list-ul {
        padding: 10px 20px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        flex-wrap: wrap;
    }
    .project-item {
        min-height: 150px;
        @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
        border-radius: 2px;
        border: 1px solid #DEE4EC;
        @include border-color($border-color-base, $dark-border-color-base);
        padding: $padding-25;
        cursor: pointer;
        &:hover {
          box-shadow: 0 2px 8px 0 $shadow-color;
        }
        .project-main {
          height: 100%;
          display: flex;
          flex-direction: column;
          .top-bar {
            width: 100%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            .project-title {
              font-size: $font-size-large;
              margin-right: 30px;
              font-weight: 600;
              white-space: nowrap;
              text-overflow: ellipsis;
              overflow: hidden;

              // color: $text-title-color;
              @include font-color($workspace-title-color, $dark-workspace-title-color);
              letter-spacing: 0;
            }
            .menu-bar {
              position: relative;
              flex-basis: 40px;
              /deep/.ivu-btn {
                @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
                @include border-color($border-color-base, $dark-border-color-base);
                @include font-color($light-text-color, $dark-text-color);
              }
              .menu-list {
                display: none;
                position: absolute;
                bottom: 23px;
                left: -8px;
                border-radius: 4px;
                padding: 5px 0;
                box-shadow: 0 1px 6px rgba(0,0,0,.2);
                z-index: 999;
                @include bg-color($menu-list-bg-color, $dark-menu-list-bg-color);
                cursor: pointer;
                .list-item {
                  width: 60px;
                  padding: 5px 8px;
                  text-align: center;
                  @include font-color($light-text-color, $dark-text-color);
                  &:hover {
                    @include bg-color($hover-color-base, $dark-hover-color-base);
                  }
                }
              }
              &:hover {
                .menu-list {
                  display: inline;
                }
              }
            }
          }
          .mid-bar {
            width: 100%;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
            font-size: $font-size-14;
            @include font-color($light-text-desc-color, $dark-text-desc-color);
            margin: 15px 0;
          }
          .bottom-bar {
            margin-bottom: 0;
            display: flex;
            justify-content: flex-start;
            align-items: center;
            flex-wrap: wrap;
            height: 23px;
            overflow: hidden;
            width: 100%;
            .tag-item {
              // color: $text-desc-color;
              @include font-color($light-text-color, $dark-text-color);
              padding: 2px 10px;
              margin-right: 10px;
              border-radius: 11px;
              @include bg-color(#F3F3F3, $dark-base-color);
              border: 1px solid $border-color-base;
              @include border-color($border-color-base, $dark-border-color-base);
              white-space: nowrap;
              text-overflow: ellipsis;
              overflow: hidden;

              font-size: 12px;
            }
          }
        }
    }
    .project-header {
      text-align: center;
      font-size: $font-size-large;
      @include font-color($light-text-color, $dark-text-color);
      line-height: 102px;
      border: 1px dashed  #dcdee2;
      @include border-color($border-color-base, $dark-border-color-base);
      // background: #F8F9FC;
      @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
      .add-icon {
        margin-top: -2px;
        margin-right: 5px;
      }
    }
}


.page-bar {
    padding: 0 30px;
    margin-bottom: 30px;
}

.no-data {
    text-align: center;
    padding: 20px;
}

.queue-manager-status {
    position: $relative;
    width: 100%;
    height: 10px;
    display: flex;
    align-items: center;
    font-size: 10px;
    border-radius: 10px;
    background-image: linear-gradient(60deg, transparent 0rem, transparent 0.8rem, rgb(88, 175, 251) 0.8rem, rgb(88, 175, 251) 1.6rem, transparent 1.6rem, transparent 2.4rem, rgb(88, 175, 251) 2.4rem);
    background-size: 21px 27px;
    box-shadow: 1px 1px 5px rgba(88, 175, 251, .6);
    -webkit-animation: process 800ms infinite linear;
    animation: process 800ms infinite linear;
    // background-color: $background-color-base;
    @include bg-color($workspace-background, $dark-workspace-background);
    &:after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        height: 100%;
        border-radius: 10px;
        background-image: linear-gradient(to bottom, rgba(88, 175, 251, .4), rgba(88, 175, 251, .4) 15%, transparent 60%, rgba(88, 175, 251, .4));
    }
    .queue-manager-status-busy {
        position: $absolute;
        left: 0;
        background: $success-color;
        height: 100%;
        border-radius: 10px;
        z-index: 1;
    }
    .queue-manager-status-idle {
        background: $success-color;
        height: 100%;
        border-radius: 10px;
        position: $absolute;
        z-index: 0;
    }
    .queue-manager-status-label {
        position: $absolute;
        right: 6px;
        color: $tooltip-color;
    }
}


/* 动画 */

@-webkit-keyframes process {
    0% {
        background-position: 0 0;
    }
    100% {
        background-position: 20px 0;
    }
}

@keyframes process {
    0% {
        background-position: 0 0;
    }
    100% {
        background-position: 20px 0;
    }
}

</style>
