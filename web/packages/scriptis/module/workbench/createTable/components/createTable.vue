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
          @on-model-click="modelEdit"
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
          :rules="modelRule.name" label="名称">
          <Input v-model="fieldModel.name" placeholder=""></Input>
        </Form-item>
        <Form-item prop="type" :rules="modelRule.type" label="类型">
          <RadioGroup v-model="fieldModel.type">
            <Radio label="index">指标</Radio>
            <Radio label="dimension">维度</Radio>
          </RadioGroup>
        </Form-item>
        <Form-item prop="business"
          :rules="modelRule.business" label="业务口径">
          <Input v-model="fieldModel.business" placeholder=""></Input>
        </Form-item>
        <Form-item prop="calculate" :rules="modelRule.calculate" label="计算口径">
          <Input v-model="fieldModel.calculate" placeholder=""></Input>
        </Form-item>
        <Form-item prop="formula" label="计算公式">
          <Input v-model="fieldModel.formula" type="textarea" placeholder=""></Input>
        </Form-item>
      </Form>
      <div slot="footer">
        <Button @click="cancelEditModel">取消</Button>
        <Button type="primary" @click="confirmEditModel">确定</Button>
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
          {required: true, message: '请输入名称', trigger: 'blur'},
          {
            max: 255,
            message: '输入超长，最大长度255',
            trigger: 'blur',
          }
        ],
        type: [
          {required: true, message: '请选择', trigger: 'blur'}
        ],
        calculate: [
          {required: true, message: '计算口径', trigger: 'blur'},
          {
            max: 255,
            message: '输入超长，最大长度255',
            trigger: 'blur',
          }
        ],
        business: [
          {required: true, message: '请输入业务口径', trigger: 'blur'},
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
}
</style>

