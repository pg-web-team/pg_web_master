import Survey from '../../models/survey'

export default interface SurveyState {
  isLoading: boolean
  surveyList: Survey[]
}
