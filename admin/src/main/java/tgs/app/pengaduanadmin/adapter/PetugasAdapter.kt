package tgs.app.pengaduanadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import tgs.app.pengaduanadmin.R
import tgs.app.pengaduanadmin.pojo.Petugas

class PetugasAdapter(private val items: MutableList<Petugas>, private val listener: (Petugas) -> Unit)
    : RecyclerView.Adapter<PetugasAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetugasAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_petugas, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PetugasAdapter.ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        private val idPetugas: TextView = containerView.findViewById(R.id.txt_id_petugas)
        private val namaPetugas: TextView = containerView.findViewById(R.id.txt_nama_petugas)
        private val level: TextView = containerView.findViewById(R.id.txt_level)

        fun bindItem(items: Petugas, listener: (Petugas) -> Unit){
            idPetugas.text = items.id_petugas
            namaPetugas.text = items.nama_petugas
            level.text = items.level
            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}