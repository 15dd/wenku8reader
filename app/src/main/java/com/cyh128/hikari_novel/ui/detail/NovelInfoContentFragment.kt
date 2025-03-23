package com.cyh128.hikari_novel.ui.detail

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.ReadParcel
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.databinding.FragmentNovelInfoContentBinding
import com.cyh128.hikari_novel.ui.detail.comment.CommentActivity
import com.cyh128.hikari_novel.ui.main.home.search.SearchActivity
import com.cyh128.hikari_novel.ui.other.PhotoViewActivity
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NovelInfoContentFragment : BaseFragment<FragmentNovelInfoContentBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[NovelInfoViewModel::class.java] }

    private var chapterAdapter: NovelChapterListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveEvent<Event>("event_novel_info_content_fragment") { event ->
            when (event) {
                Event.AddToBookshelfFailure -> {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setIcon(R.drawable.ic_error)
                        .setTitle(R.string.add_novel_error)
                        .setMessage(R.string.add_novel_error_msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }

                Event.InBookshelfEvent -> {
                    setRemoveNovelButton()
                    binding.bFNovelInfoContentBookshelf.isEnabled = true
                }

                Event.NotInBookshelfEvent -> {
                    setAddNovelButton()
                    binding.bFNovelInfoContentBookshelf.isEnabled = true
                }

                is Event.VoteSuccessEvent -> {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setIcon(R.drawable.ic_recommend)
                        .setTitle(R.string.vote)
                        .setMessage(event.msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                    binding.bFNovelInfoContentVote.isEnabled = true
                }

                else -> {}
            }
        }

        if (viewModel.novelInfo == null) { //防止activity重建后数据丢失
            viewModel.loadNovelAndChapter()
        } else {
            viewModel.isInBookshelf()
            initView()
            initListener()
        }
    }

    private fun initView() {
        binding.apply {
            viewModel.novelInfo!!.also { nv ->
                tvFNovelInfoContentTitle.text = nv.title
                tvFNovelInfoContentAuthor.apply {
                    text = nv.author
                    setOnClickListener {
                        startActivity<SearchActivity> {
                            putExtra("author", nv.author)
                        }
                    }
                }

                nv.status.also { status ->
                    tvFNovelInfoContentStatus.text = status
                    if (status == "已完结") ivFNovelInfoContentStatus.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_done_all
                        )
                    )
                }
                tvFNovelInfoContentFinUpdate.text = nv.finUpdate
                tvFNovelInfoContentHeat.text = nv.heat
                tvFNovelInfoContentTrending.text = nv.trending

                if (nv.isAnimated) {
                    tvFNovelInfoContentIsAnimated.text = getString(R.string.animated)
                    ivFNovelInfoContentAnim.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_live_tv
                        )
                    )
                } else {
                    tvFNovelInfoContentIsAnimated.text = getString(R.string.not_animated)
                    ivFNovelInfoContentAnim.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_tv_off
                        )
                    )
                }

                Glide.with(ivFNovelInfoContent)
                    .load(nv.imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivFNovelInfoContent)

                etvFNovelInfoContent.setContent(
                    Html.fromHtml(nv.introduce, Html.FROM_HTML_MODE_COMPACT).toString()
                )
                rvFNovelInfoContent.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = TagChipAdapter(nv.tag)
                }

            }

            chapterAdapter = NovelChapterListAdapter(
                novel = viewModel.novel,
                onItemClick = { volumePos: Int, chapterPos: Int ->
                    if (viewModel.readOrientation == ReaderOrientation.Vertical) {
                        startActivity<com.cyh128.hikari_novel.ui.read.vertical.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, volumePos, chapterPos, false)
                            )
                        }
                    } else {
                        startActivity<com.cyh128.hikari_novel.ui.read.horizontal.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, volumePos, chapterPos, false)
                            )
                        }
                    }
                },
                onLongClick = { cid ->
                    //长按事件
                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_reading_history_tip)
                        .setIcon(R.drawable.ic_delete)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.deleteReadHistory(cid)
                        }
                        .show()
                },
                onGroupItemChangeListener = { group, binding ->
                    viewModel.getReadHistoryByVolumeFlow(group).onEach {
                        if (it.isNullOrEmpty()) {
                            binding.tvIChapterVcssCompleted.text = getString(R.string.unread)
                            binding.tvIChapterVcss.isEnabled = true
                            binding.tvIChapterVcssCompleted.isEnabled = true
                        } else if (it.size == viewModel.novel.volume[group].chapters.size) {
                            var isAllRead = false
                            it.forEach { entity -> isAllRead = entity.progressPercent == 100 }
                            if (isAllRead) {
                                binding.tvIChapterVcssCompleted.text = getString(R.string.completed_reading)
                                binding.tvIChapterVcss.isEnabled = false
                                binding.tvIChapterVcssCompleted.isEnabled = false
                            } else {
                                binding.tvIChapterVcssCompleted.text = getString(R.string.partly_completed_reading)
                                binding.tvIChapterVcss.isEnabled = true
                                binding.tvIChapterVcssCompleted.isEnabled = true
                            }
                        } else {
                            binding.tvIChapterVcssCompleted.text = getString(R.string.partly_completed_reading)
                            binding.tvIChapterVcss.isEnabled = true
                            binding.tvIChapterVcssCompleted.isEnabled = true
                        }
                    }.launchIn(lifecycleScope)
                },
                onChildItemChangeListener = { group, child, binding ->
                    viewModel.getReadHistoryByCidFlow(viewModel.novel.volume[group].chapters[child].cid).onEach {
                        if (it == null) {
                            binding.tvIChapterCcssCompleted.text = getString(R.string.unread)
                            binding.tvIChapterCcss.isEnabled = true
                            binding.tvIChapterCcssCompleted.isEnabled = true

                            binding.tvIChapterCcssLatest.text = null
                            return@onEach
                        } else if (it.progressPercent == 100) {
                            binding.tvIChapterCcssCompleted.text = getString(R.string.completed_reading)
                            binding.tvIChapterCcss.isEnabled = false
                            binding.tvIChapterCcssCompleted.isEnabled = false
                        } else {
                            binding.tvIChapterCcssCompleted.text = "${it.progressPercent}%"
                            binding.tvIChapterCcss.isEnabled = true
                            binding.tvIChapterCcssCompleted.isEnabled = true
                        }

                        if (it.isLatest) binding.tvIChapterCcssLatest.text = getString(R.string.last_read)
                        else binding.tvIChapterCcssLatest.text = null
                    }.launchIn(lifecycleScope)
                }
            )

            ervFNovelInfoContent.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = chapterAdapter
            }
        }
    }

    private fun initListener() {
        binding.bFNovelInfoContentComment.setOnClickListener {
            startActivity<CommentActivity> {
                putExtra("aid", viewModel.aid)
            }
        }

        binding.bFNovelInfoContentBookshelf.setOnClickListener {
            it.isEnabled = false
            viewModel.addOrRemoveBook()
        }

        binding.bFNovelInfoContentVote.setOnClickListener {
            viewModel.voteNovel()
            binding.bFNovelInfoContentVote.isEnabled = false
        }

        binding.ivFNovelInfoContent.setOnClickListener {
            startActivity<PhotoViewActivity> {
                putExtra("url", viewModel.novelInfo!!.imgUrl)
            }
        }
    }

    private fun setRemoveNovelButton() {
        binding.bFNovelInfoContentBookshelf.setIconResource(R.drawable.ic_baseline_favorite)
        binding.bFNovelInfoContentBookshelf.text = getString(R.string.added_in_bookshelf)
    }

    private fun setAddNovelButton() {
        binding.bFNovelInfoContentBookshelf.setIconResource(R.drawable.ic_outline_favorite)
        binding.bFNovelInfoContentBookshelf.text = getString(R.string.add_to_bookshelf)
    }
}