import SwiftUI

struct ContactsView: View {
    @EnvironmentObject var viewModel: CumaViewModel
    @State private var showingAddSheet: Bool = false
    @State private var newName: String = ""
    @State private var newPhone: String = ""
    @State private var newGroup: String = "Aile"
    @State private var searchText: String = ""

    var filteredContacts: [RecipientContact] {
        if searchText.isEmpty {
            return viewModel.recipients
        } else {
            return viewModel.recipients.filter {
                $0.name.localizedCaseInsensitiveContains(searchText) ||
                $0.phoneNumber.contains(searchText)
            }
        }
    }

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.recipients.isEmpty {
                    // Empty State View (Starts empty as requested!)
                    VStack(spacing: 20) {
                        Spacer()

                        ZStack {
                            Circle()
                                .fill(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                                .frame(width: 90, height: 90)

                            Image(systemName: "person.crop.circle.badge.plus")
                                .font(.system(size: 40))
                                .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                        }

                        VStack(spacing: 6) {
                            Text("Rehberiniz Henüz Boş")
                                .font(.title3.bold())

                            Text("Cuma mesajlarını göndermek istediğiniz sevdiklerinizi iPhone rehberinizden tek dokunuşla aktarabilir veya elle ekleyebilirsiniz.")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal, 32)
                        }

                        VStack(spacing: 12) {
                            Button(action: { viewModel.importPhoneContacts() }) {
                                HStack {
                                    Image(systemName: "square.and.arrow.down.fill")
                                    Text("iPhone Rehberinden Kişileri Aktar")
                                        .fontWeight(.bold)
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 50)
                                .background(Color(red: 0.0, green: 0.42, blue: 0.30))
                                .foregroundColor(.white)
                                .cornerRadius(14)
                            }
                            .padding(.horizontal, 32)

                            Button(action: { showingAddSheet = true }) {
                                HStack {
                                    Image(systemName: "person.badge.plus")
                                    Text("Yeni Kişi Ekle")
                                        .fontWeight(.semibold)
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 46)
                                .background(Color(UIColor.secondarySystemBackground))
                                .foregroundColor(.primary)
                                .cornerRadius(14)
                            }
                            .padding(.horizontal, 32)
                        }

                        Spacer()
                    }
                } else {
                    // Contact list
                    VStack(spacing: 8) {
                        HStack {
                            Text("\(viewModel.selectedRecipients.count) / \(viewModel.recipients.count) Kişi Seçili")
                                .font(.caption.bold())
                                .foregroundColor(.secondary)

                            Spacer()

                            Button("Tümünü Seç") {
                                viewModel.selectAllRecipients(true)
                            }
                            .font(.caption.bold())
                            .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))

                            Text("•").foregroundColor(.secondary)

                            Button("Kaldır") {
                                viewModel.selectAllRecipients(false)
                            }
                            .font(.caption.bold())
                            .foregroundColor(.secondary)
                        }
                        .padding(.horizontal)
                        .padding(.top, 4)

                        List {
                            ForEach(filteredContacts) { contact in
                                HStack(spacing: 14) {
                                    Button(action: { viewModel.toggleRecipientSelection(id: contact.id) }) {
                                        Image(systemName: contact.isSelected ? "checkmark.circle.fill" : "circle")
                                            .foregroundColor(contact.isSelected ? Color(red: 0.0, green: 0.42, blue: 0.30) : .gray)
                                            .font(.title3)
                                    }

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(contact.name)
                                            .font(.headline)
                                        Text(contact.phoneNumber)
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }

                                    Spacer()

                                    Text(contact.groupName)
                                        .font(.caption2.bold())
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 3)
                                        .background(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                                        .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                                        .cornerRadius(8)
                                }
                                .padding(.vertical, 2)
                            }
                            .onDelete(perform: viewModel.deleteRecipient)
                        }
                        .listStyle(InsetGroupedListStyle())
                    }
                }
            }
            .navigationTitle("Kişiler")
            .navigationBarItems(
                leading: Button(action: { viewModel.importPhoneContacts() }) {
                    Image(systemName: "arrow.triangle.2.circlepath")
                },
                trailing: Button(action: { showingAddSheet = true }) {
                    Image(systemName: "plus")
                }
            )
            .sheet(isPresented: $showingAddSheet) {
                NavigationView {
                    Form {
                        Section(header: Text("Kişi Bilgileri")) {
                            TextField("İsim & Soyisim", text: $newName)
                            TextField("Telefon Numarası", text: $newPhone)
                                .keyboardType(.phonePad)
                            TextField("Grup (Örn: Aile, Arkadaşlar)", text: $newGroup)
                        }
                    }
                    .navigationTitle("Yeni Kişi")
                    .navigationBarItems(
                        leading: Button("Vazgeç") { showingAddSheet = false },
                        trailing: Button("Kaydet") {
                            if !newName.isEmpty && !newPhone.isEmpty {
                                viewModel.addRecipient(name: newName, phoneNumber: newPhone, groupName: newGroup)
                                newName = ""
                                newPhone = ""
                                showingAddSheet = false
                            }
                        }
                        .disabled(newName.isEmpty || newPhone.isEmpty)
                    )
                }
            }
        }
    }
}
