import os
import csv
from app import app
from models import db, ParqueNatural, Ruta, Usuario, Rol
from werkzeug.security import generate_password_hash

def cargar_csv():
    with app.app_context():
        # LOCALIZACIÓN DE CARPETAS
        backend_dir = os.path.abspath(os.path.dirname(__file__))
        root_dir = os.path.dirname(backend_dir)
        database_dir = os.path.join(root_dir, 'database')

        print(f"Buscando archivos CSV en: {database_dir}")

        # CREAR ROLES
        if not Rol.query.first():
            db.session.add_all([
                Rol(id_rol=1, nombre="Administrador"),
                Rol(id_rol=2, nombre="Usuario")
            ])
            db.session.commit()
            print("✓ Roles iniciales creados.")

        # FUNCIÓN AUXILIAR PARA CARGAR TABLAS
        def importar_tabla(nombre_archivo, modelo, mapeo_func):
            ruta_archivo = os.path.join(database_dir, nombre_archivo)
            if not os.path.exists(ruta_archivo):
                print(f"X Error: No se encuentra {nombre_archivo}")
                return

            with open(ruta_archivo, encoding='utf-8') as f:
                # Usamos skipinitialspace por si hay espacios tras las comas
                reader = csv.DictReader(f, delimiter='|', skipinitialspace=True)
                contador = 0
                for row in reader:
                    # Usamos el primer campo (ID) para comprobar si ya existe
                    id_campo = list(row.keys())[0]
                    if not modelo.query.get(row[id_campo]):
                        objeto = mapeo_func(row)
                        db.session.add(objeto)
                        contador += 1
                db.session.commit()
                print(f"✓ {modelo.__name__}: {contador} registros nuevos cargados.")

        # EJECUCIÓN DE LAS CARGAS
        
        # Parques
        importar_tabla('parques.csv', ParqueNatural, lambda row: ParqueNatural(
            id_parque=row['id_parque'],
            nombre=row['nombre'],
            descripcion=row['descripcion'],
            ubicacion=row['ubicacion'],
            tamanio=row['tamanio'],
            img=row['img'],
            lat=row['lat'],
            lon=row['lon']
        ))

        # Usuarios
        importar_tabla('usuarios.csv', Usuario, lambda row: Usuario(
            id_usuario=row['id_usuario'],
            nickname=row['nickname'],
            nombre=row['nombre'],
            email=row['email'],
            telefono=row['telefono'],
            dni=row['dni'],
            codigo_postal=row['codigo_postal'],
            icono=row['icono'],
            # Hasheamos la contraseña del CSV antes de guardarla en la DB
            contra=generate_password_hash(row['contra'], method='pbkdf2:sha256'),
            rol_id=2 
        ))

        # Rutas
        importar_tabla('rutas.csv', Ruta, lambda row: Ruta(
            id_ruta=row['id_ruta'],
            nombre=row['nombre'],
            telefono=row['telefono'],
            email=row['email'],
            web=row['web'],
            dificultad=row['dificultad'],
            id_parque=row['id_parque']
        ))

        print("\n--- PROCESO FINALIZADO ---")

if __name__ == '__main__':
    cargar_csv()