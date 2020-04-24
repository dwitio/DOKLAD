package tgs.app.pengaduan.masyarakat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import tgs.app.pengaduan.R
import tgs.app.pengaduan.pojo.Pengaduan

class PengaduanAdapter(private val items: MutableList<Pengaduan>, private val listener: (Pengaduan) -> Unit)
    : RecyclerView.Adapter<PengaduanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_beranda,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private val idPengaduan: TextView = containerView.findViewById(R.id.txt_id)
        private val tglPengaduan: TextView = containerView.findViewById(R.id.txt_tanggal)
        private val judul: TextView = containerView.findViewById(R.id.txt_judul)
        private val status: TextView = containerView.findViewById(R.id.txt_status)

        fun bindItem(items: Pengaduan, listener: (Pengaduan) -> Unit){
            idPengaduan.text = items.id_pengaduan
            tglPengaduan.text = items.tgl_pengaduan
            judul.text = items.judul
            status.text = items.status
            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}