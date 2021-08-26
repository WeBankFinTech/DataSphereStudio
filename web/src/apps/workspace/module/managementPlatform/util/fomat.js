/**
 *
 * @param {Object} _component
 * @return component
 */
function formatComponentData(component) {
  let _component = Object.assign({}, component);
  if( _component.if_iframe == 1 ) {
    _component.if_iframe = true;
  } else {
    _component.if_iframe = false;
  }
  if( _component.is_active == 1 ) {
    _component.is_active = true;
  } else {
    _component.is_active = false;
  }
  switch(_component.onestop_menu_id) {
    case 1:
      _component.onestop_menu_id = 'dataAccess';
      break;
    case 2:
      _component.onestop_menu_id = 'dataanAlysis';
      break;
    case 3:
      _component.onestop_menu_id = 'productionOperation';
      break;
    case 4:
      _component.onestop_menu_id = 'dataQuality';
      break;
    case 5:
      _component.onestop_menu_id = 'administratorFunction';
      break;
  }
  return _component
}

/**
 *
 * @param {Object} component
 */
function formatComponentDataForPost(component) {
  let _component = Object.assign({}, component);
  if ( _component.if_iframe ) {
    _component.if_iframe = 1;
  } else {
    _component.if_iframe = 0;
  }
  if ( _component.is_active ) {
    _component.is_active = 1;
  } else {
    _component.is_active = 0;
  }
  switch(_component.onestop_menu_id) {
    case 'dataAccess':
      _component.onestop_menu_id = 1;
      break;
    case 'dataanAlysis':
      _component.onestop_menu_id = 2;
      break;
    case 'productionOperation':
      _component.onestop_menu_id = 3;
      break;
    case 'dataQuality':
      _component.onestop_menu_id = 4;
      break;
    case 'administratorFunction':
      _component.onestop_menu_id = 5;
      break;
  }

  // 删除多余的属性
  if ( Object.keys(_component).includes('_id') ) {
    delete _component._id;
  }
  if ( Object.keys(_component).includes('isAdded') ) {
    delete _component.isAdded;
  }

  return _component
}


export {
  formatComponentData,
  formatComponentDataForPost
}
