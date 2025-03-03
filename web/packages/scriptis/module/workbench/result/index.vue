<template>
  <div style="width:100%;height:100%">
    <div
      v-if="loading"
      class="loading-text">
      请耐心等待，数据正在加载中... <Spin />
    </div>
    <result
      v-if="script.result.path || script.resultList.length > 0"
      ref="result"
      getResultUrl="filesystem"
      :script="script"
      :dispatch="dispatch"
      :script-view-state="scriptViewState"
      @loadDataDone="loading=false"
      @on-set-change="changeResultSet"/>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api'
import result from '@dataspherestudio/shared/components/consoleComponent/result.vue';
import SUPPORTED_LANG_MODES from '@dataspherestudio/shared/common/config/scriptis';
import { find, debounce } from 'lodash'
export default {
  name: 'script-result',
  components: {
    result,
  },
  data() {
    return {
      scriptViewState: {
        topPanelHeight: 450,
        bottomContentHeight: '500',
        topPanelFull: false,
        showPanel: 'result',
        bottomPanelFull: false,
        cacheLogScroll: 0,
      },
      script: {
        id: '',
        fileName: this.$route.query.fileName || '',
        runType: this.getRunType().runType || 'hql',
        scriptType: this.getRunType().scriptType || 'hive',
        data: '',
        oldData: '',
        result: {
          headRows: [],
          bodyRows: [],
          type: 'RES_EMPTY',
          total: 0,
          cache: {},
        },
        resultList: [],
        resultSet: 0,
        steps: [],
        progress: {},
        params: {},
        readOnly: false
      },
      loading: false
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.getHeight);
  },
  mounted() {
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.getHeight);
    this.scriptViewState.bottomContentHeight = this.$el.clientHeight;
    this.script.id = this.$route.query.taskId || Date.now();
    if (this.$route.query.parentPath) {
      api.fetch(
      `/filesystem/getDirFileTrees`, {
        path: this.$route.query.parentPath
      },
      'get'
    )
      .then((rst) => {
        // 后台的结果集顺序是根据结果集名称按字符串排序的，展示时会出现结果集对应不上的问题，所以加上排序
        if(rst.dirFileTrees && rst.dirFileTrees.children) {
          const slice = (name) => {
            return Number(name.slice(1, name.lastIndexOf('.')));
          }
          const resultList = rst.dirFileTrees.children.sort((a, b) => {
            return slice(a.name) - slice(b.name);
          });
          this.script.resultList = resultList
        }
      })
    } else if(this.$route.query.resultPath) {
      this.script.result.path = this.$route.query.resultPath
      this.script.resultList = [{
        path:  this.$route.query.resultPath,
      }]
    }
  },
  methods: {
    getHeight: debounce(function () {
      this.scriptViewState.bottomContentHeight = this.$el.clientHeight
    }, 300),
    getRunType() {
      if (this.$route.query.fileName ) {
        let supportedMode = find(SUPPORTED_LANG_MODES, (p) => p.rule.test(this.$route.query.fileName)) || {};
        return supportedMode
      }
    },
    changeResultSet(data, cb) {
      const resultSet = data.currentSet || 0;
      this.script = {
        ...this.script,
        resultSet
      };
      if (cb) {
        cb();
      }
    },
  }
}
</script>
<style scope>
.loading-text {
  display: flex;
  justify-content: center;
  position: absolute;
  height: 100px;
  padding: 20px;
  width: 100%;
  z-index: 2;
  align-items: center;
}
</style>
