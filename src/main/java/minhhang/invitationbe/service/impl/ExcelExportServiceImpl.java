package minhhang.invitationbe.service.impl;

import lombok.extern.slf4j.Slf4j;
import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.RsvpStatus;
import minhhang.invitationbe.service.ExcelExportService;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    @Value("${app.invitation.base-url:http://localhost:3000}")
    private String defaultBaseUrl;

    @Override
    public byte[] exportGuests(List<InvitedGuest> guests, String domain) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String resolvedDomain = resolveDomain(domain);
            buildGuestSheet(workbook, guests, resolvedDomain);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting guests excel file", e);
            throw new RuntimeException("Không thể xuất file Excel khách mời: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] exportFamilyGroups(List<FamilyGuestGroup> groups, String domain) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String resolvedDomain = resolveDomain(domain);
            buildFamilyGroupSheet(workbook, groups, resolvedDomain);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting family groups excel file", e);
            throw new RuntimeException("Không thể xuất file Excel nhóm gia đình: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] exportAll(List<InvitedGuest> guests, List<FamilyGuestGroup> groups, String domain) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String resolvedDomain = resolveDomain(domain);
            buildGuestSheet(workbook, guests, resolvedDomain);
            buildFamilyGroupSheet(workbook, groups, resolvedDomain);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error exporting combined excel file", e);
            throw new RuntimeException("Không thể xuất file Excel tổng hợp: " + e.getMessage(), e);
        }
    }

    private void buildGuestSheet(XSSFWorkbook workbook, List<InvitedGuest> guests, String domain) {
        XSSFSheet sheet = workbook.createSheet("Khach_Moi");
        sheet.setDisplayGridlines(true);

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook, false);
        CellStyle centerStyle = createDataStyle(workbook, true);
        CellStyle linkStyle = createLinkStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();

        String[] headers = {
                "STT",
                "Họ và tên",
                "Phía nhà",
                "Mã thiệp",
                "Link thiệp mời",
                "Trạng thái RSVP",
                "Thời gian phản hồi",
                "Thông tin thêm",
                "Kích hoạt"
        };

        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(28);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowIdx = 1;
        for (InvitedGuest guest : guests) {
            Row row = sheet.createRow(rowIdx++);
            row.setHeightInPoints(22);

            // 0: STT
            Cell c0 = row.createCell(0);
            c0.setCellValue(rowIdx - 1);
            c0.setCellStyle(centerStyle);

            // 1: Họ và tên
            Cell c1 = row.createCell(1);
            c1.setCellValue(guest.getFullName() != null ? guest.getFullName() : "");
            c1.setCellStyle(dataStyle);

            // 2: Phía nhà
            Cell c2 = row.createCell(2);
            c2.setCellValue(guest.getGuestSide() != null ? guest.getGuestSide().getLabel() : "");
            c2.setCellStyle(centerStyle);

            // 3: Mã thiệp
            Cell c3 = row.createCell(3);
            c3.setCellValue(guest.getInviteCode() != null ? guest.getInviteCode() : "");
            c3.setCellStyle(centerStyle);

            // 4: Link thiệp mời
            Cell c4 = row.createCell(4);
            String link = domain + "/invite/" + (guest.getInviteCode() != null ? guest.getInviteCode() : "");
            Hyperlink hl = createHelper.createHyperlink(HyperlinkType.URL);
            hl.setAddress(link);
            c4.setHyperlink(hl);
            c4.setCellValue(link);
            c4.setCellStyle(linkStyle);

            // 5: Trạng thái RSVP
            Cell c5 = row.createCell(5);
            String rsvpText = "Chờ phản hồi";
            if (guest.getRsvpStatus() == RsvpStatus.ATTENDING) {
                rsvpText = "Tham dự";
            } else if (guest.getRsvpStatus() == RsvpStatus.DECLINED) {
                rsvpText = "Không tham dự";
            }
            c5.setCellValue(rsvpText);
            c5.setCellStyle(centerStyle);

            // 6: Thời gian phản hồi
            Cell c6 = row.createCell(6);
            c6.setCellValue(guest.getRespondedAt() != null ? DATE_TIME_FORMATTER.format(guest.getRespondedAt()) : "");
            c6.setCellStyle(centerStyle);

            // 7: Thông tin thêm
            Cell c7 = row.createCell(7);
            c7.setCellValue(guest.getMessage() != null ? guest.getMessage() : "");
            c7.setCellStyle(dataStyle);

            // 8: Kích hoạt
            Cell c8 = row.createCell(8);
            c8.setCellValue(Boolean.TRUE.equals(guest.getIsActive()) ? "Đang hoạt động" : "Đã khóa");
            c8.setCellStyle(centerStyle);
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void buildFamilyGroupSheet(XSSFWorkbook workbook, List<FamilyGuestGroup> groups, String domain) {
        XSSFSheet sheet = workbook.createSheet("Nhom_Gia_Dinh");
        sheet.setDisplayGridlines(true);

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook, false);
        CellStyle centerStyle = createDataStyle(workbook, true);
        CellStyle linkStyle = createLinkStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();

        String[] headers = {
                "STT",
                "Tên nhóm / Gia đình",
                "Phía nhà",
                "Mối quan hệ",
                "Mã thiệp",
                "Link thiệp mời",
                "Số lượng khách dự kiến",
                "Số bàn tiệc",
                "Ghi chú",
                "Kích hoạt"
        };

        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(28);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowIdx = 1;
        for (FamilyGuestGroup group : groups) {
            Row row = sheet.createRow(rowIdx++);
            row.setHeightInPoints(22);

            // 0: STT
            Cell c0 = row.createCell(0);
            c0.setCellValue(rowIdx - 1);
            c0.setCellStyle(centerStyle);

            // 1: Tên nhóm / Gia đình
            Cell c1 = row.createCell(1);
            c1.setCellValue(group.getGroupName() != null ? group.getGroupName() : "");
            c1.setCellStyle(dataStyle);

            // 2: Phía nhà
            Cell c2 = row.createCell(2);
            c2.setCellValue(group.getGuestSide() != null ? group.getGuestSide().getLabel() : "");
            c2.setCellStyle(centerStyle);

            // 3: Mối quan hệ
            Cell c3 = row.createCell(3);
            c3.setCellValue(group.getRelationship() != null ? group.getRelationship() : "");
            c3.setCellStyle(dataStyle);

            // 4: Mã thiệp
            Cell c4 = row.createCell(4);
            c4.setCellValue(group.getInviteCode() != null ? group.getInviteCode() : "");
            c4.setCellStyle(centerStyle);

            // 5: Link thiệp mời
            Cell c5 = row.createCell(5);
            String link = domain + "/invite/" + (group.getInviteCode() != null ? group.getInviteCode() : "");
            Hyperlink hl = createHelper.createHyperlink(HyperlinkType.URL);
            hl.setAddress(link);
            c5.setHyperlink(hl);
            c5.setCellValue(link);
            c5.setCellStyle(linkStyle);

            // 6: Số lượng khách dự kiến
            Cell c6 = row.createCell(6);
            c6.setCellValue(group.getExpectedGuestCount() != null ? group.getExpectedGuestCount() : 1);
            c6.setCellStyle(centerStyle);

            // 7: Số bàn tiệc
            Cell c7 = row.createCell(7);
            c7.setCellValue(group.getTableNumber() != null ? group.getTableNumber() : "");
            c7.setCellStyle(centerStyle);

            // 8: Ghi chú
            Cell c8 = row.createCell(8);
            c8.setCellValue(group.getNote() != null ? group.getNote() : "");
            c8.setCellStyle(dataStyle);

            // 9: Kích hoạt
            Cell c9 = row.createCell(9);
            c9.setCellValue(Boolean.TRUE.equals(group.getIsActive()) ? "Đang hoạt động" : "Đã khóa");
            c9.setCellStyle(centerStyle);
        }

        autoSizeColumns(sheet, headers.length);
    }

    private void autoSizeColumns(Sheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1200, 3500));
        }
    }

    private String resolveDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            domain = defaultBaseUrl;
        }
        domain = domain.trim();
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = "https://" + domain;
        }
        while (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        return domain;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createDataStyle(Workbook workbook, boolean center) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        style.setAlignment(center ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createLinkStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setUnderline(Font.U_SINGLE);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}

