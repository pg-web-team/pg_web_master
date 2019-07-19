import { http } from '../utils/http'
import Table from '../models/table'

interface GetTableListRequest {
  tableId: number
}

const requestTableList = (request: GetTableListRequest) => {
  const url = `http://localhost:3333/table?tableId=${request.tableId}`
  return http.get<Table[]>(url)
}

export default {
  requestTableList,
}
