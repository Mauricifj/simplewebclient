package com.mauricifj.simplewebclientapp

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.mauricifj.simplelogger.SimpleLogger
import com.mauricifj.simplewebclientapp.R.layout.activity_main
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class MainActivity : Activity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        job = Job()

        button.setOnClickListener {
            launch {
                getRandomUser()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun getRandomUser() {
        setLoader(true)

        try
        {
            val request = GlobalScope.async {
                val service = RandomUserClient()
                service.getRandomUser()
            }

            val result = request.await()
            val response = result.result

            if (response != null)
                setUser(response)
            else {
                SimpleLogger.error("getRandomUser: response is null")
                showErrorAlert()
            }
        }
        catch (t: Throwable)
        {
            SimpleLogger.error("getRandomUser: Error while getting a random user", t)
            showErrorAlert()
        }
    }

    private fun setLoader(enabled: Boolean) {
        runOnUiThread {
            if (enabled) {
                loader.visibility = View.VISIBLE
                content.visibility = View.GONE
                button.isEnabled = false
            } else {
                loader.visibility = View.GONE
                content.visibility = View.VISIBLE
                button.isEnabled = true
            }
        }
    }

    private fun setUser(user: User?) {
        if (user != null) {
            first_name.text = user.name.first
            last_name.text = user.name.last
            age.text = user.dob.age.toString()
            phone.text = user.phone
            email.text = user.email
            setImage(user.picture.large)
        }
    }

    private fun setImage(url: String) {
        val bitmap = getImage(url)
        setImageFromBitmap(bitmap)
        setLoader(false)
    }

    private fun getImage(url: String): Bitmap {
        return BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
    }

    private fun setImageFromBitmap(bitmap: Bitmap) {
        runOnUiThread {
            picture.setImageBitmap(bitmap)
        }
    }

    private fun showErrorAlert() {
        runOnUiThread {
            val alert = AlertDialog.Builder(this).create()
            alert.setTitle(getString(R.string.alert_error_title))
            alert.setMessage(getString(R.string.alert_error_message))
            alert.setButton(BUTTON_POSITIVE, getString(R.string.alert_error_button)) { _, _ -> }
            alert.show()
            setLoader(false)
        }
    }
}