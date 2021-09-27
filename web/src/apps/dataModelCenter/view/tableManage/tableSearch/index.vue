<template>
  <div class="page-content">
    <div style="margin-bottom: 16px">
      <div class="flex-row-center top-title">
        <Icon type="md-grid" color="#2d8cf0" :size="50" class="top-icon" />
        <span class="top-text"> 数据表管理 </span>
      </div>
      <div class="flex-row-center">
        <Input
          style="width: 600px"
          v-model="searchParams.searchToken"
          size="large"
          search
          enter-button="搜索"
          placeholder="输入关键字进行搜索"
        >
          <Select
            v-model="searchParams.searchType"
            slot="prepend"
            style="width: 80px"
            placeholder="搜索类型"
          >
            <Option
              v-for="item of searchTypeOption"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </Option>
          </Select>
        </Input>
      </div>
    </div>
    <Row :gutter="15">
      <Col :span="5">
        <Card>
          <p slot="title">我的收藏</p>
        </Card>
        <Card>
          <p slot="title">数仓筛搜索</p>
          <div class="flex-row-center" style="margin-bottom: 12px">
            <div style="width: 75px">主题域：</div>
            <Select v-model="model1" style="flex: 1">
              <Option :value="1">选项</Option>
            </Select>
          </div>
          <div class="flex-row-center">
            <div style="width: 75px">分层：</div>
            <Select v-model="model1" style="flex: 1">
              <Option :value="1">选项</Option>
            </Select>
          </div>
        </Card>
        <Card>
          <p slot="title">按库搜索</p>
          <Input search placeholder="请输入数据库名称"></Input>
          <CellGroup>
            <Cell title="databaseic1">
              <Icon slot="icon" type="md-menu" :size="24" />
            </Cell>
            <Cell title="databaseic2">
              <Icon slot="icon" type="md-menu" :size="24" />
            </Cell>
            <Cell title="databaseic3">
              <Icon slot="icon" type="md-menu" :size="24" />
            </Cell>
          </CellGroup>
        </Card>
      </Col>
      <Col :span="19">
        <div class="page-content searc-data-box">
          <div style="margin-bottom: 16px">
            <span class="title">搜索结果</span>
          </div>
          <SearchResDataTable />
        </div>
      </Col>
    </Row>
  </div>
</template>

<script>
import SearchResDataTable from "./searchResDataTable.vue";
export default {
  components: {
    SearchResDataTable,
  },
  data() {
    return {
      searchParams: {
        searchToken: "",
        searchType: "",
      },
      searchResData: [],
      searchTypeOption: [
        {
          label: "按表名",
          value: "1",
        },
        {
          label: "按指标名",
          value: "1",
        },
        {
          label: "按维度名",
          value: "1",
        },
        {
          label: "按度量名",
          value: "1",
        },
        {
          label: "按表描述",
          value: "1",
        },
      ],
    };
  },
};
</script>

<style lang="scss" scoped>
@import "../../../assets/styles/common.scss";
.flex-row-center {
  display: flex;
  justify-content: center;
  align-items: center;
}
.top-title {
  margin-bottom: 16px;
  .top-icon {
    margin-right: 16px;
  }
  .top-text {
    font-size: 28px;
  }
}
.searc-data-box {
  border: 1px solid #e8eaec;
  .title {
    font-size: 18px;
    border-bottom: 3px solid #3c7aff;
  }
}
</style>
