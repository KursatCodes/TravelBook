package com.muhammedkursatgokgun.mymaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.muhammedkursatgokgun.mymaps.adapter.PlaceAdapter
import com.muhammedkursatgokgun.mymaps.databinding.ActivityMainBinding
import com.muhammedkursatgokgun.mymaps.model.Place
import com.muhammedkursatgokgun.mymaps.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val compositeDisposable= CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val db= Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        val placeDao= db.placeDao()

        compositeDisposable.add(
            placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // görüntüle
                .subscribe(this::handleResponse)
        )
    }
    private fun handleResponse(placeList: List<Place>){
        binding.recycleView.layoutManager = LinearLayoutManager(this)
        val adapter = PlaceAdapter(placeList)
        binding.recycleView.adapter= adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.placemenuj,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_place){
            val intent= Intent(this@MainActivity,MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}