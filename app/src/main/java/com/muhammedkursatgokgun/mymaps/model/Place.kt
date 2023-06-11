package com.muhammedkursatgokgun.mymaps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Place(
    @ColumnInfo(name= "name")
    var name: String,
    @ColumnInfo(name= "latitute")
    var latitute: Double,
    @ColumnInfo(name= "longitute")
    var longitute: Double) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id= 0
}