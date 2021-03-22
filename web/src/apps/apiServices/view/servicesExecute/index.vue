<template>
  <div>
    <services-execute />
  </div>
</template>

<script>
import apiServicesExecute from '../../module/apiServicesExecute';
export default {
  components: {
    servicesExecute: apiServicesExecute.component
  }
}
</script>