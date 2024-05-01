package com.eighteen.eighteenandroid.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailBinding.inflate(inflater, container, false)
        initView()
        return binding?.root
    }

    private fun initView() = with(binding) {
        binding.btnGo.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_fragmentDetail_to_fragmentMain)
        }
    }
}