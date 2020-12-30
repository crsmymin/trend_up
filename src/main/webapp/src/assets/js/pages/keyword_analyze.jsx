// import react modules
import React, { Component, Fragment } from "react";
import ReactDom from "react-dom";
import axios from 'axios'

// components
import Header from '../layouts/header.jsx'
import Footer from '../layouts/footer.jsx'

class App extends Component {
  constructor(props) {
    super(props)
  }
  componentDidMount (){

  }
  render() {
    return(
      <Fragment>
        <Header />
        <Footer />
      </Fragment>
    );
  }
}