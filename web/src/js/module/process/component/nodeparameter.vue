<template>
  <div>
    <Form
      label-position="top"
      ref="baseInfoForm"
      :rules="formRule"
      :model="currentNode"
      class="node-parameter-bar">
      <FormItem
        :label="$t('message.process.nodeParameter.nodeName')"
        prop="title">
        <Input
          v-model="currentNode.title"
          :placeholder="$t('message.process.nodeParameter.inputeNodeName')"/>
      </FormItem>
      <FormItem
        :label="$t('message.process.nodeParameter.nodeType')">
        <Input
          v-model="currentNode.type"
          :placeholder="$t('message.process.nodeParameter.nodeType')"
          disabled/>
      </FormItem>
      <FormItem :label="$t('message.process.nodeParameter.nodeDesc')">
        <Input
          v-model="currentNode.desc"
          type="textarea"
          :placeholder="$t('message.process.nodeParameter.inputeNodeDesc')"/>
      </FormItem>
    </Form>
    <div
      v-if="currentNode.jobContent && currentNode.jobContent.jobParams"
      class="node-module-param-modal-header">
      <h3>{{$t('message.process.nodeParameter.SXXX')}}</h3>
    </div>
    <Form
      v-if="currentNode.jobContent && currentNode.jobContent.jobParams"
      label-position="top"
      :rules="formRule"
      ref="parameterForm"
      :model="currentNode.jobContent.jobParams"
      class="node-parameter-bar">
      <template
        v-if="currentNode.type !== 'linkis.appjoint.sendemail'">
        <FormItem
          v-for="(item, key) in currentNode.jobContent.jobParams"
          :key="key"
          :label="toPoin(key)"
          :prop="key"
        >
          <template
            v-if="filterFormType(key) === 'number'">
            <InputNumber
              v-model="currentNode.jobContent.jobParams[key]"
              :placeholder="placeholderResult(key)"
              :min="key | minLimit"
              :max="key | maxLimit"
              :disabled="key === 'queryFrequency'"
              style="width: 100%;"
            />
          </template>
          <template v-else>
            <Input
              v-model="currentNode.jobContent.jobParams[key]"
              :type="filterFormType(key)"
              :rows="6"
              :placeholder="placeholderResult(key)"
              :disabled="['msgType', 'sourceType'].includes(key)"
              v-if="key !== 'rmbMessageType'"
            />
          </template>
          <Select
            v-if="key === 'rmbMessageType'"
            v-model="currentNode.jobContent.jobParams[key]"
            :placeholder="$t('message.process.nodeParameter.XZRMBXXJZ')">
            <Option value="SYNC">SYNC</Option>
            <Option value="ASYNC">ASYNC</Option>
          </Select>
        </FormItem>
      </template>
      <!-- 发送邮件的表单项 -->
      <template
        v-else
        v-for="(item, key) in currentNode.jobContent.jobParams">
        <formItem
          :key="key"
          :label="toPoin(key)"
          :prop="key"
          v-if="key !== 'content'">
          <Select
            v-if="key === 'category'"
            v-model="currentNode.jobContent.jobParams[key]"
            :placeholder="$t('message.process.nodeParameter.XZLX')"
            @on-change="categoryChange">
            <Option value="node">{{$t('message.process.nodeParameter.node')}}</Option>
            <Option value="text">{{$t('message.process.nodeParameter.text')}}</Option>
            <!-- <Option value="file">{{$t('message.process.nodeParameter.file')}}</Option>
            <Option value="link">{{$t('message.process.nodeParameter.link')}}</Option> -->
          </Select>
          <Input
            v-model="currentNode.jobContent.jobParams[key]"
            :type="filterFormType(key)"
            :rows="6"
            :placeholder="placeholderResult(key)"
            v-if="key !== 'category'"
          />
        </formItem>
        <formItem
          :key="key"
          :label="toPoin(key)"
          :prop="key"
          v-if="key === 'content' && currentNode.jobContent.jobParams['category'] != 'node'"
          :rules="[{ required: true, message: $t('message.process.nodeParameter.XZHSR'), trigger: 'blur', type: 'string' }]">
          <template>
            <Input
              v-model="currentNode.jobContent.jobParams[key]"
              type="text"
              :placeholder="$t('message.process.nodeParameter.SRZQNR')"/>
          </template>
        </formItem>
        <formItem
          :key="key"
          :label="toPoin(key)"
          :prop="key"
          v-if="key === 'content' && currentNode.jobContent.jobParams['category'] == 'node'"
          :rules="[{ required: true, message: $t('message.process.nodeParameter.XZHSR'), trigger: 'blur', type: 'array' }]">
          <template>
            <Select
              v-model="currentNode.jobContent.jobParams[key]"
              :placeholder="$t('message.process.nodeParameter.XZSFX')"
              multiple>
              <Option
                v-for="item in newNodes"
                :value="item.key"
                :key="item.key">{{ item.title }}</Option>
            </Select>
          </template>
        </formItem>
      </template>
    </Form>
    <div
      v-if="currentNode.jobContent && currentNode.jobContent.code"
      class="node-parameter-bar"
    >
      <div>code:</div></br>
      <Input
        v-model="currentNode.jobContent.code"
        :rows="8"
        type="textarea"/>
    </div>
    <div
      v-show="isExcuteNode()"
      class="node-parameter-bar"
    >
      <span>{{$t('message.process.nodeParameter.ZYXX')}}</span>
      <resource
        :resources="resources"
        :is-ripetition="true"
        :node-type="nodeData.type"
        :readonly="readonly"
        @update-resources="updateResources"></resource>
    </div>
    <div class="save-button">
      <Button @click="save">{{$t('message.process.nodeParameter.BC')}}</Button>
    </div>
  </div>
</template>
<script>
import resource from './resource.vue';
import { NODETYPE } from '@/js/service/nodeType'
export default {
  name: 'NodeParams',
  components: {
    resource,
  },
  filters: {
    toHump(val) {
      return val.replace(/\.(\w)/g, (all, letter) => {
        return letter.toUpperCase();
      });
    },

    minLimit(key) {
      switch (key) {
        case 'spark-driver-memory':
        case 'spark-executor-instances':
        case 'spark-executor-cores':
          return 1;
        case 'spark-executor-memory':
          return 3;
        case 'maxReceiveHours':
        case 'maxCheckHours':
        case 'queryFrequency':
        case 'timeScape':
          return 1;
        default:
          return 1;
      }
    },
    maxLimit(key) {
      switch (key) {
        case 'spark-driver-memory':
        case 'spark-executor-memory':
          return 15;
        case 'spark-executor-instances':
          return 40;
        case 'spark-executor-cores':
          return 10;
        case 'maxReceiveHours':
        case 'maxCheckHours':
        case 'queryFrequency':
        case 'timeScape':
          return 1000;
        default:
          return 10;
      }
    },
  },
  props: {
    nodeData: {
      type: Object,
      default: null,
    },
    flowName: {
      type: String,
      default: '',
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    nodes: {
      type: Array,
      default: () => [],
    }
  },
  data() {

    return {
      currentNode: {},
      resources: [],
      paramsIsChange: false,
      timer: null,
    };
  },
  watch: {
    nodeData(val) {
      this.currentNode = val;
      if (this.currentNode.jobContent && this.currentNode.jobContent.script) {
        this.resources = this.currentNode.resources.filter((item) => {
          return item.fileName !== this.currentNode.jobContent.script;
        });
      } else {
        if (this.currentNode.resources) {
          this.resources = this.currentNode.resources;
        } else {
          this.resources = [];
        }
      }
    },
    currentNode: {
      handler() {
        this.paramsIsChange = true;
        this.paramsIsChangeAction();
      },
      deep: true,
    },
    paramsIsChange(val) {
      this.$emit('paramsChange', val);
    },
  },
  computed: {
    newNodes() {
      return this.nodes.filter((item) => {
        return item.key !== this.currentNode.key;
      })
    },
    formRule() {
      const validateJson = (rule, value, callback) => {
        const isJsonString = (str) => {
          try {
            JSON.parse(str);
            return true;
          } catch (err) {
            return false;
          }
        };
        if (isJsonString(value)) {
          callback();
        } else {
          callback(new Error(this.$t('message.process.nodeParameter.QTXZQJSON')));
        }
      };
      const validatorName = (rule, value, callback) => {
        if (value === `${this.flowName}_`) {
          callback(new Error(this.$t('message.process.nodeParameter.JDMCBNYGZL')));
        } else {
          callback();
        }
      };
      return {
        'jdbcUrl': [
          { required: true, message: 'joburl为必填项', trigger: 'blur' },
          { type: 'string', pattern: /(jdbc)[a-zA-Z0-9_:./]/, message: '必须以jdbc开头，且只支持数字、字母、(_:./)', trigger: 'blur' },
        ],
        'jdbcUsername': [
          { required: true, message: 'username为必填项', trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: '此值不支持中文', trigger: 'blur' },
        ],
        'jdbcPassword': [
          { required: true, message: 'password为必填项', trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: '此值不支持中文', trigger: 'blur' },
        ],
        'sourceType': [
          { required: true, message: this.$t('message.process.nodeParameter.XZSJLY'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'checkObject': [
          { required: true, message: this.$t('message.process.nodeParameter.QTXYLSJMC'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          { type: 'string', pattern: /^[a-zA-Z]([^.]*\.[^.]*){1,}$/, message: this.$t('message.process.nodeParameter.CZGSCW'), trigger: 'blur' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'maxReceiveHours': [
          { required: false, message: this.$t('message.process.nodeParameter.QTXDDSJ'), trigger: 'blur', type: 'number' },
        ],
        'queryFrequency': [
          { required: false, message: this.$t('message.process.nodeParameter.QTXCXPL'), trigger: 'blur', type: 'number' },
        ],
        'timeScape': [
          { required: false, message: this.$t('message.process.nodeParameter.QTXSJFW'), trigger: 'blur', type: 'number' },
        ],
        'jobDesc': [
          { required: false, message: this.$t('message.process.nodeParameter.QZQTXDYPZ'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'msgSender': [
          { required: true, message: this.$t('message.process.nodeParameter.QZQTXFSZ'), trigger: 'blur' },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_@-]*$/, message: this.$t('message.process.nodeParameter.BXYZMKTSZXHXZHX'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'msgType': [
          { required: true, message: this.$t('message.process.nodeParameter.QZQTXXXLX'), trigger: 'blur' },
          {
            type: 'string',
            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
            message: this.$t('message.process.nodeParameter.BXYZMKTXHX'),
            trigger: 'change',
          },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'msgReceiver': [
          { required: true, message: this.$t('message.process.nodeParameter.QZQTXJSZ'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'msgTopic': [
          { required: true, message: this.eventFilter('msgTopic' ,this.nodeData.type), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          { type: 'string', pattern: /^[a-zA-Z0_9-]([^_]*_[^_]*){2}[a-zA-Z\d]$/, message: this.$t('message.process.nodeParameter.CGSCW'), trigger: 'blur' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'msgSavekey': [
          { required: false, message: this.$t('message.process.nodeParameter.XXLRKEYZ'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'msgName': [
          { required: true, message: this.eventFilter('msgName' ,this.nodeData.type), trigger: 'blur' },
          {
            min: 1,
            max: 128,
            message: this.$t('message.process.nodeParameter.CDZ128ZF'),
            trigger: 'change',
          },
          {
            type: 'string',
            pattern: /^[a-zA-Z][a-zA-Z0-9_-]*$/,
            message: this.$t('message.process.nodeParameter.BXYZMKTXHX'),
            trigger: 'change',
          },
        ],
        'msgBody': [
          { required: false, message: this.$t('message.process.nodeParameter.TXXXNR'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'rmbTargetDcn': [
          { required: true, message: this.$t('message.process.nodeParameter.TXMBDCN'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'rmbServiceld': [
          { required: true, message: this.$t('message.process.nodeParameter.TXRMBFW'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'rmbEnvironment': [
          { required: true, message: this.$t('message.process.nodeParameter.TXRMBXXHJ'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'rmbMessageType': [
          { required: false, message: this.$t('message.process.nodeParameter.XZRMBXXFSJZ'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'rmbMessage': [
          { required: true, message: this.$t('message.process.nodeParameter.TXRMBNRJSON'), trigger: 'blur' },
          { type: 'string', pattern: /^[^\u4e00-\u9fa5]+$/, message: this.$t('message.process.nodeParameter.CZBNSRZW'), trigger: 'change' },
          { validator: validateJson, trigger: 'blur', key: 'queryFrequency' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'spark-driver-memory': [
          { required: false, message: this.$t('message.process.nodeParameter.CDNC'), trigger: 'blur', type: 'number' },
        ],
        'spark-executor-memory': [
          { required: false, message: this.$t('message.process.nodeParameter.ZXQNC'), trigger: 'blur', type: 'number' },
        ],
        'spark-executor-cores': [
          { required: false, message: this.$t('message.process.nodeParameter.ZXQHXGS'), trigger: 'blur', type: 'number' },
        ],
        'spark-executor-instances': [
          { required: false, message: this.$t('message.process.nodeParameter.ZXQGS'), trigger: 'blur', type: 'number' },
        ],
        'wds-linkis-yarnqueue': [
          { required: false, message: this.$t('message.process.nodeParameter.ZXDL'), trigger: 'blur' },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.process.nodeParameter.BXYZMKTXHX'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'params': [
          { required: false, message: this.$t('message.process.nodeParameter.MLHCS'), trigger: 'blur' },
          { type: 'string', pattern: /^((?!([\u4e00-\u9fa5]|\\n)).)*$/, message: this.$t('message.process.nodeParameter.DGCSYKGGK'), trigger: 'change' },
          {
            min: 1,
            max: 500,
            message: this.$t('message.process.nodeParameter.CD500ZF'),
            trigger: 'change',
          },
        ],
        'title': [
          { required: true, message: this.$t('message.process.nodeParameter.TXJDMC'), trigger: 'blur' },
          {
            type: 'string',
            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
            message: this.$t('message.process.nodeParameter.BXYZMKTXHX'),
            trigger: 'change',
          },
          { validator: validatorName, trigger: 'blur' },
          {
            min: 1,
            max: 128,
            message: this.$t('message.process.nodeParameter.CDZ128ZF'),
            trigger: 'change',
          },
        ],
        'category': [
          { required: true, message: this.$t('message.process.nodeParameter.XZLX'), trigger: 'blur' },
        ],
        'subject': [
          { required: true, message: this.$t('message.process.nodeParameter.TXYJBT'), trigger: 'blur' },
          {
            type: 'string',
            pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]*$/,
            message: this.$t('message.process.nodeParameter.BTZZCZW'),
            trigger: 'change',
          },
          {
            min: 1,
            max: 128,
            message: this.$t('message.process.nodeParameter.CDZ128ZF'),
            trigger: 'change',
          },
        ],
        // 'content': [
        //   { required: true, message: this.$t('message.process.nodeParameter.XZHSR'), trigger: 'blur', type: this.changeType },
        // ],
        'to': [
          { required: true, message: this.$t('message.process.nodeParameter.TXSJR'), trigger: 'blur' },
          {
            type: 'string',
            // pattern: /^[a-z][a-zA-Z0-9_.@]*$/,
            message: this.$t('message.process.nodeParameter.BXYZMKTSZXHXZHX'),
            trigger: 'change',
          },
        ],
        'cc': [
          { required: false, message: this.$t('message.process.nodeParameter.TXCSR'), trigger: 'blur' },
          {
            type: 'string',
            // pattern: /^[a-z][a-zA-Z0-9_.@]*$/,
            message: this.$t('message.process.nodeParameter.BXYZMKTSZXHXZHX'),
            trigger: 'change',
          },
        ],
        'bcc': [
          { required: false, message: this.$t('message.process.nodeParameter.TXMMFSR'), trigger: 'blur' },
          {
            type: 'string',
            // pattern: /^[a-z][a-zA-Z0-9_.@]*$/,
            message: this.$t('message.process.nodeParameter.BXYZMKTSZXHXZHX'),
            trigger: 'change',
          },
        ],
        'itsm': [
          { required: true, message: this.$t('message.process.nodeParameter.TXGLSPD'), trigger: 'blur' },
          {
            type: 'string',
            pattern: /^[0-9_.]*$/,
            message: this.$t('message.process.nodeParameter.ZZCSZ'),
            trigger: 'change',
          },
          {
            min: 1,
            max: 128,
            message: this.$t('message.process.nodeParameter.CDZ128ZF'),
            trigger: 'change',
          },
        ],
        'executeUser': [
          { required: true, message: this.$t('message.process.nodeParameter.required'), trigger: 'blur' },
        ],
      }
    }

  },
  methods: {
    paramsIsChangeAction() {
      if (this.timer) {
        clearInterval(this.timer);
      }
      this.timer = setInterval(() => {
        if (!this.paramsIsChange) {
          clearInterval(this.timer);
        }
        this.paramsIsChange = false;
      }, 1000 * 60 * 5);
    },
    save() {
      this.resourcesAction();
      // 将jobParams值放入params的startup里, 目前之后spark节点
      if ([NODETYPE.HQL, NODETYPE.SPARKSQL, NODETYPE.SPARKPY, NODETYPE.SCALA].includes(this.currentNode.type)) {
        Object.keys(this.currentNode.jobContent.jobParams).map((key) => {
          const newKey = key.replace(/-/g, '.');
          this.currentNode.params.configuration.startup[newKey] = String(this.currentNode.jobContent.jobParams[key]);
        })
      } else {
        if (this.currentNode.jobContent && this.currentNode.jobContent.jobParams) {
          this.currentNode.params.configuration.runtime = JSON.parse(JSON.stringify(this.currentNode.jobContent.jobParams));
        }
        if ([NODETYPE.EVENTCHECKERF, NODETYPE.EVENTCHECKERW, NODETYPE.DATACHECKER, NODETYPE.JDBC].includes(this.currentNode.type)) {
          Object.keys(this.currentNode.params.configuration.runtime).map((key) => {
            this.currentNode.params.configuration.runtime[this.transtionKey(key)] = String(this.currentNode.params.configuration.runtime[key]);
            delete this.currentNode.params.configuration.runtime[key];
          })
        }
      }
      this.validFrom();
    },
    validFrom() {
      this.$refs.baseInfoForm.validate((baseInfoValid) => {
        if (baseInfoValid) {
          if (this.$refs.parameterForm) {
            this.$refs.parameterForm.validate((valid) => {
              if (valid) {
                this.$emit('saveNode', this.currentNode);
              } else {
                this.$Message.warning(this.$t('message.process.nodeParameter.BCSB'));
              }
            });
          } else {
            this.$emit('saveNode', this.currentNode);
          }
        } else {
          this.$Message.warning(this.$t('message.process.nodeParameter.JDBDXTXBHF'));
        }
      });
    },
    transtionKey(key) {
      return key.replace(/([A-Z])/g, '.$1').toLowerCase();
    },
    resourcesAction() {
      let resources = [];
      if (this.currentNode.jobContent && this.currentNode.jobContent.script) {
        resources = this.currentNode.resources.filter((item) => {
          return item.fileName === this.currentNode.jobContent.script;
        });
      }
      this.resources.map((item) => {
        resources.push(item);
      });
      this.currentNode.resources = resources;
    },
    updateResources(res) {
      this.resources = res.map((item) => {
        return {
          fileName: item.fileName,
          resourceId: item.resourceId,
          version: item.version,
        };
      });
      this.resourcesAction();
      this.$emit('saveNode', this.currentNode);
    },
    isExcuteNode() {
      return this.currentNode.type == NODETYPE.SPARKPY || this.currentNode.type == NODETYPE.SPARKSQL || this.currentNode.type === NODETYPE.SHELL;
    },
    placeholderResult(value) {
      let message = this.$t('message.process.nodeParameter.JDBDXTXBHF');
      Object.keys(this.formRule).forEach((key) => {
        if (key === value) {
          message = this.formRule[key][0].message;
        }
      });
      return message;
    },
    eventFilter(key, type) {
      const value = key === 'msgName' ? 'TXXXMC' : 'TXZT';
      if (NODETYPE.EVENTCHECKERF === type) {
        return this.$t(`message.process.nodeParameter.${value}`, {type: 'eventreceiver'})
      } else {
        return this.$t(`message.process.nodeParameter.${value}`, {type: 'eventsender'})
      }
    },
    filterFormType(val) {
      switch (val) {
        case 'maxReceiveHours':
        case 'maxCheckHours':
        case 'queryFrequency':
        case 'timeScape':
        case 'spark-driver-memory':
        case 'spark-executor-memory':
        case 'spark-executor-cores':
        case 'spark-executor-instances':
          return 'number';
        case 'jobDesc':
        case 'msgBody':
        case 'rmbMessage':
          return 'textarea';
        default:
          return 'text';
      }
    },
    // 切换类型清空选项值
    categoryChange() {
      this.currentNode.jobContent.jobParams['content'] = ''
    },
    toPoin(name) {
      switch (name) {
        case 'params':
          return this.$t('message.process.nodeParameter.MLHCS');
        case 'category':
          return this.$t('message.process.nodeParameter.LX');
        case 'subject':
          return this.$t('message.process.nodeParameter.YJLX');
        case 'content':
          return this.$t('message.process.nodeParameter.FSX');
        case 'to':
          return this.$t('message.process.nodeParameter.SJR');
        case 'cc':
          return this.$t('message.process.nodeParameter.CS');
        case 'bcc':
          return this.$t('message.process.nodeParameter.MMCS');
        case 'filter':
          return this.$t('message.process.nodeParameter.filter');
        case 'executeUser':
          return this.$t('message.process.nodeParameter.executeUser');
        default:
          return name.replace(/([A-Z])/g, '.$1').toLowerCase();
      }
    },
  },
};
</script>
