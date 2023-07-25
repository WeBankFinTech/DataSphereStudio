<template>
  <div class="register">
    <Row>
      <Col span="10">
        <div class="registerInput" @keyup.enter.stop.prevent="handleSubmit(formValidate)">
          <div class="registerTitle">{{$t('message.register.registerTitle')}}</div>
          <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80">
            <FormItem :label="$t('message.register.invit')" prop="invitation">
              <Input
                v-model="formValidate.invitation"
                :placeholder="$t('message.register.invitation')"
                class="invitation"
              />
              <span class="invitInfo" @mousemove="show" @mouseleave="hide">
                <Icon type="ios-help" size="24" />
              </span>
              <div v-if="isShow" class="explantion">
                <p class="title">{{this.$t('message.register.tips')}}</p>
                <Icon type="ios-alert-outline" />
                <p class="tip">{{this.$t('message.register.codeInfo')}}</p>
              </div>
            </FormItem>
            <FormItem :label="$t('message.register.org')" prop="organization" class="orga">
              <Input
                v-model="formValidate.organization"
                :placeholder="$t('message.register.organization')"
              />
            </FormItem>
            <FormItem :label="$t('message.register.nam')" prop="name">
              <Input v-model="formValidate.name" :placeholder="$t('message.register.name')" />
            </FormItem>
            <FormItem :label="$t('message.register.pho')" prop="phone">
              <Input v-model="formValidate.phone" :placeholder="$t('message.register.phone')" />
            </FormItem>
            <FormItem :label="$t('message.register.ver')" prop="code">
              <Input
                v-model="formValidate.code"
                :placeholder="$t('message.register.veriCode')"
                class="verCode"
              />
              <canvas id="canvas" width="120" height="40" ref="canvas" @click="drawPic()"></canvas>
            </FormItem>
            <FormItem :label="$t('message.register.contact')" prop="contactType">
              <Input
                v-model="formValidate.contactType"
                :placeholder="$t('message.register.contactType')"
              />
            </FormItem>
            <FormItem :label="$t('message.register.discover.how')" prop="discovery">
              <CheckboxGroup v-model="formValidate.discovery">
                <Checkbox :label="$t('message.register.discover.GitHub')"></Checkbox>
                <Checkbox :label="$t('message.register.discover.OsChina')"></Checkbox>
                <Checkbox :label="$t('message.register.discover.search')"></Checkbox>
                <Checkbox :label="$t('message.register.discover.csdn')"></Checkbox>
                <Checkbox :label="$t('message.register.discover.decomend')"></Checkbox>
                <Checkbox :label="$t('message.register.discover.meeting')"></Checkbox>
              </CheckboxGroup>
            </FormItem>
            <FormItem :label="$t('message.register.des')" prop="desc">
              <Input
                v-model="formValidate.desc"
                type="textarea"
                :autosize="{minRows: 4,maxRows: 5}"
                :placeholder="$t('message.register.desc')"
              />
            </FormItem>
            <FormItem class="item">
              <Checkbox v-model="isAgree">{{this.$t('message.register.agree.agr')}}</Checkbox>
              <span @click="goService('service')">{{$t('message.register.agree.service')}}</span>
              {{$t('message.register.agree.and')}}
              <span
                @click="goPrivacy('privacy')"
              >{{$t('message.register.agree.privacy')}}</span>
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                @click="handleSubmit('formValidate')"
                class="sub"
                :disabled="isdisabledFn"
              >{{this.$t('message.register.application')}}</Button>
            </FormItem>
          </Form>
        </div>
      </Col>
      <Col span="6"><div class="contentInfo">
        <div class="content">
          <p class="title">
            {{this.$t('message.register.invitationJoin.welcome')}}
          </p>
          <p class="info">
            {{this.$t('message.register.invitationJoin.join')}}
          </p>
          <img src="../images/QR code.jpg" alt />
          <!-- <p class="info">
            {{this.$t('message.register.invitationJoin.send')}}
            <a
              href="https://mailto:wedatasphere@webank.com"
            >wedatasphere@webank.com</a>
          </p>
          <p class="info">
            {{this.$t('message.register.invitationJoin.tag')}}
            <br />
            {{this.$t('message.register.invitationJoin.thank')}}
          </p> -->
        </div>
        <div class="invitationWatch">
          <p class="notice">
            {{this.$t('message.register.watch.welcome')}}
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/DataSphereStudio"
              target="_Blank"
            >{{this.$t('message.register.watch.data')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Linkis"
              target="_Blank"
            >{{this.$t('message.register.watch.links')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Qualitis"
              target="_Blank"
            >{{this.$t('message.register.watch.qualitis')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Scriptis"
              target="_Blank"
            >{{this.$t('message.register.watch.Script')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Visualis"
              target="_Blank"
            >{{this.$t('message.register.watch.Visualis')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Streamis"
              target="_Blank"
            >{{this.$t('message.register.watch.Streamis')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Schedulis"
              target="_Blank"
            >{{this.$t('message.register.watch.Schedulis')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Prophecis"
              target="_Blank"
            >{{this.$t('message.register.watch.Prophecis')}}</a>
          </p>
          <p class="toGithub">
            <a
              href="https://github.com/WeBankFinTech/Exchangis"
              target="_Blank"
            >{{this.$t('message.register.watch.Exchangis')}}</a>
          </p>
        </div>
      </div></Col>
      <Col span="6"></Col>
    </Row>
    <div class="mask" v-if="isok">
      <div class="loginOk">
        <p class="title"><span>{{this.$t('message.register.tips')}}</span> <Icon type="md-close" @click="oncolse" /></p>
        <Icon type="ios-checkmark-circle-outline" class="ok"/>
        <p class="reSuccess">
          <i>{{this.$t('message.register.success')}}</i>
        </p>
        <p class="pass">{{this.$t('message.register.userNameTitle')}}<span>{{this.$t('message.register.userName')}}</span></p>
        <p class="pass">{{this.$t('message.register.pass')}}<span>{{this.$t('message.register.pa')}}</span></p>
      </div>
    </div>
    <div class="serviceInfo" v-if="curType=='service'">
      <Service @go_register="goRegister()" />
    </div>
    <div class="serviceInfo" v-if="curType=='privacy'">
      <Privacy @go_register="goRegister()" />
    </div>
  </div>
</template>
<script>
import api from "@dataspherestudio/shared/common/service/api";
import Service from "./termsOfService.vue";
import Privacy from "./termsOfPrivacy.vue";
export default {
  components: {
    Service,
    Privacy
  },
  data() {
    const validateInvit = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("message.register.invitation")));
      } else if (value.indexOf(" ") > -1 || !/^[A-Za-z0-9]*$/.test(value)) {
        callback(new Error(this.$t("message.register.errTip.invitation")));
      } else {
        callback();
      }
    };
    const validateCodeCheck = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("message.register.errTip.vericode")));
      } else if (value.toUpperCase() !== this.formValidate.pcode) {
        callback(new Error(this.$t("message.register.errTip.code")));
      } else {
        callback();
      }
    };
    const validateOrganization = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("message.register.organization")));
      } else if (value.indexOf(" ") > -1) {
        callback(new Error(this.$t("message.register.errTip.format")));
      } else if (value.length > 120) {
        callback(new Error(this.$t("message.register.errTip.org")));
      } else {
        callback();
      }
    };
    const validateName = (rule, value, callback) => {
      if (value === "" || value.replace(/\s/g, "").length == 0) {
        callback(new Error(this.$t("message.register.name")));
      } else if (value.indexOf(" ") > -1) {
        callback(new Error(this.$t("message.register.errTip.format")));
      } else if (value.length > 120) {
        callback(new Error(this.$t("message.register.errTip.org")));
      } else {
        callback();
      }
    };
    const validateContact = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(this.$t("message.register.errTip.contact")));
      } else if (
        value.indexOf(" ") > -1 ||
        value.replace(/\s/g, "").length == 0
      ) {
        callback(new Error(this.$t("message.register.errTip.format")));
      } else {
        callback();
      }
    };
    return {
      formValidate: {
        organization: "",
        name: "",
        phone: "",
        contactType: "",
        discovery: [],
        desc: "",
        code: "",
        pcode: "",
        invitation: ""
      },
      isdisabledFn: false,
      isAgree: false,
      isok: false,
      curType: "",
      isShow: false,
      ruleValidate: {
        invitation: [
          {
            required: true,
            message: this.$t("message.register.invitation"),
            trigger: "blur"
          },
          { validator: validateInvit, trigger: "blur" }
        ],
        organization: [
          {
            required: true,
            message: this.$t("message.register.organization"),
            trigger: "blur"
          },
          { validator: validateOrganization, trigger: "blur" }
        ],
        name: [
          {
            required: true,
            message: this.$t("message.register.name"),
            trigger: "change"
          },
          { validator: validateName, trigger: "blur" }
        ],
        phone: [
          {
            required: true,
            message: this.$t("message.register.phone"),
            trigger: "blur"
          },
          {
            type: "string",
            pattern: /^1[3456789]\d{9}$/,
            message: this.$t("message.register.errTip.phone"),
            trigger: "blur"
          }
        ],
        contactType: [
          {
            required: true,
            message: this.$t("message.register.errTip.contact"),
            trigger: "change"
          },
          { validator: validateContact, trigger: "blur" }
        ],
        discovery: [
          {
            required: true,
            type: "array",
            min: 1,
            message: this.$t("message.register.errTip.discovery"),
            trigger: "change"
          }
        ],
        desc: [
          {
            type: "string",
            max: 160,
            message: this.$t("message.register.errTip.desc"),
            trigger: "blur"
          }
        ],
        code: [
          {
            required: true,
            message: this.$t("message.register.veriCode"),
            trigger: "blur"
          },
          { validator: validateCodeCheck, trigger: "blur" }
        ]
      }
    };
  },
  //没有勾选同意服务条款和隐私条款时禁用立即申请
  watch: {
    formValidate: {
      handler() {
        let isChoiceAll =
          this.formValidate.organization &&
          this.formValidate.name &&
          this.formValidate.phone &&
          this.formValidate.discovery.length > 0 &&
          this.formValidate.code &&
          this.formValidate.contactType;
        if (!isChoiceAll || (isChoiceAll && this.isAgree)) {
          this.isdisabledFn = false;
        } else {
          this.isdisabledFn = true;
        }
      },
      immediate: true,
      deep: true
    },
    isAgree() {
      let isChoiceAll =
        this.formValidate.organization ||
        this.formValidate.name ||
        this.formValidate.phone ||
        this.formValidate.discovery.length > 0 ||
        this.formValidate.code ||
        this.formValidate.contactType;
      if (this.isAgree || (!isChoiceAll && !this.isAgree)) {
        this.isdisabledFn = false;
      } else {
        this.isdisabledFn = true;
      }
    }
  },
  created() {},
  mounted() {
    this.drawPic();
  },
  methods: {
    show() {
      this.isShow = true;
    },
    hide() {
      this.isShow = false;
    },
    goService(type) {
      this.curType = type;
    },
    goPrivacy(type) {
      this.curType = type;
    },
    goRegister() {
      this.curType = "";
    },
    oncolse() {
      this.isok = false;
      this.$router.push("/login");
    },
    handleSubmit(name) {
      if (!this.isAgree) {
        this.isdisabledFn = true;
      }
      this.$refs[name].validate(valid => {
        if (valid) {
          let discoveryList = this.formValidate.discovery;
          let discoveryAll = [
            "GitHub",
            "OsChina/Gitee",
            "搜索引擎",
            "CSDN",
            "朋友推荐",
            "相关活动吸引"
          ];
          let discoveryIndex = [];
          discoveryAll.forEach(item => {
            let index = discoveryList.findIndex(value => item == value);
            if (index >= 0) {
              discoveryIndex.push(index);
            }
          });
          let discoveryFormat = discoveryIndex.join(",");
          console.log(discoveryFormat, this.formValidate.discovery)
          const params = {
            phone: this.formValidate.phone.replace(/(^\s*)|(\s*$)/g, ""),
            name: this.formValidate.name.replace(/(^\s*)|(\s*$)/g, ""),
            organization: this.formValidate.organization.replace(
              /(^\s*)|(\s*$)/g,
              ""
            ),
            contact: this.formValidate.contactType.replace(
              /(^\s*)|(\s*$)/g,
              ""
            ),
            from_type: discoveryFormat,
            code: this.formValidate.invitation.replace(/(^\s*)|(\s*$)/g, "")
          };
          //给后台发送数据
          api
            .fetch("/user/register", params, "post")
            .then(res => {
              if (res.phone) {
                // this.$Notice.success({
                //   title: "注册成功",
                //   render: h => {
                //     return h("span", [
                //       "您的密码是 ",
                //       h(
                //         "span",
                //         { style: "font-weight:bold;color:#17233d" },
                //         "手机号后六位"
                //       )
                //     ]);
                //   },
                //   duration: 0,
                //   onClose: () => {
                //     this.$router.push("/login");
                //   }
                // });
                this.isok = true;
              }
            })
            .catch(error => {
              console.log(error)
            });
        } else {
          this.$Message.error(this.$t("message.register.error"));
        }
      });
    },
    handleReset(name) {
      this.$refs[name].resetFields();
    },
    /**生成一个随机数**/
    randomNum(min, max) {
      return Math.floor(Math.random() * (max - min) + min);
    },
    /**生成一个随机色**/
    randomColor(min, max) {
      var r = this.randomNum(min, max);
      var g = this.randomNum(min, max);
      var b = this.randomNum(min, max);
      return "rgb(" + r + "," + g + "," + b + ")";
    },
    drawPic() {
      var canvas = this.$refs.canvas;
      var width = canvas.width;
      var height = canvas.height;
      var ctx = canvas.getContext("2d");
      ctx.textBaseline = "bottom";
      /**绘制背景色**/
      ctx.fillStyle = "#2d8cf0";
      ctx.fillRect(0, 0, width, height);
      /**绘制文字**/
      var str = "ABCEFGHJKLMNPQRSTWXY123456789";
      var code = [];
      for (let i = 0; i < 4; i++) {
        var txt = str[this.randomNum(0, str.length)];
        code.push(txt);
        // ctx.fillStyle = this.randomColor(50, 160); //随机生成字体颜色
        ctx.fillStyle = "#fff";
        ctx.font = this.randomNum(15, 40) + "px SimHei"; //随机生成字体大小
        var x = 10 + i * 25;
        var y = this.randomNum(25, 45);
        var deg = this.randomNum(-45, 45);
        //修改坐标原点和旋转角度
        ctx.translate(x, y);
        ctx.rotate((deg * Math.PI) / 180);
        ctx.fillText(txt, 0, 0);
        //恢复坐标原点和旋转角度
        ctx.rotate((-deg * Math.PI) / 180);
        ctx.translate(-x, -y);
      }
      this.formValidate.pcode = code.join("");
      // /**绘制干扰线**/
      for (let i = 0; i < 8; i++) {
        ctx.strokeStyle = this.randomColor(40, 180);
        ctx.beginPath();
        ctx.moveTo(this.randomNum(0, width), this.randomNum(0, height));
        ctx.lineTo(this.randomNum(0, width), this.randomNum(0, height));
        ctx.stroke();
      }
      /**绘制干扰点**/
      // for(let i = 0; i < 100; i++) {
      //   ctx.fillStyle = this.randomColor(0, 255);
      //   ctx.beginPath();
      //   ctx.arc(this.randomNum(0, width), this.randomNum(0, height), 1, 0, 2 * Math.PI);
      //   ctx.fill();
      // }
    }
  }
};
</script>
<style lang="scss" scoped>

@import '@dataspherestudio/shared/common/style/variables.scss';
.register {
    min-width: 1200px;
    margin-left: 20%;
    height: 100%;
    overflow: auto;
    .registerInput {
        // margin-left: 20%;
        width: 70%;
        padding-top: 25%;
    }
    .registerTitle {
        font-size: 24px;
        text-align: center;
        margin-bottom: 40px;
    }
    .ivu-form-item-required {
        .ivu-form-item-label {
            width: 150px !important;
            margin-left: -70px;
        }
        .ivu-form-item-label:after {
            content: '*';
            display: inline-block;
            margin-left: 4px;
            line-height: 1;
            font-family: SimSun;
            font-size: 12px;
            color: #ed4014;
        }
        .ivu-form-item-label:before {
            content: '';
        }
        .invitation {
            width: 90%;
        }
    }
    .ivu-tooltip-light {
        width: 250px;
        // left: 430px !important;
        position: absolute;
        will-change: top, left;
        top: 100px;
    }
    .invitInfo {
        background: #2d8cf0;
        color: #ffffff;
        display: block;
        text-align: center;
        border-radius: 3px;
        top: 100px;
        width: 10%;
        position: absolute;
        top: 0px;
        left: 90%;
        height: 34px;
        cursor: pointer;
    }
    .explantion {
        box-shadow: 0px 0px 15px 0px rgba(0, 0, 0, 0.2);
        position: absolute;
        left: 70%;
        width: 300px;
        height: 300px;
        z-index: 10;
        background: #ffffff;
        // text-align: center;
        top: 50px;
        i {
            font-size: 36px;
            margin: 50px 0 10px;
            color: #2d8cf0;
            margin-left: 130px;
        }
        .tip {
            text-align: center;
        }
        .title {
            border-bottom: 1px solid #515a6e14;
            font-size: 16px;
            font-weight: bold;
            padding-left: 10px;
        }
    }
    .contentInfo {
        padding-top: 50%;
        min-width: 400px;
    }
    .content {
        text-align: center;
        .info {
            text-indent: 25px;
            // padding-left: 32px;
        }
        img {
            width: 200px;
            height: 200px;
        }
        .title {
            text-align: center;
            font-size: 16px;
            font-weight: bold;
            margin-bottom: 20px;
        }
    }
    .verCode {
        width: 60%;
    }
    #canvas {
        position: absolute;
        height: 32px;
        width: 35%;
        margin-left: 5%;
    }
    .sub {
        width: 100%;
    }
    .clause {
        width: 400px;
    }
    .item {
        .ivu-tooltip-light {
            width: 1200px;
        }
        span {
            color: #2d8cf0;
            cursor: pointer;
        }
    }
    .service {
        width: 1000px;
        margin-left: 100px;
    }
    .mask {
        position: fixed;
        height: 100%;
        width: 100%;
        top: 0px;
        left: 0px;
        background: rgba(0, 0, 0, 0.5);
        .loginOk {
            position: absolute;
            top: 50%;
            left: 40%;
            width: 300px;
            height: 300px;
            background: #ffffff;
            z-index: 12000;
            transform: translateY(-50%);
            // text-align: center;
            // span {
            //     font-size: 16px;
            //     font-weight: bold;
            // }
            button {
                margin-left: 100px;
                margin-top: 40px;
            }
            .title {
                border-bottom: 1px solid #515a6e14;
                height: 40px;
                line-height: 40px;
                i {
                    font-size: 24px;
                    position: absolute;
                    right: 20px;
                    top: 10px;
                    cursor: pointer;
                }
                span {
                    font-size: 16px;
                    margin-left: 10px;
                    font-weight: bold;
                }
            }
            .ok {
                font-size: 36px;
                color: #19be6b;
                margin-left: 130px;
                margin-top: 40px;
            }
            .reSuccess {
                text-align: center;
                margin-top: 20px;
                i {
                    font-size: 16px;
                    font-style: normal;
                    padding-bottom: 20px;
                }
                margin-bottom: 15px;
            }
            .pass {
                text-align: center;
                font-weight: bold;
                font-size: 14px;
            }
        }
    }
    .invitationWatch {
        font-size: 12px;
        // width: 80%;
        margin-top: 10%;
        z-index: 999;
        text-align: center;
        .reSuccess {
            span {
                font-size: 14px;
                font-weight: bold;
            }
        }
        .toGithub {
            margin-top: 10px;
        }
        .notice {
            text-align: center;
            margin-bottom: 20px;
            font-size: 16px;
            span {
                font-weight: bold;
            }
        }
    }
}

.serviceInfo {
    width: 800px;
    position: fixed;
    left: 0;
    right: 0;
    bottom: 50px;
    top: 50px;
    margin: auto;
    background: #ffffff;
    box-shadow: 0px 0px 15px 0px rgba(0, 0, 0, 0.2);
    z-index: 999;
    overflow: auto;
    padding-bottom: 15px;
}
</style>

