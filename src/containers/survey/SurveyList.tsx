import React from 'react'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { RouteComponentProps } from 'react-router-dom'
import { AppState } from '../../store/Store'
import SurveyState from '../../store/survey/surveyState'
import { actionCreator } from '../../store/survey'

type Props = SurveyState &
  typeof actionCreator &
  RouteComponentProps<{ id: string }>

class SurveyList extends React.Component<Props, {}> {
  public componentWillMount() {
    const { requestSurveyList } = this.props
    requestSurveyList('Survey1')
  }

  public render() {
    const { isLoading, surveyList } = this.props
    return (
      <div>
        {surveyList &&
          surveyList.map(survey => {
            return (
              <div key={survey.surveyId}>
                {`${survey.surveyName} - ${survey.surveyStartDate} - ${survey.surveyEndDate}`}
              </div>
            )
          })}
        {isLoading && <span>Loading........</span>}
      </div>
    )
  }
}

export default connect(
  (state: AppState) => state.survey,
  dispatch => bindActionCreators(actionCreator, dispatch)
)(SurveyList)
