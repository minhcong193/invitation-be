CREATE TABLE invited_guests (
                                id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Mã dùng trên URL, không nên dùng ID tuần tự công khai
                                invite_code     VARCHAR(80)  NOT NULL UNIQUE,

                                full_name       VARCHAR(150) NOT NULL,

                                guest_side      VARCHAR(10)  NOT NULL
                                    CHECK (guest_side IN ('groom', 'bride')),

    -- pending: chờ phản hồi
    -- attending: tham dự
    -- declined: không tham dự
                                rsvp_status     VARCHAR(16) NOT NULL DEFAULT 'pending'
                                    CHECK (
                                        rsvp_status IN ('pending', 'attending', 'declined')
                                        ),

                                message         VARCHAR(1000),

                                responded_at    TIMESTAMPTZ,

                                is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
                                created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT invited_guest_response_time_check CHECK (
                                    (rsvp_status = 'pending' AND responded_at IS NULL)
                                        OR
                                    (rsvp_status IN ('attending', 'declined') AND responded_at IS NOT NULL)
                                    )
);

CREATE INDEX idx_invited_guests_side
    ON invited_guests (guest_side);

CREATE INDEX idx_invited_guests_rsvp
    ON invited_guests (rsvp_status);

CREATE INDEX idx_invited_guests_active
    ON invited_guests (is_active)
    WHERE is_active = TRUE;


-- =====================================================
-- 2. KHÁCH HỌ HÀNG / NHÓM GIA ĐÌNH
-- =====================================================

CREATE TABLE family_guest_groups (
                                     id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Mã dùng trên URL, không nên dùng ID tuần tự công khai
                                     invite_code         VARCHAR(80)  NOT NULL UNIQUE,

    -- Ví dụ: "Gia đình bác Nguyễn Văn A"
                                     group_name          VARCHAR(200) NOT NULL,

                                     guest_side          VARCHAR(10)  NOT NULL
                                         CHECK (guest_side IN ('groom', 'bride')),

                                     relationship        VARCHAR(100),

                                     expected_guest_count SMALLINT NOT NULL DEFAULT 1
                                         CHECK (expected_guest_count BETWEEN 1 AND 50),

                                     table_number        VARCHAR(30),
                                     note                VARCHAR(1000),

                                     is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
                                     created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_family_guest_groups_side
    ON family_guest_groups (guest_side);

CREATE INDEX idx_family_guest_groups_table
    ON family_guest_groups (table_number);


-- =====================================================
-- 3. SỔ LƯU BÚT
-- Có thể gửi bởi khách cụ thể hoặc khách truy cập thông thường
-- =====================================================

CREATE TABLE guestbook_messages (
                                    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Có giá trị nếu lời chúc đến từ khách có mã thiệp
                                    invited_guest_id    BIGINT
                                                                      REFERENCES invited_guests(id)
                                                                          ON DELETE SET NULL,

                                    display_name        VARCHAR(150)  NOT NULL,
                                    message             VARCHAR(2000) NOT NULL,

                                    moderation_status   VARCHAR(16) NOT NULL DEFAULT 'pending'
                                        CHECK (
                                            moderation_status IN (
                                                                  'pending',
                                                                  'approved',
                                                                  'rejected'
                                                )
                                            ),

                                    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    approved_at         TIMESTAMPTZ,

                                    CONSTRAINT guestbook_message_not_blank CHECK (
                                        LENGTH(BTRIM(message)) > 0
                                        ),

                                    CONSTRAINT guestbook_name_not_blank CHECK (
                                        LENGTH(BTRIM(display_name)) > 0
                                        ),

                                    CONSTRAINT guestbook_approval_time_check CHECK (
                                        moderation_status <> 'approved'
                                            OR approved_at IS NOT NULL
                                        )
);

CREATE INDEX idx_guestbook_status_created
    ON guestbook_messages (
                           moderation_status,
                           created_at DESC
        );

CREATE INDEX idx_guestbook_invited_guest
    ON guestbook_messages (invited_guest_id)
    WHERE invited_guest_id IS NOT NULL;