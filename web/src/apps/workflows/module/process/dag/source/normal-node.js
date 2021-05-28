import $ from 'jquery'
import {Node} from 'butterfly-dag'

import './normal-node.scss'
import util from '@/common/util'

class NormalNode extends Node {
  constructor(opts) {
    super(opts);
    this.options = opts;
  }

  draw = (opts) => {
    let container = $('<div class="relation-node"></div>')
      .css('top', opts.top)
      .css('left', opts.left)
      .attr('id', opts.id)
      .addClass(opts.options.className)
      .on('click', () => {
        util.Hub.$emit('dagLog', opts.options)
      })
    let icon = '<i></i>',
      state = '<span></span>'
    if (opts.options.node_type === 'SHELL') {
      icon = '<i style="color:rgb(121, 113, 246);margin-right: 5px;" class="ivu-icon ivu-icon-md-code-working"></i>'
    }
    if (opts.options.state) {
      state = `<i title='${opts.options.state.desc}' style='color:${opts.options.state.color};margin-left: 5px' class="ivu-icon ivu-icon-ios-radio-button-on"></i>`
    }
    let logoContainer = $(`<div class="logo-container">${icon}<span>${opts.options.label}</span>${state}</div>`);
    logoContainer.addClass(opts.options.className);

    container.append(logoContainer);

    return container[0];
  }
}

export default NormalNode
