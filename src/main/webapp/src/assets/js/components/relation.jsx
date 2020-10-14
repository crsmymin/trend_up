import React, { Component } from "react";

class Relation extends Component {
  constructor(props) {
    super(props)
  }
 
  componentDidMount() {
    
  }
  render() {
    return (
      <section id="relation" className={this.props.isLoadingRelated === true ? ("is-loading") : ("cf")}>
        {this.props.isLoadingRelated === true ? (
          <div className="loading-indicator">
            <div className="loader"></div>
          </div>
        ):(
        <>
        <h3 className="section-title">
                연관어 순위
          <span className="tool-tip">
                  ?
              <p className="description">
                ..............
              </p>
                </span>
        </h3>
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
        </>
        )}
      </section>
    )
  }
}

export default Relation