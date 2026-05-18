import os
import csv
from datetime import datetime
from app import app
from models import db, ParqueNatural, Ruta, Usuario, Rol, AnimalDestacado, Comentario, Avistamiento, Incidencia, Visitado, Deseado
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
            print("- Roles iniciales creados.")

        # FUNCIÓN AUXILIAR PARA CARGAR TABLAS DESDE CSV
        def importar_tabla(nombre_archivo, modelo, mapeo_func):
            ruta_archivo = os.path.join(database_dir, nombre_archivo)
            if not os.path.exists(ruta_archivo):
                print(f"! Aviso: No se encuentra {nombre_archivo}, saltando...")
                return

            with open(ruta_archivo, encoding='utf-8') as f:
                reader = csv.DictReader(f, delimiter='|', skipinitialspace=True)
                contador = 0
                for row in reader:
                    id_campo = list(row.keys())[0]
                    # Solo insertamos si no existe el ID
                    if not modelo.query.get(row[id_campo]):
                        objeto = mapeo_func(row)
                        db.session.add(objeto)
                        contador += 1
                db.session.commit()
                print(f"- {modelo.__name__}: {contador} registros cargados desde CSV.")

        # CARGA DE TABLAS PRINCIPALES
        
        importar_tabla('parques.csv', ParqueNatural, lambda row: ParqueNatural(
            id_parque=row['id_parque'], nombre=row['nombre'], descripcion=row['descripcion'],
            ubicacion=row['ubicacion'], tamanio=row['tamanio'], img=row['img'],
            lat=row['lat'], lon=row['lon']
        ))

        importar_tabla('usuarios.csv', Usuario, lambda row: Usuario(
            id_usuario=row['id_usuario'], nickname=row['nickname'], nombre=row['nombre'],
            email=row['email'], dni=row['dni'], codigo_postal=row['codigo_postal'],
            icono=row['icono'], contra=generate_password_hash(row['contra'], method='pbkdf2:sha256'),
            rol_id=row['rol_id']
        ))

        importar_tabla('rutas.csv', Ruta, lambda row: Ruta(
            id_ruta=row['id_ruta'], nombre=row['nombre'], web=row['web'],
            dificultad=row['dificultad'], id_parque=row['id_parque']
        ))

        # CARGA DE DATOS
        
        # Animales Destacados
        if not AnimalDestacado.query.first():
            animales = [
                AnimalDestacado(id_animal=1, nombre="Lince Ibérico", id_parque=1),
                AnimalDestacado(id_animal=2, nombre="Águila Imperial", id_parque=1),
                AnimalDestacado(id_animal=3, nombre="Quebrantahuesos", id_parque=2),
                AnimalDestacado(id_animal=4, nombre="Oso Pardo", id_parque=3),
                AnimalDestacado(id_animal=5, nombre="Buitre Leonado", id_parque=4),
                AnimalDestacado(id_animal=6, nombre="Cabra Montés", id_parque=5),
                AnimalDestacado(id_animal=7, nombre="Ciervo Volante", id_parque=6),
                AnimalDestacado(id_animal=8, nombre="Flamenco Rosa", id_parque=1),
                AnimalDestacado(id_animal=9, nombre="Lobo Ibérico", id_parque=3),
                AnimalDestacado(id_animal=10, nombre="Cigüeña Negra", id_parque=4)
            ]
            db.session.add_all(animales)
            db.session.commit()
            print("- Animales destacados cargados.")

        # Comentarios
        if not Comentario.query.first():
            comentarios = [
                Comentario(contenido="Increíbles vistas, la ruta es dura pero merece la pena.", id_usuario=1, id_parque=1),
                Comentario(contenido="Muy bien señalizado todo, ideal para ir con niños.", id_usuario=2, id_parque=2),
                Comentario(contenido="Ojo con el parking, se llena muy rápido.", id_usuario=3, id_parque=3),
                Comentario(contenido="¿Alguien sabe si aceptan perros en el área de picnic?", id_usuario=4, id_parque=1),
                Comentario(contenido="Pura naturaleza, desconexión total garantizada.", id_usuario=5, id_parque=3)
            ]
            db.session.add_all(comentarios)
            print("- Comentarios de ejemplo cargados.")

        # Avistamientos
        if not Avistamiento.query.first():
            avistamientos = [
                Avistamiento(descripcion="Visto cerca del arroyo al amanecer.", id_usuario=1, id_parque=1, id_animal=1),
                Avistamiento(descripcion="Sobrevolando los riscos del norte.", id_usuario=2, id_parque=2, id_animal=3),
                Avistamiento(descripcion="Huellas frescas detectadas en el sendero.", id_usuario=3, id_parque=3, id_animal=4)
            ]
            db.session.add_all(avistamientos)
            print("- Avistamientos de ejemplo cargados.")

        # Incidencias
        if not Incidencia.query.first():
            incidencias = [
                Incidencia(descripcion="Árbol caído bloqueando el sendero azul.", id_usuario=1, estado="Resuelta"),
                Incidencia(descripcion="Falta señalización en el desvío del km 4.", id_usuario=2, estado="Pendiente"),
                Incidencia(descripcion="Fuente principal sin agua.", id_usuario=3, estado="En proceso")
            ]
            db.session.add_all(incidencias)
            print("- Incidencias de ejemplo cargadas.")

        # Interacciones (Visitados y Deseados)
        if not Visitado.query.first():
            db.session.add_all([
                Visitado(id_usuario=1, id_parque=1), Visitado(id_usuario=2, id_parque=2),
                Visitado(id_usuario=3, id_parque=3), Visitado(id_usuario=4, id_parque=4)
            ])
            print("- Registro de parques visitados cargado.")

        if not Deseado.query.first():
            db.session.add_all([
                Deseado(id_usuario=1, id_parque=2), Deseado(id_usuario=2, id_parque=15),
                Deseado(id_usuario=3, id_parque=1), Deseado(id_usuario=5, id_parque=3)
            ])
            print("- Lista de deseos cargada.")

        db.session.commit()
        print("\n--- PROCESO DE CARGA FINALIZADO ---")

if __name__ == '__main__':
    cargar_csv()