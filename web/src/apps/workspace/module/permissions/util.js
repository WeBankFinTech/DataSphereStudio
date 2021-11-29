import _ from "lodash";
const ID_CHAIN = "_UPPER_ID_CHAIN_";

//为每个子部门添加所有上级部门的id，便于上溯它的部门链层
function addIdChain(obj, id) {
  if (obj.children && obj.children.length > 0) {
    obj.children.forEach(item => {
      const parentChain = obj[ID_CHAIN] || [];
      item[ID_CHAIN] = [...parentChain, obj[id]];
      if (item.children && item.children.length > 0) {
        addIdChain(item, id);
      }
    });
  }
}

//展开树形结构
function expandAll(datas) {
  const copyData = _.cloneDeep(datas);
  const newAr = [];
  copyData.forEach(item => {
    innerExpand(item, newAr);
  });
  function innerExpand(da, result) {
    const data = _.cloneDeep(da);
    result.push(data);
    if (data.children && data.children.length > 0) {
      data.unfold = true;
      data.children.forEach(item => {
        innerExpand(item, result);
      });
    }
  }
  console.log(newAr);
  return newAr;
}

//获取父级部门名
function getParentDepartName(data, departTree) {
  let hit = false;
  let ar = "";
  function innerFunc(obj, str) {
    if (hit) {
      return;
    } else if (obj.id + "" === data.id + "") {
      ar = str ? str + "-" + obj.label : obj.label;
      hit = true;
    } else if (obj.children && obj.children.length > 0) {
      obj.children.forEach(item =>
        innerFunc(item, str ? str + "-" + obj.label : obj.label)
      );
    } else {
      return;
    }
  }
  if (data.id) {
    departTree.forEach(item => {
      if (hit) {
        return;
      } else {
        innerFunc(item, "");
      }
    });
    console.log(ar);
    return ar;
  } else {
    return null;
  }
}

//删除树形结构children为空的节点的children属性
function removeEmptyChildren(data = []) {
  data.forEach((item, index) => {
    const { children, ...rest } = item;
    if (children) {
      if (children.length > 0) {
        removeEmptyChildren(children);
      } else {
        data[index] = { ...rest };
      }
    }
  });
}

//树形结构数据仅保留N层数据，一下的children删除
function keepTreeNLevel(tree, N) {
  function inner(data, currentLevel) {
    if (!data) {
      return;
    }
    const { children } = data;
    if (children && children.length > 0) {
      if (currentLevel < N) {
        inner();
        children.forEach(item => {
          inner(item, currentLevel + 1);
        });
      } else {
        delete data.children;
      }
    }
  }
  tree.forEach(item => {
    inner(item, 1);
  });
}

//检测密码是否符合要求return{valid:是否有效， tag: 无效原因};
function testPassword(value) {
  if (value === "") {
    return { valid: false, tag: "empty" };
  } else {
    const reg = /^[A-Za-z]\S{7,25}$/g;
    const reg1 = /[A-Z]+/;
    const reg2 = /\d+/;
    const reg3 = /[^\w\s]+/;
    const reg4 = /[a-z]+/;
    if (
      reg.test(value) &&
      reg1.test(value) &&
      reg2.test(value) &&
      reg3.test(value) &&
      reg4.test(value)
    ) {
      if (!testKeyboardSort(value)) {
        return { valid: false, tag: "keyboard" };
      }
      return { valid: true };
    } else {
      return { valid: false, tag: "invalid" };
    }
  }
}

function testKeyboardSort(str) {
  const num = "01234567890";
  const alphabet = "abcdefghijklmnopqrstuvwxyz";
  const keyboard = ["qwertyuiop", "asdfghjkl", "zxcvbnm"];
  let hit = false;
  for (let i = 0; i < str.length - 3; i++) {
    const substr = str.substring(i, i + 3).toLowerCase();
    const reStr = substr
      .split("")
      .reverse()
      .join("");
    if (
      num.includes(substr) ||
      num.includes(reStr) ||
      alphabet.includes(substr) ||
      alphabet.includes(reStr)
    ) {
      hit = true;
      break;
    } else {
      hit = keyboard.some(
        item => item.includes(substr) || item.includes(reStr)
      );
      if (hit) {
        break;
      }
    }
  }
  return !hit;
}
export {
  ID_CHAIN,
  addIdChain,
  expandAll,
  getParentDepartName,
  removeEmptyChildren,
  keepTreeNLevel,
  testPassword
};
