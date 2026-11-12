package minhhang.invitationbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Kết quả tạo mã QR VietQR")
public class VietQrResponse {

    @Schema(description = "Đường link ảnh QR trực tiếp từ VietQR (có thể gán thẳng vào thẻ <img src=... />)",
            example = "https://img.vietqr.io/image/970422-0987654321-compact2.png?amount=500000&addInfo=Mung%20cuoi%20Minh%20va%20Hang&accountName=TRAN%20CONG%20MINH")
    private String qrQuickLink;

    @Schema(description = "Dữ liệu ảnh dạng Data URI Base64 (data:image/png;base64,...)", example = "data:image/png;base64,iVBORw0KGgo...")
    private String qrDataUrl;

    @Schema(description = "Chuỗi mã QR văn bản chuẩn EMVCo / Napas247",
            example = "00020101021238540010A00000072701240006970422011009876543210208QRIBFTTA530370454065000005802VN62260822Mung cuoi Minh va Hang6304E8A9")
    private String qrCode;

    @Schema(description = "Mã BIN ngân hàng thụ hưởng", example = "970422")
    private String bankBin;

    @Schema(description = "Tên ngân hàng thụ hưởng", example = "MBBank")
    private String bankName;

    @Schema(description = "Số tài khoản thụ hưởng", example = "0987654321")
    private String accountNumber;

    @Schema(description = "Tên chủ tài khoản thụ hưởng", example = "TRAN CONG MINH")
    private String accountName;

    @Schema(description = "Số tiền chuyển khoản (VNĐ)", example = "500000")
    private Long amount;

    @Schema(description = "Nội dung chuyển khoản", example = "Mung cuoi Minh va Hang")
    private String description;

    @Schema(description = "Mẫu giao diện VietQR", example = "compact2")
    private String template;
}

