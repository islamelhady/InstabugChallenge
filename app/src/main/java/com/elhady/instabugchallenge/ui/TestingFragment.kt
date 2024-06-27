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


    private fun onAddQueryParam() {
        binding.addQueryItem.setOnClickListener {
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.queryHost, false)

            view.tag = queryViewsList.size.toString()
            Log.i("onAddHeader: Counter", queryViewsList.size.toString())

            view.findViewById<ImageView>(R.id.delete_item).setOnClickListener {
                // try to remove the header
                if (queryViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("onAddHeader: Tag", viewTag.toString())

                    val deletedView = queryViewsList.find {
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())
                    //Toast.makeText(this, deletedView?.tag.toString(), Toast.LENGTH_SHORT).show()

                    binding.queryHost.removeView(deletedView)
                    queryViewsList.remove(deletedView)

                }
            }
            binding.queryHost.addView(view)
            queryViewsList.add(view)
        }
    }

    private fun onAddHeader() {
        binding.addHeaderItem.setOnClickListener {
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.headerHost, false)

            view.tag = headersViewsList.size.toString()
            Log.i("onAddHeader: Counter", headersViewsList.size.toString())

            view.findViewById<ImageView>(R.id.delete_item).setOnClickListener {
                // try to remove the header
                if (headersViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("onAddHeader: Tag", viewTag.toString())

                    val deletedView = headersViewsList.find {
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())
                    //Toast.makeText(this, deletedView?.tag.toString(), Toast.LENGTH_SHORT).show()

                    binding.headerHost.removeView(deletedView)
                    headersViewsList.remove(deletedView)

                }
            }
            binding.headerHost.addView(view)
            headersViewsList.add(view)
        }
    }
}