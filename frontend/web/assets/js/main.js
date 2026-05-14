/**
 * ============================================================
 * BOSQUEA - JAVASCRIPT PRINCIPAL
 * ============================================================
 */


/* 2. INICIALIZACIÓN PRINCIPAL (Cuando el DOM está listo) */
document.addEventListener("DOMContentLoaded", function () {

    // --- Carga de piezas reutilizables ---
    loadComponent('.main-header', '/components/header.html', marcarPaginaActiva);
    loadComponent('.hero-container', '/components/hero.html');
    loadComponent('.main-footer', '/components/footer.html');

    // --- Inicializar funciones según la página actual ---
    if (document.getElementById('map')) {
        inicializarMapa();
    }

    if (window.location.pathname.includes('parque-detalle.html')) {
        renderParque();
    }

    if (document.querySelector('.profile-dashboard')) {
        gestionarTabsPerfil();
    }

    // --- Eventos Globales ---
    configurarScrollHeader();
    configurarTarjetasClicables();
    gestionarVisibilidadPassword();
    gestionarFormularioRegistro();
});

/**
 * ============================================================
 * FUNCIONES DE CARGA Y NAVEGACIÓN
 * ============================================================
 */

/**
 * Carga HTML externo (Header, Footer, Hero) en un contenedor
 */
function loadComponent(selector, file, callback) {
    const container = document.querySelector(selector);
    if (!container) return;

    fetch(file)
        .then(response => {
            if (!response.ok) throw new Error("Error cargando " + file);
            return response.text();
        })
        .then(data => {
            container.innerHTML = data;
            if (callback) callback();
        })
        .catch(err => console.error(err));
}

/**
 * Resalta el enlace activo en el menú de navegación comparando la URL
 */
function marcarPaginaActiva() {
    const path = window.location.pathname;
    const links = {
        'mapa.html': 'link-mapa',
        'rutas.html': 'link-rutas',
        'perfil.html': 'link-perfil',
        'index.html': 'link-inicio'
    };

    if (path === '/' || path.endsWith('index.html')) {
        document.getElementById('link-inicio')?.classList.add('active');
        return;
    }

    for (const [file, id] of Object.entries(links)) {
        if (path.includes(file)) {
            document.getElementById(id)?.classList.add('active');
            break;
        }
    }
}

/**
 * Añade sombra al header cuando el usuario hace scroll
 */
function configurarScrollHeader() {
    window.addEventListener('scroll', () => {
        const header = document.querySelector('.main-header');
        if (header) {
            header.style.boxShadow = window.scrollY > 20 ? "0 4px 15px rgba(0,0,0,0.1)" : "none";
        }
    });
}

/**
 * ============================================================
 * FUNCIONES ESPECÍFICAS DE PÁGINAS
 * ============================================================
 */

/**
 * MAPA: Configura Leaflet y coloca los pines de los parques
 */
async function inicializarMapa() {
    const map = L.map('map').setView([40.4637, -3.7492], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    try {
        // Obtenemos los parques reales de la API
        const response = await fetch('http://127.0.0.1:5000/api/parques');
        const parques = await response.json();

        parques.forEach((parque, index) => {
            // CORRECCIÓN AQUÍ: Usar 'lat' y 'lon' para coincidir con tu API
            if (parque.lat && parque.lon) {
                const coords = [parque.lat, parque.lon];

                const customIcon = L.divIcon({
                    className: 'bosquea-marker',
                    html: `
                        <div class="pin-shape"><div class="pin-dot"></div></div>
                        <span class="pin-number">${index + 1}</span>
                    `,
                    iconSize: [30, 45],
                    iconAnchor: [15, 45]
                });

                // URL para ir al detalle buscando por nombre
                const urlDetalle = `/pages/parque-detalle.html?nombre=${encodeURIComponent(parque.nombre)}`;
                
                L.marker(coords, { icon: customIcon })
                    .addTo(map)
                    .bindPopup(`
                        <div style="text-align:center;">
                            <b>${parque.nombre}</b><br>
                            <a href="${urlDetalle}" style="color:#2d5a27; font-weight:bold;">Ver Parque</a>
                        </div>
                    `);
            }
        });
    } catch (error) {
        console.error("Error al cargar marcadores del mapa:", error);
    }
}

/**
 * ============================================================
 * UTILIDADES Y FORMULARIOS
 * ============================================================
 */

/**
 * Permite que toda la tarjeta del parque sea un enlace
 */
function configurarTarjetasClicables() {
    document.querySelectorAll('.park-card').forEach(card => {
        card.style.cursor = 'pointer';
        card.addEventListener('click', (e) => {
            // Evitamos que el clic se active si el usuario pulsó un botón o enlace interno
            if (e.target.closest('a') || e.target.closest('button')) return;

            const nombreParque = card.getAttribute('data-nombre');

            if (nombreParque) {
                // Ajuste de ruta: si ya estamos en /pages/, no añadimos el prefijo
                const isInPagesFolder = window.location.pathname.includes('pages/');
                const prefix = isInPagesFolder ? '' : 'pages/';

                // Usamos encodeURIComponent para manejar tildes y espacios correctamente
                const nombreSeguro = encodeURIComponent(nombreParque);

                window.location.href = `${prefix}parque-detalle.html?nombre=${nombreSeguro}`;
            }
        });
    });
}

