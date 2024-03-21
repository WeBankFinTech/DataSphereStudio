const debounce = (function () {
  var FUNC_ERROR_TEXT = 'Expected a function'

  var nativeMax = Math.max
  var nativeNow = Date.now

  var now = nativeNow || function () {
    return new Date().getTime()
  }

  function debounce (func, wait, options) {
    var args
    var maxTimeoutId
    var result
    var stamp
    var thisArg
    var timeoutId
    var trailingCall
    var lastCalled = 0
    var maxWait = false
    var trailing = true

    if (typeof func !== 'function') {
      throw new TypeError(FUNC_ERROR_TEXT)
    }
    wait = wait < 0 ? 0 : (+wait || 0)
    if (options === true) {
      var leading = true
      trailing = false
    } else if (isObject(options)) {
      leading = !!options.leading
      maxWait = 'maxWait' in options && nativeMax(+options.maxWait || 0, wait)
      trailing = 'trailing' in options ? !!options.trailing : trailing
    }

    function cancel () {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
      if (maxTimeoutId) {
        clearTimeout(maxTimeoutId)
      }
      lastCalled = 0
      maxTimeoutId = timeoutId = trailingCall = undefined
    }

    function complete (isCalled, id) {
      if (id) {
        clearTimeout(id)
      }
      maxTimeoutId = timeoutId = trailingCall = undefined
      if (isCalled) {
        lastCalled = now()
        result = func.apply(thisArg, args)
        if (!timeoutId && !maxTimeoutId) {
          args = thisArg = undefined
        }
      }
    }

    function delayed () {
      var remaining = wait - (now() - stamp)
      if (remaining <= 0 || remaining > wait) {
        complete(trailingCall, maxTimeoutId)
      } else {
        timeoutId = setTimeout(delayed, remaining)
      }
    }

    function maxDelayed () {
      complete(trailing, timeoutId)
    }

    function debounced () {
      args = arguments
      stamp = now()
      thisArg = this
      trailingCall = trailing && (timeoutId || !leading)

      if (maxWait === false) {
        var leadingCall = leading && !timeoutId
      } else {
        if (!maxTimeoutId && !leading) {
          lastCalled = stamp
        }
        var remaining = maxWait - (stamp - lastCalled)
        var isCalled = remaining <= 0 || remaining > maxWait

        if (isCalled) {
          if (maxTimeoutId) {
            maxTimeoutId = clearTimeout(maxTimeoutId)
          }
          lastCalled = stamp
          result = func.apply(thisArg, args)
        } else if (!maxTimeoutId) {
          maxTimeoutId = setTimeout(maxDelayed, remaining)
        }
      }
      if (isCalled && timeoutId) {
        timeoutId = clearTimeout(timeoutId)
      } else if (!timeoutId && wait !== maxWait) {
        timeoutId = setTimeout(delayed, wait)
      }
      if (leadingCall) {
        isCalled = true
        result = func.apply(thisArg, args)
      }
      if (isCalled && !timeoutId && !maxTimeoutId) {
        args = thisArg = undefined
      }
      return result
    }

    debounced.cancel = cancel
    return debounced
  }

  function isObject (value) {
    // Avoid a V8 JIT bug in Chrome 19-20.
    // See https://code.google.com/p/v8/issues/detail?id=2291 for more details.
    var type = typeof value
    return !!value && (type === 'object' || type === 'function')
  }

  return debounce
})()

export default debounce
