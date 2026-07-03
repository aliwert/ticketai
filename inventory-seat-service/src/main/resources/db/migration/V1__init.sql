CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100),
    duration_minutes INTEGER NOT NULL,
    rating VARCHAR(10),
    poster_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movies_genre ON movies(genre);

CREATE TABLE auditoriums (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    total_rows INTEGER NOT NULL,
    total_columns INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    movie_id UUID NOT NULL REFERENCES movies(id),
    auditorium_id UUID NOT NULL REFERENCES auditoriums(id),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_session_time CHECK (end_time > start_time)
);

CREATE INDEX idx_sessions_movie_id ON sessions(movie_id);
CREATE INDEX idx_sessions_auditorium_id ON sessions(auditorium_id);
CREATE INDEX idx_sessions_start_time ON sessions(start_time);

CREATE TABLE seats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    auditorium_id UUID NOT NULL REFERENCES auditoriums(id),
    row_number INTEGER NOT NULL,
    column_number INTEGER NOT NULL,
    seat_label VARCHAR(10) NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'STANDARD',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_seat_auditorium_row_col UNIQUE (auditorium_id, row_number, column_number)
);

CREATE INDEX idx_seats_auditorium_id ON seats(auditorium_id);

CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID NOT NULL REFERENCES sessions(id),
    seat_id UUID NOT NULL REFERENCES seats(id),
    user_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'LOCKED',
    locked_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reservation_session_seat UNIQUE (session_id, seat_id)
);

CREATE INDEX idx_reservations_session_id ON reservations(session_id);
CREATE INDEX idx_reservations_seat_id ON reservations(seat_id);
CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_status ON reservations(status);
