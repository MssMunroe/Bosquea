import os
from PIL import Image
from werkzeug.utils import secure_filename

def procesar_avatar(file, nickname, upload_folder):
    # Abrimos la imagen
    img = Image.open(file)
    
    # Convertimos a RGB
    if img.mode in ("RGBA", "P"):
        img = img.convert("RGB")
    
    # 1. Hacer la imagen cuadrada (Crop centrado)
    ancho, alto = img.size
    nuevo_lado = min(ancho, alto)
    
    izq = (ancho - nuevo_lado) / 2
    top = (alto - nuevo_lado) / 2
    der = (ancho + nuevo_lado) / 2
    inf = (alto + nuevo_lado) / 2
    
    img = img.crop((izq, top, der, inf))
    
    # 2. Redimensionar
    img = img.resize((400, 400), Image.LANCZOS)
    
    # 3. Guardar
    filename = secure_filename(f"avatar_{nickname}.jpg")
    filepath = os.path.join(upload_folder, filename)
    
    img.save(filepath, "JPEG", quality=85, optimize=True)
    
    return filename