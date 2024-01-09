<template>
  <div style="width:100%;height:100%">
    <div
      v-if="loading"
      class="loading-text">
      请耐心等待，数据正在加载中... <Spin />
    </div>
    <result
      ref="result"
      getResultUrl="filesystem"
      :script="script"
      :dispatch="dispatch"
      :script-view-state="scriptViewState" />
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
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
        fileName: this.$route.query.fileName || '',
        runType: this.getRunType() || 'hql',
        data: '',
        oldData: '',
        result: {},
        steps: [],
        progress: {},
        resultList: null,
        resultSet: 0,
        params: {},
        readOnly: false
      },
      loading: true
    }
  },
  computed: {
    scriptResult() {
      let res = {
        headRows: [],
        bodyRows: [],
        type: 'EMPTY',
        total: 0,
        path: ''
      };
      if (this.script.resultList) {
        res = this.script.resultList[this.script.resultSet || 0].result || res
      }
      return res
    },
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.getHeight);
  },
  mounted() {
    // 监听窗口变化，获取浏览器宽高
    window.addEventListener('resize', this.getHeight);
    this.getResult(this.$route.query.resultPath)
    this.scriptViewState.bottomContentHeight = this.$el.clientHeight
  },
  methods: {
    getHeight: debounce(function () {
      this.scriptViewState.bottomContentHeight = this.$el.clientHeight
    }, 300),
    getResult(resultPath) {
      const pageSize = 5000;
      const url = '/filesystem/openFile';
      api.fetch(url, {
        path: resultPath,
        pageSize,
      }, 'get')
        .then((ret) => {
          const result = {
            'headRows': ret.metadata,
            'bodyRows': ret.fileContent,
            // 如果totalLine是null，就显示为0
            'total': ret.totalLine ? ret.totalLine : 0,
            // 如果内容为null,就显示暂无数据
            'type': ret.fileContent ? ret.type : 0,
            'path': resultPath,
            'current': 1,
            'size': 20,
          };
          this.script.resultSet = 0;
          this.script.resultList = [{
            path: resultPath,
            result
          }]
          this.$nextTick(()=>{
            this.loading = false
          })
        }).catch(() => {
        });
    },
    getRunType() {
      if (this.$route.query.fileName ) {
        let supportedMode = find(SUPPORTED_LANG_MODES, (p) => p.rule.test(this.$route.query.fileName)) || {};
        return supportedMode.runType
      }
    }
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
