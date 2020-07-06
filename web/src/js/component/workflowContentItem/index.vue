<template>
  <div>
    <h3 class="item-header">
      <span>{{ currentData.name }}</span>
    </h3>
    <slot></slot>
    <Row
      class="content-item">
      <i-col
        :xs="12" :sm="8" :md="6" :lg="4"
        class="project-item ">
        <div class="project-add" @click="add">
          <Icon
            type="ios-add"
            class="icon-blod"
            size="60"
          ></Icon>
          <span>{{$t(`message.${source}.create${source}`)}}</span>
        </div>
      </i-col>
      <i-col
        class="project-item"
        :xs="12" :sm="8" :md="6" :lg="4"
        @click.native="goto(currentData, subitem)"
        v-for="(subitem, index) in cachedataList"
        :key="subitem.id"
      >
        <div
          class="project-main"
          :ref="`card${index}`">
          <div>
            <div
              class="tool-bar">
              <div
                @mouseleave="handleToolbarShow(subitem, false)"
                @mouseover="handleToolbarShow(subitem, true)"
                class="tool-bar-more">
                <span v-if="subitem.isToolbarShow">
                  <!-- <Icon
                    type="ios-cloud-upload"
                    size="20"
                    style="margin-right:4px"
                    :title="$t('message.workflowItem.resource')"
                    v-if="!hidePublishAndcopy "
                    @click.stop="showResourceView(currentData.id, subitem)"
                  ></Icon> -->
                  <Icon
                    type="ios-map"
                    size="20"
                    :title="$t('message.workflowItem.viewVersion')"
                    @click.stop="detail(currentData.id, subitem)"
                  ></Icon>
                  <Icon
                    type="ios-copy"
                    size="20"
                    :title="$t('message.newConst.copy')"
                    v-if="!hidePublishAndcopy"
                    @click.stop="copy(currentData.id, subitem)"
                  ></Icon>
                  <Icon
                    type="ios-cog"
                    size="20"
                    :title="$t('message.workflowItem.config')"
                    v-if="!readonly"
                    @click.stop="modify(currentData.id, subitem)"></Icon>
                  <Icon
                    type="ios-trash"
                    size="20"
                    :title="$t('message.newConst.delete')"
                    v-if="!readonly"
                    @click.stop="deleteProject(subitem)"></Icon>
                </span>
                <Icon
                  v-else
                  type="ios-menu"
                  size="20"
                  :title="$t('message.workflowItem.action')"
                ></Icon>
              </div>
            </div>
          </div>
          <div
            class="product-app middle"
            :class="[{'high': subitem.business && subitem.business.length && subitem.applicationArea !== null}]">
            <div
              v-if="subitem.product"
              class="product">
              <img class="product-bgc" src="./img/product.svg">
              <span>{{subitem.product}}</span>
            </div>
          </div>
          <div
            class="applicationArea low"
            :class="[{'middle': subitem.business && subitem.business.length}]">
            <div
              v-if="subitem.applicationArea !== null"
              class="app">
              <img class="app-bgc" src="./img/app.svg">
              <span>{{applicationAreaMap[subitem.applicationArea]}}</span>
            </div>
          </div>
          <div
            v-if="subitem[tagProp] && subitem[tagProp].length"
            class="project-main-business low">
            <Icon type="ios-pricetags" size="16" />
            {{ tagInfo(subitem) }}
          </div>
          <div
            class="icon-bar"
            v-if="!hideButtonBar">
            <span
              class="icon-marsk"
              v-if="!hidePublishAndcopy">
              <Icon
                :type="subitem.notPublish ? 'md-star-outline' : 'md-star'"
                class="status-icon"
                size="16"></Icon>
              {{ subitem.notPublish ? $t('message.project.unpublish') : $t('message.project.published') }}
            </span>
            <span
              class="icon-marsk"
              v-if="hidePublishAndcopy">
              <Icon
                :type="readonly ? 'md-book' : 'md-brush'"
                class="status-icon"
                size="16"></Icon>
              {{ readonly ? $t('message.workflowItem.readonly') : $t('message.workflowItem.editor') }}
            </span>
          </div>
        </div>
        <div class="project-item-info">
          <div class="project-main-title">
            {{subitem.name}}
            <span class="project-main-desc version-bar" :title="subitem.latestVersion.version"> {{subitem.latestVersion ? `${subitem.latestVersion.version}` : ''}} </span>
          </div>
          <div class="project-main-desc" >{{subitem.description || $t('message.workflowItem.noDesc')}}</div>
          <!-- <div class="project-main-desc" v-else>
            <span v-if="subitem.applicationArea">{{ subitem.applicationArea }}</span>
            <span v-if="subitem.product">{{ subitem.applicationArea ? `: ${subitem.product}` : subitem.product}}</span>
          </div> -->
          <Button
            class="icon-bar-publish"
            v-if="!hidePublishAndcopy"
            size="small"
            :disabled="isPercent(subitem.id)"
            @click.stop="publish(currentData.id, subitem)">{{$t('message.workflowItem.publish')}}</Button>
        </div>
        <Tooltip
          v-if="!hidePublishAndcopy && isPercent(subitem.id)"
          style="width:100%"
          :content="`发布中`"
          placement="top">
          <span class="queue-manager-status">
          <!-- <span
                class="queue-manager-status-busy"
                :style="{'width': percentVlaue(subitem.id)}"
                :title="percentVlaue(subitem.id)">
                <i class="queue-manager-status-label"></i>
              </span> -->
          </span>
        </Tooltip>
      </i-col>

    </Row>
    <!-- <div
      class="no-data"
      v-else>{{$t('message.workflowItem.nodata')}}</div> -->
    <Page
      v-if="dataList.length > 0 && pagination.size < dataList.length "
      class="page-bar"
      :total="dataList.length"
      show-sizer
      :current="pagination.current"
      :page-size="pagination.size"
      :page-size-opts="pagination.opts"
      @on-change="pageChange"
      @on-page-size-change="pageSizeChange"></Page>
  </div>
</template>
<script>
import api from '@/js/service/api';
export default {
  name: 'WorkflowContentItem',
  props: {
    dataList: {
      type: Array,
      default: null,
    },
    hideButtonBar: {
      type: Boolean,
      default: true,
    },
    hidePublishAndcopy: {
      type: Boolean,
      default: true,
    },
    currentData: {
      type: Object,
      default() {
        return {};
      },
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    precentList: {
      type: Array,
      default: () => [],
    },
    tagProp: {
      type: String,
      default: 'business',
    },
    source: {
      type: String,
      default: 'Project'
    }
  },
  data() {
    return {
      pagination: {
        size: 15,
        current: 1,
        total: 0,
        opts: [15, 30, 45, 60],
      },
      isToolbarShow: false,
      cardShowNum: 4,
      applicationAreaMap: []
    };
  },
  computed: {
    cachedataList() {
      return this.dataList.filter((item, index) => {
        return (this.pagination.current - 1) * this.pagination.size <= index && index < this.pagination.current * this.pagination.size;
      });
    },
    pageNumber() {
      return Math.ceil(this.dataList.length / this.pagination.size);
    },
  },
  watch: {
    pageNumber(val) {
      if (val < this.pagination.current && val !== 0) {
        this.pagination.current = val;
      }
    },
  },
  mounted() {
    api.fetch('/dss/listApplicationAreas', 'get').then((res) => {
      this.applicationAreaMap = res.applicationAreas
    })
  },
  methods: {
    modify(classifyId, project) {
      this.$emit('modify', classifyId, project);
    },
    goto(item, subItem) {
      if (this.isPercent(subItem.id)) return this.$Message.warning(this.$t('message.workflowItem.noView'));
      this.$emit('goto', item, subItem);
    },
    detail(classifyId, project) {
      this.$emit('detail', classifyId, project);
    },
    deleteProject(project) {
      this.$emit('delete', project);
    },
    copy(classifyId, project) {
      this.$emit('copy', classifyId, project);
    },
    publish(classifyId, project) {
      this.$emit('publish', classifyId, project);
    },
    add() {
      this.$emit('add');
    },
    isPercent(id) {
      let flag = false;
      this.precentList.map((item) => {
        if (item.id === id) {
          flag = true;
        }
      });
      return flag;
    },
    percentVlaue(id) {
      let value = '';
      this.precentList.map((item) => {
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
      this.pagination.size = size;
    },
    // showResourceView(classifyId, project) {
    //     this.$emit('showResourceView', classifyId, project);
    // },
    handleToolbarShow(item, flag) {
      this.$set(item, 'isToolbarShow', flag);
    },
    tagInfo(subitem) {
      if (!Array.isArray(subitem[this.tagProp])) {
        return subitem[this.tagProp]
      }
      return subitem[this.tagProp].join(', ')
    }
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
