package net.edgwbs.bookstorage.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import info.androidhive.fontawesome.FontDrawable
import kotlinx.android.synthetic.main.nav_item.view.*
import kotlinx.coroutines.Job

import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookListMainBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookService
import net.edgwbs.bookstorage.utils.FragmentConstBookID
import net.edgwbs.bookstorage.utils.OnLoadMoreListener
import net.edgwbs.bookstorage.utils.RecyclerViewLoadMoreScroll
import net.edgwbs.bookstorage.viewModel.BookListViewModel
import net.edgwbs.bookstorage.viewModel.RequestCallback

class BookListFragment : Fragment() {
    private val viewModel:BookListViewModel by lazy {
        ViewModelProviders.of(this).get(BookListViewModel::class.java)
    }
    private lateinit var binding: FragmentBookListMainBinding
    private lateinit var adapter: BookListAdapter
    private lateinit var loadBookJob: Job
    private var page: Int = 1
    private var state: String? = null

    private val bookClickCallback = object: BookClickCallback {
        override fun onClick(book: Book) {
            val bundle = Bundle()
            bundle.putString(FragmentConstBookID, book.id.toString())
            val fragment = BookDetailFragment()
            fragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()
            transaction?.let {
                it.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                it.addToBackStack(null)
                it.replace(R.id.fragment_container, fragment).commit()
            }
        }
    }

    private val loadBookListCallback = object : RequestCallback {
        override fun onRequestSuccess() {
            // Snackbar.make(binding.root , "request success", Snackbar.LENGTH_LONG).show()
        }

        override fun onRequestFail() {
            Snackbar.make(binding.root , "request fail", Snackbar.LENGTH_LONG).show()
        }

        override fun onFail() {
            val snackbar = Snackbar.make(binding.root , "fail", Snackbar.LENGTH_LONG)
            snackbar.setAction("Reload", object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.d("tag", "click")
                }
            })
            snackbar.show()
        }

        override fun onFinal() {
            Thread.sleep(2000)
            binding.isLoading = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_list_main, container, false)

        binding.isLoading = true

        val context = binding.root.context
        initTab(binding.bookListContent.tabLayout, context)
        initFab(binding.bookRegisterFab, context)

        adapter = BookListAdapter(bookClickCallback)
        val rv = binding.root.findViewById<RecyclerView>(R.id.book_list)
        rv.setHasFixedSize(true)


        observeViewModel(viewModel)
        // viewModel.clearCachedBook()
        // loadBookJob = viewModel.loadBookList(page, state, loadBookListCallback)

        rv.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.activity?.let {
            initMenu(it, binding.root)
        }
    }

    private fun observeViewModel(viewModel: BookListViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
//        viewModel.getLiveData().observe(
//            viewLifecycleOwner,
//            Observer { books ->
//                if (books != null) {
//                    Log.d("debug", "observe start")
//                    adapter.setBookList(books)
//                    binding.isLoading = false
//                }
//            })
        viewModel.getLiveData2().removeObservers(viewLifecycleOwner)
        viewModel.getLiveData2().observe(
            viewLifecycleOwner,
            Observer { books ->
                if (books != null) {
                    Log.d("debug", "observe!!!")
                    adapter.submitList(books)
                    binding.isLoading = false
                }
            })

        viewModel.networkState.observe(
            viewLifecycleOwner,
            Observer { n ->
                Log.d("debug", "--------------"+n.name)
            }
        )
    }

    private fun initMenu(activity: FragmentActivity, view: View) {

        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        // setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val burgerItems = mutableListOf<BurgerMenu>()

        burgerItems.add(BurgerMenu("ログアウト", resources.getString(R.string.fa_sign_out_alt_solid)))
        val navView = view.findViewById<ListView>(R.id.nav_menu_items)
        navView.adapter = MenuItemAdapter(view.context, burgerItems)
    }

    private fun initTab(tabLayout: TabLayout, context: Context) {
        tabLayout.addTab(tabLayout.newTab().setText("ALL"))
        tabLayout.addTab(tabLayout.newTab().setText("未読"))
        tabLayout.addTab(tabLayout.newTab().setText("読中"))
        tabLayout.addTab(tabLayout.newTab().setText("読了"))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                loadBookJobCancel()

                state = when (tab.text) {
                    "未読" -> "not_read"
                    "読中" -> "reading"
                    "読了" -> "read"
                    else -> null
                }
                viewModel.clearCachedBook()
                viewModel.loadBookList(page, state, loadBookListCallback)
            }
        })
    }


    private fun initFab(fab: FloatingActionButton, context: Context) {
        val drawable = FontDrawable(context, R.string.fa_plus_solid, true, true)
        drawable.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        fab.setImageDrawable(drawable)

        fab.setOnClickListener{
            loadBookJobCancel()
            val fragment = BookRegisterFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.let {
                // it.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                it.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                it.addToBackStack(null)
                it.replace(R.id.fragment_container, fragment).commit()
            }
        }
    }

    private fun loadBookJobCancel() {
        if (::loadBookJob.isInitialized) {
            loadBookJob.cancel()
            Log.d("tag", "cancel!!!!")
        }
    }
}

class BurgerMenu(private val title: String, private val icon: String){
    fun getTitle(): String = title
    fun getIcon(): String = icon
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
        textView.nav_item_icon.text = items[position].getIcon()
        return textView
    }
}
