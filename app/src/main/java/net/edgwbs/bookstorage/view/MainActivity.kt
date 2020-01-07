package net.edgwbs.bookstorage.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.edgwbs.bookstorage.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = BookListFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
    }
}
