// import react modules
import React, { Component } from "react";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class SearchKeyword extends Component {
  constructor(props) {
    super(props)
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
          <input id="searchField" type="text" placeholder="궁금한 검색어를 입력해주세요"/>
          <ul id="searchOption2" className="flex-cont">
            <li className="flex-cont period">
              <div id="periodDate">
                <DatePicker
                  id="selectedStartDate"
                  // value={this.state.startDate}
                  // selected={this.state.startDate}
                  // onChange={this._onChangeDateStart}
                  dateFormat="yyyy.MM.dd"
                  maxDate={new Date()}
                />
                ~
                <DatePicker
                  id="selectedEndDate"
                  // value={this.state.endDate}
                  // selected={this.state.endDate}
                  // onChange={this._onChangeDateEnd}
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
      </div>
    )
  }
}
export default SearchKeyword;