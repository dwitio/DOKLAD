package tgs.app.pengaduan.masyarakat

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduan.R
import tgs.app.pengaduan.pojo.Pengaduan

class DetailActivity : AppCompatActivity() {

    companion object {
        var DETAIL = "DETAIL"
    }

    var detail: Pengaduan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        detail = intent.extras?.getParcelable<Pengaduan>(DETAIL)
        txt_tanggal.text = detail?.tgl_pengaduan
        txt_judul.text = detail?.judul
        txt_isi_laporan.text = detail?.isi_laporan
        txt_status.text = detail?.status

        detail_pengaduan(detail)

        swipe_refresh_detail.isRefreshing = true

        swipe_refresh_detail.setOnRefreshListener {
            detail_pengaduan(detail)
        }
        supportActionBar?.title = "Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        detail_pengaduan(detail)
        readTanggapan()
    }

    private fun detail_pengaduan(detail: Pengaduan?) {
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
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(img_bukti)
                        }

                        if (responses.getString("status") == "belum ditanggapi"){
                            txt_tanggapan.visibility = View.GONE
                            isi_tanggapan.visibility = View.GONE
                        }
                        if (responses.getString("status") == "proses") {
                            txt_tanggapan.visibility = View.VISIBLE
                            isi_tanggapan.visibility = View.VISIBLE
                            readTanggapan()
                        }
                        if (responses.getString("status") == "selesai") {
                            txt_tanggapan.visibility = View.VISIBLE
                            isi_tanggapan.visibility = View.VISIBLE
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
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        val responses = jsonArray.getJSONObject(0)
                        isi_tanggapan.text = responses.getString("tanggapan")
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
