package net.edgwbs.bookstorage.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.nav_item.view.*
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookListMainBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.utils.FragmentConstBookID
import net.edgwbs.bookstorage.viewModel.BookListViewModel

class BookListFragment : Fragment() {
    private val viewModel:BookListViewModel by lazy {
        ViewModelProviders.of(this).get(BookListViewModel::class.java)
    }
    private lateinit var binding: FragmentBookListMainBinding
    private lateinit var adapter: BookListAdapter

    private val bookClickCallback = object: BookClickCallback {
        override fun onClick(book: Book) {
            val bundle = Bundle()
            bundle.putString(FragmentConstBookID, book.id.toString())
            val fragment = BookDetailFragment()
            fragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()
            transaction?.let {
                it.addToBackStack(null)
                it.replace(R.id.fragment_container, fragment).commit()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_list_main, container, false)

        adapter = BookListAdapter(bookClickCallback)
        val rv = binding.root.findViewById<RecyclerView>(R.id.book_list)
        rv.adapter = adapter

        binding.isLoading = true

        initTab(binding.root)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.activity?.let {
            initMenu(it, binding.root)
        }
        observeViewModel(viewModel)
    }

    private fun observeViewModel(viewModel: BookListViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
        viewModel.getLiveData().observe(
            viewLifecycleOwner,
            Observer { books ->
                Log.d("tag", books.toString())
                if (books != null) {
                    adapter.setBookList(books)
                    binding.isLoading = false
                }
            })
    }

    private fun initMenu(activity: FragmentActivity, view: View) {

        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        // setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val burgerItems = mutableListOf<BurgerMenu>()
        burgerItems.add(BurgerMenu("ログアウト"))
        val navView = view.findViewById<ListView>(R.id.nav_menu_items)
        navView.adapter = MenuItemAdapter(view.context, burgerItems)
    }

    private fun initTab(view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        tabLayout.addTab(tabLayout.newTab().setText("ALL"))
        tabLayout.addTab(tabLayout.newTab().setText("未読"))
        tabLayout.addTab(tabLayout.newTab().setText("読中"))
        tabLayout.addTab(tabLayout.newTab().setText("読了"))
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                when (tab.text) {
//                    "未読" -> {
//                        state = "not_read"
//                    }
//                    "読中" -> {
//                        state = "reading"
//                    }
//                    "読了" -> {
//                        state = "read"
//                    }
//                }
//            }
//        })


    }
}




class BurgerMenu(private val title: String){
    fun getTitle(): String {
        return title
    }
}
class MenuItemAdapter(private val context: Context, private val items: List<BurgerMenu>): BaseAdapter() {
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.count()
    }

    override fun getItem(position: Int): BurgerMenu {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = layoutInflater.inflate(R.layout.nav_item, parent, false)
        textView.nav_item.text = items[position].getTitle()
        return textView
    }
}
