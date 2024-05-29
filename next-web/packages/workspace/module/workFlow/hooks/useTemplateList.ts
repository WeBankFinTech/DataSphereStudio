import { ref } from 'vue';
type ObjectType = Record<string, unknown>;
export const useTemplateList = (templates: { [key: string]: any }) => {
  const templateData = ref<ObjectType[]>([]);

  function createData(data: ObjectType[]): ObjectType[] | undefined {
    if (!data) return;
    return data.map((v) => ({
      label: v.label,
      value: v.value,
      scopeBus: v.scopeBus,
      children: createData(v.children as ObjectType[]),
      prefix: null,
      suffix: null,
    }));
  }

  const loadTemplates = async (node: ObjectType) => {
    if (!node) {
      return Promise.resolve([]);
    }
    const list = templates.value
      .filter((item: ObjectType) => item.engineType === node.value)
      .map((item: ObjectType) => {
        item.isLeaf = true;
        return item;
      });
    return Promise.resolve(list);
  };

  return {
    templateData,
    createData,
    loadTemplates,
  };
};
