<template>
  <tableEditorView />
</template>
<script>
import tableEditorModel from "../../../module/tableEditor";
export default {
  components: {
    tableEditorView: tableEditorModel.component,
  },
};
</script>
