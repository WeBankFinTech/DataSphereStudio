import storage from "@/common/service/storage";

/**
 * 
 */
export function isAdmin() {
  const currentUser = storage.get("baseInfo", 'local') || {};
  if (currentUser.isAdmin) {
    return true;
  }
  return false;
}

export default {
  isAdmin,
}