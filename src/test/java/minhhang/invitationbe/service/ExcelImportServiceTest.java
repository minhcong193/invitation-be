package minhhang.invitationbe.service;

import minhhang.invitationbe.dto.response.ExcelImportResult;
import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.entity.InvitedGuest;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import minhhang.invitationbe.repository.FamilyGuestGroupRepository;
import minhhang.invitationbe.repository.InvitedGuestRepository;
import minhhang.invitationbe.service.impl.ExcelImportServiceImpl;
import minhhang.invitationbe.service.impl.ExcelTemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelImportServiceTest {

    private final List<InvitedGuest> savedGuests = new ArrayList<>();
    private final List<FamilyGuestGroup> savedFamilyGroups = new ArrayList<>();

    private ExcelTemplateService templateService;
    private ExcelImportService importService;

    @BeforeEach
    void setUp() {
        savedGuests.clear();
        savedFamilyGroups.clear();

        InvitedGuestRepository invitedGuestRepository = createProxy(InvitedGuestRepository.class, savedGuests);
        FamilyGuestGroupRepository familyGuestGroupRepository = createProxy(FamilyGuestGroupRepository.class, savedFamilyGroups);

        templateService = new ExcelTemplateServiceImpl();
        importService = new ExcelImportServiceImpl(invitedGuestRepository, familyGuestGroupRepository);
    }

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, List<?> captured) {
        return (T) Proxy.newProxyInstance(
                intf.getClassLoader(),
                new Class<?>[]{intf},
                (proxy, method, args) -> {
                    if ("saveAll".equals(method.getName()) && args != null && args.length == 1) {
                        Iterable<?> iterable = (Iterable<?>) args[0];
                        for (Object item : iterable) {
                            ((List<Object>) captured).add(item);
                        }
                        return args[0];
                    }
                    return null;
                }
        );
    }

    @Test
    void testImportGuestsFromGeneratedTemplate() {
        byte[] templateBytes = templateService.generateGuestTemplate();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "guest_import_template.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                templateBytes
        );

        ExcelImportResult result = importService.importGuests(file);

        assertNotNull(result);
        assertEquals(3, result.getTotalRows());
        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertTrue(result.getErrors().isEmpty());

        assertEquals(3, savedGuests.size());
        assertEquals("Nguyễn Văn Tuấn", savedGuests.get(0).getFullName());
        assertEquals(GuestSide.GROOM, savedGuests.get(0).getGuestSide());
        assertEquals("Bạn cấp 3 của chú rể", savedGuests.get(0).getMessage());
        assertEquals(RsvpStatus.PENDING, savedGuests.get(0).getRsvpStatus());
        assertNull(savedGuests.get(0).getRespondedAt());
        assertTrue(savedGuests.get(0).getIsActive());

        assertEquals("Trần Thị Mai", savedGuests.get(1).getFullName());
        assertEquals(GuestSide.BRIDE, savedGuests.get(1).getGuestSide());
        assertEquals("Đồng nghiệp cô dâu", savedGuests.get(1).getMessage());
        assertEquals(RsvpStatus.PENDING, savedGuests.get(1).getRsvpStatus());
        assertNull(savedGuests.get(1).getRespondedAt());
        assertTrue(savedGuests.get(1).getIsActive());

        assertEquals("Lê Hoàng Long", savedGuests.get(2).getFullName());
        assertEquals(GuestSide.GROOM, savedGuests.get(2).getGuestSide());
        assertEquals("Bạn đại học", savedGuests.get(2).getMessage());
        assertEquals(RsvpStatus.PENDING, savedGuests.get(2).getRsvpStatus());
        assertNull(savedGuests.get(2).getRespondedAt());
        assertTrue(savedGuests.get(2).getIsActive());
    }

    @Test
    void testImportFamilyGroupsFromGeneratedTemplate() {
        byte[] templateBytes = templateService.generateFamilyGroupTemplate();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "family_group_import_template.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                templateBytes
        );

        ExcelImportResult result = importService.importFamilyGroups(file);

        assertNotNull(result);
        assertEquals(2, result.getTotalRows());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertTrue(result.getErrors().isEmpty());

        assertEquals(2, savedFamilyGroups.size());
        assertEquals("Gia đình bác Nguyễn Văn A", savedFamilyGroups.get(0).getGroupName());
        assertEquals(GuestSide.GROOM, savedFamilyGroups.get(0).getGuestSide());
        assertEquals("Bác ruột (bên nội)", savedFamilyGroups.get(0).getRelationship());
        assertEquals(1, savedFamilyGroups.get(0).getExpectedGuestCount());
        assertNotNull(savedFamilyGroups.get(0).getInviteCode());

        assertEquals("Gia đình cô Trần Thị B", savedFamilyGroups.get(1).getGroupName());
        assertEquals(GuestSide.BRIDE, savedFamilyGroups.get(1).getGuestSide());
        assertEquals("Cô ruột (bên ngoại)", savedFamilyGroups.get(1).getRelationship());
        assertEquals(1, savedFamilyGroups.get(1).getExpectedGuestCount());
        assertNotNull(savedFamilyGroups.get(1).getInviteCode());
    }
}
