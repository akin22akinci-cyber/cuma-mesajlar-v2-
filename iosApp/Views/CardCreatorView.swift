import SwiftUI

struct CardCreatorView: View {
    @EnvironmentObject var viewModel: CumaViewModel
    @State private var showingImagePicker: Bool = false
    @State private var pinterestUrlInput: String = ""
    @State private var isDownloadingPinterest: Bool = false
    @State private var showShareSheet: Bool = false

    let fontStyles = ["Serif", "Sans", "Script", "Bold"]
    let frameStyles = ["Gold", "Emerald", "Lantern", "Minimal"]
    let colorOptions: [(hex: String, name: String)] = [
        ("#FFFFFF", "Beyaz"),
        ("#FFF3D0", "Altın Krem"),
        ("#E8FFF5", "Zümrüt"),
        ("#FFE4E1", "Gül")
    ]

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Live Postcard Preview
                VStack(spacing: 12) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 18)
                            .fill(Color.black)
                            .frame(height: 350)

                        if let img = viewModel.previewCardImage {
                            Image(uiImage: img)
                                .resizable()
                                .scaledToFit()
                                .frame(maxHeight: 350)
                                .cornerRadius(18)
                        } else {
                            ProgressView()
                        }
                    }

                    HStack {
                        Button(action: { showShareSheet = true }) {
                            HStack {
                                Image(systemName: "square.and.arrow.up")
                                Text("Kartı Paylaş")
                                    .fontWeight(.bold)
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color(red: 0.0, green: 0.42, blue: 0.30))
                            .foregroundColor(.white)
                            .cornerRadius(12)
                        }

                        Button(action: { viewModel.generatePreviewCard() }) {
                            Image(systemName: "arrow.clockwise")
                                .frame(width: 44, height: 44)
                                .background(Color(UIColor.secondarySystemBackground))
                                .cornerRadius(12)
                        }
                    }
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(20)
                .padding(.horizontal)

                // 1. Photo Source Section
                VStack(alignment: .leading, spacing: 14) {
                    Text("1. Fotoğraf Seçimi & Pinterest")
                        .font(.headline)

                    HStack(spacing: 10) {
                        Button(action: { showingImagePicker = true }) {
                            HStack {
                                Image(systemName: "photo.on.rectangle.angled")
                                Text("Galeriden Fotoğraf Seç")
                                    .font(.subheadline.bold())
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                            .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                            .cornerRadius(12)
                        }

                        Button(action: {
                            if let url = URL(string: PinterestHelper.pinterestSearchURL) {
                                UIApplication.shared.open(url)
                            }
                        }) {
                            HStack {
                                Image(systemName: "safari")
                                Text("Pinterest Aç")
                                    .font(.subheadline.bold())
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color(UIColor.tertiarySystemBackground))
                            .foregroundColor(.primary)
                            .cornerRadius(12)
                        }
                    }

                    // Download URL field
                    HStack {
                        TextField("Pinterest veya Resim Linki Yapıştır...", text: $pinterestUrlInput)
                            .textFieldStyle(RoundedBorderTextFieldStyle())

                        if !pinterestUrlInput.isEmpty {
                            Button(action: {
                                isDownloadingPinterest = true
                                PinterestHelper.shared.downloadImage(from: pinterestUrlInput) { downloaded in
                                    isDownloadingPinterest = false
                                    if let img = downloaded {
                                        viewModel.setCustomImage(img)
                                        pinterestUrlInput = ""
                                    }
                                }
                            }) {
                                if isDownloadingPinterest {
                                    ProgressView()
                                } else {
                                    Text("İndir")
                                        .font(.subheadline.bold())
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 8)
                                        .background(Color(red: 0.0, green: 0.42, blue: 0.30))
                                        .foregroundColor(.white)
                                        .cornerRadius(8)
                                }
                            }
                        }
                    }

                    // Curated Mosques Carousel
                    Text("Hazır Manevi Cami Fotoğrafları:")
                        .font(.caption.bold())
                        .foregroundColor(.secondary)

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            ForEach(PinterestHelper.shared.curatedGallery) { mosque in
                                Button(action: { viewModel.setSelectedImageKey(mosque.id) }) {
                                    VStack(alignment: .leading, spacing: 6) {
                                        ZStack {
                                            RoundedRectangle(cornerRadius: 12)
                                                .fill(Color(red: 0.0, green: 0.28, blue: 0.20))
                                                .frame(width: 100, height: 120)

                                            Image(systemName: "building.columns.fill")
                                                .font(.largeTitle)
                                                .foregroundColor(.white.opacity(0.8))

                                            if viewModel.schedule.selectedImageKey == mosque.id {
                                                VStack {
                                                    HStack {
                                                        Spacer()
                                                        Image(systemName: "checkmark.circle.fill")
                                                            .foregroundColor(Color(hex: "#E5BE65"))
                                                            .padding(6)
                                                    }
                                                    Spacer()
                                                }
                                            }
                                        }
                                        Text(mosque.tag)
                                            .font(.caption2.bold())
                                            .foregroundColor(.primary)
                                            .lineLimit(1)
                                    }
                                    .frame(width: 100)
                                }
                            }
                        }
                    }
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(20)
                .padding(.horizontal)

                // 2. Typography & Frame Designer
                VStack(alignment: .leading, spacing: 14) {
                    Text("2. Yazı & Çerçeve Stüdyosu")
                        .font(.headline)

                    // Font Style
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Yazı Tipi:")
                            .font(.caption.bold())
                            .foregroundColor(.secondary)

                        Picker("Yazı Tipi", selection: $viewModel.schedule.fontStyle) {
                            ForEach(fontStyles, id: \.self) { style in
                                Text(style).tag(style)
                            }
                        }
                        .pickerStyle(SegmentedPickerStyle())
                        .onChange(of: viewModel.schedule.fontStyle) { _ in viewModel.generatePreviewCard() }
                    }

                    // Frame Style
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Çerçeve Bordürü:")
                            .font(.caption.bold())
                            .foregroundColor(.secondary)

                        Picker("Çerçeve", selection: $viewModel.schedule.frameStyle) {
                            ForEach(frameStyles, id: \.self) { style in
                                Text(style).tag(style)
                            }
                        }
                        .pickerStyle(SegmentedPickerStyle())
                        .onChange(of: viewModel.schedule.frameStyle) { _ in viewModel.generatePreviewCard() }
                    }

                    // Text Size Slider
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text("Yazı Boyutu:")
                                .font(.caption.bold())
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(String(format: "%.1fx", viewModel.schedule.textSizeModifier))
                                .font(.caption.bold())
                                .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                        }

                        Slider(value: $viewModel.schedule.textSizeModifier, in: 0.8...1.4, step: 0.1)
                            .accentColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                            .onChange(of: viewModel.schedule.textSizeModifier) { _ in viewModel.generatePreviewCard() }
                    }

                    // Text Color Options
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Yazı Rengi:")
                            .font(.caption.bold())
                            .foregroundColor(.secondary)

                        HStack(spacing: 12) {
                            ForEach(colorOptions, id: \.hex) { option in
                                Button(action: {
                                    viewModel.schedule.textColorHex = option.hex
                                    viewModel.generatePreviewCard()
                                }) {
                                    HStack(spacing: 6) {
                                        Circle()
                                            .fill(Color(hex: option.hex))
                                            .frame(width: 18, height: 18)
                                            .overlay(Circle().stroke(Color.gray, lineWidth: 1))
                                        Text(option.name)
                                            .font(.caption)
                                    }
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(viewModel.schedule.textColorHex == option.hex ? Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.15) : Color(UIColor.tertiarySystemBackground))
                                    .cornerRadius(10)
                                }
                            }
                        }
                    }

                    // Signature
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Gönderen İmzası:")
                            .font(.caption.bold())
                            .foregroundColor(.secondary)

                        TextField("Örn: Akın Akıncı ve Ailesi", text: $viewModel.schedule.senderSignature)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .onChange(of: viewModel.schedule.senderSignature) { _ in viewModel.generatePreviewCard() }
                    }
                }
                .padding(16)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(20)
                .padding(.horizontal)
            }
            .padding(.vertical)
        }
        .navigationTitle("Cuma Kartı Tasarla")
        .sheet(isPresented: $showingImagePicker) {
            IOSImagePicker(image: $viewModel.customPickedImage) {
                if let picked = viewModel.customPickedImage {
                    viewModel.setCustomImage(picked)
                }
            }
        }
        .sheet(isPresented: $showShareSheet) {
            SocialShareSheetView(
                message: viewModel.currentMessage?.content ?? "Hayırlı Cumalar",
                image: viewModel.previewCardImage
            )
        }
    }
}

// Native iOS UIImagePicker wrapper
struct IOSImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    var onPicked: () -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .photoLibrary
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: IOSImagePicker
        init(_ parent: IOSImagePicker) { self.parent = parent }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            if let uiImage = info[.originalImage] as? UIImage {
                parent.image = uiImage
                parent.onPicked()
            }
            picker.dismiss(animated: true)
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            picker.dismiss(animated: true)
        }
    }
}
