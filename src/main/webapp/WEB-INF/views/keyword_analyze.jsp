<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<meta charset="UTF-8">
<head>
  <%@include file="./head.jsp"%>
  <title>C.view Keyword</title>

</head>
<body>
  <div id="app">
  </div>
  <script src="./dist/vendors.bundle.js"></script>
  <script type="text/javascript" src="trendup/js/d3.v3.min.js"></script>
  <script type="text/javascript" src="trendup/js/d3.layout.cloud.js"></script>
  <script src="./dist/keyword_analyze.bundle.js"></script>
</body>
<script type="text/javascript">
  function news_page(page){
    $.ajax({
      url: "/searchCrawlerNews",
      data: {
        "searchValue": $('#searchField').attr("data-attr"),
        "startDate": $('#selectedStartDate').val(),
        "endDate":$('#selectedEndDate').val(),
        "start": page
      },
      success:function(d){
      var data = d.naverCrawlerNews;
      var obj = JSON.parse(data);
      var html="";
      $('.news-page').html(obj.naverNewsPage);
      for(var i=0;i<obj.naverNews.length;i++){
        html+="<li><h5><a href='"+obj.naverNews[i].link+"' target='_blank'>"+obj.naverNews[i].title+"</a></h5><p class='source'>"+obj.naverNews[i].medium+" | "+obj.naverNews[i].date+" </p><p class='content'>"+obj.naverNews[i].description+" </p></li>"
      }
      $('#articleNews ul').html(html);
    }
    })
    
  }

  function blog_page(page){
    $.ajax({
      url: "/searchCrawlerBlog",
      data: {
        "searchValue": $('#searchField').attr("data-attr"),
        "startDate": $('#selectedStartDate').val(),
        "endDate":$('#selectedEndDate').val(),
        "start": page
      },
      success:function(d){
      var data = d.naverCrawlerBlog;
      var obj = JSON.parse(data);
      var html="";
      $('.blog-page').html(obj.naverBlogPage);
      for(var i=0;i<obj.naverBlog.length;i++){
        html+="<li><h5><a href='"+obj.naverBlog[i].url+"' target='_blank'>"+obj.naverBlog[i].title+"</a></h5><p class='source'>"+obj.naverBlog[i].writerName+" | "+obj.naverBlog[i].documentDate+" </p><p class='content'>"+obj.naverBlog[i].content+" </p></li>"
      }
      $('#articleBlog ul').html(html);
    }
    })
    
  }

  function cafe_page(page){
    $.ajax({
      url: "/searchCrawlerCafe",
      data: {
        "searchValue": $('#searchField').attr("data-attr"),
        "startDate": $('#selectedStartDate').val(),
        "endDate":$('#selectedEndDate').val(),
        "start": page
      },
      success:function(d){
      var data = d.naverCrawlerCafe;
      var obj = JSON.parse(data);
      var html="";
      $('.cafe-page').html(obj.naverCafePage);
      for(var i=0;i<obj.naverCafe.length;i++){
        html+="<li><h5><a href='"+obj.naverCafe[i].link+"' target='_blank'>"+obj.naverCafe[i].title+"</a></h5><p class='source'>"+obj.naverCafe[i].medium+" | "+obj.naverCafe[i].date+" </p><p class='content'>"+obj.naverCafe[i].description+" </p></li>"
      }
      $('#articleCafe ul').html(html);
    }
    })
    
  }
</script>
</html>