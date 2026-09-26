package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CumaApp
import com.example.data.model.FridayMessage
import com.example.data.model.FridaySchedule
import com.example.data.model.RecipientContact
import com.example.data.model.SentLog
import com.example.data.sample.DefaultData
import com.example.util.AlarmScheduler
import com.example.util.CardGenerator
import com.example.util.ContactsHelper
import com.example.util.PinterestHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SequentialSendStatus(
    val isActive: Boolean = false,
    val contacts: List<RecipientContact> = emptyList(),
    val currentIndex: Int = 0,
    val currentImageUri: Uri? = null,
    val currentMessage: String = ""
)

class CumaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as CumaApp).repository

    val recipients: StateFlow<List<RecipientContact>> = repository.getAllRecipients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedRecipients: StateFlow<List<RecipientContact>> = repository.getSelectedRecipients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messages: StateFlow<List<FridayMessage>> = repository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedule: StateFlow<FridaySchedule?> = repository.getScheduleFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultData.defaultSchedule)

    val sentLogs: StateFlow<List<SentLog>> = repository.getSentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow("Tümü")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _recipientFilterGroup = MutableStateFlow("Tümü")
    val recipientFilterGroup: StateFlow<String> = _recipientFilterGroup.asStateFlow()

    private val _previewCardUri = MutableStateFlow<Uri?>(null)
    val previewCardUri: StateFlow<Uri?> = _previewCardUri.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Sequential sender state
    private val _sequentialSendState = MutableStateFlow(SequentialSendStatus())
    val sequentialSendState: StateFlow<SequentialSendStatus> = _sequentialSendState.asStateFlow()

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setRecipientFilterGroup(group: String) {
        _recipientFilterGroup.value = group
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // Recipient Actions
    fun toggleRecipient(id: Long, isSelected: Boolean) {
        viewModelScope.launch {
            repository.toggleRecipientSelection(id, isSelected)
        }
    }

    fun selectAllRecipients(select: Boolean) {
        viewModelScope.launch {
            repository.selectAllRecipients(select)
        }
    }

    fun addRecipient(name: String, phoneNumber: String, groupName: String = "Aile") {
        viewModelScope.launch {
            if (name.isNotBlank() && phoneNumber.isNotBlank()) {
                repository.addRecipient(
                    RecipientContact(
                        name = name.trim(),
                        phoneNumber = phoneNumber.trim(),
                        groupName = groupName,
                        isSelected = true
                    )
                )
                _statusMessage.value = "$name rehbere eklendi."
            }
        }
    }

    fun deleteRecipient(recipient: RecipientContact) {
        viewModelScope.launch {
            repository.deleteRecipient(recipient)
        }
    }

    fun importPhoneContacts() {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                val contacts = ContactsHelper.fetchPhoneContacts(getApplication())
                if (contacts.isNotEmpty()) {
                    repository.addRecipients(contacts)
                    _statusMessage.value = "${contacts.size} kişi telefon rehberinden aktarıldı."
                } else {
                    _statusMessage.value = "Rehberde kişi bulunamadı veya izin verilmedi."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Kişiler alınamadı: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    // Message Actions
    fun addMessage(title: String, content: String, category: String = "Özel") {
        viewModelScope.launch {
            if (content.isNotBlank()) {
                val id = repository.addMessage(
                    FridayMessage(
                        title = title.ifBlank { "Özel Mesaj" },
                        content = content.trim(),
                        category = category,
                        isCustom = true
                    )
                )
                updateSelectedMessage(id)
                _statusMessage.value = "Yeni Cuma mesajı kaydedildi ve seçildi."
            }
        }
    }

    fun selectRandomMessage() {
        viewModelScope.launch {
            val list = messages.value
            if (list.isNotEmpty()) {
                val random = list.random()
                updateSelectedMessage(random.id)
                _statusMessage.value = "Bu Cuma için yeni bir mesaj seçildi: ${random.title}"
            }
        }
    }

    fun toggleMessageFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleMessageFavorite(id, isFavorite)
        }
    }

    fun deleteMessage(message: FridayMessage) {
        viewModelScope.launch {
            repository.deleteMessage(message)
        }
    }

    // Schedule & Visual Typography Actions
    fun updateScheduleTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            val updated = curr.copy(hour = hour, minute = minute)
            repository.updateSchedule(updated)
            if (updated.isEnabled) {
                AlarmScheduler.scheduleFridayReminder(getApplication(), hour, minute)
            }
            _statusMessage.value = "Zamanlayıcı güncellendi: ${AlarmScheduler.formatTime(hour, minute)}"
        }
    }

    fun toggleScheduleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            val updated = curr.copy(isEnabled = enabled)
            repository.updateSchedule(updated)
            if (enabled) {
                AlarmScheduler.scheduleFridayReminder(getApplication(), updated.hour, updated.minute)
                _statusMessage.value = "Cuma zamanlayıcısı aktif edildi."
            } else {
                AlarmScheduler.cancelReminder(getApplication())
                _statusMessage.value = "Cuma zamanlayıcısı duraklatıldı."
            }
        }
    }

    fun updateSelectedMessage(messageId: Long) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(selectedMessageId = messageId))
            generatePreviewCard()
        }
    }

    fun updateSelectedImageKey(imageKey: String) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(selectedImageKey = imageKey))
            generatePreviewCard()
        }
    }

    fun pickCustomPhotoUri(uri: Uri) {
        viewModelScope.launch {
            updateSelectedImageKey(uri.toString())
            _statusMessage.value = "Fotoğraf galerinizden başarıyla seçildi!"
        }
    }

    fun updateFontStyle(style: String) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(fontStyle = style))
            generatePreviewCard()
        }
    }

    fun updateFrameStyle(style: String) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(frameStyle = style))
            generatePreviewCard()
        }
    }

    fun updateTextSizeModifier(modifier: Float) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(textSizeModifier = modifier))
            generatePreviewCard()
        }
    }

    fun updateTextColorHex(hex: String) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(textColorHex = hex))
            generatePreviewCard()
        }
    }

    fun updateSenderSignature(signature: String) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(senderSignature = signature))
            generatePreviewCard()
        }
    }

    fun toggleMergeTextOnImage(merge: Boolean) {
        viewModelScope.launch {
            val curr = schedule.value ?: DefaultData.defaultSchedule
            repository.updateSchedule(curr.copy(mergeTextOnImage = merge))
        }
    }

    // Card Generation
    fun generatePreviewCard() {
        viewModelScope.launch {
            val sched = schedule.value ?: DefaultData.defaultSchedule
            val msg = messages.value.find { it.id == sched.selectedMessageId }
                ?: messages.value.firstOrNull()
                ?: return@launch

            val uri = CardGenerator.generateCardImage(
                context = getApplication(),
                imageKeyOrPath = sched.selectedImageKey,
                messageText = msg.content,
                signature = sched.senderSignature,
                recipientName = "",
                fontStyle = sched.fontStyle,
                frameStyle = sched.frameStyle,
                textSizeModifier = sched.textSizeModifier,
                textColorHex = sched.textColorHex
            )
            _previewCardUri.value = uri
        }
    }

    // Download Pinterest / Web Image
    fun downloadWebImage(url: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isProcessing.value = true
            val localPath = PinterestHelper.downloadImageFromUrl(getApplication(), url)
            _isProcessing.value = false
            if (localPath != null) {
                updateSelectedImageKey(localPath)
                _statusMessage.value = "Pinterest resmi başarıyla indirildi ve seçildi!"
                onComplete(true)
            } else {
                _statusMessage.value = "Resim indirilemedi. Lütfen geçerli bir Pinterest veya resim linki girin."
                onComplete(false)
            }
        }
    }

    // Sequential Queue Dispatcher
    fun startSequentialSend() {
        viewModelScope.launch {
            val targets = selectedRecipients.value
            if (targets.isEmpty()) {
                _statusMessage.value = "Lütfen önce mesaj gönderilecek kişileri seçin veya rehberden aktarın."
                return@launch
            }

            val sched = schedule.value ?: DefaultData.defaultSchedule
            val msg = messages.value.find { it.id == sched.selectedMessageId }
                ?: messages.value.firstOrNull()

            val rawMessage = msg?.content ?: "Hayırlı Cumalar dilerim."

            val cardUri = if (sched.mergeTextOnImage) {
                CardGenerator.generateCardImage(
                    context = getApplication(),
                    imageKeyOrPath = sched.selectedImageKey,
                    messageText = rawMessage,
                    signature = sched.senderSignature,
                    fontStyle = sched.fontStyle,
                    frameStyle = sched.frameStyle,
                    textSizeModifier = sched.textSizeModifier,
                    textColorHex = sched.textColorHex
                )
            } else null

            _sequentialSendState.value = SequentialSendStatus(
                isActive = true,
                contacts = targets,
                currentIndex = 0,
                currentImageUri = cardUri,
                currentMessage = rawMessage
            )
        }
    }

    fun advanceSequentialSend(isSent: Boolean = true) {
        val currState = _sequentialSendState.value
        if (!currState.isActive) return

        val currentContact = currState.contacts.getOrNull(currState.currentIndex)
        if (currentContact != null && isSent) {
            viewModelScope.launch {
                repository.markContactSent(currentContact.id)
                repository.logSent(
                    contactName = currentContact.name,
                    phoneNumber = currentContact.phoneNumber,
                    messageSnippet = currState.currentMessage
                )
            }
        }

        val nextIndex = currState.currentIndex + 1
        if (nextIndex < currState.contacts.size) {
            _sequentialSendState.value = currState.copy(currentIndex = nextIndex)
        } else {
            // Completed!
            _sequentialSendState.value = SequentialSendStatus(isActive = false)
            _statusMessage.value = "Tüm Cuma mesajları başarıyla iletildi! Allah kabul etsin. 🤲"
        }
    }

    fun cancelSequentialSend() {
        _sequentialSendState.value = SequentialSendStatus(isActive = false)
    }
}
