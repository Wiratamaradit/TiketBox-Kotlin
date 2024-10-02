package com.andri.ticketbox

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.andri.ticketbox.databinding.ActivityInformationBinding
import java.text.NumberFormat

class InformationActivity : AppCompatActivity() {
    lateinit var binding : ActivityInformationBinding

    private var hargaTiketUngu  = 250000L
    private var hargaTiketGeisha  = 300000L
    private var hargaTiketSlank  = 500000L
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Toolbar
        title = "Informasi Pembelian"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.apply {
            tvTglBeli.text = intent.getStringExtra("tgl_beli")
            val qtyUngu = intent.getLongExtra("qty_ungu", 0L)
            if (qtyUngu != 0L) {
                tvTiketUngu.text = "Tiket Ungu x$qtyUngu : ${convertRupiahFormat(qtyUngu * hargaTiketUngu)} "
            } else {
                tvTiketUngu.visibility = View.GONE
            }
            val qtyGeisha = intent.getLongExtra("qty_geisha", 0L)
            if (qtyGeisha != 0L) {
                tvTiketGeisha.text = "Tiket Ungu x$qtyGeisha : ${convertRupiahFormat(qtyGeisha * hargaTiketGeisha)} "
            } else {
                tvTiketGeisha.visibility = View.GONE
            }
            val qtySlank = intent.getLongExtra("qty_slank", 0L)
            if (qtySlank != 0L) {
                tvTiketSlank.text = "Tiket Ungu x$qtySlank : ${convertRupiahFormat(qtySlank * hargaTiketSlank)} "
            } else {
                tvTiketSlank.visibility = View.GONE
            }
            tvLokasi.text = intent.getStringExtra("lokasi")
            tvNoId.text = intent.getStringExtra("no_id")
            tvJenisIdentitas.text = intent.getStringExtra("jenis_identitas")
            tvNamaPembeli.text = intent.getStringExtra("nama_pembeli")
            tvEmailPembeli.text = intent.getStringExtra("email_pembeli")
            tvPhonePembeli.text = intent.getStringExtra("telp_pembeli")
            tvAlamatPembeli.text = intent.getStringExtra("alamat_pembeli")
            tvTotalPembayaran.text = intent.getStringExtra("total_bayar")
            val metodeBayar = intent.getStringExtra("metode_bayar")
            if (metodeBayar == "Transfer Bank") {
                llTransferBank.visibility = View.VISIBLE
                tvJmlTransfer.text = convertRupiahFormat(intent.getStringExtra("jumlah_transfer")?.toInt() ?: 0)
                tvTglTransfer.text = intent.getStringExtra("tgl_transfer")
                tvViaBank.text = intent.getStringExtra("via_bank")
                tvNoAc.text = intent.getStringExtra("no_ac")
                tvAtasNamaTf.text = intent.getStringExtra("atas_nama")
            } else {
                llKartuKredit.visibility = View.VISIBLE
                tvNamaCc.text = intent.getStringExtra("nama_cc")
                tvJmlBayar.text = convertRupiahFormat(intent.getStringExtra("jumlah_bayar")?.toInt() ?: 0)
                tvAtasNamaCc.text = intent.getStringExtra("atas_nama")
                tvTglBayar.text = intent.getStringExtra("tgl_bayar")
            }
        }
    }

    private fun convertRupiahFormat(value : Number) : String {
        return "Rp. ${NumberFormat.getInstance().format(value)}.-"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}