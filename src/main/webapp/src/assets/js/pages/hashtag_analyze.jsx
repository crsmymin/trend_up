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
      visible2: true
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

  componentDidMount (){
    
  }

  render() {
    return(
      <Fragment>
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
              <input 
              id="searchField" 
              type="text"
              data-attr={this.props.searchValue} 
              placeholder="#"
              onChange={this._handleChange}
              onKeyPress={this._onKeyPress}
              />
              <div className="btn-wrap">
                <button type="button" className="btn-s">
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
              <h3 onClick={this._openSection1} className={this.state.visible1 ? "section-title open":"section-title"}>
                #Top100 HashTags
                <img src="./src/assets/images/accordion_btn.svg" alt="" />
                <span className="info-tag">blahblahblahblahblah</span>
              </h3>
              <div className={this.state.visible1 ? "section-inner open" : "section-inner"}>
                <div className="best-hashtag-list flex-cont">
                  <div className="list">
                    <h4>INSTAGRAM</h4>
                    <ul className="scrollbar-inner">
                      <li className="tag-lis insta">
                        <span className="rank">1.</span>
                        <span className="word">법정구속</span>
                        <span className="metion-amount">10k</span>
                      </li>
                    </ul>
                  </div>
                  <div className="list">
                    <h4>TIKTOK</h4>
                    <ul className="scrollbar-inner">
                      <li className="tag-lis tik">
                        <span className="rank">1.</span>
                        <span className="word">법정구속</span>
                        <span className="metion-amount">10k</span>
                      </li>
                    </ul>
                  </div>
                  <div className="list">
                    <h4>TWITTER</h4>
                    <ul className="scrollbar-inner">
                      <li className="tag-lis twit">
                        <span className="rank">1.</span>
                        <span className="word">법정구속</span>
                        <span className="metion-amount">10k</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </section>

            <section id="relatedHashtag">
              <h3 onClick={this._openSection2} className={this.state.visible2 ? "section-title open":"section-title"}>
                연관 해시태그
                <img src="./src/assets/images/accordion_btn.svg" alt="" />
                <span className="info-tag">blahblahblahblahblah</span>
              </h3>
              <div className={this.state.visible2 ? "section-inner open" : "section-inner"}>
                <ul className="related-hashtag-list">
                  <li className="flex-cont">
                    <div className="mentions">
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                    </div>
                    <div className="btn-wrap">
                      <button className="btn-copy">Copy</button>
                    </div>
                  </li>
                  <li className="flex-cont">
                    <div className="mentions">
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                    </div>
                    <div className="btn-wrap">
                      <button className="btn-copy">Copy</button>
                    </div>
                  </li>
                  <li className="flex-cont">
                    <div className="mentions">
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                    </div>
                    <div className="btn-wrap">
                      <button className="btn-copy">Copy</button>
                    </div>
                  </li>
                  <li className="flex-cont">
                    <div className="mentions">
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                    </div>
                    <div className="btn-wrap">
                      <button className="btn-copy">Copy</button>
                    </div>
                  </li>
                  <li className="flex-cont">
                    <div className="mentions">
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                      #법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속#법정구속
                    </div>
                    <div className="btn-wrap">
                      <button className="btn-copy">Copy</button>
                    </div>
                  </li>
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