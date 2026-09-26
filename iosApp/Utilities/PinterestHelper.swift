import Foundation
import UIKit

struct MosqueAsset: Identifiable {
    let id: String
    let title: String
    let imageName: String
    let recommendedPrayer: String
    let tag: String
}

final class PinterestHelper {
    static let shared = PinterestHelper()
    static let pinterestSearchURL = "https://tr.pinterest.com/search/pins/?q=cuma%20mesajlar%C4%B1%20cami%20ve%20g%C3%BCzel&rs=typed"

    let curatedGallery: [MosqueAsset] = [
        MosqueAsset(
            id: "mosque_sunset",
            title: "Sultanahmet Günbatımı & Kuşlar",
            imageName: "img_mosque_sunset",
            recommendedPrayer: "Günün batışıyla dertleriniz son bulsun, Cuma vaktinin feyziyle kalbiniz huzur dolsun. Rabbim tüm hayırlı dualarınızı kabul eylesin {isim}.",
            tag: "Günbatımı & Huzur"
        ),
        MosqueAsset(
            id: "mosque_spiritual",
            title: "Manevi Avlu, Güller & Kandiller",
            imageName: "img_mosque_spiritual",
            recommendedPrayer: "Gül kokulu, nurlu bir Cuma gününe uyanmayı nasip eden Rabbimize hamd olsun. Hanenizden bereket, kalbinizden sevgi eksik olmasın.",
            tag: "Güller & Bereket"
        ),
        MosqueAsset(
            id: "mosque_night",
            title: "Mavi Camii ve Hilal Gecesi",
            imageName: "img_mosque_night",
            recommendedPrayer: "Karanlıkları nuruyla aydınlatan Yüce Allah, bu mübarek Cuma hürmetine gönlünüzdeki tüm hüzünleri sevince çevirsin. Hayırlı Cumalar {isim}!",
            tag: "Hilal & Cami"
        ),
        MosqueAsset(
            id: "mosque_interior",
            title: "Ulu Cami Hat & Işık Huzmeleri",
            imageName: "img_mosque_interior",
            recommendedPrayer: "Birlik ve huşû ile secdeye varan, dualarda buluşan kullardan eylesin. Cumanız mübarek, ameliniz makbul olsun.",
            tag: "Hat & Maneviyat"
        )
    ]

    func downloadImage(from urlString: String, completion: @escaping (UIImage?) -> Void) {
        guard let url = URL(string: urlString) else {
            completion(nil)
            return
        }

        URLSession.shared.dataTask(with: url) { data, _, _ in
            if let data = data, let image = UIImage(data: data) {
                DispatchQueue.main.async {
                    completion(image)
                }
            } else {
                DispatchQueue.main.async {
                    completion(nil)
                }
            }
        }.resume()
    }
}
