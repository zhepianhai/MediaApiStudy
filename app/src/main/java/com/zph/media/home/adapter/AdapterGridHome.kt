package com.zph.media.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.zph.media.R
import com.zph.media.base.BaseHolder


open class AdapterGridHome (private val context: Context, private val planetList: MutableList<String>) : BaseAdapter() {

    override fun getCount(): Int = planetList.size

    override fun getItem(position: Int): Any = planetList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_gridview_home_item, null)
            holder = ViewHolder(view)
            //视图持有者的内部控件对象已经在构造时一并初始化了，故这里无需再做赋值
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        holder.tv_name!!.text = planetList[position]
        return view!!
    }

    //ViewHolder中的属性在构造时初始化
    inner class ViewHolder(val view: View) {
        val tv_name: TextView = view.findViewById(R.id.item_tv_title) as TextView
    }
}//    override fun convert(holder: BaseHolder, position: Int) {
//        // 获取item中的TextView
//        val text = holder.getView<TextView>(R.id.item_tv_title)
//        text.text = this.mData[position]
//    }
//}