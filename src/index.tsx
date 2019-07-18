import 'core-js'

import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import configureStore from './store/configureStore'
import Root from './containers/Root'
import './index.scss'

const store = configureStore()

const rootElement = document.getElementById('root')

ReactDOM.render(
  <Provider store={store}>
    <Root />
  </Provider>,
  rootElement
)
