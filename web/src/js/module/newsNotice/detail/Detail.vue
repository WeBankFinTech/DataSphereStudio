<template>
  <div class="container">
    <Card class="detail-card">
      <p slot="title">
        <Icon type="md-arrow-round-back" class="back-icon" @click="$router.go(-1)"/>
        {{ $t('message.newsNotice.detail.title') }}
      </p>
      <Collapse v-model="panelAction">
        <Panel v-for="(item, index) in notifyList" :key="index" :name="`notify_${index}`">
          <span slot="">
            <span style="padding-right: 15px;">{{ parseTime(item.createdTime) }}</span>
            <span>{{ item.woSubject }}</span>
          </span>
          <div slot="content" class="panel-detail">
            <Row>
              <Col>
              <div class="item-title">{{ $t('message.newsNotice.detail.detailForm.status') }}</div>
              <div>{{ item.statusName }}</div>
              </Col>
            </Row>
            <Row>
              <Col>
              <div class="item-title">{{ $t('message.newsNotice.detail.detailForm.woDesc') }}</div>
              <div>{{ item.woDesc }}</div>
              </Col>
            </Row>
            <Row>
              <Col>
              <div class="item-title">{{ $t('message.newsNotice.detail.detailForm.resolvedResult') }}</div>
              <div>
                {{ item.resolvedResult? item.resolvedResult : $t('message.newsNotice.detail.emptyText') }}
              </div>
              </Col>
            </Row>
          </div>
        </Panel>
      </Collapse>
      <Row class="attach">
        <Col>
        <div class="item-title">{{ $t('message.newsNotice.detail.detailForm.attach') }}</div>
        <div>
          <div
            v-for="(item, index) in attachList"
            :key="index"
            class="attach-item"
          >
            <span class="attach-item-name" @click="downLoadFile(item)">
              {{item.filename}}
            </span>
            <Icon v-if="deleteFileShow" type="md-trash" @click="deleteFile(item)" style="cursor: pointer;" />
          </div>
        </div>
        </Col>
      </Row>
    </Card>
    <div class="footer">
      <div class="btn">
        <Button type="primary" size="large" style="margin-right: 10px;" :disabled="appendDisabled" @click="openFeedback">{{ $t('message.newsNotice.detail.appendFeedback') }}</Button>
        <Button type="success" size="large" @click="openResloved" :disabled="reslovedDisabled" :loading="reslovedLoading">{{ $t('message.newsNotice.detail.resloved') }}</Button>
      </div>
    </div>
    <FeedBackDialog
      ref="feedBackForm"
      :action-type="feedBackActionType"
      :feedBackFormShow="feedBackShow"
      :feedBackType="feedBackType"
      :issueId="Number(issueId)"
      source="detail"
      @show="feedBackShowAction"
      @refresh-detail="getNotifyDetail"
    />
    <Spin fix v-if="loading"></Spin>
  </div>
</template>
<script>
import axios from 'axios';
import { isEmpty } from 'lodash';
import storage from '@/js/helper/storage';
import moment from 'moment';
import module from '../index';
import api from '@/js/service/api';
import FeedBackDialog from '@/js/module/feedBack/index.vue';

export default {
  name: 'Detail',
  components: {
    FeedBackDialog
  },
  data() {
    return {
      loading: false,
      notifyList: [],
      attachList: [],
      issueId: null,
      panelAction: '',
      feedBackShow: false,
      feedBackActionType: '',
      feedBackType: '',
      userName: '',
      url: module.data.API_PATH,
      reslovedDisabled: true,
      reslovedLoading: false,
      appendDisabled: true,
      deleteFileShow: false,
    };
  },
  created() {
    this.issueId = this.$route.query.id;
    const issueStatus = this.$route.query.status;
    // 状态为处理中和已处理
    if (issueStatus === 'istatus.processing' || issueStatus === 'istatus.resolved') {
      this.appendDisabled = false;
      this.deleteFileShow = true;
    }
    // 状态为已处理
    if (issueStatus === 'istatus.resolved') {
      this.reslovedDisabled = false;
    }
    const userInfo = storage.get('userInfo');
    this.userName = userInfo.basic.username;
  },
  mounted() {
    this.initData();
  },
  methods: {
    initData() {
      this.getNotifyDetail();
    },
    getNotifyDetail() {
      this.loading = true;
      api.fetch(`${this.url}userFeedBacks/${this.issueId}/details`, { username: this.userName }, 'get').then((data) => {
        if (!isEmpty(data.workOrderList)) {
          this.notifyList = data.workOrderList;
          this.panelAction = `notify_${this.notifyList.length - 1}`;
        }
        this.attachList = data.attachList;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 追加反馈
    openFeedback() {
      if (this.notifyList.length > 3) {
        this.$Modal.warning({
          title: this.$t('message.newsNotice.warning.title'),
          content: this.$t('message.newsNotice.warning.appendFeedBackMax')
        });
        return;
      }
      // 判断能否追加反馈
      api.fetch(`${this.url}userFeedBacks/${this.issueId}/judgeFeedBack`, { username: this.userName }, 'get').then((data) => {
        if (data) {
          this.feedBackActionType = 'append';
          this.feedBackType = 'itype.luban.functionAdvice';
          this.feedBackShow = true;
        } else {
          this.$Modal.warning({
            title: this.$t('message.newsNotice.confirm.title'),
            content: this.$t('message.newsNotice.confirm.judgeFeedBackContent')
          });
        }
      }).catch((err) => {
        this.$Message.error(err.message);
      });
    },
    // 确认已解决
    openResloved() {
      this.$Modal.confirm({
        title: this.$t('message.newsNotice.confirm.title'),
        content: this.$t('message.newsNotice.confirm.reslovedContent'),
        className: 'verticalCenterModal',
        onOk: () => {
          // 确认解决接口
          this.reslovedLoading = true;
          api.fetch(`${this.url}issues/${this.issueId}/close`, { closeReason: 'luban', status: 'istatus.closed', tableName: 0 }, 'put').then((data) => {
            if (data) {
              this.appendDisabled = true;
              this.reslovedDisabled = true;
              this.reslovedLoading = false;
              this.$store.dispatch('newsNotice/getUnreadNewsCount');
              this.$Message.success({
                content: this.$t('message.newsNotice.success.reslovedMsg'),
                duration: Number(this.$t('message.feedBack.messageDuration'))
              });
              this.$router.go(-1);
            }
          }).catch(() => {
            this.appendDisabled = false;
            this.reslovedLoading = false;
          });
        },
        onCancel: () => {
        },
      });
    },
    feedBackShowAction(val) {
      this.feedBackShow = val;
    },
    downLoadFile(file) {
      axios.get(`${this.url}issues/files/${file.fileId}`, {
        responseType: 'blob',
        headers: { 'Authorization': 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtYWlsIjpudWxsLCJwaG9uZSI6IjE3NzE3MjkxMzQxIiwicm9sZXMiOiJzdXBlclVzZXIiLCJpZCI6IjEiLCJ1c2VybmFtZSI6ImFkbWluIiwiZXhwIjoxNjI4NzUxNTg0LCJuYmYiOjE1OTcyMTU1ODR9.XxG57DsnayGZY_J90gIpyRrMe5x95lKf9hzJh320XmM' }
      }).then((response) => {
        const link = document.createElement('a');
        const blob = new Blob([response.data], { type: response.headers['content-type'] });
        link.href = window.URL.createObjectURL(blob);
        link.download = file.filename;
        let event = null;
        if (window.MouseEvent) {
          event = new MouseEvent('click');
        } else {
          event = document.createEvent('MouseEvents');
        }
        const flag = link.dispatchEvent(event);
        this.$nextTick(() => {
          if (flag) {
            this.$Message.success({
              content: this.$t('message.workBench.body.script.history.success.download'),
              duration: Number(this.$t('message.feedBack.messageDuration'))
            });
          }
        });
      }).catch((err) => {
        this.$Message.error(err.message);
      });
    },
    deleteFile(file) {
      this.$Modal.confirm({
        title: this.$t('message.newsNotice.confirm.title'),
        content: this.$t('message.newsNotice.confirm.deleteFile'),
        onOk: () => {
          api.fetch(`${this.url}issues/${this.issueId}/files/${file.fileId}`, { username: this.userName }, 'delete').then((data) => {
            if (data) {
              this.$Message.success({
                content: this.$t('message.newsNotice.success.attachMsg'),
                duration: Number(this.$t('message.feedBack.messageDuration'))
              });
              this.getNotifyDetail();
            }
          });
        },
        onCancel: () => {
        },
      });
    },
    parseTime(time) {
      let newDate = '';
      if (time && time.toString().length === 13) {
        const date = new Date(parseInt(time));
        newDate = moment(date).format('YYYY-MM-DD HH:mm');
      } else if (time && time.toString().indexOf('T') > 0) {
        const date = new Date(time).toJSON();
        newDate = new Date(+new Date(date) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '');
      }
      return newDate;
    },
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
<style lang="scss">
.verticalCenterModal{
  display: flex;
  align-items: center;
  justify-content: center;

  .ivu-modal{
    top: 0;
  }
}
</style>
