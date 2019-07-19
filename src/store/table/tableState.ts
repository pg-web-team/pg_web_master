import Table from '../../models/table'

export default interface TableState {
  isLoading: boolean
  tableList: Table[]
}
