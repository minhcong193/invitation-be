package minhhang.invitationbe.entity;

import jakarta.persistence.*;
import lombok.*;
import minhhang.invitationbe.enums.ModerationStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "guestbook_messages",
        indexes = {
                @Index(name = "idx_guestbook_status_created", columnList = "moderation_status, created_at DESC"),
                @Index(name = "idx_guestbook_invited_guest", columnList = "invited_guest_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestbookMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_guest_id")
    private InvitedGuest invitedGuest;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Builder.Default
    @Column(name = "moderation_status", nullable = false, length = 16)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "approved_at")
    private Instant approvedAt;
}

