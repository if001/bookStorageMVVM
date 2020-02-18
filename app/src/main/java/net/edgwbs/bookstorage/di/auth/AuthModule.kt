package net.edgwbs.bookstorage.di.auth

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class AuthModule {
    @Provides
    fun provideAuthModule(): AuthModel {
        return AuthModelImpl()
    }
}


interface AuthModel {
    fun signInWithEmail(email: String, password: String, authCallback: AuthCallback)
    fun signInWithGoogle()
    fun signOut()
    fun createAccount()
    fun getUser(): User?
}

interface AuthCallback {
    fun onSuccess()
    fun onFail()
    fun also()
}

class AuthModelImplMock: AuthModel {
    override fun signInWithEmail(email: String, password: String, authCallback: AuthCallback) {
        if (email == "hoge" && password == "hoge"){
            authCallback.onSuccess()
        } else {
            authCallback.onFail()
        }
        authCallback.also()
    }

    override fun signInWithGoogle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signOut() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createAccount() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUser(): User? {
        return null
    }
}

class User(token: String)


class AuthModelImpl: AuthModel {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun signInWithEmail(email: String, password: String, authCallback: AuthCallback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    authCallback.onSuccess()
                } else {
                    authCallback.onFail()
                }
            }.also {
                authCallback.also()
            }
    }

    override fun signInWithGoogle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signOut() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createAccount() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUser(): User? {
         return auth.currentUser?.let {
             User(it.uid)
         }
    }

}