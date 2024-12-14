package com.dev4life.imagelabeling.data.impl

import com.dev4life.imagelabeling.data.dao.LabelsDao
import com.dev4life.imagelabeling.data.labels.LabelItem
import com.dev4life.imagelabeling.data.repo.LabelsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LabelsRepoImpl @Inject constructor(private val labelsDao: LabelsDao) : LabelsRepo {
    override fun getAllLabels(): Flow<List<LabelItem>> = labelsDao.getAllLabels()

    override suspend fun insertLabel(label: LabelItem) {
        labelsDao.insertLabel(label)
    }

    override suspend fun insertLabels(labels: List<LabelItem>) {
        labelsDao.insertLabels(labels)
    }

    override suspend fun deleteLabels(labelIds: List<Long>) {
        labelsDao.deleteLabelsByIds(labelIds)
    }
}