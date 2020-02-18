package net.edgwbs.bookstorage.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import net.edgwbs.bookstorage.utils.ErrorFeedback
import net.edgwbs.bookstorage.viewModel.LoadState

abstract class BaseFragment: Fragment() {
    private lateinit var mBinding: ViewDataBinding
    val errorFeedbackHandler = MutableLiveData<ErrorFeedback>()
    val loadState = MutableLiveData<LoadState>()

    abstract fun layoutId(): Int
    abstract fun lifecycleOwner(): LifecycleOwner
    abstract fun setBool(b: Boolean)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        setObserve()
        return mBinding.root
    }
    fun getBinding() = mBinding

    private fun setObserve() {
        errorFeedbackHandler.observe(
            lifecycleOwner(),
            Observer { feedback ->
                // Log.d("tag", n.getMessage(context).toString())
                val snackbarTime = when(feedback) {
                    ErrorFeedback.ApiNotReachErrorFeedback -> {
                        Snackbar.LENGTH_INDEFINITE
                    }
                    else -> Snackbar.LENGTH_LONG
                }
                feedback?.let{
                    Snackbar.make(mBinding.root, it.getMessage(), snackbarTime).show()
                }
            }
        )

        loadState.observe(
            lifecycleOwner(),
            Observer { state ->
                when(state) {
                    LoadState.Loaded -> {
                        //binding.isLoading = true
                        setBool(false)
                    }
                    LoadState.Loading -> {
                        // binding.isLoading = false
                        setBool(true)
                    }
                    else -> {
                        // binding.isLoading = false
                        setBool(false)
                    }
                }
                Log.d("debug", "--------------"+state.name)
            }
        )
    }
}