/**
 * Created by DemonRay on 2019/3/24.
 */

import memoize from './memorize'
import debounce from './debounce'
import throttle from './throttle'

const class2type = {}

const getProto = Object.getPrototypeOf

const toString = class2type.toString

const hasOwn = class2type.hasOwnProperty

const fnToString = hasOwn.toString

const ObjectFunctionString = fnToString.call(Object)

const isFunction = function isFunction (obj) {
  return typeof obj === 'function' && typeof obj.nodeType !== 'number'
}

const RGBToHex = (r, g, b) => ((r << 16) + (g << 8) + b).toString(16).padStart(6, '0')

const query = document.querySelectorAll.bind(document)

function isPlainObject (obj) {
  let proto, Ctor

  // Detect obvious negatives
  // Use toString instead of jQuery.type to catch host objects
  if (!obj || toString.call(obj) !== '[object Object]') {
    return false
  }

  proto = getProto(obj)

  // Objects with no prototype (e.g., `Object.create( null )`) are plain
  if (!proto) {
    return true
  }

  // Objects with prototype are plain iff they were constructed by a global Object function
  Ctor = hasOwn.call(proto, 'constructor') && proto.constructor
  return typeof Ctor === 'function' && fnToString.call(Ctor) === ObjectFunctionString
}

function extend () {
  let options; let name; let src; let copy; let copyIsArray; let clone
  let target = arguments[ 0 ] || {}
  let i = 1
  let length = arguments.length
  let deep = false

  // Handle a deep copy situation
  if (typeof target === 'boolean') {
    deep = target

    // Skip the boolean and the target
    target = arguments[ i ] || {}
    i++
  }

  // Handle case when target is a string or something (possible in deep copy)
  if (typeof target !== 'object' && !isFunction(target)) {
    target = {}
  }

  // Extend jQuery itself if only one argument is passed
  if (i === length) {
    /* eslint-disable */
    target = this
    i--
  }

  for (; i < length; i++) {
    // Only deal with non-null/undefined values
    if ((options = arguments[ i ]) != null) {
      // Extend the base object
      for (name in options) {
        src = target[ name ]
        copy = options[ name ]

        // Prevent never-ending loop
        if (target === copy) {
          continue
        }

        // Recurse if we're merging plain objects or arrays
        if (deep && copy && (isPlainObject(copy) ||
                    (copyIsArray = Array.isArray(copy)))) {
          if (copyIsArray) {
            copyIsArray = false
            clone = src && Array.isArray(src) ? src : []
          } else {
            clone = src && isPlainObject(src) ? src : {}
          }

          // Never move original objects, clone them
          target[ name ] = extend(deep, clone, copy)

          // Don't bring in undefined values
        } else if (copy !== undefined) {
          target[ name ] = copy
        }
      }
    }
  }

  // Return the modified object
  return target
}

function $ (id) { return document.getElementById(id) }

function once (dom, type, callback) {
  const handle = function () {
    callback()
    dom.removeEventListener(type, handle)
  }
  dom.addEventListener(type, handle)
}

function isNode (obj) {
  if (obj && obj.nodeType === 1) {
    if (window.Node && (obj instanceof Node)) {
      return true
    }
  }
}

function css (el, attr) {
  if (typeof attr === 'string') { // get
    var win = el.ownerDocument.defaultView
    return win.getComputedStyle(el, null)[ attr ]
  } else if (typeof attr === 'object') { // set
    for (var k in attr) {
      el.style[ k ] = attr[ k ]
    }
  }
}

function addClass (el, className) {
  el.classList.add(className)
}

function removeClass (el, className) {
  el.classList.remove(className)
}

function hasClass (el, className) {
  return el.classList.contains(className)
}

function offset (el) {
  const box = el.getBoundingClientRect()

  return {
    top: box.top + window.pageYOffset - document.documentElement.clientTop,
    left: box.left + window.pageXOffset - document.documentElement.clientLeft
  }
}

function guid () {
  function s4 () {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1)
  }

  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4()
}

export default {
  hasOwn,
  isFunction,
  query,
  isPlainObject,
  extend,
  css,
  addClass,
  removeClass,
  hasClass,
  offset,
  once,
  $,
  isNode,
  debounce,
  throttle,
  RGBToHex,
  memoize,
  guid
}
