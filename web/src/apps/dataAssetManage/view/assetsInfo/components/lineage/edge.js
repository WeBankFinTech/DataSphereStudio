import { Edge } from "butterfly-dag";

class RelationEdge extends Edge {
  draw(obj) {
    let path = super.draw(obj);
    if (this.options.color) {
      path.classList.add(this.options.color);
    }
    return path;
  }

  drawArrow(isShow) {
    let dom = super.drawArrow(isShow);
    if (this.options.color) {
      dom.classList.add(this.options.color);
    }
    return dom;
  }

  drawLabel(text) {
    let dom = null;
    if (text) {
      let dom = document.createElement("span");
      dom.classList.add("butterflies-label");
      dom.innerText = text;
    }
    return dom;
  }
}

export default RelationEdge;
