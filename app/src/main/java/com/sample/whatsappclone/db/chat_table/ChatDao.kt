package com.sample.whatsappclone.db.chat_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.models.MediaResponse.FetchingCountOfNotSeenMessageToHomeScreen
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable


@Dao
interface ChatDao {

    @Insert()
    fun insertChat(users: Chat): Maybe<Long>

    @Query("SELECT * FROM Chat WHERE (from_u_id_no = :other_id OR to_u_id_no = :other_id) ORDER BY message_time ASC")
    fun fetchAllChatOfUser(other_id: Int): Flowable<List<Chat>>

    @Query("SELECT COUNT(*) FROM Chat WHERE (from_u_id_no = :other_id OR to_u_id_no = :other_id) AND message_seen = 'no'")
    fun fetchTotalNotSeen(other_id: Int): Observable<Integer>

    @Query("SELECT COUNT(*) as total_not_seen, from_u_id_no FROM Chat WHERE message_seen = 'no' GROUP BY from_u_id_no")
    fun fetchTotalNotSeenMessageCount(): Flowable<List<FetchingCountOfNotSeenMessageToHomeScreen>>

    @Query("SELECT COUNT(*) as total_not_seen, from_u_id_no FROM Chat WHERE from_u_id_no = :from_u_id_no")
    fun fetchTotalNotSeenMessageForSpecCount(from_u_id_no : Int): Flowable<List<FetchingCountOfNotSeenMessageToHomeScreen>>

    @Query("Update Chat SET message_seen = 'yes' WHERE from_u_id_no = :from_u_id_no ")
    fun setSeenToMessage(from_u_id_no : Int): Completable
}