import { ref, computed, type Ref, type ComputedRef, onMounted } from 'vue';
import { useDateFormat } from '@vueuse/core';

export type PaginationAndParams = {
  pageNow?: number;
  pageSize?: number;
  totalCount?: number;
  totalPage?: number;
  [index: string]: any;
};

export const usePagination = (
  param?: Ref<PaginationAndParams>,
  cb?: (param: PaginationAndParams) => Promise<PaginationAndParams>
) => {
  // 分页信息初始化
  const init = () => ({
    pageNow: 1,
    pageSize: 10,
    totalCount: 0,
    totalPage: 0,
  });
  const pagination = ref<PaginationAndParams>(init());
  const isLoading = ref(false); // 加载中

  // 查询参数
  const parameter: ComputedRef<PaginationAndParams> = computed(() => ({
    ...param?.value,
    pageNow: pagination.value.pageNow,
    pageSize: pagination.value.pageSize,
  }));

  // 设置分页
  const setPagination = (params: { [index: string]: unknown } = {}): void => {
    Object.keys(params).forEach((key: string) => {
      pagination.value[key] = params[key];
    });
  };

  // 回调函数
  const callback = async () => {
    try {
      if (cb) {
        isLoading.value = true;
        const pagedata = await cb(parameter.value);
        isLoading.value = false;
        setPagination(pagedata);
      }
    } catch (error) {
      isLoading.value = false;
      console.log(error);
    }
  };

  // 查询表单
  const handleInit = async () => {
    pagination.value.pageNow = 1;
    await callback();
  };

  // 查询表单
  const handleCurrent = async () => {
    await callback();
  };

  // 分页事件
  const handleCurrentChange = async (currentPage: number, pageSize: number) => {
    pagination.value.pageSize = pageSize;
    pagination.value.pageNow = currentPage;
    await callback();
  };

  onMounted(() => {
    pagination.value.pageSize = param?.value?.pageSize || 10;
    pagination.value.pageNow = param?.value?.pageNow || 1;
  });

  // 表格内容filter
  const fillText = (row: { [index: string]: unknown }) =>
    ['null', 'undefined', ''].includes(String(row.cellValue))
      ? '- -'
      : row.cellValue;

  // 表格内容filter
  const fillTimeText = (row: { [index: string]: unknown }) =>
    ['null', 'undefined', ''].includes(String(row.cellValue))
      ? '- -'
      : useDateFormat(row.cellValue as string, 'YYYY-MM-DD HH:mm:ss').value;
  return {
    isLoading,
    pagination,
    handleInit,
    handleCurrent,
    handleCurrentChange,
    fillText,
    fillTimeText,
  };
};
