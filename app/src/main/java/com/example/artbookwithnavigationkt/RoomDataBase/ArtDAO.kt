package com.example.artbookwithnavigationkt.RoomDataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDAO {

    @Query("SELECT ArtName, id ,ArtImage FROM Art")
    fun getArtWithNameAndIdAndImage(): Flowable<List<Art>>

    @Query("SELECT * FROM Art WHERE id = :id")
    fun getArtById(id: Int): Flowable<Art>

    @Insert
    fun insert(art: Art): Completable

    @Delete
    fun delete(art: Art): Completable



}