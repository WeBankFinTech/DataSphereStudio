import debounce from './debounce'
const throttle = function (func, wait, options) {
  var leading = true
  var trailing = true

  if (options === false) {
    leading = false
  } else if (typeof options === typeof {}) {
    leading = 'leading' in options ? options.leading : leading
    trailing = 'trailing' in options ? options.trailing : trailing
  }
  options = options || {}
  options.leading = leading
  options.maxWait = wait
  options.trailing = trailing

  return debounce(func, wait, options)
}
export default throttle
