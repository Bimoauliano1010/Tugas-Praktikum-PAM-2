# News Feed Simulator

Proyek **News Feed Simulator** adalah aplikasi Kotlin Multiplatform (KMP) + Compose Multiplatform yang dikembangkan untuk Tugas Praktikum Pengembangan Aplikasi Mobile (PAM) ITERA.

## 📌 Deskripsi Singkat
Aplikasi ini mensimulasikan aliran berita secara langsung (*real-time*). Berita baru akan dikirim dan dimunculkan secara otomatis setiap 2 detik menggunakan Kotlin Flow, disaring berdasarkan kategori (`filter`), diubah format tampilannya (`map`), dicatat aktivitasnya (`onEach`), disimpan jumlah berita yang sudah dibaca (`StateFlow`), serta dimuat detail beritanya secara latar belakang tanpa membuat aplikasi macet/freeze (`Coroutines`).

## 📱 Screenshot:
<p align="center">
  <img src="screenshot.png" alt="Screenshot News Feed Simulator" width="350" />
</p>

## 🛠️ Teknologi yang Digunakan
- **Kotlin Multiplatform (KMP)**
- **Compose Multiplatform**
- **Kotlin Coroutines & Flow**
- **Compose Material 3**

## 🚀 Cara Menjalankan Project

### Android
1. Buka project di **Android Studio**.
2. Pilih run configuration `androidApp`.
3. Buka emulator / hubungkan perangkat fisik Android.
4. Klik tombol **Run** atau jalankan perintah:
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

### Desktop (JVM)
```bash
./gradlew :desktopApp:run
```
