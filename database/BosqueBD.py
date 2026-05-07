import sqlite3


def creacionDB():
    # Con IF NOT EXISTS para evitar errores de creación
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS parques_naturales (
            id_parque INT PRIMARY KEY,
            nombre VARCHAR(150) NOT NULL,
            ubi VARCHAR(200) NOT NULL,
            hecta DECIMAL(12,2) NOT NULL
        );
        """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS rutas (
            id_ruta INT PRIMARY KEY,
            nombre VARCHAR(150) NOT NULL,
            telefono VARCHAR(50),
            email VARCHAR(150),
            web VARCHAR(200),
            dificultad VARCHAR(50),
            id_parque INT NOT NULL,
            FOREIGN KEY (id_parque) REFERENCES parques_naturales(id)
        );
        """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS usuario (
            id_user INT PRIMARY KEY,
            nickname VARCHAR(50) UNIQUE NOT NULL,
            nombre VARCHAR(100) NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL,
            telefono VARCHAR(20),
            dni VARCHAR(20) UNIQUE NOT NULL,
            codigo_postal VARCHAR(10),
            contra VARCHAR(255) NOT NULL
        );
        """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS visitados (
            id_user INT NOT NULL,
            id_parque INT NOT NULL,
            PRIMARY KEY (id_user, id_parque),
            FOREIGN KEY (id_user) REFERENCES usuario(id),
            FOREIGN KEY (id_parque) REFERENCES parques_naturales(id)
        );
        """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS deseados (
            id_user INT NOT NULL,
            id_parque INT NOT NULL,
            PRIMARY KEY (id_user, id_parque),
            FOREIGN KEY (id_user) REFERENCES usuario(id),
            FOREIGN KEY (id_parque) REFERENCES parques_naturales(id)
        );
        """)



#PRINCIPAL

conn = sqlite3.connect(database='bosquea.db')
cursor = conn.cursor()

#Creamos la base de datos
print("Creando Base de Datos")
creacionDB()

cursor.close() 
conn.close()