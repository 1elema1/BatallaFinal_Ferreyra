CREATE TABLE personajes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    apodo TEXT UNIQUE,
    tipo TEXT,
    vida_final INTEGER,
    victorias INTEGER DEFAULT 0,
    derrotas INTEGER DEFAULT 0,
    supremos_usados INTEGER DEFAULT 0,
    armas_invocadas INTEGER DEFAULT 0
);

CREATE TABLE batallas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    heroe_id INTEGER,
    villano_id INTEGER,
    ganador_id INTEGER,
    turnos INTEGER,

    FOREIGN KEY (heroe_id) REFERENCES personajes(id),
    FOREIGN KEY (villano_id) REFERENCES personajes(id),
    FOREIGN KEY (ganador_id) REFERENCES personajes(id)
);
