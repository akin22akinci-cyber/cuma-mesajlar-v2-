import SwiftUI

struct ScheduleView: View {
    @EnvironmentObject var viewModel: CumaViewModel
    @State private var selectedDate: Date = {
        var comp = DateComponents()
        comp.hour = 9
        comp.minute = 0
        return Calendar.current.date(from: comp) ?? Date()
    }()

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Main Switch Card
                VStack(spacing: 16) {
                    HStack {
                        ZStack {
                            Circle()
                                .fill(Color(red: 0.0, green: 0.42, blue: 0.30).opacity(0.12))
                                .frame(width: 48, height: 48)
                            Image(systemName: "alarm.fill")
                                .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                                .font(.title3)
                        }

                        VStack(alignment: .leading, spacing: 2) {
                            Text("Cuma Zamanlayıcısı")
                                .font(.headline)
                            Text("Her Cuma sabahı bildirim ve hatırlatma")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }

                        Spacer()

                        Toggle("", isOn: Binding<Bool>(
                            get: { viewModel.schedule.isEnabled },
                            set: { viewModel.toggleScheduleEnabled($0) }
                        ))
                        .labelsHidden()
                    }

                    Divider()

                    // Time Picker
                    DatePicker(
                        "Hatırlatma Saati",
                        selection: $selectedDate,
                        displayedComponents: .hourAndMinute
                    )
                    .datePickerStyle(WheelDatePickerStyle())
                    .labelsHidden()
                    .onChange(of: selectedDate) { newDate in
                        let cal = Calendar.current
                        let h = cal.component(.hour, from: newDate)
                        let m = cal.component(.minute, from: newDate)
                        viewModel.updateScheduleTime(hour: h, minute: m)
                    }

                    HStack {
                        Image(systemName: "clock")
                            .foregroundColor(Color(hex: "#E5BE65"))
                        Text("Her Cuma Saat: \(String(format: "%02d:%02d", viewModel.schedule.hour, viewModel.schedule.minute))")
                            .font(.headline)
                            .foregroundColor(Color(red: 0.0, green: 0.42, blue: 0.30))
                    }
                    .padding(.top, 4)
                }
                .padding(18)
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(20)
                .padding(.horizontal)

                // History / Sent Log
                VStack(alignment: .leading, spacing: 12) {
                    Text("Gönderim Geçmişi (\(viewModel.sentLogs.count))")
                        .font(.headline)
                        .padding(.horizontal)

                    if viewModel.sentLogs.isEmpty {
                        VStack(spacing: 8) {
                            Text("Henüz gönderim geçmişi bulunmuyor.")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            Text("Mesaj gönderildiğinde burada listelenir.")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(24)
                        .background(Color(UIColor.secondarySystemBackground))
                        .cornerRadius(16)
                        .padding(.horizontal)
                    } else {
                        VStack(spacing: 8) {
                            ForEach(viewModel.sentLogs) { log in
                                HStack {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundColor(Color(hex: "#25D366"))
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(log.contactName)
                                            .font(.subheadline.bold())
                                        Text(log.phoneNumber)
                                            .font(.caption2)
                                            .foregroundColor(.secondary)
                                    }
                                    Spacer()
                                    Text(log.timestamp, style: .date)
                                        .font(.caption2)
                                        .foregroundColor(.secondary)
                                }
                                .padding(12)
                                .background(Color(UIColor.secondarySystemBackground))
                                .cornerRadius(12)
                            }
                        }
                        .padding(.horizontal)
                    }
                }
            }
            .padding(.vertical)
        }
        .navigationTitle("Zamanlayıcı")
    }
}
