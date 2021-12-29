<template lang="html">
  <canvas ref="designerGrid" :height="height" :width="width" class="gird-view" />
</template>
<script>
import { mixin } from './store';
export default {
  mixins: [mixin],
  props: {
    height: {
      type: Number,
      required: true
    },
    width: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      borderWidth: 0
    };
  },
  mounted() {
    setTimeout(() => {
      this.initGrid();
    }, 0);

    this.$watch('state.baseOptions.pageSize', function () {
      this.initGrid();
    });
  },
  methods: {
    initGrid() {
      if (!this.grid) {
        this.grid = this.$refs.designerGrid;
      }
      if (!this.gridContent) {
        this.gridContent = this.grid.getContext('2d');
      }
      if (this.gridContent) {
        this.gridContent.clearRect(0, 0, this.width, this.height);
        this.drawBackgroud(this.gridContent);
        if (this.state.gridOptions.show) {
          this.drawGrid(
            this.gridContent,
            20 * this.state.baseOptions.pageSize,
            20 * this.state.baseOptions.pageSize,
            'rgba(227, 232, 238, 0.6)',
            0.5
          );
            
          this.drawGrid(
            this.gridContent,
            100 * this.state.baseOptions.pageSize,
            100 * this.state.baseOptions.pageSize,
            'rgba(227, 232, 238, 1)',
            0.5
          );
        }
      }
    },
    drawBackgroud(context) {
      let borderWidth = this.borderWidth * this.state.baseOptions.pageSize;
      context.fillStyle = '#ffffff';
      context.fillRect(
        borderWidth,
        borderWidth,
        this.grid.width - borderWidth * 2,
        this.grid.height - borderWidth * 2
      );
    },
    drawGrid(context, stepx, stepy, color, lineWidth) {
      let borderWidth = this.borderWidth * this.state.baseOptions.pageSize;
      context.save();
      context.strokeStyle = color;
      // 设置线宽为0.5
      context.lineWidth = lineWidth;
      // 绘制x轴网格
      // 注意：canvas在两个像素的边界处画线
      // 由于定位机制，1px的线会变成2px
      // 于是要+0.5
      for (
        let i = stepx + borderWidth + 0.5;
        i < context.canvas.width - borderWidth;
        i = i + stepx
      ) {
        // 开启路径
        context.beginPath();
        context.moveTo(i, 0 + borderWidth);
        context.lineTo(i, context.canvas.height - borderWidth);
        context.stroke();
      }
      // 绘制y轴网格
      for (
        let i = borderWidth + stepy + 0.5;
        i < context.canvas.height - borderWidth;
        i = i + stepy
      ) {
        context.beginPath();
        context.moveTo(borderWidth, i);
        context.lineTo(context.canvas.width - borderWidth, i);
        context.stroke();
      }
      context.restore();
    }
  }
};
</script>
