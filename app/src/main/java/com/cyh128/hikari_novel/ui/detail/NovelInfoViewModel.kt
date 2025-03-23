package com.cyh128.hikari_novel.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.cyh128.hikari_novel.data.source.local.database.bookshelf.BookshelfEntity
import com.cyh128.hikari_novel.data.source.local.database.visit_history.VisitHistoryEntity
import com.cyh128.hikari_novel.util.TimeUtil
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    var novelInfo: NovelInfo? = null
    lateinit var novel: Novel private set
    lateinit var aid: String
    private var bid: String? = null

    private var isInBookshelf: Boolean = false

    val readOrientation = appRepository.getReaderOrientation()

    fun getReadHistoryByCidFlow(cid: String) = if (readOrientation == ReaderOrientation.Vertical) {
        verticalReadRepository.getByCid(cid)
    } else {
        horizontalReadRepository.getByCid(cid)
    }

    fun getReadHistoryByVolumeFlow(volume: Int) = if (readOrientation == ReaderOrientation.Vertical) {
        verticalReadRepository.getByVolume(aid, volume)
    } else {
        horizontalReadRepository.getByVolume(aid, volume)
    }

    fun getLatestReadHistoryFlow() = if (readOrientation == ReaderOrientation.Vertical) {
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

    fun isInBookshelf() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = bookshelfRepository.getByAid(aid)
            if (result == null) {
                isInBookshelf = false
                bid = null
                sendEvent(Event.NotInBookshelfEvent,"event_novel_info_content_fragment")
            } else {
                isInBookshelf = true
                bid = result.bid
                sendEvent(Event.InBookshelfEvent,"event_novel_info_content_fragment")
            }
        }
    }

    fun loadNovelAndChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getNovelInfo(novelUrl)
                .onSuccess { success ->
                    novelInfo = success
                    getChapter()
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message),"event_novel_info_activity")
                }
            }
    }

    private suspend fun getChapter() {
        wenku8Repository.getChapter(chapterUrl)
            .onSuccess { success ->
                novel = Novel(aid, success)
                addVisitHistory()
                isInBookshelf()
                sendEvent(Event.LoadSuccessEvent,"event_novel_info_activity")
            }.onFailure { failure ->
                sendEvent(Event.NetworkErrorEvent(failure.message),"event_novel_info_activity")
            }
    }

    private suspend fun addVisitHistory() { //添加浏览记录
        visitHistoryRepository.add(
            VisitHistoryEntity(
                aid = this.aid,
                title = novelInfo!!.title,
                img = novelInfo!!.imgUrl,
                time = TimeUtil.getTimeToken()
            )
        )
    }

    private suspend fun removeNovel() {
        wenku8Repository.removeNovel(bid!!)
            .onSuccess {
                bookshelfRepository.delete(aid)
            }.onFailure { failure ->
                sendEvent(Event.NetworkErrorEvent(failure.message),"event_novel_info_activity")
            }
    }

    private suspend fun addNovel() {
        wenku8Repository.addNovel(aid)
            .onSuccess { success ->
                if (!success) sendEvent(Event.AddToBookshelfFailure,"event_novel_info_content_fragment")
                else {
                    wenku8Repository.getBookshelf(0)
                        .onSuccess { bookshelf ->
                            val bnl = bookshelf.list.find { it.aid == aid }
                            bookshelfRepository.upsert(
                                BookshelfEntity(
                                    aid = bnl!!.aid,
                                    bid = bnl.bid,
                                    detailUrl = bnl.detailUrl,
                                    title = bnl.title,
                                    img = bnl.img,
                                    classId = 0
                                )
                            )
                        }.onFailure { failure ->
                            sendEvent(Event.NetworkErrorEvent(failure.message),"event_novel_info_activity")
                        }
                }
            }.onFailure { failure ->
                sendEvent(Event.NetworkErrorEvent(failure.message),"event_novel_info_activity")
            }
    }

    fun voteNovel() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.novelVote(aid)
                .onSuccess { success ->
                    sendEvent(Event.VoteSuccessEvent(success),"event_novel_info_content_fragment")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message),"event_novel_info_activity")
                }
        }
    }

    fun addOrRemoveBook() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isInBookshelf) { //在书架中时
                removeNovel()
            } else {
                addNovel()
            }
//            getAllBookshelf() //刷新书架列表

            /*
            获取最新的bookshelfList，不能用bookshelfRepository.bookshelfList.value
            否则获取不到最新的bookshelfList
            */
            isInBookshelf() //重新获取bid
        }
    }
}