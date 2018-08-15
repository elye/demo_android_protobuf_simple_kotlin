package com.elyeproj.demosimpleprotobufkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okreplay.OkReplayInterceptor
import tutorial.Dataformat.Person

class MainActivity : AppCompatActivity() {


    companion object {
        val okReplayInterceptor = OkReplayInterceptor()
        private val okHttpClient = OkHttpClient.Builder().addInterceptor(okReplayInterceptor).build()
    }

    private val observable = Observable.just("http://elyeproject.x10host.com/experiment/protobuf")
            .map{
                val request = Request.Builder().url(it).build()
                val call = okHttpClient.newCall(request)
                val response = call.execute()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        return@map Person.parseFrom(responseBody.byteStream())
                    }
                }
                return@map null

            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observable.subscribe{
            showResult(it as Person)
        }
    }

    private fun showResult(result: Person) {
        txt_main.text = result.toString()
    }
}
