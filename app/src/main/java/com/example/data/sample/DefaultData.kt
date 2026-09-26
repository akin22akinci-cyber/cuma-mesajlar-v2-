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

    // Initially empty as requested by user
    val sampleRecipients = emptyList<RecipientContact>()

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
            title = "İcabet Saati Müjdesi",
            content = "“Cuma gününde bir saat vardır ki, Müslüman bir kul o saatte Allah'tan bir hayır dilerse, Allah ona mutlaka verir.” (Buhârî)\n\nDualarımızın makbul olduğu hayırlı, nurlu Cumalar.",
            category = "Ayet & Hadis",
            isFavorite = true
        ),
        FridayMessage(
            title = "Salavat ve Şefaat",
            content = "“Cuma günü bana çokça salavat getirin; zira sizin salavatınız bana arz olunur.” (Ebû Dâvûd)\n\nAllahümme salli alâ seyyidinâ Muhammed. Cumanız mübarek olsun.",
            category = "Ayet & Hadis"
        ),
        FridayMessage(
            title = "Mağfiret Müjdesi",
            content = "“Kim güzelce abdest alır, sonra Cuma namazına gelir, can kulağıyla dinler ve susarsa, iki Cuma arasındaki günahları bağışlanır.” (Müslim)\n\nHayırlı ve affa vesile Cumalar.",
            category = "Ayet & Hadis"
        ),

        // Dualar & Bereket
        FridayMessage(
            title = "Huzur ve Afiyet Duası",
            content = "Allah'ım! Kalbimize ferahlık, hanemize huzur ve bereket, bedenimize sağlık, ömrümüze afiyet ihsan eyle. Bizleri sevdiklerimizle cennette buluştur.\n\nMübarek Cuma gününüz hayırlara vesile olsun.",
            category = "Dualar & Bereket",
            isFavorite = true
        ),
        FridayMessage(
            title = "Gönül Şifası ve Genişlik",
            content = "Rabbim, ömrümüzü bereketli, amellerimizi ihlaslı, dualarımızı kabul eylesin. Dertlerimize deva, gönüllerimize şifa, hanelerimize neşe nasip etsin.\n\nHayırlı ve nurlu Cumalar dilerim {isim}.",
            category = "Dualar & Bereket",
            isFavorite = true
        ),
        FridayMessage(
            title = "Tevbe ve Rahmet Kapısı",
            content = "Ya Rabbi! Günahlarımızı bağışla, bizi doğru yoldan ayırma, sevdiklerimizi muhafaza eyle. Kapına gelenleri boş çevirme Allah'ım.\n\nCumanız feyizli ve mübarek olsun.",
            category = "Dualar & Bereket"
        ),
        FridayMessage(
            title = "Rızık ve Bereket Duası",
            content = "Ey kapıları açan Rabbimiz! Önümüze hayır kapılarını aç, rızkımızı helalinden ve bol eyle. Zorluklarımızı kolaylaştır. Cumanız bereket dolsun.",
            category = "Dualar & Bereket"
        ),
        FridayMessage(
            title = "Kardeşlik ve Birlik Duası",
            content = "Rabbimiz kalplerimizi İslam sevgisiyle birleştirsin. Mazlumlara imdat, dertlilere deva ihsan eylesin. Hayırlı Cumalar.",
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
            content = "Bu mübarek Cuma gününün size ve ailenize sağlık, mutluluk ve huzur getirmesini dilerim. Selam ve dua ile...",
            category = "Samimi & Kısa"
        ),
        FridayMessage(
            title = "Nurlu Cumalar",
            content = "Dualarda buluşmak ümidiyle; Cumanız hayırlı, ömrünüz bereketli olsun. 🤲",
            category = "Samimi & Kısa"
        ),

        // Akraba & Büyükler
        FridayMessage(
            title = "Büyüklerimize Saygı ve Hürmet",
            content = "Hürmet ve sevgilerimle ellerinizden öperim. Rabbim başımızdan eksik etmesin, sağlıklı ve hayırlı ömürler versin.\n\nMübarek Cuma gününüz kutlu olsun {isim}.",
            category = "Akraba & Büyükler",
            isFavorite = true
        ),
        FridayMessage(
            title = "Aile ve Sıla-i Rahim",
            content = "Gönül bağıyla bağlı olduğumuz tüm sevdiklerimize selam olsun. Cuma günümüzün feyzi ve bereketi hanenizin üzerine olsun.\n\nHayırlı Cumalar.",
            category = "Akraba & Büyükler"
        ),
        FridayMessage(
            title = "Hayırlı Dualar",
            content = "Canım ailem ve kıymetli büyüklerim; Rabbim sağlık, sıhhat ve afiyetten ayırmasın. Cumanız mübarek olsun.",
            category = "Akraba & Büyükler"
        ),

        // Peygamberimizin Duaları
        FridayMessage(
            title = "Rabbena Duası",
            content = "“Rabbimiz! Bize dünyada da iyilik ver, ahirette de iyilik ver ve bizi ateş azabından koru.” (Bakara, 201)\n\nDualarımızın kabul olması dileğiyle hayırlı Cumalar.",
            category = "Peygamberimizin Duaları",
            isFavorite = true
        ),
        FridayMessage(
            title = "İnşirah ve Ferahlık",
            content = "“Şüphesiz her güçlükle beraber bir kolaylık vardır.” (İnşirah Suresi)\n\nRabbim tüm sıkıntılarınızı hayırlı kolaylıklara tebdil eylesin. Cumanız kutlu olsun.",
            category = "Peygamberimizin Duaları"
        )
    )
}
