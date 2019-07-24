import React from 'react'
import { Route, Switch, BrowserRouter } from 'react-router-dom'

import App from '../components/home/Home'
import Test from '../components/Test/Test'
import SurveyList from './survey/SurveyList'
import TableList1 from './table/TableList'
import LearnPage from './learn/learn'

const Root = () => (
  <BrowserRouter>
    <div>
      <Switch>
        <Route exact path="/" component={App} />
        <Route exact path="/home" component={App} />
        <Route exact path="/test" component={Test} />
        <Route path="/survey-list" component={SurveyList} />
        <Route path="/table-list" component={TableList1} />
        <Route path="/learn" component={LearnPage} />
      </Switch>
    </div>
  </BrowserRouter>
)

export default Root
