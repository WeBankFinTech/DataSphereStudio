<template>
  <Modal
    v-model="show"
    width="360"
    class-name="delete-modal">
    <p
      slot="header"
      class="delete-modal-header">
      <Icon type="ios-information-circle"/>
      <span>{{ label }}{{$t('message.deleteDialog.waring')}}</span>
    </p>
    <div class="delete-modal-content">
      <p>
        <span>{{$t('message.deleteDialog.action', {label:label})}}</span>
        <span class="delete-modal-content-type">{{ type }}</span>
        <span class="delete-modal-content-name">{{ name }}</span>
      </p>
      <p>{{$t('message.deleteDialog.isNext')}}</p>
    </div>
    <div slot="footer">
      <Button
        :loading="loading"
        type="error"
        size="large"
        long
        @click="del">{{ [$t('message.deleteType.engine'), $t('message.deleteType.task'), $t('message.deleteType.engineAndTask')].indexOf(type) !== -1 ? $t('message.deleteDialog.action', {type}) : $t('message.newConst.delete') }}</Button>
    </div>
  </Modal>
</template>
<script>
export default {
  props: {
    loading: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      show: false,
      type: '',
      name: '',
    };
  },
  computed: {
    label() {
      return [this.$t('message.deleteType.engine'), this.$t('message.deleteType.task'), this.$t('message.deleteType.engineAndTask')].indexOf(this.type) !== -1 ? this.$t('message.deleteDialog.overThe') : this.$t('message.newConst.delete');
    },
  },
  watch: {
    'loading': function(val) {
      if (!val) {
        this.show = false;
      }
    },
  },
  methods: {
    open(opt) {
      this.show = true;
      let { type, name } = opt;
      this.type = type;
      this.name = name;
    },
    close() {
      this.show = false;
    },
    del() {
      this.$emit('delete', this.type);
    },
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
