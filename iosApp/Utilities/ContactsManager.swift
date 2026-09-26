import Foundation
import Contacts

final class ContactsManager {
    static let shared = ContactsManager()
    private let contactStore = CNContactStore()

    func requestAccess(completion: @escaping (Bool) -> Void) {
        contactStore.requestAccess(for: .contacts) { granted, _ in
            DispatchQueue.main.async {
                completion(granted)
            }
        }
    }

    func fetchContacts() -> [RecipientContact] {
        var results: [RecipientContact] = []
        let keys = [
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey
        ] as [CNKeyDescriptor]

        let request = CNContactFetchRequest(keysToFetch: keys)
        request.sortOrder = .userDefault

        do {
            try contactStore.enumerateContacts(with: request) { contact, _ in
                let fullName = "\(contact.givenName) \(contact.familyName)".trimmingCharacters(in: .whitespaces)
                guard !fullName.isEmpty else { return }

                if let firstPhone = contact.phoneNumbers.first?.value.stringValue {
                    let recipient = RecipientContact(
                        name: fullName,
                        phoneNumber: firstPhone,
                        groupName: "Rehber",
                        isSelected: true
                    )
                    results.append(recipient)
                }
            }
        } catch {
            print("Failed to fetch iOS contacts: \(error)")
        }

        return results
    }
}
