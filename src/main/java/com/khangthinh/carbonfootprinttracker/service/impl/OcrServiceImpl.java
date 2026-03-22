package com.khangthinh.carbonfootprinttracker.service.impl;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.khangthinh.carbonfootprinttracker.dto.OcrResponse;
import com.khangthinh.carbonfootprinttracker.service.OcrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrServiceImpl implements OcrService {
    @Value("${google.vision.credentials-path}")
    private String credentialsPath;
    // Khởi tạo Client kết nối với Google bằng file JSON
    private ImageAnnotatorClient getVisionClient() throws IOException {
        FileInputStream serviceAccountStream = new FileInputStream(credentialsPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        return ImageAnnotatorClient.create(settings);
    }

    @Override
    public OcrResponse scanReceipt(MultipartFile file) {
        try (ImageAnnotatorClient client = getVisionClient()) {
            // 1. Chuyển đổi file ảnh sang định dạng Google hiểu
            ByteString imgBytes = ByteString.readFrom(file.getInputStream());
            Image img = Image.newBuilder().setContent(imgBytes).build();

            // 2. Cấu hình yêu cầu (Chỉ yêu cầu đọc Text)
            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            // 3. Gửi cho Google và chờ kết quả
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            if (responses.isEmpty() || responses.get(0).hasError()) {
                throw new RuntimeException("Lỗi khi đọc ảnh: " + responses.get(0).getError().getMessage());
            }

            // 4. Lấy toàn bộ Text trả về
            String rawText = responses.get(0).getFullTextAnnotation().getText();
            String cleanText = rawText.toLowerCase().replace("|", " ").replaceAll("\\s+", " ");

            // 1. Nhận diện loại hóa đơn (Category)
            String activityName = detectActivityName(cleanText);

            // 2. Trích xuất giá trị dựa trên Activity Name
            Double extractedValue = extractValueByActivity(cleanText, activityName);

            return OcrResponse.builder()
                    .rawText(rawText)
                    .extractedValue(extractedValue)
                    .category(activityName) // Trả về "Điện lưới (Việt Nam)", "Nước sạch",...
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi hệ thống OCR: " + e.getMessage());
        }
    }

    private String detectActivityName(String text) {
        if (text.contains("tiền nước") || text.contains("cấp nước") || text.contains("biwase") || text.contains("m3") || text.contains("m³"))
            return "Nước sạch";
        if (text.contains("tiền điện") || text.contains("kwh") || text.contains("điện năng") || text.contains("evn"))
            return "Điện lưới (Việt Nam)";
        if (text.contains("gas") || text.contains("lpg") || text.contains("kg"))
            return "Khí Gas nấu ăn (LPG)";
        return "Khác";
    }

    // Thuật toán trích xuất số liệu cơ bản (Có thể nâng cấp sau)
    private Double extractValueByActivity(String text, String activityName) {
        String regex = "";

        switch (activityName) {
            case "Nước sạch":
                // Bắt số đứng sát m3 HOẶC số đứng sau cụm "tiêu thụ", "sản lượng", "cộng"
                regex = "(\\d+[.,]?\\d*)\\s*(?:m3|m³|khối)|(?:tiêu thụ|sản lượng|khối lượng|cộng|kl\\(m3\\))[:\\s]*(\\d+[.,]?\\d*)";
                break;
            case "Điện lưới (Việt Nam)":
                // Bắt số sát kwh HOẶC số đứng sau "tiêu thụ", "cộng", "điện năng"
                regex = "(\\d+[.,]?\\d*)\\s*(?:kwh|kinh|kw|k/h)|(?:tiêu thụ|sản lượng|điện năng|đn tiêu thụ|cộng)[:\\s]*(\\d+[.,]?\\d*)";
                break;

            case "Khí Gas nấu ăn (LPG)":
                regex = "(\\d+[.,]?\\d*)\\s*(?:kg|k9)|(?:trọng lượng|khối lượng|gas)[:\\s]*(\\d+[.,]?\\d*)";
                break;

            default:
                return null;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        List<Double> candidates = new ArrayList<>();
        while (matcher.find()) {
            String valStr = (matcher.group(1) != null) ? matcher.group(1) : matcher.group(2);
            try {
                // Chuẩn hóa số (VN dùng dấu phẩy cho thập phân)
                valStr = valStr.replace(",", ".");
                if (valStr.indexOf(".") != valStr.lastIndexOf(".")) {
                    valStr = valStr.replaceFirst("\\.", "");
                }
                double val = Double.parseDouble(valStr);

                // Lọc bỏ các số chắc chắn không phải sản lượng (như năm 2022, 2025, hoặc số quá nhỏ/quá lớn)
                if (val > 0 && val != 2022 && val != 2024 && val != 2025 && val < 10000) {
                    candidates.add(val);
                }
            } catch (Exception e) { continue; }
        }

        // CHIẾN THUẬT LẤY SỐ:
        if (candidates.isEmpty()) return null;

        // Với hóa đơn nước/điện dạng bảng, số "Cộng" thường xuất hiện sau cùng
        // hoặc lặp lại nhiều lần. Ta lấy số xuất hiện cuối cùng trong danh sách quét được.
        return candidates.get(candidates.size() - 1);
    }
}
