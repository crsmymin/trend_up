import React, { Component } from "react";

class Relation extends Component {
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
      <section id="relation" className="cf">        
        <h3 onClick={this._openSection} className={this.state.visible ? "section-title open":"section-title"}> 
            연관어 순위 : {this.props.searchValue}
          <img src="./src/assets/images/accordion_btn.svg" alt="" />  
          <span className="info-tag">관련 콘텐츠의 문장을 형태소 분석을 통해 연관어를 제공합니다. <br></br>
          분석 API: aiopen.etri
          </span>
        </h3>
        <div className={this.state.visible ? "section-inner open" : "section-inner"}>
          {this.props.isLoadingRelated === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>  
            </div>
          ) : (
            <div className="relation-words flex-cont">
              {/* 워드클라우드 */}
              <div id="wordCloud"></div>
              {/* 워드클라우드 끝*/}
    
              {/* 연관어 리스트}*/}
              <div className="words">
                <h4><strong>{this.props.searchValue}</strong> 연관어</h4>
                <ul>
                  {this.props.relatedWords
                  .sort((a, b) => b.count - a.count)
                  .map((relatedWords, index) =>
                    <li key={index + 1} className="flex-cont">
                      <em>{index + 1}.</em>
                      <p>{relatedWords.word}</p>
                      <span>{relatedWords.count}</span>
                    </li>
                  )} 
                </ul>
              </div>
              {/*연관어 리스트 끝*/}
            </div>
          )}
        </div>
      </section>
    )
  }
}

export default Relation