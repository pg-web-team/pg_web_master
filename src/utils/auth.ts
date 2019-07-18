import { USER_INFO_STORAGE_KEY } from '../constants/AppConstant'

const storage = window.sessionStorage // window.localStorage

interface AuthenticationInfo {
  username: string
  token: string
}

const authorize = (username: string, token: string) => {
  if (!token) {
    throw new Error('Token can not be null.')
  }
  const authInfo: AuthenticationInfo = {
    username,
    token,
  }

  storage.setItem(USER_INFO_STORAGE_KEY, JSON.stringify(authInfo))
}

const logout = () => {
  storage.removeItem(USER_INFO_STORAGE_KEY)
}

const getAuthInfo = () => {
  try {
    const authInfo = storage.getItem(USER_INFO_STORAGE_KEY)
    if (authInfo) {
      return JSON.parse(authInfo)
    }
    return {
      username: '',
      token: '',
    }
  } catch (ex) {
    throw new Error(ex)
  }
}

const isAuthorized = () => getAuthInfo().token && true

const getToken = () => getAuthInfo().token

export default {
  authorize,
  logout,
  isAuthorized,
  getToken,
}
