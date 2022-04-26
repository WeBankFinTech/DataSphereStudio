<template>
  <div style="height:100%; width:100%">
    <vueProcess :shapes="shapes" :value="value" :view-options="viewOptions" @change="change" @message="message"
      @node-click="click" @node-dblclick="dblclick" />
  </div>
</template>
<script type="text/ecmascript-6">
import Vue from 'vue'
import shapes from './config.js'
import vueProcess from '@dataspherestudio/shared/components/vue-process';
export default {
  components: {
    vueProcess
  },
  data() {
    return {
      shapes,
      value: {
        'edges': [{
          'source': '155782031442826',
          'target': '155782031442825',
          'sourceLocation': 'left',
          'targetLocation': 'right',
          'label': {
            text: 'ddd'
          }
        }],
        'nodes': [{
          'data': {
            date: '2019-11-01 15:30:00',
            num: 1000
          },
          'key': '155782031337810',
          'title': 'hive',
          'desc': '',
          'type': 'hive',
          'layout': {
            'x': 154,
            'y': 136
          },
          nodeAnchors: [
            {x: 0,y: 20, arrow: 'left'},
            {x: 0,y: 45,arrow: 'left'},
            {x: 150,y: 20, arrow: 'right'},
            {x: 150,y: 45,arrow: 'right'}
          ],
          'render': Vue.extend({
            data() {
              return {
                date: '2019-11-01 15:30:00',
                num: 1000,
                title: 'hive',
              }
            },
            render(h) {
              return h('div', {
                'class': 'node-html-content',
              },
              [
                h('div', {
                  'class': 'title',
                }, this.title),
                h('p', {
                }, this.date),
                h('p', {
                }, [
                  '数量：',
                  h('span', {
                    class: 'num'
                  }, this.num)
                ])
              ])
            }
          })
        }, {
          'key': '155782031442825',
          'title': 'spark-sql',
          'desc': '',
          'type': 'spark-sql',
          'layout': {
            'x': 154,
            'y': 264
          }
        }, {
          'key': '155782031442826',
          'title': 'spark',
          'desc': '',
          'type': 'spark-sql',
          'layout': {
            'width': 100,
            'height': 45,
            'x': 354,
            'y': 241
          },
          'render': Vue.extend({
            data() {
              return {
                title: '一个跟斗十万八千里',
                date: '2019-11-01 15:30:00',
                num: 1000
              }
            },
            render(h) {
              return h('div', {
                'class': 'node-html-content user-size',
              },
              [
                h('div', {
                  'class': 'title',
                }, this.title),
                h('p', {
                }, this.date),
                h('p', {
                }, [
                  '数量：',
                  h('span', {
                    class: 'num'
                  }, this.num)
                ])
              ])
            }
          })
        }]
      },
      viewOptions: {
        showBaseInfoOnAdd: false, // 不显示默认的拖拽添加节点弹出的基础信息面板
        shapeView: true, // 左侧shape列表
        control: false,
        canAddLink: true
      }
    }
  },
  mounted() {
    // do something
  },
  methods: {
    change(rst) {
      console.log(rst)
    },
    click(node) {
      console.log(node)
    },
    dblclick(node) {
      console.log(node)
    },
    message(obj) {
      console.log(obj)
    }
  }
}

</script>
<style lang="scss">
.node-html-content {
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 0 8px #7dd38094;
  .title {
    font-size: 16px;
    font-weight: 500;
    text-align: center;
    padding: 5px;
    background: #68bdae;
    color: #fff
  }
  p {
    font-size: 13px;
    padding: 4px;
    background: #9ecc9e5e;
  }
  p:not(:last-child) {
    border-bottom: 1px solid #ccc;
  }
  .num {
    color: #2ba2e8
  }
}
.node-html-content.user-size {
  width: 180px;
  height: 60px;
  overflow: auto
}
</style>
