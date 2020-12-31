import React, { Component } from "react";

class Emotion extends Component {
  constructor(props) {
    super(props)
  }
 
  componentDidMount() {
    var tab = document.querySelector("#emotion .section-title");
    tab.addEventListener("click", function(){
      this.classList.toggle("open");
      var tabCont = document.querySelector(".emotional-words");
      tabCont.classList.toggle("open");
    })
  }

  render() {
    return (
      <section id="emotion" className="cf">
        <>
        <h3 className="section-title">
            감성어 변화 : {this.props.searchValue}
          <img src="./src/assets/images/accordion_btn.svg" alt="" />  
        </h3>
        <div className="emotional-words flex-cont">
          {/* 감성어 버블차트 */}
          <div id="bubbleChart"></div>
          {/* 감성어 버블차트 끝*/}

          {/* 감성어 리스트}*/}
          <div className="words">
            <h4>감성어 랭킹</h4>
            <table>
              <thead>
                <tr>
                  <th>순위</th>
                  <th>분류</th>
                  <th>키워드</th>
                  <th>건수</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>1</td>
                  <td><span className="pos">긍정</span></td>
                  <td>즐겁다</td>
                  <td>18</td>
                </tr>
                <tr>
                  <td>2</td>
                  <td><span className="neg">부정</span></td>
                  <td>싫다</td>
                  <td>12</td>
                </tr>
                <tr>
                  <td>3</td>
                  <td><span className="neu">중립</span></td>
                  <td>상관없다</td>
                  <td>8</td>
                </tr>
              </tbody>
            </table>
          </div>
          {/*감성어 리스트 끝*/}
        </div>
        </>
      </section>
    )
  }
}

export default Emotion