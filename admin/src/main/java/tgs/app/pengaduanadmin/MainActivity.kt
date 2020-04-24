package tgs.app.pengaduanadmin

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.BerandaActivity.Companion.id
import tgs.app.pengaduanadmin.BerandaActivity.Companion.password
import tgs.app.pengaduanadmin.petugas.BerandaPetugasActivity

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.d("MainActivity", "Access allowed")
        } else {
            requestStoragePermission()
        }

        btn_login.setOnClickListener {
            if (edit_user.text.toString().isEmpty() || edit_pass.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Isi data dengan benar!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val user: String = edit_user.text.toString()
                val pass: String = edit_pass.text.toString()
                AndroidNetworking.post(url + "login_petugas.php")
                    .addBodyParameter("username", user)
                    .addBodyParameter("password", pass)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            if (response.get("response") == "success") {
                                if (response.get("level") == "admin") {
                                    Toast.makeText(
                                        applicationContext,
                                        "Berhasil login",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val idPetugas: String = response.getString("id_petugas")
                                    val passPetugas: String = response.getString("password")
                                    startActivity(
                                        Intent(applicationContext, BerandaActivity::class.java)
                                            .putExtra(id, idPetugas)
                                            .putExtra(password, passPetugas)
                                    )
                                    finish()
                                } else {
                                    val idPetugas: String = response.getString("id_petugas")
                                    val passPetugas: String = response.getString("password")
                                    startActivity(
                                        Intent(applicationContext, BerandaPetugasActivity::class.java)
                                            .putExtra(id, idPetugas)
                                            .putExtra(password, passPetugas)
                                    )
                                    finish()
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Username atau password salah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(applicationContext, "Ada masalah dengan koneksi", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }
        supportActionBar?.title = "DOKLAD!"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.lapor_icon)
        supportActionBar?.setDisplayUseLogoEnabled(true)
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("The permission is needed because for generate report to pdf")
                .setPositiveButton("OK"){ _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }
                .setNegativeButton("CANCEL"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
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
