package tgs.app.pengaduan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduan.masyarakat.BerandaActivity.Companion.nik
import tgs.app.pengaduan.masyarakat.BerandaActivity.Companion.password
import tgs.app.pengaduan.masyarakat.BerandaActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login.setOnClickListener {
            if (edit_user.text.toString().isEmpty() || edit_pass.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Isi data dengan benar!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val user: String = edit_user.text.toString()
                val pass: String = edit_pass.text.toString()

                AndroidNetworking.post(url + "login_masyarakat.php")
                    .addBodyParameter("username", user)
                    .addBodyParameter("password", pass)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            if (response.get("response") == "success") {
                                Toast.makeText(
                                    applicationContext,
                                    "Berhasil login",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val niks: String = response.getString("nik")
                                val passWord: String = response.getString("password")
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        BerandaActivity::class.java
                                    ).putExtra(nik, niks).putExtra(password, passWord)
                                )
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Username atau password salah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(
                                applicationContext,
                                "Connection error!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("error1", error.errorBody)
                            Log.e("error2", error.errorCode.toString())
                            Log.e("error3", error.errorDetail)
                        }
                    })
            }
        }

        text_sign_up.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }

        supportActionBar?.title = "DOKLAD!"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.lapor_icon)
        supportActionBar?.setDisplayUseLogoEnabled(true)
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Peringatan")
            .setMessage("Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Ya"){ _, _ ->
                finish()
            }
            .setNegativeButton("Tidak"){ dialog, _ ->
                dialog.dismiss()
            }
        alertDialog.create().show()
    }
}
