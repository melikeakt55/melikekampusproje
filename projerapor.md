# melikekampusproje

 Kampüs Etkinlik ve Sosyal Paylaşım Platformu (CampusEvents)
Bu proje, üniversite öğrencilerinin kampüs içerisindeki sosyal etkileşimini artırmak, etkinliklerden haberdar olmalarını sağlamak ve kulüp sorumlularının etkinliklerini yönetmelerini kolaylaştırmak amacıyla geliştirilmiş bir Android uygulamasıdır.
🚀 Özellikler
•
Çift Rol Sistemli Üyelik: Kullanıcılar "Öğrenci" veya "Kulüp Sorumlusu" (Temsilci) olarak kayıt olabilirler.







•
Etkinlik Yönetimi: Kulüp sorumluları yeni etkinlikler oluşturabilir; tüm kullanıcılar etkinlikleri görüntüleyip "Katıl" butonu ile takibe alabilir.



•
Sosyal Akış (Social Feed): Kullanıcıların anlık düşüncelerini veya duyurularını paylaşabildiği, profil fotoğraflarının göründüğü dinamik bir akış ekranı.




•
Gelişmiş Profil Yönetimi: Kullanıcılar biyografilerini, okul/bölüm bilgilerini güncelleyebilir ve galeri üzerinden profil fotoğrafı yükleyebilir.



•
Oturum Yönetimi: SessionManager ile kullanıcı çıkış yapana kadar oturumun açık kalması sağlanır.




•
Yerel Veritabanı: Tüm veriler (Kullanıcılar, Etkinlikler, Katılımlar, Postlar) SQLite ile cihazda güvenli bir şekilde saklanır.


🛠️ Kullanılan Teknolojiler
•
Dil: Java


•
Veritabanı: SQLite (Local Database)


•
Arayüz: XML, Material Design Bileşenleri
•
Mimari Yapı: Fragment-based Navigation, ViewPager2, Navigation Drawer, Custom Adapters


•
Görsel İşleme: URI tabanlı yerel görsel depolama ve ölçeklendirme

📂 Proje Yapısı

☕ Java Sınıfları

•
DBHelper.java: Veritabanı şeması ve tüm SQL operasyonları (CRUD).


•
MainActivity.java: Ana ekran navigasyonu ve kullanıcı arayüzü kontrolü.


•
SessionManager.java: SharedPreferences tabanlı oturum yönetimi.


•
ProfileEditActivity.java: Profil bilgilerinin ve fotoğrafının güncellendiği modül.


•
SosyalFragment.java: Sosyal medya akışının mantıksal işleyişi.


•
ViewPagerAdapter.java: Sekmeler arası (Etkinlik/Geçmiş/Sosyal) geçiş yönetimi.


🎨 XML Tasarımları


•
anaekran.xml: TabLayout ve DrawerLayout içeren ana iskelet.


•
activity_login.xml & activity_register.xml: Modern form tasarımları.


•
item_sosyal_post.xml: Sosyal akıştaki paylaşımlar için özel kart tasarımı.



•
etkinlikkart.xml: Etkinlik listesi için kullanılan görsel bileşen.



•
input_border.xml: Özelleştirilmiş kavisli giriş alanları (Custom EditText).


📊 Veritabanı Şeması


Uygulama CampusEvents.db adında 4 ana tablodan oluşan bir yapı kullanır:


1.
users: ID, Kullanıcı Adı, Şifre, Rol, Email, Biyografi, Okul, Bölüm, Kulüp Adı, Profil Resmi.


3.
events: Etkinlik Adı, Açıklama, Tarih, Konum, Sahibi.


5.
participations: Etkinlik ID, Kullanıcı Adı, Durum.



7.
posts: Kullanıcı Adı, İçerik, Zaman Damgası.



🛠️ Kurulum ve Çalıştırma

1.
Bu depoyu klonlayın: git clone https://github.com/kullaniciadi/proje-adiniz.git


3.
Android Studio'yu açın ve projeyi içe aktarın (Import Project).



5.
Gradle senkronizasyonunun tamamlanmasını bekleyin.



7.
Bir emülatör veya gerçek Android cihaz bağlayarak Run butonuna basın.

