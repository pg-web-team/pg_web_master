import React from 'react'
import { Route, Switch, BrowserRouter } from 'react-router-dom'

import App from '../components/home/Home'
import SurveyList from './survey/SurveyList'

const Root = () => (
  <BrowserRouter>
    <div>
      <Switch>
        <Route exact path="/" component={App} />
        <Route exact path="/home" component={App} />
        <Route path="/survey-list" component={SurveyList} />
      </Switch>
    </div>
  </BrowserRouter>
)

export default Root
