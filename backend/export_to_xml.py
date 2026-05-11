import os
import xml.etree.ElementTree as ET
from xml.dom import minidom
from backend.app import app
from backend.models import db, ParqueNatural, Ruta

def generar_xml_real():
    with app.app_context():
        # 1. Obtener datos de la BBDD
        parques = ParqueNatural.query.all()
        
        # 2. Crear el elemento raíz
        root = ET.Element('bosquea_export')
        root.set('fecha_generacion', str(os.popen('date').read().strip()))

        # 3. Bloque de Parques y sus Rutas
        parques_node = ET.SubElement(root, 'parques_nacionales')
        
        for p in parques:
            parque_node = ET.SubElement(parques_node, 'parque')
            parque_node.set('id', str(p.id_parque))
            
            ET.SubElement(parque_node, 'nombre').text = p.nombre
            ET.SubElement(parque_node, 'ubicacion').text = p.ubicacion
            ET.SubElement(parque_node, 'hectareas').text = str(p.tamanio)
            
            # Buscamos las rutas asociadas a este parque en la BBDD
            rutas_node = ET.SubElement(parque_node, 'rutas_disponibles')
            rutas = Ruta.query.filter_by(parque_id=p.id_parque).all()
            
            for r in rutas:
                ruta_item = ET.SubElement(rutas_node, 'ruta')
                ET.SubElement(ruta_item, 'nombre_ruta').text = r.nombre
                ET.SubElement(ruta_item, 'dificultad').text = r.dificultad
                ET.SubElement(ruta_item, 'web_oficial').text = r.web if r.web else "N/A"

        # 4. Formatear el XML para que sea legible (indented)
        xml_string = ET.tostring(root, encoding='utf-8')
        reparsed = minidom.parseString(xml_string)
        pretty_xml = reparsed.toprettyxml(indent="  ")

        # 5. Guardar en la carpeta database
        backend_dir = os.path.abspath(os.path.dirname(__file__))
        ruta_salida = os.path.join(backend_dir, '../database', 'informe_parques.xml')
        
        with open(ruta_salida, "w", encoding="utf-8") as f:
            f.write(pretty_xml)
            
        print(f"¡Éxito! Archivo generado en: {ruta_salida}")

if __name__ == '__main__':
    generar_xml_real()