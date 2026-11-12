package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu tạo mã QR VietQR chuyển khoản ngân hàng")
public class GenerateVietQrRequest {

    @NotBlank(message = "Mã BIN hoặc mã viết tắt ngân hàng không được để trống (ví dụ: 970422, MB, VCB, TCB)")
    @Schema(description = "Mã BIN ngân hàng (ví dụ: 970422) hoặc mã viết tắt ngân hàng (ví dụ: MB, VCB, TCB, VPB, ACB)", example = "970422")
    private String bankBin;

    @NotBlank(message = "Số tài khoản người thụ hưởng không được để trống")
    @Schema(description = "Số tài khoản ngân hàng người nhận", example = "0987654321")
    private String accountNumber;

    @Schema(description = "Tên chủ tài khoản (in hoa không dấu hoặc có dấu)", example = "TRAN CONG MINH")
    private String accountName;

    @Schema(description = "Số tiền mừng / chuyển khoản (VNĐ, để trống nếu tùy tâm)", example = "500000")
    private Long amount;

    @Schema(description = "Nội dung chuyển khoản / lời chúc mừng cưới", example = "Mung cuoi Minh va Hang")
    private String description;

    @Builder.Default
    @Schema(description = "Kiểu mẫu hiển thị QR: compact2 (chuẩn đẹp mặc định), compact (gọn), qr_only (chỉ mã QR), print (in ấn)", example = "compact2")
    private String template = "compact2";
}

