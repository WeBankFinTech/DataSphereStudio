<template>
  <tableEditorView />
</template>
<script>
import tableEditorModel from "@/apps/dataModelCenter/module/tableEditor";
export default {
  components: {
    tableEditorView: tableEditorModel.component,
  },
};
</script>
