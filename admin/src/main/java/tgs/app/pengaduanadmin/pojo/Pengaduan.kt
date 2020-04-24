package tgs.app.pengaduanadmin.pojo

import android.os.Parcel
import android.os.Parcelable

data class Pengaduan (
    var id_pengaduan: String? = null,
    var tgl_pengaduan: String? = null,
    var nama: String? = null,
    var judul: String? = null,
    var isi_laporan: String? = null,
    var foto: String? = null,
    var status: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id_pengaduan)
        parcel.writeString(tgl_pengaduan)
        parcel.writeString(nama)
        parcel.writeString(judul)
        parcel.writeString(isi_laporan)
        parcel.writeString(foto)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pengaduan> {
        override fun createFromParcel(parcel: Parcel): Pengaduan {
            return Pengaduan(parcel)
        }

        override fun newArray(size: Int): Array<Pengaduan?> {
            return arrayOfNulls(size)
        }
    }
}