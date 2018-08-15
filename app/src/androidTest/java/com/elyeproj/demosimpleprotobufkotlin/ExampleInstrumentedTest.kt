package com.elyeproj.demosimpleprotobufkotlin

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
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

    private val okReplayConfig = OkReplayConfig.Builder()
            .tapeRoot(AndroidTapeRoot(
                    InstrumentationRegistry.getContext(), javaClass))
            .defaultMode(TapeMode.READ_ONLY) // or TapeMode.READ_ONLY
            .sslEnabled(true)
            .interceptor(MainActivity.okReplayInterceptor)
            .build()

    @get:Rule
    private val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @get:Rule
    val testRule = OkReplayRuleChain(okReplayConfig, activityRule).get()

    @Before
    fun setup() {
        activityRule.launchActivity(null)
    }

    @Test
    @OkReplay(tape = "protobuf output", mode = TapeMode.READ_ONLY)
    fun loadAndCheck() {
        // Context of the app under test.
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.txt_main)).check(
                ViewAssertions.matches(ViewMatchers.withText(containsString("123456789"))))
    }
}
