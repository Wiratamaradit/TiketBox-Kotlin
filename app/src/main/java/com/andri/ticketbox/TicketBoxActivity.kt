package com.andri.ticketbox

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.core.widget.addTextChangedListener
import com.andri.ticketbox.databinding.ActivityTicketBoxBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TicketBoxActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTicketBoxBinding
    private var qtyTiketUngu  = 0L
    private var qtyTiketGeisha  = 0L
    private var qtyTiketSlank  = 0L
    private var hargaTiketUngu  = 250000L
    private var hargaTiketGeisha  = 300000L
    private var hargaTiketSlank  = 500000L
    private var lokasiKonser = arrayOf("Malang", "Jakarta", "Surabaya", "Bandung")
    private var lokasiSelected  = lokasiKonser[0]
    private var jenisIdentitasSelected  = ""
    private var totalPembayaran = 0L

    private var datePickerTglBeli : DatePickerDialog? = null
    private var datePickerTglBayar : DatePickerDialog? = null
    private var datePickerTglTf : DatePickerDialog? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Tiket Box"
        binding.apply {
            //Hidden layout yang disuruh
            llUngu.visibility = View.GONE
            llGeisha.visibility = View.GONE
            llSlank.visibility = View.GONE
            //set harga pada layout
            tvHargaUngu.text = "Harga Tiket : ${convertRupiahFormat(hargaTiketUngu)}"
            tvHargaGeisha.text = "Harga Tiket : ${convertRupiahFormat(hargaTiketGeisha)}"
            tvHargaSlank.text = "Harga Tiket :  ${convertRupiahFormat(hargaTiketSlank)}"
            //mendeteksi editext qty ungu di input
            etQtyUngu.addTextChangedListener {
                qtyTiketUngu = if (!etQtyUngu.text?.toString().isNullOrEmpty()) {
                    etQtyUngu.text?.toString()?.toLong() ?: 0L
                } else {
                    0L
                }
                hitungTotalBayar()
            }
            //mendeteksi editext qty ungu di input
            etQtyGeisha.addTextChangedListener {
                qtyTiketGeisha = if (!etQtyGeisha.text?.toString().isNullOrEmpty()) {
                    etQtyGeisha.text?.toString()?.toLong() ?: 0L
                } else {
                    0L
                }
                hitungTotalBayar()
            }
            //mendeteksi editext qty slank di input
            etQtySlank.addTextChangedListener {
                qtyTiketSlank = if (!etQtySlank.text?.toString().isNullOrEmpty()) {
                    etQtySlank.text?.toString()?.toLong() ?: 0L
                } else {
                    0L
                }
                hitungTotalBayar()
            }

            //initial spinner
            spLokasi.adapter = ArrayAdapter(this@TicketBoxActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lokasiKonser)
            spLokasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    lokasiSelected = lokasiKonser[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    lokasiSelected = ""
                }

            }

            //ketika tiket ungu di check
            cbUngu.setOnCheckedChangeListener { buttonView, isChecked ->
                llUngu.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked) {
                    qtyTiketUngu = 0L
                    hitungTotalBayar()
                }
            }
            //ketika tiket geisha di check
            cbGeisha.setOnCheckedChangeListener { buttonView, isChecked ->
                llGeisha.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked) {
                    qtyTiketSlank = 0L
                    hitungTotalBayar()
                }
            }
            //ketika tiket slank di check
            cbSlank.setOnCheckedChangeListener { buttonView, isChecked ->
                llSlank.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked) {
                    qtyTiketSlank = 0L
                    hitungTotalBayar()
                }
            }

            //ketika radio jenis identitas KTP dipilih
            rbKtp.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) setSelectedIdentitas(rbKtp.text.toString())
            }
            //ketika radio jenis identitas SIM dipilih
            rbSim.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) setSelectedIdentitas(rbSim.text.toString())
            }
            //ketika radio jenis identitas KARTU PELAJAR dipilih
            rbPelajar.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) setSelectedIdentitas(rbPelajar.text.toString())
            }
            //ketika radio jenis identitas PASPOR dipilih
            rbPaspor.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) setSelectedIdentitas(rbPaspor.text.toString())
            }

            //ketika radio Transfer Bank dipilih
            rbTf.setOnCheckedChangeListener { _, isChecked ->
                llTransferBank.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked) {
                    //reset data juga jika tidak dipilih
                    etJumlahTransfer.text = null
                    tvTglTransfer.text = null
                    etViaBank.text = null
                    etNoAc.text = null
                    etAtasNama.text = null
                }
            }

            //ketika radio Kartu Kredit dipilih
            rbCc.setOnCheckedChangeListener { _, isChecked ->
                llKartuKredit.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked) {
                    //reset data juga jika tidak dipilih
                    etJumlahBayar.text = null
                    tvTglBayar.text = null
                    etNamaCc.text = null
                    etAtasNamaCc.text = null
                }
            }

            //Pilih Tanggal Beli
            tvTglBeli.setOnClickListener {
                showDialogTglBeli()
            }
            //pilih tgl transfer
            tvTglTransfer.setOnClickListener {
                showDialogTglTransfer()
            }
            //pilih tgl bayar
            tvTglBayar.setOnClickListener {
                showDialogTglBayar()
            }

            //ketika tombol proses di klik
            btnProses.setOnClickListener {
                Intent(this@TicketBoxActivity, InformationActivity::class.java).also { intentData ->
                    intentData.putExtra("tgl_beli", tvTglBeli.text?.toString())
                    intentData.putExtra("qty_ungu", qtyTiketUngu)
                    intentData.putExtra("qty_geisha", qtyTiketUngu)
                    intentData.putExtra("qty_slank", qtyTiketSlank)
                    intentData.putExtra("lokasi", lokasiSelected)
                    intentData.putExtra("no_id", etNoId.text?.toString())
                    intentData.putExtra("jenis_identitas", jenisIdentitasSelected)
                    intentData.putExtra("nama_pembeli", etNamaPembeli.text?.toString())
                    intentData.putExtra("email_pembeli", etEmailPembeli.text?.toString())
                    intentData.putExtra("telp_pembeli", etNoTelpPembeli.text?.toString())
                    intentData.putExtra("alamat_pembeli", etAlamatPembeli.text?.toString())
                    intentData.putExtra("total_bayar", tvTotalPembayaran.text?.toString())
                    if (rbTf.isChecked) {
                        intentData.putExtra("metode_bayar", rbTf.text.toString())
                        intentData.putExtra("jumlah_transfer", etJumlahTransfer.text.toString())
                        intentData.putExtra("tgl_transfer", tvTglTransfer.text.toString())
                        intentData.putExtra("via_bank", etViaBank.text.toString())
                        intentData.putExtra("no_ac", etNoAc.text.toString())
                        intentData.putExtra("atas_nama", etAtasNama.text.toString())
                    }
                    if (rbCc.isChecked) {
                        intentData.putExtra("metode_bayar", rbCc.text.toString())
                        intentData.putExtra("nama_cc", etNamaCc.text.toString())
                        intentData.putExtra("jumlah_bayar", etJumlahBayar.text.toString())
                        intentData.putExtra("atas_nama", etAtasNama.text.toString())
                        intentData.putExtra("tgl_bayar", tvTglBayar.text.toString())
                    }
                    startActivity(intentData)
                }
            }


        }
    }

    private fun showDialogTglBeli() {
        //cek apakah dialog tampil atau tidak
        if (datePickerTglBeli?.isShowing == true) return
        //Calendar untuk mendapatkan tanggal sekarang
        val calendar = Calendar.getInstance(Locale("id","ID"))
        //Initiate DatePicker dialog
        datePickerTglBeli = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            //merubah calendar sesuai tanggal yang dipilih
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //mengubah format tanggal menjadi : Senin, 31 Februari 2023
            val sdf = SimpleDateFormat("EEEE, dd LLLL yyyy", Locale("id","ID"))
            // tampilkan dan simpan pilihan tanggal
            binding.tvTglBeli.text = sdf.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerTglBeli?.show()
    }

    private fun showDialogTglBayar() {
        //cek apakah dialog tampil atau tidak
        if (datePickerTglBayar?.isShowing == true) return
        //Calendar untuk mendapatkan tanggal sekarang
        val calendar = Calendar.getInstance(Locale("id","ID"))
        //Initiate DatePicker dialog
        datePickerTglBayar = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            //merubah calendar sesuai tanggal yang dipilih
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            //mengubah format tanggal menjadi : Senin, 31 Februari 2023
            val sdf = SimpleDateFormat("EEEE, dd LLLL yyyy", Locale("id","ID"))
            // tampilkan dan simpan pilihan tanggal
            binding.tvTglBayar.text = sdf.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerTglBayar?.show()
    }

    private fun showDialogTglTransfer() {
        //cek apakah dialog tampil atau tidak
        if (datePickerTglTf?.isShowing == true) return
        //Calendar untuk mendapatkan tanggal sekarang
        val calendar = Calendar.getInstance(Locale("id","ID"))
        //Initiate DatePicker dialog
        datePickerTglTf = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            //merubah calendar sesuai tanggal yang dipilih
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            //mengubah format tanggal menjadi : Senin, 31 Februari 2023
            val sdf = SimpleDateFormat("EEEE, dd LLLL yyyy", Locale("id","ID"))
            // tampilkan dan simpan pilihan tanggal
            binding.tvTglTransfer.text = sdf.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerTglTf?.show()
    }

    private fun hitungTotalBayar() {
        totalPembayaran = (qtyTiketUngu * hargaTiketUngu) + (qtyTiketGeisha * hargaTiketGeisha) + (qtyTiketSlank * hargaTiketSlank)
        binding.tvTotalPembayaran.text = convertRupiahFormat(totalPembayaran)
    }

    private fun setSelectedIdentitas(value : String) {
        jenisIdentitasSelected = value
    }

    private fun convertRupiahFormat(value : Number) : String {
        return "Rp. ${NumberFormat.getInstance().format(value)}.-"
    }
}