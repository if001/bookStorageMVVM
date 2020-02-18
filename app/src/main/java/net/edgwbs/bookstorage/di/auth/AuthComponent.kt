package net.edgwbs.bookstorage.di.auth

import dagger.Component
import net.edgwbs.bookstorage.view.LoginFragment
import net.edgwbs.bookstorage.view.MainActivity

@Component(modules = [AuthModule::class])
interface AuthComponent {
    fun inject(activity: MainActivity)
}