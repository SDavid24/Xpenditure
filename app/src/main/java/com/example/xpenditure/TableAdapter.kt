package com.example.xpenditure

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import android.widget.TextView
import android.widget.LinearLayout
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.table_view_cell_layout.view.*
/*

class TableAdapter: AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {

    class CellViewHolder(itemView: View) :
        AbstractViewHolder(itemView) {
        val cell_container: LinearLayout = itemView.findViewById(R.id.cell_container)
        val cell_textview: TextView = itemView.findViewById(R.id.cell_data)

    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        // Get cell xml layout
        val layout: View =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.table_view_cell_layout,
                    parent, false
                )
        // Create a Custom ViewHolder for a Cell item.
        return CellViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindCellViewHolder(holder: AbstractViewHolder, cellItemModel: Cell?,
                                      columnPosition: Int, rowPosition: Int) {

        if(holder is CellViewHolder){
            when (columnPosition) {
                0 -> {
                    holder.itemView.cell_data.text = cellItemModel!!.date
                }
                1 -> {
                    holder.itemView.cell_data.text = cellItemModel!!.merchantType
                }
                2 -> {
                    holder.itemView.cell_data.text = "$" + cellItemModel!!.totalAmount.toString()
                }
                else -> {
                    holder.itemView.cell_data.text = cellItemModel!!.commentDetail
                }
            }

            // It is necessary to remeasure itself.
            holder.cell_container.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            holder.cell_textview.requestLayout()

        }
    }

    //Adapter for Column Header
     class ColumnHeaderViewHolder(itemView: View) :
        AbstractViewHolder(itemView) {
        val column_header_container: LinearLayout = itemView.findViewById(R.id.column_header_container)
        val cell_textview: TextView = itemView.findViewById(R.id.column_header_textView)
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup, viewType: Int):AbstractViewHolder {
        val layout: View =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.table_view_column_header_layout,
                    parent, false
                )
        // Create a Custom ViewHolder for a Cell item.
        return ColumnHeaderViewHolder(layout)    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder, columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        if (holder is ColumnHeaderViewHolder){
            when (columnPosition) {
                0 -> {
                    holder.cell_textview.text = columnHeaderItemModel!!.dateWord
                }
                1 -> {
                    holder.cell_textview.text = columnHeaderItemModel!!.merchantWord
                }
                2 -> {
                    holder.cell_textview.text = columnHeaderItemModel!!.totalWord
                }
                3 -> {
                    holder.cell_textview.text = columnHeaderItemModel!!.commentWord
                }
            }
        }
    }


    internal class RowHeaderViewHolder(itemView: View) :
        AbstractViewHolder(itemView) {
        val cell_textview: TextView = itemView.findViewById(R.id.cell_row_data)

    }


    override fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {
        // Get Row Header xml Layout
        val layout =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.table_view_row_header_layout, parent,
                    false
                )

        // Create a Row Header ViewHolder
        return RowHeaderViewHolder(layout)    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int) {

        if (holder is RowHeaderViewHolder){
            //holder.cell_textview.text = (rowPosition + 1).toString()
            holder.cell_textview.text = (1).toString()
        }
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.getContext())
            .inflate(R.layout.table_view_corner_layout, parent, false);    }


}*/
