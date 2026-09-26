import SwiftUI

struct MessagesView: View {
    @EnvironmentObject var viewModel: CumaViewModel
    @State private var selectedCategory: String = "Tümü"
    @State private var showingAddMessage: Bool = false
    @State private var newTitle: String = ""
    @State private var newContent: String = ""
    @State private var sharingMessage: String? = nil

    let categories = ["Tümü", "Ayet & Hadis", "Dualar & Bereket", "Samimi & Kısa", "Akraba & Büyükler", "Peygamberimizin Duaları", "Özel"]

    var filteredMessages: [FridayMessage] {
        if selectedCategory == "Tümü" {
            return viewModel.messages
        } else {
            return viewModel.messages.filter { $0.category == selectedCategory }
        }
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 8) {
                // Category Pills
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(categories, id: \.self) { cat in
                            Button(action: { selectedCategory = cat }) {
                                Text(cat)
                                    .font(.caption.bold())
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(selectedCategory == cat ? Color(red: 0.0, green: 0.42, blue: 0.30) : Color(UIColor.secondarySystemBackground))
                                    .foregroundColor(selectedCategory == cat ? .white : .primary)
                                    .cornerRadius(16)
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 4)
                }

                // Messages List
                List {
                    ForEach(filteredMessages) { msg in
                        let isSelected = viewModel.schedule.selectedMessageId == msg.id

                        VStack(alignment: .leading, spacing: 10) {
                            HStack {
                                Text(msg.category)
                                    .font(.caption2.bold())
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 3)
                                    .background(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                                    .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                                    .cornerRadius(8)

                                Spacer()

                                Button(action: { viewModel.toggleMessageFavorite(id: msg.id) }) {
                                    Image(systemName: msg.isFavorite ? "heart.fill" : "heart")
                                        .foregroundColor(msg.isFavorite ? .red : .gray)
                                }
                            }

                            Text(msg.title)
                                .font(.headline)

                            Text(msg.content)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .lineLimit(4)

                            HStack {
                                Button(action: {
                                    viewModel.schedule.selectedMessageId = msg.id
                                    viewModel.generatePreviewCard()
                                }) {
                                    HStack(spacing: 4) {
                                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                                        Text(isSelected ? "Karta Seçildi" : "Bu Mesajı Seç")
                                    }
                                    .font(.caption.bold())
                                    .foregroundColor(isSelected ? Color(red: 0.0, green: 0.42, blue: 0.30) : .primary)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .background(isSelected ? Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.15) : Color(UIColor.tertiarySystemBackground))
                                    .cornerRadius(8)
                                }

                                Spacer()

                                Button(action: { sharingMessage = msg.content }) {
                                    Image(systemName: "square.and.arrow.up")
                                        .font(.subheadline)
                                        .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                                }
                            }
                        }
                        .padding(.vertical, 6)
                    }
                }
                .listStyle(InsetGroupedListStyle())
            }
            .navigationTitle("Cuma Mesajları")
            .navigationBarItems(
                leading: Button(action: { viewModel.selectRandomMessage() }) {
                    Image(systemName: "shuffle")
                },
                trailing: Button(action: { showingAddMessage = true }) {
                    Image(systemName: "plus")
                }
            )
            .sheet(isPresented: $showingAddMessage) {
                NavigationView {
                    Form {
                        Section(header: Text("Mesaj Başlığı")) {
                            TextField("Örn: Bereket ve Nurlu Cuma", text: $newTitle)
                        }
                        Section(header: Text("Dua veya Mesaj Metni")) {
                            TextEditor(text: $newContent)
                                .frame(height: 140)
                        }
                    }
                    .navigationTitle("Yeni Mesaj Yaz")
                    .navigationBarItems(
                        leading: Button("Vazgeç") { showingAddMessage = false },
                        trailing: Button("Kaydet") {
                            if !newContent.isEmpty {
                                viewModel.addCustomMessage(title: newTitle.isEmpty ? "Özel Mesaj" : newTitle, content: newContent)
                                newTitle = ""
                                newContent = ""
                                showingAddMessage = false
                            }
                        }
                        .disabled(newContent.isEmpty)
                    )
                }
            }
            .sheet(item: Binding<IdentifiableString?>(
                get: { sharingMessage != nil ? IdentifiableString(id: sharingMessage!) : nil },
                set: { sharingMessage = $0?.id }
            )) { item in
                SocialShareSheetView(message: item.id, image: nil)
            }
        }
    }
}

struct IdentifiableString: Identifiable {
    let id: String
}
