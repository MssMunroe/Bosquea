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
            contenedor.innerHTML = "<p>No hay rutas disponibles en este momento.</p>";
            return;
        }

        rutas.forEach(ruta => {
            const article = document.createElement("article");
            article.className = "route-item";

            article.innerHTML = `
                <h2 class="route-title">${ruta.nombre}</h2>
                <div class="route-grid">
                    <div class="route-data">
                        <p class="label">Web Oficial:</p>
                        <p class="value"><a href="${ruta.web}" target="_blank" class="link">${ruta.web}</a></p>
                    </div>
                    <div class="route-data spacer-top">
                        <p class="label">Dificultad:</p>
                        <p class="value">${ruta.dificultad}</p>
                    </div>
                    <div class="route-data spacer-top full-width">
                        <p class="label">Parque Natural:</p>
                        <p class="value">${ruta.parque_nombre} - ${ruta.parque_ubicacion}</p>
                    </div>
                </div>
            `;
            contenedor.appendChild(article);
        });

    } catch (error) {
        console.error("Error al cargar rutas:", error);
        contenedor.innerHTML = "<p>Error al conectar con el servidor.</p>";
    }
}