package com.remindersample

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity<DB : ViewDataBinding, VM : AndroidViewModel> : AppCompatActivity() {
    lateinit var binding: DB
    lateinit var viewModel: VM

    abstract fun getLayoutId(): Int
    abstract fun getViewModelClass(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutId())

        viewModel = ViewModelProviders.of(this).get(getViewModelClass())
        binding.setLifecycleOwner(this)
        binding.setVariable(BR.viewModel, viewModel)
    }


}