package minhhang.invitationbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.CreateGuestbookRequest;
import minhhang.invitationbe.dto.request.UpdateModerationStatusRequest;
import minhhang.invitationbe.dto.response.GuestbookMessageResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.enums.ModerationStatus;
import minhhang.invitationbe.service.GuestbookMessageService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guestbook")
@RequiredArgsConstructor
@Tag(name = "Guestbook Messages", description = "APIs sổ lưu bút / gửi và duyệt lời chúc mừng đám cưới")
public class GuestbookController {

    private final GuestbookMessageService guestbookMessageService;

    @PostMapping
    @Operation(summary = "Public: Gửi lời chúc mừng đám cưới", description = "Có thể gửi kèm mã inviteCode hoặc chỉ cần tên hiển thị và lời chúc")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Gửi lời chúc thành công, chờ kiểm duyệt"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu lời chúc không hợp lệ")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<GuestbookMessageResponse>> create(
            @Valid @RequestBody CreateGuestbookRequest request
    ) {
        GuestbookMessageResponse response = guestbookMessageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(minhhang.invitationbe.dto.response.ApiResponse.success("Message submitted successfully, awaiting approval", response));
    }

    @GetMapping
    @Operation(summary = "Public: Lấy danh sách lời chúc đã được duyệt", description = "Hiển thị trên tường lưu bút của thiệp cưới, sắp xếp thời gian mới nhất lên đầu")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách lời chúc thành công")
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<PageResponse<GuestbookMessageResponse>>> getApprovedMessages(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<GuestbookMessageResponse> response = guestbookMessageService.getApprovedMessages(pageable);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @GetMapping("/admin")
    @Operation(summary = "Admin: Lấy tất cả lời chúc", description = "Hỗ trợ lọc theo trạng thái duyệt ('pending', 'approved', 'rejected')")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<PageResponse<GuestbookMessageResponse>>> getAllMessages(
            @Parameter(description = "Lọc theo trạng thái duyệt: 'pending', 'approved', 'rejected'") @RequestParam(required = false) ModerationStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<GuestbookMessageResponse> response = guestbookMessageService.getAllMessages(status, pageable);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết 1 lời chúc theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy lời chúc"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lời chúc với ID tương ứng")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<GuestbookMessageResponse>> getById(
            @Parameter(description = "ID lời chúc", example = "1") @PathVariable Long id
    ) {
        GuestbookMessageResponse response = guestbookMessageService.getById(id);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @PatchMapping("/{id}/moderation")
    @Operation(summary = "Admin: Duyệt hoặc từ chối lời chúc", description = "Cập nhật moderationStatus sang 'approved', 'rejected' hoặc 'pending'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái duyệt thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lời chúc")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<GuestbookMessageResponse>> updateModerationStatus(
            @Parameter(description = "ID lời chúc cần duyệt", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateModerationStatusRequest request
    ) {
        GuestbookMessageResponse response = guestbookMessageService.updateModerationStatus(id, request);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Message moderation status updated", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Xóa lời chúc theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa lời chúc thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lời chúc")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<Void>> delete(
            @Parameter(description = "ID lời chúc cần xóa", example = "1") @PathVariable Long id
    ) {
        guestbookMessageService.delete(id);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Message deleted successfully", null));
    }
}
