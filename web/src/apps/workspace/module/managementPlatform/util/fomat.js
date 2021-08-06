/**
 *
 * @param {Object} _component
 * @return component
 */
function formatComponentData(component) {
  const _componet = Object.assign({}, component);
  if( _componet.if_iframe == 1 ) {
    _componet.if_iframe = true;
  } else {
    _componet.if_iframe = false;
  }
  if( _componet.is_active == 1 ) {
    _componet.is_active = true;
  } else {
    _componet.is_active = false;
  }
  switch(_componet.onestop_menu_id) {
    case 1:
      _componet.onestop_menu_id = 'dataAccess';
      break;
    case 2:
      _componet.onestop_menu_id = 'dataanAlysis';
      break;
    case 3:
      _componet.onestop_menu_id = 'productionOperation';
      break;
    case 4:
      _componet.onestop_menu_id = 'dataQuality';
      break;
    case 5:
      _componet.onestop_menu_id = 'administratorFunction';
      break;
  }
  return _componet
}

/**
 *
 * @param {Object} component
 */
function formatComponentDataForPost(component) {
  const _componet = Object.assign({}, component);
  if ( _componet.if_iframe ) {
    _componet.if_iframe = 1;
  } else {
    _componet.if_iframe = 0;
  }
  if ( _componet.is_active ) {
    _componet.is_active = 1;
  } else {
    _componet.is_active = 0;
  }
  switch(_componet.onestop_menu_id) {
    case 'dataAccess':
      _componet.onestop_menu_id = 1;
      break;
    case 'dataanAlysis':
      _componet.onestop_menu_id = 2;
      break;
    case 'productionOperation':
      _componet.onestop_menu_id = 3;
      break;
    case 'dataQuality':
      _componet.onestop_menu_id = 4;
      break;
    case 'administratorFunction':
      _componet.onestop_menu_id = 5;
      break;
  }

  // 删除多余的属性
  if ( Object.keys(_componet).includes('_id') ) {
    delete _componet._id;
  }
  if ( Object.keys(_componet).includes('isAdded') ) {
    delete _componet.isAdded;
  }

  return _componet
}


export {
  formatComponentData,
  formatComponentDataForPost
}
