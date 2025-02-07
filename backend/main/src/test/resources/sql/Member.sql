insert into storage_file (id, created_at, folder, name)
values ('00000000-0000-0000-0000-000000000001', '2025-01-01 00:00:00', 'user/00000000-0000-0000-0000-000000000001',
        'profile_picture.jpeg');

insert into "user" (id, auth_resource_id, created_at, email, register_email, type, email_verified, profile_picture_id)
values ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '2025-01-01 00:00:00',
        'sonia@example.com', 'member1@example.com', 'MEMBER', true, '00000000-0000-0000-0000-000000000001'),
       ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '2025-01-01 00:00:00',
        'charlie@example.com', 'member2@example.com', 'MEMBER', true, null),
       ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', '2025-01-01 00:00:00',
        'anita@example.com', 'member3@example.com', 'MEMBER', true, null),
       ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004', '2025-01-01 00:00:00',
        'delmer@example.com', 'member4@example.com', 'MEMBER', true, null);

insert into member(id, nickname)
values ('00000000-0000-0000-0000-000000000001', 'Sonia'),
       ('00000000-0000-0000-0000-000000000002', 'Charlie'),
       ('00000000-0000-0000-0000-000000000003', 'Anita'),
       ('00000000-0000-0000-0000-000000000004', 'Delmer');
