package tgs.app.pengaduanadmin.tanggapan

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.R
import tgs.app.pengaduanadmin.pojo.Pengaduan
import tgs.app.pengaduanadmin.tanggapan.TanggapanActivity.Companion.idnPengaduan
import tgs.app.pengaduanadmin.tanggapan.TanggapanActivity.Companion.profilPetugas


class DetailActivity : AppCompatActivity() {

    companion object {
        var DETAIL = "DETAIL"
        var IdentityPetugas = "IDENTITAS"
    }

    var detail: Pengaduan? = null
    var identitas: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        detail = intent.extras?.getParcelable(DETAIL)
        identitas = intent.extras?.getString(IdentityPetugas)
        detailPengaduan(detail)

        swipe_refresh_detail.isRefreshing = true

        swipe_refresh_detail.setOnRefreshListener {
            detailPengaduan(detail)
        }

        supportActionBar?.title = "Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        detailPengaduan(detail)
        readTanggapan()
    }

    private fun detailPengaduan(detail: Pengaduan?) {
        AndroidNetworking.post(url + "detail_pengaduan.php")
            .addBodyParameter("id_pengaduan", detail?.id_pengaduan)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    swipe_refresh_detail.isRefreshing = false
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        val responses = jsonArray.getJSONObject(0)
                        txt_tanggal.text = responses?.getString("tgl_pengaduan")
                        txt_nama.text = responses?.getString("nama")
                        txt_judul.text = responses?.getString("judul")
                        txt_isi_laporan.text = responses?.getString("isi_laporan")
                        txt_status.text = responses?.getString("status")
                        if (responses?.getString("foto") == "") {
                            img_bukti.visibility = View.GONE
                            progress_circular.visibility = View.GONE
                        } else {
                            Glide.with(applicationContext)
                                .load(responses.getString("foto"))
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable?>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        progress_circular.visibility = View.GONE
                                        img_bukti.visibility = View.GONE
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable?>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        progress_circular.visibility = View.GONE
                                        return false
                                    }
                                })
                                .transition(withCrossFade())
                                .into(img_bukti)
                        }

                        if (responses.getString("status") == "belum ditanggapi"){
                            txt_tanggapan.visibility = View.GONE
                            isi_tanggapan.visibility = View.GONE
                            btn_tanggapan.setOnClickListener {
                                val builder = AlertDialog.Builder(this@DetailActivity)
                                builder.setTitle("Peringatan")
                                builder.setMessage("Proses laporan?")
                                builder.setPositiveButton("Ok"){dialog, which ->
                                    AndroidNetworking.post(url + "update_status_pengaduan.php")
                                        .addBodyParameter("id_pengaduan", detail?.id_pengaduan)
                                        .addBodyParameter("status", "proses")
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(object : JSONObjectRequestListener {
                                            override fun onResponse(response: JSONObject) {
                                                if (response.get("response") == "success"){
                                                    Toast.makeText(applicationContext, "Laporan telah diproses", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(applicationContext, "Gagal memproses laporan", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            override fun onError(error: ANError) {
                                                Toast.makeText(applicationContext, "Connection error!!", Toast.LENGTH_SHORT).show()
                                                Log.e("error1", error.errorBody)
                                            }
                                        })
                                }
                                builder.setNegativeButton("Batal"){dialog, which ->
                                    dialog.cancel()
                                }
                                builder.create().show()
                            }
                        }
                        if (responses.getString("status") == "proses") {
                            txt_tanggapan.visibility = View.VISIBLE
                            isi_tanggapan.visibility = View.VISIBLE
                            btn_tanggapan.text = "Beri Tanggapan"
                            btn_tanggapan.setOnClickListener {
                                startActivity(
                                    Intent(applicationContext, TanggapanActivity::class.java)
                                        .putExtra(profilPetugas, identitas)
                                        .putExtra(idnPengaduan, detail?.id_pengaduan))
                            }
                            readTanggapan()
                        }
                        if (responses.getString("status") == "selesai") {
                            txt_tanggapan.visibility = View.VISIBLE
                            isi_tanggapan.visibility = View.VISIBLE
                            btn_tanggapan.visibility = View.GONE
                            readTanggapan()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(error: ANError) {
                }
            })
    }

    private fun readTanggapan() {
        AndroidNetworking.post(url + "read_tanggapan.php")
            .addBodyParameter("id_pengaduan", detail?.id_pengaduan)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    swipe_refresh_detail.isRefreshing = false
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        val responses = jsonArray.getJSONObject(0)
                        isi_tanggapan.text = responses.getString("tanggapan")
                        btn_tanggapan.text = "Selesai"
                        btn_tanggapan.setOnClickListener {
                            val builders = AlertDialog.Builder(this@DetailActivity)
                            builders.setTitle("Peringatan")
                            builders.setMessage("Selesaikan laporan?")
                            builders.setPositiveButton("Ok"){dialog, which ->
                                AndroidNetworking.post(url + "update_status_pengaduan.php")
                                    .addBodyParameter("id_pengaduan", detail?.id_pengaduan)
                                    .addBodyParameter("status", "selesai")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(object : JSONObjectRequestListener {
                                        override fun onResponse(response: JSONObject) {
                                            if (response.get("response") == "success"){
                                                Toast.makeText(applicationContext, "Laporan telah diselesaikan", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(applicationContext, "Gagal memproses laporan", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onError(error: ANError) {
                                            Toast.makeText(applicationContext, "Connection error!!", Toast.LENGTH_SHORT).show()
                                            Log.e("error1", error.errorBody)
                                            Log.e("error2", error.errorCode.toString())
                                            Log.e("error3", error.errorDetail)
                                        }
                                    })
                            }
                            builders.setNegativeButton("Batal"){dialog, which ->
                                dialog.cancel()
                            }
                            builders.create().show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(error: ANError) {
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
