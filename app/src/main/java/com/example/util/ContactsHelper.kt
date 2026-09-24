package com.example.util

import android.content.Context
import android.provider.ContactsContract
import com.example.data.model.RecipientContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactsHelper {

    suspend fun fetchPhoneContacts(context: Context): List<RecipientContact> = withContext(Dispatchers.IO) {
        val contactsList = mutableListOf<RecipientContact>()
        val seenNumbers = mutableSetOf<String>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = it.getString(nameIndex) ?: "Kayıtsız İsim"
                    val rawNumber = it.getString(numberIndex) ?: ""
                    val cleaned = WhatsAppSender.cleanPhoneNumber(rawNumber)

                    if (cleaned.isNotBlank() && !seenNumbers.contains(cleaned)) {
                        seenNumbers.add(cleaned)
                        contactsList.add(
                            RecipientContact(
                                name = name,
                                phoneNumber = rawNumber,
                                groupName = "Rehber",
                                isSelected = true
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        contactsList
    }
}
