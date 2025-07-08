package com.api.Event_Management_API.util;

import java.util.Map;

import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.QuanLy;

public class ThongTinCaNhanUtil {
    
    public static void getThongTinCaNhan(Map<String, Object> map, Object obj) {
        if (obj instanceof KhachHang kh) {
            map.put("hoTen", kh.getHoTen());
            map.put("diaChi", kh.getDiaChi());
            map.put("email", kh.getEmail());
            map.put("phone", kh.getPhone());
            map.put("gioiTinh", kh.getGioiTinh());
            map.put("soTuoi", kh.getSoTuoi());
        } else if (obj instanceof NhanVien nv) {
            map.put("hoTen", nv.getHoTen());
            map.put("diaChi", nv.getDiaChi());
            map.put("email", nv.getEmail());
            map.put("phone", nv.getPhone());
            map.put("gioiTinh", nv.getGioiTinh());
            map.put("soTuoi", nv.getSoTuoi());
        } else if (obj instanceof QuanLy ql) {
            map.put("hoTen", ql.getHoTen());
            map.put("diaChi", ql.getDiaChi());
            map.put("email", ql.getEmail());
            map.put("phone", ql.getPhone());
            map.put("gioiTinh", ql.getGioiTinh());
            map.put("soTuoi", ql.getSoTuoi());
        }
    }
}
