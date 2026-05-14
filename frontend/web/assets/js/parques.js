document.addEventListener("DOMContentLoaded", () => {
    fetchParques();
});

async function fetchParques() {
    const contenedor = document.getElementById("contenedor-parques");

    try {
        // Llamada a tu API de Flask
        const response = await fetch("http://127.0.0.1:5000/api/parques");
        const parques = await response.json();

        contenedor.innerHTML = ""; // Limpiar el mensaje de carga

        parques.forEach(parque => {
            const article = document.createElement("article");
            article.className = "park-card";
            // Guardamos el nombre para la función de clic
            article.setAttribute("data-nombre", parque.nombre);

            // Construimos la estructura idéntica a la que tenías
            article.innerHTML = `
                <div class="card-header">
                    <img src="assets/img/logo.png" alt="Icono" class="card-icon">
                    <span>Parque Natural</span>
                </div>
                <img src="${parque.img}" alt="${parque.nombre}" class="park-img">
                <div class="card-body">
                    <h3>${parque.nombre}</h3>
                    <p class="subtitle">${parque.ubicacion}</p>
                    <p class="excerpt">${parque.descripcion.substring(0, 100)}...</p>
                    <a href="pages/parque-detalle.html?nombre=${encodeURIComponent(parque.nombre)}" class="btn-visit">Visitar</a>
                </div>
            `;
            contenedor.appendChild(article);
        });

        // Una vez creadas las tarjetas, activamos la función de clic que definimos antes
        if (typeof configurarTarjetasClicables === 'function') {
            configurarTarjetasClicables();
        }

    } catch (error) {
        console.error("Error al cargar parques:", error);
        contenedor.innerHTML = "<p>Error al conectar con el servidor de Bosquea.</p>";
    }
}