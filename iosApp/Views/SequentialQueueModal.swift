import SwiftUI

struct SequentialQueueModalView: View {
    @EnvironmentObject var viewModel: CumaViewModel

    var body: some View {
        let contacts = viewModel.selectedRecipients
        let index = viewModel.currentQueueIndex
        let currentContact = contacts[safe: index]

        ZStack {
            Color.black.opacity(0.4).edgesIgnoringSafeArea(.all)

            VStack(spacing: 16) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("WhatsApp Gönderim Sırası")
                            .font(.headline)
                            .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                        Text("Adım adım kolay iletim")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    Spacer()
                    Button(action: { viewModel.cancelQueue() }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title2)
                            .foregroundColor(.gray)
                    }
                }

                // Progress
                ProgressView(value: Double(index + 1), total: Double(max(contacts.count, 1)))
                    .accentColor(Color(red: 0.0, green: 0.42, blue: 0.30))

                Text("\(index + 1) / \(contacts.count) Kişi")
                    .font(.caption.bold())
                    .foregroundColor(.secondary)

                if let contact = currentContact {
                    // Current recipient card
                    HStack(spacing: 14) {
                        ZStack {
                            Circle()
                                .fill(Color(red: 0.0, green: 0.42, blue: 0.30))
                                .frame(width: 48, height: 48)
                            Image(systemName: "person.fill")
                                .foregroundColor(.white)
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            Text(contact.name)
                                .font(.headline)
                            Text(contact.phoneNumber)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                    .padding(14)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(14)

                    // Message snippet
                    let personalized = (viewModel.currentMessage?.content ?? "Hayırlı Cumalar").replacingOccurrences(of: "{isim}", with: contact.name)
                    Text("“\(String(personalized.prefix(80)))...”")
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)

                    // Action: Send on WhatsApp
                    Button(action: {
                        SocialShareManager.shared.shareDirectWhatsApp(
                            phoneNumber: contact.phoneNumber,
                            message: personalized,
                            image: viewModel.previewCardImage
                        )
                    }) {
                        HStack {
                            Image(systemName: "paperplane.fill")
                            Text("WhatsApp'ta Gönder (\(index + 1)/\(contacts.count))")
                                .fontWeight(.bold)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color(hex: "#25D366"))
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }

                    // Advance
                    Button(action: { viewModel.advanceQueue(didSend: true) }) {
                        HStack {
                            Image(systemName: "checkmark.circle.fill")
                            Text(index + 1 < contacts.count ? "Gönderildi, Sıradakine Geç" : "Tamamla")
                                .fontWeight(.semibold)
                            Image(systemName: "arrow.right")
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 46)
                        .background(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                        .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                        .cornerRadius(12)
                    }

                    // Skip
                    Button("Bu Kişiyi Atla") {
                        viewModel.advanceQueue(didSend: false)
                    }
                    .font(.footnote)
                    .foregroundColor(.secondary)
                }
            }
            .padding(20)
            .background(Color(UIColor.systemBackground))
            .cornerRadius(20)
            .shadow(radius: 20)
            .padding(24)
        }
    }
}
