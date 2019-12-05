<template lang="html">
  <div class="linker-box" />
</template>
<script>
import { mixin } from './store';
import { findComponentUpward } from './util.js'
export default {
  mixins: [mixin],
  props: {
    k: {
      type: [String, Number],
      required: true
    },
    beginNode: {
      type: Object,
      required: true
    },
    beginNodeArrow: {
      type: String,
      required: true
    },
    endNode: {
      type: Object,
      required: true
    },
    endNodeArrow: {
      type: String,
      required: true
    },
    lineWidth: {
      type: [Number],
      required: true
    },
    borderWidth: {
      type: [Number],
      required: true
    },
    extWidth: {
      type: [Number],
      required: true
    },
    borderColor: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      currentBorderColor: this.borderColor
    };
  },
  computed: {
    beginX() {
      let x = 0;
      if (this.beginNodeArrow == 'top' || this.beginNodeArrow == 'bottom') {
        x = this.beginNode.x + this.beginNode.width / 2;
      }
      if (this.beginNodeArrow == 'left') {
        x = this.beginNode.x;
      }
      if (this.beginNodeArrow == 'right') {
        x = this.beginNode.x + this.beginNode.width;
      }
      return x * this.state.baseOptions.pageSize;
    },
    beginY() {
      let y = 0;
      if (this.beginNodeArrow == 'top') {
        y = this.beginNode.y;
      }
      if (this.beginNodeArrow == 'bottom') {
        y = this.beginNode.y + this.beginNode.height;
      }
      if (this.beginNodeArrow == 'left') {
        y = this.beginNode.y + this.beginNode.height / 2;
      }
      if (this.beginNodeArrow == 'right') {
        y = this.beginNode.y + this.beginNode.height / 2;
      }
      return y * this.state.baseOptions.pageSize;
    },
    endX() {
      let x = 0;
      if (this.endNodeArrow == 'top' || this.endNodeArrow == 'bottom') {
        x = this.endNode.x + this.endNode.width / 2;
      }
      if (this.endNodeArrow == 'left') {
        x = this.endNode.x;
      }
      if (this.endNodeArrow == 'right') {
        x = this.endNode.x + this.endNode.width;
      }
      return x * this.state.baseOptions.pageSize;
    },
    endY() {
      let y = 0;
      if (this.endNodeArrow == 'top') {
        y = this.endNode.y;
      }
      if (this.endNodeArrow == 'bottom') {
        y = this.endNode.y + this.endNode.height;
      }
      if (this.endNodeArrow == 'left') {
        y = this.endNode.y + this.endNode.height / 2;
      }
      if (this.endNodeArrow == 'right') {
        y = this.endNode.y + this.endNode.height / 2;
      }
      return y * this.state.baseOptions.pageSize;
    }
  },
  watch: {
    'beginNode.runState'(value) {
      if (value.borderColor) {
        this.currentBorderColor = value.borderColor;
        // this.emitChange()
      }
    },
    // 有可能只改变了连线的方式，也要重新计算
    'state.linkType'() {
      this.calculate();
    }
  },
  mounted() {
    this.nodes = [];
    this.designer = findComponentUpward(this, 'Designer');
    this.calculate();

    // 当起始节点被拖动时，重新计算绘制连线
    this.$watch('beginX', () => {
      this.calculate();
    });
    this.$watch('beginY', () => {
      this.calculate();
    });
    this.$watch('endX', () => {
      this.calculate();
    });
    this.$watch('endY', () => {
      this.calculate();
    });
  },
  methods: {
    calculate() {
      // this.nodes表示从起点到终点折线（直线）所需要的关键点
      // 如果终点和起点太近的话，连线会出现错乱的bug，此时没必要
      if (Math.abs(this.endY - this.beginY) < 5 && Math.abs(this.endX - this.beginX) < 5) {
        return
      }
      // 每次重新算
      this.nodes.splice(0, this.nodes.length);
      let {beginX, beginY, endX, endY} = this;
      let border = this.extWidth;
      this.nodes.push({
        x: beginX,
        y: beginY
      });
      let _beginY = beginY;
      // let _beginX = beginX;
      if (this.beginNodeArrow == 'right' && this.endNodeArrow == 'left') {
        if (endX >= beginX) {
          beginX = Math.round(beginX + (endX - beginX) / 2);
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        } else {
          if (Math.abs(endY - beginY) > (this.beginNode.height + this.endNode.height) / 2 * this.state.baseOptions.pageSize + 10) {
            // 可以从中间过
            beginX = beginX + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round(beginY + (endY - beginY) / 2);
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginX = Math.max(beginX, endX + this.endNode.width * this.state.baseOptions.pageSize) + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endY > beginY) {
              beginY = endY + this.endNode.height / 2 * this.state.baseOptions.pageSize + border;
            } else {
              beginY = endY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        }
      }
      if (this.beginNodeArrow == 'right' && this.endNodeArrow == 'top') {
        if (endX >= beginX) {
          if (endY >= beginY) {
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            if (Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize) >= beginX) {
              beginX = Math.round((endX - this.endNode.width / 2 * this.state.baseOptions.pageSize + beginX) / 2);
            } else {
              beginX = Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + border);
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (endY > beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize) {
            beginX = beginX + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round((endY + beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize) / 2);
          } else {
            beginX = Math.max(beginX, Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize)) + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.min(endY, Math.round(beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize)) - border;
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      if (this.beginNodeArrow == 'right' && this.endNodeArrow == 'bottom') {
        if (endX >= beginX) {
          if (endY <= beginY) {
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            if (Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize) >= beginX) {
              beginX = Math.round((endX - this.endNode.width / 2 * this.state.baseOptions.pageSize + beginX) / 2);
            } else {
              beginX = Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + border);
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (endY < beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize) {
            beginX = beginX + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round((endY + beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize) / 2);
          } else {
            beginX = Math.max(beginX, Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize)) + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.max(endY, Math.round(beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize)) + border;
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      if (this.beginNodeArrow == 'right' && this.endNodeArrow == 'right') {
        if (
          endY - this.endNode.height / 2 * this.state.baseOptions.pageSize >= beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize ||
                    endY + this.endNode.height / 2 * this.state.baseOptions.pageSize <= beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize
        ) {
          beginX = Math.max(endX, beginX) + border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        } else {
          beginX = beginX + border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          if (endX > beginX) {
            if (endY > beginY) {
              beginY = endY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border;
            } else {
              beginY = endY + this.endNode.height / 2 * this.state.baseOptions.pageSize + border;
            }
          } else {
            if (endY < beginY) {
              beginY = beginY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border;
            } else {
              beginY = beginY + this.endNode.height / 2 * this.state.baseOptions.pageSize + border;
            }
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX + border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      if (this.beginNodeArrow == 'bottom' && this.endNodeArrow == 'left') {
        if (endX >= beginX) {
          if (endY >= beginY) {
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginY = beginY + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endX > beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
              beginX = Math.round((endX + beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) / 2);
            } else {
              beginX = Math.min(endX, Math.round(beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize)) - border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (Math.round(endY - this.endNode.height / 2 * this.state.baseOptions.pageSize) >= beginY) {
            beginY = Math.round((endY - this.endNode.height / 2 * this.state.baseOptions.pageSize + beginY) / 2);
          } else {
            beginY = Math.max(beginY, Math.round(endY + this.endNode.height / 2 * this.state.baseOptions.pageSize)) + border;
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = Math.min(beginX - Math.round(this.beginNode.width / 2 * this.state.baseOptions.pageSize), endX) - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      if (this.beginNodeArrow == 'bottom' && this.endNodeArrow == 'right') {
        if (endX <= beginX) {
          if (endY >= beginY) {
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginY = beginY + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endX < beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
              beginX = Math.round((endX + beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize) / 2);
            } else {
              beginX = Math.max(endX, Math.round(beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize)) + border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (Math.round(endY - this.endNode.height / 2 * this.state.baseOptions.pageSize) >= beginY) {
            beginY = Math.round((endY - this.endNode.height / 2 * this.state.baseOptions.pageSize + beginY) / 2);
          } else {
            beginY = Math.max(beginY, Math.round(endY + this.endNode.height / 2 * this.state.baseOptions.pageSize)) + border;
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = Math.max(beginX + Math.round(this.beginNode.width / 2 * this.state.baseOptions.pageSize), endX) + border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      if (this.beginNodeArrow == 'bottom' && this.endNodeArrow == 'top') {
        // 现在是只有从 父节点bottom 到 子节点top 或者 父节点top 到 子节点bottom时会进行直线和斜线的转换
        // 连线的类型，straight：直线（直角折线），要执行以下逻辑；curve：斜线，不用执行以下逻辑
        if (this.state.linkType === 'straight') {
          if (endY >= beginY) {
            if (beginX !== endX) {
              beginY = Math.round((beginY + endY) / 2);
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = endX;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            }
          } else {
            if (endX + this.endNode.width / 2 * this.state.baseOptions.pageSize < beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
              beginY = beginY + border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = Math.round((endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize) / 2);
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginY = endY - border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = endX;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            } else if (
              endX + this.endNode.width / 2 * this.state.baseOptions.pageSize >= beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize &&
                            endX + this.endNode.width / 2 * this.state.baseOptions.pageSize < beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize
            ) {
              beginY = beginY + border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX =
                                Math.min(Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize), Math.round(beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize)) -
                                border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginY = Math.min(beginY - border - this.beginNode.height * this.state.baseOptions.pageSize, endY) - border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = endX;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            } else if (
              endX - this.endNode.width / 2 * this.state.baseOptions.pageSize > beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize &&
                            endX - this.endNode.width / 2 * this.state.baseOptions.pageSize <= beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize
            ) {
              beginY = beginY + border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX =
                                Math.max(Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize), Math.round(beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize)) +
                                border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginY = Math.min(beginY - border - this.beginNode.height * this.state.baseOptions.pageSize, endY) - border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = endX;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            } else if (endX - this.endNode.width / 2 * this.state.baseOptions.pageSize > beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
              beginY = beginY + border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = Math.round((endX - this.endNode.width / 2 * this.state.baseOptions.pageSize + beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) / 2);
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginY = endY - border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
              beginX = endX;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            }
          }
        }
      }
      if (this.beginNodeArrow == 'bottom' && this.endNodeArrow == 'bottom') {
        if (
          endX + this.endNode.width / 2 * this.state.baseOptions.pageSize <= beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize ||
                    endX - this.endNode.width / 2 * this.state.baseOptions.pageSize >= beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize
        ) {
          beginY = Math.max(beginY, endY) + border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        } else {
          beginY = beginY + border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          if (endY <= beginY) {
            if (endX <= beginX) {
              beginX = beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize - border;
            } else {
              beginX = beginX + this.endNode.width / 2 * this.state.baseOptions.pageSize + border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            if (endX <= beginX) {
              beginX = endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + border;
            } else {
              beginX = endX - this.endNode.width / 2 * this.state.baseOptions.pageSize - border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.max(this.beginY, endY) + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        }
      }
      // ok
      if (this.beginNodeArrow == 'left' && this.endNodeArrow == 'right') {
        if (endX <= beginX) {
          beginX = Math.round(beginX + (endX - beginX) / 2);
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        } else {
          if (Math.abs(endY - beginY) > (this.beginNode.height + this.endNode.height) / 2 * this.state.baseOptions.pageSize + 10) {
            // 可以从中间过
            beginX = beginX - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round(beginY + (endY - beginY) / 2);
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginX = Math.min(beginX, endX - this.endNode.width * this.state.baseOptions.pageSize) - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endY > beginY) {
              beginY = endY + this.endNode.height / 2 * this.state.baseOptions.pageSize + border;
            } else {
              beginY = endY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        }
      }
      // ok
      if (this.beginNodeArrow == 'left' && this.endNodeArrow == 'top') {
        if (endX <= beginX) {
          if (endY >= beginY) {
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            if (Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize) <= beginX) {
              beginX = Math.round((endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + beginX) / 2);
            } else {
              beginX = Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize - border);
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (endY > beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize) {
            beginX = beginX - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round((endY + beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize) / 2);
          } else {
            beginX = Math.min(beginX, Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize)) - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.min(endY, Math.round(beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize)) - border;
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      // ok
      if (this.beginNodeArrow == 'left' && this.endNodeArrow == 'bottom') {
        if (endX <= beginX) {
          if (endY <= beginY) {
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            if (Math.round(endX + this.endNode.width / 2 * this.state.baseOptions.pageSize) <= beginX) {
              beginX = Math.round((endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + beginX) / 2);
            } else {
              beginX = Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize - border);
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (endY < beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize) {
            beginX = beginX - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round((endY + beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize) / 2);
          } else {
            beginX = Math.min(beginX, Math.round(endX - this.endNode.width / 2 * this.state.baseOptions.pageSize)) - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.max(endY, Math.round(beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize)) + border;
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      // ok
      if (this.beginNodeArrow == 'left' && this.endNodeArrow == 'left') {
        if (
          endY - this.endNode.height / 2 * this.state.baseOptions.pageSize >= beginY + this.beginNode.height / 2 * this.state.baseOptions.pageSize ||
                    endY + this.endNode.height / 2 * this.state.baseOptions.pageSize <= beginY - this.beginNode.height / 2 * this.state.baseOptions.pageSize
        ) {
          beginX = Math.min(endX, beginX) - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        } else {
          beginX = beginX - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          if (endX < beginX) {
            if (endY > beginY) {
              beginY = endY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border;
            } else {
              beginY = endY + this.endNode.height / 2 * this.state.baseOptions.pageSize + border;
            }
          } else {
            if (endY < beginY) {
              beginY = beginY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border;
            } else {
              beginY = beginY + this.endNode.height / 2 * this.state.baseOptions.pageSize + border;
            }
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      // ok
      if (this.beginNodeArrow == 'top' && this.endNodeArrow == 'left') {
        if (endX >= beginX) {
          if (endY <= beginY) {
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginY = beginY - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endX > beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
              beginX = Math.round((endX + beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) / 2);
            } else {
              beginX = Math.min(endX, Math.round(beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize)) - border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          if (endY + this.endNode.height / 2 * this.state.baseOptions.pageSize < beginY) {
            beginY = Math.round((beginY + endY + this.endNode.height / 2 * this.state.baseOptions.pageSize ) / 2);
          } else {
            beginY = Math.round(endY - this.endNode.height / 2 * this.state.baseOptions.pageSize - border);
            if (beginY > _beginY) {
              beginY = _beginY - border;
            }
          }
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = Math.min(beginX - Math.round(this.beginNode.width / 2 * this.state.baseOptions.pageSize), endX) - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginY = endY;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        }
      }
      // ok
      if (this.beginNodeArrow == 'top' && this.endNodeArrow == 'right') {
        if (endY < beginY) {
          if (endX <= beginX) {
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginY = Math.round((endY + beginY + this.endNode.height / 2 * this.state.baseOptions.pageSize) / 2);
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        } else {
          beginY = beginY - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          if (endX < beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
            beginX = Math.round((endX + beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize) / 2);
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginX = Math.max((beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize), endX ) + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        }
      }
      // ok
      if (this.beginNodeArrow == 'top' && this.endNodeArrow == 'top') {
        if (endX + this.endNode.width / 2 * this.state.baseOptions.pageSize < beginX ||
                    endX - this.endNode.width / 2 * this.state.baseOptions.pageSize > beginX) {
          beginY = Math.min(endY, beginY) - border;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
          beginX = endX;
          this.nodes.push({
            x: beginX,
            y: beginY
          });
        } else {
          if (endY > beginY + this.beginNode.height * this.state.baseOptions.pageSize) {
            beginY = beginY - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endX < beginX) {
              beginX = beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize - border;
            } else {
              beginX = beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize + border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = Math.round((_beginY + this.beginNode.height * this.state.baseOptions.pageSize + endY) / 2);
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else if (endY - this.endNode.height / 2 * this.state.baseOptions.pageSize < beginY ) {
            beginY = Math.round(( beginY + endY + this.endNode.height * this.state.baseOptions.pageSize) / 2);
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endX < beginX) {
              beginX = endX + this.endNode.width / 2 * this.state.baseOptions.pageSize + border;
            } else {
              beginX = endX - this.beginNode.width / 2 * this.state.baseOptions.pageSize - border;
            }
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginY = endY - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        }
      }
      // ok
      if (this.beginNodeArrow == 'top' && this.endNodeArrow == 'bottom') {
        // 连线的类型，straight：直线（直角折线），要执行以下逻辑；curve：斜线，不用执行以下逻辑
        if (this.state.linkType === 'straight') {
          if (endY <= beginY) {
            beginY = (endY + beginY) / 2;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          } else {
            beginY = beginY - border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            if (endX - this.endNode.width / 2 * this.state.baseOptions.pageSize >= beginX + this.beginNode.width / 2 * this.state.baseOptions.pageSize) {
              beginX = (beginX - this.beginNode.width * this.state.baseOptions.pageSize / 2 + endX + this.endNode.width * this.state.baseOptions.pageSize / 2) / 2;
              this.nodes.push({
                x: beginX,
                y: beginY
              });

            } else if (endX >= beginX) {
              beginX = endX + this.endNode.width * this.state.baseOptions.pageSize / 2 + border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            } else if (endX + this.endNode.width / 2 * this.state.baseOptions.pageSize >= beginX - this.beginNode.width / 2 * this.state.baseOptions.pageSize ) {
              beginX = endX - this.endNode.width * this.state.baseOptions.pageSize / 2 - border;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            } else {
              beginX = (beginX - this.beginNode.width * this.state.baseOptions.pageSize / 2 + endX + this.endNode.width * this.state.baseOptions.pageSize / 2) / 2;
              this.nodes.push({
                x: beginX,
                y: beginY
              });
            }
            beginY = endY + border;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
            beginX = endX;
            this.nodes.push({
              x: beginX,
              y: beginY
            });
          }
        }
      }

      this.nodes.push({
        x: endX,
        y: endY
      });

      this.emitChange()
    },
    emitChange() {
      this.$emit('after-calculate', {
        key: this.k,
        nodes: this.nodes,
        stroke: this.currentBorderColor,
        fill: this.currentBorderColor,
        lineWidth: this.lineWidth
      });
    }
  }
};
</script>
