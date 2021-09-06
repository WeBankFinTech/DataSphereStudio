<template>
  <Modal
    v-model="isFieldModalShow"
    :title="$t('message.scripts.createTable.selectResource')"
    class="field-modal"
    width="320"
  >
    <virtual-list
      ref="fieldList"
      :size="28"
      :remain="fieldList.length > 10 ? 10 : fieldList.length"
      wtag="ul"
      v-if="activedFieldItem">
      <li
        v-for="(item, index) in fieldList"
        :key="index"
        class="field-modal-li">
        <input
          :checked="activedFieldItem.sourceFields.indexOf(item.name) !== -1"
          :value="item.name"
          type="checkbox"
          @change="hanlderCheck(item)">
        <span
          class="field-modal-li-label"
          @click="hanlderCheck(item)">{{ item.name }}</span>
      </li>
    </virtual-list>
  </Modal>
</template>
<script>
import virtualList from '@/components/virtualList';
export default {
  components: {
    virtualList,
  },
  props: {
    fieldList: {
      type: Array,
      default: () => [],
    },
    activedFieldItem: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      isFieldModalShow: false,
    };
  },
  methods: {
    open() {
      this.$nextTick(() => {
        this.isFieldModalShow = true;
      });
    },
    close() {
      this.isFieldModalShow = false;
    },
    hanlderCheck(item) {
      const isIn = this.activedFieldItem.sourceFields.indexOf(item.name);
      if (isIn === -1) {
        this.activedFieldItem.sourceFields.push(item.name);
      } else {
        this.activedFieldItem.sourceFields.splice(isIn, 1);
      }
    },
  },
};
</script>
<style lang="scss">
.field-modal {
    font-size: 12px;
    .field-modal-li {
        height: 28px;
        line-height: 28px;
        display: flex;
        align-items: center;
    }
    .field-modal-li-label {
        margin-left: 10px;
        display: inline-block;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;
    }
}
</style>
