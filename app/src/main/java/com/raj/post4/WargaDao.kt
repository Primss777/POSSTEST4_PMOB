package com.raj.post4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WargaDao {
    @Insert
    fun insert(warga: Warga)

    @Query("DELETE FROM warga")
    fun deleteAll()

    @Query("SELECT * FROM warga")
    fun getAll(): List<Warga>
}
