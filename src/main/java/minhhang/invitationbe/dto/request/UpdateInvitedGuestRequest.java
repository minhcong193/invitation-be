package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu cập nhật thông tin khách mời")
public class UpdateInvitedGuestRequest {

    @Schema(description = "Họ và tên mới của khách mời", example = "Nguyễn Văn Tuấn (Bạn chú rể)", maxLength = 150)
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @Schema(description = "Thuộc phía nhà gái ('bride') hoặc nhà trai ('groom')", example = "groom")
    private GuestSide guestSide;

    @Schema(description = "Thông tin thêm / Lời nhắn dành cho khách mời", example = "Bạn cấp 3 của chú rể", maxLength = 1000)
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;

    @Schema(description = "Trạng thái phản hồi tham dự ('pending', 'attending' hoặc 'declined')", example = "attending")
    private RsvpStatus rsvpStatus;

    @Schema(description = "Trạng thái kích hoạt thiệp", example = "true")
    private Boolean isActive;
}
