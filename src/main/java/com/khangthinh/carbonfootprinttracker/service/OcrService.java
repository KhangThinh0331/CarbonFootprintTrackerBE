package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.OcrResponse;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    OcrResponse scanReceipt(MultipartFile file);
}
