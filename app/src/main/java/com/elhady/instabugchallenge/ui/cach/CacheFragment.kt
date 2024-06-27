package com.elhady.instabugchallenge.ui.cach

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elhady.instabugchallenge.databinding.FragmentCacheBinding

class CacheFragment : Fragment() {

    private val viewModel: CachViewModel by viewModels()
    private lateinit var binding: FragmentCacheBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCacheBinding.inflate(inflater, container, false)
        return binding.root

    }
}