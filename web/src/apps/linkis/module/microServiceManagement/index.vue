<template>
  <div>
    <Row class="search-bar">
      <Col span="6">
      <Input v-model="searchName" suffix="ios-search" class="input" placeholder="实例名称" @on-enter="search"></Input>
      </Col>
      <Col span="6" class="search-item">
      <span class="lable">类型：</span>
      <Select v-model="searchType" class="input">
        <Option value="ALL">全部</Option>
      </Select>
      </Col>
      <Col span="6" class="search-item">
      <span class="lable">状态：</span>
      <Select v-model="searchStatus" class="input">
        <Option value="ALL">全部</Option>
      </Select>
      </Col>
      <Col span="6" class="search-item">
      <Button class="search" type="primary" @click="search">查询</Button>
      </Col>
    </Row>
    <Table
      border
      :columns="tableColumnNum"
      :data="pageDatalist"
      class="table-content">
      <template
        slot-scope="{row}"
        slot="name"
      >
        <span>{{row.name}}</span>
      </template>
      <template
        slot-scope="{row}"
        slot="status"
      >
        <span>{{row.status}}</span>
      </template>
      <template
        slot-scope="{row}"
        slot="tag"
      >
        <div class="tag-box">
          <span class="tag-item" v-for="item in row.tag.split(',')" :key="item">{{ item }}</span>
        </div>
      </template>
      <template
        slot-scope="{row, index}"
        slot="action"
      >
        <Button type="text" @click="modify(row, index)">编辑</Button>
      </template>
    </Table>
  </div>
</template>
<script>
export default {
  components: {
  },
  data() {
    return {
      searchName: '',
      searchType: '',
      searchStatus: '',
      page: {
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      },
      tableColumnNum: [
        {
          title: '实例名称',
          key: 'name',
          slot: 'name',
          minWidth: 120,
        },
        {
          title: '状态',
          key: 'status',
          slot: 'status',
          minWidth: 80,
          align: 'center',
        },
        {
          title: '标签',
          key: 'tag',
          slot: 'tag',
          align: 'center',
          tooltip: true,
          minWidth: 80,
        },
        {
          title: '类型',
          key: 'type',
          tooltip: true,
          minWidth: 240,
        },
        {
          title: '启用者',
          key: 'staruser',
          align: 'center',
          tooltip: true,
          minWidth: 80,
        },
        {
          title: '启动时间',
          key: 'time',
          align: 'center',
          tooltip: true,
          minWidth: 80,
        },
        {
          title: '操作',
          slot: 'action',
          minWidth: 120,
        },
      ],
      pageDatalist: [
        {
          name: '海拉鲁大流氓',
          status: '正常',
          tag: '勇者',
          type: '战士',
          staruser: '塞尔达',
          time: '100',

        },
        {
          name: '海拉鲁大流氓',
          status: '正常',
          tag: '勇者',
          type: '战士',
          staruser: '塞尔达',
          time: '100',

        },
        {
          name: '海拉鲁大流氓',
          status: '正常',
          tag: '勇者',
          type: '战士',
          staruser: '塞尔达',
          time: '100',

        }
      ]
    }
  },
  methods: {
    search() {

    },
    modify(data, index) {
      console.log(data, index, 'data')
    }
  }
}
</script>
<style lang="scss" src="./index.scss" scoped></style>