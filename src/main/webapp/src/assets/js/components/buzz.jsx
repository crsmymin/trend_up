import React, { Component } from "react";

class Buzz extends Component {
  constructor(props) {
    super(props)
  }

  componentDidMount() {
    
  }

  render() {
    return (
      <section id="buzz" className={this.props.isLoadingBuzz === true ? ("is-loading") : ("")}>
        {this.props.isLoadingBuzz === true ? (
          <div className="loading-indicator">
            <div className="loader"></div>
          </div>
        ):(
        <>  
        <h3 className="section-title">
          버즈추이
          <span className="tool-tip">
            ?
            <p className="description">
              ..............
            </p>
          </span>
        </h3>
        <div className="buzz-trasition flex-cont">

          {/* 차트정보 */}
          <div className="info">
            <h4>{this.props.searchValue}</h4>
            <div className="buzz-quantity">
              <span>전체버즈량</span>
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
              <span className="title">CAFE : {this.props.buzzTotalCafe}</span>
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
        </>
        )}
      </section>
    )
  }
}

export default Buzz