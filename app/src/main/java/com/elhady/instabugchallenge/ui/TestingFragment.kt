package com.elhady.instabugchallenge.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.elhady.instabugchallenge.R
import com.elhady.instabugchallenge.databinding.FragmentTestingBinding

class TestingFragment : Fragment() {

    private val viewModel: TestingViewModel by viewModels()
    lateinit var binding: FragmentTestingBinding
    private lateinit var headersViewsList: MutableList<View>
    private lateinit var queryViewsList: MutableList<View>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headersViewsList = mutableListOf()
        queryViewsList = mutableListOf()
        onAddHeader()
        onAddQueryParam()
    }

    private fun onAddHeader() {
        binding.addHeaderItem.setOnClickListener {
            // inflate header item from layouts
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.headerHost, false)

            Log.i("Click", "${headersViewsList.size}")

             // on remove header clicked
            view.findViewById<ImageView>(R.id.delete_item).setOnClickListener {
                if (headersViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("AddHeader", viewTag.toString())

                    val deletedView = headersViewsList.find { it ->
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())

                    // remove header view from list and view hierarchy
                    binding.headerHost.removeView(deletedView)
                    headersViewsList.remove(deletedView)
                }
            }
            // add header view to list
            binding.headerHost.addView(view)
            headersViewsList.add(view)
        }
    }

    private fun onAddQueryParam(){
        binding.addQueryItem.setOnClickListener {
            // inflate header item from layouts
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.queryHost, false)

            Log.i("Click", "${queryViewsList.size}")

            // on remove header clicked
            view.findViewById<ImageView>(R.id.delete_item).setOnClickListener {
                if (queryViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("AddParam", viewTag.toString())

                    val deletedView = queryViewsList.find { it ->
                        Log.i("onAddParam", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddParam", deletedView?.tag.toString())

                    // remove header view from list and view hierarchy
                    binding.queryHost.removeView(deletedView)
                    queryViewsList.remove(deletedView)
                }
            }
            // add query view to list
            binding.queryHost.addView(view)
            queryViewsList.add(view)
        }
    }
}