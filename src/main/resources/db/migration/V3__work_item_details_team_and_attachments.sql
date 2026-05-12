CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    team_name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE team_members (
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (team_id, user_id)
);

ALTER TABLE work_items
    ADD COLUMN parent_id UUID REFERENCES work_items(id) ON DELETE SET NULL,
    ADD COLUMN team_id UUID REFERENCES teams(id) ON DELETE SET NULL,
    ADD COLUMN start_date DATE;

CREATE TABLE work_item_labels (
    work_item_id UUID NOT NULL REFERENCES work_items(id) ON DELETE CASCADE,
    label VARCHAR(100) NOT NULL
);

CREATE INDEX idx_work_items_parent ON work_items(parent_id);
CREATE INDEX idx_work_items_team ON work_items(team_id);
CREATE INDEX idx_work_item_labels_work_item ON work_item_labels(work_item_id);
CREATE INDEX idx_team_members_user ON team_members(user_id);

CREATE TRIGGER set_teams_updated_at BEFORE UPDATE ON teams FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();
