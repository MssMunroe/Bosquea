from email.utils import unquote
import os, xlsxwriter
import xml.etree.ElementTree as ET
from flask import Flask, jsonify, request, send_file, abort, session
from flask_cors import CORS
from werkzeug.security import generate_password_hash, check_password_hash
from io import BytesIO
from sqlalchemy import func, or_
from datetime import datetime

# Importaciones locales
from utils import procesar_avatar
from models import db, Usuario, ParqueNatural, Ruta, Incidencia, Visitado, Deseado, Rol, Comentario, Avistamiento, AnimalDestacado

app = Flask(__name__)
CORS(app)

# --- CONFIGURACIÓN ---
basedir = os.path.abspath(os.path.dirname(__file__))
UPLOAD_FOLDER = os.path.join(basedir, '..', 'frontend', 'web', 'assets', 'uploads')

if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, '../database', 'bosquea.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['MAX_CONTENT_LENGTH'] = 2 * 1024 * 1024 

db.init_app(app)

# --- MIDDLEWARE / SEGURIDAD ---
def verificar_admin(rol_id):
    if str(rol_id) != '1':
        abort(403)

@app.errorhandler(413)
def request_entity_too_large(error):
    return jsonify({"error": "El archivo excede el límite de 2MB"}), 413

# --- 1. AUTENTICACIÓN ---

@app.route('/api/auth/register', methods=['POST'])
def register():
    nombre = request.form.get('nombre')
    nickname = request.form.get('nickname')
    email = request.form.get('email')
    contra = request.form.get('contra')
    dni = request.form.get('dni')
    cp = request.form.get('codigo_postal')
    
    if Usuario.query.filter_by(email=email).first():
        return jsonify({"error": "El usuario ya existe"}), 400

    nombre_imagen = "default.png"
    if 'icono' in request.files:
        file = request.files['icono']
        if file.filename != '':
            try:
                nombre_imagen = procesar_avatar(file, nickname, UPLOAD_FOLDER)
            except Exception as e:
                print(f"Error al procesar: {e}")

    pass_cifrada = generate_password_hash(contra, method='pbkdf2:sha256')
    
    nuevo_usuario = Usuario(
        nickname=nickname, nombre=nombre, email=email, contra=pass_cifrada,
        dni=dni, codigo_postal=cp, icono=nombre_imagen, rol_id=2
    )
    
    db.session.add(nuevo_usuario)
    db.session.commit()
    return jsonify({"mensaje": "Usuario registrado con éxito"}), 201

@app.route('/api/auth/login', methods=['POST'])
def login():
    data = request.get_json()
    usuario = Usuario.query.filter(or_(Usuario.email == data['email'], Usuario.nickname == data['email'])).first()

    if usuario and check_password_hash(usuario.contra, data['contra']):
        return jsonify({
            "mensaje": "Login correcto",
            "usuario": {
                "id": usuario.id_usuario,
                "nickname": usuario.nickname,
                "rol_id": usuario.rol_id,
                "email": usuario.email
            }
        }), 200
    return jsonify({"error": "Email o contraseña incorrectos"}), 401

# --- 2. PERFIL Y USUARIOS ---

@app.route('/api/users/<int:user_id>/profile', methods=['GET'])
def get_user_profile(user_id):
    try:
        user = Usuario.query.get_or_404(user_id)
        deseados = db.session.query(ParqueNatural).join(Deseado).filter(Deseado.id_usuario == user_id).all()
        visitados = db.session.query(ParqueNatural).join(Visitado).filter(Visitado.id_usuario == user_id).all()

        return jsonify({
            "id": user.id_usuario,
            "nickname": user.nickname,
            "icono": user.icono,
            "estadisticas": {
                "lista_deseos": len(deseados),
                "parques_visitados": len(visitados)
            },
            "lista_deseados": [{"id": p.id_parque, "nombre": p.nombre, "img": p.img, "ubicacion": p.ubicacion} for p in deseados],
            "lista_visitados": [{"id": p.id_parque, "nombre": p.nombre, "img": p.img, "ubicacion": p.ubicacion} for p in visitados]
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/users/<int:user_id>/comments', methods=['GET'])
def get_user_comments(user_id):
    try:
        comentarios = db.session.query(Comentario, ParqueNatural.nombre)\
            .join(ParqueNatural, Comentario.id_parque == ParqueNatural.id_parque)\
            .filter(Comentario.id_usuario == user_id)\
            .order_by(Comentario.fecha.desc()).all()
        
        return jsonify([{
            "id": c.Comentario.id_comentario,
            "contenido": c.Comentario.contenido,
            "fecha": c.Comentario.fecha.strftime("%d/%m/%Y"),
            "parque_nombre": nombre_parque
        } for c, nombre_parque in comentarios])
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# --- 3. PARQUES Y BÚSQUEDA ---

@app.route('/api/parques', methods=['GET'])
def get_parques():
    parques = ParqueNatural.query.all()
    return jsonify([{
        "id": p.id_parque,
        "nombre": p.nombre,
        "descripcion": p.descripcion,
        "ubicacion": p.ubicacion,
        "img": p.img,
        "tamanio": p.tamanio,
        "lat": p.lat, 
        "lon": p.lon 
    } for p in parques])

@app.route('/api/parques/<string:nombre>', methods=['GET'])
def get_parque_por_nombre(nombre):
    nombre_decodificado = unquote(nombre)
    p = ParqueNatural.query.filter_by(nombre=nombre_decodificado).first_or_404()
    animales = AnimalDestacado.query.filter_by(id_parque=p.id_parque).all()
    
    user_id = request.args.get('user_id')
    es_favorito = bool(Deseado.query.filter_by(id_usuario=user_id, id_parque=p.id_parque).first()) if user_id else False

    return jsonify({
        "id": p.id_parque, "nombre": p.nombre, "descripcion": p.descripcion,
        "ubicacion": p.ubicacion, "tamanio": p.tamanio, "img": p.img,
        "lat": p.lat, "lon": p.lon, "es_favorito": es_favorito,
        "animales": [{"id": a.id_animal, "nombre": a.nombre} for a in animales]
    })

@app.route('/api/search', methods=['GET'])
def search():
    query = request.args.get('q', '').strip()
    if not query:
        return jsonify([])

    # Buscamos en Parques
    parques = ParqueNatural.query.filter(ParqueNatural.nombre.ilike(f'%{query}%')).all()
    
    # Buscamos en Rutas
    rutas = Ruta.query.filter(Ruta.nombre.ilike(f'%{query}%')).all()

    resultados = []

    # Formateamos parques
    for p in parques:
        resultados.append({
            "tipo": "parque",
            "nombre": p.nombre,
            "url": f"/pages/parque-detalle.html?nombre={p.nombre}"
        })

    # Formateamos rutas
    for r in rutas:
        resultados.append({
            "tipo": "ruta",
            "nombre": r.nombre,
            "url": "/pages/rutas.html" # O el enlace a la sección de rutas
        })

    return jsonify(resultados)

# --- 4. RUTAS ---

@app.route('/api/routes', methods=['GET'])
def get_all_routes():
    rutas = Ruta.query.all()
    resultado = []
    for r in rutas:
        parque = ParqueNatural.query.get(r.id_parque)
        resultado.append({
            "id": r.id_ruta, "nombre": r.nombre, "dificultad": r.dificultad,
            "web": r.web, "parque_nombre": parque.nombre if parque else "N/A"
        })
    return jsonify(resultado)

@app.route('/api/parques/<int:id>/routes', methods=['GET'])
def get_park_routes(id):
    rutas = Ruta.query.filter_by(id_parque=id).all()
    return jsonify([{"id": r.id_ruta, "nombre": r.nombre, "dificultad": r.dificultad} for r in rutas])

# --- 5. INTERACCIONES (COMENTARIOS, AVISTAMIENTOS, INCIDENCIAS) ---

@app.route('/api/parques/<int:id_parque>/comments', methods=['GET'])
def get_comments(id_parque):
    comentarios = db.session.query(Comentario, Usuario.nickname, Usuario.icono)\
        .join(Usuario, Comentario.id_usuario == Usuario.id_usuario)\
        .filter(Comentario.id_parque == id_parque).order_by(Comentario.fecha.desc()).all()
    
    return jsonify([{
        "id": c.id_comentario, "contenido": c.contenido, 
        "fecha": c.fecha.strftime("%d/%m/%Y %H:%M"), "autor": nick, "avatar": icono
    } for c, nick, icono in comentarios])

@app.route('/api/comments', methods=['POST'])
def post_comment():
    data = request.get_json()
    nuevo = Comentario(contenido=data['contenido'], id_usuario=data['id_usuario'], id_parque=data['id_parque'])
    db.session.add(nuevo)
    db.session.commit()
    return jsonify({"mensaje": "Comentario publicado"}), 201

@app.route('/api/comments/<int:comment_id>', methods=['DELETE'])
def delete_comment(comment_id):
    comentario = Comentario.query.get_or_404(comment_id)
    db.session.delete(comentario)
    db.session.commit()
    return jsonify({"mensaje": "Comentario eliminado"}), 200

@app.route('/api/favoritos/toggle', methods=['POST'])
def toggle_favorito():
    data = request.json
    u_id, p_id = data.get('id_usuario'), data.get('id_parque')
    fav = Deseado.query.filter_by(id_usuario=u_id, id_parque=p_id).first()

    if fav:
        db.session.delete(fav)
        db.session.commit()
        return jsonify({"mensaje": "Eliminado", "estado": False}), 200
    
    db.session.add(Deseado(id_usuario=u_id, id_parque=p_id))
    db.session.commit()
    return jsonify({"mensaje": "Añadido", "estado": True}), 201

@app.route('/api/reports/incident', methods=['POST'])
def post_incident():
    data = request.get_json()
    nueva = Incidencia(descripcion=data['descripcion'], id_usuario=data['id_usuario'], estado='Pendiente')
    db.session.add(nueva)
    db.session.commit()
    return jsonify({"mensaje": "Incidencia reportada"}), 201

# --- 6. ADMINISTRACIÓN (ADMINS ONLY) ---

@app.route('/api/admin/parques', methods=['POST'])
def admin_create_park():
    data = request.get_json()
    verificar_admin(data.get('rol_id'))
    nuevo_p = ParqueNatural(nombre=data['nombre'], ubicacion=data['ubicacion'], tamanio=data['tamanio'], descripcion=data.get('descripcion'))
    db.session.add(nuevo_p)
    db.session.commit()
    return jsonify({"mensaje": "Parque creado"}), 201

@app.route('/api/admin/report-chart', methods=['GET'])
def export_excel_chart():
    output = BytesIO()
    workbook = xlsxwriter.Workbook(output)
    worksheet = workbook.add_worksheet("Estadísticas")
    
    stats = db.session.query(
        func.strftime('%m', Visitado.fecha_visita).label('mes'),
        func.count(Visitado.id_usuario).label('total')
    ).group_by('mes').all()

    worksheet.write('A1', 'Mes'); worksheet.write('B1', 'Visitantes')
    meses_nombres = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
    
    row = 1
    for s in stats:
        worksheet.write(row, 0, meses_nombres[int(s.mes) - 1])
        worksheet.write(row, 1, s.total)
        row += 1

    chart = workbook.add_chart({'type': 'column'})
    chart.add_series({'categories': ['Estadísticas', 1, 0, row-1, 0], 'values': ['Estadísticas', 1, 1, row-1, 1]})
    worksheet.insert_chart('D2', chart)
    workbook.close()
    output.seek(0)

    return send_file(output, as_attachment=True, download_name="Reporte_Bosquea.xlsx", 
                     mimetype="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

@app.route('/api/admin/import-xml', methods=['POST'])
def import_xml():
    verificar_admin(request.form.get('rol_id'))
    if 'file' not in request.files: return jsonify({"error": "No file"}), 400
    
    try:
        tree = ET.parse(request.files['file'])
        root = tree.getroot()
        for ruta_tag in root.findall('.//ruta'):
            nueva = Ruta(nombre=ruta_tag.find('nombre').text, dificultad=ruta_tag.find('dificultad').text,
                         web=ruta_tag.find('web').text, id_parque=int(ruta_tag.get('id_parque')))
            db.session.add(nueva)
        db.session.commit()
        return jsonify({"mensaje": "XML importado correctamente"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(host='0.0.0.0', port=int(os.environ.get("PORT", 5000)))