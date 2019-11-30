<template>
  <div
    class="progress"
    :style="{'padding-left': status ? '40px' : 0}">
    <we-toolbar
      :status="status"
      :application="application"
      @open-panel="openPanel"
      v-if="status"></we-toolbar>
    <steps
      :step-list="steps"
      v-if="steps.length"></steps>
    <Row
      class="total-progress"
      v-if="isStatusOk">
      <Col
        span="2"
        class="title"
      >{{ $t('message.workBench.body.script.progress.title') + '：' }}</Col>
      <Col span="12">
      <Progress
        :percent="percent"
        status="active"/>
      </Col>
      <Col
        span="10"
        v-if="percent === 100">
      <span>{{$t('message.container.process')}}</span>
      <span class="progress-costtime">{{ costTime }}</span>
      </Col>
    </Row>
    <Row
      class="total-progress"
      v-if="isWaittingSizeShow">
      <Col
        span="2"
        class="title"
      >{{ $t('message.workBench.body.script.progress.title') + '：' }}</Col>
      <Col span="12">
      <span>{{ $t('message.workBench.body.script.progress.waitingList', { num: waitingSize }) }}</span>
      </Col>
    </Row>
    <Table
      class="progress-table"
      v-if="isStatusOk"
      :columns="columns"
      :data="progressInfo"
      :height="tableHeight"
      size="small"
      stripe
      border/>
  </div>
</template>
<script>
import module from '../index';
import resizeMixin from './mixin.js';
import steps from './steps.vue';
import weToolbar from './toolbar_progress.vue';
export default {
  components: {
    steps,
    weToolbar,
  },
  mixins: [module.mixin, resizeMixin],
  props: {
    progressInfo: {
      type: Array,
      required: true,
      default: () => [],
    },
    current: {
      type: Number,
      default: 0,
    },
    waitingSize: Number,
    steps: {
      type: Array,
      required: true,
      default: () => [],
    },
    status: String,
    application: String,
    costTime: {
      type: String,
      defalut: `0 second`,
    },
  },
  data() {
    let that = this;
    return {
      columns: [
        {
          title: this.$t('message.workBench.body.script.progress.columns.id.title'),
          key: 'id',
          width: 200,
        },
        {
          title: this.$t('message.workBench.body.script.progress.columns.progress.title'),
          key: 'action',
          render(h, { row }) {
            return h('div', {
              class: 'progress-wrap',
            }, [
              h('div', {
                class: 'progress-item progress-waiting',
                style: that.waitingStyle(row),
              }, {

              }),
              h('div', {
                class: 'progress-item progress-success',
                style: that.successStyle(row),
              }),
              h('div', {
                class: 'progress-item progress-failed',
                style: that.failedStyle(row),
              }),
            ]);
          },
        },
        {
          title: this.$t('message.workBench.body.script.progress.columns.taskNum.title'),
          key: 'action',
          render(h, { row }) {
            return h('div', [
              h('span', {
                class: 'progress-text',
                style: {
                  'color': '#2d8cf0',
                },
              }, `${that.$t('message.workBench.body.script.progress.columns.taskNum.status.running')}：${row.runningTasks || 0}`),
              h('span', {
                class: 'progress-text',
                style: {
                  'color': '#19be6b',
                },
              }, `${that.$t('message.workBench.body.script.progress.columns.taskNum.status.success')}：${row.succeedTasks || 0}`),
              h('span', {
                class: 'progress-text',
                style: {
                  'color': '#ed4014',
                },
              }, `${that.$t('message.workBench.body.script.progress.columns.taskNum.status.fail')}：${row.failedTasks || 0}`),
              h('span', {
                class: 'progress-text',
              }, `${that.$t('message.workBench.body.script.progress.columns.taskNum.status.totalTasks')}：${row.totalTasks || 0}`),
            ]);
          },
        },
      ],
    };
  },
  computed: {
    percent() {
      return Number((this.current * 100).toFixed(2));
    },
    isWaittingSizeShow() {
      // 当waitingSize等于null时，waitingSize >= 0为true；
      return (this.status === 'Scheduled' || this.status === 'Inited') && (this.waitingSize !== null && this.waitingSize >= 0);
    },
    isStatusOk() {
      return this.status && !(this.status === 'Scheduled' || this.status === 'Inited');
    }
  },
  methods: {
    'Workbench:setParseAction'() {
      this.resize();
    },
    successStyle({ failedTasks, runningTasks, succeedTasks, totalTasks }) {
      let succeedPercent = (succeedTasks / totalTasks) * 100;
      let runningPercent = (runningTasks / totalTasks) * 100;
      let borderStyle = {};
      if (failedTasks) {
        borderStyle = {
          'border-bottom-right-radius': 0,
          'border-top-right-radius': 0,
        };
      }
      if (runningTasks) {
        Object.assign(borderStyle, {
          'border-top-left-radius': 0,
          'border-bottom-left-radius': 0,
        });
      }
      if (succeedPercent) {
        Object.assign(borderStyle, {
          'min-width': '10px',
        });
      } else {
        Object.assign(borderStyle, {
          'min-width': '0px',
        });
      }
      return Object.assign(borderStyle, {
        'left': runningPercent + '%',
        'width': succeedPercent + '%',

      });
    },
    waitingStyle({ failedTasks, runningTasks, succeedTasks, totalTasks }) {
      let runningPercent = (runningTasks / totalTasks) * 100;
      let borderStyle = {};
      if (succeedTasks || failedTasks) {
        borderStyle = {
          'border-bottom-right-radius': 0,
          'border-top-right-radius': 0,
        };
      }
      if (runningPercent) {
        Object.assign(borderStyle, {
          'min-width': '10px',
        });
      } else {
        Object.assign(borderStyle, {
          'min-width': '0px',
        });
      }
      return Object.assign(borderStyle, {
        'width': runningPercent + '%',
      });
    },
    failedStyle({ failedTasks, runningTasks, succeedTasks, totalTasks }) {
      let leftPercent = ((succeedTasks + runningTasks) / totalTasks) * 100;
      let failedPercent = (failedTasks / totalTasks) * 100;
      let borderStyle = {};
      if (runningTasks || succeedTasks) {
        borderStyle = {
          'border-top-left-radius': 0,
          'border-bottom-left-radius': 0,
        };
      }
      if (failedPercent) {
        Object.assign(borderStyle, {
          'min-width': '10px',
        });
      } else {
        Object.assign(borderStyle, {
          'min-width': '0px',
        });
      }
      return Object.assign(borderStyle, {
        'left': leftPercent + '%',
        'width': failedPercent + '%',
      });
    },
    resize() {
      this.tableHeight = this.$el.clientHeight - 120;
    },
    openPanel(type) {
      this.$emit('open-panel', type);
    },
  },
};
</script>
