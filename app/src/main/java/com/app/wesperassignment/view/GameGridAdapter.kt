package com.app.wesperassignment.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.app.wesperassignment.R
import com.app.wesperassignment.model.BoardCell

class GameGridAdapter(
    private val context: Context,
    private var cells: List<BoardCell>,
    private val onCellTapListener: OnCellTapListener
): BaseAdapter() {

    override fun getCount(): Int = cells.size

    override fun getItem(position: Int): BoardCell = cells[position]

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: BoardCellView(context).apply {
            setupCell(position, onCellTapListener)
        }
        val item = getItem(position)
        (view as BoardCellView).apply {
            if (item.flashed){
                setBackgroundColor(context.getColor(R.color.purple_700))
            } else {
                setBackgroundColor(context.getColor(R.color.white))
            }
        }
        return view
    }

    fun updateItems(list: List<BoardCell>){
        cells = list
        notifyDataSetChanged()
    }

}