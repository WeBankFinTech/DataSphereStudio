<template>
  <div>
    <div v-if="currentStep === 1" key="1">
      <div id="mask1">
        <div class="description">
          <p>
            {{ $t('message.common.dss.dss') }}
          </p>
          <br />
          <p>{{ $t('message.common.dss.nextoper') }}</p>
        </div>
        <div id="confirm1">
          <div class="next" @click="next()">{{ $t('message.common.dss.isee') }}</div>
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
        <div class="description">{{$t('message.common.dss.isworkspacehome', {name:$APP_CONF.app_name})}}</div>
        <div id="confirm2">
          <div class="skip" @click="skip()">{{ $t('message.common.dss.skip') }}</div>
          <div class="next" @click="next()">{{ $t('message.common.dss.nextstep') }}</div>
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
            {{ $t('message.common.dss.click') }}<span>{{ $t('message.common.dss.customemenu') }}</span>{{$t('message.common.dss.globalfunmenu')}}
          </div>
          <div class="desc2">
            {{ $t('message.common.dss.click') }}<span>{{ $APP_CONF.app_name }}</span>{{$t('message.common.dss.returnhome')}}
          </div>
          <div class="desc3">
            {{ $t('message.common.dss.showall') }}<span>{{ $t('message.common.dss.Workspace') }}</span>{{$t('message.common.dss.switch')}}
          </div>
          <div class="desc4">{{ $t('message.common.dss.click') }}<span>{{ $t('message.common.dss.homepage2') }}</span>{{$t('message.common.dss.returnhome')}}</div>
          <div class="desc5">
            {{ $t('message.common.dss.click') }}<span>{{ $t('message.common.dss.Account') }}</span>{{$t('message.common.dss.switchandclear')}}
          </div>
        </div>
      </div>
      <div class="footerSuspension">
        <div class="item1">
          <div class="desc1">
            {{ $t('message.common.dss.click') }}<span>{{ $t('message.common.dss.helpcenter') }}</span>{{$t('message.common.dss.documentpage')}}
          </div>
          <div class="desc2">
            {{ $t('message.common.dss.click') }}<span>{{ $t('message.common.dss.resource') }}</span>{{$t('message.common.dss.engineuse')}}
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
        <div class="desc">{{ $t('message.common.dss.this') }} <span>{{ $t('message.common.dss.customemenu') }}</span>{{ $t('message.common.dss.tryclick') }}</div>
        <div id="confirm3">
          <div class="skip" @click="skip()">{{ $t('message.common.dss.skip') }}</div>
          <div class="prev" @click="prev()">{{ $t('message.common.dss.prevstep') }}</div>
          <div class="next" @click="next()">{{ $t('message.common.dss.nextstep') }}</div>
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
            {{ $t('message.common.dss.this') }}<span>{{ $t('message.common.dss.customemenu') }}</span>{{ $t('message.common.dss.savetomenu') }}
          </div>
          <div id="confirm4">
            <div class="skip" @click="skip()">{{ $t('message.common.dss.skip') }}</div>
            <div class="prev" @click="prev()">{{ $t('message.common.dss.prevstep') }}</div>
            <div class="next" @click="next()">{{ $t('message.common.dss.nextstep') }}</div>
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
        <div class="desc">{{ $t('message.common.dss.this') }}<span>{{ $t('message.common.dss.Account') }}</span>{{ $t('message.common.dss.tryclick') }}</div>
        <div id="confirm5">
          <div class="skip" @click="skip()">{{ $t('message.common.dss.skip') }}</div>
          <div class="prev" @click="prev()">{{ $t('message.common.dss.prevstep') }}</div>
          <div class="next" @click="next()">{{ $t('message.common.dss.nextstep') }}</div>
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
          {{ $t('message.common.dss.this') }}<span>{{ $t('message.common.dss.Account') }}</span>，{{ $t('message.common.dss.switchaccount') }}
        </div>
        <div id="confirm6">
          <div class="skip" @click="skip()">{{ $t('message.common.dss.skip') }}</div>
          <div class="prev" @click="prev()">{{ $t('message.common.dss.prevstep') }}</div>
          <div class="next" @click="next()">{{ $t('message.common.dss.nextstep') }}</div>
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
          <div class="desc">{{ $t('message.common.dss.this') }}<span>{{ $t('message.common.dss.dochelp') }}</span>{{ $t('message.common.dss.tryclick') }}</div>
          <div id="confirm7">
            <div class="skip" @click="skip()">{{ $t('message.common.dss.skip') }}</div>
            <div class="prev" @click="prev()">{{ $t('message.common.dss.prevstep') }}</div>
            <div class="next" @click="next()">{{ $t('message.common.dss.nextstep') }}</div>
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
    <div v-show="currentStep === 8">
      <div id="mask9">
        <div class="description">
          <p>
            {{$t('message.common.dss.endintro', {app:$APP_CONF.app_name})}}<span>{{ $t('message.common.dss.anyquestion') }}</span>{{$t('message.common.dss.vish')}}
          </p>
          <br />
          <p>
            {{ $t('message.common.dss.bugreport') }}
          </p>
        </div>
        <div id="confirm1">
          <div class="next" @click="next()">{{ $t('message.common.dss.isee') }}</div>
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
      if (store === "null" | store === null) {
        this.timer = setTimeout(() => {
          this.currentStep += 1;
          let div = document.createElement("div");
          div.className = "overlay ___guidance_overlay";
          let body = document.querySelector("body");
          body.appendChild(div);
        }, 1500);
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
          select6.style.width = userMenu.clientWidth - 10 + "px"
          select6.style.height = userMenu.clientHeight + "px"
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
          // let img3 = document.querySelector(".headerSuspension .item2 .img3")
          let img5 = document.querySelector(".headerSuspension .item2 .img5")
          // let computedStyle3 = getComputedStyle(img3, null)
          // let offset = dropdownWidth.clientWidth + 38 - 110
          block5.setAttribute("style", "width: " + user.clientWidth + "px")
          block3.setAttribute("style", "width: " + (dropdownWidth.clientWidth + 38) + "px")
          // img3.setAttribute("style", "margin-left: " + (parseInt(computedStyle3.marginLeft.slice(0, -2)) + offset) + "px")
          img5.setAttribute("style", "right: " + ((user.clientWidth / 2) + 5) + "px")
        })
      } else if (this.currentStep === 4) {
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu();
        this.currentStep -= 1;
      } else if (this.currentStep === 5) {
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.handleTriggerClick();
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.showRight();
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
          select6.style.width = userMenu.clientWidth - 10 + "px"
          select6.style.height = userMenu.clientHeight + "px"
        })
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
      localStorage.setItem("cacheGuide", "ok");
      if (this.currentStep == 1) {
        head.setAttribute("style", "z-index: 1002");
        foot.setAttribute("style", "z-index: 1002");
        this.currentStep += 1;
        Vue.nextTick(()=>{
          let dropdownWidth = document.querySelector(".ivu-dropdown-rel")
          let block3 = document.querySelector(".headerSuspension .item1 .block3")
          let block5 = document.querySelector(".headerSuspension .item1 .block5")
          // let img3 = document.querySelector(".headerSuspension .item2 .img3")
          let img5 = document.querySelector(".headerSuspension .item2 .img5")
          // let computedStyle3 = getComputedStyle(img3, null)
          // let offset = dropdownWidth.clientWidth + 38 - 110
          block5.setAttribute("style", "width: " + user.clientWidth + "px")
          block3.setAttribute("style", "width: " + (dropdownWidth.clientWidth + 38) + "px")
          // img3.setAttribute("style", "margin-left: " + (parseInt(computedStyle3.marginLeft.slice(0, -2)) + offset) + "px")
          img5.setAttribute("style", "right: " + ((user.clientWidth / 2) + 5) + "px")
        })
      } else if (this.currentStep === 2) {
        foot.setAttribute("style", "z-index: 1000");
        this.currentStep += 1;
      } else if (this.currentStep === 3) {
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.handleTriggerClick();
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.showRight();
        setTimeout(() => {
          this.currentStep += 1;
          navM.removeEventListener(
            "click",
            this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu
          );
        }, 250);
      } else if (this.currentStep === 4) {
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu();
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
          select6.style.width = userMenu.clientWidth - 10 + "px"
          select6.style.height = userMenu.clientHeight + "px"
        })
      } else if (this.currentStep === 6) {
        head.setAttribute("style", "z-index: 1000");
        foot.setAttribute("style", "z-index: 1002");
        this.currentStep += 1;
      } else if (this.currentStep === 7) {
        this.currentStep += 1;
      } else if (this.currentStep === 8) {
        navM.addEventListener(
          "click",
          this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu
        );
        this.$parent.$refs.layoutFooter.$refs.Guide.flag = true;
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
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu
      );
      if (this.currentStep === 4) {
        this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu();
      }
      this.currentStep = 0;
      if (!this.$parent.$refs.layoutFooter.$refs.Guide.flag) {
        this.$parent.$refs.layoutFooter.$refs.Guide.flag = true;
      }
    },
    maskFooterClick() {
      // 点击右下角帮助，展开帮助文档，并且设置点击其他区域无法取消弹框
      this.currentStep += 1;
      this.$parent.$refs.layoutFooter.toggleGuide();
      this.$parent.$refs.layoutFooter.$refs.Guide.flag = false;
    },
    navMenuClick() {
      // 点击导航菜单，打开弹出框，并移除对.luban-nav-mask元素的监听
      this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.handleTriggerClick();
      this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.showRight();
      setTimeout(() => {
        this.currentStep += 1;
        let navM = document.querySelector(".luban-nav-mask");
        navM.removeEventListener("click", this.$parent.$refs.layoutHeader.$refs.vueLubanMenu.hideMenu);
      }, 300);
    },
  },
  beforeDestroy() {
    clearTimeout(this.timer)
    let overlay = document.querySelector(".___guidance_overlay");
    if (overlay) overlay.parentNode.removeChild(overlay);
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
