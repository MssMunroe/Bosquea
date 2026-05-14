from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

# ROLES
class Rol(db.Model):
    __tablename__ = 'roles'
    id_rol = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(50), nullable=False)

    # Relación con usuarios
    usuarios = db.relationship('Usuario', backref='rol_info', lazy=True)

# USUARIOS
class Usuario(db.Model):
    __tablename__ = 'usuarios'
    id_usuario = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(100), nullable=False)
    nickname = db.Column(db.String(50), unique=True, nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    contra = db.Column(db.String(255), nullable=False)
    telefono = db.Column(db.String(20))
    dni = db.Column(db.String(20), unique=True, nullable=False)
    codigo_postal = db.Column(db.String(10))
    fecha_registro = db.Column(db.DateTime, default=datetime.utcnow)
    icono = db.Column(db.String(255), default='default.png')

    rol_id = db.Column(db.Integer, db.ForeignKey('roles.id_rol'), nullable=False)

# PARQUES NATURALES
class ParqueNatural(db.Model):
    __tablename__ = 'parques_naturales'
    id_parque = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(150), nullable=False)
    descripcion = db.Column(db.Text)
    ubicacion = db.Column(db.String(200), nullable=False)
    tamanio = db.Column(db.String(50))
    img = db.Column(db.String(255))
    lat = db.Column(db.String(20))
    lon = db.Column(db.String(20))


# RUTAS
class Ruta(db.Model):
    __tablename__ = 'rutas'
    id_ruta = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(150), nullable=False)
    dificultad = db.Column(db.String(50))
    telefono = db.Column(db.String(50))
    email = db.Column(db.String(150))
    web = db.Column(db.String(200))

    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), nullable=False)

# COMENTARIOS
class Comentario(db.Model):
    __tablename__ = 'comentarios'
    id_comentario = db.Column(db.Integer, primary_key=True)
    contenido = db.Column(db.Text, nullable=False)
    fecha = db.Column(db.DateTime, default=datetime.utcnow)

    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), nullable=False)
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), nullable=False)

# ANIMALES DESTACADOS
class AnimalDestacado(db.Model):
    __tablename__ = 'animales_destacados'
    id_animal = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(100), nullable=False)

    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), nullable=False)

# AVISTAMIENTOS
class Avistamiento(db.Model):
    __tablename__ = 'avistamientos'
    id_avistamiento = db.Column(db.Integer, primary_key=True)
    descripcion = db.Column(db.Text)
    fecha = db.Column(db.DateTime, default=datetime.utcnow)

    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), nullable=False)
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), nullable=False)
    id_animal = db.Column(db.Integer, db.ForeignKey('animales_destacados.id_animal'), nullable=False)


# INCIDENCIAS
class Incidencia(db.Model):
    __tablename__ = 'incidencias'
    id_incidencia = db.Column(db.Integer, primary_key=True)
    descripcion = db.Column(db.Text, nullable=False)
    fecha = db.Column(db.DateTime, default=datetime.utcnow)
    estado = db.Column(db.String(50), default='Pendiente')

    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), nullable=False)

# VISITADOS (Tabla intermedia)
class Visitado(db.Model):
    __tablename__ = 'visitados'
    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), primary_key=True)
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), primary_key=True)
    fecha_visita = db.Column(db.DateTime, default=datetime.utcnow)

# DESEADOS (Tabla intermedia)
class Deseado(db.Model):
    __tablename__ = 'deseados'
    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), primary_key=True)
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), primary_key=True)