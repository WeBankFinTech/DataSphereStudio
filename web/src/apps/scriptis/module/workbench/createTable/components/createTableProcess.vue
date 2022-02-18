<template>
  <div>
    <div class="create-table-step-first">
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
    </div>
    <div class="create-table-second-step">
      <Spin
        v-if="loading"
        size="large"
        fix/>
      <div class="data-source">
        <div class="data-source-item-wrap">
          <span class="data-source-title">{{$t('message.scripts.createTable.SJSXLY')}}</span> <!-- 数据属性来源 -->
          <Select
            v-model="attrInfo.source.source"
            class="data-source-type">
            <Option
              v-for="(item) in sourceTypeList"
              :key="item.value"
              :value="item.value">{{ item.label }}</Option>
          </Select>
        </div>
        <div
          style="margin-left:20px;"
          class="data-source-item-wrap"
          v-show="attrInfo.source.source === 'import'">
          <span class="data-source-title">{{$t('message.scripts.createTable.DRFS')}}</span>   <!-- 导入方式 -->
          <Select
            v-model="attrInfo.source.importType"
            class="data-source-type"
            @on-change="onImportTypeChange">
            <Option
              v-for="(item) in importTypeList"
              :key="item.value"
              :value="item.value">{{ item.label }}</Option>
          </Select>
        </div>
      </div>
      <create-table
        ref="createTable"
        v-if="attrInfo.source.source === 'new'"
        :target="attrInfo.target.new"
        :partition-table="attrInfo.model.partitionTable"></create-table>
      <import-table
        ref="importTable"
        v-if="attrInfo.source.source === 'import' && dbList.length"
        :source="attrInfo.source"
        :target="attrInfo.target"
        :db-list="dbList"
        :partition-table="attrInfo.model.partitionTable"
        @get-fields="getFields"
        @get-tables="getTables"
        @isPathLeaf="csvOrXlsxPathChange"></import-table>
    </div>
  </div>
</template>
<script>
import { debounce } from 'lodash';
import createTable from './createTable.vue';
import importTable from './importTable.vue';
export default {
  components: {
    createTable,
    importTable,
  },
  props: {
    dbList: {
      type: Array,
      required: true,
    },
    personalDbList: {
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
        callback(new Error(this.$('message.scripts.createTable.nameNotice')));
      } else {
        callback();
      }
    };
    return {
      currentDb: 0,
      firstStepData: null,
      info: [
        {
          title: this.$t('message.scripts.createTable.baseAttr'),
          ref: 'basicAttr',
          key: 'basic',
          map: [{
            key: 'database',
            name: this.$t('message.scripts.createTable.daName'),
            inputType: 'select',
            opt: [],
          }, {
            key: 'name',
            name: this.$t('message.scripts.createTable.tableName'),
            inputType: 'input',
            placeholder: this.$t('message.scripts.createTable.tablePlaceholder'),
            maxlength: 64,
          }, {
            key: 'alias',
            name: this.$t('message.scripts.createTable.tableOtherName'),
            inputType: 'input',
            placeholder: this.$t('message.scripts.createTable.aliasPlaceholder'),
            maxlength: 64,
          }, {
            key: 'usage',
            name: this.$t('message.scripts.createTable.use'),
            inputType: 'input',
            placeholder: this.$t('message.scripts.createTable.emptyPlaceholder'),
            maxlength: 128,
          }, {
            key: 'productName',
            name: this.$t('message.scripts.createTable.product'),
            inputType: 'input',
            placeholder: this.$t('message.scripts.createTable.emptyPlaceholder'),
            maxlength: 64,
          }, {
            key: 'projectName',
            name: this.$t('message.scripts.createTable.project'),
            inputType: 'input',
            placeholder: this.$t('message.scripts.createTable.emptyPlaceholder'),
            maxlength: 64,
          }, {
            key: 'comment',
            name: this.$t('message.scripts.createTable.tableDesc'),
            inputType: 'textarea',
            placeholder: this.$t('message.scripts.createTable.emptyPlaceholder'),
            maxlength: 128,
          }],
          rules: {
            database: [
              { required: true, message: this.$t('message.scripts.createTable.selectDbName'), trigger: 'change' },
            ],
            name: [
              { required: true, message: this.$t('message.scripts.createTable.inputTableName'), trigger: 'blur' },
              {
                type: 'string',
                pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
                message: this.$t('message.scripts.createTable.tablePlaceholder'),
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
              { required: true, message: this.$t('message.scripts.createTable.inputOtherName'), trigger: 'change' },
              { type: 'string', pattern: /^[\w\u4e00-\u9fa5]+$/, message: this.$t('message.scripts.createTable.aliasPlaceholder'), trigger: 'change' },
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
          title: this.$t('message.scripts.createTable.moduleAttr'),
          ref: 'modelAttr',
          key: 'model',
          map: [{
            key: 'partitionTable',
            name: this.$t('message.scripts.createTable.partitionType'),
            inputType: 'radio',
            opt: [{
              value: 1,
              label: this.$t('message.scripts.createTable.partition'),
            }, {
              value: 0,
              label: this.$t('message.scripts.createTable.unartition'),
            }],
          }, {
            key: 'lifecycle',
            name: this.$t('message.scripts.createTable.lifecycle'),
            inputType: 'select',
            opt: [{
              value: 0,
              label: this.$t('message.scripts.createTable.forever'),
            }, {
              value: 1,
              label: this.$t('message.scripts.createTable.day'),
            }, {
              value: 2,
              label: this.$t('message.scripts.createTable.week'),
            }, {
              value: 3,
              label: this.$t('message.scripts.createTable.moon'),
            }, {
              value: 4,
              label: this.$t('message.scripts.createTable.midYear'),
            }],
          }, {
            key: 'modelLevel',
            name: this.$t('message.scripts.createTable.moduleRank'),
            inputType: 'select',
            opt: [{
              value: 0,
              label: `ODS(${this.$t('message.scripts.createTable.resource')})`,
            }, {
              value: 1,
              label: `DWD(${this.$t('message.scripts.createTable.detailLevel')})`,
            }, {
              value: 2,
              label: `DWS(${this.$t('message.scripts.createTable.sumData')})`,
            }, {
              value: 3,
              label: `ADS(${this.$t('message.scripts.createTable.appData')})`,
            }],
          }, {
            key: 'useWay',
            name: this.$t('message.scripts.createTable.actionType'),
            inputType: 'select',
            opt: [{
              value: 0,
              label: this.$t('message.scripts.createTable.noeWrite'),
            }, {
              value: 1,
              label: this.$t('message.scripts.createTable.ZSGC'),
            }, {
              value: 2,
              label: this.$t('message.scripts.createTable.FG'),
            }, {
              value: 3,
              label: this.$t('message.scripts.createTable.OED'),
            }],
          }, {
            key: 'externalUse',
            name: this.$t('message.scripts.createTable.isUse'),
            inputType: 'radio',
            toolTip: this.$t('message.scripts.createTable.isDwsExeis'),
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
              { required: true, type: 'number', message: this.$t('message.scripts.createTable.selectPartitionType'), trigger: 'blur' },
            ],
            lifecycle: [
              { required: true, type: 'number', message: this.$t('message.scripts.createTable.selectLife'), trigger: 'blur' },
            ],
            modelLevel: [
              { required: true, type: 'number', message: this.$t('message.scripts.createTable.selectModuleLevel'), trigger: 'blur' },
            ],
            useWay: [
              { required: true, type: 'number', message: this.$t('message.scripts.createTable.selectUseType'), trigger: 'blur' },
            ],
            externalUse: [
              { required: true, type: 'number', message: this.$t('message.scripts.createTable.selectIsUse'), trigger: 'blur' },
            ],
          },
        },
      ],
      sourceTypeList: [{
        value: 'new',
        label: this.$t('message.scripts.createTable.XJ'),
      }, {
        value: 'import',
        label: this.$t('message.scripts.createTable.DR'),
      }],
      importTypeList: [{
        value: 'hive',
        label: 'hive',
      },
      {
        value: 'csv',
        label: 'csv',
      }, {
        value: 'xlsx',
        label: 'xlsx',
      }],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.setDbList();
    });
  },
  methods: {
    setDbList() {
      if (!this.personalDbList.length) {
        return;
      }
      const list = this.info[0].map[0].opt = this.personalDbList.filter((item) => /(_ind|_qml|_default)$/.test(item.name)).map((item) => {
        return {
          label: item.name,
          value: item.name,
        };
      });
      this.attrInfo.basic.database = list[0].value;
      this.currentDb = 0;
    },
    handleTbInput: debounce((val, cb, that) => {
      const tbList = that.personalDbList[that.currentDb].children;
      const isDupl = tbList.find((el) => el.name === val);
      if (isDupl) {
        return cb(new Error(that.$t('message.scripts.createTable.canned')));
      }
      return cb();
    }, 300),
    handleChange(val, item) {
      if (item.key !== 'database') {
        return;
      }
      const index = item.opt.findIndex((el) => el.value === val[0]);
      this.currentDb = index;
      if (!this.personalDbList[index].length) {
        this.$emit('get-tables', this.personalDbList[index].name);
      }
    },
    getTables(val) {
      this.$emit('get-tables', val);
    },
    getFields(data) {
      this.$emit('get-fields', data);
    },
    onImportTypeChange() {
      // 导入方式改变时清空字段列表
      this.attrInfo.target.importFieldsData.fields = [];
      this.attrInfo.target.fieldList = [];
      this.attrInfo.source.hive.tbName = '';
    },
    csvOrXlsxPathChange(arg) {
      // 当选择的路径不是csv和xlsx时，清空字段
      if (!arg[0]) {
        this.attrInfo.target.importFieldsData.fields = [];
        this.attrInfo.target.fieldList = [];
      }
    }
  },
};
</script>
<style lang="scss" scoped>
@mixin bg-color1($initial-color, $replace-color) {
  background-color: $initial-color;
  [data-theme="dark"] & {
    background-color: $replace-color;
  }
}
@mixin bg-color2($initial-color, $replace-color) {
  background: $initial-color;
  [data-theme="dark"] & {
    background: $replace-color;
  }
}
@mixin border {
  border: 1px solid #dcdee2;
  [data-theme="dark"] & {
    border: none;
  }
}
@mixin color1 {
  color: #17233d;
  [data-theme="dark"] & {
    color: rgba(255, 255, 255, 0.85);
  }
}
.create-table-step-first {
    .basic-card {
        .basic-card-form {
            .ivu-form-item {
                display: inline-block;
                width: 50%;
            }
        }
    }
    .create-table-btn-group {
        float: right;
        margin-top: 10px;
        button {
            margin-right: 20px;
        }
    }
}
.create-table-second-step {
    position: relative;
    .data-source {
        display: block;
        @include bg-color2(#fff, #2A303C);
        border-radius: 4px;
        font-size: 14px;
        position: relative;
        -webkit-transition: all .2s ease-in-out;
        transition: all .2s ease-in-out;
        padding: 14px 16px;
        line-height: 1;
        &:hover {
            -webkit-box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
            box-shadow: 0 1px 6px rgba(0, 0, 0, .2);
            border-color: #eee;
        }
        .data-source-item-wrap {
            display: inline-block;
            .data-source-title {
                display: inline-block;
                height: 20px;
                line-height: 20px;
                font-size: 14px;
                @include color1;
                font-weight: 700;
            }
            .data-source-type {
                margin-left: 20px;
                width: 200px;
            }
        }
    }
    /deep/ .create-table{
      .ivu-card-bordered{
        // @include border;
        .ivu-card-body{
          .field-table .field-table-header{
            @include bg-color1(#5e9de0, #8899992b !important);
          }
          .field-table .field-table-body{
            @include bg-color2(#fff, none);
            @include border;
            &:hover{
              @include bg-color2(#ebf7ff, #39414b !important);
            }
          }
        }
      }
    }
}
</style>

