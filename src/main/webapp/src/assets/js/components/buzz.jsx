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
        <>  
        <h3 onClick={this._openSection} className={this.state.visible ? "section-title open":"section-title"}>
          버즈추이
          <span className="tool-tip">?<p className="description">..............</p></span>
          <img src="./src/assets/images/accordion_btn.svg" alt="" />
        </h3>
        <div className={this.state.visible ? "buzz-trasition flex-cont open":"buzz-trasition flex-cont"}>
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
      </section>
    )
  }
}

export default Buzz