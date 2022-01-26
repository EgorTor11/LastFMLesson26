package com.example.lastfmclientlesson26

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import kotlin.Throws
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.lastfmlesson26.LastFMActivity
import com.example.lastfmlesson26.R
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    var edName: EditText? = null
    var edPassword: EditText? = null
    var test: String? = null
    var str = ""
    var isSuccess = true
    var tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edName = findViewById(R.id.edName)
        edPassword = findViewById(R.id.edPassword)
        tv = findViewById(R.id.tv)
        val api_key = "793b148b583e5bf178315829ba53b378" // your api key
        val api_sig = "2c42cfa7cba03f711debaf4827351e04" // your api sig  на самом деле это секрет
        tv?.setOnClickListener(View.OnClickListener {
            val username =
                edName?.getText().toString() //"geberlastfm"; // username you want to log in
            val password = edPassword?.getText().toString() // "?_85n\"fHpsKTBWJ"; // user password
            val apiSignature =
                "api_key" + api_key + "methodauth.getMobileSessionpassword" + password + "username" + username + api_sig
            val hexString = StringBuilder()
            Thread {
                try {
                    val md5Encrypter = MessageDigest.getInstance("MD5")
                    // Create MD5 Hash
                    val digest = MessageDigest
                        .getInstance("MD5")
                    digest.update(apiSignature.toByteArray(charset("UTF-8")))
                    val messageDigest = digest.digest()

                    // Create Hex String
                    for (aMessageDigest in messageDigest) {
                        var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                        while (h.length < 2) h = "0$h"
                        hexString.append(h)
                    }
                    val urlParameter =
                        "method=auth.getMobileSession&api_key=$api_key&password=$password&username=$username&api_sig=$hexString"
                    val request = Request.Builder()
                        .url("https://ws.audioscrobbler.com/2.0/?$urlParameter")
                        .post(RequestBody.create(null, ByteArray(0))).build()
                    val client = OkHttpClient.Builder().build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            response.body().use { responseBody ->
                               var test = responseBody!!.string()
                                Log.d("MyLog",
                                    test) // your .xml with the session id, see https://www.last.fm/api/show/auth.getSession
                                //edSessionKey.setText(test);
                                if (test.contains("ok")) {
                                    val intent =
                                        Intent(this@MainActivity, LastFMActivity::class.java)
                                    startActivity(intent)
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity,
                                            "Усе зашибок",
                                            Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    runOnUiThread {
                                        Toast.makeText(this@MainActivity,
                                            "неверное имя или пароль !!!",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }
                                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            }
                        }
                    })
                } catch (ex: Exception) {
                    println(ex.toString())
                }
            }.start()
        })
    }
}