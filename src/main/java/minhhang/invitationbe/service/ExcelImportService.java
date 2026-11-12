package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.response.ExcelImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {

    ExcelImportResult importGuests(MultipartFile file);

    ExcelImportResult importFamilyGroups(MultipartFile file);
}

