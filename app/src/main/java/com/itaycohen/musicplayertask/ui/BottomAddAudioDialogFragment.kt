package com.itaycohen.musicplayertask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.itaycohen.musicplayertask.databinding.FragmentAddAudioItemBinding

class BottomAddAudioDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddAudioItemBinding? = null
    private val binding: FragmentAddAudioItemBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAudioItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}