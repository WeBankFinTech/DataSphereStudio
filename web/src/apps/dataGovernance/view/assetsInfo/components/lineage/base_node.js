/* eslint-disable */
import {Node} from 'butterfly-dag';

import './base_node.scss';

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

    let logoContainer = $(`<div class="logo-container" title="${opts.options.name}"><span style="margin-right: 5px">${opts.options.icon}</span>${opts.options.name}</div>`);
    logoContainer.addClass(opts.options.className);

    container.append(logoContainer);

    return container[0];
  }
}

export default BaseNode;
