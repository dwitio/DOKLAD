package tgs.app.pengaduanadmin.petugas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_beranda.*
import tgs.app.pengaduanadmin.MainActivity
import tgs.app.pengaduanadmin.R
import tgs.app.pengaduanadmin.profil.ProfilActivity
import tgs.app.pengaduanadmin.profil.ProfilActivity.Companion.idProfil
import tgs.app.pengaduanadmin.profil.ProfilActivity.Companion.passProfil
import tgs.app.pengaduanadmin.tanggapan.BeriTanggapanActivity
import tgs.app.pengaduanadmin.tanggapan.BeriTanggapanActivity.Companion.idPetugass

class BerandaPetugasActivity : AppCompatActivity() {

    companion object {
        var id: String = "ID"
        var password: String = "PASS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda_petugas)

        val idProfils = intent.extras?.getString(id)
        val passwordProfil = intent.extras?.getString(password)

        tanggapan.setOnClickListener {
            startActivity(Intent(this, BeriTanggapanActivity::class.java).putExtra(idPetugass, idProfils))
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
