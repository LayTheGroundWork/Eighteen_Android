package com.eighteen.eighteenandroid.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<T : ViewBinding>(private val bindingFactory: (LayoutInflater) -> T) :
    BottomSheetDialogFragment() {

    private var _binding: T? = null
    val binding get() = _binding!!

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingFactory(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    abstract fun initView()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected inline fun bind(block: T.() -> Unit) {
        binding.apply(block)
    }

    fun requestWithRequiredLogin(request: () -> Unit) =
        (activity as? MainActivity)?.requestWithRequiredLogin(request)
}