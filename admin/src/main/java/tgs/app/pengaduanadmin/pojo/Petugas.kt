package tgs.app.pengaduanadmin.pojo

import android.os.Parcel
import android.os.Parcelable

data class Petugas (
    var id_petugas: String? = null,
    var nama_petugas: String? = null,
    var username: String? = null,
    var password: String? = null,
    var telp: String? = null,
    var level: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id_petugas)
        parcel.writeString(nama_petugas)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(telp)
        parcel.writeString(level)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Petugas> {
        override fun createFromParcel(parcel: Parcel): Petugas {
            return Petugas(parcel)
        }

        override fun newArray(size: Int): Array<Petugas?> {
            return arrayOfNulls(size)
        }
    }
}