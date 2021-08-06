/**
 *
 * @param {Object} component
 * @return component
 */
function formatComponentData(component) {
  if( component.if_iframe == 1 ) {
    component.if_iframe = true;
  } else {
    component.if_iframe = false;
  }
  if( component.is_active == 1 ) {
    component.is_active = true;
  } else {
    component.is_active = false;
  }
  switch(component.onestop_menu_id) {
    case 1:
      component.onestop_menu_id = 'dataAccess';
      break;
    case 2:
      component.onestop_menu_id = 'dataanAlysis';
      break;
    case 3:
      component.onestop_menu_id = 'productionOperation';
      break;
    case 4:
      component.onestop_menu_id = 'dataQuality';
      break;
    case 5:
      component.onestop_menu_id = 'administratorFunction';
      break;
  }
  return component
}

/**
 *
 * @param {Object} component
 */
function formatComponentDataForPost(component) {
  if ( component.if_iframe ) {
    component.if_iframe = 1;
  } else {
    component.if_iframe = 0;
  }
  if ( component.is_active ) {
    component.is_active = 1;
  } else {
    component.is_active = 0;
  }
  switch(component.onestop_menu_id) {
    case 'dataAccess':
      component.onestop_menu_id = 1;
      break;
    case 'dataanAlysis':
      component.onestop_menu_id = 2;
      break;
    case 'productionOperation':
      component.onestop_menu_id = 3;
      break;
    case 'dataQuality':
      component.onestop_menu_id = 4;
      break;
    case 'administratorFunction':
      component.onestop_menu_id = 5;
      break;
  }

  // 删除多余的属性
  if ( Object.keys(component).includes('_id') ) {
    delete component._id;
  }
  if ( Object.keys(component).includes('isAdded') ) {
    delete component.isAdded;
  }

  return component
}


export {
  formatComponentData,
  formatComponentDataForPost
}
