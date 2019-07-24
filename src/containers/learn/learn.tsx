import React from 'react'
import { Select } from 'antd';
import { DatePicker } from 'antd';
import { Upload, Icon, message } from 'antd';
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import { RouteComponentProps } from 'react-router-dom'
import auth from 'utils/auth';
// import { Map } from 'antd';
import { Button } from 'antd';



class App extends React.Component<any, any> {
    state = {
        loading: false,
        iconLoading: false,
    };

    public enterLoading() {
        this.setState({ loading: true });
    };

    public enterIconLoading() {
        this.setState({ iconLoading: true });
    };

    public render() {
        return (
            <div>
                <Button type="primary" loading={this.state.loading} onClick={this.enterLoading} style={{ marginRight: 10 }}>
                    保存
                </Button>
                <Button type="primary" loading={this.state.loading} onClick={this.enterLoading}>
                    取消
                </Button>
            </div >
        );
    }
}

class Avatar extends React.Component<any, any> {
    state = {
        loading: false,
    };
    public getBase64(img: any, callback: any) {
        const reader = new FileReader();
        reader.addEventListener('load', () => callback(reader.result));
        reader.readAsDataURL(img);
    }

    public beforeUpload(file: any) {
        const isJPG = file.type === 'image/jpeg';
        if (!isJPG) {
            message.error('You can only upload JPG file!');
        }
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
            message.error('Image must smaller than 2MB!');
        }
        return isJPG && isLt2M;
    }


    public handleChange(info: any) {
        if (info.file.status === 'uploading') {
            this.setState({ loading: true });
            return;
        }
        if (info.file.status === 'done') {
            // Get this url from response in real world.
            this.props.getBase64(info.file.originFileObj, (imageUrl: any) =>
                this.setState({
                    imageUrl,
                    loading: false,
                }),
            );
        }
    };

    public render() {
        const uploadButton = (
            <div>
                {/* <Icon type={this.state.loading ? 'loading' : 'plus'} /> */}
                <div className="ant-upload-text">上传封面图片</div>
            </div>
        );
        const { imageUrl }: any = this.state;
        return (
            <Upload
                name="avatar"
                listType="picture-card"
                className="avatar-uploader"
                showUploadList={false}
                action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                beforeUpload={this.props.beforeUpload}
                onChange={this.handleChange}
            >
                {imageUrl ? <img src={imageUrl} alt="avatar" /> : uploadButton}
            </Upload>
        );
    }
}


class LearnPage extends React.Component<any, any> {

    public select_data = [
        {
            '品类1': [
                {
                    '洗面奶': ['视频_洗面奶1', '视频_洗面奶2', '视频_洗面奶3']
                },
                {
                    '护发素': ['视频_护发素1', '视频_护发素2', '视频_护发素3']
                },
            ]
        },
        {
            '品类2': [
                {
                    '沐浴露': ['视频_沐浴露1', '视频_沐浴露2', '视频_沐浴露3']
                },
                {
                    '香水': ['视频_香水1', '视频_香水2', '视频_香水3']
                },
            ]
        },
        {
            '品类3': [
                {
                    '防嗮霜': ['视频_防晒1', '视频_防晒2', '视频_防晒3']
                },
                {
                    '毛巾': ['视频_毛巾1', '视频_毛巾2', '视频_毛巾3']
                },
            ]
        },
    ];

    constructor(props: any) {
        super(props);
        this.state = {
            data: this.select_data,
        }
    }
    public handleSelectChange(value: any) {
        console.log(`selected ${value}`);
    }
    public onDateChange(date: any, dateString: any) {
        console.log(date, dateString);
    }

    // state = {
    //     cities: cityData[provinceData[0]],
    //     secondCity: cityData[provinceData[0]][0],
    // };

    // public handleLiandongChange(value: any) {
    //     this.setState({
    //         two_tree: level_tree_two.get(Key: value),
    //         secondCity: cityData[value][0],
    //     });
    // };

    // onSecondCityChange(value: any) {
    //     this.setState({
    //         secondCity: value,
    //     });
    // };

    selectIt(list_array: any) {

        for (let key of list_array) {

            console.log({ key });
            // return (
            //     <span>{key}</span>
            // )
        }
    }

    public render() {
        const { Option } = Select;
        // const { select_data_one } = ;
        const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
        let someArray = [1, "string", false];

        return (

            <div style={{ margin: '0,auth' }}>

                <span>{JSON.stringify(this.state.data)}</span>
                {
                    this.state.data.map((tab: any) => {
                        return (
                            <span></span>
                        )
                    }
                    )
                    // this.selectIt(this.state.data)    
                    // this.state.data.map((select_tab: any) => {
                    //     return (
                    // this.selectIt(this.state.data)
                    //         // <span>123{JSON.stringify(select_tab.keys())}</span>
                    //     )
                    // })
                }
                <div style={{ width: 1000, marginRight: 10 }}>
                    <span>一级分类：</span>
                    <Select defaultValue="品类课程" style={{ width: 120, marginRight: 10 }} onChange={this.handleSelectChange}>
                        <Option value="品类1">品类1</Option>
                        <Option value="品类2">品类2</Option>
                        <Option value="品类3">品类3</Option>
                    </Select>
                    <span>二级分类：</span>
                    <Select defaultValue="洗漱用品" style={{ width: 120, marginRight: 10 }} onChange={this.handleSelectChange}>
                        <Option value="护发素">护发素</Option>
                        <Option value="洗面奶">洗面奶</Option>
                    </Select>
                    <span>三级分类：</span>
                    <Select defaultValue="培训视频" style={{ width: 120, marginRight: 10 }} onChange={this.handleSelectChange}>
                        <Option value="视频1">视频1</Option>
                        <Option value="视频2">视频2</Option>
                    </Select>
                </div>
                <div>
                    <span>课程名：</span>
                    <input style={{ width: 400, marginRight: 10 }}></input>
                </div>
                <div>
                    <span>文档有效期：</span>
                    <DatePicker onChange={this.onDateChange} placeholder="开始日期" />
                    <span>至</span>
                    <DatePicker onChange={this.onDateChange} placeholder="结束日期" />
                </div>
                <div><Avatar /></div>
                <div>
                    <span>简介：</span><br></br>
                    <textarea style={{ width: 500, marginRight: 10 }}></textarea>
                </div>
                <App></App>
            </div>
        )
    }
}

export default LearnPage