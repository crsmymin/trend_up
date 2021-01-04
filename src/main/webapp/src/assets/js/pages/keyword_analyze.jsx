// import react modules
import React, { Component, Fragment } from "react";
import ReactDom from "react-dom";
import axios from 'axios'

// components
// components
import Header from '../layouts/header.jsx'
import Footer from '../layouts/footer.jsx'
import SearchTrend from '../layouts/searchTrend.jsx'
import Keywords from '../components/keywords.jsx'
import Statistics from '../components/statistics.jsx'
import Buzz from '../components/buzz.jsx'
import Relation from '../components/relation.jsx'
import Emotion from '../components/emotion.jsx'
import Article from '../components/article.jsx'

class App extends Component {
  constructor(props) {
    super(props)
    this.state = {
      page : "keyword",
      fromDate : "",
      toDate : "",
      searchValue : "",
      naver: [],
      zum: [],
      twitter: [],
      buzzTotal: "",
      relatedWords: [], 
      newsOrigin: "",
      newsCrawler: [],
      newsBlog: [],
      newsCafe: [],
      listOrigin: [],
      word : "",
      searchResult : [],
      isLoadingKeyword: true,
      isLoadingArticle : true,
      isLoadingBuzz: true,
      isLoadingRelated: true,
      buzzTotalNews: 0,
      buzzTotalBlog: 0,
      buzzTotalCafe: 0,
      selectedDate:""
    }
  }

  // get search result by period
  _getSearchResultByPeriod = (searchResult,startdate,enddate) => {
  // 키워드 순위 세팅
    this.setState({
      searchResult: searchResult,
      twitter: searchResult.twitter.twitterRank,
      naver: searchResult.naver.naverRank,
      fromDate: startdate,
      toDate: enddate,
      isLoadingKeyword: false,
      selectedDate: $('#selectedStartDate').val() +" "+$('#hoursSelect option:selected').text()

    })
    let keyword = document.querySelectorAll(".keywords-lis");
    let firstKeyword = keyword[0];
    firstKeyword.classList.add("is-selected");

    for (let i = 0; i < keyword.length; i++) {
      keyword[i].addEventListener("click", function () {
        for(let j = 0; j < keyword.length; j++) {
          keyword[j].classList.remove("is-selected");    
        }
        this.classList.add("is-selected");
      })
    }
   
    // 컨텐츠 기간검색 초기실행
    this._getSearchResultByKeywords(searchResult.naver.naverRank[0]);
  }

  // get search result by keywords
  _getSearchResultByKeywords = (keyword) => {
    this.setState({
      isLoadingArticle: true,
      isLoadingBuzz: true,
      isLoadingRelated: true
    })

    let toDate = new Date(this.state.fromDate);
    let to_month=1+toDate.getMonth();
      to_month=to_month>= 10 ? to_month : '0' + to_month;
    let to_day=toDate.getDate()>= 10 ? toDate.getDate() : '0' + toDate.getDate();
    let startDate =toDate.getFullYear()+"."+to_month+"."+to_day;
    
    // 컨텐츠 기간검색
    axios({
      method: 'get',
      url: "/searchNaverNews",
      params: {
        searchValue: keyword,
        fromDate: startDate,
        toDate: this.state.toDate,
        start: 1
      }
    })
    .then(res => {
      const data = res.data;
      let searchValue = data.searchValue;
      let parseData = JSON.parse(data.naverNews);
      let buzzTotal = parseData.total;
      let newsOrigin = JSON.parse(data.naverNews);
      let newsCrawler = JSON.parse(data.naverCrawlerNews);
      let newsBlog = JSON.parse(data.naverCrawlerBlog);
      let newsCafe = JSON.parse(data.naverCrawlerCafe);
      let listOrigin = newsOrigin.items;
     
      var fromDate= this.state.fromDate;
      var toDate= this.state.toDate;
      this.setState({
       searchValue,
       buzzTotal,
       newsOrigin,
       newsCrawler,
       newsBlog,
       newsCafe,
       listOrigin,
       isLoadingArticle: false
     })
      this.draw_d3();
    })
    .catch(error => {
      console.log(error)
    })
  }

  draw_d3 = () =>{
    // 연관어 순위 
    let toDate = new Date(this.state.fromDate);
    let to_month=1+toDate.getMonth();
      to_month=to_month>= 10 ? to_month : '0' + to_month;
    let to_day=toDate.getDate()>= 10 ? toDate.getDate() : '0' + toDate.getDate();
    let startDate =toDate.getFullYear()+"."+to_month+"."+to_day;
    
    axios({
      method: 'get',
      url: "/drawWordCloud",
      params: {
        searchValue: this.state.searchValue,
        fromDate: startDate,
        toDate: this.state.toDate,
        start: 1
      }
    })
    .then(res => {
      const data = res.data;
      let relatedWords = JSON.parse(data.morpheme);
      
      const arrayList =new Array();
		
      for(let i=0; i<relatedWords.length; i++){
        let unique = true;
        for(let j=0;j<arrayList.length;j++)
          if ((relatedWords[i].word === arrayList[j].word)) {
            arrayList[j].count=arrayList[j].count+relatedWords[i].count;
                  unique = false;
              }
        if (unique) {
          arrayList.push(relatedWords[i]);
          }
      }

      this.setState({
        isLoadingRelated: false,
        relatedWords :arrayList
      })

      // set word cloud
      let resData = this.state.relatedWords;
      
      let width = 600;
      let height = 400;
      let fill = d3.scale.category20c();
      let wordScale = d3.scale.linear().range([30, 70]);

      let subjects = resData
        .map(function (d) { return { text: d.word, size: +d.count } })
        .sort(function (a, b) { return d3.descending(a.size, b.size); })
        .slice(0, 100);

      wordScale.domain([
        d3.min(subjects, function (d) { return d.size; }),
        d3.max(subjects, function (d) { return d.size; }),
      ]);

      d3.layout.cloud().size([width, height])
        .words(subjects)
        .padding(1)
        .rotate(function () { return ~~(Math.random() * 2) * 0; })
        .font("Impact")
        .fontSize(function (d) { return wordScale(d.size); })
        .on("end", draw)
        .start();

      function draw(words) {
        let wordCloudWrap = document.getElementById("wordCloud");
        
			  $('#wordCloud').html("");
        d3.select(wordCloudWrap).append("svg")
          .attr("width", width)
          .attr("height", height)
          .append("g")
          .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")")
          .selectAll("text")
          .data(words)
          .enter().append("text")
          .style("font-size", function (d) { return d.size + "px"; })
          .style("font-family", "Impact")
          .style("fill", function (d, i) { return fill(d.size) })
          .attr("text-anchor", "middle")
          .attr("transform", function (d) {
            return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
          })
          .text(function (d) { return d.text; });
      }
      this.draw_buz();
    })
    .catch(error => {
      console.log(error)
    })
  }

  draw_buz = () => {
    //버즈추이 그래프
    let toDate = new Date(this.state.fromDate);
    let to_month=1+toDate.getMonth();
      to_month=to_month>= 10 ? to_month : '0' + to_month;
    let to_day=toDate.getDate()>= 10 ? toDate.getDate() : '0' + toDate.getDate();
    let startDate =toDate.getFullYear()+"."+to_month+"."+to_day;
    
    axios({
      method: 'get',
      url: "/drawBuzzChart",
      params: {
        searchValue: this.state.searchValue,
        fromDate: startDate,
        toDate: this.state.toDate,
        start: 1
      }
    })
    .then(res => {
    this.setState({
      isLoadingBuzz: false
    })
    //console.log(d.uploadDateNews);
    $('#buzzChart').remove();
    $('.chart').append('<canvas id="buzzChart"><canvas>');

    //console.log(d.uploadDateNews);
    const data = res.data;
    let buzzTotal=0;
    let total_n =0;
    let total_b =0;
    let total_c =0;
  
    if(data.uploadDateNews!=null){
      var arrayList_news =this.string_to_array(data.uploadDateNews);
      var array_count_news =[];
      var array_date =[];
      for(var i=0; i<arrayList_news.length; i++){
        array_count_news[i]=arrayList_news[i].count;
        array_date[i]=arrayList_news[i].date;
        total_n+=arrayList_news[i].count;
      }
      buzzTotal+=total_n;
    }
    //console.log(d.uploadDateBlog);
    if(data.uploadDateBlog!=null){
      var arrayList_blog =this.string_to_array(data.uploadDateBlog);
      var array_count_blog =[];
      for(var i=0; i<arrayList_blog.length; i++){
        array_count_blog[i]=arrayList_blog[i].count;
        total_b+=arrayList_blog[i].count;
      }
      buzzTotal+=total_b;
    }
    //console.log(d.uploadDateCafe);
    if(data.uploadDateCafe!=null){
      var arrayList_cafe=this.string_to_array(data.uploadDateCafe);
      var array_count_cafe =[];
      for(var i=0; i<arrayList_cafe.length; i++){
        array_count_cafe[i]=arrayList_cafe[i].count;
        total_c+=arrayList_cafe[i].count;
      }
      buzzTotal+=total_c;
    }	

    this.setState({
      buzzTotal: buzzTotal,
      buzzTotalNews: total_n,
      buzzTotalBlog: total_b,
      buzzTotalCafe: total_c
    })

  let ctx = document.getElementById("buzzChart");
    $('#buzzChart').removeData();
    var myChart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: array_date,
            datasets: [{
              label: ['news'],
              data: array_count_news,
              borderColor: '#117A65',
              fill: false,
              backgroundColor:'rgb(17,122,101,0.3)',
              borderWidth: 0.5
              },
            {
              label: ['blog'],
              data: array_count_blog,
              borderColor: '#E74C3C',
              fill: false,
              backgroundColor:'rgb(231,76,60,0.3)',
              borderWidth: 0.5
            },
            {
              label: ['cafe'],
              data: array_count_cafe,
              borderColor:  '#6C3483',
              fill: false,
              backgroundColor:'rgb(108,52,131,0.3)',
              borderWidth: 0.5
            }
          ]
          },
          options: {
            scales: {
              yAxes: [{
                ticks: {
                  beginAtZero: true
                }
              }]
            },
            tooltips: { mode: 'index' } 
          }
        })
    })
  }

  string_to_array(uploadDate){
    //데이터 중복일경우 카운트값 더하기
    var upload_date =JSON.parse(uploadDate);
    var arrayList =new Array();
    for(var i=0; i<upload_date.length; i++){
      var unique = true;
      for(var j=0;j<arrayList.length;j++)
        if ((upload_date[i].date === arrayList[j].date)) {
          arrayList[j].count=arrayList[j].count+upload_date[i].count;
                unique = false;
            }
      if (unique) {
        arrayList.push(upload_date[i]);
        }
    }
    
    arrayList.sort(function(a, b) { // 오름차순
        return a.date < b.date ? -1 : a.date > b.date ? 1 : 0;
      
    });
    
    return arrayList;
  }

  componentDidMount (){

  }
  render() {
    return(
      <Fragment>
        <Header 
          page={this.state.page}
        />
        <SearchTrend
          getSearchResultByPeriod={this._getSearchResultByPeriod}
        />
        <div id="mainPage" className="container cf">
          <div className="wrap">
            <Keywords
              naver={this.state.naver}
              zum={this.state.zum}
              twitter={this.state.twitter}
              isLoadingKeyword={this.state.isLoadingKeyword}
              getSearchResultByKeywords={this._getSearchResultByKeywords}
              selectedDate={this.state.selectedDate}
            />
            <Statistics 
              isLoadingKeyword={this.state.isLoadingKeyword}
            />
            <Buzz
              searchValue={this.state.searchValue}
              buzzTotal={this.state.buzzTotal}
              isLoadingBuzz={this.state.isLoadingBuzz}
              buzzTotalNews={this.state.buzzTotalNews}
              buzzTotalBlog={this.state.buzzTotalBlog}
              buzzTotalCafe={this.state.buzzTotalCafe}
            />
            <Relation
              searchValue={this.state.searchValue}
              relatedWords={this.state.relatedWords}
              isLoadingRelated={this.state.isLoadingRelated}
            />
            <Emotion 
              searchValue={this.state.searchValue}
            />
            <Article
              searchValue={this.state.searchValue}
              originTotal={this.state.newsOrigin.total}
              listOrigin={this.state.listOrigin}
              newsCrawler={this.state.newsCrawler}
              newsBlog={this.state.newsBlog}
              newsCafe={this.state.newsCafe}
              isLoadingArticle={this.state.isLoadingArticle}
            />
          </div>
        </div>
        <Footer />
      </Fragment>
    );
  }
}

export default App;

ReactDom.render(<App />, document.getElementById("app"));