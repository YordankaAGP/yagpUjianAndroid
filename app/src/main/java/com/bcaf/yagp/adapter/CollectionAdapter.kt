package com.bcaf.yagp.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.yagp.R
import com.bcaf.yagp.service.model.CollectionItem

class CollectionAdapter (
    private var data : List<CollectionItem?>,
    private val clickListener: (CollectionItem) -> Unit,
    private val longClickListener : (CollectionItem) ->Unit
) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    fun setCollection(d: List<CollectionItem?>?){
        if (d != null) {
            data = d
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.collection_data, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.txtNama.text = data?.get(position)?.nama
        holder.txtAlamat.text = data?.get(position)?.alamat
        holder.txtOutstanding.text = data?.get(position)?.outstanding

        holder.itemView.setOnClickListener {
            clickListener(data?.get(position)!!)
        }

        holder.itemView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Hapus Data")
                .setMessage("Apakah anda yakin ingin menghapus data ini ?")
                .setPositiveButton("Ya") { _, _ ->
                    longClickListener(data?.get(position)!!)
                }
                .setNegativeButton("Tidak", null)
                .create()
            alertDialog.show()
            true
        }

    }

    override fun getItemCount():Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNama: TextView = itemView.findViewById(R.id.txtNama)
        val txtAlamat: TextView = itemView.findViewById(R.id.txtAlamat)
        val txtOutstanding: TextView = itemView.findViewById(R.id.txtOutstanding)
    }
}