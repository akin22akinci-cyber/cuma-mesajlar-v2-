import Foundation
import UserNotifications

final class NotificationManager {
    static let shared = NotificationManager()

    func requestAuthorization() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Notification permission error: \(error)")
            }
        }
    }

    func scheduleFridayReminder(hour: Int, minute: Int) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: ["FRIDAY_CUMA_REMINDER"])

        let content = UNMutableNotificationContent()
        content.title = "Hayırlı Cumalar! 🕌"
        content.body = "Cuma vaktiniz mübarek olsun. Sevdiklerinize Cuma tebrik kartınızı göndermek için dokunun."
        content.sound = .default

        var dateComponents = DateComponents()
        dateComponents.weekday = 6 // 1 = Sunday, 6 = Friday in iOS Calendar
        dateComponents.hour = hour
        dateComponents.minute = minute

        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(identifier: "FRIDAY_CUMA_REMINDER", content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Failed to schedule Friday notification: \(error)")
            }
        }
    }

    func cancelReminder() {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: ["FRIDAY_CUMA_REMINDER"])
    }
}
