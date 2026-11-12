package minhhang.invitationbe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import minhhang.invitationbe.dto.request.CreateInvitedGuestRequest;
import minhhang.invitationbe.dto.request.RsvpRequest;
import minhhang.invitationbe.dto.request.UpdateInvitedGuestRequest;
import minhhang.invitationbe.dto.response.ExcelImportResult;
import minhhang.invitationbe.dto.response.InvitedGuestResponse;
import minhhang.invitationbe.dto.response.PageResponse;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import minhhang.invitationbe.service.ExcelImportService;
import minhhang.invitationbe.service.ExcelTemplateService;
import minhhang.invitationbe.service.InvitedGuestService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
@Tag(name = "Invited Guests", description = "APIs quản lý khách mời có mã thiệp (UUID) & tiếp nhận phản hồi RSVP")
public class InvitedGuestController {

    private final InvitedGuestService invitedGuestService;
    private final ExcelTemplateService excelTemplateService;
    private final ExcelImportService excelImportService;

    @PostMapping
    @Operation(summary = "Tạo mới khách mời", description = "Admin tạo khách mời, mã thiệp inviteCode dạng UUID sẽ được hệ thống tự động sinh ngẫu nhiên duy nhất")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Khách mời được tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<InvitedGuestResponse>> create(
            @Valid @RequestBody CreateInvitedGuestRequest request
    ) {
        InvitedGuestResponse response = invitedGuestService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(minhhang.invitationbe.dto.response.ApiResponse.success("Guest created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách khách mời", description = "Hỗ trợ phân trang và lọc theo mã thiệp (inviteCode), nhà trai/nhà gái, trạng thái RSVP, trạng thái hoạt động")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<PageResponse<InvitedGuestResponse>>> getAll(
            @Parameter(description = "Lọc theo mã thiệp mời (inviteCode)") @RequestParam(required = false) String inviteCode,
            @Parameter(description = "Lọc theo phía nhà: 'groom' hoặc 'bride'") @RequestParam(required = false) GuestSide guestSide,
            @Parameter(description = "Lọc theo trạng thái RSVP: 'pending', 'attending' hoặc 'declined'") @RequestParam(required = false) RsvpStatus rsvpStatus,
            @Parameter(description = "Lọc theo trạng thái hoạt động (true/false)") @RequestParam(required = false) Boolean isActive,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<InvitedGuestResponse> response = invitedGuestService.getAll(inviteCode, guestSide, rsvpStatus, isActive, pageable);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khách mời theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy thông tin khách mời"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách mời với ID tương ứng")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<InvitedGuestResponse>> getById(
            @Parameter(description = "ID khách mời", example = "1") @PathVariable Long id
    ) {
        InvitedGuestResponse response = invitedGuestService.getById(id);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khách mời")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách mời")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<InvitedGuestResponse>> update(
            @Parameter(description = "ID khách mời cần cập nhật", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateInvitedGuestRequest request
    ) {
        InvitedGuestResponse response = invitedGuestService.update(id, request);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Guest updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khách mời theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách mời")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<Void>> delete(
            @Parameter(description = "ID khách mời cần xóa", example = "1") @PathVariable Long id
    ) {
        invitedGuestService.delete(id);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Guest deleted successfully", null));
    }

    @GetMapping(value = {"/invite/{inviteCode}", "/by-code/{inviteCode}", "/code/{inviteCode}"})
    @Operation(summary = "Lấy thông tin khách mời theo mã inviteCode (UUID)", description = "Hỗ trợ truy cập qua các path: /api/v1/guests/invite/{inviteCode}, /api/v1/guests/code/{inviteCode}, /api/v1/guests/by-code/{inviteCode}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thiệp thành công"),
            @ApiResponse(responseCode = "404", description = "Mã thiệp không tồn tại")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<InvitedGuestResponse>> getByInviteCode(
            @Parameter(description = "Mã UUID thiệp mời trên URL", example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c") @PathVariable String inviteCode
    ) {
        InvitedGuestResponse response = invitedGuestService.getByInviteCode(inviteCode);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success(response));
    }

    @PostMapping("/invite/{inviteCode}/rsvp")
    @Operation(summary = "Public: Khách xác nhận tham dự hoặc từ chối (RSVP)", description = "Cập nhật trạng thái tham dự và thời gian phản hồi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gửi phản hồi RSVP thành công"),
            @ApiResponse(responseCode = "400", description = "Thiệp đã bị vô hiệu hóa hoặc dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Mã thiệp không tồn tại")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<InvitedGuestResponse>> submitRsvp(
            @Parameter(description = "Mã UUID thiệp mời trên URL", example = "c9a646d3-9c61-4cc9-bc55-e7eb8863f35c") @PathVariable String inviteCode,
            @Valid @RequestBody RsvpRequest request
    ) {
        InvitedGuestResponse response = invitedGuestService.submitRsvp(inviteCode, request);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("RSVP submitted successfully", response));
    }

    @GetMapping("/excel-template")
    @Operation(summary = "Tải file mẫu Excel để import danh sách khách mời",
            description = "File Excel .xlsx gồm các cột: Họ và tên (*), Phía nhà (*), Thông tin thêm với dropdown chọn 'Chú rể' hoặc 'Cô dâu'")
    @ApiResponse(responseCode = "200", description = "Tải file template thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<byte[]> downloadGuestTemplate() {
        byte[] excelBytes = excelTemplateService.generateGuestTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guest_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import danh sách khách mời từ file Excel",
            description = "Tải lên file Excel (.xlsx hoặc .xls) theo đúng mẫu template để import hàng loạt khách mời")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Import hoàn tất (trả về chi tiết số lượng thành công, thất bại và danh sách lỗi theo dòng)"),
            @ApiResponse(responseCode = "400", description = "File không đúng định dạng hoặc không thể đọc")
    })
    public ResponseEntity<minhhang.invitationbe.dto.response.ApiResponse<ExcelImportResult>> importGuests(
            @Parameter(description = "File Excel chứa dữ liệu khách mời (.xlsx, .xls)", required = true)
            @RequestParam("file") MultipartFile file
    ) {
        ExcelImportResult result = excelImportService.importGuests(file);
        return ResponseEntity.ok(minhhang.invitationbe.dto.response.ApiResponse.success("Import processed successfully", result));
    }

    @GetMapping("/export")
    @Operation(summary = "Xuất danh sách khách mời ra file Excel",
            description = "Xuất file Excel chứa thông tin khách mời kèm link mở thiệp theo domain/base-url (được gắn hyperlink bấm trực tiếp)")
    @ApiResponse(responseCode = "200", description = "Xuất file Excel thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<byte[]> exportGuests(
            @Parameter(description = "Domain hoặc URL trang thiệp (tùy chọn, ví dụ: https://damcuoi.vn)") @RequestParam(required = false) String domain,
            @Parameter(description = "Lọc theo phía nhà: 'groom' hoặc 'bride'") @RequestParam(required = false) GuestSide guestSide,
            @Parameter(description = "Lọc theo trạng thái RSVP: 'pending', 'attending' hoặc 'declined'") @RequestParam(required = false) RsvpStatus rsvpStatus,
            @Parameter(description = "Lọc theo trạng thái hoạt động (true/false)") @RequestParam(required = false) Boolean isActive
    ) {
        byte[] excelBytes = invitedGuestService.exportGuests(domain, guestSide, rsvpStatus, isActive);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_khach_moi.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/export-all")
    @Operation(summary = "Xuất toàn bộ khách mời và nhóm gia đình ra file Excel (2 sheet)",
            description = "Xuất file Excel gồm 2 sheet: 'Khách mời' và 'Nhóm gia đình' kèm đường link dẫn tới thiệp mời")
    @ApiResponse(responseCode = "200", description = "Xuất file Excel thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<byte[]> exportAll(
            @Parameter(description = "Domain hoặc URL trang thiệp (tùy chọn, ví dụ: https://damcuoi.vn)") @RequestParam(required = false) String domain
    ) {
        byte[] excelBytes = invitedGuestService.exportAll(domain);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_khach_moi_va_gia_dinh.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
