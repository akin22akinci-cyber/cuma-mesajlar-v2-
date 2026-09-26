import UIKit
import SwiftUI

enum IOSSocialPlatform: String, CaseIterable, Identifiable {
    case whatsapp = "WhatsApp"
    case telegram = "Telegram"
    case instagram = "Instagram"
    case messenger = "Messenger"
    case facebook = "Facebook"
    case snapchat = "Snapchat"
    case universal = "Diğer / Sistem"

    var id: String { rawValue }

    var badgeText: String {
        switch self {
        case .whatsapp: return "WA"
        case .telegram: return "TG"
        case .instagram: return "IG"
        case .messenger: return "MSG"
        case .facebook: return "FB"
        case .snapchat: return "SC"
        case .universal: return "✦"
        }
    }

    var colorHex: String {
        switch self {
        case .whatsapp: return "#25D366"
        case .telegram: return "#229ED9"
        case .instagram: return "#E4405F"
        case .messenger: return "#0084FF"
        case .facebook: return "#1877F2"
        case .snapchat: return "#FFFC00"
        case .universal: return "#006C4C"
        }
    }
}

final class SocialShareManager {
    static let shared = SocialShareManager()

    func share(platform: IOSSocialPlatform, message: String, image: UIImage?, from viewController: UIViewController? = nil) {
        let vc = viewController ?? getTopViewController()

        switch platform {
        case .whatsapp:
            shareToWhatsApp(message: message, image: image, presenter: vc)
        case .telegram:
            shareViaScheme(scheme: "tg://msg?text=\(urlEncoded(message))", fallbackPresenter: vc, message: message, image: image)
        case .instagram:
            shareViaScheme(scheme: "instagram://app", fallbackPresenter: vc, message: message, image: image)
        case .messenger:
            shareViaScheme(scheme: "fb-messenger://share?link=", fallbackPresenter: vc, message: message, image: image)
        case .facebook:
            shareViaScheme(scheme: "fb://", fallbackPresenter: vc, message: message, image: image)
        case .snapchat:
            shareViaScheme(scheme: "snapchat://", fallbackPresenter: vc, message: message, image: image)
        case .universal:
            shareUniversal(message: message, image: image, presenter: vc)
        }
    }

    func shareDirectWhatsApp(phoneNumber: String, message: String, image: UIImage?) {
        let cleaned = phoneNumber.filter { $0.isNumber }
        let formatted = cleaned.hasPrefix("0") ? "90" + cleaned.dropFirst() : (cleaned.hasPrefix("90") ? cleaned : "90" + cleaned)
        let urlString = "https://wa.me/\(formatted)?text=\(urlEncoded(message))"

        if let url = URL(string: urlString), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        } else {
            shareUniversal(message: message, image: image)
        }
    }

    private func shareToWhatsApp(message: String, image: UIImage?, presenter: UIViewController?) {
        let encoded = urlEncoded(message)
        if let url = URL(string: "whatsapp://send?text=\(encoded)"), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        } else {
            shareUniversal(message: message, image: image, presenter: presenter)
        }
    }

    private func shareViaScheme(scheme: String, fallbackPresenter: UIViewController?, message: String, image: UIImage?) {
        if let url = URL(string: scheme), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        } else {
            shareUniversal(message: message, image: image, presenter: fallbackPresenter)
        }
    }

    func shareUniversal(message: String, image: UIImage?, presenter: UIViewController? = nil) {
        var items: [Any] = [message]
        if let img = image {
            items.insert(img, at: 0)
        }

        let activityVC = UIActivityViewController(activityItems: items, applicationActivities: nil)
        let targetVC = presenter ?? getTopViewController()

        if let popover = activityVC.popoverPresentationController {
            popover.sourceView = targetVC?.view
            popover.sourceRect = CGRect(x: (targetVC?.view.bounds.midX ?? 0), y: (targetVC?.view.bounds.midY ?? 0), width: 0, height: 0)
            popover.permittedArrowDirections = []
        }

        targetVC?.present(activityVC, animated: true)
    }

    private func urlEncoded(_ string: String) -> String {
        string.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? string
    }

    private func getTopViewController() -> UIViewController? {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController else {
            return nil
        }
        var topVC = rootVC
        while let presented = topVC.presentedViewController {
            topVC = presented
        }
        return topVC
    }
}
