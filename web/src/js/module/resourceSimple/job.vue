<template>
  <div>
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <div class="job-manager">
      <div
        class="job-manager-empty"
        v-if="!jobList.length">{{ $t('message.resourceSimple.ZWSJ') }}</div>
      <div
        class="job-manager-type"
        v-for="(type, index) in jobTypeList"
        :key="index">
        <span
          class="job-manager-title"
          v-if="jobList.length && checkJobLength(type.en)">{{ type.cn }}</span>
        <div
          v-for="(item, index) in jobList"
          :key="index">
          <div
            v-if="item.requestApplicationName === type.en"
            class="job-manager-item-wrapper"
            :style="getClass(item)"
            :class="{'actived': item.isActive}"
            @click="handleItemClick(item)"
            @contextmenu.prevent.stop="handleItemMenu($event, item)">
            <Icon
              type="md-checkmark"
              class="job-manager-item-active"
              v-if="item.isActive"
            ></Icon>
            <span
              class="job-manager-item-progress"
              :style="{'width': item.progress ? (item.progress * 100).toFixed(2) + '%' : 0}"></span>
            <i
              class="job-manager-item-icon"
              :class="getIconClass(item)"></i>
            <span class="job-manager-item-label">{{ getLabel(item) }}</span>
            <span class="job-manager-item-progress-label">{{ item.progress ? (item.progress * 100).toFixed(2) + '%' : '' }}</span>
            <Icon
              size="16"
              type="ios-close-circle-outline"
              class="job-manager-item-close"
              @click="openKillModal($t('message.resourceSimple.RW'))"/>
          </div>
        </div>
      </div>
      <we-menu
        ref="contextMenu"
        id="jobManager">
        <we-menu-item
          @select="openKillModal($t('message.resourceSimple.YQRW'))"
          v-if="lastClick && lastClick.status !== 'Inited'">
          <span>{{ $t('message.resourceSimple.JSYQRW') }}</span>
        </we-menu-item>
        <we-menu-item @select="openHistoryScript">
          <span>{{ $t('message.resourceSimple.CKJBXX') }}</span>
        </we-menu-item>
        <we-menu-item @select="copyInfos">
          <span>{{ $t('message.resourceSimple.FZJBXX') }}</span>
        </we-menu-item>
      </we-menu>
    </div>
    <detele-modal
      ref="killModal"
      :loading="loading"
      @delete="kill"></detele-modal>
  </div>
</template>
<script>
import { orderBy } from 'lodash';
import util from '@/js/util';
import api from '@/js/service/api';
import deteleModal from '@js/component/deleteDialog';
export default {
  name: 'Job',
  components: {
    deteleModal,
  },
  data() {
    return {
      loading: false,
      jobTypeList: [],
      jobList: [],
      lastClick: null,
    };
  },
  watch: {
    loading(val) {
      this.$emit('change-loading', val);
    },
  },
  methods: {
    getJobList() {
      this.jobList = [];
      api.fetch('/jobhistory/list', {
        pageSize: 100,
        status: 'Running,Inited,Scheduled',
      }, 'get').then((rst) => {
        this.dispatch('Footer:updateRunningJob', rst.tasks.length);
        this.jobTypeList = [{ 'en': 'IDE', 'cn': this.$t('message.resourceSimple.YS') }, { 'en': 'visualis', 'cn': this.$t('message.resourceSimple.ZH') },  {'en': 'flowexecution', 'cn': this.$t('message.resourceSimple.FLOW1') }, {'en': 'Scheduler', 'cn': this.$t('message.resourceSimple.FLOW2')}];
        rst.tasks.forEach((item, index) => {
          const tmpItem = Object.assign({}, item, { isActive: false, fileName: this.convertJson(item) });
          this.jobList.push(tmpItem);
        });
        this.jobList = orderBy(this.jobList, ['status', 'fileName']);
        this.$emit('update-job', rst.tasks.length);
      }).catch((err) => {
        this.isLoading = false;
      });
    },
    // 删除当前工作
    killJob() {
      const item = this.lastClick;
      if (this.loading) return this.$Message.warning(this.$t('message.resourceSimple.DDJK'));
      if (!item) return this.$Message.warning(this.$t('message.resourceSimple.QXZYTJL'));
      if (!item.strongerExecId) return this.$Message.warning(this.$t('message.resourceSimple.WHQD'));
      this.loading = true;
      api.fetch(`/entrance/${item.strongerExecId}/kill`, 'get').then((rst) => {
        this.loading = false;
        this.$emit('close-modal');
        // 停止执行
        this.$Notice.close(item.scriptPath);
        this.$Notice.warning({
          title: this.$t('message.resourceSimple.YXTS'),
          desc: `${this.$t('message.resourceSimple.YJTZZXJB')}: ${item.fileName || ''} ！`,
          name: item.scriptPath,
          duration: 3,
        });
      }).catch(() => {
        this.loading = false;
      });
    },
    async killJobAndEngine() {
      const item = this.lastClick;
      if (this.loading) return this.$Message.warning(this.$t('message.resourceSimple.DDJK'));
      if (!item.strongerExecId) return this.$Message.warning(this.$t('message.resourceSimple.WHQD'));
      if (!item.engineInstance) return this.$Message.warning(this.$t('message.resourceSimple.WHQDENGINE'));
      this.loading = true;
      await api.fetch(`/entrance/${item.strongerExecId}/kill`, 'get');
      api.fetch('/resourcemanager/engines').then((res) => {
        const engines = res.engines;
        const engine = engines.find((e) => e.engineInstance === item.engineInstance);
        if (engine) {
          const params = [{
            ticketId: engine.ticketId,
            moduleName: engine.moduleName,
            engineManagerInstance: engine.engineManagerInstance,
            creator: engine.creator,
          }];
          api.fetch(`/resourcemanager/enginekill`, params).then((rst) => {
            this.loading = false;
            this.$emit('close-modal');
            this.$Notice.close(item.scriptPath);
            this.$Notice.warning({
              title: this.$t('message.resourceSimple.YXTS'),
              desc: this.$t('message.resourceSimple.JSYQHRWCG'),
              name: item.scriptPath,
              duration: 3,
            });
          }).catch(() => {
            this.loading = false;
          });
        }
      }).catch(() => {
        this.loading = false;
      });
    },
    openHistoryScript() {
      const item = this.lastClick;
      if (item.requestApplicationName === 'IDE') {
        const supportModes = this.getSupportModes();
        const match = supportModes.find((s) => s.rule.test(item.fileName));
        const ext = match ? match.ext : '.hql';
        const name = `history_item_${item.taskID}${ext}`;
        const md5Id = util.md5(name);
        this.$emit('close-modal');
        if (this.$route.name === 'Home') {
          this.dispatch('Workbench:add', {
            id: md5Id, // 唯一标识，就算文件名修改，它都能标识它是它
            taskID: item.taskID,
            filename: name,
            filepath: item.scriptPath,
            // saveAs表示临时脚本，需要关闭或保存时另存
            saveAs: true,
            code: item.executionCode,
            type: 'historyScript',
          }, (f) => {
            if (f) {
              this.$Message.success(this.$t('message.resourceSimple.DKCG'));
            }
          });
        } else {
          this.$router.push({ path: '/',
            query: {
              id: md5Id,
              taskID: item.taskID,
              filename: name,
              filepath: item.scriptPath,
              saveAs: true,
              type: 'historyScript',
              code: item.executionCode,
            } });
        }
      }
    },
    getColor(item) {
      let color = '';
      if (item.status === 'Inited') {
        color = '#515a6e';
      } else if (item.status === 'Running') {
        color = 'rgb(45, 140, 240)';
      } else {
        color = 'rgba(243, 133, 243, 0.5)';
      }
      return color;
    },
    getClass(item) {
      const color = this.getColor(item);
      return {
        // border: `1px solid ${color}`,
        color,
      };
    },
    getIconClass(script) {
      let logos = this.getSupportModes().filter((item) => {
        return item.rule.test(script.fileName) || item.runType === script.runType;
      });
      if (logos.length > 0) {
        return logos[0].logo;
      } else if (script.requestApplicationName === 'flowexecution' || script.requestApplicationName === 'Scheduler') {
        return 'fi-workflow1';
      } else {
        return 'fi-bi';
      }
    },
    handleItemClick(item) {
      this.$emit('change-job-disabled', false);
      if (!this.lastClick) {
        this.lastClick = item;
        item.isActive = true;
      } else if (this.lastClick !== item) {
        this.lastClick.isActive = false;
        this.lastClick = item;
        item.isActive = true;
      }
    },
    handleItemMenu(ev, item) {
      this.handleItemClick(item);
      this.$refs.contextMenu.open(ev);
    },
    copyInfos() {
      util.executeCopy(JSON.stringify(this.lastClick));
      this.$Message.success(this.$t('message.resourceSimple.JBFZDZTRB'));
    },
    checkJobLength(type) {
      const list = this.jobList.filter((item) => item.requestApplicationName === type);
      return list.length;
    },
    openKillModal(type) {
      this.$refs.killModal.open({ type, name: '' });
    },
    kill(type) {
      if (type === this.$t('message.resourceSimple.YQRW')) {
        this.killJobAndEngine();
      } else {
        this.killJob();
      }
    },
    getLabel(item) {
      if (item.fileName) {
        return item.fileName;
      } else {
        if (item.requestApplicationName === 'IDE') {
          return this.$t('message.resourceSimple.WZJBMC')
        } else if (item.requestApplicationName === 'flowexecution') {
          return this.$t('message.resourceSimple.GZLJB');
        } else if (item.requestApplicationName === 'Scheduler') {
          return this.$t('message.resourceSimple.GZLJB');
        } else {
          return this.$t('message.resourceSimple.ZHJB');
        }
      }
    },
    convertJson(item) {
      if (item.sourceJson) {
        const json = JSON.parse(item.sourceJson);
        return json.fileName || json.flowName;
      }
      return '';
    }
  },
};
</script>
