package com.example.data.sample

import com.example.data.model.FridayMessage
import com.example.data.model.FridaySchedule
import com.example.data.model.RecipientContact

object DefaultData {
    val defaultSchedule = FridaySchedule(
        id = 1,
        isEnabled = true,
        hour = 9,
        minute = 0,
        selectedMessageId = 1,
        selectedImageKey = "mosque_sunset",
        mergeTextOnImage = true,
        senderSignature = "",
        autoRemindVibrate = true
    )

    val sampleRecipients = listOf(
        RecipientContact(name = "Babam", phoneNumber = "+905551112233", groupName = "Aile", isSelected = true),
        RecipientContact(name = "Annem", phoneNumber = "+905552223344", groupName = "Aile", isSelected = true),
        RecipientContact(name = "Kardeşim", phoneNumber = "+905553334455", groupName = "Aile", isSelected = true),
        RecipientContact(name = "Ahmet Amca", phoneNumber = "+905554445566", groupName = "Akrabalar", isSelected = true),
        RecipientContact(name = "Mehmet Arkadaşım", phoneNumber = "+905555556677", groupName = "Arkadaşlar", isSelected = true)
    )

    val defaultMessages = listOf(
        // Ayet & Hadis
        FridayMessage(
            title = "Cuma Suresi 9. Ayet",
            content = "“Ey iman edenler! Cuma günü namaza çağrıldığınız zaman hemen Allah'ı anmaya koşun ve alışverişi bırakın. Eğer bilirseniz bu sizin için daha hayırlıdır.” (Cuma Suresi, 9)\n\nHayırlı ve bereketli Cumalar dilerim.",
            category = "Ayet & Hadis",
            isFavorite = true
        ),
        FridayMessage(
            title = "En Hayırlı Gün",
            content = "Resulullah (s.a.v.) buyurdu: “Üzerine güneşin doğduğu en hayırlı gün Cuma günüdür.”\n\nRabbim bu mübarek günün hürmetine gönlünüzdeki hayırlı duaları kabul eylesin. Hayırlı Cumalar {isim}!",
            category = "Ayet & Hadis",
            isFavorite = true
        ),
        FridayMessage(
            title = "Salavat ve Mağfiret",
            content = "“Cuma gününde bir saat vardır ki, Müslüman bir kul o saatte Allah'tan bir hayır isterse, Allah ona mutlaka verir.” (Buhârî)\n\nDualarımızın kabul olması dileğiyle, hayırlı nurlu Cumalar.",
            category = "Ayet & Hadis"
        ),

        // Dualar & Bereket
        FridayMessage(
            title = "Huzur ve Sağlık Duası",
            content = "Allah'ım! Kalbimize ferahlık, hanemize huzur ve bereket, bedenimize sağlık, ömrümüze afiyet ihsan eyle. Bizleri sevdiklerimizle cennette buluştur.\n\nMübarek Cuma gününüz hayırlara vesile olsun.",
            category = "Dualar & Bereket",
            isFavorite = true
        ),
        FridayMessage(
            title = "Gönül Ferahlığı",
            content = "Rabbim, ömrümüzü bereketli, amellerimizi ihlaslı, dualarımızı kabul eylesin. Dertlerimize deva, gönüllerimize şifa nasip etsin.\n\nHayırlı ve nurlu Cumalar dilerim {isim}.",
            category = "Dualar & Bereket"
        ),
        FridayMessage(
            title = "Tevbe ve Rahmet Kapısı",
            content = "Ya Rabbi! Günahlarımızı bağışla, bizi doğru yoldan ayırma, sevdiklerimizi muhafaza eyle. Kapına gelenleri boş çevirme Allah'ım.\n\nCumanız mübarek olsun.",
            category = "Dualar & Bereket"
        ),

        // Samimi & Kısa
        FridayMessage(
            title = "Samimi ve Nurlu",
            content = "Gönlünüzden geçen her hayırlı duanın kabul olması dileğiyle... Hayırlı, huzurlu ve bereketli Cumalar!",
            category = "Samimi & Kısa",
            isFavorite = true
        ),
        FridayMessage(
            title = "Kısa ve İçten",
            content = "Rabbim gününüzü aydın, hanenizi bereketli, dualarınızı kabul eylesin. Hayırlı Cumalar {isim} kardeşim.",
            category = "Samimi & Kısa"
        ),
        FridayMessage(
            title = "Sevgi ve Selam",
            content = "Bu mübarek Cuma gününün size ve ailenize sağlık, mutluluk ve huzur getirmesini dilerim. Selam ve dua ile, Hayırlı Cumalar.",
            category = "Samimi & Kısa"
        ),

        // Akraba & Büyükler
        FridayMessage(
            title = "Büyüklerimize Saygı ve Dua",
            content = "Hürmet ve sevgilerimle ellerinizden öperim. Rabbim başımızdan eksik etmesin, sağlıklı ve hayırlı ömürler versin.\n\nMübarek Cuma gününüz kutlu olsun {isim}.",
            category = "Akraba & Büyükler",
            isFavorite = true
        ),
        FridayMessage(
            title = "Aile ve Sıla-i Rahim",
            content = "Gönül bağıyla bağlı olduğumuz tüm sevdiklerimize selam olsun. Cuma günümüzün feyzi ve bereketi üzerinize olsun.\n\nHayırlı Cumalar.",
            category = "Akraba & Büyükler"
        ),

        // Özel Cuma Kartı Metinleri
        FridayMessage(
            title = "Zarif Cuma Tebriki",
            content = "Kalpler taşlaşmasın, gözler yaştan, diller duadan ayrılmasın. Cuma günümüzün manevi iklimi yüreğinizi ferahlatsın.\n\nHayırlı Cumalar.",
            category = "Özel"
        )
    )

    data class MosqueImageItem(
        val key: String,
        val title: String,
        val drawableResId: Int?,
        val previewUrl: String? = null,
        val description: String
    )
}
