export const storage = {
  setItem(key, val) {
    return sessionStorage.setItem(key, val)
  },
  getItem(key) {
    return sessionStorage.getItem(key)
  },
  removeItem(key) {
    return sessionStorage.removeItem(key)
  }
}
