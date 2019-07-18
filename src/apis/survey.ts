import { http } from '../utils/http'
import Survey from '../models/survey'

interface GetSurveyListRequest {
  surveyName: string
  startIndex: string
  pageSize: string
}

const requestSurveyList = (reqeust: GetSurveyListRequest) => {
  const url = `http://localhost:3333/survey?surveyName=${reqeust.surveyName}`
  return http.get<Survey[]>(url)
}

export default {
  requestSurveyList,
}
