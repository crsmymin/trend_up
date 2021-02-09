// import react modules
import React, { Component, Fragment } from "react";
import ReactDom from "react-dom";
import axios from 'axios'

// components
// components
import Header from '../layouts/header.jsx'
import Footer from '../layouts/footer.jsx'
import SearchKeyword from '../layouts/searchKeyword.jsx'
import SearchByPeriod from "../components/search-by-period.jsx";
import Statistics from '../components/statistics.jsx'
import Buzz from '../components/buzz.jsx'
import Relation from '../components/relation.jsx'
import Emotion from '../components/emotion.jsx'
import Article from '../components/article.jsx'

class App extends Component {
  constructor(props) {
    let today = new Date(); 

    let today_to_month=1+today.getMonth();
    today_to_month=today_to_month>= 10 ? today_to_month : '0' + today_to_month;
    let today_to_day=today.getDate()>= 10 ? today.getDate() : '0' + today.getDate();
    let selectedDate =today.getFullYear()+"."+today_to_month+"."+today_to_day;
    let currentHours = today.getHours()>= 10 ? today.getHours() : '0'+today.getHours();
    
    let start_date = new Date(today);
    start_date.setDate(start_date.getDate()-7);

    let start_to_month=1+start_date.getMonth();
    start_to_month=start_to_month>= 10 ? start_to_month : '0' + start_to_month;
    let start_to_day=start_date.getDate()>= 10 ? start_date.getDate() : '0' + start_date.getDate();
    let startDate =start_date.getFullYear()+"."+start_to_month+"."+start_to_day;
  
    let end_to_month=1+today.getMonth();
    end_to_month=end_to_month>= 10 ? end_to_month : '0' + end_to_month;
    let end_to_day=today.getDate()>= 10 ? today.getDate() : '0' + today.getDate();
    let endDate =today.getFullYear()+"."+end_to_month+"."+end_to_day;

    super(props);
    this.state = {
      page : "keyword",
      selectedDate : selectedDate,
      hours : currentHours+":00:00",
      // 검색 조건 
      startDate : startDate,
      endDate : endDate,
      searchValue : "",
      // 키워드 순위
      naver: [],
      zum: [],
      nate: [],
      // 원간 검색량 
      searchTotal: 0,
      searchPC: 0,
      searchMobile: 0,
      // 버즈추이 수 
      buzzTotal: "",
      buzzTotalNews: 0,
      buzzTotalBlog: 0,
      buzzTotalCafe: 0,
      // 연관어 순위
      relatedWords: [],
      // 감성어 순위 + 긍부정 퍼센트
      emotionWords: [], 
      keywordNegative: 0,
      keywordNeutral: 0,
      keywordPositive: 0,
      keywordEtc: 0,
      // 원문보기
      listOrigin: [],
      newsCrawler: [],
      newsBlog: [],
      newsCafe: [],
      //로딩 플러그 
      isLoadingArticle : true,
      isLoadingBuzz: true,
      isLoadingRelated: true,
      isLoadingEmotion: true
    }
  }


  // get keywords by data
  _getKeywordsByDate = () => {
    axios({
      method: 'get',
      url: "/searchRankAuto",
      params: {
        searchValue: this.state.selectedDate + "T" + this.state.hours
      }
    })
    .then(res => {
      const data = res.data;
      this.setState({ 
        searchValue: data.naver.naverRank[0],
      });
      $('#searchField').val(data.naver.naverRank[0]);

      this._getSearchResultByKeywords(data.naver.naverRank[0]);

    })
    .catch(error => {
      console.log(error)
    }) 

    }
  // get data by period
  _getDataByPeriod = (startDate,endDate) => {
    this.setState({
      startDate: startDate,
      endDate: endDate
    })
  }

  // get search result by keywords
  _getSearchResultByKeywords = (keyword) => {
  if(keyword==undefined) keyword=this.state.searchValue;
    this.setState({
      isLoadingArticle: true,
      isLoadingBuzz: true,
      isLoadingRelated: true
    })
    //키워드를 통한 컨텐츠 조회
    axios({
        method: 'get',
        url: "/searchNaverNews",
        params: {
          searchValue: keyword,
          startDate: this.state.startDate,
          endDate: this.state.endDate,
          start: 1
        }
      })
      .then(res => {
        const data = res.data;
        let searchValue = data.searchValue;
        let parseData = JSON.parse(data.naverNews);
        let originTotal = parseData.total;
        let newsCrawler = JSON.parse(data.naverCrawlerNews);
        let newsBlog = JSON.parse(data.naverCrawlerBlog);
        let newsCafe = JSON.parse(data.naverCrawlerCafe);
        let listOrigin = parseData.items;
        let searchTotal=parseData.searchTotal;
        let searchMobile=parseData.searchMobile;
        let searchPc=parseData.searchPc;
        this.setState({
        searchValue,
        originTotal,
        newsCrawler,
        newsBlog,
        newsCafe,
        listOrigin,
        isLoadingArticle: false,
        searchTotal,
        searchMobile,
        searchPc
        })
        
        this.draw_buz();
        this.draw_related();
        this.draw_emotion();
      })
      .catch(error => {
        console.log(error)
      })
  }
  
  draw_buz = () => {
    axios({
      method: 'get',
      url: "/drawBuzzChart",
      params: {
        searchValue: this.state.searchValue,
        startDate: this.state.startDate,
        endDate: this.state.endDate,
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

  draw_related = () =>{
    // 연관어 순위 
    axios({
      method: 'get',
      url: "/drawWordCloud",
      params: {
        searchValue: this.state.searchValue,
        startDate: this.state.startDate,
        endDate: this.state.endDate,
        start: 1
      }
    })
    .then(res => {
      const data = res.data;
      let relatedWords = JSON.parse(data.morpheme);
      const arrayList =new Array();

      if (relatedWords != null && relatedWords.length > 1) {
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
    })
    .catch(error => {
      console.log(error)
    })
  }
  
  draw_emotion = () => {
    // 감성어 순위 
    axios({
      method: 'get',
      url: "/emotionAnalysis",
      params: {
        searchValue: this.state.searchValue.replace(/(\s*)/g, ""),
        startDate: this.state.startDate,
        endDate: this.state.endDate
      }
    })
   .then(res => {
     const data = res.data;
     let emotionAnalysis = JSON.parse(data.emotionAnalysis);
     this.setState({
       emotionWords:emotionAnalysis.data,
       keywordNegative: emotionAnalysis.keywordMap.negative,
       keywordNeutral :emotionAnalysis.keywordMap.neutral,
       keywordPositive :emotionAnalysis.keywordMap.positive,
       keywordEtc :emotionAnalysis.keywordMap.other,
       isLoadingEmotion: false
     })
     // set word cloud
     let resData = this.state.emotionWords;
     let width = 600;
     let height = 475;
     let fill = d3.scale.category20c();
     let wordScale = d3.scale.linear().range([30, 70]);
     if(resData.length === 0) {
       console.log("조회된 감성어 데이터없음");
       document.querySelector(".emotional-words .inner-box").style.width = "100%";
       document.getElementById("wordCloud2").innerHTML="<h5>조회된 감성어 데이터가 없습니다.</h5>"
     } else {
       document.querySelector(".emotional-words .inner-box").style.width = "70%";
       let subjects = resData
       .map(function (d) { return { text: d.name, size: +d.frequency, type: d.polarity } })
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
       let wordCloudWrap = document.getElementById("wordCloud2");
       
       $('#wordCloud2').html("");
       d3.select(wordCloudWrap).append("svg")
         .attr("width", width)
         .attr("height", height)
         .append("g")
         .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")")
         .selectAll("text")
         .data(words)
         .enter().append("text")
         .style("font-size", function (d) { 
           return d.size + "px"; 
         })
         .style("font-family", "Impact")
         .style("fill", function(d,i) { 
           //console.log(d.type);
           return d.type === "positive" ? "#5d9cec" : d.type === "negative" ? "#ef6674" : d.type === "neutral" ? "#7cbf4c" : "grey";
         })
         .attr("text-anchor", "middle")
         .attr("transform", function (d) {
           return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
         })
         .text(function (d) { 
           return d.text; 
         });
       }
     }
   })
   .catch(error => {
     console.log(error)
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
    this._getKeywordsByDate();
  }

  render() {
    return(
      <Fragment>
        <Header 
          page={this.state.page} 
        />
        <div id={this.state.page === "keyword" ? "keyword" : ""} className="container cf">
          <div className="wrap">
            <SearchKeyword  
              searchValue={this.state.searchValue}
              getSearchResultByKeywords={this._getSearchResultByKeywords}
              getDataByPeriod={this._getDataByPeriod}
            />
            <Statistics 
              searchValue={this.state.searchValue}
              searchMobile={this.state.searchMobile}
              searchPC={this.state.searchPc}
              searchTotal={this.state.searchTotal}
              newsCrawler={this.state.newsCrawler}
              newsBlog={this.state.newsBlog}
              newsCafe={this.state.newsCafe}
              newsCafe={this.state.newsCafe}
            />
            <Buzz
              searchValue={this.state.searchValue}
              buzzTotal={this.state.buzzTotal}
              buzzTotalNews={this.state.buzzTotalNews}
              buzzTotalBlog={this.state.buzzTotalBlog}
              buzzTotalCafe={this.state.buzzTotalCafe}
              isLoadingBuzz={this.state.isLoadingBuzz}
            />
            <Relation
              searchValue={this.state.searchValue}
              relatedWords={this.state.relatedWords}
              isLoadingRelated={this.state.isLoadingRelated}
            />
            <Emotion 
              searchValue={this.state.searchValue}
              keywordNegative={this.state.keywordNegative}
              keywordNeutral={this.state.keywordNeutral}
              keywordPositive={this.state.keywordPositive}
              keywordEtc={this.state.keywordEtc}
              emotionWords={this.state.emotionWords}
              isLoadingEmotion={this.state.isLoadingEmotion}
            />
            <Article
              searchValue={this.state.searchValue}
              originTotal={this.state.originTotal}
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