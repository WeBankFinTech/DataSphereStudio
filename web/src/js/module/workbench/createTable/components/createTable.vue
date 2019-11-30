<template>
  <div class="create-table">
    <Card>
      <p slot="title">
        {{$t('message.createTable.baseAttr')}}
      </p>
      <Button
        slot="extra"
        type="primary"
        size="small"
        @click.prevent="addFieldsItem">{{$t('message.createTable.add')}}</Button>
      <Form
        ref="fieldsForm"
        :model="target.newFieldsData">
        <field-list
          :list="target.newFieldsData.fields"
          :static-data="fieldsConfig"
          :has-action="true"
          @delete="delteFields"></field-list>
      </Form>
    </Card>
    <Card v-if="partitionTable">
      <p slot="title">
        {{$t('message.createTable.partitionAttr')}}
      </p>
      <Form
        ref="partForm"
        :model="target.newPartitionsData">
        <field-list
          :list="target.newPartitionsData.fields"
          :static-data="partitionsConfig"></field-list>
      </Form>
    </Card>
  </div>
</template>
<script>
import config from '../config.js';
import fieldList from './fieldList.vue';
export default {
  components: {
    fieldList,
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
    delteFields(index) {
      this.target.newFieldsData.fields.splice(index, 1);
    },
  },
};
</script>
