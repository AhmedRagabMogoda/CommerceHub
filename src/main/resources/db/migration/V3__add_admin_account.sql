INSERT INTO users (
    username,
    email,
    password,
    first_name,
    last_name,
    phone_number,
    is_enable,
    is_locked,
    email_verified,
    last_login_at,
    status_code,
    is_deleted,
    created_by,
    created_at,
    last_modified_by,
    last_modified_at
)
SELECT
    'admin',
    'admin@commercehub.com',
    '$2a$10$UhSr9zPTiDzwva7MX7n4qeFh2RHqGXwd4CYu6X.zzC8ToyaCHYoc6',
    'System',
    'Admin',
    NULL,
    1,         
    0,          
    1,          
    GETDATE(),
    'ACTIVE',
    0,
    'system',
    GETDATE(),
    'system',
    GETDATE()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);
