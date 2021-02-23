// import react modules
import React, { Component } from 'react';

class Footer extends Component {
  constructor(props) {
    super(props)
    
  }
  
  render () {
    return (
      <footer>
        <div className="wrap">
          <p className="footer-info">
            서울특별시 서대문구 성산로 13길 39<br/>
              TEL : 02 338 9395  <br/>  E-MAIL : master@cside.co.kr
          </p>
          <p className="copyright">
            Copyright 2020  C.VIEWCorp. All rights reserved.
          </p>
        </div>
      </footer>
    );
  }
};

export default Footer;