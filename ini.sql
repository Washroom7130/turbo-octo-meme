-- Create database
CREATE DATABASE IF NOT EXISTS event_management_sys;
USE event_management_sys;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'api_manager'@'%' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON event_management_sys.* TO 'api_manager'@'%';
FLUSH PRIVILEGES;

-- Table: TaiKhoan
CREATE TABLE TaiKhoan (
    maTaiKhoan VARCHAR(50) PRIMARY KEY,
    tenDangNhap VARCHAR(100) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL,
    ngayTao DATE,
    trangThai VARCHAR(50),
    vaiTro VARCHAR(50),
    xacMinhEmail BOOLEAN,
    maNguoiDung VARCHAR(50)
);

-- Table: QuanLy
CREATE TABLE QuanLy (
    maQuanLy VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100),
    diaChi VARCHAR(255),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    gioiTinh VARCHAR(10),
    soTuoi INT
);

-- Table: NhanVien
CREATE TABLE NhanVien (
    maNhanVien VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100),
    diaChi VARCHAR(255),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    gioiTinh VARCHAR(10),
    soTuoi INT
);

-- Table: KhachHang
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(50) PRIMARY KEY,
    hoTen VARCHAR(100),
    diaChi VARCHAR(255),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    gioiTinh VARCHAR(10),
    soTuoi INT
);

-- Table: DanhMucSuKien
CREATE TABLE DanhMucSuKien (
    maDanhMuc VARCHAR(50) PRIMARY KEY,
    tenDanhMuc VARCHAR(100)
);

-- Table: SuKien
CREATE TABLE SuKien (
    maSuKien VARCHAR(50) PRIMARY KEY,
    tenSuKien VARCHAR(100),
    moTa TEXT,
    anhSuKien VARCHAR(255),
    diaDiem VARCHAR(255),
    trangThaiSuKien VARCHAR(50),
    phiThamGia FLOAT,
    luongChoNgoi INT,
    ngayTaoSuKien DATETIME,
    ngayBatDau DATETIME,
    ngayKetThuc DATETIME,
    maDanhMuc VARCHAR(50),
    FOREIGN KEY (maDanhMuc) REFERENCES DanhMucSuKien(maDanhMuc)
);

-- Table: DangKy
CREATE TABLE DangKy (
    maDangKy VARCHAR(50) PRIMARY KEY,
    ngayDangKy DATE,
    viTriGhe VARCHAR(50),
    trangThaiDangKy VARCHAR(50),
    maKhachHang VARCHAR(50),
    maSuKien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: HoaDon
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    ngayTao DATETIME,
    trangThaiHoaDon VARCHAR(50),
    tongTien FLOAT,
    thoiGianHieuLuc INT,
    thoiGianThanhCong DATETIME,
    phuongThucThanhToan VARCHAR(50),
    maKhachHang VARCHAR(50),
    maDangKy VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maDangKy) REFERENCES DangKy(maDangKy)
);

-- Table: DanhGia
CREATE TABLE DanhGia (
    maDanhGia VARCHAR(50) PRIMARY KEY,
    loaiDanhGia INT,
    binhLuan TEXT,
    ngayDanhGia DATE,
    maKhachHang VARCHAR(50),
    maSuKien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: CauHoi
CREATE TABLE CauHoi (
    maCauHoi VARCHAR(50) PRIMARY KEY,
    noiDungCauHoi TEXT,
    noiDungTraLoi TEXT,
    trangThai VARCHAR(50),
    maKhachHang VARCHAR(50),
    maSuKien VARCHAR(50),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maSuKien) REFERENCES SuKien(maSuKien)
);

-- Table: DiemDanh
CREATE TABLE DiemDanh (
    maDiemDanh VARCHAR(50) PRIMARY KEY,
    ngayTaove DATETIME,
    ngayDiemDanh DATETIME,
    trangThaiDiemDanh VARCHAR(50),
    viTriGheNgoi VARCHAR(50),
    maDangKy VARCHAR(50),
    FOREIGN KEY (maDangKy) REFERENCES DangKy(maDangKy)
);

-- Table: Ticket
CREATE TABLE Ticket (
    maHoTro VARCHAR(50) PRIMARY KEY,
    tenKhachHang VARCHAR(100),
    email VARCHAR(100),
    noiDung TEXT,
    noiDungGiaiDap TEXT,
    trangThai VARCHAR(50),
    maNhanVien VARCHAR(50),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);
