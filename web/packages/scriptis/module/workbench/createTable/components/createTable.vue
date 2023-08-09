<template>
  <div class="create-table">
    <div class="basic-panel">
      <div style="position: relative;height: 30px;">
        <p class="title" style="float:left">
          {{$t('message.scripts.createTable.baseAttr')}}
        </p>
        <Button
          slot="extra"
          type="primary"
          size="small"
          style="margin-right:15px; float:right"
          @click.prevent="addFields">{{$t('message.scripts.createTable.addMore')}}</Button>
        <Button
          slot="extra"
          type="primary"
          size="small"
          style="margin-right:15px; float:right"
          @click.prevent="addFieldsItem">{{$t('message.scripts.createTable.add')}}</Button>
      </div>
      <Form
        ref="fieldsForm"
        :model="target.newFieldsData">
        <fields-table
          :fields="target.newFieldsData.fields"
          :table-columns="fieldsConfig"
          :can-be-delete="true"
          @on-model-click="modelEdit"
          @on-delete="delteFields"></fields-table>
      </Form>
    </div>
    <div v-if="partitionTable" class="partition-panel">
      <p class="title">
        {{$t('message.scripts.createTable.partitionAttr')}}
      </p>
      <Form
        ref="partForm"
        :model="target.newPartitionsData">
        <fields-table
          :fields="target.newPartitionsData.fields"
          :table-columns="partitionsConfig"></fields-table>
      </Form>
    </div>
    <Modal v-model="batchAddShow" :title="$t('message.scripts.createTable.addMore')">
      <Input v-model="batchFields" type="textarea" :rows="3" placeholder="字段1,字段2,字段3.....">
      </Input>
      <div slot="footer">
        <Button type="text" size="large" @click="Cancel">{{
          $t("message.workspaceManagement.cancel")
        }}</Button>
        <Button type="primary" size="large" @click="confirmAdd">{{
          $t("message.workspaceManagement.ok")
        }}</Button>
      </div>
    </Modal>
    <Modal v-model="editModelShow" :title="$t('message.scripts.createTable.titleModel')">
      <Form ref="modelForm" :model="fieldModel" :label-width="80">
        <Form-item prop="name"
          :rules="modelRule.name" :label="$t('message.scripts.Name')">
          <Input v-model="fieldModel.name" placeholder=""></Input>
        </Form-item>
        <Form-item prop="type" :rules="modelRule.type" :label="$t('message.scripts.Type')">
          <RadioGroup v-model="fieldModel.type">
            <Radio label="index">{{ $t('message.scripts.Metrics') }}</Radio>
            <Radio label="dimension">{{ $t('message.scripts.Dimensions') }}</Radio>
          </RadioGroup>
        </Form-item>
        <Form-item prop="business"
          :rules="modelRule.business" :label="$t('message.scripts.busst')">
          <Input v-model="fieldModel.business" placeholder=""></Input>
        </Form-item>
        <Form-item prop="calculate" :rules="modelRule.calculate" :label="$t('message.scripts.calcst')">
          <Input v-model="fieldModel.calculate" placeholder=""></Input>
        </Form-item>
        <Form-item prop="formula" :label="$t('message.scripts.Formula')">
          <Input v-model="fieldModel.formula" type="textarea" placeholder=""></Input>
        </Form-item>
      </Form>
      <div slot="footer">
        <Button @click="cancelEditModel">{{ $t('message.scripts.Cancel') }}</Button>
        <Button type="primary" @click="confirmEditModel">{{ $t('message.scripts.confirm') }}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import config from '../config.js';
import fieldsTable from '@dataspherestudio/shared/components/virtualTable/fieldTable/fieldsTable.vue';
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
      loading: true,
      editModelShow: false,
      fieldModel: {
        type: 'index'
      },
      modelRule: {
        name: [
          {required: true, message: this.$t('message.scripts.plsinputname'), trigger: 'blur'},
          {
            max: 255,
            message: '输入超长，最大长度255',
            trigger: 'blur',
          }
        ],
        type: [
          {required: true, message: this.$t('message.scripts.plsselect'), trigger: 'blur'}
        ],
        calculate: [
          {required: true, message: this.$t('message.scripts.calcst'), trigger: 'blur'},
          {
            max: 255,
            message: '输入超长，最大长度255',
            trigger: 'blur',
          }
        ],
        business: [
          {required: true, message: this.$t('message.scripts.plsinputbusiness'), trigger: 'blur'},
          {
            max: 255,
            message: '输入超长，最大长度255',
            trigger: 'blur',
          }
        ]
      }
    };
  },
  methods: {
    addFieldsItem() {
      if(!this.checkDup()){
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
      }
    },
    delteFields(item, index) {
      if (this.target.newFieldsData.fields.length === 1) return;
      this.target.newFieldsData.fields.splice(index, 1);
    },
    addFields() {
      if(!this.checkDup()){
        this.batchAddShow = true;
      }
    },
    checkDup() {
      const dupNames= {}
      this.target.newFieldsData.fields.forEach((it) => {
        if (it.name) {
          dupNames[it.name] = dupNames[it.name] ? dupNames[it.name] + 1 : 1
        }
      })
      const dupfield = Object.keys(dupNames).filter(it => dupNames[it] > 1)
      if (dupfield.length) {
        this.$Message.warning(this.$t('message.scripts.createTable.dupfields', {fields: dupfield.join('<br>')}));
        return true
      }
      return false
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
        this.$Message.error(this.$t('message.scripts.inputfield'))
        return false
      }
      this.checkDup();
    },
    modelEdit(item) {
      this.editModelShow = true
      this.editFieldItem = item
      this.fieldModel = item.modeInfo || { type: 'index' }
    },
    confirmEditModel() {
      this.$refs.modelForm.validate(valid => {
        if (valid) {
          this.editModelShow = false
          this.editFieldItem.modeInfo = {...this.fieldModel}
          this.fieldModel = { type: 'index'}
          this.target.newFieldsData.fields = this.target.newFieldsData.fields.slice(0)
          this.$refs.modelForm.resetFields()
        }
      })
    },
    cancelEditModel() {
      this.editModelShow = false
      this.fieldModel = { type: 'index'}
      this.$refs.modelForm.resetFields()
    }
  },
};
</script>
<style lang="scss" scoped>
.create-table {
    height: 50%;
    .basic-panel, .partition-panel {
      padding: 16px 20px;
      position: relative;
      .title {
        line-height: 20px;
        font-size: 14px;
        font-weight: bold;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
}
</style>

