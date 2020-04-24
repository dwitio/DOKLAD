package tgs.app.pengaduanadmin.akunPetugas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_akun_petugas.*
import kotlinx.android.synthetic.main.activity_beri_tanggapan.swipe_refresh
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.R
import tgs.app.pengaduanadmin.adapter.PetugasAdapter
import tgs.app.pengaduanadmin.pojo.Petugas

class AkunPetugasActivity : AppCompatActivity() {

    private lateinit var petugasAdapter: PetugasAdapter
    private val petugas = mutableListOf<Petugas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun_petugas)

        read_petugas()
        petugasAdapter = PetugasAdapter(petugas) {
            val detail = Petugas(
                it.id_petugas,
                it.nama_petugas,
                it.username,
                it.password,
                it.telp,
                it.level
            )
            startActivity(
                Intent(this, DetailPetugasActivity::class.java).putExtra(DetailPetugasActivity.DETAIL, detail))

        }

        list_petugas.layoutManager = LinearLayoutManager(this)
        list_petugas.hasFixedSize()
        list_petugas.adapter = petugasAdapter

        swipe_refresh.setOnRefreshListener {
            read_petugas()
        }

        btn_tambah.setOnClickListener {
            startActivity(Intent(this, TambahPetugasActivity::class.java))
        }

        supportActionBar?.title = "List Petugas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun read_petugas() {
        list_petugas.adapter = null
        petugas.clear()
        AndroidNetworking.get(url + "read_petugas.php")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    swipe_refresh.isRefreshing = false
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        for (i in 0 until jsonArray.length()) {
                            val responses = jsonArray.getJSONObject(i)
                            petugas.add(
                                Petugas(
                                    responses.getString("id_petugas"),
                                    responses.getString("nama_petugas"),
                                    responses.getString("username"),
                                    responses.getString("password"),
                                    responses.getString("telp"),
                                    responses.getString("level")
                                )
                            )
                        }
                        list_petugas.adapter = petugasAdapter
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
