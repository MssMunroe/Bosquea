import re
import sqlite3
from verificarUsuarios import verificarUsuarios

def obtenerID():
    conn = sqlite3.connect(database='bosquea.db')
    cursor = conn.cursor()

    cursor.execute("""
        SELECT id_user 
        FROM usuarios 
        ORDER BY id_user DESC
        LIMIT 1
        ;""")

    identificador = cursor.fetchone()
    
    try:
        num = int(identificador[0][2:5])+1
        identificador = f"us00{num}"
    except ValueError as e:
        print("**Error: Problemas al asignar el nuevo ID**")

    
    cursor.close() 
    conn.close()

    return identificador

def comprobarDNI(num, letra):
    match letra.capitalize():
        case "T":
            if num != 0:
                print("**Error: DNI no valido**")
                return ""
        case "R":
            if num != 1:
                print("**Error: DNI no valido**")
                return ""
        case "W":
            if num != 2:
                print("**Error: DNI no valido**")
                return ""
        case "A":
            if num != 3:
                print("**Error: DNI no valido**")
                return ""
        case "G":
            if num != 4:
                print("**Error: DNI no valido**")
                return ""
        case "M":
            if num != 5:
                print("**Error: DNI no valido**")
                return ""
        case "Y":
            if num != 6:
                print("**Error: DNI no valido**")
                return ""
        case "F":
            if num != 7:
                print("**Error: DNI no valido**")
                return ""
        case "P":
            if num != 8:
                print("**Error: DNI no valido**")
                return ""
        case "D":
            if num != 9:
                print("**Error: DNI no valido**")
                return ""
        case "X":
            if num != 10:
                print("**Error: DNI no valido**")
                return ""
        case "B":
            if num != 11:
                print("**Error: DNI no valido**")
                return ""
        case "N":
            if num != 12:
                print("**Error: DNI no valido**")
                return ""
        case "J":
            if num != 13:
                print("**Error: DNI no valido**")
                return ""
        case "Z":
            if num != 14:
                print("**Error: DNI no valido**")
                return ""
        case "S":
            if num != 15:
                print("**Error: DNI no valido**")
                return ""
        case "Q":
            if num != 16:
                print("**Error: DNI no valido**")
                return ""
        case "V":
            if num != 17:
                print("**Error: DNI no valido**")
                return ""
        case "H":
            if num != 18:
                print("**Error: DNI no valido**")
                return ""
        case "L":
            if num != 19:
                print("**Error: DNI no valido**")
                return ""
        case "C":
            if num != 20:
                print("**Error: DNI no valido**")
                return ""
        case "K":
            if num != 21:
                print("**Error: DNI no valido**")
                return ""
        case "E":
            if num != 22:
                print("**Error: DNI no valido**")
                return ""
        case _:
            print("**Error: DNI no valido**")
            return ""

    return f"{num}"+letra

def confirmarName(name):
    if len(name) > 100:
        print("**Error: Nombre demasiado largo**")
        return ""
    
    else:
        name = name.capitalize()
    return name

def confirmarTel(tel):
    if len(tel) > 20:
        print("**Error: Teléfono demasiado largo**")
        return ""
    try:
        tel = int(tel)
    except ValueError as e:
        print("**Error: Teléfono debe ser solo números**")
        return ""
    
    return tel

def confirmarEmail(email):
    if len(email) > 150:
        print("**Error: Email demasiado largo**")
        return ""
    if "@" in email:
        parte = email.split("@")
        if not "." in parte[1]:
            print("**Error: Email incorrecto**")
            return ""
    else:
        print("**Error: Email incorrecto**")
        return ""
    return email

def confirmarCP(cp):
    if len(cp) < 3 or len(cp) > 10:
        print("**Error: Código Postal incorrecto**")
        return ""
    return cp

def confirmarDNI(dni):
    #Solo validaré dni españoles
    if len(dni) != 9:
        print("**Error: DNI no valido**")    
        return ""
    try:
        num = int(dni[0:8]) % 23
        comprobarDNI(num, str(dni[8:9]))
    except ValueError as e:
        print("**Error: DNI no valido**")
        return ""

    #Mirar que no este en la base de datos
    return dni

def confirmarNN(nn):
    if len(nn) > 50:
        print("**Error: Nickname demasiado largo**")
        return ""

    #Mirar que no este en la base de datos
    return nn

def confirmarContra(contra):
    #Esto valida que sea mayor a 10 y contenga mínimo una letra y un número
    if len(contra) < 10 or not re.search(r'(?=.*[a-z])(?=.*[A-Z])(?=.*\d)', contra):
        print("** Contraseña muy débil **")
        print("""
            - Mínimo 10 carácteres
            - Mínimo una minúscula
            - Mínimo una mayúscula
            - Mínimo un número
        """)
        return ""
    return contra

def datos():

    while True:
        name = str(input("Nombre: ")).lower()
        name = confirmarName(name)
        if name != "":
            break
    while True:
        tel = str(input("Teléfono: "))
        tel = confirmarTel(tel)
        if tel != "":
            break
    while True:
        email = str(input("Email: ")).lower()
        email = confirmarEmail(email)
        if email != "":
            break
    while True:
        cp = str(input("Código Postal: "))
        cp = confirmarCP(cp)
        if cp != "":
            break
    while True:
        dni = str(input("DNI: ")).lower()
        dni = confirmarDNI(dni)
        if dni != "":
            break
    while True:
        nn = str(input("Nickname: ")).lower()
        nn = confirmarNN(nn)
        if nn != "":
            break
    while True:
        contra = str(input("Contraseña: "))
        contra = confirmarContra(contra)
        if contra != "":
            break

    return [nn, name, email, tel, dni, cp, contra]


def principal():
    
    usuario = [obtenerID()] + datos() #Aquí debemos agregar el id de la base de datos

    print(f"""
    -- Confirma tus datos --
 ----------------------------------------
        Nombre: {usuario[2]}
        Teléfono: {usuario[4]}
        Email: {usuario[3]}
        Código Postal: {usuario[6]}
        DNI: {usuario[5]}
        Nickname: {usuario[1]}
        Contraseña: {usuario[7]}
 ----------------------------------------
    """)

    print("¿Estan los datos correctos?")
    confir = str(input("(y / n) -> ")).lower() #Esto será un bóton en interfaz

    if confir == "n":
        print("\nRellene de nuevo sus datos")
        print("--------------------------------------")
        principal()

    #Aquí mando los datos al csv
    if confir == "y":
        #Verificar datos en la base de datos y archivo csv
        archivoCSVUser()
    

#MAIN

print("""
---------------------------
    NUEVO USUARIO
---------------------------        
""")

principal()
