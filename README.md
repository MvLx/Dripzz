# TripFlex - Referensi Tempat Nongs Buat Kawula Muda!

**TripFlex** adalah aplikasi Android yang membantu Anda menemukan tempat nongkrong, kuliner, dan destinasi menarik di berbagai kota di Indonesia. Data tempat diambil dari Foursquare API dan lokasi dari OpenStreetMap Nominatim API. Aplikasi ini juga mendukung mode offline cache dan fitur favorit.

## Fitur Utama

- Cari Tempat: Temukan tempat menarik berdasarkan kota atau lokasi yang Anda masukkan.
- Daftar Kota Populer: Pilihan cepat kota-kota besar di Indonesia.
- Detail Tempat: Lihat detail lengkap, jam buka, kategori, alamat, dan foto tempat.
- Favorit: Simpan tempat favorit Anda untuk akses cepat.
- Offline Cache: Data tempat dan gambar disimpan secara lokal untuk akses tanpa internet.
- Mode Gelap/Terang: Ubah tampilan aplikasi sesuai preferensi Anda.

## Screenshot

![WhatsApp Image 2025-06-10 at 23 50 33](https://github.com/user-attachments/assets/d07a1d9f-cb3b-4f0e-bd1c-d68d7d15664f)
![WhatsApp Image 2025-06-10 at 23 50 33 (1)](https://github.com/user-attachments/assets/b81727a2-c0f5-4db8-9f8c-6da234954dec)


## Instalasi & Menjalankan Aplikasi

### 1. Clone Repository

```bash
git clone https://github.com/username/dripz.git
cd dripz
```

### 2. Buka di Android Studio

- Buka **Android Studio**.
- Pilih **Open an existing project**.
- Arahkan ke folder hasil clone (`dripz`).

### 3. Konfigurasi API Key

- API Key Foursquare sudah tertanam di kode (`fsq3a4FzRMpB8kLrNHnB8bJgY+nTbIDEOtk7088yl5pCI4A=`).
- Untuk penggunaan produksi, sebaiknya ganti API Key di file berikut:
  - `app/src/main/java/com/example/dripz/adapter/PlaceAdapter.java`
  - `app/src/main/java/com/example/dripz/ui/detail/DetailPlaceActivity.java`
  - `app/src/main/java/com/example/dripz/ui/home/HomeFragment.java`

### 4. Jalankan Aplikasi

- Hubungkan perangkat Android atau gunakan emulator.
- Klik **Run** (ikon ▶️) di Android Studio.

### 5. Hak Akses

Pastikan aplikasi memiliki izin akses internet dan penyimpanan (storage) untuk menyimpan cache gambar.

## Struktur Proyek

- `adapter/` — Adapter RecyclerView untuk daftar kota dan tempat.
- `db/` — Helper SQLite untuk cache dan favorit.
- `model/` — Model data (Place, City, dsb).
- `network/` — Retrofit API interface untuk Foursquare & Geocoding.
- `ui/` — Fragment dan Activity utama aplikasi.
- `util/` — Utility seperti image downloader.

## Kontribusi

Pull request dan issue sangat terbuka untuk pengembangan lebih lanjut!

## Lisensi

MIT License

---

**Tata Cara Download:**
1. Klik tombol **Code** di halaman GitHub, lalu pilih **Download ZIP**.
2. Ekstrak file ZIP.
3. Buka folder hasil ekstrak di Android Studio.
4. Ikuti langkah instalasi di atas.

---

**Catatan:**  
Aplikasi ini menggunakan data dari Foursquare dan OpenStreetMap. Pastikan penggunaan API sesuai dengan terms of service masing-masing layanan.
