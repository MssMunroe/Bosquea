document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const parqueNombre = params.get("nombre");

    if (parqueNombre) {
        fetchParqueDetalle(parqueNombre);
    } else {
        document.getElementById("parque-detail").innerHTML = "<p>Parque no encontrado en la URL.</p>";
    }
});

async function fetchParqueDetalle(nombreParque) {
    const contenedor = document.getElementById("parque-detail");

    try {
        // 2. Llamada al endpoint específico para UN parque
        const response = await fetch(`http://127.0.0.1:5000/api/parques/${encodeURIComponent(nombreParque)}`);

        if (!response.ok) throw new Error("Parque no encontrado");

        const parque = await response.json();

        // 3. Renderizar todos los datos que mencionas
        contenedor.innerHTML = `
            <div class="park-header">
                <img src="${parque.img}" alt="${parque.nombre}" class="detail-img">
                <h1 class="detail-title">${parque.nombre}</h1>
            </div>
            
            <div class="park-info-grid">
                <div class="info-item">
                    <span class="label"><i class="fas fa-map-marker-alt"></i> Ubicación:</span>
                    <span class="value">${parque.ubicacion}</span>
                </div>
                <div class="info-item">
                    <span class="label"><i class="fas fa-expand-arrows-alt"></i> Tamaño:</span>
                    <span class="value">${parque.tamanio} hectáreas</span>
                </div>
            </div>

            <div class="park-description">
                <h2>Sobre el Parque</h2>
                <p>${parque.descripcion}</p>
            </div>
        `;

    } catch (error) {
        console.error("Error al cargar el detalle:", error);
        contenedor.innerHTML = "<p>Hubo un error al cargar la información del parque.</p>";
    }
}