package com.cyh128.hikari_novel.ui.view.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.Novel
import com.cyh128.hikari_novel.data.model.NovelInfo
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.HorizontalReadRepository
import com.cyh128.hikari_novel.data.repository.VerticalReadRepository
import com.cyh128.hikari_novel.data.repository.VisitHistoryRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.visit_history.VisitHistoryEntity
import com.cyh128.hikari_novel.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelInfoViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val appRepository: AppRepository,
    private val visitHistoryRepository: VisitHistoryRepository,
    private val verticalReadRepository: VerticalReadRepository,
    private val horizontalReadRepository: HorizontalReadRepository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val bookshelfList = bookshelfRepository.bookshelfList

    lateinit var novelInfo: NovelInfo
    lateinit var novel: Novel private set
    lateinit var aid: String
    private var bid: String? = null

    private var isInBookshelf: Boolean = false

    val readOrientation = appRepository.getReaderOrientation()

    fun getReadHistoryByCid(cid: String) = if (readOrientation == ReaderOrientation.Vertical) {
        verticalReadRepository.getByCid(cid)
    } else {
        horizontalReadRepository.getByCid(cid)
    }

    fun getReadHistoryByVolume(volume: Int) = if (readOrientation == ReaderOrientation.Vertical) {
        verticalReadRepository.getByVolume(aid, volume)
    } else {
        horizontalReadRepository.getByVolume(aid, volume)
    }

    fun getLatestReadHistory() = if (readOrientation == ReaderOrientation.Vertical) {
        verticalReadRepository.getLatestChapter(aid)
    } else {
        horizontalReadRepository.getLatestChapter(aid)
    }

    fun deleteAllReadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            if (readOrientation == ReaderOrientation.Vertical) {
                verticalReadRepository.deleteAll(aid)
            } else {
                horizontalReadRepository.deleteAll(aid)
            }
        }
    }

    val novelUrl get() = "https://${wenku8Repository.getWenku8Node()}/modules/article/articleinfo.php?id=$aid"
    private val chapterUrl get() = "https://${wenku8Repository.getWenku8Node()}/modules/article/reader.php?aid=$aid"

    fun deleteReadHistory(cid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (readOrientation == ReaderOrientation.Vertical) verticalReadRepository.delete(cid)
            else horizontalReadRepository.delete(cid)
        }
    }

    private suspend fun isInBookshelf(list: List<BookshelfNovelInfo>) {
        bid = null //bid置空，防止在查不到对应的bid时错误判断此aid已在书架内
        for (i in list) {
            if (i.aid == aid) {
                bid = i.bid
                break
            }
        }

        if (bid.isNullOrBlank()) {
            isInBookshelf = false
            _eventFlow.emit(Event.NotInBookshelfEvent)
            Log.d("n i v m", "not in bookshelf")
        } else {
            isInBookshelf = true
            _eventFlow.emit(Event.InBookshelfEvent)
            Log.d("n i v m", "in bookshelf")
        }
    }

    fun loadNovelAndChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getNovelInfo(novelUrl)
                .onSuccess { success ->
                    novelInfo = success
                    getChapter()
                }.onFailure { failure ->
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
            }
    }

    private suspend fun getChapter() {
        wenku8Repository.getChapter(chapterUrl)
            .onSuccess { success ->
                novel = Novel(aid, success)
                addVisitHistory()
                isInBookshelf(bookshelfList.value)
                _eventFlow.emit(Event.LoadSuccessEvent)
            }.onFailure { failure ->
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
    }

    private suspend fun addVisitHistory() { //添加浏览记录
        visitHistoryRepository.add(
            VisitHistoryEntity(
                aid = this.aid,
                title = novelInfo.title,
                img = novelInfo.imgUrl,
                time = TimeUtil.getTimeToken()
            )
        )
    }

    private suspend fun removeNovel() {
        wenku8Repository.removeNovel(bid!!)
            .onFailure { failure ->
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
    }

    private suspend fun addNovel() {
        wenku8Repository.addNovel(aid)
            .onSuccess { success ->
                if (!success) {
                    _eventFlow.emit(Event.AddToBookshelfFailure)
                }
            }.onFailure { failure ->
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
    }

    fun voteNovel() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.novelVote(aid)
                .onSuccess { success ->
                    _eventFlow.emit(Event.VoteSuccessEvent(success))
                }.onFailure { failure ->
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
        }
    }

    //刷新书架列表
    private suspend fun refreshBookshelfList() {
        wenku8Repository.getBookshelf()
            .onSuccess { success ->
                bookshelfRepository.updateBookshelfList(success)
            }.onFailure { failure ->
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
    }

    fun addOrRemoveBook() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isInBookshelf) { //在书架中时
                Log.d("n i v m", "in bookshelf, prepare to remove")
                removeNovel()
            } else {
                Log.d("n i v m", "not in bookshelf, prepare to add")
                addNovel()
            }

            refreshBookshelfList() //刷新书架列表

            Log.d("n i v m", "refreshBookshelfList passed")

            /*
            获取最新的bookshelfList，不能用bookshelfRepository.bookshelfList.value
            否则获取不到最新的bookshelfList
            */
            isInBookshelf(bookshelfRepository.bookshelfList.take(1).last()) //重新获取bid
        }
    }
}