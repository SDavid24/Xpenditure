package com.example.xpenditure

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.table_row_layout.view.*
import kotlinx.android.synthetic.main.table_view_cell_layout.view.*


class DataCellAdapter(private val context: Context,
                             private val list: ArrayList<Cell>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.table_row_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {

            holder.itemView.tv_date.text = model.date
            holder.itemView.tv_merchant.text = model.merchantType

            holder.itemView.tv_total_amount.text = model.totalAmount.toString().toInt().toString()
            holder.itemView.tv_comment.text = model.commentDetail

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Cell)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}