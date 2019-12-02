<template>
  <div
    :class="{'is-empty': isEmpty}"
    class="we-file-tree">
    <we-tree
      v-if="fsType === 'workBench'"
      ref="tree"
      :data="tree"
      :node-props="nodeProps"
      :load-data-fn="loadDataFn"
      :before-remove="beforeRemove"
      :sort-fn="sortFn"
      :filter-node="filterNode"
      :node-edit-valid="nodeEditValid"
      :before-change="beforeChange"
      :highlight-path="highlightPath"
      :is-root-default-open="isRootDefaultOpen"
      @node-click="dispatch('work-bench-click', $event, arguments)"
      @node-edit="dispatch('work-bench-edit', $event, arguments)"
      @node-create="dispatch('work-bench-create', $event, arguments)"
      @node-contextmenu="dispatch('work-bench-contextMenu', $event, arguments)"
      @node-check="dispatch('work-bench-check', $event, arguments)"
      @node-dblclick="dispatch('work-bench-dblclick', $event, arguments)"/>
    <div v-if="!loading && isEmpty">暂无数据，请点击<a @click="refresh">刷新</a>重试！</div>
    <Spin
      v-if="loading"
      size="large"
      fix/>
  </div>
</template>
<script>
import { orderBy, map } from 'lodash';
export default {
  name: 'WeFileTree',
  props: {
    loading: {
      type: Boolean,
      default: false,
    },
    fsType: {
      type: String,
      default: 'workBench',
    },
    tree: {
      type: Array,
      default: () => {},
    },
    filterText: {
      type: String,
      default: '',
    },
    highlightPath: {
      type: String,
      default: '',
    },
    isRootDefaultOpen: {
      type: Boolean,
      defalut: false,
    },
    loadDataFn: {
      type: Function,
      default: function() {},
    },
    beforeRemove: {
      type: Function,
      default: function() {},
    },
    beforeChange: {
      type: Function,
      default: function() {},
    },
    nodeProps: {
      type: Object,
      default: () => {
        return {
          children: 'children',
          label: 'name',
          icon: 'icon',
          isLeaf: 'isLeaf',
        };
      },
    },
  },
  data() {
    return {
      // nodeProps: {
      //     children: 'children',
      //     label: 'text',
      //     icon: 'icon',
      //     isLeaf: 'isLeaf',
      // },
    };
  },
  computed: {
    isEmpty() {
      return !(this.tree && this.tree.length);
    },
  },
  watch: {
    filterText(val) {
      this.$refs.tree.store.filter();
    },
  },
  mounted() {},
  methods: {
    sortFn(data) {
      return orderBy(data, ['isLeaf', 'label'], ['asc', 'asc']);
    },
    filterNode(node, isCaseSensitive) {
      if (this.filterText) {
        let label = node.data.name;
        let searchText = this.filterText;
        // 是否对大小写敏感
        if (!isCaseSensitive) {
          label = label.toLowerCase();
          searchText = searchText.toLowerCase();
        }
        return label.indexOf(searchText) !== -1;
      }
      return true;
    },
    dispatch(eventName, ev, args) {
      const { node, nodeData, oldLabel } = args[1];
      this.$emit(eventName, {
        ev,
        nodeData,
        node,
        oldLabel,
      });
    },
    append(newData, currentData) {
      this.$refs.tree.store.append(newData, currentData);
    },
    nodeEditValid(data) {
      let msg = '';
      const label = data.label;
      const node = data.node;
      const isLeaf = node.isLeaf;
      const len = label.length;
      if (!isLeaf) {
        const reg = /^[\w\u4e00-\u9fa5]{1,200}$/;
        if (!reg.test(label)) {
          if (len === 0) {
            msg = '文件夹名称不得为空！';
          } else if (len >= 200) {
            msg = '文件夹名称超过200字符！';
          } else {
            msg =
                            '文件夹名称只支持大写、小写字母、数字、下划线和中文!';
          }
        }
      } else {
        // 输入的脚本名称前缀
        const prefix = label.substring(label.lastIndexOf('.'), 0);
        // 是否有后缀
        const isHaveSuffix = label.indexOf('.') !== -1;
        const newSuffix = label.substr(
          label.lastIndexOf('.'),
          label.length
        );
        const oldSuffix = node.name.substr(
          node.name.lastIndexOf('.'),
          node.name.length
        );
        const pointNum = label.split('.').length - 1;
        const reg = /^[\w\u4e00-\u9fa5]{1,200}\.[A-Za-z]+$/;
        const unReg = /[^\w\u4e00-\u9fa5]+/g;
        if (!reg.test(label)) {
          if (len === 0) {
            msg = '脚本名称不得为空！';
          } else if (!prefix.length) {
            msg = '脚本前缀名不得为空！';
          } else if (!isHaveSuffix) {
            msg = '后缀名不得为空！';
          } else if (pointNum > 1) {
            msg = '存在非法字符"."！';
          } else if (prefix.length >= 200) {
            msg = '脚本前缀名称超过200字符！';
          } else {
            let unRegString = '';
            try {
              map(prefix, (v) => {
                // 这里注意赋值的时候其实unReg.exec(prefix)已经执行了一次，所以要用变量装着
                const arr = unReg.exec(prefix);
                // 这里的arr[0]是匹配出来的字符串
                unRegString += arr[0] + ', ';
                if (!arr) {
                  const err = 'error';
                  throw err;
                }
                return unRegString;
              });
            } catch (errMsg) {
              msg = errMsg;
            }
            // 这里是去掉拼接字符串最后的逗号
            unRegString = unRegString.slice(
              0,
              unRegString.length - 2
            );
            msg = `存在非法字符"${unRegString}"`;
          }
        } else if (newSuffix !== oldSuffix) {
          msg = `脚本类型(后缀名)不得修改！`;
        }
      }
      return msg;
    },
    refresh() {
      this.$emit('on-refresh');
    },
  },
};
</script>
<style lang="scss" src="./index.scss">

</style>
