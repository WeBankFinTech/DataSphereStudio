<template>
  <div style="position:relative;height:100%">
    <div
      class="table-div"
      :style="{height:`${tableHeightCalc}px`}"
    >
      <div
        id="topDiv"
        class="table-top"
        :style="{right:`${(calcScrollBarWidth)}px`}"
        @contextmenu.prevent.stop="headerContextMenu"
        @dblclick="headDblclick"
      >
        <table
          id="topTable"
          cellpadding="0"
          cellspacing="0"
          border="0"
          style="width:100%;table-layout:fixed;"
          @mousemove.prevent.stop="mousemove"
          @mouseup.prevent.stop="mouseup"
        >
          <tr>
            <td
              v-for="(top,index) in columnCalc"
              class="top-td"
              :key="index"
              :data-col-index="index"
              :style="{width: top.width? `${top.width}px` : 'auto'}"
            >
              <span  class="text" :data-col-index="index" :title="top.colHeadHoverTitle">{{top.title?top.title:""}}</span>
              <span
                :class="'sort-icon'"
                v-if="top.sortable"
              >
                <i class="ivu-icon ivu-icon-md-arrow-dropup"
                  :class="{'on': sortType.index === index && sortType.type === 'asc'}" @click="handleSort(index, 'asc')"></i>
                <i class="ivu-icon ivu-icon-md-arrow-dropdown"
                  :class="{'on': sortType.index === index && sortType.type === 'desc'}" @click="handleSort(index, 'desc')"></i>
              </span>
              <div
                v-if="adjust"
                class="resize-bar"
                :data-col-index="index"
                @mousedown="mousedown"
              >
              </div>
            </td>
          </tr>
        </table>
      </div>
      <div
        id="bottomDiv"
        class="table-bottom"
        @scroll.stop="handleScroll"
        @contextmenu.prevent.stop="handleContextMenu"
      >
        <div :style="{height:`${dataTop}px`}"></div>
        <table
          id="bottomTable"
          v-if="showTableList.length"
          cellpadding="0"
          cellspacing="0"
          border="0"
          style="width:100%;table-layout:fixed;"
          :style="{height:`${loadedNum*tdHeight}px`}"
        >
          <tr
            v-for="(items,indexs) in showTableList"
            @click="rowClick(items,indexs+dataTop/tdHeight)"
            @dblclick="rowDblclick(items,indexs+dataTop/tdHeight)"
            :key="indexs"
            :class="selectIndex==indexs?'trselect':'trhover'"
            :style="{height:`${tdHeight}px`}"
          >
            <td
              class="bottom-td"
              v-if="columnCalc[0].type=='index'"
              :style="{width: columnCalc[0].width? `${columnCalc[0].width}px` : 'auto', height:`${tdHeight}px`}"
            >
              {{indexs+dataTop/tdHeight+1}}</td>
            <td
              class="bottom-td"
              v-if="columnCalc[0].type=='select'"
            ></td>
            <template v-for="(item,index) in columnsBottom">
              <td
                v-if="item.key"
                class="bottom-td"
                :class="{'null-text': items[item.key] === 'NULL'}"
                :key="index"
                :style="{width: item.width?`${item.width}px`:'auto', height:`${tdHeight}px`}"
                :title="item.logic==undefined?items[item.key]:item.logic(items)"
              >
                {{item.logic==undefined?items[item.key]:item.logic(items)}}
              </td>
              <td
                v-if="item.slot"
                class="bottom-td"
                :key="index"
                :style="{width: item.width?`${item.width}px`:'auto', height:`${tdHeight}px`}"
              >
                <slot :name="item.slot" :row="items" :index="indexs+dataTop/tdHeight+1"></slot>
              </td>
            </template>
          </tr>
        </table>
        <div v-show="showTableList.length < 1" class="no-data-tip" :style="noDataStyle">
          {{ $t('message.common.No') }}
        </div>
        <div :style="{height:`${tableOtherBottom}px`}"></div>
      </div>
    </div>
    <div
      class="table-bottom-load"
      v-show="showLoad"
      :style="{right:`${(calcScrollBarWidth)}px`,top:'40px',height:`${tableHeightCalc-40}px`}"
    >
      <svg
        class="icon loading "
        aria-hidden="true"
      >
        <use xlink:href="#icon-jiazai"></use>
      </svg>
      <div class="msg">加载中,请稍候</div>
    </div>
    <div v-if="dragStartX" class="drag-line" :style="{left: dragLine.left, display: dragLine.show}" />
  </div>
</template>

<script>
export default {
  name: "WbTable",
  props: {
    loadNum: {
      //默认加载行数
      type: [Number, String],
      default() {
        return 50;
      }
    },
    tdHeight: {
      //表格行高
      type: [Number, String],
      default() {
        return 40;
      }
    },
    tableHeight: {
      //表格高度
      type: [Number, String],
    },
    tableList: {
      //所有表格数据
      type: Array,
      default() {
        return [];
      }
    },
    columns: {
      //所有表格匹配规则
      type: Array,
      default() {
        return [];
      }
    },
    showHeader: {
      type: Boolean,
      default: true
    },
    adjust: {
      type: Boolean,
      default: false
    },
    highlightRow: {},
    outerSort: {}, // 若外部排序则点击表头排序图标只触发事件
    scrollBarWidth: {
      type: Number,
      default(){
        return 16
      }
    }
  },
  data() {
    return {
      showHeaderMenu: false,
      showLoad: false,
      showTableList: [], //实际显示的表格数据
      loadedNum: 0, //实际渲染的数据数量
      dataTotal: 0, //总数据条数
      dataTop: 0, //渲染数据顶部的高度
      scrollTop: 0, //滚动上下的距离
      scrollHeight: 0, //数据滚动的高度
      handleScroll: null,
      selectIndex: -1, //选择的行
      sortType: {},
      noDataStyle: {
        width: '100%'
      },
      calcScrollBarWidth: this.scrollBarWidth,
      dragStartX: undefined,
      dragEndX: undefined,
      dragLine: {
        show: 'none',
        diff: 0,
        left: 0
      },
      adjustCol: []
    };
  },
  computed: {
    tableOtherBottom() {
      //表格剩余数据底部高度
      return (
        this.dataTotal * this.tdHeight -
        this.dataTop -
        this.loadedNum * this.tdHeight
      );
    },
    columnsBottom() {
      if (this.columnCalc[0].type != undefined) {
        return this.columnCalc.slice(1, this.columnCalc.length);
      } else {
        return this.columnCalc;
      }
    },
    // 计算表格实际数据低于渲染条数时的高度
    tableHeightCalc() {
      let dataHeightt = (this.tdHeight + 1) * this.loadedNum + 43;// 43为表头和预留高度, +1 为border
      if(this.tableHeight) return this.tableHeight;
      if(this.tableList.length <= 0) return 100;
      return dataHeightt
    },
    columnCalc() {
      return this.columns.map((item, index) => {
        return this.adjustCol[index] ? {
          ...item,
          width: this.adjustCol[index]
        } : {
          ...item
        }
      })
    }
  },
  methods: {
    /**
     * @typedef {Object} Options -配置项
     * @property {Boolean} leading -开始是否需要额外触发一次
     * @property {this} context -上下文
     **/
    //使用Proxy实现函数防抖
    proxy(
      func,
      time,
      options = {
        leading: true,
        context: null
      }
    ) {
      let timer;
      let _this = this;
      let handler = {
        apply(target, _, args) {
          //代理函数调用
          let bottomScroll = _this.$el.querySelector("#bottomDiv");
          let topScroll = _this.$el.querySelector("#topDiv");
          if (bottomScroll.scrollTop == _this.scrollTop) {
            //左右滚动
            _this.handleScrollLeft(topScroll, bottomScroll);
            return;
          }
          // 和闭包实现核心逻辑相同
          if (!options.leading) {
            if (timer) return;
            timer = setTimeout(() => {
              timer = null;
              Reflect.apply(func, options.context, args);
            }, time);
          } else {
            if (timer) {
              _this.needLoad(bottomScroll);
              clearTimeout(timer);
            }
            timer = setTimeout(() => {
              Reflect.apply(func, options.context, args);
            }, time);
          }
        }
      };
      return new Proxy(func, handler);
    },
    //是否显示加载中
    needLoad(bottomScroll) {
      if (
        Math.abs(bottomScroll.scrollTop - this.scrollTop) >
        this.tdHeight * this.loadNum
      ) {
        this.showLoad = true; //显示加载中
        this.scrollTop = bottomScroll.scrollTop;
      }
    },
    //滚动处理
    scrollProcessing() {
      const bottomScroll = this.$el.querySelector("#bottomDiv");
      const direction = bottomScroll.scrollTop >= this.scrollTop; //滚动方向
      //记录上一次向下滚动的位置
      this.scrollTop = bottomScroll.scrollTop;
      direction ? this.handleScrollBottom() : this.handleScrollTop();
      this.showLoad = false;
    },
    //滚动条向上滚动
    handleScrollTop() {
      if (this.dataTop < this.scrollTop) {
        //如果最后滚动位置在数据上方应该调用向下滚动
        this.handleScrollBottom();
        return;
      }
      //如果加载的数据小于默认加载的数据量
      if (this.dataTotal > this.loadNum) {
        const computeHeight = this.dataTop; //数据需要处理的时候的高度
        const maxHeigth = computeHeight - this.loadNum * this.tdHeight; //不需要清除所有数据的高度
        if (this.scrollTop < computeHeight && this.scrollTop >= maxHeigth) {
          //如果数据总数大于已经渲染的数据
          const dataTopNum = parseInt(this.dataTop / this.tdHeight); //数据顶部条数
          dataTopNum - this.loadNum >= 0
            ? this.dataProcessing(
              this.loadNum,
              this.loadedNum - this.loadNum,
              "top"
            )
            : this.dataProcessing(dataTopNum, dataTopNum, "top");
        } else if (this.scrollTop < maxHeigth) {
          const scrollNum = parseInt(this.scrollTop / this.tdHeight); //滚动的位置在第几条数据
          scrollNum - this.loadNum >= 0
            ? this.dataProcessing(this.loadNum * 2, scrollNum, "topAll")
            : this.dataProcessing(
              scrollNum + this.loadNum,
              scrollNum,
              "topAll"
            );
        }
      }
    },
    //滚动条向下滚动
    handleScrollBottom() {
      if (this.dataTop > this.scrollTop) {
        this.handleScrollTop();
        return;
      }
      const computeHeight =
        this.dataTop + this.loadedNum * this.tdHeight - this.tableHeightCalc; //数据需要处理的时候的高度
      const maxHeight = computeHeight + this.tdHeight * this.loadNum; //不需要清除所有数据的高度
      if (this.scrollTop > computeHeight && this.scrollTop <= maxHeight) {
        //如果滚动高度到达数据显示底部高度
        if (this.dataTotal > this.loadedNum) {
          const dataTopNum = parseInt(this.dataTop / this.tdHeight); //数据顶部条数
          const total = dataTopNum + this.loadedNum + this.loadNum;
          const otherTotal = this.dataTotal - (dataTopNum + this.loadedNum);
          total <= this.dataTotal
            ? this.dataProcessing(
              this.loadedNum - this.loadNum,
              this.loadNum,
              "bottom"
            )
            : this.dataProcessing(otherTotal, otherTotal, "bottom");
        }
      } else if (this.scrollTop > maxHeight) {
        let scrollNum = parseInt(this.scrollTop / this.tdHeight); //滚动的位置在第几条数据
        scrollNum + this.loadNum <= this.dataTotal
          ? this.dataProcessing(scrollNum, this.loadNum * 2, "bottomAll")
          : this.dataProcessing(
            scrollNum,
            this.dataTotal - scrollNum + this.loadNum,
            "bottomAll"
          );
      }
    },
    //滚动条左右滚动
    handleScrollLeft(topScroll, bottomScroll) {
      //顶部表头跟随底部滚动
      topScroll.scrollTo(bottomScroll.scrollLeft, topScroll.pageYOffset);
    },
    //上下滚动时数据处理
    dataProcessing(topNum, bottomNum, type) {
      const topPosition = parseInt(this.dataTop / this.tdHeight);
      if (type === "top") {
        this.showTableList.splice(this.loadedNum - bottomNum, bottomNum); //减去底部数据
        for (let i = 1; i <= topNum; i++) {
          //加上顶部数据
          const indexNum = topPosition - i;
          this.showTableList.unshift(this.tableList[indexNum]);
        }
        this.loadedNum = this.loadedNum + topNum - bottomNum; //重新计算实际渲染数据条数
        this.dataTop = this.dataTop - topNum * this.tdHeight; //重新计算渲染数据的高度
        const h = this.$el.querySelector("#bottomDiv").scrollTop +
            bottomNum * this.tdHeight;
        this.$el.querySelector("#bottomDiv").scrollTop = h
        this.scrollTop = h

      } else if (type == "bottom") {
        this.showTableList.splice(0, topNum); //减去顶部数据
        for (let i = 0; i < bottomNum; i++) {
          //加上底部数据
          const indexNum = topPosition + this.loadedNum + i;
          this.showTableList.push(this.tableList[indexNum]);
        }
        this.loadedNum = this.loadedNum - topNum + bottomNum; //重新计算实际渲染数据条数
        this.dataTop = this.dataTop + topNum * this.tdHeight; //重新计算渲染数据的高度
        const h = this.$el.querySelector("#bottomDiv").scrollTop -
            topNum * this.tdHeight;
        this.$el.querySelector("#bottomDiv").scrollTop = h
        this.scrollTop = h;
      } else if (type == "bottomAll") {
        this.showTableList = []; //减去顶部数据
        let scrollNum = topNum;
        for (let i = 0; i < bottomNum; i++) {
          //加上底部数据
          let indexNum = scrollNum - this.loadNum + i;
          this.showTableList.push(this.tableList[indexNum]);
        }
        this.loadedNum = bottomNum; //重新计算实际渲染数据条数
        this.dataTop = (scrollNum - this.loadNum) * this.tdHeight; //重新计算渲染数据的高度
        this.scrollTop = this.$el.querySelector("#bottomDiv").scrollTop;
      } else if (type == "topAll") {
        this.showTableList = []; //减去顶部数据
        let scrollNum = bottomNum;
        for (let i = 0; i < topNum; i++) {
          //加上底部数据
          let indexNum = scrollNum - topNum + this.loadNum + i;
          this.showTableList.push(this.tableList[indexNum]);
        }
        this.loadedNum = topNum; //重新计算实际渲染数据条数
        this.dataTop = (scrollNum - topNum + this.loadNum) * this.tdHeight; //重新计算渲染数据的高度
        this.scrollTop = this.$el.querySelector("#bottomDiv").scrollTop;
      }
      this.showLoad = false;
    },
    rowClick(item, index) {
      if (this.highlightRow !== undefined) {
        this.selectIndex = index;
      }
      this.$emit("on-current-change", item, index);
    },
    rowDblclick(index, item) {
      this.$emit("on-row-dblclick", item, index);
    },
    headDblclick(e) {
      if (e && e.target && e.target.dataset.colIndex) {
        let index = e.target.dataset.colIndex
        this.$emit("on-head-dblclick", this.columns[index], index);
        const selection = window.getSelection();
        selection.selectAllChildren(e.target);
      } else {
        window.getSelection().removeAllRanges();
      }
    },
    handleContextMenu(e) {
      if (e && e.target && e.target.tagName == "TD") {
        this.$emit("on-tdcontext-munu", {
          content: e.target.title,
          e
        });
      }
    },
    headerContextMenu() {
      if (this.adjustCol.length) {
        this.$Modal.confirm({
          title: this.$t("message.common.dss.Prompt"),
          content: this.$t("message.common.dss.adjustcolwidth"),
          mask: false,
          onOk: () => {
            this.adjustCol = []
          }
        });
      }
    },
    //排序
    handleSort(index, type) {
      let column = this.columns[index];
      let key = column.key;
      if (this.sortType.type === type && this.sortType.index === index) {
        type = 'normal';
      }
      this.sortType = {
        index,
        type
      };
      if (this.outerSort !== undefined) {
        this.$emit("on-sort-change", {
          column,
          key,
          order: type
        });
        return
      }

      this.tableList.sort((a, b) => {
        if (column.sortMethod) {
          return column.sortMethod(a[key], b[key], type);
        } else {
          if (type === "asc") {
            return a[key] > b[key] ? 1 : -1;
          } else if (type === "desc") {
            return a[key] < b[key] ? 1 : -1;
          }
        }
      });
    },
    initTable() {
      this.$el.querySelector("#bottomDiv") &&
        (this.$el.querySelector("#bottomDiv").scrollTop = 0);
      // 后面如果要做切换tab保存滚动条状态，得记录滚动距离再赋值
      // this.$el.querySelector("#bottomDiv") &&
      //   (this.$el.querySelector("#bottomDiv").scrollLeft = 0);
      this.loadedNum = 0; //实际渲染的数据数量
      this.dataTotal = 0; //总数据条数
      this.dataTop = 0; //渲染数据顶部的高度
      this.scrollTop = 0; //滚动上下的距离
      this.showTableList = [];
      if (this.tableList.length > 0) {
        this.dataTotal = this.tableList.length; //获取数据长度
        if (this.dataTotal >= this.loadNum) {
          //判断数据长度是否大于设置的每次渲染长度
          this.loadedNum = this.loadNum; //设置实际渲染条数
          for (var i = 0; i < this.loadNum; i++) {
            let data = this.tableList[i];
            this.showTableList.push(data);
          }
        } else if (this.dataTotal < this.loadNum) {
          this.loadedNum = this.dataTotal;
          for (let i = 0; i < this.dataTotal; i++) {
            let data = this.tableList[i];
            this.showTableList.push(data);
          }
        }
      }
      if(this.showTableList.length < 1) {
        this.$nextTick(()=>{
          this.getNoDataStyle()
        })
      }
      setTimeout(()=>{
        this.calcScrollBarWidth = this.scrollBarWidthCalc()
      }, 200)
    },
    getNoDataStyle() {
      let topTable = this.$el.querySelector("#topTable");
      if (topTable) {
        this.noDataStyle.width =  topTable.clientWidth + 'px'
      }
    },
    // 判断是否出现竖直滚动条
    scrollBarWidthCalc() {
      let bottomDiv = this.$el.querySelector("#bottomDiv");
      return  bottomDiv && bottomDiv.scrollHeight > bottomDiv.clientHeight ? this.scrollBarWidth : 0
    },
    mousedown(e) {
      if (e && e.target && e.target.dataset.colIndex) {
        this.dragColIndex = e.target.dataset.colIndex - 0
        this.dragStartX = e.clientX
        this.tableLeft = this.$el.getBoundingClientRect().x
      }
    },
    mousemove(e) {
      if (!this.adjust) return
      if (this.dragStartX !== undefined) {
        this.dragEndX = e.clientX
        this.dragLine = {
          left: this.dragEndX - this.tableLeft + 'px',
          diff: this.dragEndX - this.dragStartX,
          show: 'block'
        }
      }
    },
    mouseup(e) {
      if (!this.adjust) return
      if (this.dragStartX !== undefined) {
        this.dragEndX = e.clientX
        this.dragLine = {
          left: this.dragEndX - this.tableLeft + 'px',
          diff: this.dragEndX - this.dragStartX,
          show: 'none'
        }
        this.dragStartX = undefined
        this.adjustColWidth()
      }

    },
    adjustColWidth() {
      const adjustCol = []
      this.$el.querySelectorAll('#topTable td').forEach(item => adjustCol.push(item.clientWidth))
      adjustCol.forEach((item, index) => {
        if (index === this.dragColIndex) {
          adjustCol[index] = adjustCol[index] + this.dragLine.diff
        } else {
          adjustCol[index] = adjustCol[index] + this.dragLine.diff / (adjustCol.length - 1) * -1
        }
      })
      this.adjustCol = adjustCol
    }
  },
  created() {
    this.handleScroll = this.proxy(this.scrollProcessing, 240, {
      leading: true,
      context: this
    });
  },
  mounted() {
    this.initTable();
  },
  watch: {
    tableList: {
      handler(newValue, oldValue) {
        this.initTable();
        if (oldValue) {
          this.scrollProcessing();
        }
      }
    },
    tableHeight(newValue) {
      if (newValue) {
        this.scrollProcessing(); //表格高度改变重新计算
      }
    }
  }
};
</script>
<style lang="scss" src="./table.scss" scoped>
</style>
