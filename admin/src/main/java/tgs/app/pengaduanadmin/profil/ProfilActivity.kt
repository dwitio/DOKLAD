package tgs.app.pengaduanadmin.profil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_detail_petugas.*
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.android.synthetic.main.activity_profil.btn_simpan
import kotlinx.android.synthetic.main.activity_profil.edit_nama
import kotlinx.android.synthetic.main.activity_profil.edit_pass_confirm
import kotlinx.android.synthetic.main.activity_profil.edit_user
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.R

class ProfilActivity : AppCompatActivity() {

    companion object {
        var idProfil: String = "IDPROFIL"
        var passProfil: String = "PASSPROFIL"
    }

    private var passOk: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val idPetugas = intent.extras?.getString(idProfil)
        val passwordPetugas = intent.extras?.getString(passProfil)

        readPetugas(idPetugas)

        btn_simpan.setOnClickListener {
            updatePetugas(passwordPetugas, idPetugas)
        }

        supportActionBar?.title = "Ubah Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun readPetugas(idPetugas: String?) {
        AndroidNetworking.get(url + "read_petugas2.php")
            .addQueryParameter("id_petugas", idPetugas)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        val jsonObject = jsonArray.getJSONObject(0)
                        val nama = jsonObject.getString("nama_petugas")
                        val telp = jsonObject.getString("telp")
                        val user = jsonObject.getString("username")

                        edit_nama.setText(nama)
                        edit_telp.setText(telp)
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

    private fun updatePetugas(passwordPetugas: String?, idPetugas: String?) {
        val nama = edit_nama.text.toString()
        val user = edit_user.text.toString()
        val telp = edit_telp.text.toString()
        val passNew = edit_pass_new.text.toString()
        val passConfirm = edit_pass_confirm.text.toString()
        if (passNew == "" && passConfirm == ""){
            passOk = passwordPetugas
        } else if (passNew == passConfirm){
            passOk = passConfirm
        } else {
            Toast.makeText(applicationContext, "Password harus sama", Toast.LENGTH_SHORT).show()
        }

        AndroidNetworking.post(url + "update_petugas.php")
            .addBodyParameter("id_petugas", idPetugas)
            .addBodyParameter("nama_petugas", nama)
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
