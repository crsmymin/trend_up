import React, { Component } from "react";

class Emotion extends Component {
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
      <section id="emotion" className="cf">
        <h3 onClick={this._openSection} className={this.state.visible ? "section-title open":"section-title"}>
            감성어 변화 : {this.props.searchValue}
          <img src="./src/assets/images/accordion_btn.svg" alt="" />  
          <span className="info-tag">긍정, 부정, 중립으로 분류한 감성 사전에 의해 추출된 연관성이 높은 감성 단어를 제공합니다.<br></br>
          감성어 랭킹: 썸트렌드
          </span>
        </h3>
        <div className={this.state.visible ? "section-inner open" : "section-inner"}>

          {this.props.isLoadingEmotion === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>  
            </div>
          ) : (
            <div className="emotional-words flex-cont">
              {/* 감성어 버블차트 */}
              <div className="inner-box">
                <div id="wordCloud2">
                </div>
                <div className="ratio">
                  <h5>"{this.props.searchValue}" 감성 연관어 TOP 10</h5>
                  {this.props.emotionWords.length === 0 ? "" : (
                  <ul>
                    <li className="type pos">긍정 {this.props.keywordPositive}%</li>
                    <li className="type neg">부정 {this.props.keywordNegative}%</li>
                    <li className="type neu">중립 {this.props.keywordNeutral}%</li>
                    <li className="type etc">기타 {this.props.keywordEtc}%</li>
                  </ul>
                  )}
                </div>
              </div>
              {/* 감성어 버블차트 끝*/}

              {/* 감성어 리스트}*/}
              {this.props.emotionWords.length === 0 ? "" : (
              <div className="words">
                <h4>감성어 랭킹</h4>
                <table>
                  <thead>
                    <tr>
                      <th>순위</th>
                      <th>분류</th>
                      <th>키워드</th>
                      <th>건수</th>
                    </tr>
                  </thead>
                  <tbody>
                    {this.props.emotionWords
                    .sort((a, b) => b.count - a.count)
                    .map((emotionWords, index) =>
                      <tr key={index + 1}>
                        <td>{index + 1}</td>
                        {emotionWords.polarity === "positive" ? (<td><span className="pos">긍정</span></td>) : null}
                        {emotionWords.polarity === "negative" ? (<td><span className="neg">부정</span></td>) : null}
                        {emotionWords.polarity === "neutral" ? (<td><span className="neu">중립</span></td>) : null}
                        {emotionWords.polarity === "other" ? (<td><span className="oth">기타</span></td>) : null}
                        <td>{emotionWords.name}</td>
                        <td>{emotionWords.frequency}</td>
                      </tr>
                    )} 
                  </tbody>
                </table>
              </div>
              )}
              {/*감성어 리스트 끝*/}
            </div>
          )}
        </div>
      </section>
    )
  }
}

export default Emotion