-- =============================================================================
-- Labzang 데이터베이스 초기화 스크립트
-- OAuth, User, Admin 서비스를 위한 스키마 및 기본 데이터 설정
-- =============================================================================

-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS labzang_oauth;
CREATE SCHEMA IF NOT EXISTS labzang_user;
CREATE SCHEMA IF NOT EXISTS labzang_admin;

-- OAuth 서비스용 테이블들
CREATE TABLE IF NOT EXISTS labzang_oauth.oauth_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS labzang_oauth.oauth_users (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    profile_image_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(provider, provider_id),
    UNIQUE(email)
);

-- User 서비스용 테이블들
CREATE TABLE IF NOT EXISTS labzang_user.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS labzang_user.user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES labzang_user.users(id) ON DELETE CASCADE,
    bio TEXT,
    website VARCHAR(255),
    location VARCHAR(255),
    birth_date DATE,
    gender VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin 서비스용 테이블들
CREATE TABLE IF NOT EXISTS labzang_admin.admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ADMIN',
    permissions TEXT[], -- JSON array of permissions
    is_active BOOLEAN DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS labzang_admin.admin_sessions (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT REFERENCES labzang_admin.admin_users(id) ON DELETE CASCADE,
    session_token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_oauth_tokens_user_id ON labzang_oauth.oauth_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_oauth_tokens_provider ON labzang_oauth.oauth_tokens(provider);
CREATE INDEX IF NOT EXISTS idx_oauth_users_email ON labzang_oauth.oauth_users(email);
CREATE INDEX IF NOT EXISTS idx_oauth_users_provider ON labzang_oauth.oauth_users(provider, provider_id);

CREATE INDEX IF NOT EXISTS idx_users_email ON labzang_user.users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON labzang_user.users(username);
CREATE INDEX IF NOT EXISTS idx_users_active ON labzang_user.users(is_active);

CREATE INDEX IF NOT EXISTS idx_admin_users_email ON labzang_admin.admin_users(email);
CREATE INDEX IF NOT EXISTS idx_admin_users_username ON labzang_admin.admin_users(username);
CREATE INDEX IF NOT EXISTS idx_admin_sessions_token ON labzang_admin.admin_sessions(session_token);
CREATE INDEX IF NOT EXISTS idx_admin_sessions_expires ON labzang_admin.admin_sessions(expires_at);

-- 기본 관리자 계정 생성 (개발용)
INSERT INTO labzang_admin.admin_users (username, email, password_hash, role, permissions)
VALUES (
    'admin',
    'admin@labzang.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGjO6NE3Z6pE.B3dqgF.3G', -- password: admin123
    'SUPER_ADMIN',
    ARRAY['USER_MANAGEMENT', 'SYSTEM_CONFIG', 'ANALYTICS', 'CONTENT_MANAGEMENT']
) ON CONFLICT (email) DO NOTHING;

-- 권한 설정
GRANT ALL PRIVILEGES ON SCHEMA labzang_oauth TO labzang;
GRANT ALL PRIVILEGES ON SCHEMA labzang_user TO labzang;
GRANT ALL PRIVILEGES ON SCHEMA labzang_admin TO labzang;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA labzang_oauth TO labzang;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA labzang_user TO labzang;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA labzang_admin TO labzang;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA labzang_oauth TO labzang;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA labzang_user TO labzang;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA labzang_admin TO labzang;
