from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app) # Fundamental para que tu app Kotlin se conecte luego

@app.route('/API')
def index():
    return jsonify({
        "status": "online",
        "proyecto": "Bosquea API",
        "version": "1.0.0",
        "mensaje": "Servidor configurado correctamente"
    })

if __name__ == '__main__':
    app.run(debug=True)