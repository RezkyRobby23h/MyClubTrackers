# MyClubTrackers

Aplikasi Android untuk melacak jadwal, klasemen, klub, dan statistik pemain sepak bola dari berbagai liga dunia secara real-time dan offline.

---

<p align="center">
  <a href="#fitur-utama">Fitur Utama</a> •
  <a href="#prasyarat">Prasyarat</a> •
  <a href="#instalasi--menjalankan">Instalasi &amp; Menjalankan</a> •
  <a href="#struktur-proyek">Struktur Proyek</a> •
  <a href="#teknologi-yang-digunakan">Teknologi yang Digunakan</a> •
  <a href="#screenshot">Screenshot</a> •
  <a href="#menyimpan-gambar">Menyimpan Gambar</a> •
  <a href="#kontribusi">Kontribusi</a> •
  <a href="#lisensi">Lisensi</a>
</p>

---

## Fitur Utama

- **Jadwal & Hasil Pertandingan**  
  Lihat jadwal pertandingan dan hasil terbaru dari liga favorit.
- **Klasemen Liga**  
  Pantau posisi klub-klub di klasemen liga.
- **Top Skor & Assist**  
  Daftar pemain dengan gol dan assist terbanyak.
- **Mode Gelap (Dark Mode)**
- **Offline Mode**  
  Data yang pernah diakses tetap tersedia tanpa internet.
- **Pengaturan Liga & Musim**  
  Pilih liga dan musim favorit.

---

## Prasyarat

Sebelum menjalankan aplikasi, pastikan Anda telah memenuhi persyaratan berikut:

- **Android Studio** versi terbaru
- **JDK** 8 atau lebih baru
- **Gradle** (otomatis melalui Android Studio)
- **Koneksi Internet** (untuk pengambilan data awal)
- **API Key** dari [API-Football]([https://dashboard.api-football.com/register](https://dashboard.api-football.com/register))
- **Perangkat/Emulator** Android minimal API 21 (Lollipop)

---

## Instalasi & Menjalankan

1. **Clone repository ini**
    ```sh
    git clone https://github.com/username/MyClubTrackers.git
    ```
2. **Buka di Android Studio**
3. **Dapatkan API Key**  
   Daftar di [API-Football](https://dashboard.api-football.com/register) dan salin API Key Anda.
4. **Masukkan API Key**  
   Buka file:
   ```
   app/src/main/java/com/example/myclubtrackers/network/RetrofitClient.java
   ```
   Ganti nilai variabel `API_KEY` dengan API Key Anda.
5. **Build & Jalankan**  
   Klik tombol `Run` di Android Studio atau gunakan:
    ```sh
    ./gradlew assembleDebug
    ```

---

## Struktur Proyek

```
.
├── MainActivity.java
├── MatchDetailActivity.java
├── adapter/
│   ├── ClubAdapter.java
│   ├── MatchAdapter.java
│   ├── PlayerAdapter.java
│   └── StandingsAdapter.java
├── database/
│   ├── AppDatabase.java
│   ├── dao/
│   └── entity/
├── fragment/
│   ├── ClubFragment.java
│   ├── HomeFragment.java
│   ├── SettingFragment.java
│   └── TopPlayerFragment.java
├── model/
│   ├── Club.java
│   ├── Match.java
│   └── PlayerStats.java
├── network/
│   ├── ApiService.java
│   ├── RetrofitClient.java
│   └── response/
└── utils/
    ├── DateFormatter.java
    ├── NetworkUtils.java
    ├── SharedPrefManager.java
    ├── StringListConverter.java
    └── ThemeUtils.java
```

---

## Teknologi yang Digunakan

- **Java** & Android Jetpack (Fragment, Room, ViewBinding)
- **Retrofit2** (REST API)
- **Room** (Database lokal)
- **Glide** (Pemuatan gambar)
- **Material Components** (UI)
- **API-Football** (Sumber data sepak bola)

---

## Screenshot

<p align="center">
  <img src="https://github.com/user-attachments/assets/bebc1674-317b-443e-8fa5-21ef385e39e0" alt="Screenshot 1" width="250"/>
  <img src="https://github.com/user-attachments/assets/f17fcb0b-9618-4922-8a4f-1c831415bf4d" alt="Screenshot 2" width="250"/>
  <img src="https://github.com/user-attachments/assets/5ef1af27-0985-40c1-99f4-49421397ee9a" alt="Screenshot 3" width="250"/>
  <img src="https://github.com/user-attachments/assets/3adafc41-1ed2-406f-b62e-bf937f72fd43" alt="Screenshot 4" width="250"/>
  <img src="https://github.com/user-attachments/assets/feb8b7ce-9912-4501-87d1-787a64d6f14f" alt="Screenshot 5" width="250"/>
</p>

---

## Kontribusi

Kontribusi sangat terbuka!  
Silakan buat pull request atau buka issue untuk fitur/bug yang ingin didiskusikan.

---

## Lisensi

Proyek ini dibuat untuk keperluan akademik di Universitas Hasanuddin, Semester 4, Lab Pemrograman Mobile.

---

> Dibuat dengan ❤️ oleh RezkyRobby
