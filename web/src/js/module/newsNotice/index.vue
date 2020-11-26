<template>
  <div class="container">
    <div class="page-container-warp">{{ menuTitle }}</div>
    <div class="page-content">
      <div class="header">
        <div class="left">
          <Button v-if="addBtnShow" type="primary" icon="md-add" @click="addFeedBack">{{ $t('message.constants.add') }}</Button >
        </div>
        <div>
          <Input
            v-model="keyword"
            :placeholder="$t('message.newsNotice.placeholderMsg.pleaseInputSubject')"
            @input="handleInputChange"
            @keyup.enter.native="handleSearch"
            style="width: 260px;"
          >
          <Icon type="ios-search" slot="suffix" @click="handleSearch"  style="cursor: pointer;" />
          </Input>
        </div>
      </div>
      <div class="table-content">
        <Table
          stripe
          :columns="columns"
          :data="noticeList"
          :loading="loading"
          @on-sort-change="handleSortChange"
        >
          <template slot-scope="{ row }" slot="icon">
            <span v-if="row.status === 'istatus.resolved'">
              <img
                style="width: 25px;"
                src="../../../assets/images/new.gif"/>
            </span>
          </template>
          <template slot-scope="{ row }" slot="createdTime">
            <span>{{ row.createdTime ? parseTime(row.createdTime) : '' }}</span>
          </template>
          <template slot-scope="{ row }" slot="updatedTime">
            <span>{{ row.updatedTime ? parseTime(row.updatedTime) : '' }}</span>
          </template>
          <template slot-scope="{ row }" slot="operate">
            <div class="operate">
              <Button type="text" class="operate-btn" @click="gotoDetail(row)">
                {{ $t('message.newsNotice.columns.operate.button.detail') }}
              </Button>
              <Button
                type="text"
                :disabled="row.status!=='istatus.processing'"
                :class="operateButtonClass(row.status!=='istatus.processing')"
                @click="handleRecall(row)"
              >
                {{ $t('message.newsNotice.columns.operate.button.recall') }}
              </Button>
              <Button
                type="text"
                :disabled="!(row.status==='istatus.processing' || row.status==='istatus.resolved')"
                :class="operateButtonClass(!(row.status==='istatus.processing' || row.status==='istatus.resolved'))"
                @click="openFeedBack(row)"
              >
                {{ $t('message.newsNotice.columns.operate.button.appendFeedBack') }}
              </Button>
            </div>
          </template>
        </Table>
        <div class="table-page">
          <Page
            :total="page.total"
            :page-size-opts="page.sizeOpts"
            :page-size="page.size"
            :current="page.current"
            show-sizer
            show-total
            size="small"
            @on-change="change"
            @on-page-size-change="changeSize"/>
        </div>
      </div>
    </div>
    <FeedBackDialog
      ref="feedBackForm"
      :action-type="feedBackActionType"
      :feedBackFormShow="feedBackShow"
      :feedBackType="feedBackType"
      :issueId="Number(issueId)"
      @show="feedBackShowAction"
      @refresh-notice="handleSearch"
    />
  </div>
</template>
<script>
import { isEmpty, cloneDeep } from 'lodash';
import storage from '@/js/helper/storage';
import moment from 'moment';
import module from './index';
import api from '@/js/service/api';
import FeedBackDialog from '../feedBack/index.vue';

export default {
  name: 'NewsNotice',
  components: {
    FeedBackDialog
  },
  data() {
    this.statusOptions = [
      { label: this.$t('message.newsNotice.statusType.processing'), value: 'istatus.processing' },
      { label: this.$t('message.newsNotice.statusType.processed'), value: 'istatus.resolved' },
      { label: this.$t('message.newsNotice.statusType.resloved'), value: 'istatus.closed_isDelete0' },
      { label: this.$t('message.newsNotice.statusType.closed'), value: 'istatus.closed_isDelete1' },
    ];
    this.dateOptions = {
      disabledDate (date) {
        return date && date.valueOf() > Date.now();
      }
    };
    // this.operate = [
    //   { name: this.$t('message.newsNotice.columns.operate.button.detail'), value: 'detail', click: this.gotoDetail },
    //   { name: this.$t('message.newsNotice.columns.operate.button.recall'), value: 'recall', click: this.handleRecall },
    //   { name: this.$t('message.newsNotice.columns.operate.button.appendFeedBack'), value: 'appendFeedBack', click: this.openFeedBack }
    // ];
    this.columns = [
      {
        // title: null,
        // key: '',
        slot: 'icon',
        width: 40,
        align: 'left',
        renderHeader: (h) => {
          return h('span', {}, '')
        }
      },
      {
        title: this.$t('message.newsNotice.columns.subject'),
        key: 'subject',
        ellipsis: true,
        align: 'left'
      },
      {
        title: this.$t('message.newsNotice.columns.status'),
        key: 'statusName',
        align: 'center',
        width: 350,
        renderHeader: (h, params) => {
          return h('div', {
            style: { whiteSpace: 'nowrap' }
          }, [
            h('span', {
              style: { paddingRight: '6px' }
            }, '状态'),
            h('Select', {
              style: { width: '290px', textAlign: 'left' },
              props: {
                // value: params.row.statusName, // 获取选择的下拉框的值
                size: 'small',
                multiple: true
              },
              on: {
                'on-change': e => {
                  this.status = e;
                  this.handleSearch();
                }
              }
            }, this.statusOptions.map((item) => { // this.productTypeList下拉框里的data
              return h('Option', { // 下拉框的值
                props: {
                  value: item.value,
                  label: item.label
                }
              })
            }))
          ])

          // return h('Select', {
          //   props: {
          //     value: params.row.value
          //   },
          //   on： {
          //     'on-change': e=> {
          //       params.row.value = e;
          //     }
          //   }
          // },
          // this.statusOptions.map((item) => {
          //   return h('Option', {
          //     props: {
          //       value: item.value,
          //       label: item.label
          //     }
          //   })
          // }))
        }
      },
      {
        title: this.$t('message.newsNotice.columns.createdTime'),
        key: 'createdTime',
        slot: 'createdTime',
        align: 'center',
        sortable: 'custom',
      },
      {
        title: this.$t('message.newsNotice.columns.updatedTime'),
        key: 'updatedTime',
        slot: 'updatedTime',
        align: 'center',
        sortable: 'custom',
        sortType: 'desc',
      },
      {
        title: this.$t('message.newsNotice.columns.operate.colTitle'),
        slot: 'operate',
        width: 180,
        align: 'center'
      }
    ];
    return {
      keyword: '',
      status: [],
      page: {
        start: 0,
        end: 10,
        current: 1,
        size: 10,
        sizeOpts: [10, 20, 30, 50],
        total: 0,
      },
      defaultParams: {
        sortBy: 'updatedTime',
        sortOrder: 'desc',
        isDeleted: 1
      },
      issueId: '',
      feedBackShow: false,
      feedBackActionType: '',
      feedBackType: '',
      url: module.data.API_PATH,
      userName: '',
      loading: false,
      noticeList: [],
      addBtnShow: false,
      menuTitle: '',
      // tableHeight: 0,
    };
  },
  computed: {
    operateButtonClass() {
      return (disabled) => {
        return {
          'operate-btn': true,
          'disabled-color': disabled
        }
      }
    }
  },
  created() {
    const userInfo = storage.get('userInfo');
    this.userName = userInfo.basic.username;
    this.menuTitle = this.$route.query.menuName;
    this.initSearchForm();
  },
  mounted() {
    // this.tableHeight = this.$route.query.height - 90;
    this.handleSearch();
  },
  methods: {
    initSearchForm() {
      const status = this.$route.query.status;
      if (!isEmpty(status)) {
        this.addBtnShow = false;
        this.status.push(status);
      } else {
        this.addBtnShow = true;
      }
    },
    handleSearch() {
      const params = cloneDeep(this.defaultParams);
      if (!isEmpty(this.keyword)) {
        params.subject = this.keyword;
      }
      if (this.status.length > 0 && this.status.length !== this.statusOptions.length) {
        params.status = [];
        params.isDeleted = 0;
        const otherStatus = this.status.filter((item) => item !== 'istatus.closed_isDelete0' && item !== 'istatus.closed_isDelete1');
        params.status = otherStatus;
        const closeStatus = this.status.filter((item) => item === 'istatus.closed_isDelete0' || item === 'istatus.closed_isDelete1');
        if (!isEmpty(closeStatus) && closeStatus.length > 0) {
          params.status.push('istatus.closed');
          if (closeStatus.some((ele) => ele === 'istatus.closed_isDelete1')) params.isDeleted = 1;
        }
      }
      params.pageNum = this.page.current;
      params.pageSize = this.page.size;
      params.username = this.userName;
      console.log('-----查询参数---', params);
      this.loading = true;
      api.fetch(`${this.url}userFeedBacks/search`, params, 'post').then((data) => {
        console.log('-----列表数据---', data);
        this.noticeList = data.list;
        this.page.total = data.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    handleInputChange(val) {
      this.keyword = val;
    },
    // 时间排序
    handleSortChange(column, key, order) {
      this.defaultParams.sortBy = column.key;
      if (column.order === 'normal') {
        this.defaultParams.sortOrder = this.defaultParams.sortOrder;
      } else {
        this.defaultParams.sortOrder = column.order;
      }
      this.handleSearch();
    },
    // 新增用户反馈
    addFeedBack() {
      this.feedBackActionType = 'add';
      this.feedBackShow = true;
    },
    // 查看详情
    gotoDetail(row) {
      // this.$router.push({
      //   path: '/newsNotice/detail',
      //   query: {
      //     id: row.id,
      //     status: row.status
      //   }
      // });
      // this.$router.push(`/newsNotice/detail/${row.id}`);
      this.$router.push({
        path: '/noticeDetail',
        query: {
          id: row.id,
          status: row.status
        }
      });
    },
    // 撤回
    handleRecall(row) {
      this.$Modal.confirm({
        title: this.$t('message.newsNotice.confirm.title'),
        content: this.$t('message.newsNotice.confirm.recallContent'),
        onOk: async () => {
          const rst = await this.judgeWithdraw(row.id);
          if (rst) {
            this.withdraw(row.id);
          }
        },
        onCancel: () => {
        },
      });
    },
    // 校验是否能够撤回
    async judgeWithdraw(id) {
      return await api.fetch(`${this.url}userFeedBacks/${id}/judgeWithdraw`, { username: this.userName }, 'get');
    },
    withdraw(id) {
      api.fetch(`${this.url}userFeedBacks/${id}/withdraw`, { username: this.userName }, 'put').then((data) => {
        this.$Message.success(this.$t('message.newsNotice.success.recall'));
        this.handleSearch();
      }).catch(() => {
        this.loading = false;  
      });
    },
    // 追加反馈
    async openFeedBack(row) {
      // 判断能否追加反馈
      api.fetch(`${this.url}userFeedBacks/${row.id}/judgeFeedBack`, { username: this.userName }, 'get').then((data) => {
        if (data) {
          this.feedBackActionType = 'append';
          this.issueId = row.id;
          this.feedBackType = row.itype
          this.feedBackShow = true;
        } else {
          this.$Modal.warning({
            title: this.$t('message.newsNotice.confirm.title'),
            content: this.$t('message.newsNotice.confirm.judgeFeedBackContent')
          });
        }
      });
    },
    feedBackShowAction(val) {
      this.feedBackShow = val;
    },
    change(page) {
      this.page.current = page;
      this.pageingData();
    },
    changeSize(size) {
      this.page.size = size;
      this.page.current = 1;
      this.pageingData();
    },
    pageingData() {
      this.page.start = (this.page.current - 1) * this.page.size;
      this.page.end = this.page.start + this.page.size;
      this.handleSearch();
    },
    parseTime(time) {
      let newDate = '';
      if (time && time.toString().length === 13) {
        const date = new Date(parseInt(time));
        newDate = moment(date).format('YYYY-MM-DD');
      } else if (time && time.toString().indexOf('T') > 0) {
        const date = new Date(time).toJSON();
        newDate = new Date(+new Date(date) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '');
      }
      return newDate;
    },
  },
};
</script>
<style lang="scss" scoped>
.container {
  background: #f7f7f7;
  height: 100%;
  font-family: -apple-system,BlinkMacSystemFont,segoe ui,Roboto,helvetica neue,Arial,noto sans,sans-serif,apple color emoji,segoe ui emoji,segoe ui symbol,noto color emoji;
  .page-container-warp {
    height: 62px;
    line-height: 62px;
    font-size: 20px;
    font-weight: 600;
    background: #ffffff;
    padding: 0 20px;
    color: rgba(0,0,0,.85);
  }
  .page-content {
    margin: 20px;
    padding: 20px;
    background: #ffffff;
    .header {
      display: flex;
      justify-content: space-between;
      /deep/ .ivu-select-dropdown {
        max-width: none;
      }
    }
    .operate {
      .operate-btn {
        color: #2d8cf0;
      }
      .operate-btn, .operate-btn:hover, .operate-btn:focus {
        background: transparent;
        border: none;
        padding: 5px;
        box-shadow: none;
      }
      .operate-btn:hover {
        color: #57a3f3;
      }
      .disabled-color, .disabled-color:hover {
        color: #808695;
      }
    }
    .table-content {
      // display: flex;
      justify-content: center;
      align-items: center;
      margin-top: 16px;
      .ivu-table th {
        background-color: #2d8cf0;
        color: #fff;
      }
    }
    .table-page {
      margin-top: 10px;
      width: 100%;
      text-align: center;
    }
  }
}
</style>
