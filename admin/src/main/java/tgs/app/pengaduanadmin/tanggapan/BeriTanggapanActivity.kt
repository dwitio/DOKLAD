package tgs.app.pengaduanadmin.tanggapan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_beri_tanggapan.*
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.R
import tgs.app.pengaduanadmin.adapter.PengaduanAdapter
import tgs.app.pengaduanadmin.pojo.Pengaduan
import tgs.app.pengaduanadmin.tanggapan.DetailActivity.Companion.DETAIL
import tgs.app.pengaduanadmin.tanggapan.DetailActivity.Companion.IdentityPetugas

class BeriTanggapanActivity : AppCompatActivity() {

    companion object {
        var idPetugass = "PETUGAS_ID"
    }

    private lateinit var pengaduanAdapter: PengaduanAdapter
    private val pengaduan = mutableListOf<Pengaduan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beri_tanggapan)

        val petugasId = intent.extras?.getString(idPetugass)

        read_pengaduan()
        pengaduanAdapter = PengaduanAdapter(pengaduan) {
            val detail = Pengaduan(
                it.id_pengaduan,
                it.tgl_pengaduan,
                it.nama,
                it.judul,
                it.isi_laporan,
                it.foto,
                it.status
            )
            startActivity(
                Intent(this, DetailActivity::class.java).putExtra(DETAIL, detail).putExtra(IdentityPetugas, petugasId)
            )
        }

        list_pengaduan.layoutManager = LinearLayoutManager(this)
        list_pengaduan.hasFixedSize()
        list_pengaduan.adapter = pengaduanAdapter

        swipe_refresh.setOnRefreshListener {
            read_pengaduan()
        }

        supportActionBar?.title = "Beri Tanggapan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun read_pengaduan() {
        list_pengaduan.adapter = null
        pengaduan.clear()
        AndroidNetworking.get(url + "read_pengaduan.php")
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
                                    responses.getString("nama"),
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
                    Toast.makeText(applicationContext, "Connection error!!!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
