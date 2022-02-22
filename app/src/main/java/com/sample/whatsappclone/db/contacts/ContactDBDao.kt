package com.sample.whatsappclone.db.contacts

//import android.arch.persistence.room.Dao
//import android.arch.persistence.room.Insert
//import android.arch.persistence.room.Query
import androidx.room.*
import com.sample.whatsappclone.models.Contact
import io.reactivex.Completable
import io.reactivex.Flowable


import io.reactivex.Maybe


@Dao
interface ContactDBDao {

    @Query("SELECT * FROM Contact")
    fun getAllContacts(): Flowable<List<Contact>>

    @Query( "DELETE FROM Contact")
    fun deleteAllContacts(): Completable

    @Insert()
    fun insertAllContacts(users: List<Contact>): Maybe<List<Long>>

}