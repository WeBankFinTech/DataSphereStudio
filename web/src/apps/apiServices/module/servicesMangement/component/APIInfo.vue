<template>
  <div>
    <Row>
      <Col span="24" v-for="(item, index) in dataList" :key="index">
      <div class="contetn" :title="item.value">
        <span class="label">{{ item.label }}</span>
        <span class="content-item">{{ item.value }}</span>
      </div>
      </Col>
    </Row>
    <slot></slot>
  </div>
</template>
<script>
export default {
  props: {
    /**
     * 需要渲染的字段
     * 数据格式 [ { label: '我是lable', value: '我是value' }, { label: '我是lable', value: '我是value' } ]
     */
    dataList: {
      type: Array
    }
  },
  data() {
    return {

    }

  },
  created() {

  },
  methods: {

  }
}
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
  .contetn {
    padding: 10px 15px;
    box-sizing: border-box;
    line-height: $line-height-base;
    font-size: $font-size-base;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    .label {
      margin-right: $margin;
    }
    .content-item {
      font-weight: 700;
    }
  }
</style>

