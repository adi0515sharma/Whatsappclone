package com.sample.whatsappclone.api.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.whatsappclone.db.contacts.ContactDBRepo
import com.sample.whatsappclone.models.Contact

import io.reactivex.Completable

class ContactViewModel(var repository: ContactDBRepo) : ViewModel(){



    fun fetchUpdatedRecords() : LiveData<List<Contact>>{
        repository.fetchUpdatedRecords()
        return repository.returnFetchUpdatedRecords()
    }
    fun deleteRecord() : Completable {
        return repository.deleteAllRecords()
    }
    fun insertRecords(records : List<Contact>){
        Log.e("size to be saved", "${records.size}")
        repository.insertAllRecords(records)
    }

    class WordViewModelFactory(private val repository: ContactDBRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
                return ContactViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}