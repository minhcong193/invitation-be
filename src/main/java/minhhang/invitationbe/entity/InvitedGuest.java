package minhhang.invitationbe.entity;

import jakarta.persistence.*;
import lombok.*;
import minhhang.invitationbe.enums.GuestSide;
import minhhang.invitationbe.enums.RsvpStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "invited_guests",
        indexes = {
                @Index(name = "idx_invited_guests_side", columnList = "guest_side"),
                @Index(name = "idx_invited_guests_rsvp", columnList = "rsvp_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitedGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invite_code", nullable = false, unique = true, length = 80)
    private String inviteCode;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "guest_side", nullable = false, length = 10)
    private GuestSide guestSide;

    @Column(name = "message", length = 1000)
    private String message;

    @Builder.Default
    @Column(name = "rsvp_status", length = 16)
    private RsvpStatus rsvpStatus = RsvpStatus.PENDING;

    @Column(name = "responded_at")
    private Instant respondedAt;

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
        if (this.rsvpStatus == null) {
            this.rsvpStatus = RsvpStatus.PENDING;
        }
    }
}
