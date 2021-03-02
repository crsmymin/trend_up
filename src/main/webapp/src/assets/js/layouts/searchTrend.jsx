// import react modules
import React, { Component } from "react";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class SearchTrend extends Component {
  constructor(props) {
    let today = new Date();  
    let hours = today.getHours()>= 10 ? today.getHours() : '0'+today.getHours();

    super(props) 
    this.state = {
      searchResult : [],
      hours : hours+":00:00",
      selectedDate : today
    };
  }

  _onChangeHours = () => {
    this.setState({ hours: event.target.value});
  }

  _onChangeDate = date => {
    this.setState({ selectedDate: date });
  }

  _doAnalyze = () => {
    let year = new Date().getFullYear();              
    let month = (1 + new Date().getMonth());          
        month = month >= 10 ? month : '0' + month;  
    let day = new Date().getDate();                 
        day = day >= 10 ? day : '0' + day;          
    const selectedDate = document.getElementById("selectedStartDate").value;
    const toDay = year + "." + month + "." + day;
    const numberOfHours = parseInt(this.state.hours.split(":")[0]);
    const currentHours = new Date().getHours(); 
    let limit = new Date('2017-03-29');
    limit.setHours(0);

    let today = new Date();  
    let hours = today.getHours()>= 10 ? today.getHours() : '0'+today.getHours();


    if(this.state.selectedDate<limit){
      alert("2017년 03월 29일 이후로만 가능합니다.");
      this.setState({
        hours : hours+":00:00",
        selectedDate : today
      });
    } else if (selectedDate === toDay && numberOfHours > currentHours) {
      alert("검색은 현재시간 이전으로만 가능합니다.");
      this.setState({ hours : hours+":00:00"});
      //return false;
    } else {
      this.setState({
        isLoadingAll: true
      })
      
      axios({
        method: 'get',
        url: "/searchRank",
        params: {
          searchValue: selectedDate + "T" + this.state.hours
        }
      })
      .then(res => {
        const data = res.data;
        this.setState({ 
          searchResult: data,
          isLoadingAll: false
        });
        this.props.getKeywordsByDate(this.state.searchResult,this.state.isLoadingAll);
        $(".keywords .list ul").scrollTop(0);
      })
      .catch(error => {
        console.log(error)
      })    
    }
  }

  componentDidMount() {
    this._doAnalyze();
  }

  render () {
    return (
      <>
        <div className={this.state.isLoadingAll === true ? ("loading-indicator-all show"):("loading-indicator-all")}>
          <div className="loader-all"></div>
        </div>
        <div className="search-area">
          <div className="wrap flex-cont">
            <h4>
              궁금한 <strong>날짜</strong> 를 검색해보세요
            </h4>
            <ul id="optionByDate">
              <li className="field flex-cont">
                <div id="periodDate">
                  <DatePicker
                    id="selectedStartDate"
                    value={this.state.selectedDate}
                    selected={this.state.selectedDate}
                    onChange={this._onChangeDate}
                    dateFormat="yyyy.MM.dd"
                    maxDate={new Date()}
                  />
                </div>
                <div id="periodTime">
                  <select
                    id="hoursSelect"
                    value={this.state.hours}
                    onChange={this._onChangeHours}
                  >
                    <option value="00:00:00">00:00</option>
                    <option value="01:00:00">01:00</option>
                    <option value="02:00:00">02:00</option>
                    <option value="03:00:00">03:00</option>
                    <option value="04:00:00">04:00</option>
                    <option value="05:00:00">05:00</option>
                    <option value="06:00:00">06:00</option>
                    <option value="07:00:00">07:00</option>
                    <option value="08:00:00">08:00</option>
                    <option value="09:00:00">09:00</option>
                    <option value="10:00:00">10:00</option>
                    <option value="11:00:00">11:00</option>
                    <option value="12:00:00">12:00</option>
                    <option value="13:00:00">13:00</option>
                    <option value="14:00:00">14:00</option>
                    <option value="15:00:00">15:00</option>
                    <option value="16:00:00">16:00</option>
                    <option value="17:00:00">17:00</option>
                    <option value="18:00:00">18:00</option>
                    <option value="19:00:00">19:00</option>
                    <option value="20:00:00">20:00</option>
                    <option value="21:00:00">21:00</option>
                    <option value="22:00:00">22:00</option>
                    <option value="23:00:00">23:00</option>
                  </select>
                </div>
              </li>
            </ul>
            <div className="btn-wrap">
              <button type="button" id="btnExecution" className="btn-s" onClick={this._doAnalyze}>
                트렌드 순위 조회
              </button>
            </div>
          </div>
        </div>
      </>
    );
  };
};

export default SearchTrend;