package tgs.app.pengaduanadmin.tanggapan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_tanggapan.*
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.R

class TanggapanActivity : AppCompatActivity() {

    companion object {
        var profilPetugas = "PROFIL_PETUGAS"
        var idnPengaduan = "IDENTITAS_PENGADUAN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggapan)

        val profilPetugass = intent.extras?.getString(profilPetugas)
        val identitasPengaduan = intent.extras?.getString(idnPengaduan)

        btn_simpan.setOnClickListener {
            val tanggapan = edit_tanggapan.text.toString()
            AndroidNetworking.post(url + "create_tanggapan.php")
                .addBodyParameter("id_pengaduan", identitasPengaduan)
                .addBodyParameter("tanggapan", tanggapan)
                .addBodyParameter("id_petugas", profilPetugass)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Toast.makeText(applicationContext, "Berhasil menambah tanggapan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    override fun onError(error: ANError) {
                    }
                })
        }

        supportActionBar?.title = "Tanggapan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
