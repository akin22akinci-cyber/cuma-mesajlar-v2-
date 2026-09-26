import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var viewModel: CumaViewModel
    @State private var selectedTab: Int = 0

    var body: some View {
        ZStack {
            TabView(selection: $selectedTab) {
                HomeView()
                    .tabItem {
                        Image(systemName: "house.fill")
                        Text("Ana Sayfa")
                    }
                    .tag(0)

                ContactsView()
                    .tabItem {
                        Image(systemName: "person.2.fill")
                        Text("Kişiler")
                    }
                    .tag(1)

                MessagesView()
                    .tabItem {
                        Image(systemName: "text.book.closed.fill")
                        Text("Mesajlar")
                    }
                    .tag(2)

                CardCreatorView()
                    .tabItem {
                        Image(systemName: "paintpalette.fill")
                        Text("Cuma Kartı")
                    }
                    .tag(3)

                ScheduleView()
                    .tabItem {
                        Image(systemName: "alarm.fill")
                        Text("Zamanlayıcı")
                    }
                    .tag(4)
            }

            // Sequential Dispatcher HUD on top
            if viewModel.isQueueActive {
                SequentialQueueModalView()
            }
        }
    }
}
