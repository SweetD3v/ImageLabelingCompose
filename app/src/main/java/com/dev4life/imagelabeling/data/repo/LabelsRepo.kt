package com.dev4life.imagelabeling.data.repo

import com.dev4life.imagelabeling.data.labels.LabelItem
import kotlinx.coroutines.flow.Flow

interface LabelsRepo {
    fun getAllLabels(): Flow<List<LabelItem>>
    suspend fun insertLabel(label: LabelItem)
    suspend fun insertLabels(labels: List<LabelItem>)
    suspend fun deleteLabels(labelIds: List<Long>)
}