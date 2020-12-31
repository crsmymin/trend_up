import React, { Component } from "react";
import axios from "axios";

class Statistics extends Component {
  constructor(props) {
    super(props);
    
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
        )}
      </section>
    )
  }
}

export default Statistics;