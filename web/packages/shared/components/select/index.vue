<template>
  <Select v-bind="$attrs" v-on="$listeners" class="luban-select">
    <slot></slot>
  </Select>
</template>

<script>

export default {
  props: {
    disabledTags: {
      type: Array,
      default: () => []
    }
  },

  created(){
    const dealStyle = (tags)=> {
      tags.forEach((el) => {
        if(this.disabledTags.includes(el.querySelector('.ivu-tag-text').innerText)){
          console.log(el.querySelector('.ivu-icon-ios-close').classList)
          el.querySelector('.ivu-icon-ios-close').classList.add('none')
        }
      })
    }
    this.$nextTick(()=>{
      dealStyle(this.$el.querySelectorAll('.ivu-tag-checked'));
    })
    
  }
  
}
</script>

<style lang="scss" src="./index.scss">
</style>


