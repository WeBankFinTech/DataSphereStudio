// const menuInfo = JSON.parse(sessionStorage.getItem("menuOptions")) || [];
// const menuOptions = [];
// menuInfo.forEach(info => {
//   menuOptions.push(info.titleEn);
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
  if (_component.ifIframe == 1) {
    _component.ifIframe = true;
  } else {
    _component.ifIframe = false;
  }
  if (_component.isActive == 1) {
    _component.isActive = true;
  } else {
    _component.isActive = false;
  }
  const menuInfo = JSON.parse(sessionStorage.getItem("menuOptions")) || [];
  const menuOptions = [];
  menuInfo.forEach(info => {
    menuOptions.push(info.titleEn);
  });
  const _menuOptions = new Map();
  menuOptions.forEach((item, idx) => {
    _menuOptions.set(item, idx);
  });
  _component.onestopMenuId = menuOptions[_component.onestopMenuId - 1];
  return _component;
}

/**
 *
 * @param {Object} component
 */
function formatComponentDataForPost(component) {
  let _component = Object.assign({}, component);
  if (_component.ifIframe) {
    _component.ifIframe = '1';
  } else {
    _component.ifIframe = '0';
  }
  if (_component.isActive) {
    _component.isActive = '1';
  } else {
    _component.isActive = '0';
  }
  const menuInfo = JSON.parse(sessionStorage.getItem("menuOptions")) || [];
  const menuOptions = [];
  menuInfo.forEach(info => {
    menuOptions.push(info.titleEn);
  });
  const _menuOptions = new Map();
  menuOptions.forEach((item, idx) => {
    _menuOptions.set(item, idx);
  });

  _component.onestopMenuId = (_menuOptions.get(_component.onestopMenuId) + 1) + '';

  // 删除多余的属性
  if (Object.keys(_component).includes("_id")) {
    delete _component._id;
  }
  if (Object.keys(_component).includes("isAdded")) {
    delete _component.isAdded;
  }

  if (!_component.id) {
    _component.id = null
  }

  return _component;
}

export { formatComponentData, formatComponentDataForPost };
