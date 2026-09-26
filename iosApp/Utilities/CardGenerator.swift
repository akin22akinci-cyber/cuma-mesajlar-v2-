import UIKit

final class CardGenerator {
    static let shared = CardGenerator()

    func generateCardImage(
        backgroundImage: UIImage,
        messageText: String,
        signature: String = "",
        recipientName: String = "",
        fontStyle: String = "Serif",
        frameStyle: String = "Gold",
        textSizeModifier: Double = 1.0,
        textColorHex: String = "#FFFFFF"
    ) -> UIImage {
        let targetSize = CGSize(width: 1080, height: 1440)
        let renderer = UIGraphicsImageRenderer(size: targetSize)

        return renderer.image { ctx in
            let cgContext = ctx.cgContext

            // 1. Draw base photo
            backgroundImage.draw(in: CGRect(origin: .zero, size: targetSize))

            // 2. Gradient overlay over lower 75%
            let colors = [
                UIColor(red: 0, green: 0, blue: 0, alpha: 0.1).cgColor,
                UIColor(red: 2/255.0, green: 22/255.0, blue: 16/255.0, alpha: 0.70).cgColor,
                UIColor(red: 1/255.0, green: 12/255.0, blue: 9/255.0, alpha: 0.92).cgColor,
                UIColor(red: 0, green: 8/255.0, blue: 6/255.0, alpha: 0.98).cgColor
            ] as CFArray
            let colorSpace = CGColorSpaceCreateDeviceRGB()
            let locations: [CGFloat] = [0.0, 0.35, 0.75, 1.0]

            if let gradient = CGGradient(colorsSpace: colorSpace, colors: colors, locations: locations) {
                let startPoint = CGPoint(x: 0, y: targetSize.height * 0.22)
                let endPoint = CGPoint(x: 0, y: targetSize.height)
                cgContext.drawLinearGradient(gradient, start: startPoint, end: endPoint, options: [])
            }

            // Top soft vignette
            let topColors = [
                UIColor(red: 0, green: 16/255.0, blue: 11/255.0, alpha: 0.75).cgColor,
                UIColor.clear.cgColor
            ] as CFArray
            if let topGrad = CGGradient(colorsSpace: colorSpace, colors: topColors, locations: [0.0, 1.0]) {
                cgContext.drawLinearGradient(topGrad, start: .zero, end: CGPoint(x: 0, y: 260), options: [])
            }

            // 3. Frame
            let frameMargin: CGFloat = 44.0
            let frameColor: UIColor
            switch frameStyle {
            case "Emerald":
                frameColor = UIColor(red: 56/255.0, green: 211/255.0, blue: 159/255.0, alpha: 1.0)
            case "Minimal":
                frameColor = UIColor(white: 1.0, alpha: 0.35)
            case "Lantern":
                frameColor = UIColor(red: 255/255.0, green: 213/255.0, blue: 79/255.0, alpha: 1.0)
            default: // Gold
                frameColor = UIColor(red: 229/255.0, green: 190/255.0, blue: 101/255.0, alpha: 1.0)
            }

            if frameStyle != "Minimal" {
                cgContext.setStrokeColor(frameColor.cgColor)
                cgContext.setLineWidth(4.0)
                let outerRect = CGRect(x: frameMargin, y: frameMargin, width: targetSize.width - frameMargin * 2, height: targetSize.height - frameMargin * 2)
                cgContext.stroke(outerRect)

                cgContext.setLineWidth(1.5)
                cgContext.setStrokeColor(frameColor.withAlphaComponent(0.5).cgColor)
                let innerRect = outerRect.insetBy(dx: 12, dy: 12)
                cgContext.stroke(innerRect)
            }

            // 4. Header & Bismillah
            let ornamentText = frameStyle == "Lantern" ? "✨ ﷽ ✨" : (frameStyle == "Emerald" ? "🌿 ﷽ 🌿" : "✦ ﷽ ✦")
            let ornamentAttrs: [NSAttributedString.Key: Any] = [
                .font: UIFont.systemFont(ofSize: 34, weight: .medium),
                .foregroundColor: frameColor
            ]
            let ornamentSize = (ornamentText as NSString).size(withAttributes: ornamentAttrs)
            (ornamentText as NSString).draw(at: CGPoint(x: (targetSize.width - ornamentSize.width) / 2, y: 110), withAttributes: ornamentAttrs)

            let headerText = "HAYIRLI CUMALAR"
            let headerFont: UIFont
            if #available(iOS 13.0, *) {
                let descriptor = UIFont.systemFont(ofSize: 56, weight: .bold).fontDescriptor.withDesign(.serif) ?? UIFont.boldSystemFont(ofSize: 56).fontDescriptor
                headerFont = UIFont(descriptor: descriptor, size: 56)
            } else {
                headerFont = UIFont.boldSystemFont(ofSize: 56)
            }

            let headerAttrs: [NSAttributedString.Key: Any] = [
                .font: headerFont,
                .foregroundColor: UIColor(red: 255/255.0, green: 243/255.0, blue: 208/255.0, alpha: 1.0)
            ]
            let headerSize = (headerText as NSString).size(withAttributes: headerAttrs)
            (headerText as NSString).draw(at: CGPoint(x: (targetSize.width - headerSize.width) / 2, y: 190), withAttributes: headerAttrs)

            // Divider line
            let divY: CGFloat = 265.0
            cgContext.setStrokeColor(frameColor.cgColor)
            cgContext.setLineWidth(3.0)
            cgContext.move(to: CGPoint(x: targetSize.width / 2 - 180, y: divY))
            cgContext.addLine(to: CGPoint(x: targetSize.width / 2 + 180, y: divY))
            cgContext.strokePath()

            // 5. Message Text
            var processed = messageText
            if !recipientName.isEmpty {
                processed = processed.replacingOccurrences(of: "{isim}", with: recipientName)
            } else {
                processed = processed.replacingOccurrences(of: "{isim}", with: "").trimmingCharacters(in: .whitespacesAndNewlines)
            }

            let font: UIFont
            let fontSize = CGFloat(42.0 * textSizeModifier)
            switch fontStyle {
            case "Sans":
                font = UIFont.systemFont(ofSize: fontSize, weight: .regular)
            case "Script":
                font = UIFont.italicSystemFont(ofSize: fontSize)
            case "Bold":
                font = UIFont.boldSystemFont(ofSize: fontSize)
            default: // Serif
                if #available(iOS 13.0, *), let desc = UIFont.systemFont(ofSize: fontSize).fontDescriptor.withDesign(.serif) {
                    font = UIFont(descriptor: desc, size: fontSize)
                } else {
                    font = UIFont.systemFont(ofSize: fontSize)
                }
            }

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.alignment = .center
            paragraphStyle.lineSpacing = 10.0

            let textAttrs: [NSAttributedString.Key: Any] = [
                .font: font,
                .foregroundColor: UIColor(hex: textColorHex) ?? UIColor.white,
                .paragraphStyle: paragraphStyle
            ]

            let textWidth = targetSize.width - (frameMargin * 2) - 80
            let bounding = (processed as NSString).boundingRect(
                with: CGSize(width: textWidth, height: CGFloat.greatestFiniteMagnitude),
                options: [.usesLineFragmentOrigin, .usesFontLeading],
                attributes: textAttrs,
                context: nil
            )

            let availableHeight = targetSize.height - 300 - 180
            let textTopY = 300 + (availableHeight - bounding.height) / 2
            let textRect = CGRect(x: (targetSize.width - textWidth) / 2, y: textTopY, width: textWidth, height: bounding.height)
            (processed as NSString).draw(in: textRect, withAttributes: textAttrs)

            // 6. Signature
            let signText = !signature.isEmpty ? signature : (!recipientName.isEmpty ? "Selam ve Dua İle..." : "Dualarınızın Kabul Olması Dileğiyle 🤲")
            let signAttrs: [NSAttributedString.Key: Any] = [
                .font: UIFont.italicSystemFont(ofSize: 34),
                .foregroundColor: frameColor
            ]
            let signSize = (signText as NSString).size(withAttributes: signAttrs)
            (signText as NSString).draw(at: CGPoint(x: (targetSize.width - signSize.width) / 2, y: targetSize.height - 95), withAttributes: signAttrs)
        }
    }
}

// Hex color parser for iOS UIColor
extension UIColor {
    convenience init?(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        if hexSanitized.hasPrefix("#") {
            hexSanitized.remove(at: hexSanitized.startIndex)
        }
        guard hexSanitized.count == 6 else { return nil }
        var rgbValue: UInt64 = 0
        Scanner(string: hexSanitized).scanHexInt64(&rgbValue)

        self.init(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: 1.0
        )
    }
}
