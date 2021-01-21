package com.itaycohen.musicplayertask.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.database.LocalDatabase
import com.itaycohen.musicplayertask.data.models.FormEntryState
import com.itaycohen.musicplayertask.data.models.FormState
import com.itaycohen.musicplayertask.data.models.QueryState
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import com.itaycohen.musicplayertask.databinding.FragmentAddAudioItemBinding


class BottomAddAudioDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddAudioItemBinding? = null
    private val binding: FragmentAddAudioItemBinding
        get() = _binding!!

    private val addAudioViewModel: AddAudioViewModel by navGraphViewModels(R.id.nav_graph) {
        AddAudioViewModel.Factory(
            requireContext().applicationContext,
            MediaItemsRepository.getInstance(
                requireContext().applicationContext,
                LocalDatabase.getInstance(requireContext().applicationContext).audioDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAudioItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addAudioViewModel.formStateLiveData.observe(viewLifecycleOwner, formStateObserver)
        binding.audioImageUrlEditText.setOnEditorActionListener(editorImeAction)
    }

    private val editorImeAction = TextView.OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            addAudioViewModel.saveAudioIfValid(createFormMap())
            true
        } else
            false
    }

    private fun createFormMap() = with(binding) {
        mapOf(
            audioTitleEditText.id to audioTitleEditText.text.toString(),
            audioUrlEditText.id to audioUrlEditText.text.toString(),
            audioImageUrlEditText.id to audioImageUrlEditText.text.toString(),
        )
    }

    private val formStateObserver = Observer<FormState> { formState ->
        when (formState.saveState) {
            is QueryState.Failure -> {
                view?.let { view ->
                    hideKeyboard()
                    formState.saveState.errMsg?.let {Toast.makeText(view.context, it, Toast.LENGTH_LONG).show()}
                }
            }
            is QueryState.Success ->
                dismiss()
        }
        val statesMap = formState.entriesStateMap
        _binding ?: return@Observer
        statesMap.forEach { (viewId, state) ->
            with(binding) {
                val textField = when (viewId) {
                    audioTitleEditText.id -> audioTitleLayuot
                    audioUrlEditText.id -> audioUrlLayuot
                    audioImageUrlEditText.id -> audioImageUrlLayuot
                    else -> null
                }
                textField?.let { updateTextFieldState(it, state) }
            }
        }
    }

    private fun hideKeyboard() {
        view?.let {
            val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun updateTextFieldState(textField: TextInputLayout, state: FormEntryState) = when (state){
        is FormEntryState.Idle,
        is FormEntryState.Valid -> textField.error = null
        is FormEntryState.Invalid -> textField.error = state.reason
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}