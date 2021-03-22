<template>
  <div class="create-table">
    <Card>
      <p slot="title">
        {{$t('message.scripts.createTable.baseAttr')}}
      </p>
      <Button
        slot="extra"
        type="primary"
        size="small"
        @click.prevent="addFieldsItem">{{$t('message.scripts.createTable.add')}}</Button>
      <Form
        ref="fieldsForm"
        :model="target.newFieldsData">
        <fields-table
          :fields="target.newFieldsData.fields"
          :table-columns="fieldsConfig"
          :can-be-delete="true"
          @on-delete="delteFields"></fields-table>
      </Form>
    </Card>
    <Card v-if="partitionTable">
      <p slot="title">
        {{$t('message.scripts.createTable.partitionAttr')}}
      </p>
      <Form
        ref="partForm"
        :model="target.newPartitionsData">
        <fields-table
          :fields="target.newPartitionsData.fields"
          :table-columns="partitionsConfig"></fields-table>
      </Form>
    </Card>
  </div>
</template>
<script>
import config from '../config.js';
import fieldsTable from '@/components/virtualTable/fieldTable/fieldsTable.vue';
export default {
  components: {
    fieldsTable,
  },
  props: {
    target: {
      type: Object,
      required: true,
    },
    partitionTable: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      fieldsConfig: config.createTableFieldsConfig,
      partitionsConfig: config.createTablePartitionsConfig,
    };
  },
  methods: {
    addFieldsItem() {
      this.target.newFieldsData.fields.push({
        name: '',
        type: 'string',
        alias: '',
        length: null,
        rule: '',
        primary: 0,
        comment: '',
        partitionField: 0,
      });
    },
    delteFields(item, index) {
      if (this.target.newFieldsData.fields.length === 1) return;
      this.target.newFieldsData.fields.splice(index, 1);
    },
  },
};
</script>
<style lang="scss" scoped>
.create-table {
    height: 50%;
}
</style>

