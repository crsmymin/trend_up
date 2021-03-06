import React, { Component, Fragment } from "react";

class Article extends Component {
  constructor(props) {
    super(props)
    this.state = {
      page : 1,
      start : 1,
      end : 10,
      activeTab : 0,
      items : [],
      visible: true
    }
  }

  _openSection = () => {
    this.setState(prevState => ({
      visible: !prevState.visible
    }));
  }

  _clickHandler = (id) => {
    console.log(id)
    this.setState({
      activeTab: id
    })
  }

  componentDidMount() {
    
  }

  render() {
    return (
      <div className="article-wrap">
        <h3 onClick={this._openSection} className={this.state.visible ? "section-title open":"section-title"}>
          원문보기
          <img src="./src/assets/images/accordion_btn.svg" alt="" />  
          <span className="info-tag">키워드가 포함된 문서 원문 및 링크를 제공합니다. <br></br>
          원문: 네이버 API(전체기간) / 뉴스: NATE / 블로그: 다음 / 게시판: ZUM </span>
        </h3>
        <div className={this.state.visible ? "section-inner open":"section-inner"}>
          {this.props.isLoadingArticle === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>  
            </div>
          ) : (
            <div className="articles">
              <ul className="tab-menu flex-cont">
                <li 
                  onClick={()=> this._clickHandler(0)} 
                  className={this.state.activeTab === 0 ? "tab on":"tab"}>원문</li>
                <li 
                  onClick={()=> this._clickHandler(1)} 
                  className={this.state.activeTab === 1 ? "tab on":"tab"}>뉴스</li>
                <li 
                  onClick={()=> this._clickHandler(2)} 
                  className={this.state.activeTab === 2 ? "tab on":"tab"}>블로그</li>
                <li 
                  onClick={()=> this._clickHandler(3)} 
                  className={this.state.activeTab === 3 ? "tab on":"tab"}>게시판</li>
              </ul>
              {/* 원문 */}
              <section id="articleOrigin" className={this.state.activeTab === 0 ? "article on":"article"}>   
                <h4 className="section-sub-title">
                  원문보기 : {this.props.searchValue}
                </h4>
                <p>
                  <em>전체 포스트</em>
                  <span className="quantity">{this.props.originTotal}</span>
                </p>
                {this.props.listOrigin === undefined ? (
                  <div className="not-found-result">검색된 결과가 없습니다.</div>
                ) : (
                  <ul>
                    {this.props.listOrigin.map((listOrigin, index) => (
                      <li key={index + 1}>
                        <h5>
                          <a href={listOrigin.link} target="_blank">
                            <strong>뉴스</strong>
                            <span
                              dangerouslySetInnerHTML={{
                                __html: listOrigin.title,
                              }}
                            >
                            </span>
                          </a>
                        </h5>
                        <p className="date">{listOrigin.pubDate}</p>
                        <p className="content" dangerouslySetInnerHTML={{__html: listOrigin.description,}}></p>
                      </li>
                    ))}
                  </ul>
                )}
              </section>
              {/* 원문 끝*/}
  
              {/* 네이버 뉴스 */}
              <section id="articleNews" className={this.state.activeTab === 1 ? "article on":"article"}>
                <h4 className="section-sub-title">
                  기간검색 - 뉴스 : {this.props.searchValue}
                </h4>
                <p>
                  <em>전체 포스트</em>
                  <span className="quantity">
                    {this.props.newsCrawler.naverNewsCnt}
                  </span>
                </p>
                {this.props.newsCrawler.naverNews === undefined ? (
                  <div className="not-found-result">검색된 결과가 없습니다.</div>
                ) : (
                  <ul>
                    {this.props.newsCrawler.naverNews.map(
                      (newsCrawler, index) => (
                        <li key={index + 1}>
                          <h5>
                            <a href={newsCrawler.link} target="_blank">
                              {newsCrawler.title}
                            </a>
                          </h5>
                          <p className="source">
                            {newsCrawler.medium} | {newsCrawler.date}
                          </p>
                          <p className="content">{newsCrawler.description}</p>
                        </li>
                      )
                    )}
                  </ul>
                )}
                <div className="news-page" dangerouslySetInnerHTML={{__html: this.props.newsCrawler.naverNewsPage,}}></div>
              </section>
              {/* 네이버 뉴스 끝*/}
  
              {/* 네이버 블로그 */}
              <section id="articleBlog" className={this.state.activeTab === 2 ? "article on":"article"}>
                <h4 className="section-sub-title">
                  기간검색 - 블로그 : {this.props.searchValue}
                </h4>
                <p>
                  <em>전체 포스트</em>
                  <span className="quantity">
                    {this.props.newsBlog.naverBlogCnt}
                  </span>
                </p>
                {this.props.newsBlog.naverBlog === undefined ? (
                  <div className="not-found-result">검색된 결과가 없습니다.</div>
                ) : (
                  <ul>
                    {this.props.newsBlog.naverBlog.map((naverBlog, index) => (
                      <li key={index + 1}>
                        <h5>
                          <a href={naverBlog.url} target="_blank">
                            {naverBlog.title}
                          </a>
                        </h5>
                        <p className="source">
                          {naverBlog.writerName} | {naverBlog.documentDate}
                        </p>
                        <p className="content">{naverBlog.content}</p>
                      </li>
                    ))}
                  </ul>
                )}
                <div
                  className="blog-page" dangerouslySetInnerHTML={{__html: this.props.newsBlog.naverBlogPage,}}>
                </div>
              </section>
              {/* 네이버 블로그 끝*/}
  
              {/* 네이버 카페 */}
              <section id="articleCafe" className={this.state.activeTab === 3 ? "article on":"article"}>
                <h4 className="section-sub-title">
                  기간검색 - 게시판 : {this.props.searchValue}
                </h4>
                <p>
                  <em>전체 포스트</em>
                  <span className="quantity">
                    {this.props.newsCafe.naverCafeCnt}
                  </span>
                </p>
                {this.props.newsCafe.naverCafe === undefined ? (
                  <div className="not-found-result">검색된 결과가 없습니다.</div>
                ) : (
                  <ul>
                    {this.props.newsCafe.naverCafe.map((newsCafe, index) => (
                      <li key={index + 1}>
                        <h5>
                          <a href={newsCafe.link} target="_blank">
                            {newsCafe.title}
                          </a>
                        </h5>
                        <p className="source">
                          {newsCafe.medium} | {newsCafe.date}
                        </p>
                        <p className="content">{newsCafe.description}</p>
                      </li>
                    ))}
                  </ul>
                )}
                <div className="cafe-page" dangerouslySetInnerHTML={{__html: this.props.newsCafe.naverCafePage,}}></div>          
              </section>
              {/* 네이버 카페 끝 */}
            </div>
          )}
        </div>
      </div>
    );
  }
}

export default Article;