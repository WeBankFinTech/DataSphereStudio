import { ref, computed, type Ref } from 'vue';
type ObjectType = Record<string, unknown>;
export const useTableConfig = (origins: Ref) => {
  // 用于接收当前需要展示的表头的列
  const headers = ref([]);
  const originHeaders = computed(() =>
    (origins.value as ObjectType[]).filter(
      (v) => !['action', 'selection'].includes(v.type as string)
    )
  );
  const showTableHeaderConfig = ref(false);
  // 判断表头是否展示
  const checkTColShow = (col: string) =>
    headers.value.map((item: ObjectType) => item.prop).includes(col);
  return {
    headers,
    originHeaders,
    showTableHeaderConfig,
    checkTColShow,
  };
};
