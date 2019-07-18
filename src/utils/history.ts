import { createBrowserHistory } from 'history'
import * as AppUrl from '../constants/AppUrl'

export default createBrowserHistory({
  basename: AppUrl.HOME,
})
