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
    maQuanLy VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    diaChi VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gioiTinh VARCHAR(10),
    soTuoi INT NOT NULL
);

-- Table: NhanVien
CREATE TABLE NhanVien (
    maNhanVien VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    diaChi VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gioiTinh VARCHAR(10),
    soTuoi INT NOT NULL
);

-- Table: KhachHang
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100) NOT NULL,
    diaChi VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gioiTinh VARCHAR(10),
    soTuoi INT NOT NULL
);

-- Table: TaiKhoan
CREATE TABLE TaiKhoan (
    maTaiKhoan VARCHAR(50) PRIMARY KEY,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(60) NOT NULL,
    ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    trangThai VARCHAR(50),
    vaiTro VARCHAR(20),
    xacMinhEmail BOOLEAN DEFAULT FALSE,
    maKhachHang VARCHAR(50),
    maQuanLy VARCHAR(50),
    maNhanVien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maQuanLy) REFERENCES QuanLy(maQuanLy),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- Table: DanhMucSuKien
CREATE TABLE DanhMucSuKien (
    maDanhMuc VARCHAR(50) PRIMARY KEY,
    tenDanhMuc VARCHAR(50) NOT NULL
);

-- Table: SuKien
CREATE TABLE SuKien (
    maSuKien VARCHAR(50) PRIMARY KEY,
    tenSuKien VARCHAR(100) NOT NULL,
    moTa TEXT,
    anhSuKien VARCHAR(70),
    diaDiem VARCHAR(255),
    trangThaiSuKien VARCHAR(20) DEFAULT 'Còn chỗ',
    phiThamGia DECIMAL(18,2),
    luongChoNgoi INT,
    ngayTaoSuKien DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngayBatDau DATETIME,
    ngayKetThuc DATETIME,
    maDanhMuc VARCHAR(50),
    FOREIGN KEY (maDanhMuc) REFERENCES DanhMucSuKien(maDanhMuc)
);

-- Table: DangKy
CREATE TABLE DangKy (
    maDangKy VARCHAR(50) PRIMARY KEY,
    ngayDangKy DATETIME DEFAULT CURRENT_TIMESTAMP,
    viTriGhe VARCHAR(10) NOT NULL,
    trangThaiDangKy VARCHAR(20) DEFAULT 'Đang xử lý',
    maKhachHang VARCHAR(50),
    maSuKien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    trangThaiHoaDon VARCHAR(20) DEFAULT 'Chưa thanh toán',
    tongTien DECIMAL(18,2),
    thoiGianHieuLuc DATETIME NOT NULL,
    thoiGianThanhCong DATETIME NOT NULL,
    phuongThucThanhToan VARCHAR(20) DEFAULT 'Qua ngân hàng',
    maKhachHang VARCHAR(50),
    maDangKy VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maDangKy) REFERENCES DangKy(maDangKy)
);

-- Table: DanhGia
CREATE TABLE DanhGia (
    maDanhGia VARCHAR(50) PRIMARY KEY,
    loaiDanhGia INT,
    binhLuan TEXT NOT NULL,
    ngayDanhGia DATETIME DEFAULT CURRENT_TIMESTAMP,
    maKhachHang VARCHAR(50),
    maSuKien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: CauHoi
CREATE TABLE CauHoi (
    maCauHoi VARCHAR(50) PRIMARY KEY,
    noiDungCauHoi TEXT NOT NULL,
    noiDungTraLoi TEXT NOT NULL,
    trangThai VARCHAR(20) DEFAULT 'Chưa xử lý',
    maKhachHang VARCHAR(50),
    maSuKien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: DiemDanh
CREATE TABLE DiemDanh (
    maDiemDanh VARCHAR(50) PRIMARY KEY,
    ngayTaove DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngayDiemDanh DATETIME NOT NULL,
    trangThaiDiemDanh VARCHAR(20) DEFAULT 'Vắng mặt',
    viTriGheNgoi VARCHAR(10) NOT NULL,
    maDangKy VARCHAR(50),
    FOREIGN KEY (maDangKy) REFERENCES DangKy(maDangKy)
);

-- Table: Ticket
CREATE TABLE Ticket (
    maHoTro VARCHAR(50) PRIMARY KEY,
    tenKhachHang VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    noiDung TEXT NOT NULL,
    noiDungGiaiDap TEXT,
    trangThai VARCHAR(20) DEFAULT 'Chưa xử lý',
    maNhanVien VARCHAR(50),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- Table Token
CREATE TABLE Token (
    maToken VARCHAR(50) PRIMARY KEY,
    loaiToken VARCHAR(20) NOT NULL,
    thoiDiemHetHan DATETIME,
    maTaiKhoan VARCHAR(50),
    FOREIGN KEY (maTaiKhoan) REFERENCES TaiKhoan(maTaiKhoan)
);