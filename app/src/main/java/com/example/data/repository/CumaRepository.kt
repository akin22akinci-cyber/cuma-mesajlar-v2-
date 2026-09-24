package com.example.data.repository

import com.example.data.local.CumaDatabase
import com.example.data.model.FridayMessage
import com.example.data.model.FridaySchedule
import com.example.data.model.RecipientContact
import com.example.data.model.SentLog
import com.example.data.sample.DefaultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CumaRepository(private val database: CumaDatabase) {
    private val recipientDao = database.recipientDao()
    private val messageDao = database.fridayMessageDao()
    private val scheduleDao = database.fridayScheduleDao()
    private val sentLogDao = database.sentLogDao()

    suspend fun initializeDefaultsIfNeeded() {
        if (messageDao.getMessageCount() == 0) {
            messageDao.insertMessages(DefaultData.defaultMessages)
        }
        if (scheduleDao.getSchedule() == null) {
            scheduleDao.setSchedule(DefaultData.defaultSchedule)
        }
        val currentRecipients = recipientDao.getSelectedRecipientsList()
        if (currentRecipients.isEmpty()) {
            val all = recipientDao.getAllRecipients().firstOrNull() ?: emptyList()
            if (all.isEmpty()) {
                recipientDao.insertRecipients(DefaultData.sampleRecipients)
            }
        }
    }

    // Recipients
    fun getAllRecipients(): Flow<List<RecipientContact>> = recipientDao.getAllRecipients()
    fun getSelectedRecipients(): Flow<List<RecipientContact>> = recipientDao.getSelectedRecipients()
    suspend fun getSelectedRecipientsList(): List<RecipientContact> = recipientDao.getSelectedRecipientsList()

    suspend fun addRecipient(recipient: RecipientContact): Long = recipientDao.insertRecipient(recipient)
    suspend fun addRecipients(recipients: List<RecipientContact>) = recipientDao.insertRecipients(recipients)
    suspend fun updateRecipient(recipient: RecipientContact) = recipientDao.updateRecipient(recipient)
    suspend fun toggleRecipientSelection(id: Long, isSelected: Boolean) = recipientDao.updateSelection(id, isSelected)
    suspend fun selectAllRecipients(select: Boolean) = recipientDao.updateAllSelection(select)
    suspend fun deleteRecipient(recipient: RecipientContact) = recipientDao.deleteRecipient(recipient)
    suspend fun markContactSent(id: Long) = recipientDao.updateLastSent(id, System.currentTimeMillis())

    // Messages
    fun getAllMessages(): Flow<List<FridayMessage>> = messageDao.getAllMessages()
    fun getMessagesByCategory(category: String): Flow<List<FridayMessage>> = messageDao.getMessagesByCategory(category)
    suspend fun getMessageById(id: Long): FridayMessage? = messageDao.getMessageById(id)
    suspend fun addMessage(message: FridayMessage): Long = messageDao.insertMessage(message)
    suspend fun updateMessage(message: FridayMessage) = messageDao.updateMessage(message)
    suspend fun deleteMessage(message: FridayMessage) = messageDao.deleteMessage(message)
    suspend fun toggleMessageFavorite(id: Long, isFavorite: Boolean) = messageDao.updateFavorite(id, isFavorite)

    // Schedule
    fun getScheduleFlow(): Flow<FridaySchedule?> = scheduleDao.getScheduleFlow()
    suspend fun getSchedule(): FridaySchedule? = scheduleDao.getSchedule()
    suspend fun updateSchedule(schedule: FridaySchedule) = scheduleDao.updateSchedule(schedule)

    // Sent Logs
    fun getSentLogs(): Flow<List<SentLog>> = sentLogDao.getAllLogs()
    suspend fun logSent(contactName: String, phoneNumber: String, messageSnippet: String, isSuccess: Boolean = true) {
        sentLogDao.insertLog(
            SentLog(
                contactName = contactName,
                phoneNumber = phoneNumber,
                messageSnippet = messageSnippet.take(120),
                isSuccess = isSuccess
            )
        )
    }
}
