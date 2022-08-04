/* eslint-disable require-jsdoc */
function drawArrow(ctx, x1, y1, x2, y2, style, which, angle, d, line = true) {
  if (typeof x1 == 'string') {
    x1 = parseInt(x1);
  }
  if (typeof y1 == 'string') {
    y1 = parseInt(y1);
  }
  if (typeof x2 == 'string') {
    x2 = parseInt(x2);
  }
  if (typeof y2 == 'string') {
    y2 = parseInt(y2);
  }
  style = typeof style != 'undefined' ? style : 3;
  which = typeof which != 'undefined' ? which : 1;
  angle = typeof angle != 'undefined' ? angle : Math.PI / 9;
  d = typeof d != 'undefined' ? d : 10;
  let toDrawHead = typeof style != 'function' ? drawHead : style;
  let dist = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
  let ratio = (dist - d / 3) / dist;
  let tox, toy, fromx, fromy;
  if (which & 1) {
    tox = Math.round(x1 + (x2 - x1) * ratio);
    toy = Math.round(y1 + (y2 - y1) * ratio);
  } else {
    tox = x2;
    toy = y2;
  }

  if (which & 2) {
    fromx = x1 + (x2 - x1) * (1 - ratio);
    fromy = y1 + (y2 - y1) * (1 - ratio);
  } else {
    fromx = x1;
    fromy = y1;
  }


  ctx.beginPath();
  if (line) {
    ctx.moveTo(fromx, fromy);
    ctx.lineTo(tox, toy);
    ctx.stroke();
  }

  let lineangle = Math.atan2(y2 - y1, x2 - x1);
  let h = Math.abs(d / Math.cos(angle));
  if (which & 1) {
    let angle1 = lineangle + Math.PI + angle;
    let topx = x2 + Math.cos(angle1) * h;
    let topy = y2 + Math.sin(angle1) * h;
    let angle2 = lineangle + Math.PI - angle;
    let botx = x2 + Math.cos(angle2) * h;
    let boty = y2 + Math.sin(angle2) * h;
    toDrawHead(ctx, topx, topy, x2, y2, botx, boty, style);
  }

  if (which & 2) {
    let angle1 = lineangle + angle;
    let topx = x1 + Math.cos(angle1) * h;
    let topy = y1 + Math.sin(angle1) * h;
    let angle2 = lineangle - angle;
    let botx = x1 + Math.cos(angle2) * h;
    let boty = y1 + Math.sin(angle2) * h;
    toDrawHead(ctx, topx, topy, x1, y1, botx, boty, style);
  }
}
function drawHead(ctx, x0, y0, x1, y1, x2, y2, style) {
  if (typeof x0 == 'string') {
    x0 = parseInt(x0);
  }
  if (typeof y0 == 'string') {
    y0 = parseInt(y0);
  }
  if (typeof x1 == 'string') {
    x1 = parseInt(x1);
  }
  if (typeof y1 == 'string') {
    y1 = parseInt(y1);
  }
  if (typeof x2 == 'string') {
    x2 = parseInt(x2);
  }
  if (typeof y2 == 'string') {
    y2 = parseInt(y2);
  }

  ctx.save();
  ctx.beginPath();
  ctx.moveTo(x0, y0);
  ctx.lineTo(x1, y1);
  ctx.lineTo(x2, y2);

  switch (style) {
    case 0:
      var backdist = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
      ctx.arcTo(x1, y1, x0, y0, 0.55 * backdist);
      ctx.fill();
      break;
    case 1:
      ctx.beginPath();
      ctx.moveTo(x0, y0);
      ctx.lineTo(x1, y1);
      ctx.lineTo(x2, y2);
      ctx.lineTo(x0, y0);
      ctx.fill();
      break;
    case 2:
      ctx.stroke();
      break;
    case 3:
      var cpx = (x0 + x1 + x2) / 3;
      var cpy = (y0 + y1 + y2) / 3;
      ctx.quadraticCurveTo(cpx, cpy, x0, y0);
      ctx.fill();
      break;
    case 4:
      var cp1x, cp1y, cp2x, cp2y;
      var shiftamt = 5;
      if (x2 == x0) {
        backdist = y2 - y0;
        cp1x = (x1 + x0) / 2;
        cp2x = (x1 + x0) / 2;
        cp1y = y1 + backdist / shiftamt;
        cp2y = y1 - backdist / shiftamt;
      } else {
        backdist = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
        var xback = (x0 + x2) / 2;
        var yback = (y0 + y2) / 2;
        var xmid = (xback + x1) / 2;
        var ymid = (yback + y1) / 2;
        var m = (y2 - y0) / (x2 - x0);
        var dx = backdist / (2 * Math.sqrt(m * m + 1)) / shiftamt;
        var dy = m * dx;
        cp1x = xmid - dx;
        cp1y = ymid - dy;
        cp2x = xmid + dx;
        cp2y = ymid + dy;
      }
      ctx.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x0, y0);
      ctx.fill();
      break;
  }
  ctx.restore();
}

function drawLink(ctx, link, state ) {
  let {nodes = [], lineWidth, stroke, fill, linkType} = link;
  // 开始画线
  ctx.strokeStyle = stroke;
  ctx.fillStyle = fill;
  ctx.lineWidth = lineWidth * state.baseOptions.pageSize;
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  if (linkType === 'bezier') {
    ctx.beginPath();
    ctx.moveTo(nodes[0].x, nodes[0].y);
    ctx.quadraticCurveTo(nodes[1].x, nodes[1].y, nodes[2].x, nodes[2].y);
    ctx.stroke();
    drawArrow(
      ctx,
      nodes[1].x,
      nodes[1].y,
      nodes[2].x,
      nodes[2].y,
      3,
      1,
      Math.PI / 8,
      10 * state.baseOptions.pageSize,
      false
    );
  } else {
    nodes.forEach((node, index) => {
      if (index == 0) {
        ctx.beginPath();
        ctx.moveTo(node.x, node.y );
      } else if (index == nodes.length - 1) {
        drawArrow(
          ctx,
          nodes[index - 1].x,
          nodes[index - 1].y,
          node.x,
          node.y,
          3,
          1,
          Math.PI / 8,
          10 * state.baseOptions.pageSize
        );
      } else {
        ctx.lineTo(node.x, node.y );
        ctx.stroke();
      }
    });
  }

}

function drawLinkers(ctx, links, state) {
  // 先画未选中的
  links = links.filter(link => {
    if (link.fill =='#3399ff') { // 选中的颜色
      return true
    } else {
      drawLink(ctx, link, state)
      return false
    }
  })
  links.forEach(link => {
    drawLink(ctx, link, state)
  })
}

export default drawLinkers
