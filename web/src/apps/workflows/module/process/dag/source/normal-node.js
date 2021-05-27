import $ from 'jquery';
import {Node} from 'butterfly-dag';

import './normal-node.scss';

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
      .addClass(opts.options.className);

    let logoContainer = $(`<div class="logo-container">${opts.options.label}</div>`);
    logoContainer.addClass(opts.options.className);

    container.append(logoContainer);

    return container[0];
  }
}

export default NormalNode
