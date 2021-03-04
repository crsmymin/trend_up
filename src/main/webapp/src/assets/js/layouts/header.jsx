// import react modules
import React, { Component } from 'react';

class Header extends Component {
  constructor(props) {
    super(props) 
  }

  render () {
    return (
      <header>
        <div id="headerTop">
          <div className="wrap flex-cont">
            <h1 id="topLogo" className="col-5">
              <a href="/">
                <img src="./src/assets/images/logo.png" alt="로고"></img>
              </a>
            </h1>
            <nav>
              <ul className="flex-cont">
                <li>
                  <a href="/trend">트렌드 분석</a>
                </li>
                <li>
                  <a href="/keyword">키워드 분석</a>
                </li>
                <li>
                  <a href="/hashtag">Hashtag 트렌드</a>
                </li>
                <li className="feedback">
                  <a href="https://docs.google.com/spreadsheets/d/1S2JTsEphhlopw00CL2VdTnnPWQF8jteaJz3qWs9wQOk/edit#gid=264097599" target="_blank">ver.5 피드백 남기기</a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
        <div id="headerBottom">
          <div className="wrap">
            <div className="trend">
              {this.props.page === "trend" ? (
                <>
                  <h2>트렌드 분석</h2>
                  <p>
                    선택한 검색어(명사, 동사, 형용사)의 언급량, 연관어, 감성 분석을 <br/>
                    통해 해당 키워드에 대한 관심도와 관심의 내용, 긍·부정 감성 파악이 <br/>
                    가능하며, 선택 기간 내 변화를 확인할 수 있습니다. 
                  </p>
                </>
              ):("")}
              {this.props.page === "keyword" ? (
                <>
                  <h2>키워드 분석</h2>
                  <p>
                    입력한 키워드(명사, 동사, 형용사)의 언급량, 연관어, 감성 분석을 <br/>
                    통해 해당 키워드에 대한 관심도와 관심의 내용, 긍·부정 감성 파악이 <br/>
                    가능하며, 선택 기간 내 변화를 확인할 수 있습니다. 
                  </p>
                </>
              ):("")}
               {this.props.page === "hashtag" ? (
                <>
                  <h2>Hashtag 트렌드</h2>
                  <p>
                  인스타그램에 게시 된 상위 100개 Hashtag를 추천하고, <br/>
                  선택한 키워드와 함께 사용하는 가장 인기있는 키워드들을 추천해드립니다. 
                  </p>
                </>
              ):("")}
            </div>
          </div>
        </div>
      </header>
    );
  }
}

export default Header;