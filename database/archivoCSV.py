import csv
import os

#Extraer los datos
def archivoCSVParques():
    if os.path.exists("users.csv") and os.path.getsize("users.csv") > 0:
        with open("users.csv", "a", newline="", encoding="utf-8") as archivo:
            escritor = csv.writer(archivo)
            escritor.writerow(usuario)
        print("-- Registrado correctamente --")
    else:
        print("**Archivo incorrecto**")
        exit(1)

def archivoCSVRutas():
    if os.path.exists("users.csv") and os.path.getsize("users.csv") > 0:
        with open("users.csv", "a", newline="", encoding="utf-8") as archivo:
            escritor = csv.writer(archivo)
            escritor.writerow(usuario)
        print("-- Registrado correctamente --")
    else:
        print("**Archivo incorrecto**")
        exit(1)

def archivoCSVUser():
    if os.path.exists("users.csv") and os.path.getsize("users.csv") > 0:
        with open("users.csv", "a", newline="", encoding="utf-8") as archivo:
            escritor = csv.writer(archivo)
            escritor.writerow(usuario)
        print("-- Registrado correctamente --")
    else:
        print("**Archivo incorrecto**")
        exit(1)
