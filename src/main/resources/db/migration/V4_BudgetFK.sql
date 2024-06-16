ALTER TABLE budget
    ADD COLUMN author_id integer REFERENCES author (id);