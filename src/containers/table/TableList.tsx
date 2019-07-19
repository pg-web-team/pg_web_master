import React from 'react'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { RouteComponentProps } from 'react-router-dom'
import { AppState } from '../../store/Store'
import TableState from '../../store/table/tableState'
import { actionCreator } from '../../store/table'

type Props = TableState &
  typeof actionCreator &
  RouteComponentProps<{ id: string }>

class TableList extends React.Component<Props, {}> {
  public componentWillMount() {
    const { requestTableList } = this.props
    requestTableList(1)
    requestTableList(2)
    requestTableList(3)
    // const { tempList} =
  }

  public render() {
    const { isLoading, tableList } = this.props
    return (
      <div>
        {/* <span>{JSON.stringify(tableList)}</span> */}
        <table>
          {
            tableList.map(tab => {
              return (
                <tr>
                  {tab.tableDetail.map(detail => {
                    return (
                      <td><span>{`${detail.detailId}---${detail.tableDate}---${detail.tableName}`}</span></td>
                    )
                  }
                  )}
                </tr>
              )

            })
          }
        </table>
        {isLoading && <span>hahahahaha........</span>}

      </div>
    )
  }
}


export default connect(
  (state: AppState) => state.table,
  dispatch => bindActionCreators(actionCreator, dispatch)
)(TableList)


