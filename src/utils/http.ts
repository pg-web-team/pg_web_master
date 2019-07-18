/* eslint-disable no-param-reassign */
import axios, { AxiosResponse } from 'axios'
import { IS_MOCK, API_BASE_URL } from '../constants/AppConstant'
import * as AppUrl from '../constants/AppUrl'
import history from './history'
import auth from './auth'
import loading from './loading'

export interface HttpResponse<T> {
  code: 0
  message: ''
  data: T
}

const instance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

instance.interceptors.request.use(
  config => {
    if (auth.isAuthorized()) {
      config.headers.Authorization = `Bearer ${auth.getToken()}`
      config.headers['Cache-Control'] = 'no-cache'
    }
    return config
  },
  error => Promise.reject(error)
)

instance.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const errMsg = error.response
        ? error.response.data.message
        : error.message

      switch (error.response.status) {
        case 401:
          auth.logout()
          history.push({ pathname: AppUrl.LOGIN })
          break
        case 403:
          history.push({ pathname: AppUrl.FORBIDDEN })
          break
        default:
          // eslint-disable-next-line no-console
          console.log(errMsg)
          break
      }
    }
    return Promise.reject(error)
  }
)

function decorateData<T>(res: AxiosResponse<T>) {
  if (IS_MOCK) {
    return {
      message: '',
      code: 0,
      data: res.data as T,
    }
  }
  return res.data
}

function get<T>(url: string) {
  return new Promise<HttpResponse<T>>((resolve, reject) => {
    loading.show()
    instance
      .get(url)
      .then(res => {
        loading.hide()
        resolve(decorateData(res) as HttpResponse<T>)
      })
      .catch(() => {
        loading.hide()
        reject()
      })
  })
}

export const http = {
  get,
}
