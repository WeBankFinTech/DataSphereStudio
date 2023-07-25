/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* eslint-disable require-jsdoc */
import { findComponentUpward, getKey } from './util'
export function createStore() {
  return {
    state: {
      mapMode: false, // 地图模式
      disabled: false,
      fullScreen: false,
      shapes: [],
      nodes: [],
      links: [],
      // 连线的类型，straight：直线（直角折线）；curve：斜线
      linkType: 'straight',
      // 是否已经进行过格式化
      isFormatted: false,
      // 拖拽的对象
      draging: {
        type: '',
        data: {}
      },
      // 选中的对象
      choosing: {
        type: '',
        key: ''
      },
      // 复制的对象
      copying: {
        type: '',
        key: ''
      },
      // 编辑基础信息的对象
      editBaseInfoNode: null,
      // 编辑参数的对象
      editParamNode: null,
      // 基础配置信息
      baseOptions: {
        pageSize: 1,
        shapeViewOffsetX: 0,
        shapeViewOffsetY: 0,
        nodeViewOffsetX: 0,
        nodeViewOffsetY: 0
      },
      // shape配置信息--工具栏的图形
      shapeOptions: {
        viewWidth: 180,
        width: 22,
        height: 22
      },
      // 节点配置信息
      nodeOptions: {
        width: 150,
        height: 40,
        borderWidth: 1,
        radiusWidth: 4, // 节点圆角大小
        anchorSize: 12, // 连接点大小
        borderColor: '#6A85A7'
      },
      // 连线的配置
      linkerOptions: {
        lineWidth: 2,
        borderWidth: 10, // 影响linker-box的宽高，影响linker的canvas在box中展示
        extWidth: 30, // 连接线弯折时距离起点或者终点的宽度
        borderColor: '#B3C1D3'
      },
      // 节点连线连接点，默认上下左右四个四个点，居中
      arrows: ['left', 'top', 'right', 'bottom'],
      // 网格配置
      gridOptions: {
        show: false,
        height: 5000,
        width: 5000
      },
      // x轴 提示线
      snapLineX: {
        show: false,
        left: 0,
        top: 0
      },
      // y轴 提示线
      snapLineY: {
        show: false,
        left: 0,
        top: 0
      }
    },
    getters: {},
    mutations: {
      UPDATE_DISABLED: function (state, data) {
        state.disabled = data;
      },
      UPDATE_MAPMODE: function (state, data) {
        state.mapMode = data;
      },
      TRUNCATE_NODE: function (state) {
        state.nodes.splice(0, state.nodes.length);
      },
      ADD_NODE: function (state, data) {
        state.nodes.push(data);
      },
      UPDATE_NODE: function (state, data) {
        let len = state.nodes.length;
        for (let i = 0; i < len; i++) {
          if (state.nodes[i].key == data.key) {
            state.nodes.splice(i, 1, data.obj);
            break;
          }
        }
      },
      UPDATE_ALLNODES: function (state, data) {
        state.nodes = data
      },
      DELETE_NODE: function (state, index) {
        state.nodes.splice(index, 1);
      },
      TRUNCATE_LINKER: function (state) {
        state.links.splice(0, state.links.length);
      },
      ADD_LINKER: function (state, data) {
        state.links.push(data);
      },
      UPDATE_LINKER: function (state, data) {
        state.links.splice(data.index, 1, data.obj);
      },
      UPDATE_ALLLINKERS: function (state, data) {
        state.links = data
      },
      DELETE_LINKER: function (state, index) {
        state.links.splice(index, 1);
      },
      DELETE_LINKER_BY_KEY: function (state, key) {
        for (let i = 0; i < state.links.length; i++) {
          if (state.links[i].key === key) {
            state.links.splice(i, 1);
          }
        }
      },
      UPDATE_LINKTYPE: function (state, data) {
        state.linkType = data;
      },
      UPDATE_ISFORMATTED: function (state, data) {
        state.isFormatted = data;
      },
      UPDATE_PAGESIZE: function (state, data) {
        state.baseOptions.pageSize = data;
      },
      UPDATE_SHAPEVIEW_OFFSET: function (state, data) {
        state.baseOptions.shapeViewOffsetX = data.x;
        state.baseOptions.shapeViewOffsetY = data.y;
      },
      UPDATE_NODEVIEW_OFFSET: function (state, data) {
        state.baseOptions.nodeViewOffsetX = data.x;
        state.baseOptions.nodeViewOffsetY = data.y;
      },
      UPDATE_DRAGING: function (state, data) {
        state.draging.type = data.type;
        state.draging.data = data.obj;
      },
      UPDATE_SNAPLINE_X: function (state, data) {
        state.snapLineX.show = data.show;
        state.snapLineX.left = data.left;
        state.snapLineX.top = data.top;
      },
      UPDATE_SNAPLINE_Y: function (state, data) {
        state.snapLineY.show = data.show;
        state.snapLineY.left = data.left;
        state.snapLineY.top = data.top;
      },
      UPDATE_FULL_SCREEN: function (state, data) {
        state.fullScreen = data;
      },
      UPDATE_CHOOSING: function (state, data) {
        let changeType = state.choosing.type != data.type;
        state.choosing = { type: data.type, key: data.key };
        let len = state.nodes.length;
        if (data.type == 'node') {
          let hasKey = data.key && data.key.length > 0;
          for (let i = 0; i < len; i++) {
            state.nodes[i].selected =  hasKey && data.key.indexOf(state.nodes[i].key) > -1;
          }
        } else if (changeType && data.type == 'link') {
          for (let i = 0; i < len; i++) {
            state.nodes[i].selected = false;
          }
        }
      },
      UPDATE_COPYING: function (state, data) {
        state.copying.type = data.type;
        state.copying.key = data.key;
      },
      UPDATE_GIRD_OPTION: function (state, data) {
        state.gridOptions = Object.assign(state.gridOptions, data)
      },
      UPDATE_NODE_OPTION: function (state, data) {
        state.nodeOptions = Object.assign(state.nodeOptions, data)
      },
      UPDATE_EDIT_BASEINFO_NODE: function (state, data) {
        state.editBaseInfoNode = data
      },
      UPDATE_EDIT_PARAM_NODE: function (state, data) {
        state.editParamNode = data
      },
      UPDATE_SHAPE_OPTIONS: function (state, data) {
        state.shapeOptions = { ...state.shapeOptions, ...data }
      }
    },
    actions: {
      // 把外部结构数据转换成内部结构数据
      initDeginer({ commit, state }, data) {
        commit(this, 'TRUNCATE_NODE');
        commit(this, 'TRUNCATE_LINKER');
        if (data.value) {
          let shapes = [];
          data.shapes.forEach(parent => {
            shapes = shapes.concat(parent.children);
          });
          if (Array.isArray(data.value.nodes)) {
            // 恢复noder数据结构
            data.value.nodes.forEach(node => {
              node.selected = !!node.selected;
              let choosingShapes = shapes.filter(shape => {
                return shape.type == node.type;
              });
              if (choosingShapes.length > 0) {
                let shape = choosingShapes[0];
                let innerKeys = ['layout', 'type', 'title', 'desc', 'image', 'ready', 'viewOffsetX', 'viewOffsetY', 'width', 'height', 'borderWidth',
                  'radiusWidth', 'anchorSize', 'borderColor', 'key', 'x', 'y', 'createTime', 'modifyTime'];
                // 把跟流程图逻辑无关的属性收集起来
                let _other = {};
                for (let p in node) {
                  if (innerKeys.indexOf(p) === -1) {
                    _other[p] = node[p]
                  }
                }
                commit(
                  this,
                  'ADD_NODE',
                  Object.assign(
                    {
                      ready: true,
                      image: shape.image,
                      type: shape.type,
                      key: node.key,
                      title: node.title,
                      desc: node.desc,
                      params: node.params,
                      resources: node.resources,
                      createTime: node.createTime,
                      modifyTime: node.modifyTime
                    },
                    state.nodeOptions,
                    node.layout,
                    _other
                  )
                );
              }
            })
            let chooseNodes = data.value.nodes.filter(node => node.selected).map(node => node.key)
            commit(this, 'UPDATE_CHOOSING', {
              type: 'node',
              key: chooseNodes
            });
          }
          if (Array.isArray(data.value.edges)) {
            // 恢复linker数据结构
            data.value.edges.forEach(edge => {
              let linkBorderColor = state.linkerOptions.borderColor
              let beginNode = state.nodes.filter(_node => {
                return _node.key == edge.source;
              })[0];
              let endNode = state.nodes.filter(_node => {
                return _node.key == edge.target;
              })[0];

              if (beginNode && endNode) {
                // 再数据地图模式下增加三种线的颜色
                // 'depend' inherit  both
                if (edge.edgeType === 'depend') {
                  linkBorderColor = '#1155cc'
                } else if (edge.edgeType === 'inherit') {
                  linkBorderColor = '#93c47d'
                } else if (edge.edgeType === 'both') {
                  linkBorderColor = '#f6b26b'
                } else {
                  linkBorderColor = state.linkerOptions.borderColor
                }
                commit(this, 'ADD_LINKER', {
                  key: edge.key || getKey(),
                  beginNode: beginNode,
                  beginNodeArrow: edge.sourceLocation,
                  endNode: endNode,
                  data: edge.data,
                  linkType: edge.linkType,
                  label: edge.label,
                  endNodeArrow: edge.targetLocation,
                  ...state.linkerOptions,
                  borderColor: linkBorderColor
                });
              }
            })
          }
        }
        if (data.gridOptions) {
          commit(this, 'UPDATE_GIRD_OPTION', data.gridOptions)
        }
        if (data.nodeOptions) {
          commit(this, 'UPDATE_NODE_OPTION', data.nodeOptions)
        }
      },
      // 设置是否禁用
      setDisabled({ commit }, data) {
        commit(this, 'UPDATE_DISABLED', data);
      },
      // 设置模式
      setMapMode({ commit }, data) {
        commit(this, 'UPDATE_MAPMODE', data);
      },
      // 设置拖拽的对象
      setDraging({ commit }, data) {
        commit(this, 'UPDATE_DRAGING', {
          type: data.type,
          obj: data.data
        });
      },
      // 清除拖拽的对象
      clearDraging({ commit }) {
        commit(this, 'UPDATE_DRAGING', {
          type: '',
          obj: {}
        });
      },
      deleteNode({ commit, state }, key) {
        // 点击删除
        // 删除一个节点，同时需要删除它的连线
        state.nodes.forEach((node, index) => {
          if (node.key === key) {
            commit(this, 'DELETE_NODE', index);
          }
        });
        for (let i = 0; i < state.links.length; i++) {
          let link = state.links[i];
          if (link.beginNode.key === key || link.endNode.key === key) {
            commit(this, 'DELETE_LINKER', i);
            // 需要这个，不然下个删除不了
            i--;
          }
        }
      },
      getNodeByKey({ state }, k) {
        return state.nodes.filter(node => {
          return node.key == k;
        })[0];
      },
      getLinkByKey({ state }, k) {
        return state.links.filter(link => {
          return link.key == k;
        })[0];
      }
    }
  };
}

export const commit = function (store, name, ...arg) {
  if (store.mutations[name]) {
    store.mutations[name].call(store, store.state, ...arg)
  }
}

export const mapActions = function (actions) {
  let res = {};
  if (Array.isArray(actions)) {
    actions.forEach(item => {
      res[item] = function (...arg) {
        let store = this.$store;
        return store.actions[item].call(store, {
          commit,
          state: store.state
        }, ...arg)
      }
    })
  }
  return res
}

export let mixin = {
  beforeCreate() {
    let $root = findComponentUpward(this, 'Designer');
    if (!$root) {
      this.$store = createStore()
    } else {
      this.$store = $root.$store
    }
  },
  data() {
    return {
      state: this.$store.state
    }
  }
}

export default createStore
