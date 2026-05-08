import os
from flask import Flask, jsonify
from flask_cors import CORS
from models import db


app = Flask(__name__)
CORS(app) # Fundamental para que Kotlin se conecte luego

# Configuro donde quiero que se guarde la BBDD
basedir = os.path.abspath(os.path.dirname(__file__))

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, '../database', 'bosquea.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Inicializamos la BBDD
db.init_app(app)

@app.route('/API')
def index():
    return jsonify({
        "status": "online",
        "proyecto": "Bosquea API",
        "version": "1.0.0",
        "mensaje": "Servidor configurado correctamente"
    })

if __name__ == '__main__':
    # Creamos las tablas, si no existen
    with app.app_context():
        db.create_all()
        print("Base de datos y tablas creadas con éxito.")
    
    app.run(debug=True)