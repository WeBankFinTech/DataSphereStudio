<template>
  <div style="height: 100%">
    <apiServices />
  </div>
</template>

<script>
import apiServices from '../../module/apiServices';
export default {
  components: {
    apiServices: apiServices.component
  }
}
</script>