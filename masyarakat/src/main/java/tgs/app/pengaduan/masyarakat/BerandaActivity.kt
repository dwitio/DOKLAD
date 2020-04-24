package tgs.app.pengaduan.masyarakat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_beranda.*
import tgs.app.pengaduan.MainActivity
import tgs.app.pengaduan.masyarakat.ProfilActivity.Companion.nikProfil
import tgs.app.pengaduan.masyarakat.ProfilActivity.Companion.passProfil
import tgs.app.pengaduan.R
import tgs.app.pengaduan.masyarakat.TambahDataActivity.Companion.nikk

class BerandaActivity : AppCompatActivity() {

    companion object {
        var nik: String = "NIK"
        var password: String = "PASS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        val niks = intent.extras?.get(nik).toString()
        val passwords = intent.extras?.get(password).toString()

        lapor.setOnClickListener {
            startActivity(Intent(this, TambahDataActivity::class.java).putExtra(nikk, niks))
        }

        tanggapan.setOnClickListener {
            startActivity(Intent(this, TanggapanActivity::class.java).putExtra(nikk, niks))
        }

        akun.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java).putExtra(nikProfil, niks).putExtra(passProfil, passwords))
        }

        keluar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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
