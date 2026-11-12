package minhhang.invitationbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.GenerateVietQrRequest;
import minhhang.invitationbe.dto.response.BankResponse;
import minhhang.invitationbe.dto.response.VietQrResponse;
import minhhang.invitationbe.service.VietQrService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qr/vietqr")
@RequiredArgsConstructor
@Tag(name = "VietQR Generator", description = "APIs tạo mã QR chuẩn VietQR (chuyển khoản ngân hàng mừng cưới / thanh toán)")
public class VietQrController {

    private final VietQrService vietQrService;

    @PostMapping("/generate")
    @Operation(summary = "Tạo mã QR VietQR chuyển khoản (JSON)",
            description = "Trả về đường dẫn ảnh Quicklink, chuỗi payload văn bản EMVCo Napas247 và Data URL Base64 (nếu có)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo mã QR thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu yêu cầu không hợp lệ")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<VietQrResponse>> generateQr(
            @Valid @RequestBody GenerateVietQrRequest request
    ) {
        VietQrResponse response = vietQrService.generateQr(request);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Tạo mã QR thành công", response));
    }

    @GetMapping("/quicklink")
    @Operation(summary = "Tạo mã QR VietQR nhanh qua query parameters",
            description = "Tiện lợi khi gọi trực tiếp qua GET request với các tham số trên URL")
    @ApiResponse(responseCode = "200", description = "Tạo mã QR thành công")
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<VietQrResponse>> getQuickLink(
            @Parameter(description = "Mã BIN ngân hàng (ví dụ: 970422) hoặc mã viết tắt (MB, VCB, TCB)", required = true)
            @RequestParam String bankBin,
            @Parameter(description = "Số tài khoản ngân hàng người nhận", required = true)
            @RequestParam String accountNumber,
            @Parameter(description = "Tên chủ tài khoản")
            @RequestParam(required = false) String accountName,
            @Parameter(description = "Số tiền mừng / chuyển khoản (VNĐ)")
            @RequestParam(required = false) Long amount,
            @Parameter(description = "Nội dung chuyển khoản")
            @RequestParam(required = false) String description,
            @Parameter(description = "Mẫu giao diện: compact2 (mặc định), compact, qr_only, print")
            @RequestParam(required = false, defaultValue = "compact2") String template
    ) {
        GenerateVietQrRequest request = GenerateVietQrRequest.builder()
                .bankBin(bankBin)
                .accountNumber(accountNumber)
                .accountName(accountName)
                .amount(amount)
                .description(description)
                .template(template)
                .build();
        VietQrResponse response = vietQrService.generateQr(request);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Tạo mã QR thành công", response));
    }

    @GetMapping("/image")
    @Operation(summary = "Tải trực tiếp ảnh QR code dạng binary (image/png)",
            description = "Dùng để nhúng trực tiếp vào thẻ HTML: <img src='/api/v1/qr/vietqr/image?bankBin=...&accountNumber=...' />")
    @ApiResponse(responseCode = "200", description = "Trả về luồng ảnh PNG của mã QR",
            content = @Content(mediaType = "image/png"))
    public ResponseEntity<byte[]> getQrImage(
            @Parameter(description = "Mã BIN hoặc mã viết tắt ngân hàng", required = true)
            @RequestParam String bankBin,
            @Parameter(description = "Số tài khoản người nhận", required = true)
            @RequestParam String accountNumber,
            @Parameter(description = "Tên chủ tài khoản")
            @RequestParam(required = false) String accountName,
            @Parameter(description = "Số tiền chuyển khoản")
            @RequestParam(required = false) Long amount,
            @Parameter(description = "Nội dung chuyển khoản")
            @RequestParam(required = false) String description,
            @Parameter(description = "Mẫu giao diện: compact2, compact, qr_only, print")
            @RequestParam(required = false, defaultValue = "compact2") String template
    ) {
        GenerateVietQrRequest request = GenerateVietQrRequest.builder()
                .bankBin(bankBin)
                .accountNumber(accountNumber)
                .accountName(accountName)
                .amount(amount)
                .description(description)
                .template(template)
                .build();
        byte[] imageBytes = vietQrService.getQrImage(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=vietqr.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

    @GetMapping("/banks")
    @Operation(summary = "Lấy danh sách các ngân hàng Việt Nam hỗ trợ VietQR",
            description = "Trả về danh sách các ngân hàng gồm mã BIN, tên viết tắt, logo và tên đầy đủ")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách ngân hàng thành công")
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<List<BankResponse>>> getSupportedBanks() {
        List<BankResponse> banks = vietQrService.getSupportedBanks();
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Lấy danh sách ngân hàng thành công", banks));
    }
}

