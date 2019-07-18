import { Reducer } from 'redux'
import Survey from '../../models/survey'
import { AppThunkAction } from '../AppThunkAction'
import SurveyState from './surveyState'
import surveyApi from '../../apis/survey'
import {
  REQUEST_SURVEY_LIST,
  RECEVIE_SURVEY_LIST,
} from '../../constants/ActionTypes'

interface RequestSurveyListAction {
  type: typeof REQUEST_SURVEY_LIST
  payload: {
    surveyName: string
  }
}

interface ReceiveSurveyListAction {
  type: typeof RECEVIE_SURVEY_LIST
  payload: {
    surveyList: Survey[]
  }
}

type KnownAction = RequestSurveyListAction | ReceiveSurveyListAction

export const actionCreator = {
  requestSurveyList: (
    surveyName: string
  ): AppThunkAction<KnownAction> => dispatch => {
    dispatch({
      type: REQUEST_SURVEY_LIST,
      payload: {
        surveyName,
      },
    })

    surveyApi
      .requestSurveyList({ surveyName, startIndex: '1', pageSize: '10' })
      .then(res => {
        dispatch({
          type: RECEVIE_SURVEY_LIST,
          payload: {
            surveyList: res.data,
          },
        })
      })
  },
}

const initialState: SurveyState = {
  isLoading: false,
  surveyList: [],
}

export const SurveyReducer: Reducer<SurveyState, KnownAction> = (
  state: SurveyState = initialState,
  action: KnownAction
) => {
  switch (action.type) {
    case REQUEST_SURVEY_LIST:
      return {
        ...state,
        isLoading: true,
        surveyList: [],
      }
    case RECEVIE_SURVEY_LIST:
      return {
        ...state,
        isLoading: false,
        surveyList: action.payload.surveyList,
      }

    default:
      return state
  }
}
