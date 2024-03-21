/* eslint-disable no-mixed-operators */
export class EventBus {
  constructor () {
    this.events = {}
  }

  /**
   * Adds listener to EventBus
   * @param {string} type The name of the event to listen for
   * @param {function} callback Callback to call when event was triggered
   * @param {object} scope The scope in which the callback shall be executed
   * @param  {...any} args Any number of args to be passed to the callback
   */
  on (type, callback, scope, ...args) {
    if (typeof this.events[type] === 'undefined') { // Check if there is already event of this type registered
      this.events[type] = [] // If not, create array for it
    }
    this.events[type].push({
      scope,
      callback,
      args
    }) // Finally push new event to events array
  }

  /**
   * Removes listener from EventBus
   * @param {string} type The name of the event to remove
   * @param {function} callback Callback of the event to remove
   * @param {object} scope The scope of the to be removed event
   */
  off (type, callback, scope) {
    if (typeof this.events[type] === 'undefined') { // Check if event of this type exists
      return // If not just return
    }

    // keep all elements that aren't equal to the passed event
    const filterFn = event => event.scope !== scope || event.callback !== callback
    this.events[type] = this.events[type].filter(filterFn)
  }

  /**
   * Checks if the passed event is registered in the EventBus
   * @param {string} type Type of the to be checked event
   * @param {callback} callback Callback of the to be checked event
   * @param {object} scope Scope of the to be checked event
   */
  has (type, callback, scope) {
    if (typeof this.events[type] === 'undefined') { // Check if the passed type even exists
      return false // If not, quit method
    }

    // If callback and scope are undefined then every registered event is match, thus any event of the type matches
    let numOfCallbacks = this.events[type].length
    if (callback === undefined && scope === undefined) { // If scope and callback are not defined
      return numOfCallbacks > 0 // If there are any callbacks we can be sure it matches the passed one
    }

    const conditionFn = event => {
      const scopeIsSame = scope ? event.scope === scope : true // Check if scope is equal to the one passed
      const callbackIsSame = event.callback === callback // Check if callback is equal to the one passed
      if (scopeIsSame && callbackIsSame) { // Check if current event and passed event are equal
        return true // If so, break loop and return true
      }
    }
    return this.events[type].some(conditionFn)
  }

  /**
   * Emits an event on the EventBus
   * @param {string} type Type of event to emit
   * @param {object} target The caller
   * @param {...any} args Any number of args to be passed to the callback
   */
  emit (type, target, ...args) {
    if (typeof this.events[type] === 'undefined') { // Check if any event of the passed type exists
      return // If not, quit method
    }

    let bag = {
      type,
      target
    }

    const events = this.events[type].slice() // Little hack to clone array

    for (const event of events) { // Iterate all events
      if (event && event.callback) { // Check if callback of event is set
        event.callback.apply(event.scope, [bag, ...args, ...event.args]) // Call callback
      }
    }
  }

  debug () {
    let str = ''
    for (const [type, events] of Object.entries(this.events)) {
      for (const event of events) {
        let className = event.scope && event.scope.constructor.name || 'Anonymous'
        str += `${className} listening for "${type}"\n`
      }
    }
    return str
  }

  clear() {
    this.events = {}
  }
}

export default EventBus
