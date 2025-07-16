-- Drop database if exist
DROP DATABASE IF EXISTS event_management_sys;

-- Create database
CREATE DATABASE IF NOT EXISTS event_management_sys
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE event_management_sys;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'api_manager'@'%' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON event_management_sys.* TO 'api_manager'@'%';
FLUSH PRIVILEGES;

-- Table: QuanLy
CREATE TABLE QuanLy (
    maQuanLy INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    hoTen VARCHAR(100) NOT NULL,
    diaChi VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gioiTinh VARCHAR(10) NOT NULL,
    soTuoi INT NOT NULL
);

-- Table: NhanVien
CREATE TABLE NhanVien (
    maNhanVien INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    hoTen VARCHAR(100) NOT NULL,
    diaChi VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gioiTinh VARCHAR(10) NOT NULL,
    soTuoi INT NOT NULL
);

-- Table: KhachHang
CREATE TABLE KhachHang (
    maKhachHang INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    hoTen VARCHAR(100) NOT NULL,
    diaChi VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gioiTinh VARCHAR(10) NOT NULL,
    soTuoi INT NOT NULL
);

-- Table: TaiKhoan
CREATE TABLE TaiKhoan (
    maTaiKhoan VARCHAR(50) PRIMARY KEY NOT NULL,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(60) NOT NULL,
    ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    trangThai VARCHAR(50),
    vaiTro VARCHAR(20),
    xacMinhEmail BOOLEAN DEFAULT FALSE,
    maKhachHang INT,
    maQuanLy INT,
    maNhanVien INT,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maQuanLy) REFERENCES QuanLy(maQuanLy),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- Table: DanhMucSuKien
CREATE TABLE DanhMucSuKien (
    maDanhMuc INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tenDanhMuc VARCHAR(50) NOT NULL
);

-- Table: SuKien
CREATE TABLE SuKien (
    maSuKien INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tenSuKien VARCHAR(100) NOT NULL,
    moTa TEXT NOT NULL,
    anhSuKien VARCHAR(70),
    diaDiem VARCHAR(255) NOT NULL,
    trangThaiSuKien VARCHAR(20) DEFAULT 'Còn chỗ',
    phiThamGia DECIMAL(18,2) NOT NULL,
    luongChoNgoi INT NOT NULL,
    ngayTaoSuKien DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngayBatDau DATETIME NOT NULL,
    ngayKetThuc DATETIME NOT NULL,
    maDanhMuc INT,
    FOREIGN KEY (maDanhMuc) REFERENCES DanhMucSuKien(maDanhMuc)
);

-- Table: DangKy
CREATE TABLE DangKy (
    maDangKy VARCHAR(50) PRIMARY KEY NOT NULL,
    ngayDangKy DATETIME DEFAULT CURRENT_TIMESTAMP,
    viTriGhe VARCHAR(10) NOT NULL,
    trangThaiDangKy VARCHAR(20) DEFAULT 'Đang xử lý',
    maKhachHang INT,
    maSuKien INT,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY NOT NULL,
    ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    trangThaiHoaDon VARCHAR(20) DEFAULT 'Chưa thanh toán',
    tongTien DECIMAL(18,2),
    thoiGianHieuLuc DATETIME NOT NULL,
    thoiGianThanhCong DATETIME NOT NULL,
    phuongThucThanhToan VARCHAR(20) DEFAULT 'Qua ngân hàng',
    maKhachHang INT,
    maDangKy VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maDangKy) REFERENCES DangKy(maDangKy)
);

-- Table: DanhGia
CREATE TABLE DanhGia (
    maDanhGia INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    loaiDanhGia INT,
    binhLuan TEXT NOT NULL,
    ngayDanhGia DATETIME DEFAULT CURRENT_TIMESTAMP,
    maKhachHang INT,
    maSuKien INT,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: CauHoi
CREATE TABLE CauHoi (
    maCauHoi INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    noiDungCauHoi TEXT NOT NULL,
    noiDungTraLoi TEXT NOT NULL,
    trangThai VARCHAR(20) DEFAULT 'Chưa xử lý',
    maKhachHang INT,
    maNhanVien INT,
    maSuKien INT,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: DiemDanh
CREATE TABLE DiemDanh (
    maDiemDanh VARCHAR(50) PRIMARY KEY NOT NULL,
    ngayTaove DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngayDiemDanh DATETIME,
    trangThaiDiemDanh VARCHAR(20) DEFAULT 'Vắng mặt',
    viTriGheNgoi VARCHAR(10) NOT NULL,
    maDangKy VARCHAR(50),
    FOREIGN KEY (maDangKy) REFERENCES DangKy(maDangKy)
);

-- Table: Ticket
CREATE TABLE Ticket (
    maHoTro INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tenKhachHang VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    noiDung TEXT NOT NULL,
    noiDungGiaiDap TEXT,
    trangThai VARCHAR(20) DEFAULT 'Chưa xử lý',
    maNhanVien INT,
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- Table Token
CREATE TABLE Token (
    maToken VARCHAR(50) PRIMARY KEY NOT NULL,
    loaiToken VARCHAR(60) NOT NULL,
    thoiDiemHetHan DATETIME,
    maTaiKhoan VARCHAR(50),
    FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan)
);

-- Insert test admin data
-- Insert into QuanLy
INSERT INTO QuanLy (hoTen, diaChi, email, phone, gioiTinh, soTuoi)
VALUES (
    'redacted',
    'redacted',
    'redacted@abuse.com',
    'redacted',
    'Nam',
    18
);

-- Insert into TaiKhoan
INSERT INTO TaiKhoan (
    maTaiKhoan,
    tenDangNhap,
    matKhau,
    trangThai,
    vaiTro,
    xacMinhEmail,
    maQuanLy
)
VALUES (
    '66a580dc-3400-479d-a0cd-3e0fedcdf8db',
    'admin',
    '$2a$10$fd8mAqdcMoQPn/4R7sxeJugSmslPKWjLh4ahB/Wqxeosi.CgK38Py',
    'Hoạt Động',
    'QuanLy',
    TRUE,
    LAST_INSERT_ID()
);
