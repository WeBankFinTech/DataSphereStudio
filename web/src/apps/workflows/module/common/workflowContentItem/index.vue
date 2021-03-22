<template>
  <div class="workflow-item">
    <slot></slot>
    <Row v-if="dataList.length > 0" class="content-item">
      <i-col
        class="project-item"
        :xs="12"
        :sm="8"
        :md="6"
        :xl="5"
        @click.native="goto(currentData, subitem)"
        v-for="(subitem, index) in cachedataList"
        :key="subitem.id"
      >
        <div class="project-main" :ref="`card${index}`">
          <div class="top-bar">
            <span class="project-title" :title="subitem.name">
              <SvgIcon style="font-size: 16px;" color="#5580eb" icon-class="base"/>
              {{subitem.orchestratorName}}
            </span>
            <div v-if="checkEditable(subitem)" class="menu-bar">
              <Button size="small" @click.stop>{{$t('message.workflow.workflowItem.admin')}}</Button>
              <ul class="menu-list">
                <li class="list-item" @click.stop="deleteProject(subitem)">{{$t('message.workflow.workflowItem.delete')}}</li>
                <li class="list-item" @click.stop="modify(currentData.id, subitem)">{{$t('message.workflow.workflowItem.config')}}</li>
                <!-- <li class="list-item" @click.stop="Export(subitem)">导出</li> -->
                <!-- <li v-if="!isPercent(subitem.orchestratorId)" class="list-item" @click.stop="publish(subitem)">{{$t('message.workflow.workflowItem.publish')}}</li> -->
              </ul>
            </div>
            <Button size="small" style="margin-right: 8px" @click.stop="Export(subitem)">{{ $t('message.orchestratorModes.export') }}</Button>
            <Button size="small" @click.stop="detail(currentData.id, subitem)">{{$t('message.workflow.workflowItem.viewVersion')}}</Button>
          </div>
          <div class="mid-bar" :title="subitem.description">
            {{subitem.description || $t('message.workflow.workflowItem.noDesc')}}
          </div>
          <div v-if="subitem.uses" class="bottom-bar">
            <span v-for="(item, i) in subitem.uses.split(',')" :key="i" :title="item" class="tag-item">{{item}}</span>
          </div>
        </div>
        <Tooltip
          v-if="isPercent(subitem.orchestratorId)"
          class="process-bar"
          style="width:100%"
          :content="$t('message.workflow.workflowItem.publishing')"
          placement="top"
        >
          <span class="queue-manager-status">
          </span>
        </Tooltip>
      </i-col>
    </Row>
    <div class="no-data" v-else>{{$t('message.workflow.workflowItem.nodata')}}</div>
    <Page
      v-if="dataList.length > 0 && pagination.size < dataList.length "
      class="page-bar"
      :total="dataList.length"
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
import mixin from '@/common/service/mixin';
export default {
  name: "WorkflowContentItem",
  props: {
    dataList: {
      type: Array,
      default: null
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
    publishingList: {
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
    };
  },
  mixins: [mixin],
  computed: {
    cachedataList() {
      return this.dataList.filter((item, index) => {
        return (
          (this.pagination.current - 1) * this.pagination.size <= index &&
          index < this.pagination.current * this.pagination.size
        );
      });
    },
    pageNumer() {
      return Math.ceil(this.dataList.length / this.pagination.size);
    }
  },
  watch: {
    pageNumer(val) {
      if (val < this.pagination.current && val !== 0) {
        this.pagination.current = val;
      }
    }
  },
  methods: {
    checkEditable(item) {
      // 编排权限由后台的priv字段判断，1-查看， 2-编辑， 3-发布
      if ([2, 3].includes(item.priv)) {
        return true
      } else {
        return false
      }
    },
    modify(classifyId, project) {
      this.$emit("modify", classifyId, project);
    },
    goto(item, subItem) {
      // 被锁住时候不能跳转
      if(subItem.flowEditLockExist) return this.$Message.warning(this.$t("message.workflow.workflowItem.lockTip"))
      // 在发布中不允许跳转
      if (this.isPercent(subItem.orchestratorId)) return this.$Message.warning(this.$t("message.workflow.workflowItem.noView"));
      this.$emit("goto", subItem);
    },
    detail(classifyId, project) {
      this.$emit("detail", classifyId, project);
    },
    deleteProject(project) {
      this.$emit("delete", project);
    },
    copy(classifyId, project) {
      this.$emit("copy", classifyId, project);
    },
    publish(project) {
      this.$emit("publish", project);
    },
    isPercent(id) {
      return this.publishingList.includes(id)
    },
    pageChange(page) {
      this.pagination.current = page;
    },
    pageSizeChange(size) {
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
    },
    Export(project) {
      this.$emit("Export", project);
    }
  }
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
