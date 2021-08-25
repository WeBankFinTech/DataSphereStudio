<template>
  <div class="assets-index-wrap">
    <!-- top 搜索框 -->
    <div class="assets-index-t">
      <!-- bottom -->
      <div class="assets-index-t-b1">
        <div class="assets-index-t-b1-search">
          <Input
            search
            :enter-button="$t(`message.dataGovernance.search`)"
            :placeholder="$t(`message.dataGovernance.pleaseEnterATableName`)"
            size="large"
            on-search="onSearch"
          />
        </div>
      </div>
    </div>

    <!-- bottom 复选框搜索框 + card tabs -->
    <div class="assets-index-b">
      <!-- left -->
      <div class="assets-index-b-l">
        <div class="assets-index-b-l-title">
          <span>筛选条件</span>
        </div>
        <div class="assets-index-b-l-user">
          <span>负责人</span>
          <Select v-model="serachOption.user" style="width:167px">
            <Option
              v-for="item in userList"
              :value="item.value"
              :key="item.value"
              >{{ item.label }}</Option
            >
          </Select>
        </div>
        <div class="assets-index-b-l-env">
          <span>环境</span>
          <Radio v-model="serachOption.env"
            ><label
              style="color: rgba(0,0,0,0.65; fontSize: 14px; fontWeight: normal;"
              >生产环境</label
            ></Radio
          >
        </div>
        <div class="assets-index-b-l-label">
          <span>标签</span>
          <Select v-model="serachOption.label" multiple style="width:167px">
            <Option
              v-for="item in userList"
              :value="item.value"
              :key="item.value"
              >{{ item.label }}</Option
            >
          </Select>
        </div>
      </div>

      <!-- right -->
      <div class="assets-index-b-r">
        <template v-for="model in cardTabs">
          <tab-card
            :model="model"
            :key="model.id"
            @on-choose="onChooseCard"
          ></tab-card>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
//import api from "@/common/service/api";
import tabCard from "../../module/common/tabCard/index.vue";
export default {
  components: {
    tabCard
  },
  data() {
    return {
      serachOption: {},
      userList: [
        {
          value: "New York",
          label: "New York"
        },
        {
          value: "London",
          label: "London"
        },
        {
          value: "Sydney",
          label: "Sydney"
        }
      ],
      cardTabs: [
        {
          id: 1,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        },
        {
          id: 2,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        },
        {
          id: 3,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        },
        {
          id: 4,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        },
        {
          id: 5,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        },
        {
          id: 6,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        },
        {
          id: 7,
          tableName: "table1",
          readCount: 36,
          userName: "xxx",
          ctime: "xxx",
          utime: "xxx"
        }
      ]
    };
  },
  created() {},
  methods: {
    // 面包屑相关
    selectProject() {},
    onChooseWork() {},
    onRemoveWork() {},

    // 搜索
    onSearch() {},

    onChooseCard(model) {
      console.log("on-choose-card", model);
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.assets-index-wrap {
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
    height: 80px;
    background-color: #edf1f6;
    display: flex;
    justify-content: center;
    align-items: center;
    &-search {
      min-width: 552px;
      min-height: 40px;
    }
  }

  .assets-index-b {
    display: flex;
    &-l {
      min-width: 199px;
      padding-left: 16px;
      padding-right: 16px;
      padding-top: 15px;
      &-title {
        font-family: PingFangSC-Medium;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        font-weight: bold;
      }
      &-user {
        margin-top: 16px;
        span {
          display: block;
          margin-bottom: 8px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.85);
        }
      }
      &-env {
        margin-top: 16px;
        span {
          display: block;
          margin-bottom: 8px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.85);
        }
      }
      &-label {
        margin-top: 16px;
        span {
          display: block;
          margin-bottom: 8px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.85);
        }
      }
    }
    &-r {
      flex: 1;
    }
  }
}
</style>
