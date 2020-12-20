package com.itaycohen.musicplayertask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.database.LocalDatabase
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import com.itaycohen.musicplayertask.databinding.FragmentMediaListBinding

class MediaListFragment : Fragment() {

    private var _binding: FragmentMediaListBinding? = null
    private val audioViewModel: AudioViewModel by navGraphViewModels(R.id.nav_graph) {
        AudioViewModel.Factory(
            MediaItemsRepository.getInstance(
                requireContext().applicationContext,
                LocalDatabase.getInstance(requireContext().applicationContext).audioDao()
            )
        )
    }
    private val binding: FragmentMediaListBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTopAppBar()
        with(binding.mediaRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            val itemTouchHelper = ItemTouchHelper(TabsItemTouchHelperCallback(audioViewModel.itemTouchHelperCallback))
            itemTouchHelper.attachToRecyclerView(this)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context, R.drawable.media_items_divider)!!)
            })
            adapter = AudioItemsAdapter(audioViewModel, viewLifecycleOwner, itemTouchHelper)
        }
        binding.addAudioBtn.setOnClickListener {
            val frag = childFragmentManager.findFragmentByTag(BottomAddAudioDialogFragment::class.simpleName)
                    as? BottomAddAudioDialogFragment ?: BottomAddAudioDialogFragment()
            frag.show(
                childFragmentManager,
                BottomAddAudioDialogFragment::class.simpleName
            )
        }
    }

    private fun initTopAppBar() {
        setHasOptionsMenu(true)
        binding.topAppBar.setupWithNavController(findNavController())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}