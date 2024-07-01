package com.eighteen.eighteenandroid.presentation.common

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.fragment.findNavController

//Fragment 대신 currentBackStackEntry를 owner로 하여 ViewModel을 생성한
inline fun <reified VM : ViewModel> Fragment.viewModelsByBackStackEntry(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = viewModels<VM>(
    ownerProducer = { findNavController().currentBackStackEntry ?: this },
    extrasProducer = extrasProducer,
    factoryProducer = factoryProducer
)

//Navigation BackStack의 destinationId로 BackStackEntry를 찾아 owner로 ViewModel을 생성, 없을 경우 자기자신을 owner로 하여 생성
inline fun <reified VM : ViewModel> Fragment.viewModelsByBackStackEntryId(
    noinline destinationIdProducer: (() -> Int),
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = viewModels<VM>(
    ownerProducer = {
        try {
            findNavController().getBackStackEntry(destinationIdProducer())
        } catch (e: Throwable) {
            Log.e(this.javaClass.canonicalName, "$e : viewModelsByBackStackEntryId")
            this
        }
    },
    extrasProducer = extrasProducer,
    factoryProducer = factoryProducer
)