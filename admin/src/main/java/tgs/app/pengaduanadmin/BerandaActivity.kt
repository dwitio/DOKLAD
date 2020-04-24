package tgs.app.pengaduanadmin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_beranda.*
import org.json.JSONException
import org.json.JSONObject
import tgs.app.common.Config.Companion.url
import tgs.app.pengaduanadmin.akunPetugas.AkunPetugasActivity
import tgs.app.pengaduanadmin.pojo.Pengaduan
import tgs.app.pengaduanadmin.profil.ProfilActivity
import tgs.app.pengaduanadmin.profil.ProfilActivity.Companion.idProfil
import tgs.app.pengaduanadmin.profil.ProfilActivity.Companion.passProfil
import tgs.app.pengaduanadmin.tanggapan.BeriTanggapanActivity
import tgs.app.pengaduanadmin.tanggapan.BeriTanggapanActivity.Companion.idPetugass
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class BerandaActivity : AppCompatActivity() {

    companion object {
        var id: String = "ID"
        var password: String = "PASS"
    }

    private val STORAGE_PERMISSION_CODE = 1

    private lateinit var pdfFile: File
    var pengaduan = mutableListOf<Pengaduan>()

    private var table: PdfPTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        val idProfils = intent.extras?.getString(id)
        val passwordProfil = intent.extras?.getString(password)

        table = PdfPTable(floatArrayOf(3f, 3f, 3f, 3f, 3f))
        read_pengaduan()
        tanggapan.setOnClickListener {
            startActivity(Intent(this, BeriTanggapanActivity::class.java).putExtra(idPetugass, idProfils))
        }

        akun_petugas.setOnClickListener {
            startActivity(Intent(this, AkunPetugasActivity::class.java))
        }

        laporan.setOnClickListener {
            read_pengaduan()
            generateLaporan()
        }

        akun.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java).putExtra(idProfil, idProfils).putExtra(passProfil, passwordProfil))
        }

        keluar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        supportActionBar?.title = "DOKLAD!"
    }

//    override fun onResume() {
//        super.onResume()
//        read_pengaduan()
//    }

    private fun generateLaporan() {
        if (ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            createPdf()
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("The permission is needed because for generate report to pdf")
                .setPositiveButton("OK"){dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }
                .setNegativeButton("CANCEL"){dialog, which ->
                    dialog.dismiss()
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    private fun createPdf() {
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        if (!docsFolder.exists()) {
            docsFolder.mkdir()
            Log.i("TAG", "Created a new directory for PDF")
        }
        val pdfname = "Pengaduan.pdf"
        pdfFile = File(docsFolder.absolutePath, pdfname)
        val output: OutputStream = FileOutputStream(pdfFile)
        val document = Document(PageSize.A4)
        table?.defaultCell?.horizontalAlignment = Element.ALIGN_CENTER
        table?.defaultCell?.setPadding(10f)
        table?.totalWidth = PageSize.A4.width
        table?.widthPercentage = 100f
        table?.defaultCell?.verticalAlignment = Element.ALIGN_MIDDLE
        table?.addCell("Tanggal")
        table?.addCell("Nama")
        table?.addCell("Judul Laporan")
        table?.addCell("Isi Laporan")
        table?.addCell("Status")
        table?.headerRows = 1
        table?.flushContent()
        val cells = table?.getRow(0)?.cells
        for (j in cells!!.indices) {
            cells[j].backgroundColor = BaseColor.GRAY
//            cells[j].column.alignment = Element.ALIGN_CENTER
            cells[j].fixedHeight = 50f
        }
        for (i in pengaduan.indices) {
            table?.addCell(pengaduan[i].tgl_pengaduan.toString())
            table?.addCell(pengaduan[i].nama.toString())
            table?.addCell(pengaduan[i].judul.toString())
            table?.addCell(pengaduan[i].isi_laporan.toString())
            table?.addCell(pengaduan[i].status.toString())
        }
        PdfWriter.getInstance(document, output)
        document.open()
        val f = Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.NORMAL, BaseColor.BLACK)
        val g = Font(Font.FontFamily.TIMES_ROMAN, 23.0f, Font.NORMAL, BaseColor.BLACK)
        val h = Font(Font.FontFamily.TIMES_ROMAN, 18.0f, Font.NORMAL, BaseColor.BLACK)
        val paragraph1: Paragraph
        val paragraph2: Paragraph
        val paragraph3: Paragraph
        paragraph1 = Paragraph("PEMERINTAH KOTA SUKABUMI \n", f)
        paragraph2 = Paragraph("KEL. CIBEUREUM HILIR KEC. CIBEUREUM \n", g)
        paragraph3 = Paragraph("Jalan Parahita Nugraha Kode Pos 43165 \n\n", h)
        paragraph1.alignment = Element.ALIGN_CENTER
        paragraph2.alignment = Element.ALIGN_CENTER
        paragraph3.alignment = Element.ALIGN_CENTER
        document.add(paragraph1)
        document.add(paragraph2)
        document.add(paragraph3)
        document.add(table)
        document.close()
        pengaduan.clear()
        previewPdf()
    }

    private fun read_pengaduan() {
        AndroidNetworking.get(url + "read_pengaduan.php")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val jsonArray = response.getJSONArray("hasil")
                        for (i in 0 until jsonArray.length()) {
                            val responses = jsonArray.getJSONObject(i)
                            pengaduan.add(
                                Pengaduan(
                                    responses.getString("id_pengaduan"),
                                    responses.getString("tgl_pengaduan"),
                                    responses.getString("nama"),
                                    responses.getString("judul"),
                                    responses.getString("isi_laporan"),
                                    responses.getString("foto"),
                                    responses.getString("status")
                                )
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(error: ANError) {
                    Toast.makeText(applicationContext, "Connection error!!!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun previewPdf() {
        val packageManager = applicationContext!!.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list: List<*> = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isNotEmpty()) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val uri = FileProvider.getUriForFile(applicationContext, applicationContext.packageName.toString() + ".provider", pdfFile!!)
            intent.setDataAndType(uri, "application/pdf")
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show()
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
