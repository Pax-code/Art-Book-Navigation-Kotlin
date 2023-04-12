package com.example.artbookwithnavigationkt.RoomDataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Art (
    @ColumnInfo(name = "ArtName")
    var artName : String,

    @ColumnInfo(name = "ArtistName")
    var artistName : String?,

    @ColumnInfo(name = "ArtDate")
    var artDate : String?,

    @ColumnInfo(name = "ArtImage")
    var artImage : ByteArray?,

    ){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}