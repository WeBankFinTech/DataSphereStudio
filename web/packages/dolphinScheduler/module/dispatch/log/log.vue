<template>
  <span class="log-model">
    <span  v-if="stateId && item.node_type !== 'SUB_PROCESS'">
      <slot name="history"></slot>
      <span @click="_ckLog">
        <slot name="log" ></slot>
      </span>
    </span>
    <transition name="fade">
      <div v-show="isLog || source === 'list'" class="log-pop">
        <div class="log-box" >
          <div class="title">
            <span class="title-text">{{$t('message.scheduler.viewLog')}}</span>
            <div class="full-screen">
              <a href="javascript:" @click="_downloadLog" data-container="body" data-toggle="tooltip" :title="$t('message.scheduler.downloadLog')">
                <SvgIcon icon-class="download" />
              </a>
              <a href="javascript:" class="refresh-log" :class="loading ? 'active' :''" @click="!loading && _refreshLog()" data-container="body" data-toggle="tooltip" :title="$t('message.scheduler.refreshLog')">
                <SvgIcon icon-class="refresh" />
              </a>
              <a href="javascript:" @click="_screenOpen" v-show="!isScreen" data-container="body" data-toggle="tooltip" :title="$t('message.scheduler.fullScreen')">
                <SvgIcon icon-class="full-screen" />
              </a>
              <a href="javascript:" @click="_screenClose" v-show="isScreen" data-container="body" data-toggle="tooltip" :title="$t('message.scheduler.cancelScreen')">
                <SvgIcon icon-class="cancel-full-screen" />
              </a>
            </div>
          </div>
          <div class="content">
            <div class="content-log-box" >
              <textarea class="textarea-ft" id="textarea-log" style="width: 100%" spellcheck="false" ></textarea>
            </div>
          </div>
          <div class="operation">
            <Button size="small" round @click="close"> {{$t('message.scheduler.close')}} </Button>
          </div>
        </div>
      </div>
    </transition>
  </span>
</template>
<script>
/*eslint-disable */
import _ from 'lodash'
import api from '@dataspherestudio/shared/common/service/api'
import { downloadFile } from './download'

/**
 * Calculate text size
 */
const handerTextareaSize = (isH = 0) => {
  $('body').find('.tooltip.fade.top.in').remove()
  return $('.textarea-ft').css({ height: `${$('.content-log-box').height() - isH}px` })
}

let content = ''

export default {
  name: 'log',
  data () {
    return {
      api,
      isLog: false,
      stateId: $(`#${this.item.id}`).attr('data-state-id') || null,
      isScreen: false,
      loadingIndex: 0,
      isData: true,
      loading: false
    }
  },
  props: {
    item: {
      type: Object,
      default: Object
    },
    source: {
      type: String,
      default: 'from'
    },
    logId: Number
  },
  methods: {
    _refreshLog () {
      this.loading = true
      api.fetch(`dolphinscheduler/log/detail`, this._rtParam, 'get').then((data) => {
        setTimeout(() => {
          this.loading = false
          if (data) {
            this.$Message.success(this.$t('message.scheduler.updateSuccess'))
          } else {
            this.$Message.warning(this.$t('message.scheduler.noMoreLogs'))
          }
        }, 1500)
        // Handling text field size
        handerTextareaSize().html('').text(data || this.$t('message.scheduler.noLog'))
      }).catch(() => {
        this.loading = false
      })
    },
    _ckLog () {
      this.isLog = true
      api.fetch(`dolphinscheduler/log/detail`, this._rtParam, 'get').then(data => {
        if (!data) {
          this.isData = false
          setTimeout(() => {
            this.$Message.warning(this.$t('message.scheduler.noMoreLogs'))
          }, 1000)
          // Handling text field size
          handerTextareaSize().html('').text(content || this.$t('message.scheduler.noLog'))
        } else {
          this.isData = true
          content = data
          // Handling text field size
          handerTextareaSize().html('').text(content || this.$t('message.scheduler.noLog'))
        }
      }).catch(e => {
      })
    },
    _screenOpen () {
      this.isScreen = true
      let $logBox = $('.log-box')
      let winW = $(window).width() - 40
      let winH = $(window).height() - 40
      $logBox.css({
        width: winW,
        height: winH,
        marginLeft: `-${parseInt(winW / 2)}px`,
        marginTop: `-${parseInt(winH / 2)}px`
      })
      $logBox.find('.content').animate({ scrollTop: 0 }, 0)
      // Handling text field size
      handerTextareaSize().html('').text(content)
    },
    _screenClose () {
      this.isScreen = false
      let $logBox = $('.log-box')
      $logBox.attr('style', '')
      $logBox.find('.content').animate({ scrollTop: 0 }, 0)
      // Handling text field size
      handerTextareaSize().html('').text(content)
    },
    /**
     * Download log
     */
    _downloadLog () {
      downloadFile('dolphinscheduler/log/download-log', {
        taskInstanceId: this.stateId || this.logId
      })
    },
    /**
     * up
     */
    _onUp: _.debounce(function () {
      this.loadingIndex = this.loadingIndex - 1
      this._ckLog()
    }, 1000, {
      leading: false,
      trailing: true
    }),
    /**
     * down
     */
    _onDown: _.debounce(function () {
      this.loadingIndex = this.loadingIndex + 1
      this._ckLog()
    }, 1000, {
      leading: false,
      trailing: true
    }),
    /**
     * Monitor scroll bar
     */
    _onTextareaScroll () {
      let self = this
      $('#textarea-log').scroll(function () {
        let $this = $(this)
        // Listen for scrollbar events
        if (($this.scrollTop() + $this.height()) === $this.height()) {
          if (self.loadingIndex > 0) {
            self.$Message.info(self.$t('message.scheduler.logLoading'))
            self._onUp()
          }
        }
        // Listen for scrollbar events
        if ($this.get(0).scrollHeight === ($this.height() + $this.scrollTop())) {
          // No data is not requested
          if (self.isData) {
            self.$Message.info(self.$t('message.scheduler.logLoading'))
            self._onDown()
          }
        }
      })
    },
    /**
     * close
     */
    close () {
      $('body').find('.tooltip.fade.top.in').remove()
      this.isScreen = false
      this.isLog = false
      content = ''
      this.$emit('close')
    }
  },
  watch: {},
  created () {
    // Source is a task instance
    if (this.source === 'list') {
      //this.$Message.info(this.$t('message.scheduler.logLoading'))
      this._ckLog()
    }
  },
  mounted () {
    this._onTextareaScroll()
  },
  updated () {
  },
  computed: {
    _rtParam () {
      return {
        taskInstanceId: this.stateId || this.logId,
        skipLineNum: parseInt(`${this.loadingIndex ? this.loadingIndex + '000' : 0}`),
        limit: parseInt(`${this.loadingIndex ? this.loadingIndex + 1 : 1}000`)
      }
    }
  },
  components: { }
}
</script>

<style lang="scss" rel="stylesheet/scss">
  .log-pop {
    position: fixed;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,.4);
    z-index: 100;
    .log-box {
      width: 660px;
      height: 520px;
      background: #fff;
      border-radius: 3px;
      position: absolute;
      left:50%;
      top: 50%;
      margin-left: -340px;
      margin-top: -250px;
      .title {
        height: 50px;
        border-bottom: 1px solid #dcdedc;
        background-color: #FAFAFA;
        .title-text {
          font-size: 16px;
          color: #333;
          padding-left: 20px;
          display: inline-block;
          padding-top: 14px;
        }
        .full-screen {
          position: absolute;
          right: 20px;
          top: 17px;
          a{
            color: #0097e0;
            margin-left: 15px;
            font-size: 20px;
            em {
              font-size: 17px;
              font-weight: 400;
              text-decoration: none !important;
              vertical-align: middle;
            }
          }
          .clock {
            >em {
              font-size: 20px;
              vertical-align: middle;
              transform: scale(1);
            }
          }
          .refresh-log {
            >em {
              text-decoration: none;
              font-size: 20px;
              vertical-align: middle;
              transform: scale(1);
            }
            &.active {
              >em {
                -webkit-transition-property: -webkit-transform;
                -webkit-transition-duration: 1s;
                -moz-transition-property: -moz-transform;
                -moz-transition-duration: 1s;
                -webkit-animation: rotateloading .4s linear infinite;
                -moz-animation: rotateloading .4s linear infinite;
                -o-animation: rotateloading .4s linear infinite;
                animation: rotateloading .4s linear infinite;
                transform: scale(.4);
                color: #999;
              }
            }
          }
        }
      }
      .content {
        height: calc(100% - 100px);
        background: #002A35;
        padding:6px 2px;
        .content-log-box {
          width: 100%;
          height: 100%;
          word-break:break-all;
          textarea {
            background: none;
            color: #9CABAF;
            border: 0;
            font-family: 'Microsoft Yahei,Arial,Hiragino Sans GB,tahoma,SimSun,sans-serif';
            font-weight: bold;
            resize:none;
            line-height: 1.6;
            padding: 0px;
          }
        }
      }
      .operation {
        text-align: right;
        height: 50px;
        line-height: 44px;
        border-top: 1px solid #dcdedc;
        padding-right: 20px;
        background: #fff;
        position: relative;
      }
    }
  }
  @-webkit-keyframes rotateloading{from{-webkit-transform: rotate(0deg)}
    to{-webkit-transform: rotate(360deg)}
  }
  @-moz-keyframes rotateloading{from{-moz-transform: rotate(0deg)}
    to{-moz-transform: rotate(359deg)}
  }
  @-o-keyframes rotateloading{from{-o-transform: rotate(0deg)}
    to{-o-transform: rotate(359deg)}
  }
  @keyframes rotateloading{from{transform: rotate(0deg)}
    to{transform: rotate(359deg)}
  }
</style>
