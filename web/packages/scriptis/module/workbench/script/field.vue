<template>
    <div class="field">
        <!-- {{codePrecheckRes}} -->
        <template v-if="codePrecheckRes && codePrecheckRes.checkData && codePrecheckRes.checkData.length > 0">
            <p class="item-content" v-for="(item,index) in codePrecheckRes.checkData" :key="index">{{item.detailMessage}}</p>
        </template>
    </div>
</template>
<script>
export default {
    props: {
        codePrecheckRes: Object,
  },
}
</script>