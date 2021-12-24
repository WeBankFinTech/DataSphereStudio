<template>
  <div class="process-module">
    <vueProcess
      v-if="originalData"
      ref="process"
      :shapes="shape"
      :value="originalData"
      :view-options="viewOptions"
      :mapMode="mapMode"
      :ctx-menu-options="nodeMenuOptions"
      @node-click="nodeClick"
    >
      <div
        class="button"
        :title="$t('message.workflow.process.params')"
        ref="paramButton"
        @click.stop="stopClick">
        <SvgIcon class="icon" icon-class="params" color="#666"/>
        <span>{{$t('message.workflow.process.params')}}</span>
      </div>
      <div class="devider" />
      <div
        class="button"
        :title="$t('message.workflow.process.resource')"
        ref="resourceButton"
        @click.stop="stopClick">
        <SvgIcon class="icon" icon-class="fi-export" color="#666"/>
        <span>{{$t('message.workflow.process.resource')}}</span>
      </div>
      <div class="devider" />
      <div
        :title="$t('message.workflow.process.run')"
        class="button"
        @click="stopClick">
        <SvgIcon class="icon" icon-class="play-2" color="#666"/>
        <span>{{$t('message.workflow.process.run')}}</span>
      </div>
      <div class="devider" />
      <div
        :title="$t('message.workflow.process.save')"
        class="button"
        @click="stopClick">
        <SvgIcon class="icon" icon-class="save-2" color="#666"/>
        <span>{{$t('message.workflow.process.save')}}</span>
      </div>
      <div class="devider"></div>
      <div class="button" @click="changeMode">
        <SvgIcon class="icon" icon-class="change" color="#666"/>
        <span>
          {{$t('message.workflow.narmalModel')}}
        </span>
      </div>
    </vueProcess>
  </div>
</template>
<script>
import vueProcess from "@/apps/workflows/components/vue-process";
import shape from "../shape.js";
import Vue from "vue";
import{ NODEICON } from '@/apps/workflows/service/nodeType';
export default {
  components: {
    vueProcess
  },
  props: {
    dataMapModuleValue: null,
    mapOriginData: null
  },
  data() {
    return {
      viewOptions: {
        showBaseInfoOnAdd: false, // 不显示默认的拖拽添加节点弹出的基础信息面板
        shapeView: false, // 左侧shape列表
        control: true
      },
      originalData: null,
      data: null,
      shape,
      deepthLevel: {},
      mapMode: true,
      projectGoup: {},
      projectInitX: 100,
      nodeDeleteShow: false,
      currentNodeDetial: {},
      dataMapModule: this.dataMapModuleValue
    };
  },
  created() {

  },
  watch: {
    dataMapModuleValue(val) {
      this.dataMapModule = val;
    },
    mapOriginData() {
      this.init();
    }
  },
  mounted() {
    this.init();
  },
  computed: {
    nodeMenuOptions() {
      return {
        defaultMenu: {
          config: false, // 不展示默认的基础信息菜单项
          param: false, // 不展示默认的参数配置菜单项
          copy: false,
          delete: false
        },
        beforeShowMenu: () => {
          // type : 'node' | 'link' | 'view' 分别是节点右键，边右键，画布右键
          return [];
        }
      }
    }
  },
  methods: {
    init() {
      this.originalData = null;
      this.data = null
      if (this.mapOriginData) {
        this.data = JSON.parse(JSON.stringify(this.mapOriginData));
        this.nodeTemplate();
        this.originalData = this.data;
      }
    },
    changeMode() {
      this.dataMapModule = !this.dataMapModule;
      this.$emit('changeMode', this.dataMapModule);
    },
    // 双击只有本工程的根工作流可以双击进去
    // dblclick(node) {
    //   // 先排除工程节点的双击，再判断是本工程的
    //   if (node.type !== "projectNode" && node.projectData && node.projectData.id == +this.$route.query.projectID && node.isRootFlow === '1') {
    //     this.$emit("openWorkflow", node);
    //   } else {
    //     this.$Message.warning(this.$t('只支持本工程的根工作流可跳转！'));
    //   }
    // },
    // 节点模板
    nodeTemplate() {
      // 将json节点数据转成模版
      this.data.nodes = this.data.nodes.map(element => {
        element.currentProject = true;
        element.layout.width = 200;
        element.layout.height = 85;

        element.render = Vue.extend({
          data() {
            return {
              element: element,
              NODEICON
            };
          },
          methods: {
            nodeDetail(node) {
              this.$emit("nodeDetail", node);
            },
            labelAction(element, index) {
              // 先判断标签是否为空
              if (element) {
                return element.split(',')[index] || '';
              } else {
                return '';
              }
            }
          },
          // 节点有两种一种是工作流的节点，一种是工程的节点
          render(h) {
            return h('div', {
              'class': 'diy-node',
            },
            [
              h('span', {
                'class': 'node-title'
              }, [
                h('img', {
                  'class': 'title-icon',
                  attrs: {
                    src: NODEICON[this.element.type].icon
                  }
                }),
                h('span', {
                  'class': 'title-content'
                }, this.element.title)
              ]),
              h('i', {
                'class': 'line'
              }),
              h('div', {
                'class': 'label-bar'
              },[
                h('div', {
                  'class': 'label-item'
                }, [
                  h('Icon', {
                    'class': 'yellow',
                    props: {
                      type: 'ios-pricetags',
                      size: 16
                    }
                  }),
                  h('span', {
                    'class': 'label'
                  }, this.$t('message.workflow.projectDetail.business')),
                  h('span', {
                    'class': 'label_content'
                  }, this.labelAction(this.element.businessTag, 0)),
                  h('span', {
                    'class': 'label_content'
                  }, this.labelAction(this.element.businessTag, 1))
                ]),
                h('div', {
                  'class': 'label-item',
                },
                [
                  h('Icon', {
                    'class': 'green',
                    props: {
                      type: 'ios-pricetags',
                      size: 16
                    }
                  }),
                  h('span', {
                    'class': 'label'
                  }, this.$t('message.workflow.projectDetail.app')),
                  h('span', {
                    'class': 'label_content'
                  }, this.labelAction(this.element.appTag, 0)),
                  h('span', {
                    'class': 'label_content'
                  }, this.labelAction(this.element.appTag, 1))
                ]),
              ]),
              // h('span', {
              //   'class': 'right-arrow',
              //   on: {
              //     click: () => this.nodeDetail(this.element)
              //   }
              // })
            ])
          }
        });
        return element;
      });
    },
    nodeClick(node) {
      this.$emit('nodeClick', node);
    },
    stopClick() {
      this.$Message.warning(this.$t('message.workflow.mapModelNotSupported'));
    }
  }
};
</script>
<style lang="scss" scoped>
.process-module {
  position: relative;
  height: 100%;
  width: 100%;
  /deep/ .diy-node {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: flex-start;
    border: 2px solid rgb(102, 102, 102);
    height: 85px;
    position: relative;
    .node-title {
      height: 27px;
      font-weight: 600;
      color: black;
      font-size: 16px;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      .title-icon {
        width: 15px;
        height: 15px;
        margin: 0 10px;
      }
      .title-content {
        max-width: calc(100% - 35px);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    .line {
      width: 100%;
      height: 2px;
      background-color: rgb(102, 102, 102);
    }
    .label-bar {
      width: 90%;
      padding: 0 5px;
      margin-top: 10px;
    }
    .label-item {
      width: 100%;
      display: flex;
      justify-content: flex-start;
      align-items: center;
      .label {
        color: black;
        font-weight: 600;
        margin: 0 3px;
      }
      .label_content {
        display: inline-block;
        width: 50px;
        line-height: 20px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin: 0 3px;
      }
    }
    .right-arrow {
      position: absolute;
      right: 8px;
      bottom: 9px;
      font-size: 17px;
      font-weight: 500;
      cursor: pointer;
    }
    .yellow {
      color: rgb(228, 178, 11);
    }
    .green {
      color: rgb(21, 168, 98);
    }
  }
  /deep/ .current-project {
    height: 100px;
    box-sizing: border-box;
    padding: 5px;
    border-bottom: 2px solid rgb(102, 102, 102);
    .project-title {
      font-size: 16px;
      display: flex;
      font-weight: 400;
      align-items: center;
      justify-content: flex-start;
      position: relative;
      .proj-select {
        margin-right: 5px;
        width: 30px;
        height: 20px;
      }
      .detail {
        display: inline-block;
        font-size: 12px;
        color: #fff;
        width: 32px;
        height: 18px;
        background-color: rgba(29, 156, 240, .7);
        border-radius: 3px;
        text-align: center;
        line-height: 18px;
        position: absolute;
        right: 10px;
        top: 5px;
        cursor: pointer;
      }
    }
    .project-desc {
      width: 100%;
      display: block;
      padding: 5px;
      padding-left: 26px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .label-bar {
      padding-left: 5px;
      .label-item {
        width: 100%;
        .label {
          color: black;
          font-weight: 600;
          padding-right: 3px;
        }
        .label_content {
          color: #2d8cf0;
        }
      }
    }
    .yellow {
      color: rgb(228, 178, 11);
    }
    .green {
      color: rgb(21, 168, 98);
    }
  }
  .map-title {
    height: 100%;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding-left: 85px;
    .button {
      margin-right: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .bar-item {
      margin-right: 10px;
      line-height: 20px;
      .bar {
        display: inline-block;
        width: 8px;
        height: 8px;
        border-radius: 4px;
      }
      .depend {
        color: #1155cc;
      }
      .inherit {
        color: #93c47d;
      }
      .both {
        color: #f6b26b;
      }
    }
  }
}
</style>

