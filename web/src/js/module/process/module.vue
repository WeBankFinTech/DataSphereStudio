<template>
  <div class="process-module">
    <vueProcess
      ref="process"
      :shapes="shapes"
      :value="originalData"
      :ctx-menu-options="nodeMenuOptions"
      :view-options="viewOptions"
      :disabled="workflowIsExecutor"
      @change="change"
      @message="message"
      @node-click="click"
      @node-dblclick="dblclick"
      @node-baseInfo="saveNodeBaseInfo"
      @node-param="saveNodeParam"
      @node-delete="nodeDelete"
      @ctx-menu-associate="checkAssociated"
      @add="addNode"
      @ctx-menu-relySelect="relySelect"
      @ctx-menu-mycopy="copyNode"
      @ctx-menu-mypaste="pasteNode"
      @ctx-menu-allDelete="allDelete"
      @ctx-menu-console="openConsole">
      <template v-if="!myReadonly && type==='flow'">
        <div
          class="button"
          :title="$t('message.process.params')"
          @click.stop="showParamView">
          <svg
            ref="paramButton"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="1275"><path
              d="M192.402 273.2c-69.475 0-126 56.525-126 126s56.525 126 126 126 126-56.525 126-126-56.525-126-126-126zM192.402 448.4c-27.129 0-49.2-22.071-49.2-49.2s22.071-49.2 49.2-49.2 49.2 22.071 49.2 49.2-22.075 49.2-49.2 49.2z"
              fill=""
              p-id="1276"></path><path
              d="M192.402 242c23.861 0 43.2-19.339 43.2-43.2v-92.4c0-23.861-19.339-43.2-43.2-43.2s-43.2 19.339-43.2 43.2v92.4c0 23.861 19.339 43.2 43.2 43.2z"
              fill=""
              p-id="1277"></path><path
              d="M192.402 557.6c-23.861 0-43.2 19.339-43.2 43.2v319.2c0 23.861 19.339 43.2 43.2 43.2s43.2-19.339 43.2-43.2v-319.2c0-23.861-19.344-43.2-43.2-43.2z"
              fill=""
              p-id="1278"></path><path
              d="M516.224 389.216c-69.471 0-125.995 56.525-125.995 126s56.52 126 125.995 126c69.48 0 126-56.52 126-126s-56.52-126-126-126zM516.224 564.416c-27.125 0-49.195-22.071-49.195-49.2s22.071-49.2 49.195-49.2c27.134 0 49.2 22.071 49.2 49.2s-22.066 49.2-49.2 49.2z"
              fill=""
              p-id="1279"></path><path
              d="M516.229 356c23.856 0 43.2-19.339 43.2-43.2v-207.6c0-23.861-19.339-43.2-43.2-43.2-23.861 0-43.2 19.339-43.2 43.2v207.6c0 23.861 19.344 43.2 43.2 43.2z"
              fill=""
              p-id="1280"></path><path
              d="M516.229 676.4c-23.861 0-43.2 19.339-43.2 43.2v199.2c0 23.861 19.339 43.2 43.2 43.2 23.856 0 43.2-19.339 43.2-43.2v-199.2c0-23.861-19.339-43.2-43.2-43.2z"
              fill=""
              p-id="1281"></path><path
              d="M840.061 499.083c-69.48 0-126 56.52-126 126 0 69.475 56.52 125.995 126 125.995s126-56.52 126-125.995c0-69.48-56.525-126-126-126zM840.061 674.278c-27.129 0-49.2-22.071-49.2-49.195 0-27.129 22.071-49.2 49.2-49.2s49.2 22.071 49.2 49.2c0 27.125-22.071 49.195-49.2 49.195z"
              fill=""
              p-id="1282"></path><path
              d="M840.061 466.4c23.861 0 43.2-19.339 43.2-43.2v-318c0-23.861-19.339-43.2-43.2-43.2s-43.2 19.339-43.2 43.2v318c0 23.861 19.339 43.2 43.2 43.2z"
              fill=""
              p-id="1283"></path><path
              d="M840.061 786.8c-23.861 0-43.2 19.339-43.2 43.2v88.8c0 23.861 19.339 43.2 43.2 43.2s43.2-19.339 43.2-43.2v-88.8c0-23.861-19.344-43.2-43.2-43.2z"
              fill=""
              p-id="1284"></path></svg>
          <span>{{$t('message.process.params')}}</span>
        </div>
        <div class="devider" />
        <div
          class="button"
          :title="$t('message.process.resource')"
          @click.stop="showResourceView">
          <svg
            ref="resourceButton"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="2276"
            width="16"
            height="16">
            <path
              d="M111.395066 570.932204l87.488587 0 0 255.842922-87.488587 0 0-255.842922Z"
              p-id="2277"></path>
            <path
              d="M825.115324 570.932204l87.488587 0 0 255.842922-87.488587 0 0-255.842922Z"
              p-id="2278"></path>
            <path
              d="M111.395066 826.775126l801.208844 0 0 87.866187-801.208844 0 0-87.866187Z"
              p-id="2279"></path>
            <path
              d="M563.245128 722.759121 463.476867 722.759121 463.476867 287.760866 320.414567 429.16234 256.453836 365.201609 512.296759 109.358687 768.138658 365.201609 704.178951 429.16234 561.710169 290.830785Z"
              p-id="2280"></path>
          </svg>
          <span>{{$t('message.process.resource')}}</span>
        </div>
        <div class="devider" />
        <!-- <div
          :title="$t('message.process.import')"
          class="button"
          @click="showImportJsonView">
          <svg
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="3325"><path
              d="M928.64 181.76l-128-128A192 192 0 0 0 683.52 0H231.68a71.68 71.68 0 0 0-69.76 72.96v352h71.68V86.4a14.08 14.08 0 0 1 14.08-14.72h434.56v232.96H905.6V937.6a14.72 14.72 0 0 1-14.08 14.72H247.68a14.72 14.72 0 0 1-14.08-14.72V832h-71.68v119.04a71.68 71.68 0 0 0 69.76 72.96h676.48a71.68 71.68 0 0 0 69.76-72.96V329.6a272.64 272.64 0 0 0-49.28-147.84z m-188.8 57.6V93.44l9.6 8.32 128 128a64 64 0 0 1 7.68 10.24z m58.24 202.24H85.76a40.32 40.32 0 0 0-39.68 40.32v274.56a40.32 40.32 0 0 0 39.68 40.32h712.32a40.32 40.32 0 0 0 39.68-40.32V481.92a40.32 40.32 0 0 0-39.68-40.32zM512 571.52a31.36 31.36 0 0 0-25.6 11.52 64 64 0 0 0-9.6 37.12 57.6 57.6 0 0 0 9.6 37.12 30.72 30.72 0 0 0 26.24 11.52 31.36 31.36 0 0 0 26.88-11.52 64 64 0 0 0 9.6-39.68 53.76 53.76 0 0 0-10.24-35.2A34.56 34.56 0 0 0 512 571.52z m-290.56 56.32a133.12 133.12 0 0 1-5.76 44.8 55.68 55.68 0 0 1-20.48 26.24 71.04 71.04 0 0 1-40.32 10.24 94.72 94.72 0 0 1-43.52-5.12 49.92 49.92 0 0 1-22.4-20.48 84.48 84.48 0 0 1-9.6-33.28L128 640a46.72 46.72 0 0 0 0 16 19.2 19.2 0 0 0 6.4 8.96 12.8 12.8 0 0 0 8.96 0 14.72 14.72 0 0 0 12.8-6.4 40.32 40.32 0 0 0 8.96-18.56V534.4h53.12v93.44z m172.8 53.76a64 64 0 0 1-25.6 21.12 110.72 110.72 0 0 1-41.6 7.04 84.48 84.48 0 0 1-60.8-16.64 64 64 0 0 1-19.2-42.88h50.56a51.84 51.84 0 0 0 6.4 18.56 27.52 27.52 0 0 0 24.32 10.88 26.24 26.24 0 0 0 17.28-5.12 16 16 0 0 0 6.4-12.8 13.44 13.44 0 0 0-6.4-11.52A64 64 0 0 0 315.52 640a122.24 122.24 0 0 1-49.92-21.12 44.16 44.16 0 0 1-9.6-60.16 48 48 0 0 1 23.04-18.56 97.92 97.92 0 0 1 42.24-6.4 88.32 88.32 0 0 1 50.56 12.16 56.96 56.96 0 0 1 20.48 39.04H342.4a24.32 24.32 0 0 0-8.32-16.64A24.96 24.96 0 0 0 320 566.4a20.48 20.48 0 0 0-14.08 3.84 10.88 10.88 0 0 0-4.48 9.6 7.04 7.04 0 0 0 3.84 7.04 38.4 38.4 0 0 0 17.92 6.4 234.88 234.88 0 0 1 49.92 15.36 54.4 54.4 0 0 1 21.76 19.2 44.16 44.16 0 0 1 7.04 25.6 58.88 58.88 0 0 1-9.6 28.16z m197.12-12.8a69.12 69.12 0 0 1-28.8 30.08 92.16 92.16 0 0 1-48 10.88 103.04 103.04 0 0 1-48-9.6 74.88 74.88 0 0 1-31.36-28.8 101.76 101.76 0 0 1-11.52-50.56A81.92 81.92 0 0 1 512 531.2a91.52 91.52 0 0 1 64 23.04 87.68 87.68 0 0 1 23.04 64 110.72 110.72 0 0 1-10.88 49.92z m204.16 37.76H742.4l-64-94.72V704h-50.56V534.4h49.92l64 95.36V534.4h49.92z"
              p-id="3326"></path></svg>
          <span>{{$t('message.process.import')}}</span>
        </div>
        <div class="devider" />-->
        <div
          v-if="!workflowIsExecutor"
          :title="$t('message.process.run')"
          class="button"
          @click="workflowRun">
          <span
            class="icon fi-play"
            style="padding-top:2px;"></span>
          <span>{{$t('message.process.run')}}</span>
        </div>
        <div
          v-if="workflowIsExecutor"
          :title="$t('message.process.stop')"
          class="button"
          @click="workflowStop">
          <span
            class="icon fi-stop"
            style="padding-top:2px; color: red;"></span>
          <span>{{$t('message.process.stop')}}</span>
        </div>
        <div class="devider" />
        <div
          :title="$t('message.process.save')"
          class="button"
          @click="showSaveView">
          <svg
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="4564"><path
              d="M176.64 1024c-97.28 0-176.64-80.384-176.64-178.688V178.688c0-98.816 79.36-178.688 176.64-178.688h670.72c97.28 0 176.64 80.384 176.64 178.688V845.312c0 98.816-79.36 178.688-176.64 178.688h-670.72z m0-936.96c-50.688 0-91.648 41.472-91.648 92.16V845.312c0 50.688 40.96 92.16 91.648 92.16h670.72c50.688 0 91.648-41.472 91.648-92.16V178.688c0-50.688-40.96-92.16-91.648-92.16h-3.584v437.248h-663.04v-437.248h-4.096z m581.632 350.208v-350.208h-492.544v350.208h492.544z m-160.768-35.328v-240.128h84.992v240.128h-84.992z"
              p-id="4565"></path></svg>
          <span>{{$t('message.process.save')}}</span>
        </div>
      </template>
      <template v-if="!myReadonly && type==='subFlow'">
        <div
          class="button"
          :title="$t('message.process.params')"
          @click="showParamView">
          <svg
            ref="paramButton"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="1275"><path
              d="M192.402 273.2c-69.475 0-126 56.525-126 126s56.525 126 126 126 126-56.525 126-126-56.525-126-126-126zM192.402 448.4c-27.129 0-49.2-22.071-49.2-49.2s22.071-49.2 49.2-49.2 49.2 22.071 49.2 49.2-22.075 49.2-49.2 49.2z"
              fill=""
              p-id="1276"></path><path
              d="M192.402 242c23.861 0 43.2-19.339 43.2-43.2v-92.4c0-23.861-19.339-43.2-43.2-43.2s-43.2 19.339-43.2 43.2v92.4c0 23.861 19.339 43.2 43.2 43.2z"
              fill=""
              p-id="1277"></path><path
              d="M192.402 557.6c-23.861 0-43.2 19.339-43.2 43.2v319.2c0 23.861 19.339 43.2 43.2 43.2s43.2-19.339 43.2-43.2v-319.2c0-23.861-19.344-43.2-43.2-43.2z"
              fill=""
              p-id="1278"></path><path
              d="M516.224 389.216c-69.471 0-125.995 56.525-125.995 126s56.52 126 125.995 126c69.48 0 126-56.52 126-126s-56.52-126-126-126zM516.224 564.416c-27.125 0-49.195-22.071-49.195-49.2s22.071-49.2 49.195-49.2c27.134 0 49.2 22.071 49.2 49.2s-22.066 49.2-49.2 49.2z"
              fill=""
              p-id="1279"></path><path
              d="M516.229 356c23.856 0 43.2-19.339 43.2-43.2v-207.6c0-23.861-19.339-43.2-43.2-43.2-23.861 0-43.2 19.339-43.2 43.2v207.6c0 23.861 19.344 43.2 43.2 43.2z"
              fill=""
              p-id="1280"></path><path
              d="M516.229 676.4c-23.861 0-43.2 19.339-43.2 43.2v199.2c0 23.861 19.339 43.2 43.2 43.2 23.856 0 43.2-19.339 43.2-43.2v-199.2c0-23.861-19.339-43.2-43.2-43.2z"
              fill=""
              p-id="1281"></path><path
              d="M840.061 499.083c-69.48 0-126 56.52-126 126 0 69.475 56.52 125.995 126 125.995s126-56.52 126-125.995c0-69.48-56.525-126-126-126zM840.061 674.278c-27.129 0-49.2-22.071-49.2-49.195 0-27.129 22.071-49.2 49.2-49.2s49.2 22.071 49.2 49.2c0 27.125-22.071 49.195-49.2 49.195z"
              fill=""
              p-id="1282"></path><path
              d="M840.061 466.4c23.861 0 43.2-19.339 43.2-43.2v-318c0-23.861-19.339-43.2-43.2-43.2s-43.2 19.339-43.2 43.2v318c0 23.861 19.339 43.2 43.2 43.2z"
              fill=""
              p-id="1283"></path><path
              d="M840.061 786.8c-23.861 0-43.2 19.339-43.2 43.2v88.8c0 23.861 19.339 43.2 43.2 43.2s43.2-19.339 43.2-43.2v-88.8c0-23.861-19.344-43.2-43.2-43.2z"
              fill=""
              p-id="1284"></path></svg>
          <span>{{$t('message.process.params')}}</span>
        </div>
        <div class="devider" />
        <div
          class="button"
          :title="$t('message.process.resource')"
          @click="showResourceView">
          <svg
            ref="resourceButton"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="2276"
            width="16"
            height="16">
            <path
              d="M111.395066 570.932204l87.488587 0 0 255.842922-87.488587 0 0-255.842922Z"
              p-id="2277"></path>
            <path
              d="M825.115324 570.932204l87.488587 0 0 255.842922-87.488587 0 0-255.842922Z"
              p-id="2278"></path>
            <path
              d="M111.395066 826.775126l801.208844 0 0 87.866187-801.208844 0 0-87.866187Z"
              p-id="2279"></path>
            <path
              d="M563.245128 722.759121 463.476867 722.759121 463.476867 287.760866 320.414567 429.16234 256.453836 365.201609 512.296759 109.358687 768.138658 365.201609 704.178951 429.16234 561.710169 290.830785Z"
              p-id="2280"></path>
          </svg>
          <span>{{$t('message.process.resource')}}</span>
        </div>
        <div class="devider" />
        <!-- <div
          :title="$t('message.process.import')"
          class="button"
          @click="showImportJsonView">
          <svg
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="3325"><path
              d="M928.64 181.76l-128-128A192 192 0 0 0 683.52 0H231.68a71.68 71.68 0 0 0-69.76 72.96v352h71.68V86.4a14.08 14.08 0 0 1 14.08-14.72h434.56v232.96H905.6V937.6a14.72 14.72 0 0 1-14.08 14.72H247.68a14.72 14.72 0 0 1-14.08-14.72V832h-71.68v119.04a71.68 71.68 0 0 0 69.76 72.96h676.48a71.68 71.68 0 0 0 69.76-72.96V329.6a272.64 272.64 0 0 0-49.28-147.84z m-188.8 57.6V93.44l9.6 8.32 128 128a64 64 0 0 1 7.68 10.24z m58.24 202.24H85.76a40.32 40.32 0 0 0-39.68 40.32v274.56a40.32 40.32 0 0 0 39.68 40.32h712.32a40.32 40.32 0 0 0 39.68-40.32V481.92a40.32 40.32 0 0 0-39.68-40.32zM512 571.52a31.36 31.36 0 0 0-25.6 11.52 64 64 0 0 0-9.6 37.12 57.6 57.6 0 0 0 9.6 37.12 30.72 30.72 0 0 0 26.24 11.52 31.36 31.36 0 0 0 26.88-11.52 64 64 0 0 0 9.6-39.68 53.76 53.76 0 0 0-10.24-35.2A34.56 34.56 0 0 0 512 571.52z m-290.56 56.32a133.12 133.12 0 0 1-5.76 44.8 55.68 55.68 0 0 1-20.48 26.24 71.04 71.04 0 0 1-40.32 10.24 94.72 94.72 0 0 1-43.52-5.12 49.92 49.92 0 0 1-22.4-20.48 84.48 84.48 0 0 1-9.6-33.28L128 640a46.72 46.72 0 0 0 0 16 19.2 19.2 0 0 0 6.4 8.96 12.8 12.8 0 0 0 8.96 0 14.72 14.72 0 0 0 12.8-6.4 40.32 40.32 0 0 0 8.96-18.56V534.4h53.12v93.44z m172.8 53.76a64 64 0 0 1-25.6 21.12 110.72 110.72 0 0 1-41.6 7.04 84.48 84.48 0 0 1-60.8-16.64 64 64 0 0 1-19.2-42.88h50.56a51.84 51.84 0 0 0 6.4 18.56 27.52 27.52 0 0 0 24.32 10.88 26.24 26.24 0 0 0 17.28-5.12 16 16 0 0 0 6.4-12.8 13.44 13.44 0 0 0-6.4-11.52A64 64 0 0 0 315.52 640a122.24 122.24 0 0 1-49.92-21.12 44.16 44.16 0 0 1-9.6-60.16 48 48 0 0 1 23.04-18.56 97.92 97.92 0 0 1 42.24-6.4 88.32 88.32 0 0 1 50.56 12.16 56.96 56.96 0 0 1 20.48 39.04H342.4a24.32 24.32 0 0 0-8.32-16.64A24.96 24.96 0 0 0 320 566.4a20.48 20.48 0 0 0-14.08 3.84 10.88 10.88 0 0 0-4.48 9.6 7.04 7.04 0 0 0 3.84 7.04 38.4 38.4 0 0 0 17.92 6.4 234.88 234.88 0 0 1 49.92 15.36 54.4 54.4 0 0 1 21.76 19.2 44.16 44.16 0 0 1 7.04 25.6 58.88 58.88 0 0 1-9.6 28.16z m197.12-12.8a69.12 69.12 0 0 1-28.8 30.08 92.16 92.16 0 0 1-48 10.88 103.04 103.04 0 0 1-48-9.6 74.88 74.88 0 0 1-31.36-28.8 101.76 101.76 0 0 1-11.52-50.56A81.92 81.92 0 0 1 512 531.2a91.52 91.52 0 0 1 64 23.04 87.68 87.68 0 0 1 23.04 64 110.72 110.72 0 0 1-10.88 49.92z m204.16 37.76H742.4l-64-94.72V704h-50.56V534.4h49.92l64 95.36V534.4h49.92z"
              p-id="3326"></path></svg>
          <span>{{$t('message.process.import')}}</span>
        </div> -->
        <div
          v-if="!workflowIsExecutor"
          :title="$t('message.process.run')"
          class="button"
          @click="workflowRun">
          <span
            class="icon fi-play"
            style="padding-top:2px;"></span>
          <span>{{$t('message.process.run')}}</span>
        </div>
        <div
          v-if="workflowIsExecutor"
          :title="$t('message.process.stop')"
          class="button"
          @click="workflowStop">
          <span
            class="icon fi-stop"
            style="padding-top:2px; color: red;"></span>
          <span>{{$t('message.process.stop')}}</span>
        </div>
        <div class="devider" />
        <div
          :title="$t('message.process.save')"
          class="button"
          @click="showSaveView">
          <svg
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="4564"><path
              d="M176.64 1024c-97.28 0-176.64-80.384-176.64-178.688V178.688c0-98.816 79.36-178.688 176.64-178.688h670.72c97.28 0 176.64 80.384 176.64 178.688V845.312c0 98.816-79.36 178.688-176.64 178.688h-670.72z m0-936.96c-50.688 0-91.648 41.472-91.648 92.16V845.312c0 50.688 40.96 92.16 91.648 92.16h670.72c50.688 0 91.648-41.472 91.648-92.16V178.688c0-50.688-40.96-92.16-91.648-92.16h-3.584v437.248h-663.04v-437.248h-4.096z m581.632 350.208v-350.208h-492.544v350.208h492.544z m-160.768-35.328v-240.128h84.992v240.128h-84.992z"
              p-id="4565"></path></svg>
          <span>{{$t('message.process.save')}}</span>
        </div>
      </template>
    </vueProcess>
    <div
      class="process-module-param"
      v-clickoutside="handleOutsideClick"
      v-show="isParamModalShow">
      <div class="process-module-param-modal-header">
        <h3>{{ isResourceShow ? $t('message.process.resource') : $t('message.process.params') }}{{$t('message.process.seting')}}</h3>
      </div>
      <div class="process-module-param-modal-content">
        <argument
          v-show="!isResourceShow"
          :props="props"
          @change-props="onPropsChange"></argument>
        <resource
          v-show="isResourceShow"
          :resources="resources"
          :flow-name="name"
          @update-resources="updateResources"></resource>
      </div>
    </div>
    <div
      class="process-module-param"
      v-clickoutside="handleOutsideClickNode"
      v-show="nodebaseinfoShow"
      @click="clickBaseInfo">
      <div class="process-module-param-modal-header">
        <h3>{{$t('message.process.baseInfo')}}</h3>
      </div>
      <div class="process-module-param-modal-content">
        <nodeParameter
          :node-data="clickCurrentNode"
          :flow-name="name"
          :readonly="myReadonly"
          :nodes="json && json.nodes"
          @saveNode="saveNode"
          @paramsChange="paramsChange"
        ></nodeParameter>
      </div>
    </div>
    <Modal
      width="450"
      v-model="saveModal">
      <div
        class="process-module-title"
        slot="header">
        {{$t('message.process.save')}}
      </div>
      <Form
        ref="formSave"
        :model="saveModel"
        :label-width="85" >
        <FormItem
          :label="$t('message.newConst.comment')"
          prop="comment"
          :rules="[{ required: true, message: $t('message.process.inputComment') },{message: $t('message.process.commentLengthLimit'), max: 255}]">
          <Input
            v-model="saveModel.comment"
            type="textarea"
            :placeholder="$t('message.process.inputComment')"
            style="width: 300px;" />
        </FormItem>
      </Form>
      <div slot="footer">
        <Button
          type="primary"
          @click="save">{{$t('message.process.confirmSave')}}</Button>
      </div>
    </Modal>
    <Modal
      v-model="importModal">
      <div
        class="process-module-title"
        slot="header">
        {{$t('message.process.importJson')}}
      </div>
      <Upload
        ref="uploadJson"
        type="drag"
        multiple
        :before-upload="handleUpload"
        :format="[json]"
        :max-size="2001000"
        action="">
        <div style="padding: 20px 0">
          <Icon
            type="ios-cloud-upload"
            size="52"
            style="color: #3399ff"></Icon>
          <p>{{$t('message.process.uplaodJson')}}</p>
        </div>
      </Upload>
      <div v-show="importModel.names.length">
        <div
          v-for="filename in importModel.names"
          :key="filename">
          {{ filename }}
        </div>
      </div>
      <div slot="footer">
        <Button
          type="primary"
          @click="importJSON">{{$t('message.process.confirmImport')}}</Button>
      </div>
    </Modal>
    <Spin
      v-if="loading"
      size="large"
      fix/>
    <Modal
      v-model="repetitionNameShow"
      :title="$t('message.process.nodeNameNotice')"
      class="repetition-name">
      {{$t('message.process.repeatNode')}}{{ repeatTitles.join(', ') }}?
      <div slot="footer">
        <Button
          type="primary"
          @click="repetitionName">{{$t('message.newConst.ok')}}</Button>
      </div>
    </Modal>
    <associate-script
      ref="associateScript"
      @click="associateScript"/>
    <Modal
      :title="addNodeTitle"
      v-model="addNodeShow"
    >
      <Form
        label-position="left"
        :label-width="110"
        ref="addFlowfoForm"
        :model="clickCurrentNode">
        <FormItem
          :label="$t('message.process.nodeName')"
          prop="title"
          :rules="[{required: true, message: $t('message.process.inputNodeName'), trigger: 'blur'},{
            type: 'string',
            pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
            message: $t('message.newConst.validNameDesc'),
            trigger: 'change',
          },{validator: validatorName, trigger: 'blur'},{
            min: 1,
            max: 128,
            message: $t('message.process.lengthLimit'),
            trigger: 'change',
          }]">
          <Input
            v-model="clickCurrentNode.title"
            :placeholder="$t('message.process.inputNodeName')"/>
        </FormItem>
        <FormItem :label="$t('message.process.nodeDesc')">
          <Input
            v-model="clickCurrentNode.desc"
            type="textarea"
            :placeholder="$t('message.process.inputNodeDesc')"/>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button
          type="text"
          size="large"
          @click="addFlowCancel">{{$t('message.newConst.cancel')}}</Button>
        <Button
          type="primary"
          size="large"
          @click="addFlowOk">{{$t('message.newConst.ok')}}</Button>
      </div>
    </Modal>
    <template
      v-for="item in json && json.nodes">
      <console
        v-if="openningNode && item.createTime === openningNode.createTime"
        :key="item.createTime"
        :node="item"
        :stop="workflowIsExecutor"
        class="process-console"
        :style="{'left': shapeWidth + 'px', 'width': `calc(100% - ${shapeWidth}px)`}"
        @close-console="closeConsole(item)"></console>
    </template>
  </div>
</template>
<script>
import argument from './component/arguments.vue';
import resource from './component/resource.vue';
import nodeParameter from './component/nodeparameter.vue';
import vueProcess from '@/js/component/vue-process';
import console from './component/console.vue';
import shapes from './shape.js';
import api from '@/js/service/api';
import clickoutside from '@js/helper/clickoutside';
import associateScript from './component/associateScript.vue';
import { throttle, isEmpty } from 'lodash';
import { NODETYPE, ext } from '@/js/service/nodeType';
import { setTimeout, clearTimeout } from 'timers';
export default {
  components: {
    vueProcess,
    argument,
    resource,
    nodeParameter,
    associateScript,
    console,
  },
  directives: {
    clickoutside,
  },
  props: {
    flowId: {
      type: [String, Number],
      default: '',
    },
    version: {
      type: [String, Number],
      default: '',
    },
    readonly: {
      type: String,
      default: 'false',
    },
    projectVersionID: {
      type: [String, Number],
      default: '',
    },
    importReplace: {
      type: Boolean,
      default: false,
    },
    openFiles: {
      type: Object,
      default: () => {},
    },
    tabs: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      // 是否为父工作流
      isRootFlow: true,
      name: '',
      versionId: '',
      // 0 未发布过 1发布过
      state: '',
      shapes,
      // 原始数据
      originalData: null,
      // 插件返回的json数据
      json: null,
      // 工作流级别的参数
      props: [],
      // 工作流级别的资源
      resources: [],
      // 是否显示保存模态框
      saveModal: false,
      saveModel: {
        comment: '',
      },
      // 控制参数模态框是否显示
      isParamModalShow: false,
      isResourceShow: false,
      // 是否显示导入JSON视图
      importModal: false,
      importModel: {
        names: [],
        files: [],
        jsons: [],
      },
      // 是否有改变
      jsonChange: false,
      loading: false,
      ops: [], // 记录子工作流操作
      repetitionNameShow: false,
      repeatTitles: [],
      nodebaseinfoShow: false, // 自定义节点信息弹窗展示
      clickCurrentNode: {}, // 当前点击的节点
      timerClick: '',

      viewOptions: {
        showBaseInfoOnAdd: false, // 不显示默认的拖拽添加节点弹出的基础信息面板
        shapeView: true, // 左侧shape列表
        control: true,
      },
      paramsIsChange: false, // 判断参数是否有在做操作改变，是否自动保存
      addNodeShow: false, // 创建节点的弹窗显示
      cacheNode: null,
      isDelete: false,
      addNodeTitle: '创建子工作流', // 创建节点时弹窗的title
      IframeProjectLoading: false,
      nodeBasicInfoData: [], // 后台返回的节点基本信息
      workflowIsExecutor: false, // 当前工作流是否再执行
      openningNode: null, // 上一次打开控制台的节点
      shapeWidth: 0, // 流程图插件左侧工具栏的宽度
      workflowExeteId: '',
      excuteTimer: ''
    };
  },
  computed: {
    myReadonly() {
      if (this.readonly === 'true') {
        return true;
      } else if (this.readonly === 'false') {
        return false;
      } else {
        return false;
      }
    },
    type() {
      return !this.isRootFlow ? 'subFlow' : 'flow'; // flow工作流， subFlow子工作流
    },
    nodeMenuOptions() {
      return {
        defaultMenu: {
          config: false, // 不展示默认的基础信息菜单项
          param: false, // 不展示默认的参数配置菜单项
          copy: false,
          delete: !this.workflowIsExecutor
        },
        userMenu: [],
        beforeShowMenu: (node, arr, type) => {
          // type : 'node' | 'link' | 'view' 分别是节点右键，边右键，画布右键
          // 如果有runState说明已经执行过
          if (node && node.runState) {
            if (node.runState.showConsole && node.runState.taskID) {
              arr.push({
                text: this.$t('message.process.console'),
                value: 'console',
                img: require('./images/menu/xitongguanlitai.svg'),
              })
            }
          }
          if (!this.workflowIsExecutor) {
            if (type === 'node') {
              if ([NODETYPE.SPARKSQL, NODETYPE.HQL, NODETYPE.SPARKPY, NODETYPE.SCALA, NODETYPE.JDBC].includes(node.type)) {
                arr.push({
                  text: this.$t('message.process.associate'),
                  value: 'associate',
                  img: require('./images/menu/associate.svg'), // 图标资源文件，也可以通过icon配置内置字体文件支持的className
                });
              }
              arr.push({
                text: this.$t('message.process.relySelect'),
                value: 'relySelect',
                img: require('./images/menu/flow.svg'),
              });
              if (node.enableCopy) {
                arr.push({
                  text: this.$t('message.newConst.copy'),
                  value: 'mycopy',
                  img: require('./images/menu/fuzhi.svg'),
                });
              }
            }
          }
          if (type === 'view') {
            arr.push({
              text: this.$t('message.newConst.paste'),
              value: 'mypaste',
              img: require('./images/menu/zhantie.svg'),
            });
            arr.push({
              text: this.$t('message.process.allDelete'),
              value: 'allDelete',
              img: require('./images/menu/delete.svg'),
            });
          }
          return arr;
        }
      }
    }
  },
  watch: {
    jsonChange(val) {
      this.$emit('isChange', val);
    }
  },
  mounted() {
    // 基础信息
    this.getBaseInfo();
    this.setShapes();
    if (!this.myReadonly) {
      this.timer = setInterval(() => {
        if (this.jsonChange && !this.nodebaseinfoShow) { // paramsIsChange参数是否在编辑操作
          this.autoSave('自动保存', true);
        }
      }, 1000 * 60 * 5);
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    if (this.excuteTimer) {
      clearTimeout(this.excuteTimer);
    }
  },
  methods: {
    getBaseInfo() {
      this.loading = true;
      this.getCache().then((json) => {
        this.getOriginJson();
      });
    },
    init(json) {
      if (json) {
        this.originalData = this.json = JSON.parse(JSON.stringify(json));
        this.resources = json.resources;
        this.props = json.props;
      }
      this.$nextTick(() => {
        this.init = true;
        this.loading = false;
      });
    },
    getOriginJson() {
      api.fetch('/dss/get', {
        id: this.flowId,
        version: this.version,
        projectVersionID: +this.projectVersionID,
      }, 'get').then((res) => {
        let json = this.convertJson(res.flow)
        this.init(json);
      }).catch(() => {
        this.loading = false;
      });
    },
    convertJson(flow) {
      this.name = flow.name;
      this.state = flow.state;
      this.isRootFlow = flow.rootFlow;
      this.rank = flow.rank; // 工作流层级
      let json;
      // 没传version则默认取最新的
      if (!this.version) {
        json = flow.latestVersion ? flow.latestVersion.json : '';
        this.versionId = flow.latestVersion ? flow.latestVersion.id : '';
      } else {
        json = flow.flowVersions[0] ? flow.flowVersions[0].json : '';
        this.versionId = flow.flowVersions[0] ? flow.flowVersions[0].id : '';
      }
      // JSON:  优先缓存 > 通过id去查关联JSON数据
      if (json) {
        json = JSON.parse(json);
        if (Array.isArray(json.nodes)) {
          // 需要把 id -> key, jobType -> type
          json.nodes.forEach((node) => {
            // node.key = node.id;
            node.type = node.jobType;
            delete node.jobType;
            node = this.bindNodeBasicInfo(node);
          });
        }
      }
      return json;
    },
    setShapes() {
      const projectList = this.getProjectList();
      let params = '';
      projectList.forEach((item) => {
        if (!params) {
          params = `?application=${item.name}`;
        } else {
          params += `&application=${item.name}`;
        }
      });
      api.fetch(`/dss/listNodeType${params}`, 'get').then((res) => {
        const nodeArray = [];
        Object.values(res.nodeTypes).map((item) => {
          item.map((subItem) => {
            nodeArray.push(subItem);
          });
        });
        this.nodeBasicInfoData = nodeArray;
        this.shapes = this.shapes.map((item) => {
          item.children = item.children.filter((subItem) => nodeArray.some((nodeType) => nodeType.nodeType === subItem.type));
          return item;
        });
      });
    },
    change(obj) {
      this.json = obj;
      if (this.init) {
        this.heartBeat();
        this.jsonChange = true;
      }
    },
    initNode(arg) {
      arg = this.bindNodeBasicInfo(arg);
      this.clickCurrentNode = JSON.parse(JSON.stringify(arg));
      if ([NODETYPE.DISPLAY, NODETYPE.DASHBOARD].includes(this.clickCurrentNode.type)) {
        // 如果是已经创建过的，一定是在jobContent里保存有值得
        if (!this.clickCurrentNode.jobContent || !this.clickCurrentNode.jobContent.id) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            id: ''
          });
        }
      }
      if (this.clickCurrentNode.jobContent && this.clickCurrentNode.jobContent.jobParams) {
        const defaultValueAction = (object) => {
          const numberArray = {
            'maxReceiveHours': 12,
            'maxCheckHours': 1,
            'queryFrequency': 10,
            'timeScape': 24,
            'spark-driver-memory': 2,
            'spark-executor-memory': 3,
            'spark-executor-cores': 1,
            'spark-executor-instances': 2,
          };
          for (const key in object) {
            if (object.hasOwnProperty(key)) {
              if (Object.keys(numberArray).includes(key)) {
                if (object[key]) {
                  object[key] = Number(object[key]);
                } else {
                  object[key] = numberArray[key];
                }
              }
            }
          }
          return object;
        };
        this.clickCurrentNode.jobContent.jobParams = defaultValueAction(this.clickCurrentNode.jobContent.jobParams);
      }
      if (!(this.clickCurrentNode.jobContent) || !this.clickCurrentNode.jobContent.jobParams) {
        if (this.clickCurrentNode.type === NODETYPE.EVENTCHECKERW) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            jobParams: {
              'msgType': 'RECEIVE',
              'msgReceiver': `${this.$route.query.projectName}@${this.name}@${this.clickCurrentNode.title}`,
              'msgTopic': '',
              'msgName': '',
              'queryFrequency': 10,
              'maxReceiveHours': 12,
              'msgSavekey': 'msg.body',
              'onlyReceiveToday': 'true',
            },
          });
        } else if (this.clickCurrentNode.type === NODETYPE.EVENTCHECKERF) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            jobParams: {
              'msgType': 'SEND',
              'msgSender': `${this.$route.query.projectName}@${this.name}@${this.clickCurrentNode.title}`,
              'msgTopic': '',
              'msgName': '',
              'msgBody': '',
            },
          });
        } else if (this.clickCurrentNode.type === NODETYPE.DATACHECKER) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            jobParams: {
              'sourceType': 'job',
              'checkObject': '',
              'maxCheckHours': 1,
              'jobDesc': '',
            },
          });
        } else if (this.clickCurrentNode.type === NODETYPE.RMBSENDER) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            jobParams: {
              'rmbTargetDcn': '',
              'rmbServiceld': '',
              'rmbEnvironment': '',
              'rmbMessageType': '',
              'rmbMessage': '',
            },
          });
        } else if ([NODETYPE.SPARKSQL, NODETYPE.SPARKPY, NODETYPE.SCALA].includes(this.clickCurrentNode.type)) {
          const timer = setTimeout(() => {
            this.loading = true;
          }, 2000);
          Promise.all([api.fetch('/configuration/getFullTreesByAppName', {
            appName: 'spark',
            creator: 'IDE',
          }, 'get'),
          api.fetch('/configuration/getFullTreesByAppName', {
            appName: '通用设置',
            creator: '通用设置',
          }, 'get')])
            .then((rst) => {
              clearTimeout(timer);
              this.loading = false;
              const settings = rst[0].fullTree[0].settings;
              let jobParams = {
                'spark-driver-memory': 2,
                'spark-executor-memory': 3,
                'spark-executor-cores': 1,
                'spark-executor-instances': 2,
                'wds-linkis-yarnqueue': '',
              };
              if (settings.length > 0) {
                settings.forEach((item) => {
                  if (item.key === 'spark.driver.cores') {
                    jobParams['spark-driver-memory'] = +item.value || +item.defaultValue;
                  } else if (item.key === 'spark.executor.memory') {
                    jobParams['spark-executor-memory'] = +item.value || +item.defaultValue;
                  } else if (item.key === 'spark.executor.cores') {
                    jobParams['spark-executor-cores'] = +item.value || +item.defaultValue;
                  } else if (item.key === 'spark.executor.instances') {
                    jobParams['spark-executor-instances'] = +item.value || +item.defaultValue;
                  }
                });
                jobParams['wds-linkis-yarnqueue'] = rst[1].fullTree[0].settings[0].value || rst[1].fullTree[0].settings[0].defaultValue;
              }
              if (this.clickCurrentNode.jobContent) {
                this.$set(this.clickCurrentNode.jobContent, 'jobParams', jobParams);
              } else {
                this.$set(this.clickCurrentNode, 'jobContent', {
                  jobParams,
                });
              }
            }).catch(() => {
              this.loading = false;
            });
        } else if (this.clickCurrentNode.type === NODETYPE.HQL) {
          if (this.clickCurrentNode.jobContent) {
            this.$set(this.clickCurrentNode.jobContent, 'jobParams', {
              'wds-linkis-yarnqueue': '',
            });
          } else {
            this.$set(this.clickCurrentNode, 'jobContent', {
              jobParams: {
                'wds-linkis-yarnqueue': '',
              },
            });
          }
        } else if (this.clickCurrentNode.type === NODETYPE.SENDMAIL) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            jobParams: {
              'category': '',
              'subject': '',
              'content': '',
              'to': '',
              'cc': '',
              'bcc': ''
            },
          });
        } else if (this.clickCurrentNode.type === NODETYPE.QUALITIS) {
          this.$set(this.clickCurrentNode, 'jobContent', {
            ...this.clickCurrentNode.jobContent,
            jobParams: {
              'filter': '',
              'executeUser': ''
            },
          });
        } else if (this.clickCurrentNode.type === NODETYPE.JDBC) {
          if (this.clickCurrentNode.jobContent) {
            this.$set(this.clickCurrentNode.jobContent, 'jobParams', {
              'jdbcUrl': '',
              'jdbcUsername': '',
              'jdbcPassword': ''
            });
          } else {
            this.$set(this.clickCurrentNode, 'jobContent', {
              jobParams: {
                'jdbcUrl': '',
                'jdbcUsername': '',
                'jdbcPassword': ''
              },
            });
          }
        }
      }
      // 节点参数位置发生改变，先定义一个新的configuration用来存储数据，后面把jobparams干掉
      if (!this.clickCurrentNode.params || isEmpty(this.clickCurrentNode.params.configuration)) {
        // 有params可能是spark节点的variable参数
        this.$set(this.clickCurrentNode, 'params', {
          configuration: {
            special: {},
            runtime: {},
            startup: {}
          }
        })
      }
    },
    click(arg) {
      clearTimeout(this.timerClick);
      this.timerClick = setTimeout(() => {
        if (this.workflowIsExecutor) return;
        this.initNode(arg);
        this.nodebaseinfoShow = true;
        this.$emit('node-click', arg);
      }, 200);
    },
    dblclick(...arg) {
      arg[0] = this.bindNodeBasicInfo(arg[0]);
      // 由后台控制是否支持跳转
      if (!arg[0].supportJump) return;
      clearTimeout(this.timerClick);
      // 增加父工作流id,以便关闭对应工作流下的所有节点
      arg[0].parentFlowID = +this.flowId;
      if (arg[0].type === NODETYPE.FLOW) { // 如果是工作流双击将弹窗提示保存
        let flowId = arg[0].jobContent ? arg[0].jobContent.embeddedFlowId : '';
        if (!flowId) {
          this.addNodeShow = true;
          this.clickCurrentNode = JSON.parse(JSON.stringify(arg[0]));
          this.addNodeTitle = this.$t('message.process.createNode');
        } else {
          this.$emit('node-dblclick', arg);
        }
      } else if ([NODETYPE.DISPLAY, NODETYPE.DASHBOARD].includes(arg[0].type)) {
      // 执行过程中只有子工作流可双击打开
        if (this.workflowIsExecutor) return;
        let commonIframeId = arg[0].jobContent ? arg[0].jobContent.id : '';
        if (!commonIframeId) {
          this.addNodeShow = true;
          this.clickCurrentNode = JSON.parse(JSON.stringify(arg[0]));
          this.addNodeTitle = this.$t('message.process.createNode');
        } else {
          this.$emit('node-dblclick', arg);
        }
      } else {
        if (this.workflowIsExecutor) return;
        this.$emit('node-dblclick', arg);
      }
    },
    message(obj) {
      let type = {
        warning: 'warn',
        error: 'error',
        info: 'info',
      };
      this.$Message[type[obj.type]]({
        content: obj.msg,
        duration: 2,
      });
    },
    saveNodeBaseInfo(arg) {
      this.$emit('saveBaseInfo', arg);
      // 如果是可编辑脚本得改变打开的脚本得名称
      this.dispatch('Workbench:updateFlowsNodeName', arg);
      // 当保存子流程节点的基础信息时，如果子流程节点没有 embeddedFlowId:"flow_id" 则先创建子流程节点
      let node = arg;
      let flage = false; // 避免创建子工作流有两个提示
      if (node.type == NODETYPE.FLOW) {
        if (this.rank >= 4) {
          return this.$Message.warning(this.$t('message.process.rankLimit'));
        }

        //  节点原始数据
        if (!node.jobContent) {
          node.jobContent = {};
        }
        // 如果子流程节点的node.jobContent.embeddedFlowId为空表明还未子流程还未创建生成flowID
        const reg = /^[a-zA-Z][a-zA-Z0-9_]*$/;
        if (!node.title.match(reg)) {
          return this.$Message.warning(this.$t('message.newConst.validNameDesc'));
        }
        if (!node.jobContent.embeddedFlowId) {
          flage = true;
          // 调用接口创建
          api.fetch('/dss/addFlow', {
            name: node.title,
            description: node.desc,
            parentFlowID: Number(this.flowId),
            projectVersionID: +this.projectVersionID,
          }).then((res) => {
            this.$Notice.success({
              desc: this.$t('message.process.createSubSuccess'),
            });
            node.jobContent.embeddedFlowId = res.flow.id;
            this.ops.push({
              id: res.flow.id,
              nodeType: node.type,
              op: 'add',
              params: {
                name: node.title,
                description: node.desc,
              },
            });
          });
        } else {
          // 更新工作流是记录操作,有相同替换，没相同追加
          let isSame = false;
          this.ops = this.ops.map((item) => {
            if (item.subsubFlowID === node.jobContent.embeddedFlowId && item.op == 'update') {
              item.params.name = node.title;
              item.params.description = node.desc;
              isSame = true;
            }
            return item;
          });
          if (!isSame) {
            this.ops.push({
              id: node.jobContent.embeddedFlowId,
              nodeType: node.type,
              op: 'update',
              params: {
                name: node.title,
                description: node.desc,
              },
            });
          }
        }
      }
      // iframe节点
      this.saveCommonIframe(arg, () => {
        flage = true;
      });
      // 为了表单校验，基础信息弹窗保存的节点已不再是响应式，需重新赋值给json

      this.json.nodes = this.json.nodes.map((item) => {
        if (item.createTime === node.createTime && item.key === node.key) {
          item.title = node.title;
          item.desc = node.desc;
          item.jobContent = node.jobContent;
          item.resources = node.resources || [];
          item.params = node.params; // 节点参数现在存在这里，和jobparams一样
        }
        return item;
      });
      this.originalData = this.json;
      this.jsonChange = true;
      if (!flage) return this.$Message.success(this.$t('message.process.saveParamsNotice'));
    },
    saveNodeParam(...arg) {
      this.$emit('saveParam', arg);
    },
    /**
         * 显示导入JSON视图
         */
    showImportJsonView() {
      this.importModel.files = [];
      this.importModel.jsons = [];
      this.importModel.names = [];
      this.$refs.uploadJson.clearFiles();
      this.importModal = true;
    },
    handleUpload(file) {
      if (file.name.indexOf('.json') === -1) {
        this.$Message.warning(this.$t('message.process.uploadJson'));
        return false;
      }
      const sizeResult = file.size >= 1024 * 1024;
      if (sizeResult) return this.$Message.warning(this.$t('message.process.JsonLimit'));
      if (this.importModel.names.indexOf(file.name) < 0) {
        this.importModel.names.push(file.name);
        this.importModel.files.push(file);
      }

      return false;
    },
    readFileJson(file) {
      return new Promise((resolve) => {
        let reader = new FileReader();
        // Closure to capture the file information.
        reader.onload = (e) => {
          this.importModel.json = e.target.result;
          resolve({ name: file.name, json: e.target.result });
        };
        // Read in the image file as a data URL.
        reader.readAsText(file);
      });
    },
    importJSON() {
      if (this.importModel.files.length < 1) {
        return this.$Message.warning(this.$t('message.process.jsonFormat'));
      }
      let fp = this.importModel.files.map((f) => this.readFileJson(f));
      Promise.all(fp).then((res) => {
        let valid = true;
        res.forEach((fileItem) => {
          // 检查数据结构
          let json = {};
          try {
            json = JSON.parse(fileItem.json);
          } catch (error) {
            valid = false;
            return this.$Message.warning(`${this.$t('message.process.jsonFormat')}：${error.message}`);
          }

          for (let i = 0; i < json.edges.length; i++) {
            let edge = json.edges[i];
            if (!edge.source || !edge.target || !edge.sourceLocation || !edge.targetLocation) {
              return this.$Message.warning(this.$t('message.process.jsonEdgeNotice'));
            }
          }

          for (let i = 0; i < json.nodes.length; i++) {
            let node = json.nodes[i];
            if (!node.layout || !node.jobType || !node.id) {
              valid = false;
              return this.$Message.warning(this.$t('message.process.jsonNodeNotice'));
            } else if (node.layout) {
              let layout = node.layout;
              if (layout.x === undefined || layout.y === undefined || layout.width === undefined || layout.height === undefined) {
                valid = false;
                return this.$Message.warning(this.$t('message.process.jsonLayoutNotice'));
              }
            }
          }

          // 需要把 id -> key, jobType -> type，id即为名称
          json.nodes.forEach((node) => {
            node.key = node.id;
            node.createTime = +new Date();
            node.name = +new Date() + '' + Math.ceil(Math.random() * 1000); // 保存时使用时间戳作为文件名
            node.type = node.jobType;
            node.title = node.id;
            if (node.jobContent && node.jobContent.code) {
              node.resources = [];
            }
            node = this.bindNodeBasicInfo(node);
            delete node.id;
            delete node.jobType;
          });
          let validTypes = [];
          this.shapes.forEach((it) => {
            if (it.children) {
              validTypes = validTypes.concat(it.children.map((child) => child.type).filter((t) => t));
            }
          });
          validTypes = Array.from(new Set(validTypes));

          if (json.edges && !Array.isArray(json.edges)) {
            valid = false;
            return this.$Message.warning(this.$t('message.process.jsonEdgesNoArray'));
          }
          if (json.nodes && !Array.isArray(json.nodes)) {
            valid = false;
            return this.$Message.warning(this.$t('message.process.jsonNodesNoArray'));
          }
          let invalidNodes = json.nodes.filter((node) => {
            return validTypes.indexOf(node.type) < 0;
          });

          if (invalidNodes.length === json.nodes.length || json.nodes.length < 1) {
            valid = false;
            return this.$Message.warning(`${this.$t('message.process.jsonNoType')}（${validTypes.join('、')}）`);
          } else {
            invalidNodes.forEach((node) => console.warn(`${this.$t('message.process.nosupprotType')}${node.type}`, node));
          }
          if (valid) {
            this.importModel.jsons.push(json);
          }
        });
        if (valid) {
          this.mergeJson();
        }
      });
    },
    mergeJson() {
      this.importModel.jsons.forEach((json) => {
        this.changeJsonData(json);
      });
      this.importModal = false;
    },
    changeJsonData(json) {
      // importReplace 多次导入是覆盖还是追加
      if (this.importReplace) {
        this.originalData = json;
      } else if (this.json) {
        // 过滤掉重复节点，重复边
        if (Array.isArray(this.json.nodes) && Array.isArray(json.nodes)) {
          json.nodes = json.nodes.filter((node) => {
            return !this.json.nodes.some((on) => {
              return on.key === node.key;
            });
          });
          json.nodes = this.json.nodes.concat(json.nodes).map((item) => {
            if (item.type === NODETYPE.CONNECTOR) {
              item.title = item.key = item.key + '_' + this.rank;
            }
            return item;
          });
        }
        if (Array.isArray(this.json.edges) && Array.isArray(json.edges)) {
          json.edges = json.edges.filter((node) => {
            return !this.json.edges.some((on) => {
              return on.source === node.source && on.target === node.target;
            });
          });
          json.edges = this.json.edges.concat(json.edges).map((item) => { // 修改空节点名称，避免全局工程下节点同名，不是很严谨，但能减少使用人员去修改重名
            if (/^CONNECTOR_/.test(item.source)) {
              item.source = item.source + '_' + this.rank;
            }
            if (/^CONNECTOR_/.test(item.target)) {
              item.target = item.target + '_' + this.rank;
            }
            return item;
          }); ;
        }
        this.json = this.originalData = json;
      } else {
        this.json = this.originalData = json;
      }
    },
    /**
         * 显示保存视图
         */
    showSaveView() {
      if (this.workflowIsExecutor) return;
      // 检查JSON
      if (!this.validateJSON()) {
        return;
      }
      this.saveModal = true;
    },
    /**
         * 保存工作流
         */
    save() {
      this.$refs.formSave.validate((valid) => {
        // 注释未输入
        if (!valid) {
          return;
        }
        // 检查当前json是否有子节点未保存
        const subArray = this.openFiles[this.name] || [];
        const changeList = this.tabs.filter((item) => {
          return subArray.includes(item.key) && item.node.isChange;
        });
        // 保存时关闭控制台
        this.openningNode = null;
        if (changeList.length > 0) {
          this.$Modal.confirm({
            title: this.$t('message.process.cancelNotice'),
            content: this.$t('message.process.noSaveHtml'),
            okText: this.$t('message.process.confirmSave'),
            cancelText: this.$t('message.newConst.cancel'),
            onOk: () => {
              this.saveModal = false;
              let json = JSON.parse(JSON.stringify(this.json));
              json.nodes.forEach((node) => {
                this.$refs.process.setNodeRunState(node.key, {
                  borderColor: '#666',
                })
              })
              this.autoSave(this.saveModel.comment, false);
            },
            onCancel: () => {
            },
          });
        } else {
          this.saveModal = false;
          let json = JSON.parse(JSON.stringify(this.json));
          json.nodes.forEach((node) => {
            this.$refs.process.setNodeRunState(node.key, {
              borderColor: '#666',
            })
          })
          this.autoSave(this.saveModel.comment, false);
        }
      });
    },
    autoSave(comment, f) {
      // 检查JSON
      if (!this.validateJSON()) {
        return false;
      }
      this.json.ops = this.ops;
      // 需要把 key -> id,  type -> jobType， title -> id
      let json = JSON.parse(JSON.stringify(this.json));
      json.edges.forEach((edge) => {
        let sources = json.nodes.filter((node) => {
          return node.key == edge.source;
        });
        if (sources.length > 0) {
          let source = sources[0];
          edge.source = source.key;
        }
        let targets = json.nodes.filter((node) => {
          return node.key == edge.target;
        });
        if (targets.length > 0) {
          let target = targets[0];
          edge.target = target.key;
        }
      });
      let flage = false;
      json.nodes.forEach((node) => {
        const reg = /^[a-zA-Z][a-zA-Z0-9_]*$/;

        if (!node.title.match(reg)) {
          return flage = true;
        }
        node.id = node.key;
        node.jobType = node.type;
        node.selected = false; // 保存之前初始化选中状态
        delete node.type;
        delete node.menu; // 删除菜单配置
        // 将用户保存的resources值为空字符串转为空数组
        if (!node.resources) {
          node.resources = [];
        }
        // 保存之前删掉执行的状态信息
        if(node.runState) {
          delete node.runState
        }

      });
      if (flage) return this.$Message.warning(this.$t('message.newConst.validNameDesc'));
      // 判断是否有空节点
      const emptyNode = json.nodes.filter((item) => ([NODETYPE.RMBSENDER, NODETYPE.DATACHECKER, NODETYPE.EVENTCHECKERW, NODETYPE.EVENTCHECKERF, NODETYPE.QUALITIS].includes(item.jobType) && (!item.jobContent || !item.jobContent.jobParams))).map((item) => item.title);

      const isFiveNode = json.nodes.filter((item) => {
        return !item.jobContent && item.jobType === NODETYPE.FLOW && this.rank >= 4;
      });
      if (isFiveNode.length > 0) return this.$Message.warning(this.$t('message.process.deleteNodeSave'));
      if (emptyNode.length > 0 && !f) { // 自动保存时不用提示
        this.$Modal.confirm({
          title: this.$t('message.process.saveAbnormal'),
          content: this.$t('message.process.emptyNodeHtml', { emptyNode: emptyNode.join(',') }),
          okText: this.$t('message.process.continueSave'),
          cancelText: this.$t('message.process.backEditor'),
          loading: true,
          onOk: () => {
            this.saveRequest(json, comment, f, this.$Modal.remove);
          },
          onCancel: () => {
          },
        });
      } else {
        return this.saveRequest(json, comment, f);
      }
    },
    // 保存请求
    saveRequest(json, comment, f, cb) {
      // 提交保存
      // 由于后台对节点参数数字类型的值，保存的都是字符串，所以转换一下
      const defaultValueAction = (object) => {
        const defaultArray = {
          'maxReceiveHours': '12',
          'maxCheckHours': '1',
          'queryFrequency': '10',
          'timeScape': '24',
          'spark-driver-memory': '2',
          'spark-executor-memory': '3',
          'spark-executor-cores': '1',
          'spark-executor-instances': '2',
          'onlyReceiveToday': 'true',
          'msgSavekey': 'msg.body',
        };
        for (const key in object) {
          if (object.hasOwnProperty(key)) {
            if (Object.keys(defaultArray).includes(key)) {
              if (object[key]) {
                object[key] = String(object[key]);
              } else {
                object[key] = defaultArray[key];
              }
            }
          }
        }
        return object;
      };
      const updateTime = Date.now();
      const paramsJson = JSON.parse(JSON.stringify(Object.assign(json, {
        comment: comment,
        type: this.type,
        updateTime,
        props: this.props,
        resources: this.resources,
      })));

      paramsJson.nodes = paramsJson.nodes.map((node) => {
        if (node.jobContent && node.jobContent.jobParams) {
          // 保存数据前添加默认值
          node.jobContent.jobParams = defaultValueAction(node.jobContent.jobParams);
        }
        return node;
      });
      return api.fetch('/dss/saveFlow', {
        id: Number(this.flowId),
        json: JSON.stringify(paramsJson),
        comment: this.saveModel.comment || comment,
        projectVersionID: +this.projectVersionID,
        ops: json.ops || [],
      }).then((res) => {
        if (!f) {
          this.$Notice.success({
            desc: this.$t('message.process.saveWorkflowCuccess'),
          });
          this.saveModel.comment = '';
        } else {
          this.$Notice.success({
            desc: this.$t('message.process.autoSaveWorkflow'),
          });
        }
        this.jsonChange = false;
        // 每次保存成功清空ops
        this.ops = [];
        if (cb) {
          cb();
        }
        // 保存成功后去更新tab的工作流数据
        this.$emit('updateWorkflowList');
        return res;
      }).catch(() => {
        if (cb) {
          cb();
        }
      });
    },
    /**
         * 显示工作流参数配置页面
         */
    showParamView() {
      if (this.workflowIsExecutor) return;
      if (!this.isResourceShow) {
        this.isParamModalShow = !this.isParamModalShow;
      } else {
        this.isParamModalShow = true;
        this.isResourceShow = false;
      }
    },
    /**
         * 显示资源导入页面
         */
    showResourceView() {
      if (this.workflowIsExecutor) return;
      if (this.isResourceShow) {
        this.isParamModalShow = !this.isParamModalShow;
      } else {
        this.isParamModalShow = true;
        this.isResourceShow = true;
      }
    },
    /**
         * 检查JSON，是否符合规范
         * @return {Boolean}
         */
    validateJSON() {
      if (!this.json) {
        this.$Modal.warning({
          title: this.$t('message.process.notice'),
          content: this.$t('message.process.dragNode'),
        });
        return false;
      }
      let edges = this.json.edges;
      let nodes = this.json.nodes;
      if (!nodes || nodes.length === 0) {
        this.$Modal.warning({
          title: this.$t('message.process.notice'),
          content: this.$t('message.process.dragNode'),
        });
        return false;
      }
      let headers = [];
      let footers = [];
      let titles = [];
      let repeatTitles = [];
      nodes.forEach((node) => {
        if (titles.includes(node.title)) {
          repeatTitles.push(node.title);
        } else {
          titles.push(node.title);
        }
        if (!edges.some((edge) => {
          return edge.target == node.key;
        })) {
          headers.push(node);
        }
        if (!edges.some((edge) => {
          return edge.source == node.key;
        })) {
          footers.push(node);
        }
      });
      // 后台会把名称当做id处理，所以名称必须唯一
      if (repeatTitles.length > 0) {
        this.repeatTitles = repeatTitles;
        this.repetitionNameShow = true;
        return false;
      }
      return true;
    },
    onPropsChange(value) {
      this.props = value;
    },
    handleOutsideClick(e) {
      let paramButton = this.$refs.paramButton;
      let resourceButton = this.$refs.resourceButton;
      if ((paramButton && paramButton.contains(e.target)) || e.target === paramButton
                || (resourceButton && resourceButton.contains(e.target)) || e.target === resourceButton) {
        return;
      }
      if (this.isParamModalShow) {
        this.isParamModalShow = false;
      }
    },
    handleOutsideClickNode(e) {
      if (e.target.className === 'node-box-content' || e.target.className === 'node-box') return;
      if (this.nodebaseinfoShow) {
        this.nodebaseinfoShow = false;
      }
    },
    updateResources(res) {
      this.resources = res.map((item) => {
        return {
          fileName: item.fileName,
          resourceId: item.resourceId,
          version: item.version,
        };
      });
      this.jsonChange = true;
    },
    nodeDelete(node) {
      this.isDelete = true;
      if (node.type === NODETYPE.FLOW && node.jobContent && node.jobContent.embeddedFlowId) {
        // 不用调用删除接口改为记录
        this.ops.push({
          id: node.jobContent.embeddedFlowId,
          nodeType: node.type,
          op: 'delete',
          params: {
            name: node.title,
            description: node.desc,
          },
        });
      }
      if ([NODETYPE.DISPLAY, NODETYPE.DASHBOARD].includes(node.type) && node.jobContent && node.jobContent.id) {
        let id = NODETYPE.DASHBOARD == node.type ? node.jobContent.dashboardId  : node.jobContent.id;
        this.ops.push({
          id,
          nodeType: node.type,
          op: 'delete',
          params: {
            name: node.title,
            description: node.desc,
            projectID: +this.$route.query.projectID,
          },
        });
      }
      this.$emit('deleteNode', node);
    },
    repetitionName() {
      this.repetitionNameShow = false;
    },
    saveNode(node) { // 保存节点参数配置
      if (this.myReadonly) return this.$Message.warning(this.$t('message.process.readonly'));
      this.saveNodeBaseInfo(node);
    },
    checkAssociated(node) {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.process.readonlyNoAssociated'));
      if ([NODETYPE.SPARKSQL, NODETYPE.HQL, NODETYPE.SPARKPY, NODETYPE.SCALA].indexOf(node.type) === -1) {
        return this.$Notice.warning({
          desc: this.$t('message.process.noAssociated'),
        });
      } else if (node.jobContent && node.jobContent.script) {
        this.$Modal.confirm({
          title: this.$t('message.process.repeateAssociated'),
          content: this.$t('message.process.repeateAssociatedHtml'),
          okText: this.$t('message.process.confirmAssociated'),
          cancelText: this.$t('message.newConst.cancel'),
          onOk: () => {
            this.openAssociateScriptModal(node);
          },
          onCancel: () => {
            this.$Modal.remove();
          },
        });
      } else {
        this.openAssociateScriptModal(node);
      }
    },
    openAssociateScriptModal(node) {
      this.$refs.associateScript.open(node);
    },
    associateScript(node, path, cb) {
      api.fetch('/filesystem/openFile', {
        path,
      }, 'get').then((rst) => {
        const supportModes = this.getSupportModes();
        const time = new Date();
        // 由于修改了节点类型所以之前获取方法不行
        const type = ext[node.type];
        const match = supportModes.find((item) => item.flowType === type);
        const fileName = `${time.getTime()}${match.ext}`;
        const fileContent = (rst.fileContent instanceof Array) ? rst.fileContent[0][0]: rst.fileContent;
        const params = {
          fileName,
          scriptContent: fileContent,
          metadata: rst.params,
        };
        api.fetch('/filesystem/saveScriptToBML', params, 'post')
          .then((res) => {
            this.$Message.success(this.$t('message.process.associaSuccess'));
            node.params = rst.params;
            const params = {
              fileName,
              resourceId: res.resourceId,
              version: res.version,
            };
            this.$emit('check-opened', node, (isOpened) => {
              this.$emit('save-node', params, node);
              if (isOpened) {
                api.fetch('/filesystem/openScriptFromBML', params, 'get').then((res) => {
                  this.dispatch('Workbench:updateFlowsTab', node, {
                    content: res.scriptContent,
                    params: res.metadata,
                  });
                });
              } else {
                // 如果没打开节点，是无法调取Workbench的方法的
                // 所以，直接调用IndexedDB清空缓存
                this.dispatch('IndexedDB:clearLog', node.key);
                this.dispatch('IndexedDB:clearResult', node.key);
                this.dispatch('IndexedDB:clearProgress', node.key);
              }
            });
            cb(true);
          }).catch(() => {
            cb(false);
            this.$Message.error(this.$t('message.process.associaError'));
          });
      }).catch(() => {
        cb(false);
      });
    },
    paramsChange(val) {
      this.paramsIsChange = val;
    },
    addNode(node) {
      node = this.bindNodeBasicInfo(node);
      // 弹窗提示由后台控制
      if (node.shouldCreationBeforeNode) {
        this.addNodeShow = true;
      } else {
        return;
      }
      this.clickCurrentNode = JSON.parse(JSON.stringify(node));
      this.addNodeShow = true;
      this.addNodeTitle = this.$t('message.process.createNode');
    },
    validatorName(rule, value, callback) {
      if (value === `${this.name}_`) {
        callback(new Error(this.$t('message.process.nodeNameValid')));
      } else {
        callback();
      }
    },
    addFlowCancel() {
      this.addNodeShow = false;
      // 删除未创建成功的节点
      this.json.nodes = this.json.nodes.filter((subItem) => {
        return this.clickCurrentNode.key != subItem.key;
      });
      this.originalData = this.json;
    },
    addFlowOk() {
      this.$refs.addFlowfoForm.validate((valid) => {
        if (valid) {
          this.addNodeShow = false;
          if (this.myReadonly) return this.$Message.warning(this.$t('message.process.readonlyNoCeated'));
          this.saveNodeBaseInfo(this.clickCurrentNode);
        }
      });
    },
    relySelect(node) {
      /**
       * 1.获取当前节点的key
       * 2.查找以当前key为source的节点
       * 3.遍历查找出来的节点数组，接着递归
       *  */
      let stepArray = [];
      const stepArrayAction = (nodeKey) => {
        this.json.edges.forEach((item) => {
          if (item.source === nodeKey) {
            stepArray.push(item);
            stepArrayAction(item.target);
          }
        });
      };
      stepArrayAction(node.key);
      this.json.nodes = this.json.nodes.map((subItem) => {
        if (stepArray.some((item) => item.target === subItem.key) || subItem.key === node.key) {
          subItem.selected = true;
        } else {
          subItem.selected = false;
        }
        return subItem;
      });
      this.originalData = this.json;
    },
    heartBeat: throttle(() => {
      api.fetch('/user/heartbeat', 'get');
    }, 60000),
    copyNode(node) {
      node = this.bindNodeBasicInfo(node);
      if (node.enable_copy === false) return this.$Message.warning(this.$t('message.process.noCopy'));
      if (this.myReadonly) return this.$Message.warning(this.$t('message.process.readonlyNoCopy'));
      this.cacheNode = JSON.parse(JSON.stringify(node));
    },
    pasteNode(e) {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.process.noPaste'));
      if (!this.cacheNode) {
        return this.$Message.warning(this.$t('message.process.firstCopy'));
      }
      // 获取屏幕的缩放值
      let pageSize = this.$refs.process.getState().baseOptions.pageSize;
      const key = '' + new Date().getTime() + Math.ceil(Math.random() * 100);
      this.cacheNode.key = key;
      this.cacheNode.createTime = Date.now();
      this.cacheNode.layout = {
        height: this.cacheNode.height,
        width: this.cacheNode.width,
        x: (e.offsetX / pageSize),
        y: (e.offsetY / pageSize),
      };
      // 如果是flow,display和dahsborard要删除id
      if ([NODETYPE.DISPLAY, NODETYPE.DASHBOARD, NODETYPE.FLOW].includes(this.cacheNode.type)) {
        delete this.cacheNode.jobContent;
      }
      this.json.nodes.push(this.cacheNode);
      this.nodeSelectedFalse(this.cacheNode);
      this.originalData = this.json;
    },
    // 由于插件的selected不是响应式，所以得手动改变
    nodeSelectedFalse(node = {}) {
      this.json.nodes = this.json.nodes.map((subItem) => {
        if (node.key && node.key === subItem.key) {
          subItem.selected = true;
        } else {
          subItem.selected = false;
        }
        return subItem;
      });
      this.originalData = this.json;
    },
    clickBaseInfo() {
      // 插件组件内的节点outSide事件后执行，会将chosing清空，所以加个异步后执行，但是页面能看到节点样式抖动，后面想办法优化
      const teimer = setTimeout(() => {
        this.nodeSelectedFalse(this.clickCurrentNode);
        clearTimeout(teimer);
      }, 0);
    },
    // 批量删除选中节点
    allDelete() {
      if (this.myReadonly) return this.$Message.warning(this.$t('message.process.noDelete'));
      const selectNodes = this.$refs.process.getSelectedNodes().map((item) => item.key);
      this.json.nodes = this.json.nodes.filter((item) => {
        if (selectNodes.includes(item.key)) {
          this.json.edges = this.json.edges.filter((link) => {
            return !(link.source === item.key || link.target === item.key);
          });
        } else {
          item.selected = false;
          return true;
        }
      });
      this.originalData = this.json;
    },
    openConsole(node) {
      this.nodeSelectedFalse(node);
      this.openningNode = node;
      this.shapeWidth = this.$refs.process && this.$refs.process.state.shapeOptions.viewWidth;
    },
    closeConsole(node) {
      this.openningNode = null;
    },
    saveCommonIframe(node, cb) {
      if (![NODETYPE.DISPLAY, NODETYPE.DASHBOARD].includes(node.type)) return;
      //  节点原始数据
      if (!node.jobContent) {
        node.jobContent = {};
      }
      if (!node.jobContent.id) {
        cb();
        // 调用接口创建, 先判断是什么节点
        const createParams = {
          id: +this.$route.query.projectID,
          nodeType: node.type,
          params: {
            avatar: '18',
            description: node.desc,
            name: node.title,
            projectId: +this.$route.query.projectID,
            publish: true
          }
        }
        api.fetch('/dss/createAppjointNode', createParams).then((res) => {
          // 由于vsbi的错误信息返回的这里，所以得判断是否成功给予提示
          let commomData = {};
          try {
            commomData = JSON.parse(res.result);
          } catch {
            commomData = res.result;
          }
          if (commomData.payload.id) {
            if (NODETYPE.DISPLAY === node.type) {
              node.jobContent.id = commomData.payload.displayId;
            }
            if (NODETYPE.DASHBOARD === node.type) {
              node.jobContent.id = commomData.payload.dashboardPortalId;
              node.jobContent.dashboardId = commomData.payload.id
            }
            this.$Message.success({
              content: this.$t('message.process.createdSuccess')
            });
          } else {
            this.$Message.error({
              content: commomData.header.msg,
              duration: 4,
            });
          }
        })
      } else {
        // 更新iframe节点内容

      }
    },
    // 根据节点类型将后台节点基础信息加入
    bindNodeBasicInfo(node) {
      this.nodeBasicInfoData.map((item) => {
        if (item.nodeType === node.type) {
          node = Object.assign(node, item);
        }
      })
      return node;
    },
    workflowRun() {
      this.dispatch('IndexedDB:clearNodeCache');
      this.openningNode = null;
      // return this.$Message.warning('执行重构中，即将开源');
      /**
       * 1.执行之前先保存，执行改为停止
       * 2.禁用操作：左侧菜单，保存，参数修改，工具栏，更具状态来操作右键
       * 3.轮询接口获取节点状态
      */
      this.autoSave('执行保存', false).then((res) => {
        // 保存成功后再调执行接口
        const parmas = {
          executeApplicationName: "flowexecution",
          executionCode: JSON.stringify({
            projectVersionId: +this.$route.query.projectVersionID,
            flowId: this.flowId,
            version: res.flowVersion
          }),
          runType: "json",
          params: {},
          source: {
            projectName: this.$route.query.projectName,
            flowName: this.name
          },
          requestApplicationName: "flowexecution"
        }
        api.fetch('/entrance/execute', parmas).then((res) => {
          // 查询执行节点的状态
          let execID = res.execID;
          this.workflowExeteId = execID;
          this.queryWorkflowExecutor(execID)
        }).catch(() => {
          this.workflowIsExecutor = false;
        })
        this.workflowIsExecutor = true;
      });
    },
    workflowStop() {
      clearTimeout(this.excuteTimer);
      api.fetch(`/entrance/${this.workflowExeteId}/kill`, 'get').then((res) => {
        this.workflowIsExecutor = false;
        this.flowExecutorNode(this.workflowExeteId, true);
      }).catch(() => {
        this.workflowIsExecutor = false;
      })
    },
    queryWorkflowExecutor(execID) {
      api.fetch(`/entrance/${execID}/status`, {}, 'get').then((res) => {

        this.flowExecutorNode(execID);
        // 根据执行状态判断是否轮询
        const status = res.status;
        if (['Inited', 'Scheduled', 'Running'].includes(status)) {
          this.excuteTimer = setTimeout(() => {
            this.queryWorkflowExecutor(execID);
          }, 1000)
        } else {
          // Succees, Failed, Cancelled, Timeout
          this.workflowIsExecutor = false;
          if (status === 'Succeed') {
            this.$Message.success("工作流执行成功")
          }
          if (status === 'Failed') {
            this.$Message.error("工作流执行失败")
            this.flowExecutorNode(execID, true);
          }
          if (status === 'Cancelled') {
            this.$Message.error("工作流执行已取消")
            this.flowExecutorNode(execID, true);
          }
          if (status === 'Timeout') {
            this.$Message.error("工作流执行超时")
            this.flowExecutorNode(execID, true);
          }
        }
      })
    },
    flowExecutorNode(execID, end = false) {
      api.fetch(`/entrance/${execID}/execution`, {}, 'get').then((res) => {
        // 【0：未执行；1：运行中；2：已成功；3：已失败；4：已跳过】
        const actionStatus = {
          pendingJobs: {color: 'rgba(102, 102, 102, 0.3)', status: 0, iconType: '',
            colorClass: '', isShowTime: false, title: '等待执行', showConsole: false},
          runningJobs: {color: 'rgba(69, 166, 239, 1)', status: 1, iconType: 'ios-loading',
            colorClass: {'executor-loading': true}, isShowTime: true, title: '执行中', showConsole: true},
          succeedJobs: {color: 'rgba(78, 225, 78, 0.4)', status: 2,iconType: 'ios-checkmark-circle-outline',
            colorClass: {'executor-success': true}, isShowTime: false, title: '执行成功', showConsole: true},
          failedJobs: {color: 'rgba(231, 77, 98, 0.4)', status: 3, iconType: 'ios-close-circle-outline',
            colorClass: {'executor-faile': true}, isShowTime: false, title: '执行失败', showConsole: true},
          skippedJobs: {color: 'rgba(236, 152, 47, 0.8)', status: 4, iconType: 'ios-alert-outline',
            colorClass: {'executor-skip': true}, isShowTime: false, title: '跳过', showConsole: false}
        };
        // 获取节点的状态，如果没有执行完成继续查询
        const data =  res;
        const date = (new Date()).getTime();

        Object.keys(data).forEach((key) => {
          // 如果当前工作流已经执行结束，还得获取状态到没有执行的节点为止
          if(end && key === 'runningJobs' && data[key].length > 0) {
            setTimeout(() => {
              this.flowExecutorNode(execID, true);
            }, 2000)
          }
          data[key].forEach((node) => {
            this.$refs.process.setNodeRunState(node.nodeID, {
              time: this.timeTransition(date - node.startTime),
              status: actionStatus[key].status,
              borderColor: actionStatus[key].color,
              iconType: actionStatus[key].iconType,
              colorClass: actionStatus[key].colorClass,
              isShowTime: actionStatus[key].isShowTime,
              title: actionStatus[key].title,
              showConsole: actionStatus[key].showConsole,
              execID: node.execID,
              taskID: node.taskID
            })
          })
        })

      })
    },
    timeTransition(time) {
      time = Math.floor(time / 1000);
      let hour = 0;
      let minute = 0;
      let second = 0;
      if (time > 60) {
        minute = Math.floor(time / 60);
        second = Math.floor(time % 60);
        if (minute >= 60) {
          hour = Math.floor(minute / 60);
          minute = Math.floor(minute % 60);
        } else {
          hour = 0;
        }
      } else {
        hour = 0;
        if (time == 60) {
          minute = 1;
          second = 0;
        } else {
          minute = 0;
          second = time;
        }
      }
      const addZero = (num) => {
        let result = num;
        if (num < 10) {
          result = `0${num}`
        }
        return result;
      }
      const timeResult = `${addZero(hour)}:${addZero(minute)}:${addZero(second)}`;
      return timeResult;
    },
    getCache() {
      return new Promise((resolve) => {
        this.dispatch('IndexedDB:getProjectCache', {
          projectID: this.$route.query.projectID,
          cb: (cache) => {
            const list = cache && cache.tabList || [];
            let json = null;
            list.forEach((item) => {
              if (item.flowId === this.flowId) {
                json = item.json;
              }
            })
            resolve(json);
          }
        })
      })
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
