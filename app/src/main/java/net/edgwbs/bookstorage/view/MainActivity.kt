package net.edgwbs.bookstorage.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.di.auth.AuthModel
import net.edgwbs.bookstorage.di.auth.AuthModule
import net.edgwbs.bookstorage.di.auth.DaggerAuthComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var authModel: AuthModel

    private val component = DaggerAuthComponent.builder()
        .authModule(AuthModule())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.component.inject(this)

        val fragment = if (authModel.getUser() == null) {
            LoginFragment(authModel)
        } else {
            BookListFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
    }
}
