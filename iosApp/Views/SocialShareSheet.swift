import SwiftUI

struct SocialShareSheetView: View {
    let message: String
    let image: UIImage?
    @Environment(\.presentationMode) var presentationMode

    let platforms = IOSSocialPlatform.allCases

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                VStack(spacing: 6) {
                    Text("Cuma Tebrikini Paylaş")
                        .font(.title2.bold())
                    Text("WhatsApp, Instagram, Telegram ve diğer uygulamalarla iletin")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
                .padding(.top, 16)

                LazyVGrid(columns: [GridItem(.adaptive(minimum: 80))], spacing: 20) {
                    ForEach(platforms) { platform in
                        Button(action: {
                            SocialShareManager.shared.share(platform: platform, message: message, image: image)
                            presentationMode.wrappedValue.dismiss()
                        }) {
                            VStack(spacing: 8) {
                                ZStack {
                                    Circle()
                                        .fill(Color(hex: platform.colorHex))
                                        .frame(width: 58, height: 58)
                                    Text(platform.badgeText)
                                        .font(.system(size: 18, weight: .bold))
                                        .foregroundColor(platform == .snapchat ? .black : .white)
                                }
                                Text(platform.rawValue)
                                    .font(.caption2.weight(.medium))
                                    .foregroundColor(.primary)
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)

                Spacer()

                Button(action: {
                    SocialShareManager.shared.shareUniversal(message: message, image: image)
                    presentationMode.wrappedValue.dismiss()
                }) {
                    HStack {
                        Image(systemName: "square.and.arrow.up")
                        Text("Tüm Uygulamaları Göster (iOS Menüsü)")
                            .fontWeight(.semibold)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color(red: 0.0, green: 0.42, blue: 0.30))
                    .foregroundColor(.white)
                    .cornerRadius(14)
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 20)
            }
            .navigationBarItems(trailing: Button("Kapat") {
                presentationMode.wrappedValue.dismiss()
            })
        }
    }
}

// Color helper for Hex
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6: // RGB
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 108, 76)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
