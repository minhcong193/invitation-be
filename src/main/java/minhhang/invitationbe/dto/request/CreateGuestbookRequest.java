package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu gửi lời chúc mừng vào sổ lưu bút")
public class CreateGuestbookRequest {

    @Schema(
            description = "Mã thiệp mời UUID của khách (tùy chọn, để liên kết lời chúc với khách mời cụ thể)",
            example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c",
            maxLength = 80
    )
    @Size(max = 80, message = "Invite code must not exceed 80 characters")
    private String inviteCode;

    @Schema(
            description = "Tên hiển thị của người gửi lời chúc",
            example = "Tuấn & Mai",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 150
    )
    @NotBlank(message = "Display name is required")
    @Size(max = 150, message = "Display name must not exceed 150 characters")
    private String displayName;

    @Schema(
            description = "Nội dung lời chúc mừng hạnh phúc",
            example = "Chúc hai bạn trăm năm hạnh phúc, sớm đón quý tử nha!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 2000
    )
    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;
}
