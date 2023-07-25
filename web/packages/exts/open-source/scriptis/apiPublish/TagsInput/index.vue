<template>
  <div class="input tags-wrap">
    <div class="tags" transition="tags" v-bind:style="{backgroundColor: bgc[item.bgc_no]}" :key="index" v-for="(item, index) in dis_source">
      <span class="content">{{item.text}}</span>
      <span class="del" @click="del(index, false)">&times;</span>
    </div>
    <input class="tags-input ivu-input ivu-input-default" type="text" :placeholder="placeholder" v-model="text" @keyup.enter="add(text)" @keydown.delete="del(source.length - 1, true)">
  </div>
</template>

<script>
import i18n from '@dataspherestudio/shared/common/i18n';
export default {
  props: {
    source: {
      type: Array
    },
    placeholder: {
      type: String,
      default: i18n.t('message.ext.opensource.entercreate')
    }
  },
  watch: {
    'source'(val) {
      if (0 == val.length) {
        this.dis_source = []
      }
    }
  },
  data() {
    var dis_source = []
    this.source.forEach(function (item) {
      var obj = {
        text: item,
        bgc_no: Math.ceil(Math.random() * 10) - 1
      }
      dis_source.push(obj)
    })
    return {
      text: '',
      bgc: ['#e961b4', '#ed664b', '#7b6ac7', '#56abd1', '#f7af4c', '#fe5467', '#52c7bd', '#a479b7', '#cb81ce', '#5eabc5'],
      dis_source: dis_source
    }
  },
  methods: {
    add(text) {
      if(text != ''){
        if (this.judgeRepeat(text)) {
          this.text = ''
          return;
        }
        var count = this.source.length
        this.$set(this.source, count, text)
        this.$set(this.dis_source, count, {
          text: text,
          bgc_no: this.dis_source.length % this.bgc.length
        })
        this.text = ''
      }
    },
    del(index, way){
      if(way){
        if(index >=0 && this.text == ''){
          this.source.splice(index, 1)
          this.dis_source.splice(index, 1)
        }
      }else {
        this.source.splice(index, 1)
        this.dis_source.splice(index, 1)
      }
    },
    judgeRepeat(text) {
      var repeatFlag = false;
      this.dis_source.forEach((item) => {
        if (item.text === text) {
          repeatFlag = true;
          return true;
        }
      })
      return repeatFlag;
    }
  }
}
</script>

<style lang="scss" src="./index.scss" />
