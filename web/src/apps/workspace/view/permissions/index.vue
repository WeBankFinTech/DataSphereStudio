<template>
  <div style="height: 100%">
    <permissions />
  </div>
</template>

<script>
import permissions from '../../module/permissions';

export default {
  components: {
    permissions: permissions.component
  }
}
</script>
