package net.edgwbs.bookstorage.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import info.androidhive.fontawesome.FontDrawable
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.app_bar_with_search.view.*
import kotlinx.android.synthetic.main.nav_item.view.*
import kotlinx.coroutines.Job

import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookListMainBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.NetworkState
import net.edgwbs.bookstorage.model.ReadState
import net.edgwbs.bookstorage.utils.FragmentConstBookID
import net.edgwbs.bookstorage.viewModel.BookListViewModel
import net.edgwbs.bookstorage.viewModel.RequestCallback

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
                it.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                it.addToBackStack(null)
                it.replace(R.id.fragment_container, fragment).commit()
            }
        }
    }

//    private val loadBookListCallback = object : RequestCallback {
//        override fun onRequestSuccess() {
//            // Snackbar.make(binding.root , "request success", Snackbar.LENGTH_LONG).show()
//        }
//
//        override fun onRequestFail() {
//            Snackbar.make(binding.root , "request fail", Snackbar.LENGTH_LONG).show()
//        }
//
//        override fun onFail() {
//            val snackbar = Snackbar.make(binding.root , "fail", Snackbar.LENGTH_LONG)
//            snackbar.setAction("Reload", object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    Log.d("tag", "click")
//                }
//            })
//            snackbar.show()
//        }
//
//        override fun onFinal() {
//            Thread.sleep(2000)
//            binding.isLoading = false
//        }
//    }

    private val stateChangeCallback = object : RequestCallback {
        override fun onRequestSuccess() {}
        override fun onRequestFail() {}
        override fun onFail() {}
        override fun onFinal() {}
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

        binding.bookListContent.searchBar.bookListSearchView.isSubmitButtonEnabled = true

        adapter = BookListAdapter(bookClickCallback)
        val rv = binding.root.findViewById<RecyclerView>(R.id.book_list)
        rv.setHasFixedSize(true)
        rv.addItemDecoration(DividerItemDecoration(rv.context, LinearLayoutManager(activity).orientation))

        val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(adapter)
        swipeToDismissTouchHelper.attachToRecyclerView(rv)

        observeViewModel(viewModel)

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
        viewModel.getLiveData().removeObservers(viewLifecycleOwner)
        viewModel.getLiveData().observe(
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
                when(n) {
                    NetworkState.RUNNING -> {
                        binding.isLoading = true
                    }
                    NetworkState.NOTWORK -> {
                        binding.isLoading = false
                    }
                    else -> binding.isLoading = false
                }
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
                val state = when (tab.text) {
                    "未読" -> ReadState.NotRead
                    "読中" -> ReadState.Reading
                    "読了" -> ReadState.Read
                    else -> null
                }
                viewModel.createDataSource(state)
                viewModel.refreshData()
                adapter.notifyDataSetChanged()
            }
        })
    }


    private fun initFab(fab: FloatingActionButton, context: Context) {
        val drawable = FontDrawable(context, R.string.fa_plus_solid, true, true)
        drawable.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        fab.setImageDrawable(drawable)

        fab.setOnClickListener{
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
    }

    private fun getSwipeToDismissTouchHelper(adapter: BookListAdapter) =
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //スワイプ時に実行
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val book = adapter.getItemByPosition(viewHolder.adapterPosition)
                book?.let {
                    viewModel.changeState(it, stateChangeCallback)
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
                }
            }

            //スワイプした時の背景を設定
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val book = adapter.getItemByPosition(viewHolder.adapterPosition)
                book?.let {
                    var labelRes = -1
                    var iconRes = -1
                    var bgRes = -1
                    when (it.readState) {
                        ReadState.NotRead.value -> {
                            labelRes = R.string.swipe_action_reading
                            iconRes = R.string.fa_book_open_solid
                            bgRes = R.color.colorPrimary
                        }
                        ReadState.Reading.value -> {
                            labelRes = R.string.swipe_action_read
                            iconRes = R.string.fa_check_solid
                            bgRes = R.color.colorLightGreen
                        }
                        ReadState.Read.value -> {
                            labelRes = R.string.swipe_action_reading
                            iconRes = R.string.fa_book_open_solid
                            bgRes = R.color.colorPrimary
                        }
                    }


                    val textMargin = 40
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(context!!, bgRes))
                        .addSwipeRightLabel(getString(labelRes))
                        .setSwipeRightLabelColor(Color.WHITE)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(context!!, bgRes))
                        .addSwipeLeftLabel(getString(labelRes))
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .setIconHorizontalMargin(1, textMargin)
                        .create()
                        .decorate()

                    val itemView = viewHolder.itemView
                    val icon = FontDrawable(context, iconRes, true, false)
                    icon.textSize = 17.toFloat()
                    val rightMargin = 90
                    val topMargin = (itemView.height/2 - icon.intrinsicHeight/3)
                    val bottomMargin = (itemView.height/2 + icon.intrinsicHeight)
                    if (dX < -rightMargin) {
                        icon.setTextColor(ContextCompat.getColor(context!!, android.R.color.white))
                        icon.setBounds(
                            itemView.right - rightMargin,
                            itemView.top + topMargin,
                            itemView.right,
                            itemView.top + bottomMargin
                        )
                        icon.draw(c)
                    }
                    val leftMargin = 30
                    if (dX > leftMargin) {
                        icon.setTextColor(ContextCompat.getColor(context!!, android.R.color.white))
                        icon.setBounds(
                            itemView.left + leftMargin,
                            itemView.top + topMargin,
                            itemView.left,
                            itemView.top + bottomMargin
                        )
                        icon.draw(c)
                    }

                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

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
