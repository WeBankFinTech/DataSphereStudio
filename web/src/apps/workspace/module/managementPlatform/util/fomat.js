// const menuInfo = JSON.parse(sessionStorage.getItem("menuOptions")) || [];
// const menuOptions = [];
// menuInfo.forEach(info => {
//   menuOptions.push(info.title_en);
// });
// const _menuOptions = new Map();
// menuOptions.forEach((item, idx) => {
//   _menuOptions.set(item, idx);
// });

/**
 *
 * @param {Object} _component
 * @return component
 */
function formatComponentData(component) {
  let _component = Object.assign({}, component);
  if (_component.if_iframe == 1) {
    _component.if_iframe = true;
  } else {
    _component.if_iframe = false;
  }
  if (_component.is_active == 1) {
    _component.is_active = true;
  } else {
    _component.is_active = false;
  }
  const menuInfo = JSON.parse(sessionStorage.getItem("menuOptions")) || [];
  const menuOptions = [];
  menuInfo.forEach(info => {
    menuOptions.push(info.title_en);
  });
  const _menuOptions = new Map();
  menuOptions.forEach((item, idx) => {
    _menuOptions.set(item, idx);
  });
  _component.onestop_menu_id = menuOptions[_component.onestop_menu_id - 1];
  return _component;
}

/**
 *
 * @param {Object} component
 */
function formatComponentDataForPost(component) {
  let _component = Object.assign({}, component);
  if (_component.if_iframe) {
    _component.if_iframe = 1;
  } else {
    _component.if_iframe = 0;
  }
  if (_component.is_active) {
    _component.is_active = 1;
  } else {
    _component.is_active = 0;
  }
  const menuInfo = JSON.parse(sessionStorage.getItem("menuOptions")) || [];
  const menuOptions = [];
  menuInfo.forEach(info => {
    menuOptions.push(info.title_en);
  });
  const _menuOptions = new Map();
  menuOptions.forEach((item, idx) => {
    _menuOptions.set(item, idx);
  });

  _component.onestop_menu_id = _menuOptions.get(_component.onestop_menu_id) + 1;

  // 删除多余的属性
  if (Object.keys(_component).includes("_id")) {
    delete _component._id;
  }
  if (Object.keys(_component).includes("isAdded")) {
    delete _component.isAdded;
  }

  return _component;
}

export { formatComponentData, formatComponentDataForPost };
