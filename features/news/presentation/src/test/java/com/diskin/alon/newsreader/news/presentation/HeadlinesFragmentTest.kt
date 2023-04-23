package com.diskin.alon.newsreader.news.presentation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.paging.*
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.diskin.alon.newsreader.common.uitesting.*
import com.diskin.alon.newsreader.common.uitesting.RecyclerViewMatcher.withRecyclerView
import com.diskin.alon.newsreader.news.application.util.NewsFeatureError
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesAdapter
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesAdapter.HeadlineViewHolder
import com.diskin.alon.newsreader.news.presentation.ui.HeadlinesFragment
import com.diskin.alon.newsreader.news.presentation.viewmodel.HeadlinesViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowToast

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@SmallTest
@Config(instrumentedPackages = ["androidx.loader.content"])
class HeadlinesFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val viewModel: HeadlinesViewModel = mockk()

    // Stub data
    private val headlines = MutableLiveData<PagingData<UiHeadline>>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns viewModel

        // Stub view model
        every { viewModel.headlines } returns headlines

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<HeadlinesFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun showHeadlinesPaging_WhenResumedAndHeadlinesLoaded() {
        // Given
        val uiHeadlines = createUiHeadlines()
        val paging = PagingData.from(uiHeadlines)

        // When
        headlines.value = paging

        // Then
        onView(withId(R.id.headlines))
            .check(matches(isRecyclerViewItemsCount(uiHeadlines.size)))

        uiHeadlines.forEachIndexed { index, uiHeadline ->
            onView(withId(R.id.headlines))
                .perform(scrollToPosition<HeadlineViewHolder>(index))
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineTitle))
                .check(matches(withText(uiHeadline.title)))
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineDate))
                .check(matches(withText(uiHeadline.date)))
            onView(withRecyclerView(R.id.headlines).atPositionOnView(index, R.id.headlineSource))
                .check(matches(withText(uiHeadline.sourceName)))
        }
    }

    @Test
    fun showProgressIndicator_WhileHeadlinesLoaded() {
        // Given
        val loadState = CombinedLoadStates(
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
            LoadState.Loading,
            LoadStates(
                LoadState.NotLoading(true),
                LoadState.NotLoading(true),
                LoadState.NotLoading(true)
            )
        )

        // When
        scenario.onActivity {
            val fragment = it.supportFragmentManager.fragments[0] as HeadlinesFragment

            fragment.handleHeadlinesLoadStateUpdates(loadState)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.swipe_refresh))
            .check(matches(swipeRefreshIsRefreshing()))
    }

    @Test
    fun showProgressIndicator_WhileHeadlinesRefreshed() {
        // Given
        val loadState = CombinedLoadStates(
            LoadState.Loading,
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
            LoadStates(
                LoadState.NotLoading(true),
                LoadState.NotLoading(true),
                LoadState.NotLoading(true)
            )
        )

        // When
        scenario.onActivity {
            val fragment = it.supportFragmentManager.fragments[0] as HeadlinesFragment

            fragment.handleHeadlinesLoadStateUpdates(loadState)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.swipe_refresh))
            .check(matches(swipeRefreshIsRefreshing()))
    }

    @Test
    fun showErrorIndicator_WhenHeadlinesRefreshFailOnDeviceConnection() {
        // Given
        val error = NewsFeatureError.DeviceConnectionError
        val loadState = CombinedLoadStates(
            LoadState.Error(error),
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
            LoadStates(
                LoadState.NotLoading(true),
                LoadState.NotLoading(true),
                LoadState.NotLoading(true)
            )
        )

        // When
        scenario.onActivity {
            val fragment = it.supportFragmentManager.fragments[0] as HeadlinesFragment

            fragment.handleHeadlinesLoadStateUpdates(loadState)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val actualToastMessage = ShadowToast.getTextOfLatestToast()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedMessage = context.getString(R.string.error_message_device_connection)

        assertThat(actualToastMessage).isEqualTo(expectedMessage)
    }

    @Test
    fun showErrorIndicator_WhenHeadlinesRefreshFailOnRemoteServer() {
        // Given
        val error = NewsFeatureError.RemoteServerError
        val loadState = CombinedLoadStates(
            LoadState.Error(error),
            LoadState.NotLoading(true),
            LoadState.NotLoading(true),
            LoadStates(
                LoadState.NotLoading(true),
                LoadState.NotLoading(true),
                LoadState.NotLoading(true)
            )
        )

        // When
        scenario.onActivity {
            val fragment = it.supportFragmentManager.fragments[0] as HeadlinesFragment

            fragment.handleHeadlinesLoadStateUpdates(loadState)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val actualToastMessage = ShadowToast.getTextOfLatestToast()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedMessage = context.getString(R.string.error_message_remote_server)

        assertThat(actualToastMessage).isEqualTo(expectedMessage)
    }

    @Test
    fun refreshHeadlines_WhenUserPerformSwipeAction(){
        // Given
        mockkConstructor(HeadlinesAdapter::class)
        every { anyConstructed<HeadlinesAdapter>().refresh() } returns Unit

        scenario.recreate()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        onView(withId(R.id.swipe_refresh))
            .perform(swipeToRefresh())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        verify { anyConstructed<HeadlinesAdapter>().refresh() }
    }

    @Test
    fun shareHeadlineUrl_WhenUserSelectToShareIt() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()!!
        val uiHeadlines = createUiHeadlines()
        headlines.value = PagingData.from(listOf(uiHeadlines.first()))

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When
        onView(withId(R.id.shareImageButton))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        scenario.onActivity {
            assertThat(
                Shadows.shadowOf(it).peekNextStartedActivity().action
            ).isEqualTo(Intent.ACTION_CHOOSER)
            assertThat(
                Shadows.shadowOf(it).peekNextStartedActivity().hasExtra(Intent.EXTRA_INTENT)
            ).isTrue()
            assertThat(
                (Shadows.shadowOf(it).peekNextStartedActivity().extras!!.get(Intent.EXTRA_INTENT) as Intent)
                    .type
            ).isEqualTo(context.getString(R.string.mime_type_text))
            assertThat(
                (Shadows.shadowOf(it).peekNextStartedActivity().extras!!.get(Intent.EXTRA_INTENT) as Intent)
                    .getStringExtra(Intent.EXTRA_TEXT)
            ).isEqualTo(uiHeadlines.first().sourceUrl)
        }
    }

    @Test
    fun openHeadlineSourceArticleInBrowser_WhenUserSelectHeadline() {
        // Given
        Intents.init()

        val uiHeadlines = createUiHeadlines()
        headlines.value = PagingData.from(listOf(uiHeadlines.first()))

        // When
        onView(withId(R.id.card))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
        Intents.intended(IntentMatchers.hasData(Uri.parse(uiHeadlines.first().sourceUrl)))

        Intents.release()
    }

    @Test
    fun notifyUser_WhenDeviceUnableToOpenArticleInBrowser() {
        // Given
        val uiHeadlines = createUiHeadlines()
        headlines.value = PagingData.from(listOf(uiHeadlines.first()))

        mockkStatic(ContextCompat::class)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        every { ContextCompat.startActivity(any(),any(),any()) } throws ActivityNotFoundException()

        // When
        onView(withId(R.id.card))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val actualToastMessage = ShadowToast.getTextOfLatestToast()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedMessage = context.getString(R.string.message_article_unavailable)

        assertThat(actualToastMessage).isEqualTo(expectedMessage)

        // After
        clearStaticMockk(ContextCompat::class)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
}