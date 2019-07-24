import React, { Component } from 'react';
import { Table, Divider, Tag } from 'antd';

import tableStyle from './table.module.scss';

const rowSelection = {
    onChange: (selectedRowKeys: any, selectedRows: any) => {
        console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    }
};

interface tabprops {
    none: string
}
interface tabstate {
    data: any
}
class TableList extends React.Component<tabprops, tabstate>{
    constructor(tabprops: any) {
        super(tabprops);
        this.state = {
            data: this.datar
        }
    }
    public columns = [
        {
            title: 'Name',
            dataIndex: 'name',
            render: (text: any) => <span>{text}</span>,
        },
        {
            title: 'Age',
            dataIndex: 'age',
        },
        {
            title: 'Address',
            dataIndex: 'address',
        },
        {
            title: 'Tags',
            dataIndex: 'tags',
            render: (tags: any) => (
                <span>
                    {tags.map((tag: any) => {
                        let color = tag.length > 5 ? 'red' : 'green';
                        //tag是字符串?!。。。。。
                        return (
                            <Tag color={color} key={tag}>
                                {tag.toUpperCase()}
                            </Tag>
                        );
                    })}
                </span>
            ),
        },
        {
            title: 'Action',
            dataIndex: 'action',
            render: (text: any, record: any) => (
                <span>
                    <a href="javascript:;" onClick={this.deleteRow.bind(this, record.key)}>{record.oper}</a>
                </span>
            ),
        },
    ]
    public datar = [
        {
            key: 'a',
            name: 'John Brown',
            age: 32,
            address: 'New York No. 1 Lake Park',
            tags: ['nice', 'developer'],
            oper: "删除1"
        },
        {
            key: 'b',
            name: 'Jim Green',
            age: 42,
            address: 'London No. 1 Lake Park',
            tags: ['loser'],
            oper: "删除2"
        },
        {
            key: 'c',
            name: 'Joe Black',
            age: 32,
            address: 'Sidney No. 1 Lake Park',
            tags: ['cool', 'teacher'],
            oper: "删除3"
        },
    ];

    deleteRow(index: any, event: any) {
        let keys: any[] = [];
        this.state.data.forEach((i: any, num: any) => {
            return keys[num] = i.key;
        })
        let n = keys.indexOf(index);
        this.datar.splice(n, 1);
        this.setState({ data: this.datar })
        keys.splice(n, 1)
    }
    public render() {
        const { data } = this.state;
        return (
            <div className={tableStyle.taber}>
                {/* {JSON.stringify(data)} */}
                <Table rowSelection={rowSelection} columns={this.columns} dataSource={data} bordered size="middle" />
            </div>
        );
    }
}
export default TableList