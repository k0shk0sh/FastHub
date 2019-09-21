package com.fastaccess.fasthub.diff

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.fastaccess.data.model.CommitLinesModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.fasthub.dagger.annotations.ForActivity
import com.fastaccess.fasthub.diff.adapter.CommitLinesAdapter
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.base.utils.EXTRA
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.diff_patch_viewer_layout.*
import javax.inject.Inject

class DiffViewerActivity : BaseActivity() {

    @Inject lateinit var schedulerProvider: SchedulerProvider

    private var disposable: Disposable? = null
    private val adapter by lazy {
        CommitLinesAdapter {
            //TODO
        }
    }

    override fun layoutRes(): Int = R.layout.diff_patch_viewer_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        title = ""
        findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
            findViewById<TextView>(R.id.toolbarTitle).text = getString(R.string.commit)
            setNavigationIcon(R.drawable.ic_clear)
            setSupportActionBar(this)
        }
        diffRecyclerView.adapter = adapter
        intent?.getStringExtra(EXTRA)?.let { patch ->
            val observable = Observable.fromPublisher<List<CommitLinesModel>> { s ->
                runCatching { CommitLineBuilder.buildLines(patch) }
                    .onSuccess { s.onNext(it) }
                    .onFailure { s.onError(it) }
                s.onComplete()
            }
            disposable = observable.subscribeOn(schedulerProvider.ioThread())
                .observeOn(schedulerProvider.uiThread())
                .subscribe({
                    adapter.submitList(it)
                }, {
                    it.printStackTrace()
                    finish()
                })
        } ?: run { finish() }
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    companion object {
        fun startActivity(@ForActivity context: Context, patch: String) {
            context.startActivity(Intent(context, DiffViewerActivity::class.java).apply {
                putExtra(EXTRA, patch)
            })
        }
    }
}