package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import minhhang.invitationbe.enums.ModerationStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu kiểm duyệt lời chúc trong sổ lưu bút")
public class UpdateModerationStatusRequest {

    @Schema(
            description = "Trạng thái kiểm duyệt: 'approved' (duyệt hiển thị), 'rejected' (từ chối), 'pending' (chờ xét duyệt)",
            example = "approved",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Moderation status is required ('pending', 'approved', 'rejected')")
    private ModerationStatus moderationStatus;
}
