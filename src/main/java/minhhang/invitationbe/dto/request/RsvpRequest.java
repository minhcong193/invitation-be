package minhhang.invitationbe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import minhhang.invitationbe.enums.RsvpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu phản hồi tham dự (RSVP)")
public class RsvpRequest {

    @Schema(
            description = "Trạng thái tham dự: 'attending' (sẽ tham dự) hoặc 'declined' (không thể tham dự)",
            example = "attending",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "RSVP status is required ('attending' or 'declined')")
    private RsvpStatus rsvpStatus;
}
