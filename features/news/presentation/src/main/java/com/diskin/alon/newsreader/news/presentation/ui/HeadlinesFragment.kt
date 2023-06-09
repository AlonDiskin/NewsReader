package com.diskin.alon.newsreader.news.presentation.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.diskin.alon.newsreader.news.application.util.NewsFeatureError
import com.diskin.alon.newsreader.news.presentation.R
import com.diskin.alon.newsreader.news.presentation.databinding.FragmentHeadlinesBinding
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import com.diskin.alon.newsreader.news.presentation.viewmodel.HeadlinesViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

@OptionalInject
@AndroidEntryPoint
class HeadlinesFragment : Fragment() {

    private val viewModel: HeadlinesViewModel by viewModels()
    private lateinit var layout: FragmentHeadlinesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Init layout binding
        layout = FragmentHeadlinesBinding.inflate(inflater,container,false)

        // Setup headlines adapter
        val adapter = HeadlinesAdapter(::onShareButtonClick,::onHeadlineClick)
        layout.headlines.adapter = adapter

        // Observe view model ui state
        viewModel.headlines.observe(viewLifecycleOwner) { adapter.submitData(lifecycle,it) }

        // Handle swipe to refresh UI events
        layout.swipeRefresh.setOnRefreshListener { adapter.refresh() }

        // Handle load state updates
        adapter.addLoadStateListener(::handleHeadlinesLoadStateUpdates)

        return layout.root
    }

    @VisibleForTesting
    fun handleHeadlinesLoadStateUpdates(loadStates: CombinedLoadStates) {
        // Handle refresh/load state
        layout.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading ||
                loadStates.append is LoadState.Loading

        // Handle loading error state
        if (loadStates.refresh is LoadState.Error) {
            handleHeadlinesLoadingError((loadStates.refresh as LoadState.Error).error)
        }

        if (loadStates.append is LoadState.Error) {
            handleHeadlinesLoadingError((loadStates.append as LoadState.Error).error)
        }
    }

    private fun handleHeadlinesLoadingError(error: Throwable) {
        val message = when(error) {
            is NewsFeatureError.DeviceConnectionError -> getString(R.string.error_message_device_connection)
            is NewsFeatureError.RemoteServerError -> getString(R.string.error_message_remote_server)
            is NewsFeatureError.InternalAppError -> getString(R.string.error_message_internal)
            else -> {
                error.printStackTrace()
                "Unexpected error!"
            }
        }

        // Notify user of error
        Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT)
            .show()
    }

    private fun onShareButtonClick(headline: UiHeadline) {
        activity?.let {
            ShareCompat.IntentBuilder(it)
                .setType(getString(R.string.mime_type_text))
                .setText(headline.sourceUrl)
                .setChooserTitle(getString(R.string.title_share_headline_chooser))
                .startChooser()
        }
    }

    private fun onHeadlineClick(headline: UiHeadline) {
        val urlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(headline.sourceUrl)
        )

        try {
            startActivity(urlIntent)
        } catch (e: ActivityNotFoundException) {
            // Notify user that device cannot open article
            Toast.makeText(requireActivity(),getString(R.string.message_article_unavailable),Toast.LENGTH_SHORT)
                .show()
        }
    }
}