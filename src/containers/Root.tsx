import React from 'react'
import { Route, Switch, BrowserRouter } from 'react-router-dom'

import App from '../components/home/Home'
import Test from '../components/Test/Test'
import SurveyList from './survey/SurveyList'
import TableList from './table/TableList'
import tesT from '../components/table/main'

const Root = () => (
  <BrowserRouter>
    <div>
      <Switch>
        <Route exact path="/" component={App} />
        <Route exact path="/home" component={App} />
        <Route exact path="/test" component={Test} />
        <Route path="/survey-list" component={SurveyList} />
        <Route path="/table-list" component={TableList} />
        <Route exact path="/main" component={tesT as any} />
      </Switch>
    </div>
  </BrowserRouter>
)

export default Root
