<template>
  <div class="path-find-wrap" v-if="showPanel">
    <div class="process-module-title">
      {{$t('message.workflow.process.nodepathTitle')}}
      <SvgIcon
        class="close-btn"
        @click="close"
        style="font-size: 1rem"
        icon-class="close2"
      />
    </div>
    <div v-show="showForm">
      <Form ref="formSave" :rules="ruleValidate" :model="saveModel" :label-width="85">
        <FormItem :label="$t('message.workflow.process.basenode')" prop="baseNode">
          <Select v-model="saveModel.baseNode" filterable clearable>
            <Option v-for="item in nodes" :value="item.key" :key="item.key">{{
              item.title
            }}</Option>
          </Select>
        </FormItem>
        <FormItem :label="$t('message.workflow.process.containnode')">
          <Select v-model="saveModel.contain" filterable multiple clearable>
            <Option v-for="item in nodes" :value="item.key" :key="item.key">{{
              item.title
            }}</Option>
          </Select>
        </FormItem>
        <FormItem :label="$t('message.workflow.process.updownnode')" prop="updown">
          <CheckboxGroup v-model="saveModel.updown">
            <Checkbox label="up">{{ $t('message.workflow.process.showupnode') }}</Checkbox>
            <Checkbox label="down">{{ $t('message.workflow.process.showdownnode') }}</Checkbox>
          </CheckboxGroup>
        </FormItem>
        <FormItem :label="$t('message.workflow.process.nodelevel')" prop="number">
          <Select
            v-model="saveModel.relation"
            style="width: 92px; margin-right: 8px"
          >
            <Option value="1"><=</Option>
            <Option value="2">=</Option>
          </Select>
          <Input style="width: calc(100% - 100px)" type="number" v-model="saveModel.number" />
        </FormItem>
      </Form>
      <Button type="primary" style="margin: 0 20px 0 85px" @click="onSubmitFind">{{ $t('message.workflow.process.findnode') }}</Button>
      <Button @click="toggleFrom">{{ $t('message.workflow.process.collapse') }}</Button>
    </div>
    <div v-show="!showForm" class="expand" @click="toggleFrom">
      <SvgIcon icon-class="open" />{{ $t('message.workflow.process.showcondition') }}
    </div>
    <div class="result-items" :style="`height: ${showForm ? 'calc(100% - 300px)' : 'calc(100% - 60px)'}`" v-if="showResult">
      <p class="process-module-title">{{ $t('message.workflow.process.resultnode') }}</p>
      <div v-for="(item, index) in pagedList" :key="index" class="path-item">
        <template v-for="(node, itemIdx) in item">
          <div
            :key="node.key"
            class="node-item"
            :class="{ current: node.key === saveModel.baseNode, contain: saveModel.contain.indexOf(node.key) > -1 }"
            @dblclick="clickNode(node, 'open-node')"
            @click="clickNode(node, 'open-params')"
          >
            <img :src="node.image" />
            {{ node.title }}
          </div>
          <div v-if="(itemIdx + 1) % 4 === 0 && item.length > itemIdx + 1" :key="node.key + '_arrow'" class="wrap-link">
            <div class="link-left">
              <SvgIcon
                class="arrow"
                icon-class="open"
              />
            </div>
          </div>
          <div v-else :key="node.key + '_arrow'" class="link">
            <div class="line"></div>
            <SvgIcon
              style="
                font-size: 14px;
                position: absolute;
                top: 4px;
                z-index: 1;
                right: 0;
              "
              icon-class="fi-expand-right"
            />
          </div>
        </template>
      </div>
      <div class="pagebar" v-if="pageData.total">
        <Page
          :page-size-opts="pageData.sizeOpts"
          :total="pageData.total"
          :current="pageData.pageNow"
          show-sizer
          show-total
          @on-change="handlePageChange"
          @on-page-size-change="handlePageSizeChange"
        />
      </div>
      <div v-if="list.length < 1" style="text-align: center;margin-top: 30px;">{{ $t('message.workflow.process.nopath') }}</div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: () => {
        return {
          nodes: [],
          edges: [],
        }
      },
    },
    show: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    nodes() {
      return this.data ? this.data.nodes : []
    },
    pagedList() {
      return this.list.slice((this.pageData.pageNow - 1) * this.pageData.pageSize, this.pageData.pageNow * this.pageData.pageSize)
    }
  },
  watch: {
    show(v) {
      this.showPanel = v
    }
  },
  data() {
    return {
      showPanel: false,
      showResult: false,
      saveModel: {
        baseNode: '',
        contain: [],
        updown: ['up'],
        relation: '1',
        number: 1,
      },
      showForm: true,
      list: [],
      pageData: {
        sizeOpts: [5, 10, 20],
        pageNow: 1,
        total: 0,
        pageSize: 10
      },
      ruleValidate: {
        baseNode: [
          {
            required: true,
            message: this.$t('message.workflow.process.reqBasenode'),
            trigger: ["blur","change"]
          }
        ],
        updown: [
          {
            type: 'array',
            required: true,
            message: this.$t('message.workflow.process.pathDir'),
            trigger: "change"
          }
        ],
        number: [
          { validator: (rule, value, callback) => {
            if (value && value < 1 || value % 1 !== 0) {
              callback(new Error(this.$t('message.workflow.process.levelNum')))
            }
            callback()
          }, trigger: "blur" }
        ]
      }
    }
  },
  methods: {
    onSubmitFind() {
      this.$refs.formSave.validate(valid => {
        if (valid) {
          this.pageData.pageNow = 1
          this.findPath()
        }
      })
    },
    findPath() {
      const nodeMap = {}
      this.nodes.forEach((it) => {
        nodeMap[it.key] = it
      })

      let listUpKey = {}
      let listDownKey = {}
      // 递归遍历基础节点上下游节点
      const stepArrayAction = (key, dir, k = '') => {
        if (dir === 'down') {
          const next = this.data.edges
            .filter((item) => item.source === key)
            .map((it) => it.target)
          if (next.length) {
            next.forEach((item) => {
              if (listDownKey[k]) {
                delete listDownKey[k]
              }
              listDownKey[k + '#' + item] = 1
              stepArrayAction(item, dir, k + '#' + item)
            })
          }
        }
        if (dir === 'up') {
          const prev = this.data.edges
            .filter((item) => item.target === key)
            .map((it) => it.source)
          if (prev.length) {
            prev.forEach((item) => {
              if (listUpKey[k]) {
                delete listUpKey[k]
              }
              listUpKey[item + '#' + k] = 1
              stepArrayAction(item, dir, item + '#' + k)
            })
          }
        }
      }
      if (this.saveModel.updown.includes('up')) {
        stepArrayAction(this.saveModel.baseNode, 'up', this.saveModel.baseNode)
      }
      if (this.saveModel.updown.includes('down')) {
        stepArrayAction(this.saveModel.baseNode, 'down', this.saveModel.baseNode)
      }
      // 拼接上下游得到基础节点全路径
      let result = []
      const upKeys = Object.keys(listUpKey)
      const downKeys = Object.keys(listDownKey)

      if (upKeys.length && downKeys.length) {
        upKeys.forEach((up) => {
          downKeys.forEach((down) => {
            const nodes = (up.replace(`#${this.saveModel.baseNode}`,'') + '#' + down).split('#').map((it) => nodeMap[it])
            result.push(nodes)
          })
        })
      } else {
        [...upKeys, ...downKeys].forEach((down) => {
          const nodes = down.split('#').map((it) => nodeMap[it])
          result.push(nodes)
        })
      }

      // 层级、包含节点过滤
      result = result.map(it => {
        const idx = it.findIndex(n => n.key == this.saveModel.baseNode)
        const start = idx - this.saveModel.number
        const end = idx + (this.saveModel.number - 0)
        if (start < end) {
          const listItems = it.slice(start >=0 ? start : 0, end + 1)
          const contain = this.saveModel.contain.every(it => listItems.some(n => n.key == it))
          if (this.saveModel.relation == 1) {
            // 层级小于等于
            return contain ? listItems : undefined
          } else {
            // 层级等于
            const len = listItems.length === Number(this.saveModel.number) + 1 || listItems.length === this.saveModel.number * 2 + 1
            return len && contain ? listItems : undefined
          }
        }
      }).filter(it => !!it)

      // 按层级过滤，缩短之后有重复，去重
      const tmp = {}
      result = result.map(it => {
        const k = it.map(n => n.key).join('#')
        if (!tmp[k]) {
          tmp[k] = 1
          return it
        }
      }).filter(it => !!it)

      // 补充图标
      const shapeImage = {}
      this.$parent.shapes.forEach((shape) => {
        shape.children.forEach((item) => {
          shapeImage[item.type] = item.image
        })
      })
      result.forEach((it) => {
        it.forEach((i) => {
          i.image = shapeImage[i.type]
        })
      })
      this.list = result
      this.pageData.total = result.length
      this.showResult = true
    },
    toggleFrom() {
      this.showForm = !this.showForm
    },
    close() {
      this.showPanel = false;
      this.$emit('close');
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
    },
    clickNode(node, evt) {
      this.$emit(evt, node)
    }
  },
  mounted() {},
}
</script>

<style scoped lang="scss">
@import '@dataspherestudio/shared/common/style/variables.scss';

.path-find-wrap {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 700px;
  padding: 10px;
  border: $border-width-base $border-style-base $border-color-base;
  border-top: 0;
  box-shadow: -6px 0 6px -6px $shadow-color;
  z-index: 1005;
  @include bg-color(#fff, $dark-base-color);
  @include border-color($border-color-base, $dark-background-color-header);
  overflow: hidden;
}
.process-module-title {
  font-weight: 600;
  margin-bottom: 8px;
  padding-bottom: 10px;
  font-size: 14px;
  border-bottom: 1px solid $border-color-base;
  @include border-color($border-color-base, $dark-background-color-header);
}
.path-item {
  padding: 15px;
  @include bg-color(#F7F7F8, #151A23);
  margin-bottom: 8px;
  border-radius: 4px;
  display: flex;
  flex-wrap: wrap;
}
.node-item {
  padding: 5px;
  border: 1px solid #CFD0D3;
  border-radius: 4px;
  display: inline-block;
  width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  img {
    width: 14px;
    display: inline-block;
    vertical-align: text-bottom;
  }
  &.current {
    color: $primary-color;
    border: 1px solid $primary-color;
  }
  &.contain {
    color: #A37AED;
    border: 1px solid #A37AED;
  }
}
.expand {
  color: $primary-color;
  text-align: center;
}
.line {
  display: inline-block;
  border-top: 1px solid #CFD0D3;
  width: 31px;
  height: 0;
  z-index: 2;
  position: relative;
  top: 2px;
  color: #CFD0D3;
}
.link {
  display: inline-block;
  vertical-align: top;
  position: relative;
  width: 35px;
  color: #CFD0D3;
  &:last-child {
    display: none;
  }
}
.wrap-link {
  width: 100%;
  border-bottom: 1px solid #CFD0D3;
  height: 10px;
  margin: 0 140px 0 60px;
  border-right: 1px solid #CFD0D3;
  margin-bottom: 10px;
  .link-left {
    border-left: 1px solid #CFD0D3;
    margin-top: 10px;
    height: 10px;
    position: relative;
    color: #CFD0D3;
    .arrow {
      font-size: 12px;
      position: absolute;
      left: -7px;
      top: -2px;
    }
  }
}
.close-btn {
  float: right;
  padding: 5px;
  cursor: pointer;
}
.result-items {
  margin-top: 10px;
  overflow: auto;
}
.pagebar {
  text-align: right;
  margin-top: 10px;
}
</style>
