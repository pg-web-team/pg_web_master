const env = 'mock' // mock, sit, dev ,qa, prd ...

function getWebRootUrl() {
  if (env === 'mock') {
    return 'http://localhost:3000'
  }
  return ''
}

function getApiBaseUrl() {
  if (env === 'mock') {
    return 'http://localhost:3333'
  }
  return ''
}

function getVirtualDirectory() {
  if (env === 'mock') {
    return ''
  }
  return ''
}

window.g = {
  ENV: env,
  isMock: env === 'mock',
  isDev: env === 'dev',
  isQa: env === 'qa',
  isPrd: env === 'prd',
  webRootUrl: getWebRootUrl(),
  apiBaseUrl: getApiBaseUrl(),
  virtualDirectory: getVirtualDirectory(),
}
