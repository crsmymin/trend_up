// import react modules
import React, { Component } from "react";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class SearchKeyword extends Component {
  constructor(props) {
    super(props) 
  }

  _handleChange = (e) => {
    this.setState({
      searchValue: e.target.value
    })
  }

  _onKeyPress = (e) => {
    if(e.key === 'Enter') {
      console.log(this.state.searchValue)
      this.props.getSearchResultByKeywords(this.state.searchValue,1);
      $('.direct-btns .btn-s').removeClass('active');
		  $('.direct-btns .7').addClass('active');
    }
  }

  componentDidMount() {

  }

  render() {
    return(
      <div id="searchArea">
        <div className="wrap">
          <h4>
            궁금한 <strong>키워드</strong> 를 검색해보세요
          </h4>
          <input 
            id="searchField" 
            type="text" 
            placeholder="궁금한 검색어를 입력해주세요"
            onChange={this._handleChange}
            onKeyPress={this._onKeyPress}
            />
        </div>
      </div>
    )
  }
}
export default SearchKeyword;