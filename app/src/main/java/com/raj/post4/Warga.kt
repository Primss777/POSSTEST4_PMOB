package com.raj.post4

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warga")
data class Warga(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val nik: String,
    val alamat: String,
    val statusPernikahan: String
)
