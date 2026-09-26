import SwiftUI

@main
struct CumaMesajlariApp: App {
    @StateObject private var viewModel = CumaViewModel()

    init() {
        NotificationManager.shared.requestAuthorization()
    }

    var body: some Scene {
        WindowGroup {
            MainTabView()
                .environmentObject(viewModel)
                .accentColor(Color(red: 0.0, green: 0.42, blue: 0.30)) // Emerald Green
        }
    }
}
