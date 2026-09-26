import Foundation
import SwiftUI
import Combine

final class CumaViewModel: ObservableObject {
    // MARK: - Published State
    @Published var recipients: [RecipientContact] = [] // Starts EMPTY as requested!
    @Published var messages: [FridayMessage] = []
    @Published var schedule: FridaySchedule = FridaySchedule()
    @Published var sentLogs: [SentLog] = []

    @Published var previewCardImage: UIImage? = nil
    @Published var customPickedImage: UIImage? = nil
    @Published var statusToast: String? = nil

    // Sequential Queue State
    @Published var isQueueActive: Bool = false
    @Published var currentQueueIndex: Int = 0

    init() {
        loadInitialMessages()
        generatePreviewCard()
    }

    var selectedRecipients: [RecipientContact] {
        recipients.filter { $0.isSelected }
    }

    var currentMessage: FridayMessage? {
        messages.first(where: { $0.id == schedule.selectedMessageId }) ?? messages.first
    }

    // MARK: - Contacts Management
    func importPhoneContacts() {
        ContactsManager.shared.requestAccess { [weak self] granted in
            guard let self = self else { return }
            if granted {
                let fetched = ContactsManager.shared.fetchContacts()
                self.recipients = fetched
                self.statusToast = "\(fetched.count) kişi rehberden aktarıldı."
            } else {
                self.statusToast = "Rehber erişimine izin verilmedi."
            }
        }
    }

    func addRecipient(name: String, phoneNumber: String, groupName: String = "Aile") {
        let newContact = RecipientContact(name: name, phoneNumber: phoneNumber, groupName: groupName, isSelected: true)
        recipients.append(newContact)
        statusToast = "\(name) listeye eklendi."
    }

    func toggleRecipientSelection(id: UUID) {
        if let index = recipients.firstIndex(where: { $0.id == id }) {
            recipients[index].isSelected.toggle()
        }
    }

    func selectAllRecipients(_ select: Bool) {
        for i in 0..<recipients.count {
            recipients[i].isSelected = select
        }
    }

    func deleteRecipient(at offsets: IndexSet) {
        recipients.remove(atOffsets: offsets)
    }

    // MARK: - Messages Management
    func selectRandomMessage() {
        if let random = messages.randomElement() {
            schedule.selectedMessageId = random.id
            generatePreviewCard()
            statusToast = "Rastgele Cuma mesajı seçildi: \(random.title)"
        }
    }

    func toggleMessageFavorite(id: Int) {
        if let idx = messages.firstIndex(where: { $0.id == id }) {
            messages[idx].isFavorite.toggle()
        }
    }

    func addCustomMessage(title: String, content: String) {
        let newId = (messages.map(\.id).max() ?? 0) + 1
        let msg = FridayMessage(id: newId, title: title, content: content, category: "Özel", isFavorite: true, isCustom: true)
        messages.insert(msg, at: 0)
        schedule.selectedMessageId = newId
        generatePreviewCard()
        statusToast = "Yeni Cuma mesajınız eklendi ve seçildi."
    }

    // MARK: - Schedule & Card Customization
    func updateScheduleTime(hour: Int, minute: Int) {
        schedule.hour = hour
        schedule.minute = minute
        if schedule.isEnabled {
            NotificationManager.shared.scheduleFridayReminder(hour: hour, minute: minute)
        }
        statusToast = "Zamanlayıcı güncellendi: \(String(format: "%02d:%02d", hour, minute))"
    }

    func toggleScheduleEnabled(_ enabled: Bool) {
        schedule.isEnabled = enabled
        if enabled {
            NotificationManager.shared.scheduleFridayReminder(hour: schedule.hour, minute: schedule.minute)
            statusToast = "Cuma hatırlatıcısı aktif."
        } else {
            NotificationManager.shared.cancelReminder()
            statusToast = "Cuma hatırlatıcısı duraklatıldı."
        }
    }

    func setCustomImage(_ image: UIImage) {
        customPickedImage = image
        schedule.selectedImageKey = "custom_user_photo"
        generatePreviewCard()
        statusToast = "Fotoğraf başarıyla güncellendi!"
    }

    func setSelectedImageKey(_ key: String) {
        schedule.selectedImageKey = key
        customPickedImage = nil
        generatePreviewCard()
    }

    func generatePreviewCard() {
        let baseImage: UIImage
        if let custom = customPickedImage, schedule.selectedImageKey == "custom_user_photo" {
            baseImage = custom
        } else {
            // Default placeholder spiritual mosque representation if asset isn't present
            baseImage = UIImage(named: schedule.selectedImageKey) ?? createPlaceholderMosqueImage()
        }

        let msgText = currentMessage?.content ?? "Hayırlı Cumalar dilerim."

        previewCardImage = CardGenerator.shared.generateCardImage(
            backgroundImage: baseImage,
            messageText: msgText,
            signature: schedule.senderSignature,
            recipientName: "",
            fontStyle: schedule.fontStyle,
            frameStyle: schedule.frameStyle,
            textSizeModifier: schedule.textSizeModifier,
            textColorHex: schedule.textColorHex
        )
    }

    private func createPlaceholderMosqueImage() -> UIImage {
        let size = CGSize(width: 800, height: 1000)
        UIGraphicsBeginImageContext(size)
        let ctx = UIGraphicsGetCurrentContext()
        ctx?.setFillColor(UIColor(red: 0, green: 0.28, blue: 0.20, alpha: 1.0).cgColor)
        ctx?.fill(CGRect(origin: .zero, size: size))
        let img = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return img ?? UIImage()
    }

    // MARK: - Queue Dispatcher
    func startSequentialQueue() {
        guard !selectedRecipients.isEmpty else {
            statusToast = "Lütfen önce mesaj gönderilecek kişileri seçin."
            return
        }
        currentQueueIndex = 0
        isQueueActive = true
    }

    func advanceQueue(didSend: Bool) {
        if didSend, let contact = selectedRecipients[safe: currentQueueIndex] {
            sentLogs.insert(SentLog(contactName: contact.name, phoneNumber: contact.phoneNumber, messageSnippet: currentMessage?.content ?? ""), at: 0)
        }

        if currentQueueIndex + 1 < selectedRecipients.count {
            currentQueueIndex += 1
        } else {
            isQueueActive = false
            statusToast = "Tüm Cuma mesajları gönderildi! 🤲"
        }
    }

    func cancelQueue() {
        isQueueActive = false
    }

    // MARK: - Initial Message Library
    private func loadInitialMessages() {
        messages = [
            FridayMessage(id: 1, title: "Cuma Suresi 9. Ayet", content: "“Ey iman edenler! Cuma günü namaza çağrıldığınız zaman hemen Allah'ı anmaya koşun ve alışverişi bırakın. Eğer bilirseniz bu sizin için daha hayırlıdır.” (Cuma Suresi, 9)\n\nHayırlı ve bereketli Cumalar dilerim.", category: "Ayet & Hadis", isFavorite: true),
            FridayMessage(id: 2, title: "En Hayırlı Gün", content: "Resulullah (s.a.v.) buyurdu: “Üzerine güneşin doğduğu en hayırlı gün Cuma günüdür.”\n\nRabbim bu mübarek günün hürmetine gönlünüzdeki hayırlı duaları kabul eylesin. Hayırlı Cumalar {isim}!", category: "Ayet & Hadis", isFavorite: true),
            FridayMessage(id: 3, title: "İcabet Saati Müjdesi", content: "“Cuma gününde bir saat vardır ki, Müslüman bir kul o saatte Allah'tan bir hayır dilerse, Allah ona mutlaka verir.” (Buhârî)\n\nDualarımızın makbul olduğu hayırlı, nurlu Cumalar.", category: "Ayet & Hadis", isFavorite: true),
            FridayMessage(id: 4, title: "Huzur ve Afiyet Duası", content: "Allah'ım! Kalbimize ferahlık, hanemize huzur ve bereket, bedenimize sağlık, ömrümüze afiyet ihsan eyle. Bizleri sevdiklerimizle cennette buluştur.\n\nMübarek Cuma gününüz hayırlara vesile olsun.", category: "Dualar & Bereket", isFavorite: true),
            FridayMessage(id: 5, title: "Gönül Şifası ve Genişlik", content: "Rabbim, ömrümüzü bereketli, amellerimizi ihlaslı, dualarımızı kabul eylesin. Dertlerimize deva, gönüllerimize şifa, hanelerimize neşe nasip etsin.\n\nHayırlı ve nurlu Cumalar dilerim {isim}.", category: "Dualar & Bereket", isFavorite: true),
            FridayMessage(id: 6, title: "Samimi ve Nurlu", content: "Gönlünüzden geçen her hayırlı duanın kabul olması dileğiyle... Hayırlı, huzurlu ve bereketli Cumalar!", category: "Samimi & Kısa", isFavorite: true),
            FridayMessage(id: 7, title: "Büyüklerimize Hürmet", content: "Hürmet ve sevgilerimle ellerinizden öperim. Rabbim başımızdan eksik etmesin, sağlıklı ve hayırlı ömürler versin.\n\nMübarek Cuma gününüz kutlu olsun {isim}.", category: "Akraba & Büyükler", isFavorite: true),
            FridayMessage(id: 8, title: "Rabbena Duası", content: "“Rabbimiz! Bize dünyada da iyilik ver, ahirette de iyilik ver ve bizi ateş azabından koru.” (Bakara, 201)\n\nDualarımızın kabul olması dileğiyle hayırlı Cumalar.", category: "Peygamberimizin Duaları", isFavorite: true)
        ]
    }
}

extension Collection {
    subscript(safe index: Index) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}
