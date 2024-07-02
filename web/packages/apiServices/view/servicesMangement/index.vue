<template>
  <div style="height: 100%;">
    <servicesMangement />
  </div>
</template>

<script>
import dataServicesMangement from '../../module/servicesMangement';

export default {
  components: {
    servicesMangement: dataServicesMangement.component
  }
}
</script>