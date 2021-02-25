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
        <>
        <div className="keywords flex-cont">
          {/* 매스미디어 */}
          <div className="list mass-media">
            <figure className="sns-icon"></figure>
            <h4><strong>NAVER</strong> 순위</h4>
            <ul className="scrollbar-inner">
              {this.props.naver.map(
                (naver, index) =>
                  <li key={index} className="keywords-lis naver">
                    <span className="rank">{index + 1}.</span>
                    <span className="word" data-tag={naver} onClick={this._getSearchResultByKeywords}>
                      {naver}
                    </span>
                  </li>
              )}
            </ul>
          </div>
          {/* 매스미디어 끝*/}

          {/* zum */}
          <div className="list zum">
            <figure className="sns-icon"></figure>
            <h4><strong>ZUM </strong>순위</h4>
            <ul className="scrollbar-inner">
              {this.props.zum.map(
                (zum, index) =>
                  <li key={index} className="keywords-lis zum">
                    <span className="rank">{index + 1}.</span>
                    <span className="word" data-tag={zum} onClick={this._getSearchResultByKeywords}>
                      {zum}
                    </span>
                  </li>
              )}
            </ul>
          </div>
          {/* 매스미디어 끝*/}

          {/* 트위터 */}
          <div className="list twitter">
            <figure className="sns-icon"></figure>
            <h4><strong>NATE </strong>순위</h4>
            <ul className="scrollbar-inner">
              {this.props.nate.map(
                (nate, index) =>
                  <li className="keywords-lis twitter" key={index}>
                    <span className="rank">{index + 1}.</span>
                    <span className="word" data-tag={nate} onClick={this._getSearchResultByKeywords}>
                      {nate}
                    </span>
                  </li>
              )}
            </ul>
          </div>
          {/* 트위터 끝*/}
        </div>
        </>
        <div className="search-amount">
          
        </div>
      </section>
    )
  }
}

export default Keywords;