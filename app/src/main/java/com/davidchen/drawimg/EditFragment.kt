package com.davidchen.drawimg

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.davidchen.drawimg.databinding.FragmentEditBinding

private const val IMG_URI = "image_uri"

/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {

    private var imageUri: String? = null

    lateinit var v: FragmentEditBinding
    lateinit var paintView: PaintView
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
            TODO("Save image")
        }

        v.sbWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                paintView.strokeWidth = seekBar!!.progress.plus(15)
            }

        })

        return v.root
    }

    private fun initCanvas() {
        Glide.with(context)
                .load(imageUri)
                .asBitmap()
                .fitCenter()
                .into(v.ivEdit)

        paintView = PaintView(requireContext())
        paintView.layoutParams = v.ivEdit.layoutParams
        v.canvasLayout.addView(paintView, v.ivEdit.layoutParams)
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(imageUri: Uri) =
                EditFragment().apply {
                    arguments = Bundle().apply {
                        putString(IMG_URI, imageUri.toString())
                    }
                }
    }
}