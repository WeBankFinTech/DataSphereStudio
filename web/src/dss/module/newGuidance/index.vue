<template>
  <div>
    <div v-if="currentStep === 1" key="1">
      <div id="mask1">
        <div class="description">
          <p>你好，我是鲁班的卡通形象，你可以叫我"鲁蛋"。</p>
          <br />
          <p>
            鲁班Luban是<span>天翼云基于开源，合作共建的一站式大数据开发与服务平台</span>，他可以对于数据进行全生命周期的运营管理，帮助企业快速开发基于大数据分析建模能力的业务系统和构建数据运营能力。
          </p>
          <br />
          <p>下面，让我来带你认识一下鲁班的操作界面，请跟着页面引导操作吧。</p>
        </div>
        <div id="confirm1">
          <div class="next" @click="next()">我知道了</div>
          <img
            class="luBanLogo"
            src="~@/dss/assets/images/luBanLogo.png"
            alt=""
          />
        </div>
      </div>
    </div>
    <div v-else-if="currentStep === 2" key="2">
      <div id="mask2">
        <div class="description">这是鲁班的主页面，即工作空间首页。</div>
        <div id="confirm2">
          <div class="skip" @click="skip()">跳过</div>
          <div class="next" @click="next()">下一步</div>
          <img
            class="luBanLogo"
            src="~@/dss/assets/images/luBanLogo.png"
            alt=""
          />
        </div>
      </div>
      <div class="headerSuspension">
        <div class="item1">
          <div class="dashed block1"></div>
          <div class="dashed block2"></div>
          <div class="dashed block3"></div>
          <div class="dashed block4"></div>
          <div class="dashed block5"></div>
        </div>
        <div class="item2">
          <img class="img1" src="~@/dss/assets/images/2-1.svg" alt="" />
          <img class="img2" src="~@/dss/assets/images/2-1.svg" alt="" />
          <img class="img3" src="~@/dss/assets/images/2-1.svg" alt="" />
          <img class="img4" src="~@/dss/assets/images/2-1.svg" alt="" />
          <img class="img5" src="~@/dss/assets/images/2-2.svg" alt="" />
        </div>
        <div class="item3">
          <div class="desc1">
            点击<span>自定义功能菜单</span>，可支持自定义配置全局功能菜单。
          </div>
          <div class="desc2">
            点击<span>天翼云|鲁班</span>，可返回工作空间首页。
          </div>
          <div class="desc3">
            鼠标悬浮显示所有<span>工作空间</span>，点击可切换。
          </div>
          <div class="desc4">点击<span>首页</span>，可返回工作空间首页。</div>
          <div class="desc5">
            点击<span>账户</span>，可切换账号、清理缓存、更换界面皮肤等。
          </div>
        </div>
      </div>
      <div class="footerSuspension">
        <div class="item1">
          <div class="desc1">
            点击<span>帮助中心</span>，可弹出帮助文档，包含当页介绍，操作步骤及常见问题。
          </div>
          <div class="desc2">
            点击<span>资源管理器</span>，可显示任务，引擎及资源使用情况。
          </div>
        </div>
        <div class="item2">
          <img src="~@/dss/assets/images/2-3.svg" class="img1" />
          <img src="~@/dss/assets/images/2-3.svg" class="img2" />
        </div>
        <div class="item3">
          <div class="dashed block1"></div>
          <div class="dashed block2"></div>
        </div>
      </div>
    </div>
    <div v-else-if="currentStep === 3" key="3">
      <div
        class="maskHeader"
        style="
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          height: 54px;
          z-index: 1003;
        "
      ></div>
      <div id="mask3">
        <div class="selected" @click="navMenuClick()"></div>
        <div class="arrow">
          <img src="~@/dss/assets/images/3-1.svg" alt="" />
        </div>
        <div class="desc">这是<span>自定义功能菜单</span>，请试着点击它。</div>
        <div id="confirm3">
          <div class="skip" @click="skip()">跳过</div>
          <div class="prev" @click="prev()">上一步</div>
          <div class="next" @click="next()">下一步</div>
          <img
            class="luBanLogo"
            src="~@/dss/assets/images/luBanLogo.png"
            alt=""
          />
        </div>
      </div>
    </div>
    <div v-else-if="currentStep === 4" key="4">
      <div
        class="maskHeader"
        style="
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          z-index: 1999;
        "
      ></div>
      <div id="mask4">
        <div class="left selected"></div>
        <div class="right">
          <div class="arrow">
            <img src="~@/dss/assets/images/4-1.svg" alt="" />
          </div>
          <div class="desc">
            这是<span>自定义功能菜单</span>，点击全部产品内，具体功能旁★号，可以收藏该功能至菜单。点击自定义菜单功能旁叉号，可以删除该收藏。
          </div>
          <div id="confirm4">
            <div class="skip" @click="skip()">跳过</div>
            <div class="prev" @click="prev()">上一步</div>
            <div class="next" @click="next()">下一步</div>
            <img
              class="luBanLogo"
              src="~@/dss/assets/images/luBanLogo.png"
              alt=""
            />
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="currentStep === 5" key="5">
      <div id="mask5">
        <div class="selected" @click.once="iconRightClick()"></div>
        <div class="arrow">
          <img src="~@/dss/assets/images/5-1.svg" alt="" />
        </div>
        <div class="desc">这是<span>账户设置</span>，请试着点击它。</div>
        <div id="confirm5">
          <div class="skip" @click="skip()">跳过</div>
          <div class="prev" @click="prev()">上一步</div>
          <div class="next" @click="next()">下一步</div>
          <img
            class="luBanLogo"
            src="~@/dss/assets/images/luBanLogo.png"
            alt=""
          />
        </div>
      </div>
    </div>
    <div v-else-if="currentStep === 6" key="6">
      <div
        class="maskHeader"
        style="
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          height: 54px;
          z-index: 1004;
        "
      ></div>
      <div id="mask6">
        <div class="selected"></div>
        <div class="arrow">
          <img src="~@/dss/assets/images/6-1.svg" alt="" />
        </div>
        <div class="desc">
          这是<span>账户设置</span>，可切换账号，设置语言，更换界面皮肤等清理缓存，可以清理当前用户运行的任务缓存结果。
        </div>
        <div id="confirm6">
          <div class="skip" @click="skip()">跳过</div>
          <div class="prev" @click="prev()">上一步</div>
          <div class="next" @click="next()">下一步</div>
          <img
            class="luBanLogo"
            src="~@/dss/assets/images/luBanLogo.png"
            alt=""
          />
        </div>
      </div>
    </div>
    <div v-else-if="currentStep === 7" key="7">
      <div
        class="maskFooter"
        style="
          position: fixed;
          bottom: 0;
          left: 0;
          right: 0;
          height: 165px;
          z-index: 1003;
        "
      ></div>
      <div id="mask7">
        <div class="item1">
          <div class="desc">这是<span>帮助文档</span>，请试着点击他。</div>
          <div id="confirm7">
            <div class="skip" @click="skip()">跳过</div>
            <div class="prev" @click="prev()">上一步</div>
            <div class="next" @click="next()">下一步</div>
            <img
              class="luBanLogo"
              src="~@/dss/assets/images/luBanLogo.png"
              alt=""
            />
          </div>
        </div>
        <div class="arrow">
          <img src="~@/dss/assets/images/7-1.svg" alt="" />
        </div>
        <div class="selected" @click="maskFooterClick()"></div>
      </div>
    </div>
    <div v-else-if="currentStep === 8" key="8">
      <div id="mask8">
        <div class="item1">
          <div class="desc proc">
            这是<span>帮助文档</span>，点击步骤可弹出对应操作图示。点击常见问题可跳出至外部页查看对应问题答案。点击【前往用户手册】可以跳至外部用户手册页。
          </div>
          <div id="confirm8">
            <div class="skip" @click="skip()">跳过</div>
            <div class="prev" @click="prev()">上一步</div>
            <div class="next" @click="next()">下一步</div>
            <img
              class="luBanLogo"
              src="~@/dss/assets/images/luBanLogo.png"
              alt=""
            />
          </div>
        </div>
        <div class="arrow">
          <img src="~@/dss/assets/images/6-1.svg" alt="" />
        </div>
        <div class="selected"></div>
      </div>
    </div>
    <div v-show="currentStep === 9">
      <div id="mask9">
        <div class="description">
          <p>
            以上就是鲁班的新手介绍了，在后续的使用中，<span>有任何问题，可以随时打开帮助文档</span>，查看当页的使用帮助，希望你在使用的过程中一切顺利。
          </p>
          <br />
          <p>
            如果你发现了任何bug或者有需要向我们反馈的问题，欢迎你进入用户反馈中心，留下你的宝贵意见。
          </p>
        </div>
        <div id="confirm1">
          <div class="next" @click="next()">我知道了</div>
          <img
            class="luBanLogo"
            src="~@/dss/assets/images/luBanLogo.png"
            alt=""
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Vue from 'vue'
export default {
  name: "newComerGuidance",
  data() {
    return {
      currentStep: 0,
    };
  },
  mounted() {
    if (this.$route.path === "/workspaceHome") {
      let store = localStorage.getItem("cacheGuide") 
      if(store === "null" | store === null){
        setTimeout(() => {
          this.currentStep += 1;
          let div = document.createElement("div");
          div.className = "overlay";
          let body = document.querySelector("body");
          body.appendChild(div);
        }, 1500);
        localStorage.setItem("cacheGuide", "ok");
      }
    }
  },
  methods: {
    iconRightClick() {
      if (this.currentStep === 5) {
        this.$parent.$refs.layoutHeader.handleUserClick();
        this.currentStep += 1;
        Vue.nextTick(()=>{
          let select6 = document.querySelector("#mask6 .selected")
          let userMenu = document.querySelector(".user-menu")
          select6.style.width = userMenu.clientWidth + "px"
        })
      }
    },
    prev() {
      let head = document.querySelector(".layout-header");
      let foot = document.querySelector(".layout-footer");
      let user = document.querySelector(".user")
      if (this.currentStep === 3) {
        foot.setAttribute("style", "z-index: 1002");
        this.currentStep -= 1;
        Vue.nextTick(()=>{
          let dropdownWidth = document.querySelector(".ivu-dropdown-rel")
          let block3 = document.querySelector(".headerSuspension .item1 .block3")
          let block5 = document.querySelector(".headerSuspension .item1 .block5")
          let img3 = document.querySelector(".headerSuspension .item2 .img3")
          let img5 = document.querySelector(".headerSuspension .item2 .img5")
          let computedStyle3 = getComputedStyle(img3, null)
          let offset = dropdownWidth.clientWidth + 38 - 110
          block5.setAttribute("style", "width: " + user.clientWidth + "px")
          block3.setAttribute("style", "width: " + (dropdownWidth.clientWidth + 38) + "px")
          img3.setAttribute("style", "margin-left: " + (parseInt(computedStyle3.marginLeft.slice(0, -2)) + offset) + "px")
          img5.setAttribute("style", "right: " + ((user.clientWidth / 2) + 5) + "px")
        })
      } else if (this.currentStep === 4) {
        this.$parent.$refs.layoutHeader.$children[0].hideMenu();
        this.currentStep -= 1;
      } else if (this.currentStep === 5) {
        this.$parent.$refs.layoutHeader.$children[0].handleTriggerClick();
        this.$parent.$refs.layoutHeader.$children[0].showRight();
        setTimeout(() => {
          this.currentStep -= 1;
        }, 250);
      } else if(this.currentStep === 6){
        this.currentStep -= 1
        Vue.nextTick(()=>{
          let select5 = document.querySelector("#mask5 .selected")
          select5.style.width = user.clientWidth + "px"
        })
      }else if (this.currentStep === 7) {
        head.setAttribute("style", "z-index: 1002");
        foot.setAttribute("style", "z-index: 1000");
        this.$parent.$refs.layoutHeader.handleUserClick();
        this.currentStep -= 1;
        Vue.nextTick(()=>{
          let select6 = document.querySelector("#mask6 .selected")
          let userMenu = document.querySelector(".user-menu")
          select6.style.width = userMenu.clientWidth + "px"
        })
      } else if (this.currentStep === 8) {
        this.$parent.$children[2].toggleGuide();
        this.currentStep -= 1;
      } else {
        this.currentStep -= 1;
      }
    },
    next() {
      let navM = document.querySelector(".luban-nav-mask");
      let overlay = document.querySelector(".overlay");
      let head = document.querySelector(".layout-header");
      let foot = document.querySelector(".layout-footer");
      let user = document.querySelector(".user")
      if (this.currentStep == 1) {
        head.setAttribute("style", "z-index: 1002");
        foot.setAttribute("style", "z-index: 1002");
        this.currentStep += 1;
        Vue.nextTick(()=>{
          let dropdownWidth = document.querySelector(".ivu-dropdown-rel")
          let block3 = document.querySelector(".headerSuspension .item1 .block3")
          let block5 = document.querySelector(".headerSuspension .item1 .block5")
          let img3 = document.querySelector(".headerSuspension .item2 .img3")
          let img5 = document.querySelector(".headerSuspension .item2 .img5")
          let computedStyle3 = getComputedStyle(img3, null)
          let offset = dropdownWidth.clientWidth + 38 - 110
          block5.setAttribute("style", "width: " + user.clientWidth + "px")
          block3.setAttribute("style", "width: " + (dropdownWidth.clientWidth + 38) + "px")
          img3.setAttribute("style", "margin-left: " + (parseInt(computedStyle3.marginLeft.slice(0, -2)) + offset) + "px")
          img5.setAttribute("style", "right: " + ((user.clientWidth / 2) + 5) + "px")
        })
      } else if (this.currentStep === 2) {
        foot.setAttribute("style", "z-index: 1000");
        this.currentStep += 1;
      } else if (this.currentStep === 3) {
        this.$parent.$refs.layoutHeader.$children[0].handleTriggerClick();
        this.$parent.$refs.layoutHeader.$children[0].showRight();
        setTimeout(() => {
          this.currentStep += 1;
          navM.removeEventListener(
            "click",
            this.$parent.$refs.layoutHeader.$children[0].hideMenu
          );
        }, 250);
      } else if (this.currentStep === 4) {
        this.$parent.$refs.layoutHeader.$children[0].hideMenu();
        this.currentStep += 1;
        Vue.nextTick(()=>{
          let select5 = document.querySelector("#mask5 .selected")
          select5.style.width = user.clientWidth + "px"
        })
      } else if (this.currentStep === 5) {
        this.$parent.$refs.layoutHeader.handleUserClick();
        this.currentStep += 1;
        Vue.nextTick(()=>{
          let select6 = document.querySelector("#mask6 .selected")
          let userMenu = document.querySelector(".user-menu")
          select6.style.width = userMenu.clientWidth + "px"
        })
      } else if (this.currentStep === 6) {
        head.setAttribute("style", "z-index: 1000");
        foot.setAttribute("style", "z-index: 1002");
        this.currentStep += 1;
      } else if (this.currentStep === 7) {
        this.currentStep += 1;
        this.$parent.$children[2].toggleGuide();
        this.$parent.$children[2].$children[1].flag = false;
      } else if (this.currentStep === 8) {
        foot.setAttribute("style", "z-index: 1000");
        this.$parent.$children[2].toggleGuide();
        this.currentStep += 1;
      } else if (this.currentStep === 9) {
        navM.addEventListener(
          "click",
          this.$parent.$refs.layoutHeader.$children[0].hideMenu
        );
        this.$parent.$children[2].$children[1].flag = true;
        overlay.parentNode.removeChild(overlay);
        this.currentStep += 1;
      } else {
        this.currentStep += 1;
      }
    },
    skip() {
      // 跳过 移除蒙层overlay 恢复对导航菜单的监听
      let navM = document.querySelector(".luban-nav-mask");
      let overlay = document.querySelector(".overlay");
      overlay.parentNode.removeChild(overlay);
      navM.addEventListener(
        "click",
        this.$parent.$refs.layoutHeader.$children[0].hideMenu
      );
      if (this.currentStep === 4) {
        this.$parent.$refs.layoutHeader.$children[0].hideMenu();
      }
      if (this.currentStep === 8) {
        this.$parent.$children[2].toggleGuide();
      }
      this.currentStep = 0;
      if (!this.$parent.$children[2].$children[1].flag) {
        this.$parent.$children[2].$children[1].flag = true;
      }
    },
    maskFooterClick() {
      // 点击右下角帮助，展开帮助文档，并且设置点击其他区域无法取消弹框
      this.currentStep += 1;
      this.$parent.$children[2].toggleGuide();
      this.$parent.$children[2].$children[1].flag = false;
    },
    navMenuClick() {
      // 点击导航菜单，打开弹出框，并移除对.luban-nav-mask元素的监听
      this.$parent.$refs.layoutHeader.$children[0].handleTriggerClick();
      this.$parent.$refs.layoutHeader.$children[0].showRight();
      setTimeout(() => {
        this.currentStep += 1;
        let navM = document.querySelector(".luban-nav-mask");
        navM.removeEventListener("click", this.$parent.$refs.layoutHeader.$children[0].hideMenu);
      }, 300);
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
