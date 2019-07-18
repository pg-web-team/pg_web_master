import React from 'react'
import { Button, notification } from 'antd'
import classNames from 'classnames'
import logo from './logo.svg'
import styles from './Home.module.scss'
import gStyles from '../../styles/colors.module.scss'

class App extends React.Component<{}> {
  private openNotification = () => {
    notification.open({
      message: 'Hi~~~',
      description: 'Ah Ha',
    })
  }

  public render() {
    const headerStyle = classNames({
      [styles.header]: true,
    })

    return (
      <div className={styles.App}>
        <header className={headerStyle}>
          <img src={logo} className={styles.logo} alt="logo" />
          <p>
            Edit
            <code> src/App.tsx </code>
            and save to reload....
            <Button
              type="primary"
              className={gStyles.danger}
              onClick={this.openNotification}
            >
              ButtonTester
            </Button>
          </p>
          <a
            className={styles.link}
            href="https://reactjs.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            Learn React
          </a>
        </header>
      </div>
    )
  }
}

export default App
