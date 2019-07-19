import React from 'react'

class NameForm extends React.Component {

    handleSubmit = (event) => {
        alert('A name was submitted: ' + this.input.value);
        event.preventDefault();
        // console.log(this.input)
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <label>
                    Name:
                    <input type="text" ref={(input) => { return this.input = input }} />
                </label>
                <input type="submit" value="Submit" />
            </form>
        );
    }
}
class Child extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: "Child传给父亲"
        }
    }
    emit(t) {
        this.props.sonToparent(t);
    }
    render() {
        return (
            <div onClick={this.emit.bind(this, this.state.name)}>
                {this.props.p}
            </div>
        )
    }
}

class Parent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: "Parent"
        }
    }
    display(data) {
        this.setState({ name: data })
    }
    render() {
        return (
            <div>
                <Child p="父传子" sonToparent={this.display.bind(this)}></Child>
                子传父：{this.state.name}
                <NameForm></NameForm>
            </div>
        )
    }
}

export default Parent;


// import { Row, Col } from 'antd';
// import "../../index.scss";

// class Antd extends React.Component {
//     constructor(props) {
//         super(props);
//         this.state = {
//             name: "Antd"
//         }
//     }
//     render() {
//         return (
//             <div className="container">

//             </div>
//         )
//     }
// }
// export default Antd;