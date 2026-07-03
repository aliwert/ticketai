CREATE TABLE IF NOT EXISTS tickets (
    id UUID PRIMARY KEY,
    reservation_id UUID NOT NULL,
    payment_id UUID NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    movie_id VARCHAR(255),
    session_id UUID NOT NULL,
    seat_id UUID NOT NULL,
    ticket_type VARCHAR(50) NOT NULL DEFAULT 'REGULAR',
    status VARCHAR(50) NOT NULL DEFAULT 'ISSUED',
    qr_code_hash VARCHAR(255),
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_reservation_id ON tickets(reservation_id);
CREATE INDEX idx_tickets_payment_id ON tickets(payment_id);
CREATE INDEX idx_tickets_status ON tickets(status);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_processed_events_type ON processed_events(event_type);
