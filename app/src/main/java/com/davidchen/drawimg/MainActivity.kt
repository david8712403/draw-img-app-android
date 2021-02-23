package com.davidchen.drawimg

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davidchen.drawimg.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import lv.chi.photopicker.PhotoPickerFragment
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), PhotoPickerFragment.Callback {

    lateinit var v: ActivityMainBinding
    lateinit var preview: PreviewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        setContentView(v.root)

        Timber.plant(Timber.DebugTree())

        preview = PreviewFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.root, preview)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                val resultUri = UCrop.getOutput(data)
                resultUri?.let { preview.setImage(it) }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
            Timber.e("UCrop error: ${cropError?.message}")
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        Timber.d(photos[0].lastPathSegment)
        val uri = photos[photos.size - 1]
        val now = Calendar.getInstance().time.toString()
        val fileName = now + uri.lastPathSegment
        val destination = File(cacheDir, fileName)
        val options: UCrop.Options = UCrop.Options()
        options.setFreeStyleCropEnabled(true)

        Timber.d("destination: $destination")
        UCrop.of(uri, Uri.fromFile(destination))
            .withOptions(options)
            .start(this)
    }
}