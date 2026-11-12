package minhhang.invitationbe.service;

import minhhang.invitationbe.entity.FamilyGuestGroup;
import minhhang.invitationbe.entity.InvitedGuest;

import java.util.List;

public interface ExcelExportService {

    byte[] exportGuests(List<InvitedGuest> guests, String domain);

    byte[] exportFamilyGroups(List<FamilyGuestGroup> groups, String domain);

    byte[] exportAll(List<InvitedGuest> guests, List<FamilyGuestGroup> groups, String domain);
}

