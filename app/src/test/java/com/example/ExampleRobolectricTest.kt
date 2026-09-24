package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.AlarmScheduler
import com.example.util.WhatsAppSender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Cuma Mesajları", appName)
  }

  @Test
  fun `test phone number cleanup for whatsapp`() {
    val raw1 = "+90 (555) 123-45-67"
    assertEquals("905551234567", WhatsAppSender.cleanPhoneNumber(raw1))

    val raw2 = "0555 987 65 43"
    assertEquals("905559876543", WhatsAppSender.cleanPhoneNumber(raw2))

    val raw3 = "5551112233"
    assertEquals("905551112233", WhatsAppSender.cleanPhoneNumber(raw3))
  }

  @Test
  fun `test alarm scheduler calculates future friday`() {
    val targetMillis = AlarmScheduler.calculateNextFridayMillis(9, 0)
    assertTrue("Target millis should be in the future", targetMillis > System.currentTimeMillis())

    val cal = Calendar.getInstance().apply {
      timeInMillis = targetMillis
    }
    assertEquals(Calendar.FRIDAY, cal.get(Calendar.DAY_OF_WEEK))
    assertEquals(9, cal.get(Calendar.HOUR_OF_DAY))
    assertEquals(0, cal.get(Calendar.MINUTE))
  }
}
