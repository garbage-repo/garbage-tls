/*
==--==--==--==--==--==--==--==--==--==--==--==--==--==--==--==--

garbage-tls

Copyright (C) 2023  Anonymous



This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

::--::--::--::--::--::--::--::--::--::--::--::--::--::--::--::--
*/

package app

import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit.MILLISECONDS

import javax.net.ssl.HttpsURLConnection

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView

import garbage.tls.Client

import okhttp3.OkHttpClient

const val HOST = "10.0.2.2"
const val PORT = 12012
const val TEST_URL = "https://$HOST:$PORT"
const val SOCKET_TIMEOUT = 500

val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
val CLIENT = Client("/key.pem", SOCKET_TIMEOUT)

// cat ../app/src/main/resources/key.pem | openssl x509 -inform pem -noout -text
class MainActivity: Activity() {

    init {
        HttpsURLConnection.setDefaultSSLSocketFactory(CLIENT.ssl_socket_factory)
    }

    private val ui_handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val text: TextView by lazy {
        findViewById(R.id.text) as TextView
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state);

        setContentView(R.layout.activity__main)

        for (id in arrayOf(R.id.button__connect_using_https_url_connection, R.id.button__connect_using_ok_http)) {
            (findViewById(id) as View).setOnClickListener(click_listener)
        }
    }

    private val click_listener = View.OnClickListener { view ->
        view.setEnabled(false)
        ui_handler.postDelayed({ view.setEnabled(true) }, 500L)

        val (name, job) = when (view.getId()) {
            R.id.button__connect_using_https_url_connection -> Pair("HttpsURLConnection", ::connect_using_https_url_connection)
            R.id.button__connect_using_ok_http -> Pair("OkHttp", ::connect_using_ok_http)
            else -> null
        }!!
        Thread({
            val msg = try {
                job().toString(UTF_8)
            } catch (t: Throwable) {
                t.toString()
            }
            ui_handler.post({
                val text = StringBuilder(this@MainActivity.text.getText())
                text.insert(0, "[${DATE_FORMAT.format(Date())}] $name: $msg\n")
                this@MainActivity.text.setText(text.toString().lines().take(20).joinToString("\n"))
            })
        }).start()
    }

}

fun connect_using_https_url_connection(): ByteArray {
    val connection = URL(TEST_URL).openConnection() as HttpsURLConnection
    try {
        connection.setRequestMethod("GET")
        connection.connect()
        connection.getInputStream().use {
            return it.readBytes()
        }
    } finally {
        connection.disconnect()
    }
}

fun connect_using_ok_http(): ByteArray {
    val request_builder = okhttp3.Request.Builder().url(TEST_URL).get()
    val builder = OkHttpClient.Builder()
        .connectTimeout(SOCKET_TIMEOUT.toLong(), MILLISECONDS).readTimeout(SOCKET_TIMEOUT.toLong(), MILLISECONDS)
        .sslSocketFactory(CLIENT.ssl_socket_factory, CLIENT.x509_trust_manager)
    return builder.build().newCall(request_builder.build()).execute().body!!.bytes()
}
