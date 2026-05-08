from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

# 1. Tabla Roles
class Rol(db.Model):
    __tablename__ = 'roles'
    id_rol = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(50), nullable=False)
    # Relación con usuarios
    usuarios = db.relationship('Usuario', backref='rol_info', lazy=True)

# 2. Tabla Usuarios
class Usuario(db.Model):
    __tablename__ = 'usuarios'
    id_usuario = db.Column(db.Integer, primary_key=True)
    nickname = db.Column(db.String(50), unique=True, nullable=False)
    nombre = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    telefono = db.Column(db.String(20))
    dni = db.Column(db.String(20), unique=True, nullable=False)
    codigo_postal = db.Column(db.String(10))
    contra = db.Column(db.String(255), nullable=False)
    fecha_registro = db.Column(db.DateTime, default=datetime.utcnow)
    rol_id = db.Column(db.Integer, db.ForeignKey('roles.id_rol'), nullable=False)

# 3. Tabla Parques Naturales
class ParqueNatural(db.Model):
    __tablename__ = 'parques_naturales'
    id_parque = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(150), nullable=False)
    descripcion = db.Column(db.Text)
    ubicacion = db.Column(db.String(200), nullable=False)
    imagen_url = db.Column(db.String(255))
    tamanio = db.Column(db.String(50)) # Hectáreas

# 4. Tabla Rutas
class Ruta(db.Model):
    __tablename__ = 'rutas'
    id_ruta = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(150), nullable=False)
    dificultad = db.Column(db.String(50))
    telefono = db.Column(db.String(50))
    email = db.Column(db.String(150))
    web = db.Column(db.String(200))
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), nullable=False)

# 5. Tabla Incidencias
class Incidencia(db.Model):
    __tablename__ = 'incidencias'
    id_incidencia = db.Column(db.Integer, primary_key=True)
    descripcion = db.Column(db.Text, nullable=False)
    fecha = db.Column(db.DateTime, default=datetime.utcnow)
    estado = db.Column(db.String(50), default='Pendiente')
    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'))

# 6. Tablas Intermedias - Visitados y Deseados
class Visitado(db.Model):
    __tablename__ = 'visitados'
    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), primary_key=True)
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), primary_key=True)
    fecha_visita = db.Column(db.DateTime, default=datetime.utcnow)

class Deseado(db.Model):
    __tablename__ = 'deseados'
    id_usuario = db.Column(db.Integer, db.ForeignKey('usuarios.id_usuario'), primary_key=True)
    id_parque = db.Column(db.Integer, db.ForeignKey('parques_naturales.id_parque'), primary_key=True)