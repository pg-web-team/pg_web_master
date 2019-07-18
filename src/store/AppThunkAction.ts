import { AppState } from './Store'
// eslint-disable-next-line import/prefer-default-export
export type AppThunkAction<TAction> = (
  dispatch: (action: TAction) => void,
  getState: () => AppState
) => void
