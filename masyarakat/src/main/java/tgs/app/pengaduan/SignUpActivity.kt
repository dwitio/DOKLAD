package tgs.app.pengaduan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import tgs.app.common.Config.Companion.url


class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign Up"

        btn_sign_up.setOnClickListener {
            val nik: String = edit_nik.text.toString()
            val nama: String = edit_nama.text.toString()
            val username: String = edit_user.text.toString()
            val password: String = edit_pass.text.toString()
            val telepon: String = edit_telp.text.toString()

            if (nik.isEmpty() || nama.isEmpty() || username.isEmpty() || password.isEmpty() || telepon.isEmpty()){
                Toast.makeText(applicationContext, "Isi data dengan benar!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                AndroidNetworking.post(url + "create_masyarakat.php")
                    .addBodyParameter("nik", nik)
                    .addBodyParameter("nama", nama)
                    .addBodyParameter("username", username)
                    .addBodyParameter("password", password)
                    .addBodyParameter("telp", telepon)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            if (response.get("response") == "success") {
                                Toast.makeText(
                                    applicationContext,
                                    "Berhasil daftar",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Gagal daftar",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
