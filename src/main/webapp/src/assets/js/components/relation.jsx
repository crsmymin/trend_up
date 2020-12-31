import React, { Component } from "react";

class Relation extends Component {
  constructor(props) {
    super(props)
  }
 
  componentDidMount() {
    var tab = document.querySelector("#relation .section-title");
    tab.addEventListener("click", function(){
      this.classList.toggle("open");
      var tabCont = document.querySelector(".relation-words");
      tabCont.classList.toggle("open");
    })
  }
  render() {
    return (
      <section id="relation" className="cf">
        <>
        <h3 className="section-title">
            연관어 순위 : {this.props.searchValue}
          <img src="./src/assets/images/accordion_btn.svg" alt="" />  
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
      </section>
    )
  }
}

export default Relation