import SwiftUI

struct HomeView: View {
    @EnvironmentObject var viewModel: CumaViewModel
    @State private var showShareSheet: Bool = false

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    // Top Hero Banner
                    ZStack(alignment: .bottomLeading) {
                        LinearGradient(
                            gradient: Gradient(colors: [Color(red: 0.0, green: 0.38, blue: 0.27), Color(red: 0.0, green: 0.20, blue: 0.14)]),
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                        .frame(height: 150)
                        .cornerRadius(20)

                        VStack(alignment: .leading, spacing: 6) {
                            HStack {
                                Text("✦ ﷽ ✦")
                                    .font(.subheadline)
                                    .foregroundColor(Color(hex: "#E5BE65"))
                                Spacer()
                                Text("Cuma Vakti")
                                    .font(.caption.bold())
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 4)
                                    .background(Color.white.opacity(0.15))
                                    .cornerRadius(12)
                                    .foregroundColor(.white)
                            }

                            Text("Mübarek Cuma Tebrikleri")
                                .font(.title2.bold())
                                .foregroundColor(.white)

                            Text("Sevdiklerinize dualı ve anlamlı Cuma kartları iletin")
                                .font(.caption)
                                .foregroundColor(.white.opacity(0.85))
                        }
                        .padding(18)
                    }
                    .padding(.horizontal)

                    // Live Card Preview Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Seçili Cuma Kartı")
                                    .font(.headline)
                                Text("Görsel ve dua altın çerçeve ile hazırlandı")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            NavigationLink(destination: CardCreatorView()) {
                                HStack(spacing: 4) {
                                    Text("Düzenle")
                                    Image(systemName: "pencil")
                                }
                                .font(.footnote.bold())
                                .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                            }
                        }

                        // Preview Image
                        ZStack {
                            RoundedRectangle(cornerRadius: 16)
                                .fill(Color.black)
                                .frame(height: 320)

                            if let cardImage = viewModel.previewCardImage {
                                Image(uiImage: cardImage)
                                    .resizable()
                                    .scaledToFit()
                                    .frame(maxHeight: 320)
                                    .cornerRadius(16)
                            } else {
                                ProgressView()
                            }
                        }

                        // Share Action Buttons
                        VStack(spacing: 8) {
                            Button(action: { viewModel.startSequentialQueue() }) {
                                HStack {
                                    Image(systemName: "paperplane.fill")
                                    Text("Rehberdeki Kişilere Gönder (\(viewModel.selectedRecipients.count) Kişi)")
                                        .fontWeight(.bold)
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 50)
                                .background(Color(hex: "#25D366"))
                                .foregroundColor(.white)
                                .cornerRadius(14)
                            }

                            Button(action: { showShareSheet = true }) {
                                HStack {
                                    Image(systemName: "square.and.arrow.up")
                                    Text("Sosyal Medyada Paylaş (Telegram, Insta, FB)")
                                        .fontWeight(.semibold)
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                                .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                                .cornerRadius(14)
                            }
                        }
                    }
                    .padding(16)
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(20)
                    .padding(.horizontal)

                    // Quick Actions
                    HStack(spacing: 12) {
                        Button(action: { viewModel.selectRandomMessage() }) {
                            HStack {
                                Image(systemName: "shuffle")
                                Text("Rastgele Dua Seç")
                                    .font(.footnote.bold())
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color(UIColor.secondarySystemBackground))
                            .cornerRadius(12)
                        }

                        NavigationLink(destination: ScheduleView()) {
                            HStack {
                                Image(systemName: "alarm")
                                Text("Zamanlayıcı")
                                    .font(.footnote.bold())
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color(UIColor.secondarySystemBackground))
                            .cornerRadius(12)
                        }
                    }
                    .padding(.horizontal)
                }
                .padding(.vertical)
            }
            .navigationTitle("Cuma Mesajları")
            .sheet(isPresented: $showShareSheet) {
                SocialShareSheetView(
                    message: viewModel.currentMessage?.content ?? "Hayırlı Cumalar",
                    image: viewModel.previewCardImage
                )
            }
        }
    }
}
