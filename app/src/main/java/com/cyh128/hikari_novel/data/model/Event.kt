package com.cyh128.hikari_novel.data.model

sealed class Event {
    data object LoadSuccessEvent: Event() //加载内容成功
    data class NetworkErrorEvent(val msg: String?): Event() //网络错误
    data class VoteSuccessEvent(val msg: String?): Event() //投票成功
    data object InBookshelfEvent: Event() //此书在书架中
    data object NotInBookshelfEvent: Event() //此书不在书架中
    data object AddToBookshelfFailure: Event() //添加书架失败
    data object SearchInitSuccessEvent: Event() //初始化内容页面成功
    data object SearchResultEmptyEvent: Event() //搜索内容为空
    data object SearchLoadErrorCauseByInFiveSecondEvent: Event() //加载失败，因为5秒限制
    data object SearchInitErrorCauseByInFiveSecondEvent: Event() //初始化内容页面失败，因为5秒限制
    data object LogInSuccessEvent: Event() //登录成功
    data object LogInFailureEvent: Event() //登录失败
    data object AuthFailedEvent: Event() //验证失败事件(用户名或者密码错误)
    data object SignInSuccessEvent: Event() //签到成功
    data object SignInFailureEvent: Event() //签到失败
    data object TempSignInUnableEvent: Event() //临时签到不可用事件
    data object RefreshSearchHistoryEvent: Event() //刷新搜索记录事件

    data object RemoveNovelFromListEvent: Event() //从书架批量删除小说事件
    data object MoveNovelFromListEvent: Event() //从书架批量移动小说事件
    data object SelectAllEvent: Event() //选择全部书籍事件
    data object DeselectEvent: Event() //取消选择书籍事件
    data object ExitSelectionModeEvent: Event() //退出多选模式事件
    data object EditBookshelfEvent: Event() //编辑书架事件

    data class ChangeLineSpacingEvent(val value: Float): Event() //修改行距事件
    data class ChangeFontSizeEvent(val value: Float): Event() //修改字体大小事件
    
    data object HaveAvailableUpdateEvent: Event() //有更新
    data object NoAvailableUpdateEvent: Event() //无更新

    data object ThemeChangedEvent: Event() //主题颜色改变事件
    data object LanguageChantedEvent: Event() //语言改变事件
}