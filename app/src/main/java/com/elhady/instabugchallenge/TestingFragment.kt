package com.elhady.instabugchallenge

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.elhady.instabugchallenge.databinding.FragmentTestingBinding

class TestingFragment : Fragment() {

    private val viewModel: TestingViewModel by viewModels()
    lateinit var binding: FragmentTestingBinding
    private lateinit var headersViewsList: MutableList<View>


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
        onAddHeader()
    }

    private fun onAddHeader() {
        binding.addItem.setOnClickListener {
            // inflate header item from layouts
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.headerHost, false)

            Log.i("Click", "${headersViewsList.size}")

             // on remove header clicked
            view.findViewById<ImageView>(R.id.delete_header).setOnClickListener {
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
}