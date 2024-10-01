package com.cyh128.hikarinovel.data.model

data class NovelInfo (
     var title: String,
     var author: String,
     var status: String,
     var finUpdate: String,
     var imgUrl: String,
     var introduce: String,
     var tag: List<String>,
     var heat: String,
     var trending: String,
     val isAnimated: Boolean
 )