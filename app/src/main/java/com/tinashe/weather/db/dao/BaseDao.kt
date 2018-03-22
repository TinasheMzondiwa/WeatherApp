
package com.tinashe.weather.db.dao


import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy

interface BaseDao<in T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg items: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: T)

    @Delete
    fun delete(item: T)
}
