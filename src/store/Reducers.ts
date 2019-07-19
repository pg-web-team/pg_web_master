import { SurveyReducer } from './survey'
import { TableReducer } from './table'

/**
 * The same properties name with Store.ts
 */
const reducers = {
  survey: SurveyReducer,
  table: TableReducer
}

export default reducers
