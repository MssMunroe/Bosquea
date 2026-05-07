import sqlite3
from archivoCSV import archivoCSVParques, archivoCSVRutas, archivoCSVUser

#Datos para Parques Naturales
def datosParque():

    data = archivoCSVParques()
    stmt = "INSERT INTO parques_naturales (id_parque, nombre, ubi, hecta) VALUES (%s, %s, %s, %s)"
    # cursor.executemany(stmt, data)
    # conn.commit()

#Datos para Rutas
def datosRutas():

    data = archivoCSVRutas()
    stmt = "INSERT INTO rutas (id_ruta, nombre, telefono, email, web, dificultad, id_parque) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    # cursor.executemany(stmt, data)
    # conn.commit()


#Datos para Rutas
def datosUsers():

    data = archivoCSVUser()
    stmt = "INSERT INTO usuarios (id_user, nickname, nombre, email, telefono, dni, codigo_postal, contra) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
    # cursor.executemany(stmt, data)
    # conn.commit()

#PRINCIPAL

conn = sqlite3.connect(database='bosquea.db')
cursor = conn.cursor()

#Agregamos datos iniciales
print("Agregando Datos...")
datosParque()
datosRutas()
datosUsers()

cursor.close() 
conn.close()