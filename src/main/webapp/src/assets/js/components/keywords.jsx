import React, { Component } from "react";
import axios from "axios";

class Keywords extends Component {
  constructor(props) {
    super(props);
    this.state = {
      getKeywords : "",
      isSelected : 0,
    }
  }

  _handleClickClass = (index) => {    
    // initialize for existing selected keyword
    let keyword = document.querySelectorAll(".keywords-lis");
    for (let i = 0; i < keyword.length; i++) {
      keyword[i].classList.remove("is-selected");
    }

    this.setState({
      isSelected: index,
    });
  }

  _getSearchResultByKeywords = () => {
    this.setState({ 
      getKeywords: event.target.getAttribute("data-tag"),
    });
    this.props.getSearchResultByKeywords(event.target.getAttribute("data-tag"));
  }

  componentDidMount() {  
    
  }

  render() {    
    return (
      <section id="keyword" className={this.props.isLoadingKeyword === true ? ("is-loading"):("")}>
        {this.props.isLoadingKeyword === true ? (
        <div className="loading-indicator">
            <div className="loader"></div>  
        </div>
        ) : (
        <>
        <h3 className="section-title">
          미디어별 주요 키워드
          <span className="tool-tip">
            ?
            <p className="description">
              뉴스 자료를 통해 더 자세한 내용을 살펴보겠습니다. ▶용인 우리제일교회 집단 감염 관련 기사
            </p>
          </span>
        </h3>
        <div className="keywords flex-cont">
          {/* 매스미디어 */}
          <div className="list mass-media">
            <figure className="sns-icon"></figure>
            <h4><strong>매스미디어</strong>순위</h4>
            <ul className="scrollbar-inner">
              {this.props.naver.map(
                (naver, index) =>
                  <li 
                  key={index}
                  className="keywords-lis naver" 
                  // className={this.state.isSelected === index ? ("keywords-lis naver is-selected") : ("keywords-lis naver")} 
                  // onClick={this._handleClickClass.bind(this,index)}
                  >
                    <span className="rank">{index + 1}.</span>
                    <span className="word"
                    data-tag={naver}
                    onClick={this._getSearchResultByKeywords}
                    >
                      {naver}
                    </span>
                    <span className="media-title"></span>
                  </li>
              )}
            </ul>
          </div>
          {/* 매스미디어 끝*/}

          {/* 트위터 */}
          <div className="list twitter">
            <figure className="sns-icon"></figure>
            <h4><strong>트위터</strong>순위</h4>
            <ul className="scrollbar-inner">
              {this.props.twitter.map(
                (twitter, index) =>
                  <li 
                    className="keywords-lis twitter"
                    key={index} 
                    // className={this.state.isSelected === index ? ("keywords-lis twitter is-selected") : ("keywords-lis twitter")}
                    // onClick={this._handleClickClass.bind(this, index)}
                  >
                    <span className="rank">{index + 1}.</span>
                    <span className="word"
                    data-tag={twitter.rank}
                    onClick={this._getSearchResultByKeywords}
                    >{twitter.rank}</span>
                    <span className="media-title">{twitter.rank2}</span>
                  </li>
              )}
            </ul>
          </div>
          {/* 트위터 끝*/}
        </div>
        </>
        )}
      </section>
    )
  }
}

export default Keywords;