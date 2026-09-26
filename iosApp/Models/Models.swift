import Foundation

// MARK: - Recipient Contact
struct RecipientContact: Identifiable, Codable, Equatable {
    var id: UUID = UUID()
    var name: String
    var phoneNumber: String
    var groupName: String = "Aile"
    var isSelected: Boolean = true
    var lastSentTimestamp: Date? = nil

    enum CodingKeys: String, CodingKey {
        case id, name, phoneNumber, groupName, isSelected, lastSentTimestamp
    }
}

// MARK: - Friday Message
struct FridayMessage: Identifiable, Codable, Equatable {
    var id: Int
    var title: String
    var content: String
    var category: String
    var isFavorite: Bool = false
    var isCustom: Bool = false
}

// MARK: - Friday Schedule
struct FridaySchedule: Codable, Equatable {
    var isEnabled: Bool = true
    var hour: Int = 9
    var minute: Int = 0
    var selectedMessageId: Int = 1
    var selectedImageKey: String = "mosque_sunset"
    var mergeTextOnImage: Bool = true
    var senderSignature: String = ""
    var fontStyle: String = "Serif" // "Serif", "Sans", "Script", "Bold"
    var frameStyle: String = "Gold" // "Gold", "Emerald", "Lantern", "Minimal"
    var textSizeModifier: Double = 1.0
    var textColorHex: String = "#FFFFFF"
}

// MARK: - Sent Log
struct SentLog: Identifiable, Codable {
    var id: UUID = UUID()
    var contactName: String
    var phoneNumber: String
    var messageSnippet: String
    var timestamp: Date = Date()
}
