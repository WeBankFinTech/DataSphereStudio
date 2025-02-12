<template>
  <div style="height: 100%; overflow-y:auto">
    <scriptView
      v-if="work.type=='workspaceScript' || work.type=='hdfsScript' || work.type=='node' || work.type=='historyScript'"
      :readonly="readonly"
      :current="current"
      :node="node"
      :work="work"/>
    <backgroundScriptView
      v-if="work.type=='backgroundScript'"
      :work="work"
      @remove-work="removeWork"/>
    <tbDetail
      :work="work"
      v-if="work.type=='tableDetails'"
    />
    <createTableView
      :work="work"
      v-if="work.type=='createTable'"/>
    <dbDetails
      v-if="work.type == 'dbDetails'"
      :work="work">
    </dbDetails>
    <spaceInfo
      v-if="work.type == 'ngSpaceInfo'"
      :work="work">
    </spaceInfo>
    <tagEdgeInfo
      v-if="work.type == 'tagEdgeInfo'"
      :work="work">
    </tagEdgeInfo>
    <iframeView
      v-if="work.type == 'iframe'"
      :url="work.url">
    </iframeView>
  </div>
</template>
<script>
import scriptView from './script/script.vue';
import backgroundScriptView from './script/backgroundScript.vue';
import tbDetail from './tableDetails/index.vue';
import createTableView from './createTable/index.vue';
import dbDetails from './dbDetails/index.vue';
import spaceInfo from './nebulaInfo/spaceInfo.vue';
import tagEdgeInfo from './nebulaInfo/tagEdgeInfo.vue';
import iframeView from './iframeView/index.vue';
export default {
  name: 'workbench',
  components: {
    scriptView,
    backgroundScriptView,
    tbDetail,
    createTableView,
    dbDetails,
    spaceInfo,
    tagEdgeInfo,
    iframeView
  },
  props: {
    work: Object,
    node: Object,
    current: {
      type: String,
      default: ''
    },
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  created() {
  },
  mounted() {
  },
  methods: {
    removeWork() {
      this.$emit('remove-work', this.work);
    },
  },
};
</script>
