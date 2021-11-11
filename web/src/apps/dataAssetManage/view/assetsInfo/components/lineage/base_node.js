/* eslint-disable */
import { Node } from "butterfly-dag";

import "./base_node.scss";
import "./symbol/iconfont";

class BaseNode extends Node {
  constructor(opts) {
    super(opts);
    this.options = opts;
  }

  draw = opts => {
    let container = document.createElement("div");
    container.classList.add("relation-node");
    container.classList.add(opts.options.className);
    container.style.top = opts.top + "px";
    container.style.left = opts.left + "px";
    container.setAttribute("id", opts.id);
    let text = `<span>${opts.options.name}</span>`;
    if (opts.options.status !== "ACTIVE") {
      text = `<span style="color:rgba(0,0,0,0.2)">${opts.options.name}</span>`;
    }
    let logoContainer = new DOMParser().parseFromString(
      `<div class="logo-container" title="${opts.options.name}">
        <svg class="icon" aria-hidden="true" style="float: left;font-size: 34px;">
          <use xlink:href="#${opts.options.icon}" />
        </svg>
        ${text}
      </div>`,
      "text/html"
    ).body.firstChild;
    logoContainer.classList.add(opts.options.className);
    container.appendChild(logoContainer);
    return container;
  };
}

export default BaseNode;
