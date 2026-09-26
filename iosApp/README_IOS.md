# Cuma Mesajları - iOS Native (SwiftUI) Portu

Bu klasör, Android uygulamasının birebir **iOS (Swift & SwiftUI)** için hazırlanmış eksiksiz kaynak kodlarını içerir.

## Özellikler (iOS)
- **Başlangıçta Boş Rehber**: Sahte veri barındırmaz. Kullanıcı dilediğinde `Contacts` framework ile iPhone rehberini tek dokunuşla aktarabilir.
- **Kendi Fotoğrafını Ekleme**: iOS `Photos` kütüphanesinden kendi fotoğraflarınızı seçebilir veya Pinterest aramasından görsel indirebilirsiniz.
- **Canlı Cuma Kartı Stüdyosu**: `UIGraphicsImageRenderer` ile altın varaklı, zümrüt bordürlü veya minimal çerçeveli yüksek çözünürlüklü tebrik kartları üretir.
- **Sosyal Medya Desteği**: WhatsApp (`wa.me`), Telegram (`tg://`), Instagram (`instagram://`), Facebook Messenger (`fb-messenger://`), Facebook, Snapchat ve iOS sistem paylaşım menüsü (`UIActivityViewController`).
- **Zamanlayıcı & Bildirimler**: `UserNotifications` ile her Cuma sabahı istenen saatte sesli hatırlatıcı.

## Mac / Xcode ile Çalıştırma:
1. Xcode'u açın ve **New Project -> App (iOS / SwiftUI)** seçeneğini seçin.
2. Proje adını `CumaMesajlari` yapın.
3. Bu `iosApp` klasöründeki dosyaları projenizin içine sürükleyip bırakın.
4. Hedef cihaz olarak iPhone Simülatörü veya kendi iPhone'unuzu seçip **Run (⌘ + R)** tuşuna basın.

## iPad / Mac (Swift Playgrounds ile Doğrudan Çalıştırma):
1. iPad veya Mac'te Swift Playgrounds uygulamasını açın.
2. Yeni bir App projesi başlatıp buradaki dosyaları içeri aktararak doğrudan çalıştırabilirsiniz.
