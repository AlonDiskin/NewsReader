package com.diskin.alon.newsreader.home.presentation

import android.os.Looper
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@MediumTest
@Config(instrumentedPackages = ["androidx.loader.content"],application = HiltTestApplication::class)
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Test subject
    private lateinit var scenario: ActivityScenario<MainActivity>

    // Collaborators
    @BindValue
    @JvmField
    val navHelper: AppNavHelper = mockk()

    @Before
    fun setUp() {
        // Stub collaborators
        every { navHelper.getAppNavGraph() } returns R.navigation.fake_nav_graph

        // Launch activity under test
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun showAppNameInToolbar() {
        // Given

        // Then
        scenario.onActivity {
            val toolbar = it.findViewById<Toolbar>(R.id.toolbar)
            val expectedTitle = it.getString(R.string.app_name)
            val actualTitle = toolbar.title

            assertThat(actualTitle).isEqualTo(expectedTitle)
        }
    }

    @Test
    fun showAppNavGraphHomeDestScreen_WhenCreated() {
        // Given

        // Then
        scenario.onActivity {
            val navFrag = it.supportFragmentManager.fragments[0] as NavHostFragment

            assertThat(navFrag.navController.currentDestination?.id)
                .isEqualTo(R.id.headlinesFragment)
        }
    }

    @Test
    fun navToHeadlinesScreen_WhenHeadlinesDestSelected() {
        // Given

        // When
        onView(withId(R.id.headlinesFragment))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        scenario.onActivity {
            val navFrag = it.supportFragmentManager.fragments[0] as NavHostFragment

            assertThat(navFrag.navController.currentDestination?.id)
                .isEqualTo(R.id.headlinesFragment)
        }
    }
}