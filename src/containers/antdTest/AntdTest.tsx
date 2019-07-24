import * as React from 'react'

// interface FooProp {
//     name: string
// }
// let ppp: FooProp = {
//     name: "哈哈"
// }
// const Buttoner: (prop: FooProp) => JSX.Element = (ppp: FooProp) => <button>{ppp.name}</button> //无状态组件1

// function AntdTest(): JSX.Element {
//     return (
//         <div style={{ color: 'red' }}>
//             <button>函数式无状态组件</button>
//         </div>
//     )
// }


interface IProps {
    name: string,
    sonToparent?: any
}

interface IState {
    color: string
}
class Aom extends React.Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);
        this.state = {
            color: "red"
        }
        this.onClickColor = this.onClickColor.bind(this);
    }
    onClickColor(ev: React.MouseEvent<HTMLButtonElement>) {//React.ChangeEvent 常用
        const { color } = this.state;
        // console.log(ev.target)
        if (color === "red") {
            this.setState({
                color: "green"
            });
        }
        if (color === "green") {
            this.setState({
                color: "red"
            });
        }
    }
    render() {
        const { name } = this.props;
        const { color } = this.state;
        return (
            <div>
                <span style={{ color }}>{name}</span>
                <button onClick={this.onClickColor}>变颜色</button>
            </div>
        );
    }
}
const But = () => {
    const but = React.createRef<HTMLButtonElement>();
    return (
        <div>
            <button ref={but} onClick={() => {
                if (but && but.current) {
                    if (but.current.nodeName === "BUTTON") {
                        alert("BUTTON");
                    }
                }
            }}> refButton</button>
        </div>
    )
}

class Child extends React.Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);
        this.state = {
            color: "Child传给父亲"
        }
    }
    emit(t: any) {
        this.props.sonToparent(t);
    }
    render() {
        const { color } = this.state;
        let defaultChild = "默认子元素"
        return (
            <div onClick={this.emit.bind(this, color)}>
                {defaultChild}
            </div>
        )
    }
}

class Parent extends React.Component<IProps, IState>{
    constructor(props: IProps) {
        super(props);
        this.state = {
            color: "Parent"
        }
    }
    display(data: any) {
        this.setState({ color: data })
    }
    render() {
        return (
            <div>
                <Child name="父传子" sonToparent={this.display.bind(this)}></Child>
                子传父：{this.state.color}
            </div>
        )
    }
}

class AntdTest extends React.Component<IProps, IState>{
    constructor(props: IProps) {
        super(props);
        this.state = {
            color: ""
        }
    }
    render() {
        return (
            <div>
                <Aom name="耶耶耶" ></Aom>
                <But></But>
                <Parent name=""></Parent>
            </div>
        )
    }
}
export default AntdTest;