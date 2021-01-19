import React, { Component } from "react";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class SearchByPeriod extends Component {
  constructor(props) {
    let today = new Date();  
    let start_date = new Date(today);
    start_date.setDate(start_date.getDate()-7);
    super(props)
    this.state = {
      startDate : start_date,
      endDate : today
    };
  }

  _onChangeDateStart = date => {
    this.setState({ startDate: date });
  }
  
  _onChangeDateEnd = date => {
    this.setState({ endDate: date });
  }

  _directBtnsClick = e =>{
    const { target: {value}} = e;
    
		$('.direct-btns .btn-s').removeClass('active');
		$('.direct-btns .'+value).addClass('active');

    let start_date = new Date(this.state.endDate);
    if(value==='180'){
      start_date.setMonth(start_date.getMonth() - 6);
      start_date.setDate(start_date.getDate() + 1);
    }else{
      start_date.setDate(start_date.getDate() - value);
    }
    this.setState({ startDate: start_date });
  }

  _doAnalyze = () => {
    let yearOftoDate = this.state.startDate.getFullYear();
    let yearOfFromDate = this.state.endDate.getFullYear();
    let monthOftoDate = (1 + this.state.startDate.getMonth());
        monthOftoDate = monthOftoDate >= 10 ? monthOftoDate : '0' + monthOftoDate;
    let monthOfFromDate = (1 + this.state.endDate.getMonth());
        monthOfFromDate = monthOfFromDate >= 10 ? monthOfFromDate : '0' + monthOfFromDate;
    let dayOftoDate = this.state.startDate.getDate();
        dayOftoDate = dayOftoDate >= 10 ? dayOftoDate : '0' + dayOftoDate;
    let dayOfFromDate = this.state.endDate.getDate();
        dayOfFromDate = dayOfFromDate >= 10 ? dayOfFromDate : '0' + dayOfFromDate;
    let toDate = yearOftoDate + '.' + monthOftoDate + '.' + dayOftoDate;
    let fromDate = yearOfFromDate + '.' + monthOfFromDate + '.' + dayOfFromDate;

    console.log(toDate);
    console.log(fromDate);
    this.props.getDataByPeriod(fromDate,toDate);
  }

  componentDidMount(){

  }
  render(){
    return (
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
    )
  }
}

export default SearchByPeriod;