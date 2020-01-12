package net.edgwbs.bookstorage.view

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.model.ReadState
import java.util.*


object CustomBindingAdapter {
    @BindingAdapter("app:visibleGone")
    @JvmStatic
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(url: String?) {
        Picasso.get().load(url).into(this)
    }

    @JvmStatic
    @BindingAdapter("app:statusIcon")
    fun statusIcon(view: TextView, readState: Int) {
        return when(readState) {
            ReadState.NotRead.state -> {
                view.text = view.resources.getText(R.string.fa_book_solid).toString()
            }
            ReadState.Reading.state -> {
                view.text = view.resources.getText(R.string.fa_book_open_solid).toString()
            }
            else -> {
                view.text = view.resources.getText(R.string.fa_check_solid).toString()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:statusText")
    fun statusText(view: TextView, readState: Int) {
        return when(readState) {
            ReadState.NotRead.state -> {
                view.text = "未読"
            }
            ReadState.Reading.state -> {
                view.text = "読中"
            }
            else -> {
                view.text = "読了"
            }
        }
    }

    private val df = SimpleDateFormat("yyyy/MM/dd")
    @JvmStatic
    @BindingAdapter("app:date")
    fun formatDate(view: TextView, date: Date?) {
        date?.let {
            view.text = df.format(it)
        }
    }

    @JvmStatic
    @BindingAdapter("app:orNotSetText")
    fun orNotSetText(view: TextView, text: String?) {
        if (text == null) {
            view.text = "not set"
        } else {
            view.text = text
        }
    }
}

