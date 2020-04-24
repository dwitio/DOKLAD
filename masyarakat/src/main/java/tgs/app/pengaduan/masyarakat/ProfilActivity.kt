package tgs.app.pengaduan.masyarakat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.android.synthetic.main.activity_profil.edit_nama
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduan.R

class ProfilActivity : AppCompatActivity() {

    companion object {
        var nikProfil: String = "NIKPROFIL"
        var passProfil: String = "PASSPROFIL"
    }

    private var passOk: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val nikkk = intent.extras?.getString(nikProfil)
        val password33 = intent.extras?.getString(passProfil)

        readMasyarakat(nikkk)

        btn_simpan.setOnClickListener {
            updateMasyarakat(password33, nikkk)
        }

        supportActionBar?.title = "Ubah Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun readMasyarakat(nikkk: String?) {
        AndroidNetworking.get(url + "read_masyarakat.php")
            .addQueryParameter("nik", nikkk)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        val jsonObject = jsonArray.getJSONObject(0)
                        val nik = jsonObject.getString("nik")
                        val nama = jsonObject.getString("nama")
                        val telp = jsonObject.getString("telp")
                        val user = jsonObject.getString("username")

                        txt_nik.text =  nik
                        edit_nama.setText(nama)
                        edit_nomor_telepon.setText(telp)
                        edit_user.setText(user)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(error: ANError) {
                    Toast.makeText(applicationContext, "Connection error!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateMasyarakat(password33: String?, nikkk: String?) {
        val nama = edit_nama.text.toString()
        val user = edit_user.text.toString()
        val telp = edit_nomor_telepon.text.toString()
        val passNew = edit_pass_new.text.toString()
        val passConfirm = edit_pass_confirm.text.toString()
        if (passNew == "" && passConfirm == ""){
            passOk = password33
        } else if (passNew == passConfirm){
            passOk = passConfirm
        } else {
            Toast.makeText(applicationContext, "Password harus sama", Toast.LENGTH_SHORT).show()
        }

        AndroidNetworking.post(url + "update_masyarakat.php")
            .addBodyParameter("nik", nikkk)
            .addBodyParameter("nama", nama)
            .addBodyParameter("username", user)
            .addBodyParameter("password", passOk)
            .addBodyParameter("telp", telp)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (response.get("response") == "success"){
                        Toast.makeText(applicationContext, "Berhasil ubah profil", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(error: ANError) {
                    Toast.makeText(applicationContext, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
