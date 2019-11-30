<template>
  <div>
    <ul class="we-toolbar">
      <li
        @click.stop="analysis"
        v-if="(script.runType === 'sql' || script.runType === 'hql') && $route.name === 'Home'">
        <Icon type="md-analytics" />
        <span :title="$t('message.workBench.body.script.toolbar.analysis')" class="v-toolbar-icon">{{ $t('message.workBench.body.script.toolbar.analysis') }}</span>
      </li>
      <li :style="{cursor: rsDownload ? 'pointer': 'not-allowed'}">
        <div v-if="!rsDownload">
          <Icon type="ios-cloud-download-outline" />
          <span :title="$t('message.constants.download')" class="v-toolbar-icon">{{ $t('message.constants.download') }}</span>
        </div>
        <Poptip
          v-else
          :transfer="true"
          :width="275"
          v-model="popup.download"
          placement="right"
          popper-class="we-poptip">
          <div @click.stop="openPopup('download')">
            <svg
              t="1560156352737"
              class="icon"
              style=""
              viewBox="0 0 1024 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="5025"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              width="14"
              height="14">
              <path
                d="M1024 640.192C1024 782.912 919.872 896 787.648 896h-512C123.904 896 0 761.6 0 597.504 0 451.968 94.656 331.52 226.432 302.976 284.16 195.456 391.808 128 512 128c152.32 0 282.112 108.416 323.392 261.12C941.888 413.44 1024 519.04 1024 640.192zM341.312 570.176L512 756.48l170.688-186.24H341.312z m213.376 0v-256H469.312v256h85.376z"
                fill="#515a6e"
                p-id="5026">
              </path>
            </svg>
            <span :title="$t('message.constants.download')" class="v-toolbar-icon">{{ $t('message.constants.download') }}</span>
          </div>
          <div slot="content">
            <div>
              <Row>
                {{ $t('message.workBench.body.script.toolbar.format') }}
              </Row>
              <Row>
                <RadioGroup v-model="download.format">
                  <Col span="10">
                  <Radio label="1">CSV</Radio>
                  </Col>
                  <Col
                    span="10"
                    offset="4">
                  <Radio label="2">Excel</Radio>
                  </Col>
                </RadioGroup>
              </Row>
            </div>
            <div>
              <Row>
                {{ $t('message.workBench.body.script.toolbar.coding') }}
              </Row>
              <Row>
                <RadioGroup v-model="download.coding">
                  <Col span="10">
                  <Radio label="1">UTF-8</Radio>
                  </Col>
                  <Col
                    span="10"
                    offset="4">
                  <Radio label="2">GBK</Radio>
                  </Col>
                </RadioGroup>
              </Row>
            </div>
            <div>
              <Row>
                {{$t('message.container.toolbar.replace')}}
              </Row>
              <Row>
                <RadioGroup v-model="download.nullValue">
                  <Col span="10">
                  <Radio label="1">NULL</Radio>
                  </Col>
                  <Col
                    span="10"
                    offset="4">
                  <Radio label="2">{{$t('message.container.toolbar.emptyString')}}</Radio>
                  </Col>
                </RadioGroup>
              </Row>
            </div>
            <Row class="confirm">
              <Col span="10">
              <Button @click="cancelPopup('download')">{{$t('message.newConst.cancel')}}</Button>
              </Col>
              <Col
                span="10"
                offset="4">
              <Button
                type="primary"
                @click="confirm('download')">{{ $t('message.constants.submit') }}</Button>
              </Col>
            </Row>
          </div>
        </Poptip>
      </li>
      <li
        @click="openPopup('rowView')"
        v-if="row"
        :title="$t('message.workBench.body.script.toolbar.rowToColumnTitle')">
        <svg
          t="1560157194178"
          class="icon"
          style=""
          viewBox="0 0 1076 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          p-id="6108"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          width="14"
          height="14">
          <path
            d="M353.840537 64.013023v114.282007c-93.722967-1.500318-185.804962 51.104569-229.056306 134.77072-32.233387 60.926961-45.712803 130.738616-45.665918 199.284378h71.804264c0.234425-81.720426 18.707085-173.567996 84.861716-227.977953 33.475837-25.786709 76.492757-35.163695 118.056244-35.749756v109.874824a15748.974859 15748.974859 0 0 0 175.068314-147.218668c-58.371733-49.041633-116.743466-98.13015-175.068314-147.24211z m276.902373 18.308563c-33.077315-1.078353-51.807843 33.335182-46.884926 62.919571 0.164097 232.385136-0.328194 464.817157 0.234425 697.178851 2.203592 32.608466 36.499915 46.228537 64.935622 42.079221 58.840582-0.609504 117.798377 1.265893 176.521747-0.937699 31.834865-5.274554 40.649231-39.102028 37.414171-66.52971-0.164097-230.861376 0.328194-461.769637-0.234425-692.60757-2.203592-32.631909-36.499915-46.25198-64.935622-42.102664h-167.050992z m-515.734186 519.391211c-33.147643-1.008026-54.105205 33.241413-49.088518 63.529075 0.632947 60.036148-1.289335 120.21295 0.984583 180.131886 4.94636 29.936025 36.804667 42.946592 64.115137 39.125471h379.580361V601.73624H115.008724z"
            fill="#515a6e"
            p-id="6109">
          </path>
        </svg>
        <span class="v-toolbar-icon">{{$t('message.workBench.body.script.toolbar.rowToColumn')}}</span>
      </li>
    </ul>
    <table-row
      ref="tableRow"
      :row="row"></table-row>
  </div>
</template>
<script>
import moment from 'moment';
import module from '../index';
import tableRow from './tableRow.vue';
export default {
  components: {
    tableRow,
  },
  mixins: [module.mixin],
  props: {
    currentPath: {
      type: String,
      default: '',
    },
    script: [Object],
    row: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    let rsDownload = this.getRsDownload();
    return {
      rsDownload,
      popup: {
        download: false,
        visual: false,
        rowView: false,
      },
      download: {
        format: '1',
        coding: '1',
        nullValue: '1',
      },
    };
  },
  methods: {
    openPopup(type) {
      if (type === 'rowView') {
        this.$refs.tableRow.open();
      } else {
        this.popup[type] = true;
      }
    },
    cancelPopup(type) {
      this.popup[type] = false;
    },
    confirm(type) {
      this[`${type}Confirm`]();
      this.cancelPopup(type);
    },
    downloadConfirm() {
      const splitor = this.download.format === '1' ? 'csv' : 'xlsx';
      const charset = this.download.coding === '1' ? 'utf-8' : 'gbk';
      const nullValue = this.download.nullValue === '1' ? 'NULL' : 'BLANK';
      const timestamp = moment.unix(moment().unix()).format('MMDDHHmm');
      const filename = `Result_${this.script.fileName}_${timestamp}`;
      const url = module.data.API_PATH + 'filesystem/resultsetToExcel?path=' + this.currentPath + '&charset=' + charset + '&outputFileType=' + splitor + '&nullValue=' + nullValue + '&outputFileName=' + filename;
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', '');
      const evObj = document.createEvent('MouseEvents');
      evObj.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, true, false, 0, null);
      const flag = link.dispatchEvent(evObj);
      this.$nextTick(() => {
        if (flag) {
          this.$Message.success('下载成功，请到本地的download文件夹查看！');
        }
      });
    },
    analysis() {
      this.$emit('on-analysis');
    },
  },
};
</script>
