package net.edgwbs.bookstorage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentLoginBinding
import net.edgwbs.bookstorage.di.auth.AuthCallback
import net.edgwbs.bookstorage.di.auth.AuthModel
import net.edgwbs.bookstorage.utils.ErrorFeedback
import net.edgwbs.bookstorage.viewModel.LoadState
import javax.inject.Inject

class LoginFragment(private val authModel: AuthModel): BaseFragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun layoutId() = R.layout.fragment_login
    override fun lifecycleOwner() = viewLifecycleOwner
    override fun setBool(b: Boolean) {
        binding.isLoading = b
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = super.getBinding() as FragmentLoginBinding

        binding.signInButton.setOnClickListener {
            if (binding.email != null && binding.password != null) {
                loadState.postValue(LoadState.Loading)
                authModel.signInWithEmail(
                    binding.email!!,
                    binding.password!!,
                    signInCallback())
            }
        }
        return binding.root
    }

    private fun signInCallback(): AuthCallback {
        return object: AuthCallback {
            override fun onSuccess() {
                val fragment = BookListFragment()
                val transaction = fragmentManager?.beginTransaction()
                transaction?.let {
                    it.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    it.addToBackStack(null)
                    it.replace(R.id.fragment_container, fragment).commit()
                }
            }
            override fun onFail() {
                errorFeedbackHandler.postValue(ErrorFeedback.AuthError)
            }
            override fun also() {
                loadState.postValue(LoadState.Loaded)
            }
        }
    }



}