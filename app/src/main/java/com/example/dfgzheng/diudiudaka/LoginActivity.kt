package com.example.dfgzheng.diudiudaka

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.squareup.okhttp.*
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() , View.OnClickListener{

    private var userNameInput: EditText by Delegates.notNull<EditText>()

    private var passwordInput: EditText by Delegates.notNull<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userNameInput = findViewById(R.id.username) as EditText
        passwordInput = findViewById(R.id.password) as EditText

        findViewById(R.id.sign_in_button).setOnClickListener(this)

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val userinfoString = defaultSharedPreferences.getString("userinfo", "")
        Log.d("LoginActivity", userinfoString)
        val saveTime = defaultSharedPreferences.getLong("saveTime", 0)
        if (!TextUtils.isEmpty(userinfoString)) {
            val jsonObject = JSONObject(userinfoString)
            val expriesIn = jsonObject.getLong("expiresIn")
            if (saveTime + expriesIn >= System.currentTimeMillis()) {
                startActivity(Intent(this, PunchCardActivity::class.java))
            }
        }
    }

    override fun onClick(v: View?) {
        val okHttpClient = OkHttpClient()
        val params = "username=" + userNameInput.text.toString() + "&password=" + passwordInput.text.toString() + "&devicetype=android&clientversion=1.5.5"
        val requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), params)
        val build = Request.Builder()
                .url("https://im.bangcommunity.com/APPLogin/login")
                .post(requestBody)
                .build()
        val newCall = okHttpClient.newCall(build)
        newCall.enqueue(object :Callback {

            override fun onFailure(request: Request?, e: IOException?) {
                runOnUiThread(Runnable {
                    Toast.makeText(baseContext, "sign in fail" , Toast.LENGTH_SHORT).show()
                })

            }

            override fun onResponse(response: Response?) {
                val result = response?.body()?.string()
                val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
                val edit = defaultSharedPreferences.edit()
                edit.putString("userinfo", result)
                edit.putLong("saveTime", System.currentTimeMillis())
                edit.commit()
                runOnUiThread(Runnable {
                    startActivity(Intent(this@LoginActivity, PunchCardActivity::class.java))
                    Toast.makeText(baseContext, "sign in success" , Toast.LENGTH_SHORT).show()
                })

            }

        })
    }
}

