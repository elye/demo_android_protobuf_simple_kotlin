package com.elyeproj.demosimpleprotobufkotlin

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.elyeproj.demosimpleprotobufkotlin.test.R.id.withText
import okreplay.AndroidTapeRoot
import okreplay.OkReplay
import okreplay.OkReplayConfig
import okreplay.OkReplayRuleChain
import okreplay.TapeMode
import org.hamcrest.core.StringContains.containsString

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    companion object {
        @ClassRule
        @JvmField
        val grantExternalStoragePermissionRule: GrantPermissionRule =
                GrantPermissionRule.grant(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private val app get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MainApp


    private val okReplayConfig = OkReplayConfig.Builder()
            .tapeRoot(AndroidTapeRoot(
                    InstrumentationRegistry.getContext(), javaClass))
            .defaultMode(TapeMode.READ_WRITE) // or TapeMode.READ_ONLY
            .sslEnabled(true)
            .interceptor(MainActivity.okReplayInterceptor)
            .build()

    @get:Rule
    private val activityRule = ActivityTestRule(MainActivity::class.java, false, false)
    private val fetchingIdlingResource = FetchingIdlingResource()

    @get:Rule
    val testRule = OkReplayRuleChain(okReplayConfig, activityRule).get()

    @Before
    fun setup() {
        app.fetcherListener = fetchingIdlingResource
        activityRule.launchActivity(null)
        IdlingRegistry.getInstance().register(fetchingIdlingResource)
    }

    @Test
    @OkReplay(tape = "protobuf output", mode = TapeMode.READ_WRITE)
    fun loadAndCheck() {
        // Context of the app under test.
        Espresso.onView(ViewMatchers.withId(R.id.txt_main)).check(
                ViewAssertions.matches(ViewMatchers.withText(containsString("123456789"))))
    }
}
