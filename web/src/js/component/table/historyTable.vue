<template>
  <div
    class="we-table-wrap"
    :style="{height: height + 'px'}">
    <table
      cellspacing="0"
      cellpadding="0"
      border="0"
      class="we-table"
    >
      <thead>
        <tr
          class="we-table-thead">
          <th
            v-for="(th) in columns"
            :key="th.key"
            :style="{'min-width': th.width + 'px', 'text-align': th.align}"
            class="we-table-thead-cell">
            {{ th.title || '#' }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(td, index) in data"
          :key="index"
          class="we-table-row">
          <td
            v-for="(th, index) in columns"
            :key="th.key"
            :style="{'text-align': th.align}"
            class="we-table-row-cell">
            <div
              class="we-table-row-label"
              :style="{'width': th.width ? th.width + 'px' : getComputedWidth(th)}"
              :class="{'ellipsis': th.ellipsis}"
              :title="th.ellipsis ? td[th.key] : ''">
              <table-expand
                v-if="th.renderType"
                :row="td"
                :column="th"
                :index="index"
                :render="renderComponent({type: th.renderType, cell: td, key: th.key, params: th.renderParams})"></table-expand>
              <span
                v-else
                :class="th.className">{{ td[th.key] }}</span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import moment from 'moment';
import util from '@/js/util';
import TableExpand from './expand';
import elementResizeEvent from '@js/helper/elementResizeEvent';
export default {
  components: {
    TableExpand,
  },
  props: {
    columns: {
      type: Array,
      default: () => [],
    },
    data: {
      type: Array,
      default: () => [],
    },
    height: Number,
  },
  data() {
    return {
      offsetWidth: 0,
    };
  },
  mounted() {
    elementResizeEvent.bind(this.$el, this.resize);
  },
  beforeDestroy: function() {
    elementResizeEvent.unbind(this.$el);
  },
  methods: {
    renderComponent({ type, cell, key, params }) {
      const value = cell[key];
      switch (type) {
        case 'tag':
          return this.renderTag(value);
        case 'progress':
          return this.renderProgress(value);
        case 'button':
          return this.renderButton(value, cell, params);
        case 'tooltip':
          return this.renderTooltip(value);
        case 'formatTime':
          return this.renderFormatTime(value);
        case 'convertTime':
          return this.renderConvertTime(value);
        case 'index':
          return this.renderIndex(cell);
        case 'a':
          return this.renderA(value, cell, params);
        default:
          return null;
      }
    },
    renderTag(value) {
      return (h) => {
        let color = '';
        let background = '';
        let borderColor = '';
        let label = '';
        switch (value) {
          case 'Succeed':
            color = '#52c41a';
            borderColor = '#b7eb8f';
            background = '#f6ffed';
            label = this.$t('message.globalHistory.statusType.succeed');
            break;
          case 'Running':
            color = '#13c2c2';
            borderColor = '#87e8de';
            background = '#e6fffb';
            label = this.$t('message.globalHistory.statusType.running');
            break;
          case 'Timeout':
            color = 'gray';
            label = this.$t('message.globalHistory.statusType.timeout');
            break;
          case 'Inited':
            color = '#515a6e';
            label = this.$t('message.globalHistory.statusType.inited');
            break;
          case 'Scheduled':
            color = 'purple';
            borderColor = '#d3adf7';
            background = '#f9f0ff';
            label = this.$t('message.globalHistory.statusType.scheduled');
            break;
          case 'Failed':
            color = '#f5222d';
            borderColor = '#ffa39e';
            background = '#fff1f0';
            label = this.$t('message.globalHistory.statusType.failed');
            break;
          case 'Cancelled':
            color = '#fa8c16';
            borderColor = '#ffd591';
            background = '#fff7e6';
            label = this.$t('message.globalHistory.statusType.cancelled');
            break;
          case 'WaitForRetry':
            color = 'darksalmon';
            label = this.$t('message.globalHistory.statusType.retry');
            break;
          default:
            color = 'black';
            label = this.$t('message.globalHistory.statusType.unknown');
            break;
        }
        return h('span', {
          style: {
            'color': color,
            'background': background || '#f7f7f7',
            'border': `1px solid ${borderColor || color}`,
            'padding': '0 8px',
            'border-radius': '2px',
            'line-height': '20px',
          },
        }, label);
      };
    },
    renderProgress(percent) {
      return (h) => {
        const p = Math.round(percent * 10000) / 100;
        return h('div', {
          class: {
            'progress-wrap': true,
          },
          style: {
            width: '160px',
          },
        }, [
          h('span', {
            class: {
              'progress-busy': true,
            },
            style: {
              width: `${p}%`,
            },
          }),
          h('span', {
            class: {
              'progress-label': true,
            },
            style: {
              color: p >= 60 ? '#fff' : '#515a6e',
            },
          }, p === 0 ? 0 : p + '%'),
        ]);
      };
    },
    renderButton(value, cell, params) {
      const getList = (h) => {
        const list = [];
        params.forEach((item) => {
          list.push(h('button', {
            class: {
              'render-btn': true,
            },
            on: {
              click: (ev) => {
                item.action({ row: cell });
              },
            },
          }, item.label));
        });
        return list;
      };
      return (h) => {
        return h('div', getList(h));
      };
    },
    renderTooltip(value) {
      return (h) => {
        return h('Tooltip', {
          props: {
            placement: 'top-start',
            maxWidth: '600',
            theme: 'light',
            transfer: true,
          },
        }, [
          // 这个是表格上面的span
          h('span', {}, value),
          // 这个是tooltip上面的span
          h('div', {
            slot: 'content',
            style: {
              whiteSpace: 'normal',
              wordBreak: 'break-all',
              maxHeight: '500px',
              overflowY: 'auto',
            },
          }, value),
        ]);
      };
    },
    renderFormatTime(value) {
      return (h) => {
        return h('span', {}, value ? moment.unix(value / 1000).format('YYYY-MM-DD HH:mm:ss') : null);
      };
    },
    renderConvertTime(value) {
      return (h) => {
        return h('span', {}, util.convertTimestamp(value));
      };
    },
    renderIndex(cell) {
      const index = this.data.findIndex((item) => item.taskID === cell.taskID);
      return (h) => {
        return h('span', {}, index + 1);
      };
    },
    renderA(value, cell, params) {
      return (h) => {
        return h('div', {
          style: {
            cursor: 'pointer',
            color: '#ed4014',
          },
          on: {
            click: (ev) => {
              params.action({ row: cell });
            },
          },
        }, value);
      };
    },
    getComputedWidth(current) {
      this.offsetWidth = this.$el && this.$el.offsetWidth;
      let usedWidth = 0;
      const unHasWidthList = [];
      this.columns.forEach((item) => {
        const width = item.width || 0;
        usedWidth += width;
        if (!item.width) {
          unHasWidthList.push(item);
        }
      });
      return (this.offsetWidth - 30 - usedWidth) / unHasWidthList.length + 'px';
    },
    resize() {
      this.offsetWidth = this.$el && this.$el.offsetWidth;
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
