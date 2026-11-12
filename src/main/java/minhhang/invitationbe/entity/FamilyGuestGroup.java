package minhhang.invitationbe.entity;

import jakarta.persistence.*;
import lombok.*;
import minhhang.invitationbe.enums.GuestSide;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "family_guest_groups",
        indexes = {
                @Index(name = "idx_family_guest_groups_side", columnList = "guest_side"),
                @Index(name = "idx_family_guest_groups_table", columnList = "table_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyGuestGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invite_code", nullable = false, unique = true, length = 80)
    private String inviteCode;

    @Column(name = "group_name", nullable = false, length = 200)
    private String groupName;

    @Column(name = "guest_side", nullable = false, length = 10)
    private GuestSide guestSide;

    @Column(name = "relationship", length = 100)
    private String relationship;

    @Builder.Default
    @Column(name = "expected_guest_count", nullable = false)
    private Integer expectedGuestCount = 1;

    @Column(name = "table_number", length = 30)
    private String tableNumber;

    @Column(name = "note", length = 1000)
    private String note;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.inviteCode == null || this.inviteCode.isBlank()) {
            this.inviteCode = UUID.randomUUID().toString();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.expectedGuestCount == null) {
            this.expectedGuestCount = 1;
        }
    }
}

