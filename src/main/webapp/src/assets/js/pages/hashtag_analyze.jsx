// import react modules
import React, { Component, Fragment } from "react";
import ReactDom from "react-dom";
import axios from 'axios'

// components
import Header from '../layouts/header.jsx'
import Footer from '../layouts/footer.jsx'

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      page : "hashtag",
      visible1: true,
      visible2: true,
      engRank : [],
      korRank : [],
      tiktokRank : [],
      relatedHashtag : [],
      searchValue : "",
      isLoadingAll : true,
    }
  }

  _openSection1 = () => {
    this.setState(prevState => ({
      visible1: !prevState.visible1
    }));
  }
  
  _openSection2 = () => {
    this.setState(prevState => ({
      visible2: !prevState.visible2
    }));
  }

  _handleChange = (e) => {
    this.setState({
      searchValue: e.target.value
    })
  }

  _onKeyPress = (e) => {
    if(e.key === 'Enter') {
      this._getRelatedHashtag(this.state.searchValue);
    }
  }

  _doAnalyze = () => {
    this._getRelatedHashtag(this.state.searchValue);
  }

  // get keywords by data
  _getHashtagRank = () => {
    axios({
      method: 'get',
      url: '/hashtagRank'
    })
    .then(res => {
      const data = res.data;
      this.setState({
        isLoadingAll: false,
        korRank: data.korRank.KorHashtagRank,
        engRank: data.engRank.EngHashtagRank,
        tiktokRank: data.tiktokRank.TiktokHashtagRank,
        searchValue: data.korRank.KorHashtagRank[0].tag_text
      })
      $('#searchField').val(data.korRank.KorHashtagRank[0].tag_text);
      this._getRelatedHashtag(data.korRank.KorHashtagRank[0].tag_text);
    })
    .catch(error => {
      console.log(error)
    }) 

  }

  _getRelatedHashtag = (keyword) => {
    if(keyword==undefined) keyword=this.state.searchValue;
    this.setState({
      isLoadingAll : true,
    })
    //키워드를 통한 컨텐츠 조회
    axios({
        method: 'get',
        url: "/relatedHashtag",
        params: {
          searchValue: keyword
        }
      })
      .then(res => {
        const data = res.data;
        this.setState({
          relatedHashtag: data.Hashtags,
          isLoadingAll : false
        })
      })
      .catch(error => {
        console.log(error)
      })
  }

  _getRelatedHashtagByRank = () => { 
    this.setState({ 
      searchValue: event.target.getAttribute("data-tag")
    });
    $('#searchField').val(event.target.getAttribute("data-tag"));
    this._getRelatedHashtag(event.target.getAttribute("data-tag"));
  }

  _getCopyText = (e) => {
    let tags = e.target.parentNode.previousSibling;
    let copyText = tags.value;
    tags.focus();
    tags.select();
    document.execCommand("Copy");
    alert("해당 영역의 태그가 복사되었습니다.");
    console.log(copyText);
  }

  componentDidMount (){
    this._getHashtagRank();
    
  }

  render() {
    return(
      <Fragment>
        <div className={this.state.isLoadingAll === true ? ("loading-indicator-all show"):("loading-indicator-all")}>
          <div className="loader-all"></div>
        </div>
        <Header 
          page={this.state.page} 
        />
        {/* search area */}
        <div className="search-area hashtag">
          <div className="wrap">
            <h4>
              인기 <strong>해시태그</strong> 를 검색해보세요
            </h4>
            <div className="flex-cont">
              <span id="hashIcon">#</span>
              <input 
              id="searchField" 
              type="text"
              data-attr={this.searchValue} 
              onChange={this._handleChange}
              onKeyPress={this._onKeyPress}
              />
              <div className="btn-wrap">
                <button type="button" className="btn-s" onClick={this._getCopyText}>
                  분석실행
                </button>
              </div>
            </div>
          </div>
        </div>
        {/* end search area */}

        <div id={this.state.page === "hashtag" ? "hashtag" : ""} className="container cf">
          <div className="wrap">
            <section id="bestHashtag">
              <ul className="social-icons flex-cont">
                <li><a target="_blank" href="https://www.instagram.com/">Instagram</a></li>
                <li><a target="_blank" href="https://www.tiktok.com/">Tiktok</a></li>
                <li><a target="_blank" href="https://www.youtube.com/">Youtube</a></li>
                <li><a target="_blank" href="https://www.facebook.com/">Facebook</a></li>
                <li><a target="_blank" href="https://twitter.com/">Twitter</a></li>
              </ul>
              <h3 onClick={this._openSection1} className={this.state.visible1 ? "section-title open":"section-title"}>
                #Top100 HashTags
                <img src="./src/assets/images/accordion_btn.svg" alt="" />
                <span className="info-tag">자주 사용되는 해시태그 <br></br>
                Reference :: KOR instagram: tagsfinder / ENG instagram&tiktok: top-hashtags</span>
              </h3>
              <div className={this.state.visible1 ? "section-inner open" : "section-inner"}>
                <div className="best-hashtag-list flex-cont">
                  <div className="list">
                    <h4>INSTAGRAM( KOR )</h4>
                    <ul className="scrollbar-inner">
                      {this.state.korRank.map(
                      (korRank,index) =>
                      <li className="tag-lis insta" key={index}>
                        <span className="rank">{index + 1}</span>
                        <span onClick={this._getRelatedHashtagByRank} className="word" data-tag={korRank.tag_text}># {korRank.tag_text}</span>
                        <span className="metion-amount">{korRank.cnt}</span>
                      </li>
                      )}
                    </ul>
                  </div>
                  <div className="list">
                    <h4>INSTAGRAM( ENG )</h4>
                    <ul className="scrollbar-inner">
                      {this.state.engRank.map(
                      (engRank, index) =>
                      <li className="tag-lis tik" key={index}>
                        <span className="rank">{index + 1}</span>
                        <span onClick={this._getRelatedHashtagByRank} className="word" data-tag={engRank.tag_text}># {engRank.tag_text}</span>
                        <span className="metion-amount">{engRank.cnt}</span>
                      </li>
                      )}
                    </ul>
                  </div>
                  <div className="list">
                    <h4>TIKTOK</h4>
                    <ul className="scrollbar-inner">
                      {this.state.tiktokRank.map(
                      (tiktokRank,index) =>
                      <li className="tag-lis twit" key={index}>
                        <span className="rank">{index + 1}</span>
                        <span onClick={this._getRelatedHashtagByRank} className="word" data-tag={tiktokRank.tag_text}># {tiktokRank.tag_text}</span>
                        <span className="metion-amount">{tiktokRank.cnt}</span>
                      </li>
                      )}
                    </ul>
                  </div>
                </div>
              </div>
            </section>

            <section id="relatedHashtag">
              <h3 onClick={this._openSection2} className={this.state.visible2 ? "section-title open":"section-title"}>
                연관 해시태그
                <img src="./src/assets/images/accordion_btn.svg" alt="" />
                <span className="info-tag">검색된 단어가 포함 된 게시물에서 가장 일반적으로 사용되는 태그<br></br>
                Reference :: tagsfinder & top-hashtags</span>
              </h3>
              <div className={this.state.visible2 ? "section-inner open" : "section-inner"}>
                <ul className="related-hashtag-list">
                  {this.state.relatedHashtag.map(
                  (relatedHashtag, index) =>
                  <li className="flex-cont" key={index}>
                    <textarea className="mentions" value={relatedHashtag} readOnly></textarea>
                    <div className="btn-wrap">
                      <button className="btn-copy" onClick={this._getCopyText}>Copy</button>
                    </div>
                  </li>
                  )}
                </ul>
              </div>
            </section>

          </div>
        </div>
        <Footer />
      </Fragment>
    );
  }
}

export default App;

ReactDom.render(<App />, document.getElementById("app"));