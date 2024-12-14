package com.dev4life.imagelabeling.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev4life.imagelabeling.data.labels.LabelItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelsDao {
    @Query("SELECT * FROM labels ORDER BY labelName ASC")
    fun getAllLabels(): Flow<List<LabelItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: LabelItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabels(labelsList: List<LabelItem>)

    @Query("DELETE FROM labels WHERE lId IN (:ids)")
    suspend fun deleteLabelsByIds(ids: List<Long>)
}