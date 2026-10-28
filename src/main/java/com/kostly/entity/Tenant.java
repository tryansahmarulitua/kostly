package com.kostly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "tenants",
       uniqueConstraints = @UniqueConstraint(columnNames = "nik"))
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama lengkap wajib diisi")
    @Column(nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "NIK wajib diisi")
    @Pattern(regexp = "\\d{16}", message = "NIK harus terdiri dari 16 digit")
    @Column(nullable = false, unique = true, length = 16)
    private String nik;

    @NotBlank(message = "Nomor telepon wajib diisi")
    @Column(nullable = false, length = 20)
    private String phone;

    @Email(message = "Format email tidak valid")
    @Column(length = 100)
    private String email;

    @NotBlank(message = "Alamat wajib diisi")
    @Column(nullable = false, length = 500)
    private String address;

    public Tenant() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
    }
}