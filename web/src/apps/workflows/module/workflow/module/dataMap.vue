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
      @node-detail="nodeDetail"
      @node-dblclick="dblclick"
    >
      <div class="map-title">
        <!-- <div class="button" @click="$emit('addFlow')">
          <Button
            icon="md-add"
            size="small"
          >创建工作流</Button>
        </div> -->
        <div class="button" @click="changeMode">
          <Button
            icon="md-repeat"
            size="small"
          >{{$t('message.workflow.narmalModel')}}</Button>
        </div>
        <span>{{$t('message.workflow.connectRelationshio')}}</span>
        <span class="bar-item">
          <Icon class="depend" type="md-arrow-round-forward" size="20"/>
          {{$t('message.workflow.dependent')}}
        </span>
        <span class="bar-item">
          <Icon class="inherit" type="md-arrow-round-forward" size="20"/>
          {{$t('message.workflow.fatherAndSon')}}
        </span>
        <span class="bar-item">
          <Icon class="both" type="md-arrow-round-forward" size="20"/>
          {{$t('message.workflow.double')}}
        </span>
      </div>
    </vueProcess>
    <Modal
      v-model="nodeDeleteShow"
      :title="$t('message.workflow.detail')"
      :footer-hide="true">
      <Form
        :label-width="100"
        label-position="right"
        ref="projectForm">
        <FormItem
          :label="$t('message.workflow.name') + '：'">
          <span>{{currentNodeDetial.title}}</span>
        </FormItem>
        <FormItem
          :label="$t('message.workflow.description') + '：'">
          <span>{{currentNodeDetial.desc}}</span>
        </FormItem>
        <FormItem
          :label="$t('message.workflow.process.nodeParameter.businessLabel') + '：'">
          <span>{{currentNodeDetial.businessTag}}</span>
        </FormItem>
        <FormItem
          label="$t('message.workflow.process.nodeParameter.applyLabel') + '：'">
          <span>{{currentNodeDetial.appTag}}</span>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
<script>
import vueProcess from "@/apps/workflows/components/vue-process";
import shape from "./shape.js";
import Vue from "vue";
import api from '@/common/service/api';
import{ NODEICON } from '@/apps/workflows/service/nodeType';
import proj from '@/apps/workflows/module/process/images/proj.svg';
export default {
  components: {
    vueProcess
  },
  props: {
    dataMapModuleValue: null
  },
  data() {
    return {
      viewOptions: {
        showBaseInfoOnAdd: false, // 不显示默认的拖拽添加节点弹出的基础信息面板
        shapeView: false, // 左侧shape列表
        control: false
      },
      originalData: null,
      data: {},
      shape,
      deepthLevel: {},
      mapMode: false,
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
    }
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
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.originalData = null;
      this.data = null
      this.getOriginData().then(() => {
        this.shape = shape;
        this.initLinks()
        this.projectGroupAction();
        this.initNodePosition();
        this.$nextTick(() => {
          Object.keys(this.projectGoup).forEach(key => {
            this.$refs.process.setNodeRunState(this.projectGoup[key].key, {
              nodeStyle: {
                "background-color": "#6A85A7",
                border: "1px solid rgb(102, 102, 102)",
                "z-index": 1,
                ...this.projectGoup[key].projectLayout
              }
            });
          });
          this.mapMode = true;
        });
      });
    },
    changeMode() {
      this.dataMapModule = !this.dataMapModule;
      this.$emit('changeMode', this.dataMapModule);
    },
    nodeDetail(node) {
      // 展示节点详情
      this.nodeDeleteShow = true;
      this.currentNodeDetial = node;
    },
    // 双击只有本工程的根工作流可以双击进去
    dblclick(node) {
      // 先排除工程节点的双击，再判断是本工程的
      if (node.type !== "projectNode" && node.projectData && node.projectData.id == +this.$route.query.projectID && node.isRootFlow === '1') {
        this.$emit("openWorkflow", node);
      } else {
        this.$Message.warning(this.$t('message.workflow.projectDetail.jumpRule'));
      }
    },
    // 获取数据
    getOriginData() {
      return api.fetch(`/dss/getFlowAppMap`, {
        projectID: +this.$route.query.projectID,
        projectVersionID: +this.$route.query.projectVersionID
      }).then((res) => {
        this.data = res.appMap
        this.data.nodes.forEach((node) => {
          node.key = node.id,
          node.type = "workflow.subflow",
          node.layout = {
            width: 200,
            height: 85,
            x: '',
            y: ''
          }
        })
        return res;
      })
    },
    // 将nodes按工程分组
    projectGroupAction() {
      this.data.nodes.forEach(node => {
        // 以工程id为key，先判断已添加工程key
        if (
          Object.keys(this.projectGoup).includes(String(node.projectData.id))
        ) {
          this.projectGoup[node.projectData.id].nodes.push(node);
        } else {
          this.projectGoup[node.projectData.id] = {
            nodes: [node]
          };
        }
      });
      // 再获取每个工程分组的内部连线关系
      Object.keys(this.projectGoup).forEach(key => {
        this.projectGoup[key].links = this.data.edges.filter(link => this.projectGoup[key].nodes.map(node => node.key).includes(link.source));
      });
    },
    initLinks() {
      this.data.edges = this.data.edges.map(link => {
        // type 'depend'、'inherit'、'both' 对应依赖、父子、依赖和父子
        if (link.edgeType === 'depend') {
          link.sourceLocation = 'bottom';
          link.targetLocation = 'bottom';
        } else {
          link.sourceLocation = 'right';
          link.targetLocation = 'left';
        }
        return link;
      })
    },
    initNodePosition() {
      // 获取每个工程的根节点
      // 再获取每个工程分组的内部连线关系
      Object.keys(this.projectGoup).forEach((key, index) => {
        let rootNodes = [];
        let nodeLevel = {};
        let projectLayout = {};
        this.findRootNodes(
          this.projectGoup[key].nodes,
          this.projectGoup[key].links,
          rootNodes
        );
        this.setNodeMaxStep(
          this.projectGoup[key].nodes,
          this.projectGoup[key].links,
          rootNodes,
          nodeLevel
        );
        this.setNodesDeepth(nodeLevel);
        this.projectGoup[key].projectLayout = this.bindNodePosition(
          this.projectGoup[key].nodes,
          nodeLevel,
          projectLayout,
          index
        );
        this.projectGoup[key].rootNodes = rootNodes;
        this.projectGoup[key].nodeLevel = nodeLevel;
        // this.projectGoup[key].projectLayout = projectLayout;
      });
      this.addProjectNode();
      this.nodeTemplate();
      this.originalData = this.data;
    },
    // 节点模板
    nodeTemplate() {
      // 将json节点数据转成模版
      this.data.nodes = this.data.nodes.map(element => {
        let render = ``;
        if (element.projectData && element.nodeType !== "projectNode") {
          element.currentProject = true;
          element.layout.width = 200;
          element.layout.height = 85;
          render = function (h) {
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
                  'class': 'title-content',

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
            ])
          }
          // render = `<div class="diy-node">
          //                   <span class="node-title">
          //                       <img class="title-icon" :src="NODEICON[element.type].icon"></img>
          //                       <span class="title-content">{{element.title}}</span>
          //                   </span>
          //                   <i class="line"></i>
          //                   <div class="label-bar">
          //                       <div class="label-item">
          //                           <Icon class="yellow" type="ios-pricetags" size="16" />
          //                           <span class="label">${this.$t('message.workflow.projectDetail.business')}</span>
          //                           <span class="label_content">{{labelAction(element.businessTag, 0)}}</span>
          //                           <span class="label_content">{{labelAction(element.businessTag, 1)}}</span>
          //                       </div>
          //                       <div class="label-item">
          //                           <Icon class="green" type="ios-pricetags" size="16" />
          //                           <span class="label">${this.$t('message.workflow.projectDetail.app')}</span>
          //                           <span class="label_content">{{labelAction(element.appTag, 0)}}</span>
          //                           <span class="label_content">{{labelAction(element.appTag, 1)}}</span>
          //                       </div>
          //                   </div>
          //                   <span v-if="false" class="right-arrow" @click="nodeDetail(element)">>></span>
          //               </div>`;
        } else {
          render = function (h) {
            return h('div', {
              'class': 'current-project',
            },
            [
              h('div', {
                'class': 'project-title'
              }, [
                h('span', {
                  'class': 'proj-select',
                  props: {
                    title: this.element.title
                  }
                },[
                  h('img', {
                    attrs: {
                      src: proj
                    }
                  })
                ]),
                h('h4', {
                }, this.element.title),
                h('span', {
                  'class': 'detail',
                  props: {
                    title: this.element.title
                  },
                  on: {
                    'click.stop': () => this.nodeDetail(this.element)
                  }
                }, this.element.title),
              ]),
              h('span', {
                'class': 'project-desc'
              }, this.element.desc),
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
              h('span', {
                'class': 'right-arrow',
                on: {
                  click: () => this.nodeDetail(this.element)
                }
              })
            ])
          }
        //   render = `<div class="current-project">
        //                 <div class="project-title">
        //                   <span
        //                     class="proj-select"
        //                     :title="element.title">
        //                     <svg t="1572505474223" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1741" xmlns:xlink="http://www.w3.org/1999/xlink" width="30" height="18">
        //                       <path d="M1019.669291 397.544431c0 0.211533 0.211533 0.211533 0.211534 0.423066v-0.740366c-0.211533-0.105767-0.211533-0.105767-0.211534 0.3173z m0 0" p-id="113308" fill="#1d9cf0"></path><path d="M1201.693609 504.368697H421.559171c-28.133915 0-53.2006 18.297622-61.873461 45.162338L291.466254 759.266201V194.15526H585.285867c0 71.709754 58.065863 129.987151 129.881384 129.987151H985.50668v97.834105c0.211533 17.768789 14.701557 32.04728 32.364579 32.047279 17.980322 0 32.364579-14.701557 32.36458-32.364579 0-0.211533-0.211533-0.211533-0.211534-0.423067h0.211534v-96.987971c0-35.854877-29.085815-65.046459-65.046459-65.046459H715.378785c-35.854877 0-65.046459-29.085815-65.046459-65.046459 0-17.134189-6.980596-33.739545-19.143755-45.902704-12.163159-12.163159-28.768515-19.143755-45.902704-19.143754H291.677787c-35.854877 0-64.834926 29.085815-65.046459 64.834925V959.165074c0.211533 35.854877 29.085815 64.834926 65.046459 64.834926h779.817138c28.345449 0 53.412133-18.086089 61.873461-45.162338l133.160149-409.528273c0-35.749111-28.980048-64.729159-64.834926-64.940692z m0 0M1237.336953 181.992101c-33.528012-107.247332-154.736535-286.098652-553.476615-103.439734 0 0 410.26864-104.4974 462.940406 136.22738l-80.065316 15.759223 178.639787 112.324129 64.517625-196.620108-72.555887 35.74911z m19.989887 3.913364" fill="#1d9cf0" p-id="113309"></path><path d="M1073.927557 1023.894233s-23.691718-11.740093-35.643344-20.412953c-5.394097-3.913364-34.374145-29.085815-35.431811-30.883847l-697.530724-0.6346s-22.845585 3.384531-44.316205-12.903525c-23.797485-18.086089-34.162612-53.412133-34.162612-53.412133l-0.105766 47.806503s-0.423066 12.374692 2.644165 23.585952c2.009565 7.403662 6.028696 14.278491 7.615195 17.345722 15.124624 29.403115 54.892865 29.508881 54.892865 29.508881h782.038237z m-428.143195-5.711396" p-id="113310" fill="#1d9cf0"></path>
        //                     </svg>
        //                   </span>
        //                   <h4>{{element.title}}</h4>
        //                   <span @click.stop="nodeDetail(element)" class="detail">${this.$t('message.workflow.detail')}</span>
        //                 </div>
        //                 <span class="project-desc">
        //                     {{element.desc}}
        //                 </span>
        //                 <div class="label-bar">
        //                     <div class="label-item">
        //                         <Icon class="yellow" type="ios-pricetags" size="16" />
        //                         <span class="label">${this.$t('message.workflow.projectDetail.business')}</span>
        //                         <span class="label_content">{{labelAction(element.businessTag, 0)}}</span>
        //                         <span class="label_content">{{labelAction(element.businessTag, 1)}}</span>
        //                     </div>
        //                     <div class="label-item">
        //                         <Icon class="green" type="ios-pricetags" size="16" />
        //                         <span class="label">${this.$t('message.workflow.projectDetail.app')}</span>
        //                         <span class="label_content">{{labelAction(element.appTag, 0)}}</span>
        //                         <span class="label_content">{{labelAction(element.appTag, 1)}}</span>
        //                     </div>
        //                 </div>
        //               </div>`;
        }
        element.template = Vue.extend({
          data() {
            return {
              element: element,
              NODEICON
            };
          },
          props: {
            node: {
              type: Object,
              default: ()=> ({})
            }
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
          render
        });
        return element;
      });
    },
    // 查找当前工程的根节点
    findRootNodes(currentNodeList, currentLinks, rootNodes) {
      var len = currentNodeList.length;
      for (var i = 0; i < len; i++) {
        var node = currentNodeList[i].key;
        var isRootNode = true;
        currentLinks.forEach(link => {
          var inNode = link.target;
          //当前节点只要有作为输入点就不是根节点
          if (
            node == inNode &&
            currentNodeList.map(node => node.key).includes(link.source)
          ) {
            isRootNode = false;
          }
        });
        if (isRootNode) {
          if (!rootNodes.includes(node)) {
            rootNodes.push(node);
          }
        }
      }
    },
    //根据根节点和所有有关系的节点及关系链找到每个节点的最大步长
    setNodeMaxStep(currentNodeList, currentLinks, rootNodes, nodeLevel) {
      const len = currentNodeList.length;
      for (let i = 0; i < len; i++) {
        const searchNode = currentNodeList[i].key; //每次需要判定最大步长的节点

        //每次从根节点开始查找
        rootNodes.forEach(nodeKey => {
          let nodeArr = new Array(); //存放依次遍历的同级节点,首次放入根节点,逐步查找下一级别
          nodeArr.push(nodeKey);
          let stepCount = 1; //从根节点级别时步长计数器归零
          //设置根节点的级别,根节点的步长为零
          nodeLevel[nodeKey] = {};
          nodeLevel[nodeKey].breadth = stepCount;
          //递归查找searchNode的最大步长
          this.recordNodeStep(
            nodeArr,
            searchNode,
            stepCount,
            currentLinks,
            nodeLevel
          );
        });
      }
    },
    recordNodeStep(arr, searchNode, stepCount, currentLinks, nodeLevel) {
      if (arr != null && arr != undefined && arr.length > 0) {
        let tempNodeArr = new Array(); //临时存储下一级别节点的数组
        stepCount++; //逐级增加步长,级别的判定就是arr数组的出现频次
        for (let n = 0; n < arr.length; n++) {
          let tempNode = arr[n]; //作为输出节点去查找输入点(即查找下一级节点)
          currentLinks.forEach(link => {
            const inNode = link.target;
            const outNode = link.source;
            if (tempNode === outNode) {
              //查找到输入点
              if (!tempNodeArr.includes(inNode)) {
                tempNodeArr.push(inNode);
              }
              //节点作为输出点时找到对应的输入点,若输入点等于需要判定步长的节点记录步长信息
              if (inNode === searchNode) {
                //找到当前节点则记录当前步长
                if (!nodeLevel[inNode]) {
                  nodeLevel[inNode] = {};
                }
                //考虑到被判定步长的节点有可能存在多个根节点的关系链中且每次切换根节点计算步长都会将步长计数器归零,因此需要保留最大步长数
                if (
                  nodeLevel[inNode].breadth != undefined &&
                  nodeLevel[inNode].breadth
                ) {
                  let lastBreadth = nodeLevel[inNode].breadth;
                  if (stepCount > lastBreadth) {
                    nodeLevel[inNode].breadth = stepCount;
                  }
                } else {
                  nodeLevel[inNode].breadth = stepCount;
                }
              }
            }
          });
        }
        arr = tempNodeArr;
        this.recordNodeStep(
          arr,
          searchNode,
          stepCount,
          currentLinks,
          nodeLevel
        );
      }
    },
    setNodesDeepth(nodeLevel) {
      let deepthLevel = {};
      Object.keys(nodeLevel).forEach(key => {
        const breadth = nodeLevel[key].breadth;

        if (deepthLevel[breadth] === undefined || !deepthLevel[breadth]) {
          deepthLevel[breadth] = 0;
        }
        deepthLevel[breadth] += 1;
        nodeLevel[key].deepth = deepthLevel[breadth];
      });
    },
    // 根据计算的步长和深度来确定xy值
    bindNodePosition(currentNodeList, nodeLevel, projectLayout) {
      // 从根节点开始算位置
      let init = {
        initX: 100,
        initY: 100,
        initGap: 50
      };
      // 计算最大的步长算出工程宽度
      let maxStepX = 1,
        maxStepY = 1,
        maxWidth,
        maxHight;
      Object.keys(nodeLevel).forEach(key => {
        if (nodeLevel[key].breadth > maxStepX) {
          maxStepX = nodeLevel[key].breadth;
        }
        if (nodeLevel[key].deepth > maxStepY) {
          maxStepY = nodeLevel[key].deepth;
        }
      });
      maxWidth = maxStepX * 200 + (maxStepX - 1) * init.initGap + 100;
      maxHight = maxStepY * 85 + (maxStepY - 1) * init.initGap + 200; // 一个一百是多余间隙，一个是头部高度
      projectLayout = {
        width: maxWidth,
        height: maxHight,
        x: this.projectInitX,
        y: 50
      };
      this.projectInitX += maxWidth + init.initGap;
      currentNodeList = currentNodeList.map(node => {
        // 将计算的步长和深度绑定到节点
        node.breadthAndDeep = nodeLevel[node.key];
        node.layout.x =
          50 +
          projectLayout.x +
          (node.breadthAndDeep.breadth - 1) *
            (node.layout.width + init.initGap);
        node.layout.y =
          110 +
          projectLayout.y +
          (node.breadthAndDeep.deepth - 1) *
            (node.layout.height + init.initGap);
        node.projectData.layout = projectLayout;
        return node;
      });
      return projectLayout;
    },
    addProjectNode() {
      let tepArray = [];
      Object.keys(this.projectGoup).forEach(key => {
        this.projectGoup[key].nodes.forEach(node => {
          tepArray.push(node);
        });
      })
      this.data.nodes = JSON.parse(JSON.stringify(tepArray));

      Object.keys(this.projectGoup).forEach(key => {
        this.projectGoup[key].key = `${new Date().getTime()}.${Math.ceil(
          Math.random() * 1000
        )}`;
        this.data.nodes.push({
          key: this.projectGoup[key].key,
          nodeType: "projectNode",
          type: "projectNode",
          layout: this.projectGoup[key].projectLayout,
          ...this.projectGoup[key].nodes[0].projectData
        });
      });
    }
  }
};
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
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
        // width: 50px;
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
      font-size: $font-size-large;
      font-weight: 500;
      cursor: pointer;
    }
    .yellow {
      color: rgb(228, 178, 11);
    }
    .green {
      color: $success-color;
    }
  }
  /deep/ .current-project {
    height: 100px;
    box-sizing: border-box;
    padding: 5px;
    border-bottom: 2px solid rgb(102, 102, 102);
    .project-title {
      font-size: $font-size-large;
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
        font-size: $font-size-small;
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
          color: $primary-color;
        }
      }
    }
    .yellow {
      color: rgb(228, 178, 11);
    }
    .green {
      color: $success-color;
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
        border-radius: $border-radius-small;
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

