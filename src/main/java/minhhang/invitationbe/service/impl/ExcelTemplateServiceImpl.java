package minhhang.invitationbe.service.impl;

import lombok.extern.slf4j.Slf4j;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.service.ExcelTemplateService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class ExcelTemplateServiceImpl implements ExcelTemplateService {

    private static final int MAX_ROWS = 5000;

    @Override
    public byte[] generateGuestTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Khach_Moi");
            sheet.setDisplayGridlines(true);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {
                    "Họ và tên (*)",
                    "Phía nhà (*)",
                    "Thông tin thêm"
            };

            // 1. Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Thêm dữ liệu mẫu
            Object[][] sampleData = {
                    {"Nguyễn Văn Tuấn", "Chú rể", "Bạn cấp 3 của chú rể"},
                    {"Trần Thị Mai", "Cô dâu", "Đồng nghiệp cô dâu"},
                    {"Lê Hoàng Long", "Chú rể", "Bạn đại học"}
            };

            for (int r = 0; r < sampleData.length; r++) {
                Row row = sheet.createRow(r + 1);
                row.setHeightInPoints(22);
                for (int c = 0; c < sampleData[r].length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellValue((String) sampleData[r][c]);
                    cell.setCellStyle(dataStyle);
                }
            }

            // 3. Dropdown cho cột Phía nhà (Cột 1: Chú rể, Cô dâu)
            String[] guestSideValues = {GuestSide.GROOM.getLabel(), GuestSide.BRIDE.getLabel()};
            addDropdownValidation(sheet, guestSideValues, 1, 1, MAX_ROWS,
                    "Phía nhà không hợp lệ", "Vui lòng chọn 'Chú rể' hoặc 'Cô dâu'!");

            // 4. Căn chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1500, 5000));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error creating guest excel template", e);
            throw new RuntimeException("Không thể tạo file template Excel khách mời: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateFamilyGroupTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Nhom_Gia_Dinh");
            sheet.setDisplayGridlines(true);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {
                    "Tên nhóm / Gia đình (*)",
                    "Phía nhà (*)",
                    "Mối quan hệ"
            };

            // 1. Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 2. Thêm dữ liệu mẫu
            Object[][] sampleData = {
                    {"Gia đình bác Nguyễn Văn A", "Chú rể", "Bác ruột (bên nội)"},
                    {"Gia đình cô Trần Thị B", "Cô dâu", "Cô ruột (bên ngoại)"}
            };

            for (int r = 0; r < sampleData.length; r++) {
                Row row = sheet.createRow(r + 1);
                row.setHeightInPoints(22);
                for (int c = 0; c < sampleData[r].length; c++) {
                    Cell cell = row.createCell(c);
                    Object val = sampleData[r][c];
                    cell.setCellValue(val != null ? val.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // 3. Dropdown cho cột Phía nhà (Cột 1: Chú rể, Cô dâu)
            String[] guestSideValues = {GuestSide.GROOM.getLabel(), GuestSide.BRIDE.getLabel()};
            addDropdownValidation(sheet, guestSideValues, 1, 1, MAX_ROWS,
                    "Phía nhà không hợp lệ", "Vui lòng chọn 'Chú rể' hoặc 'Cô dâu'!");

            // 4. Căn chỉnh độ rộng cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1500, 5000));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error creating family group excel template", e);
            throw new RuntimeException("Không thể tạo file template Excel nhóm gia đình: " + e.getMessage(), e);
        }
    }

    private void addDropdownValidation(XSSFSheet sheet, String[] values, int colIndex, int startRow, int endRow,
                                       String errorTitle, String errorMessage) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(values);
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, colIndex, colIndex);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox(errorTitle, errorMessage);
        sheet.addValidationData(validation);
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

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}

