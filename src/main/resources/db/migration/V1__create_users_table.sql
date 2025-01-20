CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    category VARCHAR(255) NOT NULL CHECK (category IN ('TRAVEL', 'FOOD', 'OFFICE_SUPPLIES', 'SOFTWARE_LICENSES', 'ENTERTAINMENT')),
    description VARCHAR(500) NOT NULL,
    date DATE NOT NULL,
    receipt_url VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'REIMBURSED', 'MISCELLANEOUS')),
    approved_by BIGINT,
    reimbursement_id BIGINT,
    rejection_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);

CREATE TABLE expense_attachments (
    id SERIAL PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    attachment_url VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE TABLE reimbursements (
    id SERIAL PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    processed_by BIGINT NOT NULL,
    reimbursement_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    department VARCHAR(100) NOT NULL,
    last_login TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);


ALTER TABLE expenses
ADD CONSTRAINT fk_expenses_user_id
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE expenses
ADD CONSTRAINT fk_expenses_approved_by
FOREIGN KEY (approved_by) REFERENCES users(id);

ALTER TABLE expenses
ADD CONSTRAINT fk_expenses_reimbursement_id
FOREIGN KEY (reimbursement_id) REFERENCES reimbursements(id);

ALTER TABLE expense_attachments
ADD CONSTRAINT fk_expense_attachments_expense_id
FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE;

ALTER TABLE reimbursements
ADD CONSTRAINT fk_reimbursements_expense_id
FOREIGN KEY (expense_id) REFERENCES expenses(id);

ALTER TABLE reimbursements
ADD CONSTRAINT fk_reimbursements_processed_by
FOREIGN KEY (processed_by) REFERENCES users(id);

ALTER TABLE notifications
ADD CONSTRAINT fk_notifications_user_id
FOREIGN KEY (user_id) REFERENCES users(id);
