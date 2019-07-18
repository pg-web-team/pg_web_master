/* eslint-disable @typescript-eslint/no-var-requires */
/* eslint-disable import/no-extraneous-dependencies */

import jsonServer from 'json-server'
import { db } from './db'

const server = jsonServer.create()
const router = jsonServer.router(db)
const middlewares = jsonServer.defaults()
const port = 3333

function isAuthorized() {
  return Math.floor(Math.random() * 10000) !== 8888
}

server.use(middlewares)
server.use((req, res, next) => {
  setTimeout(() => {
    if (isAuthorized()) {
      next()
    } else {
      res.sendStatus(401)
    }
  }, Math.random() * 3000)
})

server.use(router)
server.listen(port, () => {
  // eslint-disable-next-line no-console
  console.log(`JSON Server is running on port ${port}`)
})
