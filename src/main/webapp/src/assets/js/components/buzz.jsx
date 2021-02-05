import React, { Component } from "react";

class Buzz extends Component {
  constructor(props) {
    super(props)
    this.state = {
      
    }
  }

  _openSection = () => {
    this.setState(prevState => ({
      visible: !prevState.visible
    }));
  }

  componentDidMount() {
    
  }

  render() {
    return (
      <section id="buzz">        
        <h3 onClick={this._openSection} className={this.state.visible ? "section-title open":"section-title"}>
          언급량 추이 : {this.props.searchValue}
          <img src="./src/assets/images/accordion_btn.svg" alt="" />
          <span className="info-tag">관련 콘텐츠를 수집하여, 특정 기간 내 키워드가 포함된 문서 건수를 주기별 집계하여 제공합니다.<br></br>
          뉴스·블로그: saltlux 분석API / 게시판: zum
          </span>
        </h3>
        <div className={this.state.visible ? "section-inner open" : "section-inner"}>
          {this.props.isLoadingBuzz === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>  
            </div>
          ) : (
            <div className="buzz-trasition flex-cont">
              {/* 차트정보 */}
              <div className="info">
                <h4>{this.props.searchValue}</h4>
                <div className="buzz-quantity">
                  <span>전체 언급량</span>
                  <strong>
                    {this.props.buzzTotal}
                  </strong>
                  <div className="keyword-sort">
                  <span className="title">NEWS : {this.props.buzzTotalNews}</span>
                </div>
                <div className="keyword-sort">
                  <span className="title">BLOG : {this.props.buzzTotalBlog}</span>
                </div>
                <div className="keyword-sort">
                  <span className="title">BOARD : {this.props.buzzTotalCafe}</span>
                </div>
                </div>
              </div>
              {/* 차트정보 끝 */}

              {/* 차트 그래프 */}
              <div className="chart">
              <canvas id="buzzChart"></canvas>
              </div>
              {/* 차트 그래프 끝*/}
            </div>
          )}
        </div>
      </section>
    )
  }
}

export default Buzz