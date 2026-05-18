document.addEventListener("DOMContentLoaded", () => {
    fetchRutas();
});

async function fetchRutas() {
    const contenedor = document.getElementById("contenedor-rutas");

    try {
        const response = await fetch("http://127.0.0.1:5000/api/routes");
        const rutas = await response.json();

        contenedor.innerHTML = ""; // Limpiamos el "Cargando..."

        if (rutas.length === 0) {
            contenedor.innerHTML = "<p class='no-routes'>No hay rutas disponibles en este momento.</p>";
            return;
        }

        rutas.forEach(ruta => {
            const article = document.createElement("article");
            article.className = "route-card-item";

            // Comprobamos si existen datos opcionales
            const tel = ruta.telefono || "No disponible";
            const mail = ruta.email || "No disponible";
            const webUrl = ruta.web || "#";

            article.innerHTML = `
                <h2 class="route-card-title">${ruta.nombre}</h2>
                
                <div class="route-card-grid">
                    <div class="grid-cell">
                        <span class="cell-label">Teléfono:</span>
                        <span class="cell-value">${tel}</span>
                    </div>
                    <div class="grid-cell">
                        <span class="cell-label">Email:</span>
                        <span class="cell-value">${mail}</span>
                    </div>
                    <div class="grid-cell">
                        <span class="cell-label">Web:</span>
                        <span class="cell-value">
                            <a href="${webUrl}" target="_blank" class="route-link">${webUrl}</a>
                        </span>
                    </div>
                    
                    <div class="grid-cell">
                        <span class="cell-label">Dificultad:</span>
                        <span class="cell-value">${ruta.dificultad}</span>
                    </div>
                    <div class="grid-cell span-two-columns">
                        <span class="cell-label">Parque Natural:</span>
                        <span class="cell-value">${ruta.parque_nombre}, ${ruta.parque_ubicacion}</span>
                    </div>
                </div>
            `;
            contenedor.appendChild(article);
        });

    } catch (error) {
        console.error("Error al cargar rutas:", error);
        contenedor.innerHTML = "<p class='error-msg'>Error al conectar con el servidor.</p>";
    }
}