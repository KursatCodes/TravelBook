package com.muhammedkursatgokgun.mymaps.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.muhammedkursatgokgun.mymaps.model.Place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao// data access object veriye erişim objesi
interface PlaceDao {

    //@Query("SELECT * FROM Place WHERE id= :idd")  filtreleme yapmak istersek
    //fun getAll(idd: Int): List<Place>             böyle yapıyoruz
    @Query("SELECT * FROM Place")
    fun getAll(): Flowable<List<Place>>

    @Insert
    fun insert(place: Place) : Completable

    @Delete
    fun delete(place: Place) : Completable



}