/* eslint-disable */
import {Node} from 'butterfly-dag';

import './base_node.scss';
import './symbol/iconfont'
import util from '@dataspherestudio/shared/common/util'

class BaseNode extends Node {
  constructor(opts) {
    super(opts);
    this.options = opts;
  }

  draw = (opts) => {
    let container = $('<div class="relation-node"></div>')
      .css('top', opts.top)
      .css('left', opts.left)
      .attr('id', opts.id)
      .addClass(opts.options.className);
    let text = `<span>${opts.options.name}</span>`
    if (opts.options.status !== 'ACTIVE') {
      text = `<span style="color:rgba(0,0,0,0.2)">${opts.options.name}</span>`
    } else if (opts.options.icon === 'icon-a-hivetable') {
      text = `<span id="${opts.options.model.guid}">${opts.options.name}</span>`
      util.Hub.$emit('register_click_hive_table', opts.options.model)
    }
    util.Hub.$emit('register_hover', opts.options.model)
    let logoContainer =
      $(`<div class="logo-container" title="${opts.options.name}">
        <svg class="icon" aria-hidden="true" style="float: left;font-size: 34px;">
            <use xlink:href="#${opts.options.icon}" />
        </svg>
        ${text}</div>`);
    logoContainer.addClass(opts.options.className);

    container.append(logoContainer);

    return container[0];
  }
}

export default BaseNode;
