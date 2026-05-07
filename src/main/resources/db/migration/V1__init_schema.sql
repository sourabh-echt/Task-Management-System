-- ============================================================
-- V1__init_schema.sql
-- Initial schema for Jira-clone (EchtTech SCRUM Board)
-- ============================================================

-- USERS (basic, security layer can be expanded)
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    avatar_url  VARCHAR(500),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- PROJECTS
CREATE TABLE projects (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key         VARCHAR(10)  NOT NULL UNIQUE,   -- e.g. SCRUM
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    owner_id    UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- SPRINTS  (Dhananjay's module)
CREATE TABLE sprints (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id  UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    goal        TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PLANNED'
                    CHECK (status IN ('PLANNED','ACTIVE','COMPLETED')),
    start_date  DATE,
    end_date    DATE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- WORK_ITEMS  (Tasks / Stories / Bugs — Sourabh's create-task module)
CREATE TABLE work_items (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id   UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    sprint_id    UUID REFERENCES sprints(id) ON DELETE SET NULL,
    item_key     VARCHAR(20) NOT NULL UNIQUE,   -- e.g. SCRUM-9
    work_type    VARCHAR(20)  NOT NULL DEFAULT 'TASK'
                     CHECK (work_type IN ('TASK','STORY','BUG','EPIC','SUBTASK')),
    summary      VARCHAR(255) NOT NULL,
    description  TEXT,
    status       VARCHAR(20)  NOT NULL DEFAULT 'TO_DO'
                     CHECK (status IN ('TO_DO','IN_PROGRESS','IN_REVIEW','DONE')),
    priority     VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM'
                     CHECK (priority IN ('LOWEST','LOW','MEDIUM','HIGH','HIGHEST')),
    assignee_id  UUID REFERENCES users(id) ON DELETE SET NULL,
    reporter_id  UUID REFERENCES users(id) ON DELETE SET NULL,
    story_points INTEGER,
    due_date     DATE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- PROJECT_MEMBERS (many-to-many)
CREATE TABLE project_members (
    project_id  UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    role        VARCHAR(20) NOT NULL DEFAULT 'MEMBER'
                    CHECK (role IN ('OWNER','ADMIN','MEMBER','VIEWER')),
    joined_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (project_id, user_id)
);

-- COMMENTS
CREATE TABLE comments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_item_id UUID NOT NULL REFERENCES work_items(id) ON DELETE CASCADE,
    author_id    UUID REFERENCES users(id) ON DELETE SET NULL,
    body         TEXT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ATTACHMENTS
CREATE TABLE attachments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_item_id UUID NOT NULL REFERENCES work_items(id) ON DELETE CASCADE,
    uploader_id  UUID REFERENCES users(id) ON DELETE SET NULL,
    filename     VARCHAR(255) NOT NULL,
    file_url     VARCHAR(500) NOT NULL,
    file_size    BIGINT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- AUDIT LOG (optional but professional)
CREATE TABLE audit_log (
    id           BIGSERIAL PRIMARY KEY,
    entity_type  VARCHAR(50) NOT NULL,
    entity_id    UUID NOT NULL,
    action       VARCHAR(30) NOT NULL,
    actor_id     UUID REFERENCES users(id) ON DELETE SET NULL,
    old_value    JSONB,
    new_value    JSONB,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for common queries
CREATE INDEX idx_work_items_sprint    ON work_items(sprint_id);
CREATE INDEX idx_work_items_project   ON work_items(project_id);
CREATE INDEX idx_work_items_assignee  ON work_items(assignee_id);
CREATE INDEX idx_work_items_status    ON work_items(status);
CREATE INDEX idx_sprints_project      ON sprints(project_id);
CREATE INDEX idx_sprints_status       ON sprints(status);
CREATE INDEX idx_comments_work_item   ON comments(work_item_id);
CREATE INDEX idx_audit_entity         ON audit_log(entity_type, entity_id);

-- Auto-update updated_at trigger
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_users_updated_at       BEFORE UPDATE ON users       FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
CREATE TRIGGER set_projects_updated_at    BEFORE UPDATE ON projects    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
CREATE TRIGGER set_sprints_updated_at     BEFORE UPDATE ON sprints     FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
CREATE TRIGGER set_work_items_updated_at  BEFORE UPDATE ON work_items  FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
CREATE TRIGGER set_comments_updated_at    BEFORE UPDATE ON comments    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
