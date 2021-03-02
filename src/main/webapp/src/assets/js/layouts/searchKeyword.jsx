// import react modules
import React, { Component } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class SearchKeyword extends Component {
  constructor(props) {
    let today = new Date();  
    let start_date = new Date(today);
    start_date.setDate(start_date.getDate()-7);
    super(props)
    this.state = {
      startDate : start_date,
      endDate : today,
    };
  }

  _handleChange = (e) => {
    this.setState({
      searchValue: e.target.value
    })
  }

  _onKeyPress = (e) => {
    if(e.key === 'Enter') {
      this.props.getSearchResultByKeywords(this.state.searchValue);
    }
  }

  _onChangeDateStart = date => {
    this.setState({ startDate: date });
    let start_date = new Date(date);
    let end_date = new Date(this.state.endDate);
    
    let yearOfstartDate = start_date.getFullYear();
    let yearOfendDate = end_date.getFullYear();
    let monthOfstartDate = (1 + start_date.getMonth());
        monthOfstartDate = monthOfstartDate >= 10 ? monthOfstartDate : '0' + monthOfstartDate;
    let monthOfendDate = (1 + end_date.getMonth());
        monthOfendDate = monthOfendDate >= 10 ? monthOfendDate : '0' + monthOfendDate;
    let dayOfstartDate = start_date.getDate();
        dayOfstartDate = dayOfstartDate >= 10 ? dayOfstartDate : '0' + dayOfstartDate;
    let dayOfendDate = end_date.getDate();
        dayOfendDate = dayOfendDate >= 10 ? dayOfendDate : '0' + dayOfendDate;
    let startDate = yearOfstartDate + '.' + monthOfstartDate + '.' + dayOfstartDate;
    let endDate = yearOfendDate + '.' + monthOfendDate + '.' + dayOfendDate;

    console.log("SearchByPeriod: "+startDate+"~"+endDate);
    this.props.getDataByPeriod(startDate,endDate);
  }
  
  _onChangeDateEnd = date => {
    this.setState({ endDate: date });
    let end_date = new Date(date);
    let start_date = new Date(this.state.startDate);
    
    let yearOfstartDate = start_date.getFullYear();
    let yearOfendDate = end_date.getFullYear();
    let monthOfstartDate = (1 + start_date.getMonth());
        monthOfstartDate = monthOfstartDate >= 10 ? monthOfstartDate : '0' + monthOfstartDate;
    let monthOfendDate = (1 + end_date.getMonth());
        monthOfendDate = monthOfendDate >= 10 ? monthOfendDate : '0' + monthOfendDate;
    let dayOfstartDate = start_date.getDate();
        dayOfstartDate = dayOfstartDate >= 10 ? dayOfstartDate : '0' + dayOfstartDate;
    let dayOfendDate = end_date.getDate();
        dayOfendDate = dayOfendDate >= 10 ? dayOfendDate : '0' + dayOfendDate;
    let startDate = yearOfstartDate + '.' + monthOfstartDate + '.' + dayOfstartDate;
    let endDate = yearOfendDate + '.' + monthOfendDate + '.' + dayOfendDate;

    console.log("SearchByPeriod: "+startDate+"~"+endDate);
    this.props.getDataByPeriod(startDate,endDate);
  }

  _directBtnsClick = e =>{
    const { target: {value}} = e;
    
		$('.direct-btns .btn-s').removeClass('active');
		$('.direct-btns .'+value).addClass('active');

    let end_date = new Date(this.state.endDate);
    let start_date = new Date(this.state.endDate);
    if(value==='180'){
      start_date.setMonth(start_date.getMonth() - 6);
      start_date.setDate(start_date.getDate() + 1);
    }else{
      start_date.setDate(start_date.getDate() - value);
    }
    let yearOfstartDate = start_date.getFullYear();
    let yearOfendDate = end_date.getFullYear();
    let monthOfstartDate = (1 + start_date.getMonth());
        monthOfstartDate = monthOfstartDate >= 10 ? monthOfstartDate : '0' + monthOfstartDate;
    let monthOfendDate = (1 + end_date.getMonth());
        monthOfendDate = monthOfendDate >= 10 ? monthOfendDate : '0' + monthOfendDate;
    let dayOfstartDate = start_date.getDate();
        dayOfstartDate = dayOfstartDate >= 10 ? dayOfstartDate : '0' + dayOfstartDate;
    let dayOfendDate = end_date.getDate();
        dayOfendDate = dayOfendDate >= 10 ? dayOfendDate : '0' + dayOfendDate;
    let startDate = yearOfstartDate + '.' + monthOfstartDate + '.' + dayOfstartDate;
    let endDate = yearOfendDate + '.' + monthOfendDate + '.' + dayOfendDate;

    console.log("SearchByPeriod: "+startDate+"~"+endDate);
    
    this.setState({ startDate: start_date });
    this.props.getDataByPeriod(startDate,endDate);
  }

  _doAnalyze = () => {
    this.props.getSearchResultByKeywords(this.state.searchValue);
  }

  componentDidMount() {

  }

  render() {
    return(
      <div className="search-area">
        <div className="wrap">
          <h4>
            궁금한 <strong>키워드</strong> 를 검색해보세요
          </h4>
          <input 
            id="searchField" 
            type="text"
            data-attr={this.props.searchValue} 
            placeholder="궁금한 검색어를 입력해주세요"
            onChange={this._handleChange}
            onKeyPress={this._onKeyPress}
            />
        </div>
        <ul id="optionByPeriod" className="flex-cont">
        <li className="flex-cont period">
          <div id="periodDate">
            <DatePicker
              id="selectedStartDate"
              value={this.state.startDate}
              selected={this.state.startDate}
              onChange={this._onChangeDateStart}
              dateFormat="yyyy.MM.dd"
              maxDate={new Date()}
            />
            ~
            <DatePicker
              id="selectedEndDate"
              value={this.state.endDate}
              selected={this.state.endDate}
              onChange={this._onChangeDateEnd}
              dateFormat="yyyy.MM.dd"
              maxDate={new Date()}
            />
          </div>
        </li>
        <li className="flex-cont direct-btns">
          <button className="btn-s week 7 active" onClick={this._directBtnsClick} value="7">1주일</button>
          <button className="btn-s 1month 30" onClick={this._directBtnsClick} value="30">1개월</button>
          <button className="btn-s 3month 90" onClick={this._directBtnsClick} value="90">3개월</button>
          <button className="btn-s 6month 180" onClick={this._directBtnsClick} value="180">6개월</button>
          <button className="btn-s 1year 365" onClick={this._directBtnsClick} value="365">1년</button>
        </li>
        <li className="btn-wrap">
          <button onClick={this._doAnalyze}type="button" id="btnExecution2" className="btn-s">
            분석실행
          </button>
        </li>
      </ul>
      </div>
    )
  }
}
export default SearchKeyword;