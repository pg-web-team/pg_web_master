export default interface Survey {
  surveyId: string
  surveyName: string
  storeId: string
  surveyStartDate: string
  surveyEndDate: string
  questions: SurveyQuestion[]
}

interface SurveyQuestion {
  questionId: string
  categoryEn: string
  categoryName: string
  questionDesc: string
  options: SurveyOption[]
}

interface SurveyOption {
  optionId: string
  optionDesc: string
  min: number
  max: number
  isIncentiveGold: string
  goldValue: string
  isIncentivePoint: string
  pointValue: string
  isTarget: string
  isPhotoRequired: string
}
