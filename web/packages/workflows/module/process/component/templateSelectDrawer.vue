<template>
  <Drawer
    title="节点参数模板列表"
    width="500"
    :mask-closable="false"
    closable
    v-model="showDrawer"
    @on-close="submitCancel"
  >
    <Collapse v-model="activePanelArray" @on-change="handlePanelChange">
      <Panel :name="item.templateId" v-for="(item,index) in templateList" :key="item.templateId">
        <Radio v-model="radioData[index]" :true-value="true" :false-value="false" @on-change="handleRadioChange" style="width:85%;overflow: hidden;text-overflow: ellipsis;">
          <span :title="item.templateName">{{item.templateName}}</span>
        </Radio>
        <div slot="content">
          <div v-for="(confItem,confIndex) in item.conf" :key="confIndex">
            <span>{{confItem.key}}</span>:&nbsp;<span style="color: black">{{confItem.configValue}}</span>
          </div>
          <div><span>模板描述</span>:&nbsp;<span style="color: black">{{item.desc}}</span></div>
        </div>
      </Panel>
    </Collapse>
    <div class="demo-drawer-footer">
      <Button style="margin-right: 8px" @click="submitCancel">取消</Button>
      <Button type="primary" @click="submitTemplateInfo">保存</Button>
    </div>
  </Drawer>
</template>
<script>
import { useData } from './useData.js';
const {
  getTemplateDescById,
} = useData();
export default {
  props: {
    nodeInfo: {
      type: Object,
      default: null,
    },
    templateList: {
      type: Array,
      default: () => [],
    },
    defaultTemplateId: {
      type: String,
      default: ''
    },
  },
  data() {
    return {
      curTemplateObj: [],
      showDrawer: true,
      activePanelArray: [],
      cacheRadioData: [],
      radioData: [],
    }
  },
  mounted() {
    this.initRadioData()
  },
  methods: {
    initRadioData() {
      this.templateList.forEach((item, index) => {
        if(item.templateId === this.defaultTemplateId) {
          this.$set(this.radioData, index, true)
          this.$set(this.cacheRadioData, index, true)
        } else {
          this.$set(this.radioData, index, false)
          this.$set(this.cacheRadioData, index, false)
        }
      })
    },
    handleRadioChange() {
      this.cacheRadioData.forEach((value,index) => {
        if(value && this.radioData[index]) {
          this.$set(this.radioData, index, false)
        }
      })
      this.cacheRadioData = JSON.parse(JSON.stringify(this.radioData))
    },
    async handlePanelChange(vArray) {
      vArray.forEach(async (v)=> {
        const index = this.templateList.findIndex(item => {
          return item.templateId == v
        })
        if(!this.templateList[index].desc) {
          const descRes = await getTemplateDescById({
            templateId: this.templateList[index].templateId
          })
          let curTemplate = JSON.parse(JSON.stringify(this.templateList[index]))
          curTemplate.conf = JSON.parse(JSON.stringify(descRes.conf))
          curTemplate.desc = JSON.parse(JSON.stringify(descRes.description))
          this.$set(this.templateList, index, curTemplate)
        }
      })
    },
    initData() {
      this.$emit('isShow', false);
      this.activePanelArray=[];
      this.cacheRadioData = [];
      this.radioData = [];
    },
    submitTemplateInfo() {
      this.radioData.forEach((value,index) => {
        if(value) {
          this.curTemplateObj = this.templateList[index]
        }
      })
      this.$emit('submit', this.curTemplateObj)
      this.initData()
    },
    submitCancel() {
      this.initData()

    },
  },
}
</script>
<style lang="scss" scoped>
    .demo-drawer-footer{
        width: 100%;
        bottom: 0;
        left: 0;
        border-top: 1px solid #e8e8e8;
        padding: 10px 16px;
        text-align: right;
        background: #fff;
    }
</style>
