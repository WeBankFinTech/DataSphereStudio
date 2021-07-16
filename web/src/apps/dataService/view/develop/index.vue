<template>
  <div style="height: 100%">
    <dataService />
  </div>
</template>

<script>
import dataService from '../../module/dataService';
export default {
  components: {
    dataService: dataService.component
  }
}
</script>