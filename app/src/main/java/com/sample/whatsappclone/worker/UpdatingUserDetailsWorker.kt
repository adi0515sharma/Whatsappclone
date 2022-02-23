package com.sample.whatsappclone.worker

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import com.sample.whatsappclone.api.Fetching_api
import com.sample.whatsappclone.api.repository.ContactViewModel
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.contacts.ContactDBDao
import com.sample.whatsappclone.db.contacts.ContactDBRepo
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.models.Normal.User_status
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class UpdatingUserDetailsWorker constructor(var context : Context, params:WorkerParameters) : Worker(context,params) {
    lateinit var database : WhatsappDataBase
    lateinit var repository : ContactViewModel
    override fun doWork(): Result {

        Log.e("UpdatingUserDetails", "start1")

        database = WhatsappDataBase.getDatabase(this.context) as WhatsappDataBase
        repository = ContactViewModel(ContactDBRepo(database.getContactDBDao() as ContactDBDao))

        if(!fetchPhoneContacts()){
            return Result.failure()
        }
        return Result.success()
    }

    public fun fetchPhoneContacts() : Boolean{
        try{
            var basicdetail : ArrayList<Contact> = doSomethingForEachUniquePhoneNumber(context)

            var phone : Array<String> = arrayOfNulls<String>(basicdetail.size) as Array<String>
            var i : Int = 0
            basicdetail.forEach {
                if(it.phone_number.length>=10){
                    if(!it.phone_number.startsWith("+91")){
                        phone.set(i, "+91${it.phone_number}")
                    }
                    else{
                        phone.set(i, "${it.phone_number}")
                    }
                }
                else{
                    phone.set(i, "${it.phone_number}")
                }
                i++
            }

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit : Retrofit = Retrofit.Builder().baseUrl("http://192.168.0.107/whatsapp/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            val api_status : Flowable<ArrayList<User_status>> =
                retrofit.create(Fetching_api::class.java)
                    .fetch_user_status(phone)
                    .subscribeOn(Schedulers.io())
                    .doOnError { it -> Log.e("MainActivityViewModel1", "${it.message}") };


            val other_detail : Flowable<ArrayList<Contact>> = Flowable.fromArray(basicdetail).doOnError{
                    it -> Log.e("MainActivityViewModel2", "${it.message}")
            }.subscribeOn(Schedulers.io())
                .doOnError { it -> Log.e("MainActivityViewModel3", "${it.message}") };

            var g  = Flowable.zip(api_status, other_detail, BiFunction<ArrayList<User_status>, ArrayList<Contact>, ArrayList<Contact>>
            { cricketFans, footballFans ->
                // here we get both the results at a time.
                var f : ArrayList<Contact> = arrayListOf<Contact>()
                for(i in 0..(cricketFans.size-1)){
                    var c : Contact = Contact()
                    c.name = footballFans.get(i).name;
                    c.phone_number = footballFans.get(i).phone_number
                    c.exist = cricketFans.get(i).u_exist
                    c.icon = cricketFans.get(i).u_photo
                    c.u_id_no = Integer.parseInt(cricketFans.get(i).u_id_no)
                    f.add(c)
                    Log.e("UpdatingUserDetails", "start2")

                }
                return@BiFunction f
            }).subscribeOn(Schedulers.io())
                .doOnError { it -> Log.e("MainActivityViewModel4", "${it.message}") }
                .doOnComplete {

                }.subscribe { it ->
                    run {
                        repository.deleteRecord()
                            .doOnError {
                                Log.e("MainActivityViewModel5",it.message.toString())
                            }.subscribe {}
                        if(it.size>0){
                            repository.insertRecords(it)
                        }
                    }
                }
            Log.e("UpdatingUserDetails", "start3")

        }
        catch (e : Exception){
            return false
        }
        return true
    }

    private fun doSomethingForEachUniquePhoneNumber(context: Context) : ArrayList<Contact>{
        var PhoneContacts2 : ArrayList<Contact> = ArrayList<Contact>()
        val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
            )
        var cursor: Cursor? = null
        try {
                cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                )
            } catch (e: SecurityException) {
                //SecurityException can be thrown if we don't have the right permissions
            }
        if (cursor != null) {
                try {
                    val normalizedNumbersAlreadyFound: HashSet<String> = HashSet()
                    val indexOfNormalizedNumber =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                    val indexOfDisplayName =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val indexOfDisplayNumber =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (cursor.moveToNext()) {
                        val normalizedNumber = cursor.getString(indexOfNormalizedNumber)
                        if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                            val displayName = cursor.getString(indexOfDisplayName)
                            val displayNumber = cursor.getString(indexOfDisplayNumber)
                            var c : Contact = Contact()
                            c.name = displayName;
                            c.phone_number = displayNumber
                                .replace(" ","")
                                .replace("(","")
                                .replace(")","")
                                .replace("-","")
                            c.exist =""
                            c.icon = ""
                            PhoneContacts2.add(c)
                            //haven't seen this number yet: do something with this contact!
                        } else {
                            //don't do anything with this contact because we've already found this number
                        }
                    }
                } finally {
                    cursor.close()
                }
            }
        Log.e("UpdatingUserDetails", "start4")

        return PhoneContacts2
    }


}