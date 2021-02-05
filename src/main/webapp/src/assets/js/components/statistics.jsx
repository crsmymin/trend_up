import React, { Component } from "react";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class Statistics extends Component {
  constructor(props) {
    super(props);
  }

  componentDidMount() {  
  
  }

  render() {
    return (
      <section id="statistics">
        <div className="statistic flex-cont">
          <div className="amount search">
            <h4>월간 검색량</h4>
            <p>(최근 한 달간 네이버에서 키워드가 검색된 횟수입니다.)</p>
            <div className="sorts flex-cont">
              <div className="factor">
                <span className="img"></span>
                {this.props.searchPC === undefined ? (
                <span className="counts">0</span>
                ):(
                <span className="counts">{this.props.searchPC}</span>
                )}
                <span className="type">PC</span>
              </div>
              <div className="factor">
                <span className="img"></span>
                {this.props.searchMobile === undefined ? (
                <span className="counts">0</span>
                ):(
                <span className="counts">{this.props.searchMobile}</span>
                )}
                <span className="type">Mobile</span>
              </div>
              <div className="factor">
                <span className="img"></span>
                {this.props.searchTotal === undefined ? (
                <span className="counts">0</span>
                ):(
                <span className="counts">{this.props.searchTotal}</span>
                )}
                <span className="type">Total</span>
              </div>
            </div>
          </div>
          <div className="amount contents">
            <h4>콘텐츠 발행량</h4>
            <p>(검색 기간 기준 발행된 키워드 관련 콘텐츠 수입니다. - 원문 기준)</p>
            <div className="sorts flex-cont">
              <div className="factor">
                <span className="img"></span>
                {this.props.newsCrawler.length === 0 ? (
                <span className="counts">0</span>
                ) : (
                <span className="counts">{this.props.newsCrawler.naverNewsCnt}</span>
                )}
                <span className="type">NEWS</span>
              </div>
              <div className="factor">
                <span className="img"></span>
                {this.props.newsBlog.length === 0 ? (
                <span className="counts">0</span>
                ):(
                <span className="counts">{this.props.newsBlog.naverBlogCnt}</span>
                )}
                <span className="type">BLOG</span>
              </div>
              <div className="factor">
                <span className="img"></span>
                {this.props.newsCafe.length === 0 ? (
                <span className="counts">0</span>
                ):(
                <span className="counts">{this.props.newsCafe.naverCafeCnt}</span>
                )}
                <span className="type">BOARD</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    )
  }
}

export default Statistics;