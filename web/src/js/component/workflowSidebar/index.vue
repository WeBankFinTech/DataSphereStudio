<template>
  <Row class="classify">
    <Col
      span="6"
      v-for="(item,index) in classifyList"
      class="category-col"
      :key="index">
    <Card
      class="card"
      @click.native="active(index,item)"
    >
      <div
        v-if="!readonly"
        slot="extra"
        class="classify-action">
        <Icon
          type="ios-build"
          size="18"
          @click.stop="projectModify(item)"></Icon>
        <Icon
          type="ios-trash"
          size="18"
          v-if="defaultCheck(item.name)"
          @click.stop="projectDelete(item)"></Icon>
      </div>
      <span>{{item.name}}</span>
    </Card>
    </Col>
  </Row>
</template>
<script>
export default {
  props: {
    classifyList: {
      type: Array,
      default: null,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      activeIndex: 0,
    };
  },
  watch: {
    classifyList: {
      handler(val) {
        this.activeIndex = 0;
      },
      deep: true,
    },
  },
  methods: {
    addClassify() {
      this.$emit('addClassify');
    },
    projectModify(item) {
      this.$emit('modifyClassify', item);
    },
    projectDelete(item) {
      this.$emit('projectDelete', item);
    },
    active(index, item) {
      this.activeIndex = index;
      this.$emit('active', item)
    },
    defaultCheck(name) {
      switch (name) {
        case '我的工程':
        case '我参与的工程':
        case '我的工作流':
        case '我参与的工作流':
          return false;
        default:
          return true;
      }
    },
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
