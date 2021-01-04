import React, { Component } from "react";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class Statistics extends Component {
  constructor(props) {
    let today = new Date();  
    
    super(props);
    this.state = {
      startDate : today,
      endDate : today
    };
  }

  _onChangeDateStart = date => {
    this.setState({ startDate: date });
  }
  
  _onChangeDateEnd = date => {
    this.setState({ endDate: date });
  }

  componentDidMount() {  
    
  }

  render() {
    return (
      <section id="statistics" className={this.props.isLoadingKeyword === true ? ("is-loading"):("")}>
        {this.props.isLoadingKeyword === true ? (
        <div className="loading-indicator">
          <div className="loader"></div>  
        </div>
        ) : (
        <>
          <ul id="searchOption2" className="flex-cont">
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
              <button className="btn-s week 7 active" value="7">1주일</button>
              <button className="btn-s 1month 30" value="30">1개월</button>
              <button className="btn-s 3month 90" value="90">3개월</button>
              <button className="btn-s 6month 180" value="180">6개월</button>
              <button className="btn-s 1year 365" value="365">1년</button>
            </li>
            <li className="btn-wrap">
              <button type="button" id="btnExecution2" className="btn-s">
                분석실행
              </button>
            </li>
          </ul>
          <div className="statistic flex-cont">
            <div className="amount search">
              <h4>월간 검색량</h4>
              <div className="sorts flex-cont">
                <div className="factor">
                  <span className="img"></span>
                  <span className="counts">12,222</span>
                  <span className="type">PC</span>
                </div>
                <div className="factor">
                  <span className="img"></span>
                  <span className="counts">34,123</span>
                  <span className="type">Mobile</span>
                </div>
                <div className="factor">
                  <span className="img"></span>
                  <span className="counts">43,333</span>
                  <span className="type">Total</span>
                </div>
              </div>
            </div>
            <div className="amount contents">
              <h4>월간 콘텐츠 발행량</h4>
              <div className="sorts flex-cont">
                <div className="factor">
                  <span className="img"></span>
                  <span className="counts">12,222</span>
                  <span className="type">블로그</span>
                </div>
                <div className="factor">
                  <span className="img"></span>
                  <span className="counts">34,123</span>
                  <span className="type">카페</span>
                </div>
                <div className="factor">
                  <span className="img"></span>
                  <span className="counts">43,333</span>
                  <span className="type">웹 사이트</span>
                </div>
              </div>
            </div>
          </div>
        </>  
        )}
      </section>
    )
  }
}

export default Statistics;