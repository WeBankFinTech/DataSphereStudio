<template>
  <div class="create-table-step-first">
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <Card
      :bordered="false"
      class="basic-card"
      v-for="(type, index1) in info"
      :key="index1">
      <p slot="title">{{ type.title }}</p>
      <Form
        :ref="type.ref"
        :model="attrInfo[type.key]"
        :rules="type.rules"
        :label-width="100"
        class="basic-card-form"
      >
        <FormItem
          :label="item.name"
          :prop="item.key"
          v-for="(item, index2) in type.map"
          :key="index2"
          class="basic-card-form-item">
          <Input
            v-if="item.inputType === 'input'"
            v-model="attrInfo[type.key][item.key]"
            :maxlength="item.maxlength"
            :placeholder="item.placeholder"></Input>
          <Select
            v-if="item.inputType === 'select'"
            v-model="attrInfo[type.key][item.key]"
            :placeholder="item.placeholder"
            @on-change="handleChange(arguments, item, index2)">
            <Option
              v-for="opt in item.opt"
              :key="opt.value"
              :value="opt.value">{{ opt.label }}</Option>
          </Select>
          <RadioGroup
            v-if="item.inputType === 'radio'"
            v-model="attrInfo[type.key][item.key]"
            :title="item.toolTip">
            <Radio
              v-for="opt in item.opt"
              :key="opt.value"
              :label="opt.value"
            >{{ opt.label }}</Radio>
          </RadioGroup>
          <Input
            v-if="item.inputType === 'textarea'"
            v-model="attrInfo[type.key][item.key]"
            type="textarea"
            :autosize="{minRows: 2,maxRows: 5}"
            :placeholder="item.placeholder"
            :maxlength="item.maxlength"></Input>
        </FormItem>
      </Form>
    </Card>
    <p class="create-table-btn-group">
      <Button
        @click="closeTab">{{$t('message.newConst.cancel')}}</Button>
      <Button
        type="primary"
        @click="nextStep">{{$t('message.createTable.next')}}</Button>
    </p>
  </div>
</template>
<script>
import { debounce } from 'lodash';
export default {
  props: {
    dbList: {
      type: Array,
      required: true,
    },
    attrInfo: {
      type: Object,
      required: true,
    },
    loading: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    let that = this;
    const validateName = (rule, value, callback) => {
      if (!value) {
        callback();
      } else if (!/^[\w\u4e00-\u9fa5]+$/.test(value)) {
        callback(new Error(this.$('message.createTable.nameNotice')));
      } else {
        callback();
      }
    };
    return {
      currentDb: 0,
      firstStepData: null,
      info: [
        {
          title: this.$t('message.createTable.baseAttr'),
          ref: 'basicAttr',
          key: 'basic',
          map: [{
            key: 'database',
            name: this.$t('message.createTable.daName'),
            inputType: 'select',
            opt: [],
          }, {
            key: 'name',
            name: this.$t('message.createTable.tableName'),
            inputType: 'input',
            placeholder: this.$t('message.createTable.tablePlaceholder'),
            maxlength: 64,
          }, {
            key: 'alias',
            name: this.$t('message.createTable.tableOtherName'),
            inputType: 'input',
            placeholder: this.$t('message.createTable.aliasPlaceholder'),
            maxlength: 64,
          }, {
            key: 'usage',
            name: this.$t('message.createTable.use'),
            inputType: 'input',
            placeholder: this.$t('message.createTable.emptyPlaceholder'),
            maxlength: 128,
          }, {
            key: 'productName',
            name: this.$t('message.createTable.product'),
            inputType: 'input',
            placeholder: this.$t('message.createTable.emptyPlaceholder'),
            maxlength: 64,
          }, {
            key: 'projectName',
            name: this.$t('message.createTable.project'),
            inputType: 'input',
            placeholder: this.$t('message.createTable.emptyPlaceholder'),
            maxlength: 64,
          }, {
            key: 'comment',
            name: this.$t('message.createTable.tableDesc'),
            inputType: 'textarea',
            placeholder: this.$t('message.createTable.emptyPlaceholder'),
            maxlength: 128,
          }],
          rules: {
            database: [
              { required: true, message: this.$t('message.createTable.selectDbName'), trigger: 'change' },
            ],
            name: [
              { required: true, message: this.$t('message.createTable.inputTableName'), trigger: 'blur' },
              {
                type: 'string',
                pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
                message: this.$t('message.createTable.tablePlaceholder'),
                trigger: 'change',
              },
              {
                validator(rule, value, callback) {
                  if (!value) {
                    return callback();
                  }
                  return that.handleTbInput(value, callback, that);
                },
              },
            ],
            alias: [
              { required: true, message: this.$t('message.createTable.inputOtherName'), trigger: 'change' },
              { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.createTable.aliasPlaceholder'), trigger: 'change' },
            ],
            usage: [
              { validator: validateName, trigger: 'change' },
            ],
            productName: [
              { validator: validateName, trigger: 'change' },
            ],
            projectName: [
              { validator: validateName, trigger: 'change' },
            ],
            comment: [
              { validator: validateName, trigger: 'change' },
            ],
          },
        },
        {
          title: this.$t('message.createTable.moduleAttr'),
          ref: 'modelAttr',
          key: 'model',
          map: [{
            key: 'partitionTable',
            name: this.$t('message.createTable.partitionType'),
            inputType: 'radio',
            opt: [{
              value: 1,
              label: this.$t('message.createTable.partition'),
            }, {
              value: 0,
              label: this.$t('message.createTable.unartition'),
            }],
          }, {
            key: 'lifecycle',
            name: this.$t('message.createTable.lifecycle'),
            inputType: 'select',
            opt: [{
              value: 0,
              label: this.$t('message.createTable.forever'),
            }, {
              value: 1,
              label: this.$t('message.createTable.day'),
            }, {
              value: 2,
              label: this.$t('message.createTable.week'),
            }, {
              value: 3,
              label: this.$t('message.createTable.moon'),
            }, {
              value: 4,
              label: this.$t('message.createTable.midYear'),
            }],
          }, {
            key: 'modelLevel',
            name: this.$t('message.createTable.moduleRank'),
            inputType: 'select',
            opt: [{
              value: 0,
              label: `ODS(${this.$t('message.createTable.resource')})`,
            }, {
              value: 1,
              label: `DWD(${this.$t('message.createTable.detailLevel')})`,
            }, {
              value: 2,
              label: `DWS(${this.$t('message.createTable.sumData')})`,
            }, {
              value: 3,
              label: `ADS(${this.$t('message.createTable.appData')})`,
            }],
          }, {
            key: 'useWay',
            name: this.$t('message.createTable.actionType'),
            inputType: 'select',
            opt: [{
              value: 0,
              label: this.$t('message.createTable.noeWrite'),
            }, {
              value: 1,
              label: this.$t('message.createTable.ZSGC'),
            }, {
              value: 2,
              label: this.$t('message.createTable.FG'),
            }, {
              value: 3,
              label: this.$t('message.createTable.OED'),
            }],
          }, {
            key: 'externalUse',
            name: this.$t('message.createTable.isUse'),
            inputType: 'radio',
            toolTip: this.$t('message.createTable.isDwsExeis'),
            opt: [{
              value: 1,
              label: 'yes',
            }, {
              value: 0,
              label: 'no',
            }],
          }],
          rules: {
            partitionTable: [
              { required: true, type: 'number', message: this.$t('message.createTable.selectPartitionType'), trigger: 'blur' },
            ],
            lifecycle: [
              { required: true, type: 'number', message: this.$t('message.createTable.selectLife'), trigger: 'blur' },
            ],
            modelLevel: [
              { required: true, type: 'number', message: this.$t('message.createTable.selectModuleLevel'), trigger: 'blur' },
            ],
            useWay: [
              { required: true, type: 'number', message: this.$t('message.createTable.selectUseType'), trigger: 'blur' },
            ],
            externalUse: [
              { required: true, type: 'number', message: this.$t('message.createTable.selectIsUse'), trigger: 'blur' },
            ],
          },
        },
      ],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.setDbList();
    });
  },
  methods: {
    setDbList() {
      if (!this.dbList.length) {
        return;
      }
      const list = this.info[0].map[0].opt = this.dbList.map((item) => {
        return {
          label: item.name,
          value: item.name,
        };
      });
      this.attrInfo.basic.database = list[0].value;
      this.currentDb = 0;
    },
    handleTbInput: debounce((val, cb, that) => {
      const tbList = that.dbList[that.currentDb].children;
      const isDupl = tbList.find((el) => el.name === val);
      if (isDupl) {
        return cb(new Error(this.$t('message.createTable.canned')));
      }
      return cb();
    }, 300),
    closeTab() {
      this.$emit('close-tab');
    },
    nextStep() {
      this.$refs.basicAttr[0].validate((valid1) => {
        if (!valid1) return this.$Message.warning(this.$t('message.newConst.failedNotice'));
        this.$refs.modelAttr[0].validate((valid2) => {
          if (!valid2) return this.$Message.warning(this.$t('message.newConst.failedNotice'));
          this.$emit('next-step', this.info);
        });
      });
    },
    handleChange(val, item) {
      const index = item.opt.findIndex((el) => el.value === val[0]);
      if (item.key !== 'database') {
        return;
      }
      this.currentDb = index;
      if (!this.dbList[index].length) {
        this.$emit('get-tables', this.dbList[index].name);
      }
    },
  },
};
</script>
