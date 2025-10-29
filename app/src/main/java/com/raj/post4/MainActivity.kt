package com.raj.post4

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.raj.post4.databinding.ActivityMainBinding
import kotlin.text.insert

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabaseWarga

    private lateinit var wargaDao: WargaDao
    private lateinit var appExecutor: AppExecutor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabaseWarga.getDatabase(this)
        appExecutor = AppExecutor()
        wargaDao = database.wargaDao()

        // Spinner status pernikahan
        val statusOptions = arrayOf("Belum Menikah", "Menikah", "Cerai")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter

        // Tombol Simpan
        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val nik = binding.etNIK.text.toString().trim()
            val kabupaten = binding.etKabupaten.text.toString().trim()
            val kecamatan = binding.etKecamatan.text.toString().trim()
            val desa = binding.etDesa.text.toString().trim()
            val rt = binding.etRT.text.toString().trim()
            val rw = binding.etRW.text.toString().trim()
            val selectedGenderId = binding.rgGender.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                binding.rbLaki.id -> "Laki-Laki"
                binding.rbPerempuan.id -> "Perempuan"
                else -> ""
            }

            val status = binding.spinnerStatus.selectedItem.toString()

            // 🔍 Validasi input
            if (nama.isEmpty() || nik.isEmpty() || kabupaten.isEmpty() ||
                kecamatan.isEmpty() || desa.isEmpty() ||
                rt.isEmpty() || rw.isEmpty() || gender.isEmpty()
            ) {
                Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val alamatLengkap = "RT $rt/RW $rw, $desa, $kecamatan, $kabupaten"

            val warga = Warga(
                nama = nama,
                nik = nik,
                alamat = alamatLengkap,
                statusPernikahan = "$gender - $status"
            )

            appExecutor.diskIO.execute {
                database.wargaDao().insert(warga)
                tampilkanData()
            }

            clearForm()
            Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
        }

        // Tombol Reset
        binding.btnReset.setOnClickListener {
            appExecutor.diskIO.execute {
                database.wargaDao().deleteAll()
                runOnUiThread {
                    binding.tvDaftar.text = ""
                    Toast.makeText(this, "Semua data dihapus!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tampilkanData()
    }

    private fun tampilkanData() {
        appExecutor.diskIO.execute {
            val daftarWarga = database.wargaDao().getAll()
            val builder = StringBuilder()
            for ((index, warga) in daftarWarga.withIndex()) {
                builder.append("${index + 1}. ${warga.nama} (${warga.statusPernikahan})\n")
                builder.append("NIK: ${warga.nik}\n")
                builder.append("Alamat: ${warga.alamat}\n\n")
            }
            runOnUiThread {
                binding.tvDaftar.text =
                    if (builder.isEmpty()) "Belum ada data warga." else builder.toString()
            }
        }
    }

    private fun clearForm() {
        binding.etNama.text.clear()
        binding.etNIK.text.clear()
        binding.etKabupaten.text.clear()
        binding.etKecamatan.text.clear()
        binding.etDesa.text.clear()
        binding.etRT.text.clear()
        binding.etRW.text.clear()
        binding.rgGender.clearCheck()
        binding.spinnerStatus.setSelection(0)
    }
}
