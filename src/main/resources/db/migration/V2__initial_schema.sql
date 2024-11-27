ALTER TABLE registrations ADD created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL;
ALTER TABLE registrations ADD registration_status VARCHAR(64) NOT NULL;