export default interface Table {
  tableId: number
  tableDetail: Detail[]
}

interface Detail {
  detailId: number
  tableName: string
  tableDate: string
}
