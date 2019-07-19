import { Reducer } from 'redux'
import Table from '../../models/table'
import { AppThunkAction } from '../AppThunkAction'
import TableState from './tableState'
import tableApi from '../../apis/table'
import {
  REQUEST_TABLE_LIST,
  RECEVIE_TABLE_LIST,
} from '../../constants/ActionTypes'
import table from 'apis/table';

interface RequestTableListAction {
  type: typeof REQUEST_TABLE_LIST
  payload: {
    tableId: number
  }
}

interface ReceiveTableListAction {
  type: typeof RECEVIE_TABLE_LIST
  payload: {
    tableList: Table[]
  }
}

type KnownAction = RequestTableListAction | ReceiveTableListAction

export const actionCreator = {
  requestTableList: (
    tableId: number
  ): AppThunkAction<KnownAction> => dispatch => {
    dispatch({
      type: REQUEST_TABLE_LIST,
      payload: {
        tableId,
      },
    })

    tableApi
      .requestTableList({ tableId })
      .then(res => {
        dispatch({
          type: RECEVIE_TABLE_LIST,
          payload: {
            tableList: res.data,
          },
        })
      })
  },
}

const initialState: TableState = {
  isLoading: false,
  tableList: [],
}

export const TableReducer: Reducer<TableState, KnownAction> = (
  state: TableState = initialState,
  action: KnownAction
) => {
  switch (action.type) {
    case REQUEST_TABLE_LIST:
      return {
        ...state,
        isLoading: true,
        tableList: [],
      }
    case RECEVIE_TABLE_LIST:
      return {
        ...state,
        isLoading: false,
        tableList: action.payload.tableList,
      }

    default:
      return state
  }
}
