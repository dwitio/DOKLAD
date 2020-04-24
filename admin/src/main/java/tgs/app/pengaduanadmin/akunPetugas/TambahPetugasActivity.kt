package tgs.app.pengaduanadmin.akunPetugas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_tambah_petugas.*
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.R

class TambahPetugasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_petugas)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tambah Petugas"

        btn_simpan.setOnClickListener {
            val nama: String = edit_nama.text.toString()
            val username: String = edit_user.text.toString()
            val password: String = edit_pass.text.toString()
            val telepon: String = edit_telp.text.toString()
            val passConfirm = edit_pass_confirm.text.toString()
            val level = radio_level.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(level)

            if (nama.isEmpty() || username.isEmpty() || password.isEmpty() || telepon.isEmpty()
                || passConfirm.isEmpty() || radioButton.text.isEmpty()){
                Toast.makeText(applicationContext, "Isi data dengan benar!", Toast.LENGTH_SHORT).show()
            } else {
                if (password == passConfirm) {
                    createPetugas(nama, username, password, telepon, radioButton)
                } else {
                    Toast.makeText(applicationContext, "Password harus sama", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun createPetugas(nama: String, username: String, password: String, telepon: String, level: RadioButton) {
        AndroidNetworking.post(url + "create_petugas.php")
            .addBodyParameter("nama_petugas", nama)
            .addBodyParameter("username", username)
            .addBodyParameter("password", password)
            .addBodyParameter("telp", telepon)
            .addBodyParameter("level", level.text.toString())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (response.get("response").equals("success")){
                        Toast.makeText(applicationContext, "Berhasil daftar", Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, "Silahkan refresh", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Gagal daftar", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(error: ANError) {
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
