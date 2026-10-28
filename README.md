# Kostly — Spring Boot

Starter project sistem manajemen kost berbasis Java 17, Spring Boot 3.5.5, Thymeleaf, Bootstrap, Spring Security, Spring Data JPA, dan MySQL.

## Fitur yang tersedia
- Login admin dengan Spring Security dan password BCrypt
- Dashboard statistik kamar
- CRUD kamar (tambah, lihat, edit, hapus)
- Validasi input dasar
- UI responsif Bootstrap

## Persyaratan
- JDK 17
- Maven 3.6+
- MySQL 8+

## Cara menjalankan
1. Buat database (opsional karena URL memakai `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE kostly_db;
   ```
2. Edit `src/main/resources/application.properties`, sesuaikan `spring.datasource.username` dan `spring.datasource.password`.
3. Jalankan:
   ```bash
   mvn spring-boot:run
   ```
4. Buka `http://localhost:8080/login`.

Akun awal dibuat otomatis saat aplikasi pertama kali berjalan:
- Username: `admin`
- Password: `admin123`

Segera ganti password demo sebelum aplikasi dipublikasikan. Untuk pengembangan berikutnya, tambahkan modul penghuni, kontrak sewa, tagihan, pembayaran, pengeluaran, dan laporan.
