package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.OcrResponse;
import com.khangthinh.carbonfootprinttracker.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(value = "/scan", consumes = "multipart/form-data")
    public ResponseEntity<?> scanReceipt(@RequestParam("file") MultipartFile file) {
        // Kiểm tra xem file có bị rỗng không
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng tải lên một tệp ảnh hợp lệ.");
        }

        // Gọi Service xử lý
        OcrResponse response = ocrService.scanReceipt(file);
        return ResponseEntity.ok(response);
    }
}