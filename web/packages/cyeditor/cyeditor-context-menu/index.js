import utils from '../utils'

const defaults = {
  menus: [
    {
      id: 'remove',
      content: 'remove',
      disabled: false,
      divider: true
    },
    {
      id: 'hide',
      content: 'hide',
      disabled: false
    }
  ],
  beforeShow: (e) => { return e.target.isNode() || e.target.isEdge() },
  beforeClose: () => { return true }
}

class ContextMenu {
  constructor(cy, params) {
    this.cy = cy
    this._options = Object.assign({}, defaults, params)
    this._listeners = {}
    this._init()
  }
  _init() {
    this._initContainer()
    this._initDom()
    this._initEvents()
  }

  _initContainer() {
    this._container = this.cy.container()
    this.ctxmenu = document.createElement('div')
    this.ctxmenu.className = 'cy-editor-ctx-menu'
    this._container.append(this.ctxmenu)
  }

  _initDom() {
    function getSvgIcon(icon) {
      return `<svg aria-hidden="true" class="icon designer-menu-li-icon"><use xlink:href="#icon-${icon}"></use></svg>`
    }
    this.ctxmenu.innerHTML = this._options.menus.reduce((str, item) => {
      return str + `<div class="ctx-menu-item ${item.disabled ? 'ctx-menu-item-disabled' : ''}" data-menu-item="${item.id}">
                  ${getSvgIcon(item.icon)}
                  <span class="ctx-menu-item-title">${item.content}</span>
                  ${item.children && item.children.length ? '>' : ''}
                  ${ item.children && item.children.length ?
                    `<div class="ctx-submenu-wrap">
                      ${item.children.reduce((text, child) => text + `<div class="ctx-submenu-item" data-menu-item="${item.id}" data-submenu-item="${child.value}">${getSvgIcon(child.icon)}<span>${child.text}</span></div>`, '')}
                    </div>` :
                    ''
                  }
                </div>`;
    }, '');
  }

  _initEvents() {
    this._listeners.eventCyTap = (event) => {
      this.renderedPos = event.renderedPosition || event.cyRenderedPosition
      let left = this.renderedPos.x
      let top = this.renderedPos.y
      utils.css(this.ctxmenu, {
        top: top + 'px',
        left: (left + 200) + 'px'
      })
      this.show(event)
    }
    this._listeners.eventTapstart = (e) => {
      this.close(e)
    }
    this._listeners.click = (e) => {
      e.stopPropagation();
      let target = e.target;
      let id = target.getAttribute('data-menu-item');
      if (!id) {
        target = e.target.parentNode;
        id = target.getAttribute('data-menu-item');
      }
      let menuItem = this._options.menus.find(item => item.id === id);
      const subId = target.getAttribute('data-submenu-item');
      if (subId) {
        menuItem = menuItem.children.find(item => item.value === subId);
        menuItem.id = subId;
      }
      const pan = this.triggerEvt.cy.pan()
      const zoom = this.triggerEvt.cy.zoom()
      this.cy.trigger('cyeditor.ctxmenu', {
        menuItem,
        target: this.triggerEvt.target,
        event: {
          offsetX: (this.renderedPos.x - pan.x) / zoom,//+ 75 ,
          offsetY: (this.renderedPos.y - pan.y) / zoom  //+ 20s
        }
      })
      this.close(e);
    }
    this.ctxmenu.addEventListener('mousedown', this._listeners.click)
    this.cy.on('cxttap', this._listeners.eventCyTap)
    this.cy.on('tapstart', this._listeners.eventTapstart)
  }

  disable(id, disabled = true) {
    const items = utils.query(`.cy-editor-ctx-menu [data-menu-item=${id}]`)
    items.forEach(menuItem => {
      if (disabled) {
        utils.addClass(menuItem, 'ctx-menu-item-disabled')
      } else {
        utils.removeClass(menuItem, 'ctx-menu-item-disabled')
      }
    })
  }

  changeMenus(menus) {
    this._options.menus = menus
    this._initDom()
  }

  show(e) {
    if (e.target.data().type === 'edgehandle') return
    this.triggerEvt = e;
    if (typeof this._options.beforeShow === 'function' && !this.isShow) {
      const show = this._options.beforeShow(e, this._options.menus.slice(0))
      if (!show) return
      if (show && show.then) {
        show.then((res) => {
          if (res) {
            utils.css(this.ctxmenu, {
              display: 'block'
            })
            this.isShow = true
          }
        })
        return
      }
      if (Array.isArray(show) && show.length) {
        this.changeMenus(show)
      } else {
        return
      }
      utils.css(this.ctxmenu, {
        display: 'block'
      })
      this.isShow = true
    }
  }

  close(e) {
    if (typeof this._options.beforeShow === 'function' && this.isShow) {
      const close = this._options.beforeClose(e)
      if (close === true) {
        utils.css(this.ctxmenu, {
          display: 'none'
        })
        this.isShow = false
      } else if (close.then) {
        close.then(() => {
          utils.css(this.ctxmenu, {
            display: 'none'
          })
          this.isShow = false
        })
      }
    }
  }

  destroy() {
    this.ctxmenu.removeEventListener('mousedown', this._listeners.click)
    this.cy.off('tapstart', this._listeners.eventTapstart)
    this.cy.off('cxttap', this._listeners.eventCyTap)
  }
}

export default (cytoscape) => {
  if (!cytoscape) {
    return
  }

  cytoscape('core', 'contextMenu', function (options) {
    return new ContextMenu(this, options)
  })
}
