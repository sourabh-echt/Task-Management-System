ALTER TABLE work_items
    DROP CONSTRAINT IF EXISTS work_items_work_type_check;

ALTER TABLE work_items
    ADD CONSTRAINT chk_work_items_work_type
        CHECK (work_type IN ('EPIC', 'STORY', 'TASK', 'BUG'));
