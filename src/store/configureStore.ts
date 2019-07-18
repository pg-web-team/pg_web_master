import {
  applyMiddleware,
  combineReducers,
  createStore,
  Middleware,
} from 'redux'
import { createLogger } from 'redux-logger'
import thunk from 'redux-thunk'

import reducers from './Reducers'

const rootReducer = combineReducers({ ...reducers })

export default function configureStore() {
  const middlewares: Middleware[] = [thunk]

  middlewares.push(createLogger()) // TODO: if not production

  return createStore(rootReducer, applyMiddleware(...middlewares))
}
