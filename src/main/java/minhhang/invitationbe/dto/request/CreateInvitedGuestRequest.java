package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import minhhang.invitationbe.enums.GuestSide;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu tạo mới khách mời (mã thiệp inviteCode tự sinh UUID)")
public class CreateInvitedGuestRequest {

    @Schema(
            description = "Họ và tên khách mời",
            example = "Nguyễn Văn Tuấn",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 150
    )
    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @Schema(
            description = "Thuộc phía nhà gái ('bride') hoặc nhà trai ('groom')",
            example = "groom",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Guest side is required ('groom' or 'bride')")
    private GuestSide guestSide;

    @Schema(
            description = "Thông tin thêm / Lời nhắn dành cho khách mời",
            example = "Bạn cấp 3 của chú rể",
            maxLength = 1000
    )
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
}
