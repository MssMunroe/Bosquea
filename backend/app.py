import os, xlsxwriter
import xml.etree.ElementTree as ET

from flask import Flask, jsonify, request, send_file, abort, render_template, session, redirect, url_for
from flask_cors import CORS
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime
from io import BytesIO
from sqlalchemy import func

from models import db, Usuario, ParqueNatural, Ruta, Incidencia, Visitado, Deseado, Rol, Comentario, Avistamiento, AnimalDestacado

app = Flask(__name__)
CORS(app)

basedir = os.path.abspath(os.path.dirname(__file__))
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, '../database', 'bosquea.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db.init_app(app)

# Verificar que sean ADMINS

def verificar_admin(rol_id):
    if str(rol_id) != '1':
        abort(403)

# --- 1. USUARIOS & AUTH ---

@app.route('/api/auth/register', methods=['POST'])
def register():
    data = request.get_json()
    pass_cifrada = generate_password_hash(data['contra'], method='pbkdf2:sha256')
    if Usuario.query.filter_by(email=data['email']).first():
        return jsonify({"error": "El usuario ya existe"}), 400
    
    nuevo_usuario = Usuario(
        nickname=data['nickname'],
        nombre=data['nombre'],
        email=data['email'],
        contra=pass_cifrada,
        dni=data['dni'],
        codigo_postal=data.get('codigo_postal'),
        rol_id=data.get('rol_id', 2)
    )
    db.session.add(nuevo_usuario)
    db.session.commit()
    return jsonify({"mensaje": "Usuario registrado con éxito"}), 201

@app.route('/api/auth/login', methods=['POST'])
def login():
    data = request.get_json()
    usuario = Usuario.query.filter_by(email=data['email']).first()

    if usuario and check_password_hash(usuario.contra, data['contra']):
        return jsonify({
            "mensaje": "Login correcto",
            "usuario": {
                "nickname": usuario.nickname,
                "rol_id": usuario.rol_id,
                "email": usuario.email
            }
        }), 200
        
    return jsonify({"error": "Email o contraseña incorrectos"}), 401

@app.route('/api/users/<int:id>/profile', methods=['GET'])
def get_user_profile(id):
    user = Usuario.query.get_or_404(id)
    visitados_count = Visitado.query.filter_by(id_usuario=id).count()
    deseados_count = Deseado.query.filter_by(id_usuario=id).count()
    
    return jsonify({
        "nickname": user.nickname,
        "nombre": user.nombre,
        "email": user.email,
        "estadisticas": {
            "parques_visitados": visitados_count,
            "lista_deseos": deseados_count
        }
    })

# --- 2. PARQUES & ANIMALES ---

@app.route('/api/parques', methods=['GET'])
def get_parques():
    parques = ParqueNatural.query.all()
    return jsonify([{
        "id": p.id_parque,
        "nombre": p.nombre,
        "ubicacion": p.ubicacion,
        "tamanio": p.tamanio,
    } for p in parques])

@app.route('/api/parques/<int:id>', methods=['GET'])
def get_park_detail(id):
    p = ParqueNatural.query.get_or_404(id)
    # Incluimos animales destacados del parque
    animales = AnimalDestacado.query.filter_by(id_parque=id).all()
    return jsonify({
        "id": p.id_parque,
        "nombre": p.nombre,
        "descripcion": p.descripcion,
        "ubicacion": p.ubicacion,
        "img": p.img,
        "animales": [{"id": a.id_animal, "nombre": a.nombre} for a in animales]
    })

# CRUP solo para los ADMIN
@app.route('/api/admin/parques', methods=['POST'])
def admin_create_park():
    data = request.get_json()
    verificar_admin(data.get('rol_id'))
    
    nuevo_p = ParqueNatural(
        nombre=data['nombre'],
        ubicacion=data['ubicacion'],
        tamanio=data['tamanio'],
        descripcion=data.get('descripcion')
    )
    db.session.add(nuevo_p)
    db.session.commit()
    return jsonify({"mensaje": "Parque creado correctamente"}), 201

@app.route('/api/admin/parques/<int:id>', methods=['PUT'])
def admin_edit_park(id):
    data = request.get_json()
    verificar_admin(data.get('rol_id'))
    
    p = ParqueNatural.query.get_or_404(id)
    p.nombre = data.get('nombre', p.nombre)
    p.ubicacion = data.get('ubicacion', p.ubicacion)
    p.tamanio = data.get('tamanio', p.tamanio)
    
    db.session.commit()
    return jsonify({"mensaje": "Parque actualizado"}), 200

@app.route('/api/admin/parques/<int:id>', methods=['DELETE'])
def admin_delete_park(id):
    rol_id = request.args.get('rol_id') 
    verificar_admin(rol_id)
    
    p = ParqueNatural.query.get_or_404(id)
    db.session.delete(p)
    db.session.commit()
    return jsonify({"mensaje": "Parque eliminado físicamente"}), 200

@app.route('/admin/dashboard')
def admin_dashboard():

    #Verificamos que sea admin el user
    if not session.get('user_rol') == 1:
        return "Acceso denegado. Solo para administradores.", 403
    
    # Consultamos los datos que queremos mostrar
    parques = ParqueNatural.query.all()
    incidencias = Incidencia.query.all()
    total_usuarios = Usuario.query.count()
    
    # Enviamos los datos a la plantilla HTML
    return render_template('admin.html', 
                           parques=parques, 
                           incidencias=incidencias, 
                           total_usuarios=total_usuarios)

# --- 3. RUTAS ---

@app.route('/api/parques/<int:id>/routes', methods=['GET'])
def get_park_routes(id):
    rutas = Ruta.query.filter_by(id_parque=id).all()
    return jsonify([{
        "id": r.id_ruta,
        "nombre": r.nombre,
        "dificultad": r.dificultad,
        "web": r.web
    } for r in rutas])

@app.route('/api/routes', methods=['GET'])
def get_all_routes():
    try:
        # Importante: Asegúrate de que 'Ruta' y 'ParqueNatural' estén importados
        rutas = Ruta.query.all()
        resultado = []
        
        for r in rutas:
            # Buscamos el parque asociado
            parque = ParqueNatural.query.get(r.id_parque)
            
            resultado.append({
                "id": r.id_ruta,
                "nombre": r.nombre,
                "dificultad": r.dificultad,
                "web": r.web,
                "parque_nombre": parque.nombre if parque else "Parque no encontrado",
                "parque_ubicacion": parque.ubicacion if parque else "N/A"
            })
        
        return jsonify(resultado)
    
    except Exception as e:
        # Esto imprimirá el error real en tu terminal de VS Code
        print(f"Error en /api/routes: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/api/routes', methods=['POST'])
def create_route():
    data = request.get_json()
    # Aquí podrías validar si el id_usuario del que lo envía tiene rol_id=1 (Gestor)
    nueva_ruta = Ruta(
        nombre=data['nombre'],
        dificultad=data['dificultad'],
        web=data.get('web'),
        id_parque=data['id_parque']
    )
    db.session.add(nueva_ruta)
    db.session.commit()
    return jsonify({"mensaje": "Ruta creada exitosamente"}), 201

# --- 4. INTERACCIONES (COMENTARIOS, AVISTAMIENTOS E INCIDENCIAS) ---

@app.route('/api/comments', methods=['POST'])
def post_comment():
    data = request.get_json()
    nuevo_comentario = Comentario(
        contenido=data['contenido'],
        id_usuario=data['id_usuario'],
        id_parque=data['id_parque']
    )
    db.session.add(nuevo_comentario)
    db.session.commit()
    return jsonify({"mensaje": "Comentario publicado"}), 201

@app.route('/api/sightings', methods=['POST'])
def post_sighting():
    data = request.get_json()
    nuevo_avistamiento = Avistamiento(
        id_animal=data.get('id_animal'), # Opcional según tu diagrama
        descripcion=data['descripcion'],
        id_usuario=data['id_usuario'],
        id_parque=data['id_parque']
    )
    db.session.add(nuevo_avistamiento)
    db.session.commit()
    return jsonify({"mensaje": "Avistamiento registrado"}), 201

@app.route('/api/sightings', methods=['GET'])
def get_sightings():
    # En un caso real podrías filtrar por fecha (solo hoy)
    avistamientos = Avistamiento.query.all()
    return jsonify([{
        "id": a.id_avistamiento,
        "descripcion": a.descripcion,
        "parque": a.id_parque,
        "fecha": a.fecha.strftime("%Y-%m-%d %H:%M")
    } for a in avistamientos])

@app.route('/api/reports/incident', methods=['POST'])
def post_incident():
    data = request.get_json()
    nueva_incidencia = Incidencia(
        descripcion=data['descripcion'],
        id_usuario=data['id_usuario'],
        estado='Pendiente'
    )
    db.session.add(nueva_incidencia)
    db.session.commit()
    return jsonify({"mensaje": "Incidencia reportada"}), 201

# --- 5. FAVORITOS Y VISITADOS (TABLAS INTERMEDIAS) ---

@app.route('/api/favorites/add', methods=['POST'])
def add_favorite():
    data = request.get_json()
    # Evitar duplicados
    existe = Deseado.query.filter_by(id_usuario=data['id_usuario'], id_parque=data['id_parque']).first()
    if existe: return jsonify({"mensaje": "Ya está en favoritos"}), 200
    
    nuevo_fav = Deseado(id_usuario=data['id_usuario'], id_parque=data['id_parque'])
    db.session.add(nuevo_fav)
    db.session.commit()
    return jsonify({"mensaje": "Añadido a favoritos"}), 201

@app.route('/api/favorites/remove', methods=['DELETE'])
def remove_favorite():
    data = request.get_json()
    fav = Deseado.query.filter_by(
        id_usuario=data['id_usuario'], 
        id_parque=data['id_parque']
    ).first()
    
    if fav:
        db.session.delete(fav)
        db.session.commit()
        return jsonify({"mensaje": "Eliminado de favoritos"}), 200
    return jsonify({"error": "No encontrado"}), 404

@app.route('/api/visited/add', methods=['POST'])
def add_visited():
    data = request.get_json()
    nuevo_visitado = Visitado(id_usuario=data['id_usuario'], id_parque=data['id_parque'])
    db.session.add(nuevo_visitado)
    db.session.commit()
    return jsonify({"mensaje": "Marcado como visitado"}), 201

# --- 6. GESTIÓN (REPORTS) ---

@app.route('/api/reports/visits', methods=['GET'])
def get_report():
    visitados = Visitado.query.all()
    return jsonify({"total_visitas_registradas": len(visitados)})

@app.route('/api/admin/import-xml', methods=['POST'])
def import_xml():
    if 'file' not in request.files:
        return jsonify({"error": "No se ha enviado ningún archivo"}), 400
    
    archivo_xml = request.files['file']
    try:
        tree = ET.parse(archivo_xml)
        root = tree.getroot()

        # Ejemplo 1: Importar Rutas desde el XML
        rutas_creadas = 0
        for ruta_tag in root.findall('.//ruta'):
            nueva_ruta = Ruta(
                nombre=ruta_tag.find('nombre').text,
                dificultad=ruta_tag.find('dificultad').text,
                web=ruta_tag.find('web').text,
                id_parque=int(ruta_tag.get('id_parque'))
            )
            db.session.add(nueva_ruta)
            rutas_creadas += 1
        
        db.session.commit()
        return jsonify({"mensaje": f"XML procesado. {rutas_creadas} rutas añadidas."}), 200

    except Exception as e:
        return jsonify({"error": f"Error al leer XML: {str(e)}"}), 500
    
@app.route('/api/admin/export-visitados', methods=['GET'])
def export_xml_visitados():
    visitados = Visitado.query.all()
    
    # Creamos la estructura XML
    root = ET.Element("ReporteVisitados")
    for v in visitados:
        nodo = ET.SubElement(root, "registro")
        ET.SubElement(nodo, "usuario_id").text = str(v.id_usuario)
        ET.SubElement(nodo, "id_parque").text = str(v.id_parque)
        ET.SubElement(nodo, "fecha").text = v.fecha_visita.strftime("%Y-%m-%d")
    
    # Lo convertimos a string y lo enviamos como archivo
    xml_data = ET.tostring(root, encoding='utf-8')
    
    with open("reporte.xml", "wb") as f:
        f.write(xml_data)
        
    return send_file("reporte.xml", as_attachment=True)


@app.route('/api/admin/report-chart', methods=['GET'])
def export_excel_chart():
    # 1. Creamos un archivo en memoria
    output = BytesIO()
    workbook = xlsxwriter.Workbook(output)
    worksheet = workbook.add_worksheet("Estadísticas")
    
    # 2. Consultamos datos: Contamos visitas agrupadas por mes
    # (Esto es un ejemplo, asumiendo que tienes fechas en 'Visitado')
    stats = db.session.query(
        func.strftime('%m', Visitado.fecha_visita).label('mes'),
        func.count(Visitado.id_usuario).label('total')
    ).group_by('mes').all()

    # 3. Escribimos los datos en el Excel
    worksheet.write('A1', 'Mes')
    worksheet.write('B1', 'Visitantes')
    
    meses_nombres = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"]
    
    row = 1
    for s in stats:
        nombre_mes = meses_nombres[int(s.mes) - 1]
        worksheet.write(row, 0, nombre_mes)
        worksheet.write(row, 1, s.total)
        row += 1

    # 4. CREAMOS LA GRÁFICA
    chart = workbook.add_chart({'type': 'column'}) # Gráfico de columnas
    chart.add_series({
        'name':       'Visitantes por Mes',
        'categories': ['Estadísticas', 1, 0, row - 1, 0],
        'values':     ['Estadísticas', 1, 1, row - 1, 1],
    })
    
    chart.set_title({'name': 'Afluencia Mensual de Parques'})
    chart.set_x_axis({'name': 'Meses'})
    chart.set_y_axis({'name': 'Número de Personas'})

    # Insertamos la gráfica en la hoja
    worksheet.insert_chart('D2', chart)

    workbook.close()
    output.seek(0)

    return send_file(
        output,
        as_attachment=True,
        download_name="Reporte_Mensual_Bosquea.xlsx",
        mimetype="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

# --- BÚSQUEDA GLOBAL ---

@app.route('/api/search', methods=['GET'])
def search():
    query = request.args.get('query', '')
    # Buscamos coincidencias parciales en nombre o ubicación
    resultados = ParqueNatural.query.filter(
        (ParqueNatural.nombre.like(f'%{query}%')) | 
        (ParqueNatural.ubicacion.like(f'%{query}%'))
    ).all()
    
    return jsonify([{
        "id": p.id_parque,
        "nombre": p.nombre,
        "ubicacion": p.ubicacion
    } for p in resultados])


# --- MAIN ---

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port)

