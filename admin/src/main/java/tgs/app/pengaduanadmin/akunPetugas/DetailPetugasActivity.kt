package tgs.app.pengaduanadmin.akunPetugas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import tgs.app.pengaduanadmin.pojo.Petugas

class DetailPetugasActivity : AppCompatActivity() {

    companion object {
        var DETAIL = "DETAIL"
        var IdentityPetugas = "IDENTITAS"
    }

    var detail: Petugas? = null
    var identitas: String? = null

    private var passOk: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_petugas)

        detail = intent.extras?.getParcelable(DETAIL)
        identitas = intent.extras?.getString(IdentityPetugas)
        readPetugas(detail)

        btn_simpan.setOnClickListener {
            updatePetugas(detail)
        }

        supportActionBar?.title = "Ubah Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun readPetugas(idPetugas: Petugas?) {
        AndroidNetworking.get(url + "read_petugas2.php")
            .addQueryParameter("id_petugas", idPetugas?.id_petugas)
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

    private fun updatePetugas(petugas: Petugas?) {
        val nama = edit_nama.text.toString()
        val user = edit_user.text.toString()
        val telp = edit_nomor_telepon.text.toString()
        val passNew = edit_pass_new.text.toString()
        val passConfirm = edit_pass_confirm.text.toString()
        if (passNew == "" && passConfirm == ""){
            passOk = petugas?.password
        } else if (passNew == passConfirm){
            passOk = passConfirm
        } else {
            Toast.makeText(applicationContext, "Password harus sama", Toast.LENGTH_SHORT).show()
        }

        AndroidNetworking.post(url + "update_petugas.php")
            .addBodyParameter("id_petugas", petugas?.id_petugas)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btn_delete){
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Peringatan")
                .setMessage("Anda yakin ingin menghapus " + detail?.nama_petugas + "?")
                .setPositiveButton("Ya"){dialog, which ->
                    AndroidNetworking.post(url + "delete_petugas.php")
                        .addBodyParameter("id_petugas", detail?.id_petugas)
                        .addBodyParameter("status", "selesai")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) {
                                if (response.get("response") == "success"){
                                    Toast.makeText(applicationContext, "Petugas berhasil dihapus", Toast.LENGTH_SHORT).show()
                                    Toast.makeText(applicationContext, "Silahkan refresh", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, "Gagal menghapus petugas", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onError(error: ANError) {
                                Toast.makeText(applicationContext, "Connection error!!!", Toast.LENGTH_SHORT).show()
                                Log.e("error1", error.errorBody)
                                Log.e("error2", error.errorCode.toString())
                                Log.e("error3", error.errorDetail)
                            }
                        })
                }
                .setNegativeButton("Tidak"){dialog, which ->
                    dialog.cancel()
                }
            alertDialog.create().show()
        }
        return super.onOptionsItemSelected(item)
    }
}
