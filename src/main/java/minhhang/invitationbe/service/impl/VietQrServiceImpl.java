package minhhang.invitationbe.service.impl;

import lombok.extern.slf4j.Slf4j;
import minhhang.invitationbe.dto.request.GenerateVietQrRequest;
import minhhang.invitationbe.dto.response.BankResponse;
import minhhang.invitationbe.dto.response.VietQrResponse;
import minhhang.invitationbe.service.VietQrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class VietQrServiceImpl implements VietQrService {

    private final RestClient restClient;

    @Value("${app.vietqr.api-url:https://api.vietqr.io/v2}")
    private String apiUrl;

    @Value("${app.vietqr.image-url:https://img.vietqr.io/image}")
    private String imageUrl;

    @Value("${app.vietqr.client-id:}")
    private String clientId;

    @Value("${app.vietqr.api-key:}")
    private String apiKey;

    private static final List<BankResponse> DEFAULT_BANKS = new ArrayList<>();
    private static final Map<String, BankResponse> BANK_BY_BIN = new HashMap<>();
    private static final Map<String, BankResponse> BANK_BY_CODE = new HashMap<>();

    static {
        registerBank(17, "Ngân hàng TMCP Quân Đội", "MB", "970422", "MBBank", "https://api.vietqr.io/img/MB.png");
        registerBank(2, "Ngân hàng TMCP Ngoại Thương Việt Nam", "VCB", "970436", "Vietcombank", "https://api.vietqr.io/img/VCB.png");
        registerBank(19, "Ngân hàng TMCP Kỹ Thương Việt Nam", "TCB", "970407", "Techcombank", "https://api.vietqr.io/img/TCB.png");
        registerBank(26, "Ngân hàng TMCP Việt Nam Thịnh Vượng", "VPB", "970432", "VPBank", "https://api.vietqr.io/img/VPB.png");
        registerBank(1, "Ngân hàng TMCP Á Châu", "ACB", "970416", "ACB", "https://api.vietqr.io/img/ACB.png");
        registerBank(4, "Ngân hàng TMCP Đầu tư và Phát triển Việt Nam", "BIDV", "970418", "BIDV", "https://api.vietqr.io/img/BIDV.png");
        registerBank(43, "Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam", "VBA", "970405", "Agribank", "https://api.vietqr.io/img/VBA.png");
        registerBank(3, "Ngân hàng TMCP Công Thương Việt Nam", "CTG", "970415", "VietinBank", "https://api.vietqr.io/img/CTG.png");
        registerBank(24, "Ngân hàng TMCP Tiên Phong", "TPB", "970423", "TPBank", "https://api.vietqr.io/img/TPB.png");
        registerBank(22, "Ngân hàng TMCP Sài Gòn Thương Tín", "STB", "970403", "Sacombank", "https://api.vietqr.io/img/STB.png");
        registerBank(25, "Ngân hàng TMCP Quốc tế Việt Nam", "VIB", "970441", "VIB", "https://api.vietqr.io/img/VIB.png");
        registerBank(18, "Ngân hàng TMCP Phương Đông", "OCB", "970448", "OCB", "https://api.vietqr.io/img/OCB.png");
        registerBank(21, "Ngân hàng TMCP Sài Gòn - Hà Nội", "SHB", "970443", "SHB", "https://api.vietqr.io/img/SHB.png");
        registerBank(9, "Ngân hàng TMCP Phát triển Thành phố Hồ Chí Minh", "HDB", "970437", "HDBank", "https://api.vietqr.io/img/HDB.png");
        registerBank(16, "Ngân hàng TMCP Hàng Hải Việt Nam", "MSB", "970426", "MSB", "https://api.vietqr.io/img/MSB.png");
        registerBank(15, "Ngân hàng TMCP Lộc Phát Việt Nam", "LPB", "970449", "LPBank", "https://api.vietqr.io/img/LPB.png");
        registerBank(20, "Ngân hàng TMCP Đông Nam Á", "SEAB", "970440", "SeABank", "https://api.vietqr.io/img/SEAB.png");
        registerBank(23, "Ngân hàng TMCP Sài Gòn", "SCB", "970429", "SCB", "https://api.vietqr.io/img/SCB.png");
        registerBank(5, "Ngân hàng TMCP Bảo Việt", "BVB", "970438", "BaoVietBank", "https://api.vietqr.io/img/BVB.png");
        registerBank(33, "Ngân hàng TMCP Bản Việt", "VCCB", "970454", "BVBank", "https://api.vietqr.io/img/VCCB.png");
        registerBank(27, "Ngân hàng TMCP An Bình", "ABB", "970425", "ABBANK", "https://api.vietqr.io/img/ABB.png");
        registerBank(34, "Ngân hàng TMCP Việt Á", "VAB", "970427", "VietABank", "https://api.vietqr.io/img/VAB.png");
        registerBank(14, "Ngân hàng TMCP Quốc Dân", "NCB", "970419", "NCB", "https://api.vietqr.io/img/NCB.png");
        registerBank(10, "Ngân hàng TMCP Kiên Long", "KLB", "970452", "Kienlongbank", "https://api.vietqr.io/img/KLB.png");
        registerBank(30, "Ngân hàng TMCP Thịnh vượng và Phát triển", "PGB", "970430", "PGBank", "https://api.vietqr.io/img/PGB.png");
        registerBank(31, "Ngân hàng TMCP Đại Chúng Việt Nam", "PVB", "970412", "PVcomBank", "https://api.vietqr.io/img/PVB.png");
        registerBank(53, "Ngân hàng số CAKE by VPBank", "CAKE", "546034", "CAKE", "https://api.vietqr.io/img/CAKE.png");
        registerBank(54, "Ngân hàng số Ubank by VPBank", "UBANK", "546035", "Ubank", "https://api.vietqr.io/img/UBANK.png");
        registerBank(55, "Ngân hàng số Timo by BVBank", "TIMO", "963388", "Timo", "https://api.vietqr.io/img/TIMO.png");
    }

    private static void registerBank(Integer id, String name, String code, String bin, String shortName, String logo) {
        BankResponse bank = BankResponse.builder()
                .id(id)
                .name(name)
                .code(code)
                .bin(bin)
                .shortName(shortName)
                .logo(logo)
                .build();
        DEFAULT_BANKS.add(bank);
        BANK_BY_BIN.put(bin, bank);
        BANK_BY_CODE.put(code.toUpperCase(), bank);
        BANK_BY_CODE.put(shortName.toUpperCase(), bank);
    }

    public VietQrServiceImpl() {
        RestClient client = null;
        try {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Duration.ofSeconds(3));
            factory.setReadTimeout(Duration.ofSeconds(5));
            client = RestClient.builder().requestFactory(factory).build();
        } catch (Exception e) {
            log.warn("Cannot initialize RestClient with SimpleClientHttpRequestFactory: {}", e.getMessage());
        }
        this.restClient = client;
    }

    public VietQrServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public VietQrResponse generateQr(GenerateVietQrRequest request) {
        String resolvedBin = resolveBankBin(request.getBankBin());
        String template = resolveTemplate(request.getTemplate(), request.getAmount());

        BankResponse bankInfo = BANK_BY_BIN.get(resolvedBin);
        String bankName = bankInfo != null ? bankInfo.getShortName() : resolvedBin;

        String quickLink = buildQuickLink(
                resolvedBin,
                request.getAccountNumber(),
                template,
                request.getAmount(),
                request.getDescription(),
                request.getAccountName()
        );

        String offlineEmvCo = generateEmvCoPayload(
                resolvedBin,
                request.getAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );

        String qrDataUrl = null;
        String qrCode = offlineEmvCo;

        // Thử gọi VietQR v2 API để lấy Base64 data URL
        if (restClient != null) {
            try {
                Map<String, Object> apiPayload = new HashMap<>();
                apiPayload.put("accountNo", request.getAccountNumber());
                if (request.getAccountName() != null && !request.getAccountName().isBlank()) {
                    apiPayload.put("accountName", request.getAccountName());
                }
                apiPayload.put("acqId", resolvedBin);
                if (request.getAmount() != null && request.getAmount() > 0) {
                    apiPayload.put("amount", request.getAmount());
                }
                if (request.getDescription() != null && !request.getDescription().isBlank()) {
                    apiPayload.put("addInfo", request.getDescription());
                }
                apiPayload.put("template", template);

                RestClient.RequestBodySpec requestSpec = restClient.post()
                        .uri(apiUrl + "/generate")
                        .contentType(MediaType.APPLICATION_JSON);

                if (clientId != null && !clientId.isBlank()) {
                    requestSpec.header("x-client-id", clientId);
                }
                if (apiKey != null && !apiKey.isBlank()) {
                    requestSpec.header("x-api-key", apiKey);
                }

                Map<String, Object> responseBody = requestSpec
                        .body(apiPayload)
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});

                if (responseBody != null && "00".equals(responseBody.get("code"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    if (data != null) {
                        if (data.get("qrDataURL") != null) {
                            qrDataUrl = data.get("qrDataURL").toString();
                        }
                        if (data.get("qrCode") != null) {
                            qrCode = data.get("qrCode").toString();
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("VietQR API call not available or timed out, fallback to quicklink and offline EMVCo payload: {}", e.getMessage());
            }
        }

        return VietQrResponse.builder()
                .qrQuickLink(quickLink)
                .qrDataUrl(qrDataUrl)
                .qrCode(qrCode)
                .bankBin(resolvedBin)
                .bankName(bankName)
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .amount(request.getAmount())
                .description(request.getDescription())
                .template(template)
                .build();
    }

    @Override
    public byte[] getQrImage(GenerateVietQrRequest request) {
        String resolvedBin = resolveBankBin(request.getBankBin());
        String template = resolveTemplate(request.getTemplate(), request.getAmount());

        String quickLink = buildQuickLink(
                resolvedBin,
                request.getAccountNumber(),
                template,
                request.getAmount(),
                request.getDescription(),
                request.getAccountName()
        );

        if (restClient == null) {
            throw new RuntimeException("HTTP client không khả dụng để tải ảnh mã QR");
        }
        try {
            return restClient.get()
                    .uri(quickLink)
                    .retrieve()
                    .body(byte[].class);
        } catch (Exception e) {
            log.error("Failed to fetch QR image from VietQR Quicklink: {}", e.getMessage());
            throw new RuntimeException("Không thể tải ảnh mã QR: " + e.getMessage(), e);
        }
    }

    public String resolveTemplate(String requestedTemplate, Long amount) {
        boolean hasAmount = amount != null && amount > 0;
        if (requestedTemplate != null && !requestedTemplate.isBlank()) {
            String trimmed = requestedTemplate.trim().toLowerCase();
            // Mẫu compact2 của VietQR in sẵn ô "Số tiền: 0 đ" nếu không truyền amount.
            // Do đó tự động đổi sang "compact" để ẩn hoàn toàn số tiền 0đ trên ảnh.
            if (!hasAmount && "compact2".equals(trimmed)) {
                return "compact";
            }
            return trimmed;
        }
        return hasAmount ? "compact2" : "compact";
    }

    @Override
    public List<BankResponse> getSupportedBanks() {
        if (restClient != null) {
            try {
                Map<String, Object> responseBody = restClient.get()
                        .uri(apiUrl + "/banks")
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, Object>>() {});

                if (responseBody != null && "00".equals(responseBody.get("code"))) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
                    if (data != null && !data.isEmpty()) {
                        List<BankResponse> banks = new ArrayList<>();
                        for (Map<String, Object> item : data) {
                            BankResponse b = BankResponse.builder()
                                    .id(item.get("id") instanceof Number ? ((Number) item.get("id")).intValue() : null)
                                    .name(Objects.toString(item.get("name"), ""))
                                    .code(Objects.toString(item.get("code"), ""))
                                    .bin(Objects.toString(item.get("bin"), ""))
                                    .shortName(Objects.toString(item.get("shortName"), ""))
                                    .logo(Objects.toString(item.get("logo"), ""))
                                    .build();
                            banks.add(b);
                        }
                        return banks;
                    }
                }
            } catch (Exception e) {
                log.debug("VietQR banks API unreachable or offline, using default local banks list: {}", e.getMessage());
            }
        }

        return Collections.unmodifiableList(DEFAULT_BANKS);
    }

    public String resolveBankBin(String bankIdentifier) {
        if (bankIdentifier == null || bankIdentifier.isBlank()) {
            throw new IllegalArgumentException("Mã ngân hàng / BIN không được để trống");
        }
        String trimmed = bankIdentifier.trim();
        // Nếu là mã BIN số (6 chữ số)
        if (BANK_BY_BIN.containsKey(trimmed)) {
            return trimmed;
        }

        // Tìm theo mã viết tắt
        BankResponse byCode = BANK_BY_CODE.get(trimmed.toUpperCase());
        if (byCode != null) {
            return byCode.getBin();
        }

        // Nếu người dùng nhập thẳng 6 chữ số BIN bất kỳ
        if (trimmed.matches("\\d{6}")) {
            return trimmed;
        }

        return trimmed;
    }

    public String buildQuickLink(String bin, String accountNo, String template, Long amount, String description, String accountName) {
        StringBuilder url = new StringBuilder();
        url.append(imageUrl).append("/").append(bin).append("-").append(accountNo).append("-").append(template).append(".png");

        List<String> queryParams = new ArrayList<>();
        if (amount != null && amount > 0) {
            queryParams.add("amount=" + amount);
        }
        if (description != null && !description.isBlank()) {
            queryParams.add("addInfo=" + URLEncoder.encode(description.trim(), StandardCharsets.UTF_8));
        }
        if (accountName != null && !accountName.isBlank()) {
            queryParams.add("accountName=" + URLEncoder.encode(accountName.trim(), StandardCharsets.UTF_8));
        }

        if (!queryParams.isEmpty()) {
            url.append("?").append(String.join("&", queryParams));
        }
        return url.toString();
    }

    public static String generateEmvCoPayload(String bankBin, String accountNumber, Long amount, String description) {
        StringBuilder sb = new StringBuilder();

        // Tag 00: Payload Format Indicator (01)
        sb.append(tlv("00", "01"));

        // Tag 01: Point of Initiation Method (11: Static, 12: Dynamic)
        String poi = (amount != null && amount > 0) ? "12" : "11";
        sb.append(tlv("01", poi));

        // Tag 38: Merchant Account Information (NAPAS 247)
        String guid = tlv("00", "A000000727");
        String beneficiary = tlv("00", bankBin) + tlv("01", accountNumber);
        String consumerInfo = tlv("01", beneficiary);
        String serviceCode = tlv("02", "QRIBFTTA");
        String tag38Value = guid + consumerInfo + serviceCode;
        sb.append(tlv("38", tag38Value));

        // Tag 53: Transaction Currency (704 = VND)
        sb.append(tlv("53", "704"));

        // Tag 54: Transaction Amount
        if (amount != null && amount > 0) {
            sb.append(tlv("54", String.valueOf(amount)));
        }

        // Tag 58: Country Code
        sb.append(tlv("58", "VN"));

        // Tag 62: Additional Data Field (Message / Description)
        if (description != null && !description.isBlank()) {
            String addInfo = tlv("08", description.trim());
            sb.append(tlv("62", addInfo));
        }

        // Tag 63: CRC
        String payloadBeforeCrc = sb.toString() + "6304";
        String crc = calculateCRC16(payloadBeforeCrc);
        return payloadBeforeCrc + crc;
    }

    private static String tlv(String tag, String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return String.format("%s%02d%s", tag, value.length(), value);
    }

    public static String calculateCRC16(String str) {
        int crc = 0xFFFF;
        int polynomial = 0x1021;
        byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i)) & 1) == 1;
                boolean c15 = ((crc >> 15) & 1) == 1;
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        crc &= 0xFFFF;
        return String.format("%04X", crc);
    }
}

