<template>
  <div class="serviceItem">
    <h2 class="items">用户服务条款</h2>
    <p class="title">为了向您提供更快更好的服务，请仔细阅读下列条款：</p>
    <p class="title">一、 服务条款的接受</p>
    <p> 1.1、本服务条款所称的用户是指完全同意所有条款并完成注册程序而使用”DataSphere Studio”提供的服务（以下简称“本服务”）的用户。</p>
    <p class="title">二、服务条款的变更和修改</p>
    <p> 2.1、用户行为信息将作为用户群体的整体概况数据存在和使用，不会存在行为信息与特定用户之间的关联。</p>
    <p class="title">三、用户应遵守以下法律及法规：</p>
    <p>3.1、用户同意遵守《中华人民共和国保守国家秘密法》、《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》等有关计算机及互联网规定的法律和法规、实施办法。在任何情况下，”DataSphere Studio”合理地认为用户的行为可能违反上述法律、法规，”DataSphere Studio”可以在任何时候，不经事先通知终止向该用户提供服务。</p>
    <p>3.2、用户应了解国际互联网的无国界性，应特别注意遵守当地所有有关的法律和法规。</p>
    <p class="title">四、本网站提供的所有服务项目，会员单位在未经本网站许可不得提供给任何第三方使用。</p>
    <p>4.1、我们对本隐私条款保留更改的权利，请关注并了解本隐私条款的更新内容。</p>
    <p class="title">五、本网站不保证所提供的个人登录信息及本网自行采集的信息完全准确，注册会员使用时需自行核实，对未经核实而使用本网站信息而造成的任何损失或与第三方产生的任何纠纷，本网站不承担任何责任。</p>
    <p class="title">六、用户隐私权制度</p>
    <p>6.1、当用户注册”DataSphere Studio”的服务时，用户须提供个人信息。”DataSphere Studio”收集个人信息的目的是为用户提供尽可能多的个人化网上服务。</p>
    <p class="title">七、法律的适用和管辖</p>
    <p> 7.1、本服务条款的生效、履行、解释及争议的解决均适用中华人民共和国法律，发生的争议提交北京仲裁委员会，其仲裁裁决是终局的。本服务条款因与中华人民共和国现行法律相抵触而导致部分无效，不影响其他部分的效力。</p>
    <p class="title">八、关于使用信息记录</p>
    <p>8.1、本服务为沙箱环境，数据不会进行保留，如果有重要数据请不要放到该网站，网站对数据丢失不负任何责任；</p>
    <p>8.2、本服务为沙箱环境，仅提供给用户进行试用为目的，不能做正式环境进行使用；</p>
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
.serviceItem {
    padding: 0 40px;
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
        margin-top: 40px;
        margin-left: 600px;
        button {
            width: 100px;
        }
    }
}
</style>
