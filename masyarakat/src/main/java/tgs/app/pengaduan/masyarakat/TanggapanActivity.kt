package tgs.app.pengaduan.masyarakat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_tanggapan.*
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduan.masyarakat.DetailActivity.Companion.DETAIL
import tgs.app.pengaduan.R
import tgs.app.pengaduan.pojo.Pengaduan

class TanggapanActivity : AppCompatActivity() {

    companion object {
        var nikk: String = "NIKK"
    }
    private lateinit var pengaduanAdapter: PengaduanAdapter
    private val pengaduan = mutableListOf<Pengaduan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggapan)

        val niks = intent.extras?.get(nikk).toString()
        read_pengaduan(niks)

        pengaduanAdapter = PengaduanAdapter(pengaduan) {
            val detail = Pengaduan(
                it.id_pengaduan,
                it.tgl_pengaduan,
                it.judul,
                it.isi_laporan,
                it.foto,
                it.status
            )
            startActivity(
                Intent(
                    this,
                    DetailActivity::class.java
                ).putExtra(DETAIL, detail)
            )
        }

        list_pengaduan.layoutManager = LinearLayoutManager(this)
        list_pengaduan.hasFixedSize()
        list_pengaduan.adapter = pengaduanAdapter

        swipe_refresh.setOnRefreshListener {
            read_pengaduan(niks)
        }

        supportActionBar?.title = "Tanggapan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun read_pengaduan(niks: String) {
        list_pengaduan.adapter = null
        pengaduan.clear()
        AndroidNetworking.get(url + "read_pengaduan.php")
            .addQueryParameter("nik", niks)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    swipe_refresh.isRefreshing = false
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        for (i in 0 until jsonArray.length()) {
                            val responses = jsonArray.getJSONObject(i)
                            pengaduan.add(
                                Pengaduan(
                                    responses.getString("id_pengaduan"),
                                    responses.getString("tgl_pengaduan"),
                                    responses.getString("judul"),
                                    responses.getString("isi_laporan"),
                                    responses.getString("foto"),
                                    responses.getString("status")
                                )
                            )
                            if (pengaduan.isEmpty()){
                                txt_lapor.visibility = View.VISIBLE
                            } else {
                                txt_lapor.visibility = View.GONE
                            }
                        }
                        list_pengaduan.adapter = pengaduanAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(error: ANError) {
                    Toast.makeText(applicationContext, "Connection error!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
