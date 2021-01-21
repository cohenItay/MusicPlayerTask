package com.itaycohen.musicplayertask.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.models.ItemImageInfo
import com.itaycohen.musicplayertask.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        with(binding) {
            topAppBar.setupWithNavController(findNavController())
            when (val imageInfo = args.audioItem.imageInfoData) {
                is ItemImageInfo.Local -> {
                    topImage.setImageResource(imageInfo.drawableRes)
                }
                is ItemImageInfo.Remote -> {
                    Glide.with(topImage)
                        .load(imageInfo.imageUrl)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .into(topImage)
                }
            }
            audioTitle.text = args.audioItem.title
            audioSrc.text = args.audioItem.audioUrl
            audioSrc.movementMethod = ScrollingMovementMethod()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}