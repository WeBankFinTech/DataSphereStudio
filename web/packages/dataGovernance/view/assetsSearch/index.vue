<template>
  <div class="assets-index-wrap">
    <!-- top 搜索框 -->
    <div class="assets-index-t">
      <!-- bottom -->
      <div class="assets-index-t-b1">
        <Row>
          <Col :span="24" :style="{ display: 'flex' }">
            <div class="assets-index-t-b1-label"><span>全局搜索</span></div>
            <div class="assets-index-t-b1-content">
              <Input
                :placeholder="
                  $t(`message.dataGovernance.pleaseEnterATableName`)
                "
                v-model="queryForTbls"
              >
              </Input>
            </div>
          </Col>
        </Row>
        <Row :style="{ 'margin-top': '16px' }">
          <Col :span="8" :style="{ display: 'flex' }">
            <div class="assets-index-t-b1-label">
              <span>主题域/分层</span>
            </div>
            <div class="assets-index-t-b1-content">
              <Select
                v-model="searchOption.classification"
                clearable
                style="width: 285px"
                @on-change="selectSubject"
              >
                <OptionGroup label="主题域">
                  <Option
                    v-for="(item, idx) in subjectList"
                    :value="item.name"
                    :key="idx"
                  >{{ item.name }}</Option
                  >
                </OptionGroup>
                <OptionGroup label="分层">
                  <Option
                    v-for="(item, idx) in layerList"
                    :value="item.name"
                    :key="idx"
                  >{{ item.name }}</Option
                  >
                </OptionGroup>
              </Select>
            </div>
          </Col>
          <Col :span="8" :style="{ display: 'flex' }">
            <div class="assets-index-t-b1-label"><span>负责人</span></div>
            <div class="assets-index-t-b1-content">
              <Select
                v-model="searchOption.owner"
                filterable
                clearable
                remote
                :remote-method="remoteSearchOwner"
                :loading="owerSerachLoading"
                style="width: 285px"
                @on-change="selectOwner"
              >
                <Option
                  v-for="(item, idx) in ownerList"
                  :value="item.value"
                  :key="idx"
                >{{ item.label }}</Option
                >
              </Select>
            </div>
          </Col>
          <Col :span="8">
            <div :style="{ display: 'flex', 'margin-left': '32px' }">
              <Button @click="onReset">重置</Button>
              <Button
                type="primary"
                :style="{ 'margin-left': '8px' }"
                @click="onSearch"
              >查询</Button
              >
            </div>
          </Col>
        </Row>
      </div>
    </div>

    <!-- <div class="assets-index-t-total">
      <span>共条信息</span>
    </div> -->

    <!-- bottom 复选框搜索框 + card tabs -->
    <div class="assets-index-b">
      <!-- left -->
      <!-- <div class="assets-index-b-l"> -->
      <!-- <div class="assets-index-b-l-title">
          <span>筛选条件</span>
        </div>
        <div class="assets-index-b-l-user">
          <span>负责人</span>
          <Select
            v-model="searchOption.owner"
            filterable
            clearable
            remote
            :remote-method="remoteSearchOwner"
            :loading="owerSerachLoading"
            style="width: 167px"
            @on-change="selectOwner"
          >
            <Option
              v-for="(item, idx) in ownerList"
              :value="item.value"
              :key="idx"
              >{{ item.label }}</Option
            >
          </Select>
        </div> -->
      <!--<div class="assets-index-b-l-env">
          <span>环境</span>
          <Radio v-model="searchOption.env"
            ><label
              style="color: rgba(0,0,0,0.65; fontSize: 14px; fontWeight: normal;"
              >生产环境</label
            ></Radio
          >
        </div>-->
      <!-- <div class="assets-index-b-l-label">
          <span>主题域/分层</span>
          <Select
            v-model="searchOption.classification"
            clearable
            style="width: 167px"
            @on-change="selectSubject"
          >
            <OptionGroup label="主题域">
              <Option
                v-for="(item, idx) in subjectList"
                :value="item.name"
                :key="idx"
                >{{ item.name }}</Option
              >
            </OptionGroup>
            <OptionGroup label="分层">
              <Option
                v-for="(item, idx) in layerList"
                :value="item.name"
                :key="idx"
                >{{ item.name }}</Option
              >
            </OptionGroup>
          </Select>
        </div> -->
      <!-- </div> -->

      <!-- right -->
      <div class="assets-index-b-r" v-if="cardTabs.length">
        <Scroll :on-reach-bottom="handleReachBottom" height="100vh">
          <tab-card
            v-for="model in cardTabs"
            :model="model"
            :key="model.guid"
            @on-choose="onChooseCard"
          ></tab-card>
        </Scroll>
        <Divider v-if="isCompleted" orientation="center">到底了</Divider>
      </div>
      <div class="assets-index-b-r" v-else>
        <div style="text-align: center; margin-top: 50px; font-weight: bolder">
          暂无数据
        </div>
      </div>
    </div>
  </div>
</template>
<script>
//import api from '@dataspherestudio/shared/common/service/api';
import tabCard from "../../module/common/tabCard/index.vue";
import {
  getHiveTbls,
  getWorkspaceUsers,
  getLayersAll,
  getThemedomains,
} from "../../service/api";
import { EventBus } from "../../module/common/eventBus/event-bus";
import { storage } from "../../utils/storage";
import { throttle } from "lodash";
export default {
  components: {
    tabCard,
  },
  data() {
    return {
      searchOption: {
        limit: 10,
        offset: 0,
        owner: "",
        classification: "",
        query: "",
      },
      ownerList: [],
      userList: [],
      cardTabs: [],
      queryForTbls: "",
      owerSerachLoading: false,
      isLoading: false,
      subjectList: [],
      layerList: [],
      isCompleted: false,
    };
  },
  created() {
    let searchParams = storage.getItem("searchTbls");
    if (searchParams) {
      const searchData = JSON.parse(searchParams);
      this.queryForTbls = searchData.query;
      this.searchOption.classification = searchData.classification;
      this.searchOption.owner = searchData.owner;
      getHiveTbls(JSON.parse(searchParams))
        .then((data) => {
          if (data.result) {
            if( data.result.length == this.searchOption.limit ) {
              this.isCompleted = false
            } else {
              this.isCompleted = true
            }
            this.cardTabs = data.result;
          }
        })
        .catch((err) => {
          console.log("Search", err);
        });
    } else {
      getHiveTbls({ query: "", limit: 10, offset: 0 })
        .then((data) => {
          if (data.result) {
            if( data.result.length == this.searchOption.limit ) {
              this.isCompleted = false
            } else {
              this.isCompleted = true
            }
            this.cardTabs = data.result;
          }
        })
        .catch((err) => {
          console.log("Search", err);
        });
    }
  },
  mounted() {
    let _this = this;
    this.throttleLoad = throttle(() => {
      console.log("scroll");
      _this.scrollHander();
    }, 300);
    window.addEventListener("scroll", this.throttleLoad, false);
    getThemedomains().then((res) => {
      let { result } = res;
      this.subjectList = result;
    });
    getLayersAll().then((res) => {
      let { result } = res;
      this.layerList = result;
    });
  },
  destroyed() {
    window.removeEventListener("scroll", this.throttleLoad);
  },
  methods: {
    //  重置
    onReset() {
      this.queryForTbls = '';
      this.searchOption.classification = '';
      this.searchOption.owner = ''
      this.searchOption.query = ''
    },
    // 搜索
    onSearch() {
      const params = {
        query: this.queryForTbls,
        owner: this.searchOption.owner,
        classification: this.searchOption.classification,
        limit: 10,
        offset: 0,
      };
      this.searchOption["limit"] = 10;
      this.searchOption["offset"] = 0;
      storage.setItem("searchTbls", JSON.stringify(params));
      this.isLoading = false;
      if( this.queryForTbls || params.classification) {
        EventBus.$emit("onQueryForHighLight", this.queryForTbls);
      }
      getHiveTbls(params)
        .then((data) => {
          if (data.result) {
            if( data.result.length == this.searchOption.limit ) {
              this.isCompleted = false
            } else {
              this.isCompleted = true
            }
            if( this.queryForTbls ) {
              this.cardTabs = this.foamtDataToHighLigth(data.result, this.queryForTbls)
            } else {
              this.cardTabs = data.result;
            }
          } else {
            this.cardTabs = [];
          }
        })
        .catch((err) => {
          console.log("Search", err);
        });
    },

    onChooseCard(model) {
      let that = this;
      EventBus.$emit("on-choose-card", model);
      const workspaceId = that.$route.query.workspaceId;
      const { guid } = model;
      that.$router.push({
        name: "dataGovernance/assets/info",
        params: { guid },
        query: { workspaceId },
      });
    },

    // 下拉到底部加载
    handleReachBottom() {
      let that = this;
      const res = that.cardTabs.slice(0);
      if (that.isLoading) {
        return that.$Message.success("所有数据已加载完成");
      }
      return new Promise((resolve) => {
        const params = {
          query: that.queryForTbls,
          owner: that.searchOption.owner,
          limit: that.searchOption.limit,
          offset: that.searchOption.offset + that.searchOption.limit,
          classification: that.classification,
        };
        that.searchOption.offset += that.searchOption.limit;
        getHiveTbls(params)
          .then((data) => {
            if (data.result) {
              if( data.result.length == this.searchOption.limit ) {
                this.isCompleted = false
              } else {
                this.isCompleted = true
              }
              that.cardTabs = res.concat(data.result);
            } else {
              that.$Message.success("所有数据已加载完成");
              that.isLoading = true;
            }
            resolve();
          })
          .catch((err) => {
            console.log("handleReachBottom", err);
          });
      });
    },

    // 搜索负责人
    remoteSearchOwner(query) {
      let that = this;
      if (query !== "") {
        that.owerSerachLoading = true;
        let workspaceId = that.$route.query.workspaceId;
        getWorkspaceUsers(workspaceId, query)
          .then((data) => {
            const { result } = data;
            const _res = [];
            if (result) {
              result.forEach((item) => {
                let o = Object.create(null);
                o["value"] = item;
                o["label"] = item;
                _res.push(o);
              });
              that.ownerList = _res;
              that.owerSerachLoading = false;
              console.log("ownerList", that.ownerList);
            }
          })
          .catch((err) => {
            console.log("getWorkspaceUsers", err);
          });
      } else {
        that.ownerList = [];
      }
    },

    // 下拉加載
    scrollHander() {
      const getScrollTop = () => {
        var scrollTop = 0;
        if (document.documentElement && document.documentElement.scrollTop) {
          scrollTop = document.documentElement.scrollTop;
        } else if (document.body) {
          scrollTop = document.body.scrollTop;
        }
        return scrollTop;
      };
      const getClientHeight = () => {
        var clientHeight = 0;
        if (
          document.body.clientHeight &&
          document.documentElement.clientHeight
        ) {
          clientHeight = Math.min(
            document.body.clientHeight,
            document.documentElement.clientHeight
          );
        } else {
          clientHeight = Math.max(
            document.body.clientHeight,
            document.documentElement.clientHeight,
            document.querySelector(".layout").clientHeight
          );
        }
        return clientHeight;
      };
      const getScrollHeight = () => {
        return Math.max(
          document.body.scrollHeight,
          document.documentElement.scrollHeight
        );
      };
      let st = getScrollTop();
      let ch = getClientHeight();
      let sh = getScrollHeight();
      console.log(st, ch, sh);
      if (st + ch + 54 >= sh) {
        // 拉数据
        this.handleReachBottom();
      }
    },

    // 搜索負責人
    selectOwner(value) {
      this.searchOption.owner = value;
      this.onSearch();
    },

    // 搜索主題域/分層
    selectSubject(value) {
      if (value) {
        this.searchOption.classification = value;
      } else {
        delete this.searchOption.classification;
      }
      this.onSearch();
    },

    // 处理高亮 并过滤没有 高亮 的结果
    foamtDataToHighLigth(result, query) {
      const _result = result.slice() || []
      const _query = `<span>${query}</span>`
      const reg = new RegExp(query, 'g')
      const filter_arr = []
      _result.forEach((item, idx) => {
        let flag = false
        Object.keys(item).forEach(key => {
          if( key == 'classifications' && item['classifications'] && item['classifications'].length > 0 ) {
            item['classifications'].forEach(item => {
              if( item['typeName'].indexOf(query) > -1 ) {
                flag = true
              }
            })
          }
          if( typeof item[key] == 'string' && item[key].indexOf(query) > -1 ) {
            item[key] = item[key].replace(reg, _query)
            flag = true
          }
          if( item[key] instanceof Array && item[key].length > 0 && typeof item[key][0] == 'string' && item[key].join('@').indexOf(query) > -1 ) {
            item[key] = item[key].join('@').replace(reg, _query).split('@')
            flag = true
          }
        })
        if( !flag ){
          filter_arr.push(idx)
        }
      })
      return  _result.filter((item, idx) => !filter_arr.includes(idx))
    }
  },
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";

.assets-index-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  .assets-index-t-t1 {
    padding: 0px $padding-25;
    border-bottom: $border-width-base $border-style-base $border-color-base;
    @include border-color(
      $background-color-base,
      $dark-workspace-body-bg-color
    );
    @include font-color($workspace-title-color, $dark-workspace-title-color);
    flex: none;
    display: flex;
    align-items: center;
    font-size: $font-size-large;
    .top-l-text {
      cursor: pointer;
      flex: none;
      font-size: $font-size-large;
      padding: 0 15px;
      margin-bottom: -1px;
      line-height: 40px;
      position: relative;
      &::after {
        content: "";
        border-left: 1px solid #dee4ec;
        @include border-color($border-color-base, $dark-border-color-base);
        width: 0;
        position: absolute;
        right: -15px;
        top: 12px;
        height: 16px;
        margin: 0 15px;
      }
    }
    .active {
      border-bottom: 2px solid $primary-color;
      @include border-color($primary-color, $dark-primary-color);
    }
    .top-r-container {
      flex: 1;
      height: 40px;
    }
  }

  .assets-index-t-b1 {
    min-height: 112px;
    @include bg-color(#f4f7fb, $dark-base-color);
    padding: 16px 24px;
    &-label {
      width: 100px;
      margin-right: 8px;
      height: 32px;
      line-height: 32px;

      font-size: 14px;
      color: rgba(0, 0, 0, 0.85);
      text-align: right;
      font-weight: 400;
    }
    &-content {
      flex: 1;
    }
  }

  .assets-index-t-total {
    height: 38px;
    line-height: 38px;
    text-align: right;
    padding-right: 25px;
    border-bottom: 1px solid #DEE4EC;
    border-top: 1px solid #DEE4EC;
  }

  .assets-index-b {
    display: flex;
    flex: 1;
    &-l {
      min-width: 199px;
      padding-left: 16px;
      padding-right: 16px;
      padding-top: 15px;
      border-right: 1px solid #dee4ec;
      @include border-color(#dee4ec, $dark-border-color-base);
      &-title {

        font-size: 14px;
        @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
        font-weight: bold;
      }
      &-user {
        margin-top: 16px;
        span {
          display: block;
          margin-bottom: 8px;

          font-size: 14px;
          @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
        }
      }
      &-env {
        margin-top: 16px;
        span {
          display: block;
          margin-bottom: 8px;

          font-size: 14px;
          @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
        }
      }
      &-label {
        margin-top: 16px;
        span {
          display: block;
          margin-bottom: 8px;

          font-size: 14px;
          @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
        }
      }
    }
    &-r {
      flex: 1;
      padding-bottom: 10px;
    }
  }
  ::v-deep .ivu-input-group-prepend,
  .ivu-input-group-append {
    padding: 0px;
    border: none;
    background-color: transparent;
  }

  ::v-deep .ivu-input-group .ivu-input,
  .ivu-input-group .ivu-input-inner-container {
    margin-left: 8px;
  }
}

</style>
