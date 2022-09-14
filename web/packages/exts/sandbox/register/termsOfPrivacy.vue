<template>
  <div class="pravicy">
    <h2 class="items">用户隐私条款</h2>
    <p class="title">对于用户信息的收集、使用和保护措施，请务必仔细阅读:</p>
    <p class="title">一、用户个人信息</p>
    <p>1.1、当您在使用该平台时，大部分应用部不需要您提供任何个人信息。</p>
    <p>1.2、注册使用收集的信息，目的是为您提供尽可能多的个性化服务</p>
    <p class="title">二、用户行为信息</p>
    <p> 2.1、用户行为信息将作为用户群体的整体概况数据存在和使用，不会存在行为信息与特定用户之间的关联。</p>
    <p class="title">三、用户设备信息</p>
    <p>3.1、本网站不会收集用户设备信息</p>
    <p class="title">四、隐私条款的变更</p>
    <p>4.1、我们对本隐私条款保留更改的权利，请关注并了解本隐私条款的更新内容。</p>
    <div class="agree"><Button type="primary" @click="go">同意并继续</Button></div>
  </div>
</template>
<script>
export default {
  methods: {
    go(){
      this.$emit("go_register");
    }
  },
}
</script>
<style lang="scss" scoped>
.pravicy {
    padding: 0 100px;
    .items {
        margin-top: 50px;
        margin-bottom: 50px;
        text-align: center;
        font-weight: normal;
        line-height: 1;
        color: #2d8cf0;
    }
    p {
        margin-bottom: 8px;
        font-size: 12px;
    }
    .title {
        font-size: 14px;
    }
    .agree {
        margin-top: 300px;
        margin-left: 500px;
        button {
            width: 100px;
        }
    }
}
</style>
