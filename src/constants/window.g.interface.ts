declare global {
  interface Window {
    g: {
      env: string
      isMock: boolean
      isDev: boolean
      isQa: boolean
      isPrd: boolean
      webRootUrl: string
      apiBaseUrl: string
      virtualDirectory: string
    }
  }
}

export {}
