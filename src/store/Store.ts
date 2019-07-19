import SurveyState from './survey/surveyState'
import TableState from './table/tableState'
import table from 'apis/table';

/**
 * The same properties name with reducers.ts
 */
// eslint-disable-next-line import/prefer-default-export
export interface AppState {
  survey: SurveyState,
  table: TableState
}
