

const ADMIN = 'admin';                  //管理员
const MAINTENANCE = 'maintaince';       //运维人员
const DEVELOPER = 'developer';          //开发人员
const ANALYSER = 'analyser';            //分析用户
const OPERATOR = 'operator';            //运营用户
const BOSS = 'boss';                    //领导
const CREATOR = 'creator';              //创建者

// const WRITE = 'w';
// const READ = 'r';
// const DELETE = 'd';
// const CREATE = 'c';
// const PUBLISH = 'x';
const NOT = '-';

const DEFAULT_NOT = '-----';

const workspacePremissinConfig = {
  [ADMIN]: 'rwdc-',
  [MAINTENANCE]: '----x',
  [DEVELOPER]: 'rw-c-',
  [ANALYSER]: 'r----',
  [OPERATOR]: 'r----',
  [BOSS]: 'r----',
  [CREATOR]: 'rwdc-'
}

const addPremission = ($premission1, $premission2)=>{
  let premission1 = $premission1 || DEFAULT_NOT;
  let premission2 = $premission2 || DEFAULT_NOT;

  let result = '';

  const withAdd = (a, b)=>{
    if(a===b){
      return a;
    }else {
      if(a==='-'){
        return b;
      }else {
        return a;
      }
    }
  };

  for(let i=0; i<premission1.length; i++){
    result += withAdd(premission1.charAt(i), premission2.charAt(i))
  }
  return result;
}

const getPremission = (roles)=> {
  let permissionsLabel = DEFAULT_NOT;

  roles.forEach(role => {
    permissionsLabel = addPremission(permissionsLabel, workspacePremissinConfig[role])
  });
  return permissionsLabel;
}

const getPremissionLabels = (permission)=>{
  let permissionsLabel;
  if(permission instanceof Array) {
    permissionsLabel = getPremission(permission);
  }else if(permission instanceof String){
    permissionsLabel = permission;
  }else if(permission['virtualRoles']){
    permissionsLabel = permission['virtualRoles'];
  }
  return permissionsLabel;
}

const canRead = (permission)=>{
  return getPremissionLabels(permission).charAt(0) !== NOT;
}

const canWrite = (permission)=>{
  return getPremissionLabels(permission).charAt(1) !== NOT;
}

const canDelete = (permission)=>{
  return getPremissionLabels(permission).charAt(2) !== NOT;
}

const canCreate = (permission)=>{
  return getPremissionLabels(permission).charAt(3) !== NOT;
}

const canPublish = (permission)=>{
  return getPremissionLabels(permission).charAt(4) !== NOT;
}


const setVirtualRoles = (item, username)=>{
  let virtualRoles = DEFAULT_NOT;
  if(item.releasable || (item.releaseUsers&&item.releaseUsers.some(e => e === username))) {
    virtualRoles = addPremission(virtualRoles, '----x');
  }
  if(item.editable || (item.editUsers&&item.editUsers.some(e => e === username))) {
    virtualRoles = addPremission(virtualRoles, '-w---');
  }
  if(item.accessUsers&&item.accessUsers.some(e => e === username)) {
    virtualRoles = addPremission(virtualRoles, 'r----');
  }
  if(item.createBy === username) {
    virtualRoles = addPremission(virtualRoles, 'rwdc-');
  }
  item.virtualRoles = virtualRoles
  item.canCreate = ()=> canCreate(item) ;
  item.canDelete = ()=> canDelete(item);
  item.canPublish = ()=> canPublish(item);
  item.canRead = ()=> canRead(item);
  item.canWrite = ()=> canWrite(item);
}




export {
  canRead,
  canWrite,
  canDelete,
  canCreate,
  canPublish,
  setVirtualRoles
}
