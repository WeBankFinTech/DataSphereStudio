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
        style="margin-right:15px"
        @click.prevent="addFields">{{$t('message.scripts.createTable.addMore')}}</Button>
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
    <Modal v-model="batchAddShow" :title="$t('message.scripts.createTable.addMore')">
      <Input v-model="batchFields" type="textarea" :rows="3" placeholder="字段1,字段2,字段3.....">
      </Input>
      <div slot="footer">
        <Button type="text" size="large" @click="Cancel">{{
          $t("message.workspaceManagemnet.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="confirmAdd">{{
          $t("message.workspaceManagemnet.ok")
        }}</Button>
      </div>
    </Modal>
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
      batchAddShow: false,
      batchFields: '',
      loading: true
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
    addFields() {
      this.batchAddShow = true
    },
    Cancel() {
      this.batchAddShow = false;
      this.batchFields = '';
    },
    confirmAdd() {
      if (this.batchFields) {
        this.batchFields.split(',').forEach(item => 
          this.target.newFieldsData.fields.push({
            name: item.replace(/(^\s*)|(\s*$)/g,""),
            type: 'string',
            alias: '',
            length: null,
            rule: '',
            primary: 0,
            comment: '',
            partitionField: 0,
          })
        );
        this.batchFields = '';
        this.batchAddShow = false
      } else {
        this.$Message.error("请输入字段")
        return false
      }
    }
  },
};
</script>
<style lang="scss" scoped>
.create-table {
    height: 50%;
}
</style>

