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
      icon = '<i style="color:rgb(100, 100, 100);margin-right: 10px;font-size: 13px;position: relative;top: 1px;" class="iconfont icon-shell"></i>'
    }
    if (opts.options.state) {
      state = `<i title='${opts.options.state.desc}' style='color:${opts.options.state.color};margin-left: 8px;font-size: 13px;' class='iconfont ${opts.options.state.icon}'></i>`
    }
    let logoContainer = $(`<div class="logo-container">${icon}<span>${opts.options.label}</span>${state}</div>`);
    logoContainer.addClass(opts.options.className);

    container.append(logoContainer);

    return container[0];
  }
}

export default NormalNode
