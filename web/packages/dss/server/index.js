const Koa = require('koa')
const url = require('url');
const Router = require('koa-router')
const config = require('./config/default')
const mysql = require('./mysql')
const bodyParser = require('koa-body')
const router = new Router({
  prefix: '/application'
})

const app =  new Koa()

// app.use(json())
app.use(async (ctx, next) => {
  await next()
})

router.get('/query', async (ctx, next) => {
  let id = url.parse(ctx.request.url, true).query.id
  let data = id ? await mysql.query(id) : await mysql.queryAll()
  ctx.response.body = {
    "code": 0,
    "status": 0,
    "data": data,
    "message": "success"
  }
})

router.post('/update', async (ctx, next) => {
  let data = await mysql.createApplication(ctx.request.body)
  ctx.response.body = {
    "code": 0,
    "status": 0,
    "message": "success"
  }
})

router.get('/getMenu', async (ctx, next) => {
  let data = await mysql.queryMenu()
  ctx.response.body = {
    "code": 0,
    "status": 0,
    "data": data,
    "message": "success"
  }
})

app.use(bodyParser({
  multipart: true // 支持表单解析
}))
app.use(router.routes())

app.listen(config.port)

console.log(`listening on port ${config.port}`)
