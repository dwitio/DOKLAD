package tgs.app.pengaduan.masyarakat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_tambah_data.*
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduan.R
import java.io.ByteArrayOutputStream


class TambahDataActivity : AppCompatActivity() {

    companion object {
        var nikk: String = "NIKK"
    }

    private var encoded_string: String? = null
    private var bitmap: Bitmap? = null
    private var filename: String? = null

    private val STORAGE_PERMISSION_CODE = 1
    private val GALLERY_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_data)

        val niks = intent.extras?.get(nikk).toString()

        btn_tambah_foto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Please select...."), GALLERY_REQUEST_CODE)
            } else {
                requestStoragePermission()
            }
        }

        btn_kirim.setOnClickListener {
            val judul: String = edit_judul.text.toString()
            val deskripsi: String = edit_deskripsi.text.toString()
            if (judul.isEmpty() || deskripsi.isEmpty()){
                Toast.makeText(applicationContext, "Isi data dengan benar!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                AndroidNetworking.post(url + "create_pengaduan.php")
                    .addBodyParameter("nik", niks)
                    .addBodyParameter("judul", judul)
                    .addBodyParameter("isi_laporan", deskripsi)
                    .addBodyParameter("foto", encoded_string)
                    .addBodyParameter("status", "belum ditanggapi")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            if (response.get("response") == "success") {
                                Toast.makeText(applicationContext, "Sukses", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                            Log.e("error1", error.errorDetail)
                            Log.e("error2", error.errorBody)
                            Log.e("error3", error.errorCode.toString())
                        }
                    })
            }
        }
        supportActionBar?.title = "Lapor"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("The permission is needed because of it and that")
                .setPositiveButton("OK"){ _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }
                .setNegativeButton("CANCEL"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                //data.getData returns the content URI for the selected Image
                val fileUri = data?.data!!
                val fileee: String = data.data?.lastPathSegment!!
                filename = fileee.substring(fileee.lastIndexOf("/") + 1)
                img_adu.setImageURI(fileUri)
                img_adu.visibility = View.VISIBLE
                bitmap = fileUri.getCapturedImage(applicationContext)
                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                bitmap?.recycle()
                val array = stream.toByteArray()
                encoded_string = Base64.encodeToString(array, 0)
            }
        }
    }
    private fun Uri.getCapturedImage(context: Context): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source =
                context.contentResolver?.let { ImageDecoder.createSource(it, this) }
            source?.let { ImageDecoder.decodeBitmap(it) }
        } else {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                this
            )
        }
    }
}
