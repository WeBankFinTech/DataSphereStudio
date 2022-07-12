<template>
  <div class="range-group">
    <div class="range-group-item" :class="{'group-item-checked': option.key == currentRange}" v-for="option in rangeOptions" :key="option.key" @click="handleDateChange(option)">
      <template v-if="option.key != 'picker'">{{option.name}}</template>
      <Date-picker
        v-else
        :open="datePickerOpen"
        :value="datePickerRange"
        type="daterange"
        placement="bottom-end"
        @on-change="handlePickerChange"
      >
        <div class="date-trigger" @click="handlePickerClick">
          <span v-if="datePickerRange.length" class="date-show">{{datePickerRange.join('   -   ')}}</span>
          <SvgIcon icon-class="riqi" />
        </div>
      </Date-picker>
    </div>
  </div>
</template>
<script>
import util from './util';
export default {
  data() {
    return {
      rangeOptions: [
        { key: 'week', name: this.$t("message.dataService.apiMonitor.range_week") },
        { key: 'yesterday', name: this.$t("message.dataService.apiMonitor.range_yesterday") },
        { key: 'today', name: this.$t("message.dataService.apiMonitor.range_today") },
        { key: 'picker', name: 'picker' }
      ],
      currentRange: 'week',
      datePickerOpen: false,
      datePickerRange: []
    }
  },
  computed: {
    range() {
      const now = Date.now();
      if (this.currentRange == 'week') {
        return {
          startTime: util.dateFormat(new Date(now - 7*86400*1000)),
          endTime: util.dateFormat(new Date(), '23:59:59')
        }
      } else if (this.currentRange == 'yesterday') {
        return {
          startTime: util.dateFormat(new Date(now - 86400*1000)),
          endTime: util.dateFormat(new Date(), '23:59:59')
        }
      } else if (this.currentRange == 'today') {
        return {
          startTime: util.dateFormat(),
          endTime: util.dateFormat(new Date(), '23:59:59')
        }
      } else if (this.currentRange == 'picker') {
        return {
          startTime: `${this.datePickerRange[0]} 00:00:00`,
          endTime: `${this.datePickerRange[1]} 23:59:59`
        }
      }
      return {
        startTime: util.dateFormat(),
        endTime: util.dateFormat(new Date(), '23:59:59')
      }
    }
  },
  methods: {
    handleDateChange(option) {
      this.currentRange = option.key;
      if (option.key != 'picker') {
        this.datePickerRange = [];
        this.datePickerOpen = false;
        this.getRangeScreenData();
      }
    },
    handlePickerClick () {
      this.datePickerOpen = !this.datePickerOpen;
    },
    handlePickerChange (date) {
      this.datePickerRange = date;
      this.datePickerOpen = false;
      this.getRangeScreenData();
    },
    getRangeScreenData() {
      this.$emit('on-date-change', this.range)
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.range-group {
  display: flex;
  align-items: center;
  .range-group-item {
    min-width: 60px;
    height: 32px;
    font-size: 14px;
    line-height: 30px;
    transition: all .2s ease-in-out;
    cursor: pointer;
    border: 1px solid #D9D9D9;
    @include border-color(#D9D9D9, $dark-border-color);
    border-left: 0;
    @include bg-color(#fff, $dark-base-color);
    text-align: center;
    @include font-color(#666, $dark-text-color);
    &:first-child {
      border-radius: 4px 0 0 4px;
      border-left: 1px solid #D9D9D9;
      @include border-color(#D9D9D9, $dark-border-color);
    }
    &:last-child {
      border-radius: 0 4px 4px 0;
    }
    &.group-item-checked {
      @include bg-color(#fff, $dark-base-color);
      @include border-color(#1890FF, #1890FF);
      color: #1890FF;
      box-shadow: -1px 0 0 0 #1890FF;
    }
    &.group-item-checked:first-child {
      box-shadow: none!important;
    }
    .date-trigger {
      padding: 0 15px;
      line-height: 32px;
      font-size: 16px;
      .date-show {
        margin-right: 15px;
      }
      i {
        vertical-align: 0;
      }
    }
  }
}
</style>