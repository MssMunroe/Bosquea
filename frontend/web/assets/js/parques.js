async function inicializarMapa() {
    // Crear mapa
    const map = L.map('map').setView([40.4637, -3.7492], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);

    // LEYENDA
    const legend = L.control({ position: 'topright' });
    legend.onAdd = function () {
        const div = L.DomUtil.create('div', 'map-legend-container');
        div.innerHTML = `<img src="https://raw.githubusercontent.com/MssMunroe/Bosquea/refs/heads/main/frontend/web/assets/img/leyenda.png" alt="Leyenda" class="map-legend-img">`;
        return div;
    };
    legend.addTo(map);

    // CARGAR MARCADORES
    try {
        const res = await fetch('https://bosquea-backend.onrender.com/api/parques');
        const parques = await res.json();

        parques.forEach((p, i) => {
            // Convertimos el texto de la DB a número decimal
            const latitud = parseFloat(p.lat);
            const longitud = parseFloat(p.lon);

            if (isNaN(latitud) || isNaN(longitud)) {
                console.warn(`Coordenadas inválidas para: ${p.nombre}`);
                return;
            }

            const customIcon = L.divIcon({
                className: 'bosquea-marker',
                html: `<div class="pin-shape"></div><span class="pin-number">${i + 1}</span>`,
                iconSize: [32, 32],
                iconAnchor: [16, 32],
                popupAnchor: [0, -32]
            });

            L.marker([latitud, longitud], { icon: customIcon })
                .addTo(map)
                .bindPopup(`
                    <b>${p.nombre}</b>
                    <a href="parque-detalle.html?nombre=${encodeURIComponent(p.nombre)}" class="popup-link">
                        <i class="fa-solid fa-arrow-right-to-bracket" style="margin-right: 4px;"></i> Ver Parque
                    </a>
                `);
        });
    } catch (err) { console.error("Error en mapa:", err); }

    // Forzar al mapa a recalcular su tamaño
    setTimeout(() => { map.invalidateSize(); }, 500);
}

function configurarTarjetasClicables() {
    document.querySelectorAll('.park-card').forEach(card => {
        card.addEventListener('click', (e) => {
            if (e.target.closest('a') || e.target.closest('button')) return;
            const nombre = card.getAttribute('data-nombre');
            if (nombre) {
                const prefix = window.location.pathname.includes('pages/') ? '' : 'pages/';
                window.location.href = `${prefix}parque-detalle.html?nombre=${encodeURIComponent(nombre)}`;
            }
        });
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const contenedor = document.getElementById('contenedor-parques');
    if (contenedor) {
        fetchParquesCards();
    }
});

async function fetchParquesCards() {
    const contenedor = document.getElementById('contenedor-parques');
    try {
        const res = await fetch('https://bosquea-backend.onrender.com/api/parques');
        const parques = await res.json();

        contenedor.innerHTML = ""; // Limpiar cargando

        parques.forEach(p => {
            const card = document.createElement('div');
            card.className = 'park-card';
            card.setAttribute('data-nombre', p.nombre);
            card.innerHTML = `
                <div class="parque-card-header">
                    <i class="fa-solid fa-tree"></i> Parque Natural
                </div>
                <img src="${p.img}" alt="${p.nombre}">
                <div class="park-card-info">
                    <h3>${p.nombre}</h3>
                    <p class="ubicacion">${p.ubicacion}</p> 
                    <button class="btn-visit">Visitar</button>
                </div>
            `;
            contenedor.appendChild(card);
        });

        configurarTarjetasClicables();
    } catch (err) {
        console.error("Error cargando tarjetas:", err);
        contenedor.innerHTML = "<p>Error al cargar los parques.</p>";
    }
}