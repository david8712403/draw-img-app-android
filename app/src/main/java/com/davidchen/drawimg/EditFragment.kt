package com.davidchen.drawimg

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.davidchen.drawimg.databinding.FragmentEditBinding
import timber.log.Timber
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {

    private var imageUri: String? = null

    lateinit var v: FragmentEditBinding
    lateinit var paintView: PaintView
    lateinit var params: ViewGroup.LayoutParams
    private var curColor: Int = 0
    private val circleViews: ArrayList<MyCircleView> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getString(IMG_URI)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = FragmentEditBinding.bind(inflater.inflate(R.layout.fragment_edit, container, false))

        initCanvas()
        generateCircleView()

        v.btBack.setOnClickListener {
            paintView.back()
        }

        v.btClear.setOnClickListener {
            paintView.clear()
        }

        v.btSave.setOnClickListener {
            val bitmap = getResultBitmap()
            val uri = saveImage(bitmap)
            val b = Bundle()
            b.putString(IMG_URI, uri.toString())
            parentFragmentManager.setFragmentResult(IMG_URI, b)
            parentFragmentManager.popBackStack()
        }

        v.sbWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                paintView.strokeWidth = seekBar!!.progress.plus(15)
            }

        })

        return v.root
    }

    private fun saveImage(bitmap: Bitmap) : Uri {
        // save image
        // reference: https://stackoverflow.com/questions/56904485/how-to-save-an-image-in-android-q-using-mediastore/56990305
        val now = Calendar.getInstance()
        val relativeLocation = requireContext().getString(R.string.app_name)
        val name = "${relativeLocation}_${now[Calendar.YEAR]}_${now[Calendar.MONTH]}_${now[Calendar.DATE]}" +
                "_${now[Calendar.HOUR]}_${now[Calendar.MINUTE]}_${now[Calendar.SECOND]}"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            // store in /{app_name}
//            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
//        } // or picture store in default media folder -> /Pictures

        val resolver = requireContext().contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null

        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                throw IOException("Failed to save bitmap.")
            }
            return uri
        } catch (e: Exception) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
                uri = null
            }
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            throw e
        } finally {
            stream?.close()
        }
    }

    private fun getResultBitmap() : Bitmap {
        val resultBitmap = Bitmap.createBitmap(
                params.width,
                params.height,
                Bitmap.Config.ARGB_8888
        )
        val originBitmap = (v.ivEdit.drawable as BitmapDrawable).bitmap
        val c = Canvas(resultBitmap)

        // draw origin first
        c.drawBitmap(originBitmap, 0f, 0f, null)
        // draw paint view
        paintView.draw(c)
        return resultBitmap
    }

    private fun initCanvas() {
        // load image
        Glide.with(context)
                .load(imageUri)
                .asBitmap()
                .fitCenter()
                .into(v.ivEdit)

        // add image layout listener
        v.ivEdit.addOnLayoutChangeListener { iv, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Timber.d("iv: $left, $top, $right, $bottom")
            params = ViewGroup.LayoutParams(right - left, bottom - top)
            Timber.d("params: w:${right - left}, h:${bottom - top}")

            // wait ivEdit generate finish
            if(left != right && top != bottom) {
                paintView.layout(left, top, right, bottom)
                v.canvasLayout.removeView(paintView)
                v.canvasLayout.addView(paintView)
            }
        }

        paintView = PaintView(requireContext())
        // track paint view layout
        paintView.addOnLayoutChangeListener { pv, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Timber.d("pv: $left, $top, $right, $bottom")
        }
    }

    private fun generateCircleView() {
        val colors = requireContext().resources.getIntArray(R.array.draw_array)
        for (c in colors) {
            val cv = MyCircleView(
                    requireContext(),
                    null,
                    R.style.Widget_Theme_DrawImg_MyView
            )
            cv.circleColor = c
            cv.setOnClickListener {
                for (circleView in circleViews) {
                    circleView.isCheck = false
                }
                cv.isCheck = true
                curColor = cv.circleColor
                paintView.color = curColor
            }
            circleViews.add(cv)
            v.scrollView.addView(cv, 120, 120)
        }
        circleViews[0].isCheck = true
        curColor = circleViews[0].circleColor
        paintView.apply {
            color = curColor
            strokeWidth = v.sbWidth.progress.plus(15)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param imageUri image uri.
         * @return A new instance of fragment EditFragment.
         */

        const val IMG_URI = "image_uri"

        @JvmStatic
        fun newInstance(imageUri: Uri) =
                EditFragment().apply {
                    arguments = Bundle().apply {
                        putString(IMG_URI, imageUri.toString())
                    }
                }
    }
}