import { ref } from 'vue';
type ObjectType = Record<string, unknown>;

export const useDataUtils = () => {
  // 按照条件过滤
  function filterate(item: ObjectType, param: ObjectType) {
    const validList = [];
    if (param['nodeName']) {
      const valid1 = item.title === param['nodeName'];
      validList.push(valid1);
    }
    if (param['nodeType'] && (param['nodeType'] as string[]).length > 0) {
      const valid2 = (param['nodeType'] as string[]).includes(
        item.jobType as string
      );
      validList.push(valid2);
    }
    if (param['templateId']) {
      const valid3 = item.ecConfTemplateId === param['templateId'];
      validList.push(valid3);
    }
    if (param['modifyUser']) {
      const valid4 = item.modifyUser === param['modifyUser'];
      validList.push(valid4);
    }
    if (param['updateTimes'] && (param['updateTimes'] as number[]).length > 0) {
      const [startTime, endTime] = param['updateTimes'] as number[];
      const valid5 =
        (item.modifyTime as number) >= startTime &&
        (item.modifyTime as number) <= endTime;
      validList.push(valid5);
    }
    return !validList.some((valid) => valid === false);
  }

  const timer = ref<number | undefined>();

  function handleNodeClick(
    node: ObjectType | null,
    action: string,
    flowId: string | number
  ) {
    if (timer.value) {
      window.clearTimeout(timer.value);
    }
    timer.value = window.setTimeout(() => {
      window.parent.postMessage(
        JSON.stringify({
          action,
          flowId,
          type: 'dss-nextweb',
          node,
        }),
        '*'
      );
    }, 300);
  }

  function handleNodeDblClick(
    node: ObjectType,
    action: string,
    flowId: string | number
  ) {
    if (timer.value) {
      clearTimeout(timer.value);
    }
    window.parent.postMessage(
      JSON.stringify({
        action,
        flowId,
        type: 'dss-nextweb',
        node,
      }),
      '*'
    );
  }

  return {
    filterate,
    handleNodeClick,
    handleNodeDblClick,
  };
};
