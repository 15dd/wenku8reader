<div align="center">
  <div align="center">
    <img src="./README/logo.png" alt="Logo" width="100" height="100">
  </div>

  <h2 align="center">轻小说文库</h2>
  <div align="center">
    一个 <b>第三方</b> 轻小说文库 <b>阅读器</b>
    <br />
    <a href="https://www.bilibili.com/video/BV1ns4y1B7J2/"><b>视频展示</b></a>
    <br />
  </div>

</div>

- 使用Material Design 3，界面简洁
- 支持阅读进度保存
- 深色模式适配
## 注意
- 本软件是轻小说文库的第三方软件，本软件数据全部来自轻小说文库(www.wenku8.net)
- 支持的安卓版本：Android 10+ (API 29+)

<h2>觉得好用别忘了star哦，你的star是对我最大的激励</h2>

## 目录
- [安装](#安装)
- [软件截图](#软件截图)
- [代码目录结构](#代码目录结构)
- [感谢](#感谢)
- [作者的话](#作者的话)

## 安装
[前往release页面下载安装](https://github.com/15dd/wenku8reader/releases)

## 软件截图
浅色模式
<div align="left">
  <img src="./README/1.jpg" width="18%"></img> <img src="./README/2.jpg" width="18%"></img> <img src="./README/3.jpg" width="18%"></img> <img src="./README/4.jpg" width="18%"></img> <img src="./README/5.jpg" width="18%"></img> 
</div>

<br>

深色模式
<div align="left">
  <img src="./README/n1.jpg" width="18%"></img> <img src="./README/n2.jpg" width="18%"></img> <img src="./README/n3.jpg" width="18%"></img> <img src="./README/n4.jpg" width="18%"></img> <img src="./README/n5.jpg" width="18%"></img> 
</div>

## 代码目录结构
```
wenku8reader
│  App.java 实现MD3动态取色、数据库初始化和读取数据
│  
├─activity
│      AppActivity.java 首页界面
│      CommentActivity.java 评论界面
│      CommentInCommentActivity.java 评论回复界面
│      ContentsActivity.java 小说目录界面
│      LoginingActivity.java 开屏、等待登录完成界面
│      LoginInputActivity.java 登录和注册界面
│      PhotoViewActivity.java 查看原图界面
│      ReaderActivity.java 阅读器界面
│      SearchActivity.java 搜索界面
│      SettingActivity.java 设置界面
│      TagSearchActivity.java 按Tag搜索的界面
│      TagSelectActivity.java 选择Tag的界面
│      CrashActivity 软件崩溃界面
│      UserInfoActivity 用户信息界面
│      
├─adapter
│      BookCaseAdapter.java 书架adapter
│      BookListAdapter.java 小说信息adapter
│      CommentAdapter.java 评论adapter
│      CommentInCommentAdapter.java 回复adapter
│      ContentsListAdapter.java 小说目录列表adapter
│      ReaderAdapter.java 阅读器插图adapter(如果有插图的话)
│      
├─bean
│      BookcaseBean.java 书架bean
│      BookListBean.java 小说信息bean
│      ContentsCcssBean.java 章节bean
│      ContentsVcssBean.java 卷bean
│      
├─fragment
│      BookCaseFragment.java 书架fragment
│      BookListFragment.java 小说信息fragment
│      HomeFragment.java 首页fragment
│      MyinfoFragment.java 我的fragment
│      ReadFragment.java 阅读器fragment
│      SearchFragment.java 搜索fragment
│      TagSearchFragment.java 按Tag搜索的fragment
│      
└─util
       CheckNetwork.java 检测网络状态
       CheckUpdate.java 检查更新
       LoginWenku8.java 登录wenku8
       MyNestedScrollView.java 自定义NestedScrollView
       VarTemp.java 全局变量存放
       Wenku8Spider.java Wenku8爬虫
```
## 感谢
### 第三方库
- org.jsoup:jsoup 解析html
- com.squareup.okhttp3:okhttp  网络请求
- com.github.chrisbanes:PhotoView  图片查看器
- com.github.bumptech.glide:glide  加载图片
- com.alibaba:fastjson  解析json
- com.github.youlookwhat:ByRecyclerView  上拉加载，下拉刷新
- com.github.MZCretin:ExpandableTextView  可展开和收回的TextView
- com.github.TutorialsAndroid:crashx  全局崩溃拦截
- com.github.youxiaochen:expandable-recyclerview 可展开和收回的recyclerview（用这个主要是不会与nestedscrollview发生滑动冲突）
### 开源项目
- https://github.com/ya-b/NetNovelReader  阅读器实现
### 其他
- https://github.com/wildma/ScreenAdaptation  根据屏幕分辨率适配组件大小（Android Studio插件）
- AI


# 作者的话
<b>
本人只是个职高生（目前在读），能力实在有限，所以如果有比较影响体验的bug的话请谅解。
<br>
因为是职高生，所以开发新功能和解决问题的速度会比较慢，这个也请谅解。
</b>
<br><br>
在写那个阅读器时，真的花了我很多时间，在Github上找了很久，总算找到一个可以轻松Ctrl+C的项目（不需要在一串很长的代码、很多文件中找到自己想要的。直接将与reader相关的文件复制过来，然后setText即可），但是只能显示文本，显示不了图片。于是我去修改了他的代码，但在修改的时候是真的很痛苦，首先他的代码都是kotlin写的，我还需要去学一下语法，然后是他使用了自定义view，这方面我根本没了解过，所以在想显示图片的时候也花了很多时间，最后的方法也很简单：写一个跟显示文本页面(PageText)差不多的自定义view，然后修改一下PageView的代码就行了。
<br><br>
<br><br>
本项目是从2023.4.8开始写起来的（安卓相关知识也是从这个时候开始学习的），平常也就放学回家和周六周日的时候写这个，由于本人之前从来没有接触过安卓开发，所以这个软件都是边学边写的，难免会出现一些问题。我甚至连Java都没怎么学过，都是靠C++的语法基础的，所以看源代码的时候轻喷。<br>
很多代码都是我从网上抄的，源代码注释也有写出处。源代码中的文件夹，文件和变量名的命名都挺混乱的，请见谅。
<br><br>
我写这个软件的目的是为了适配高版本的安卓系统。<br>
之前一直在用其他人写的第三方软件，但是在安卓版本高的系统上会出现一些问题（例如安卓13），所以就自己写了一个软件。
<br><br>
软件界面的颜色配色我是直接使用了MD3的动态配色，我个人很喜欢MD3的动态配色和Material Design风格。
<br><br>
本人也在这个项目中学到了很多东西，比如class的好处（以前我都不喜欢用class的，因为没理解😂），收获也挺多的。

## 开源协议

```
MIT License

Copyright (c) 2023 cyh128

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```