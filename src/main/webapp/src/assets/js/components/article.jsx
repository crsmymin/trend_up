import React, { Component, Fragment } from "react";

class Article extends Component {
  constructor(props) {
    super(props)
    this.state = {
      page : 1,
      start : 1,
      end : 10,
      items : []
    }
  }
  componentDidMount() {
  }

  render() {
    return (
      <Fragment>
        {/* 원문 */}
        <section id="articleOrigin" className={this.props.isLoadingArticle === true ? ("is-loading articles") : ("articles")}>
          {this.props.isLoadingArticle === true ? (
          <div className="loading-indicator">
            <div className="loader"></div>
          </div>
          ):(
          <>
          <h3 className="section-title">
            원문보기
            <span className="tool-tip">
              ?
              <p className="description">
                ..............
              </p>
            </span>
          </h3>
          <p><em>전체 포스트</em><span className="quantity">{this.props.originTotal}</span></p>
          {this.props.listOrigin === undefined ? 
          (<div className="not-found-result">검색된 결과가 없습니다.</div>) : (
          <ul>
            {this.props.listOrigin.map(
            (listOrigin, index) =>
            <li key={index + 1}>
              <h5>
                <a href={listOrigin.link} target="_blank">
                  <strong>네이버뉴스</strong>
                  <span dangerouslySetInnerHTML={{__html: listOrigin.title}}></span>
                </a>
              </h5>
              <p className="date">{listOrigin.pubDate}</p>
              <p className="content" dangerouslySetInnerHTML={{ __html: listOrigin.description}}></p>
            </li>
            )}
          </ul>
          )}
          </>
          )}
        </section>
        {/* 원문 끝*/}
        
        {/* 네이버 뉴스 */}
        <section id="articleNews" className={this.props.isLoadingArticle === true ? ("is-loading articles") : ("articles")}>
          {this.props.isLoadingArticle === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>
            </div>
          ) : (
          <>  
          <h3 className="section-title">
                  기간검색 - 네이버뉴스
            <span className="tool-tip">
                    ?
              <p className="description">
                ..............
              </p>
                  </span>
          </h3>
          <p><em>전체 포스트</em><span className="quantity">{this.props.newsCrawler.naverNewsCnt}</span></p>
          {this.props.newsCrawler.naverNewsCnt === undefined ? 
          (<div className="not-found-result">검색된 결과가 없습니다.</div>) : (
          <ul>
            {this.props.newsCrawler.naverNews.map(
            (newsCrawler, index) =>
            <li key={index + 1}>
              <h5><a href={newsCrawler.link} target="_blank">{newsCrawler.title}</a></h5>
              <p className="source">{newsCrawler.medium} | {newsCrawler.date}</p>
              <p className="content">{newsCrawler.description}</p>
            </li>
            )}
          </ul>
          )}
          <div className="news-page" dangerouslySetInnerHTML={{ __html: this.props.newsCrawler.naverNewsPage}}></div>
          </>
          )}
        </section>
        {/* 네이버 뉴스 끝*/}

        {/* 네이버 블로그 */}
        <section id="articleBlog" className={this.props.isLoadingArticle === true ? ("is-loading articles") : ("articles")}>
          {this.props.isLoadingArticle === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>
            </div>
          ) : (
          <>  
          <h3 className="section-title">
                  기간검색 - 네이버블로그
            <span className="tool-tip">
                    ?
              <p className="description">
                ..............
              </p>
                  </span>
          </h3>
          <p><em>전체 포스트</em><span className="quantity">{this.props.newsBlog.naverBlogCnt}</span></p>
          {this.props.newsBlog.naverBlogCnt === undefined ? 
          (<div className="not-found-result">검색된 결과가 없습니다.</div>) : (
          <ul>
            {this.props.newsBlog.naverBlog.map(
            (naverBlog, index) =>
            <li key={index + 1}>
              <h5><a href={naverBlog.link} target="_blank">{naverBlog.title}</a></h5>
              <p className="source">{naverBlog.medium} | {naverBlog.date}</p>
              <p className="content">{naverBlog.description}</p>
            </li>
            )}
          </ul>
          )}
          <div className="blog-page" dangerouslySetInnerHTML={{ __html: this.props.newsBlog.naverBlogPage}}></div>
          </>
          )}
        </section>
        {/* 네이버 블로그 끝*/}

        {/* 네이버 카페 */}
        <section id="articleCafe" className={this.props.isLoadingArticle === true ? ("is-loading articles") : ("articles")}>
          {this.props.isLoadingArticle === true ? (
            <div className="loading-indicator">
              <div className="loader"></div>
            </div>
          ) : (
          <>  
          <h3 className="section-title">
                  기간검색 - 네이버카페
            <span className="tool-tip">
                    ?
              <p className="description">
                ..............
              </p>
                  </span>
          </h3>
          <p><em>전체 포스트</em><span className="quantity">{this.props.newsCafe.naverCafeCnt}</span></p>
          {this.props.newsCafe.naverCafeCnt === undefined ? 
          (<div className="not-found-result">검색된 결과가 없습니다.</div>):
          (
          <ul>
            {this.props.newsCafe.naverCafe.map(
            (newsCafe, index) =>
            <li key={index + 1}>
              <h5><a href={newsCafe.link} target="_blank">{newsCafe.title}</a></h5>
              <p className="source">{newsCafe.medium} | {newsCafe.date}</p>
              <p className="content">{newsCafe.description}</p>
            </li>
            )}
          </ul>
          )}
          <div className="cafe-page" dangerouslySetInnerHTML={{ __html: this.props.newsCafe.naverCafePage}}></div>
          </>
          )}
        </section>
        {/* 네이버 카페 끝*/}
      </Fragment>
    )
  }
}

export default Article;