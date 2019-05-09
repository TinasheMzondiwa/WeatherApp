
package com.tinashe.weather.data.db.dao


import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Completable

interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: T): Completable

    @Delete
    fun delete(item: T): Completable
}
