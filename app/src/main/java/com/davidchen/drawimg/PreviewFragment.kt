package com.davidchen.drawimg

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.davidchen.drawimg.databinding.FragmentPreviewBinding
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment


/**
 * A simple [Fragment] subclass.
 * Use the [PreviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PreviewFragment : Fragment() {

    lateinit var v: FragmentPreviewBinding
    lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.davidchen.drawimg"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_preview, container, false)
        v = FragmentPreviewBinding.bind(view)

        v.ivImage.setOnClickListener {
            PhotoPickerFragment.newInstance(
                multiple = false,
                allowCamera = false,
                theme = R.style.ChiliPhotoPicker_Dark
            ).show(parentFragmentManager, "photo")
        }

        v.btEditImg.setOnClickListener {
            val editFragment = EditFragment.newInstance(photoUri)
            parentFragmentManager
                .beginTransaction()
                .addToBackStack("edit")
                .replace(R.id.root, editFragment)
                .commit()
        }

        return v.root
    }

    fun setImage(photo: Uri) {
        photoUri = photo
        Glide.with(context)
            .load(photo)
            .asBitmap()
            .fitCenter()
            .into(v.ivImage)
        v.btEditImg.visibility = View.VISIBLE
        v.ivAdd.visibility = View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PreviewFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}