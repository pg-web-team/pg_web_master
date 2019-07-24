import React, { Component } from 'react';

interface Product {
    category: string;
    price: string;
    stocked: boolean;
    name: string;
}
// ProductCategoryRow
interface ProductCategoryRowProps {
    category: string;
}
class ProductCategoryRow extends React.Component<ProductCategoryRowProps, any>{
    public render() {
        return (
            <tr><th>{this.props.category}</th></tr>
        );
    }
}
// ProductRow
interface ProductRowProps {
    product: Product;
}
class ProductRow extends React.Component<ProductRowProps, any>{
    public render() {
        let name = this.props.product.stocked ? this.props.product.name : <span style={{ color: 'red' }}>{this.props.product.name}</span>;
        return (
            <tr>
                <td>{name}</td>
                <td>{this.props.product.price}</td>
            </tr>
        );
    }
}
// ProductTable
interface ProductTableProps {
    products: Product[];
    filterText: string;
    inStockOnly: boolean;
}
class ProductTable extends React.Component<ProductTableProps, any>{
    public render() {
        var rows: any = [];
        var lastCategory: any = null;
        const { products, filterText, inStockOnly } = this.props
        products.map(product => {
            if (product.name.indexOf(this.props.filterText) === -1 || (!product.stocked && this.props.inStockOnly)) {
                return;
            }
            if (product.category !== lastCategory) {
                rows.push(<ProductCategoryRow category={product.category} key={product.category} />);
            }
            rows.push(<ProductRow product={product} key={product.name} />);
            lastCategory = product.category;
        });
        return (
            <table>
                <thead>
                    <tr>
                        <th>名称</th>
                        <th>价格</th>
                    </tr>
                </thead>
                <tbody>{rows}</tbody>
            </table>
        );
    }
}
// SearchBar 
interface SearchBarProps {
    filterText: string;
    inStockOnly: boolean;
    onUserInput: (a: string, b: boolean) => any;
}
class SearchBar extends React.Component<SearchBarProps, any>{
    handleChange() {
        // // 函数调用
        // this.props.onUserInput(
        //     this.refs.filterTextInput,
        //     this.refs.inStockOnlyInput.checked
        // );
    }
    public render() {
        return (
            <form>
                <input type="text" placeholder="搜索···" value={this.props.filterText} ref="filterTextInput" onChange={this.handleChange.bind(this)} />
                <p>
                    <label>
                        <input type="checkbox" checked={this.props.inStockOnly} ref="inStockOnlyInput" onChange={this.handleChange.bind(this)} />只显示有货的
          </label>
                </p>
            </form>
        );
    }
}
// FilterableProductTable 
interface FilterableProductTableProps {
    products: Product[];
}
interface FilterableProductTableState {
    filterText: string;
    inStockOnly: boolean;
}
class FilterableProductTable extends React.Component<FilterableProductTableProps, FilterableProductTableState>{
    constructor(props: any, FilterableProductTableProps: any) {
        super(props);
        const products = PRODUCTS
        this.state = {
            filterText: '',
            inStockOnly: false
        };
    }
    public handleUserInput(filterText: any, inStockOnly: any) {
        this.setState({ filterText: filterText, inStockOnly: inStockOnly });
    }
    public render() {
        return (
            <div>
                <SearchBar filterText={this.state.filterText} inStockOnly={this.state.inStockOnly} onUserInput={this.handleUserInput.bind(this)} />
                <ProductTable products={this.props.products} filterText={this.state.filterText} inStockOnly={this.state.inStockOnly} />
            </div>
        );

    }
}
let PRODUCTS: Product[] = [
    { category: 'Sporting Goods', price: '$49.99', stocked: true, name: 'Football' },
    { category: 'Sporting Goods', price: '$9.99', stocked: true, name: 'Baseball' },
    { category: 'Sporting Goods', price: '$29.99', stocked: false, name: 'Basketball' },
    { category: 'Electronics', price: '$99.99', stocked: true, name: 'iPod Touch' },
    { category: 'Electronics', price: '$399.99', stocked: false, name: 'iPhone 5' },
    { category: 'Electronics', price: '$199.99', stocked: true, name: 'Nexus 7' }
];

// ReactDOM.render(
//     <FilterableProductTable products={PRODUCTS} />,
//     document.getElementById('example')
// );

export default FilterableProductTable