/**
 * ============================================================
 * BOSQUEA - JAVASCRIPT PRINCIPAL
 * ============================================================
 */

/* 1. DATOS ESTÁTICOS (Simulación de Base de Datos) */
const datosParques = {
    "picos-de-europa": {
        nombre: "Parque Nacional de los Picos de Europa",
        imagen: "../assets/img/picos-grande.jpg",
        descripcion: "Representa los ecosistemas ligados al bosque atlántico. Los Picos de Europa presentan la mayor formación caliza de la Europa Atlántica...",
        superficieTotal: "66.030,36 ha.",
        superficieSocio: "133.683,56 ha.",
        provincias: "Asturias, León y Cantabria.",
        comunidades: "Cantabria, Castilla y León y Principado de Asturias.",
        coordenadas: ["43° 18' 58'' N, 5° 07' 15'' O", "43° 04' 28'' N, 4° 37' 03'' O"]
    },
    "donana": {
        nombre: "Parque Nacional de Doñana",
        imagen: "../assets/img/Doñana.jpeg",
        descripcion: "El Parque Nacional de Doñana es un mosaico de ecosistemas que albergan una biodiversidad única en Europa...",
        superficieTotal: "54.251 ha.",
        superficieSocio: "Variables según zona periférica.",
        provincias: "Huelva y Sevilla.",
        comunidades: "Andalucía.",
        coordenadas: ["37° 0' 0'' N, 6° 30' 0'' O"]
    }
};

/* 2. INICIALIZACIÓN PRINCIPAL (Cuando el DOM está listo) */
document.addEventListener("DOMContentLoaded", function() {
    
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
function inicializarMapa() {
    const map = L.map('map').setView([40.4637, -3.7492], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    const parquesParaMapa = [
        { id: 'picos-de-europa', nombre: 'Picos de Europa', coords: [43.190, -4.830], numero: 1 },
        { id: 'donana', nombre: 'Doñana', coords: [36.992, -6.433], numero: 6 },
        { id: 'teide', nombre: 'Teide', coords: [28.272, -16.642], numero: 3 },
        { id: 'monfrague', nombre: 'Monfragüe', coords: [39.842, -6.046], numero: 14 }
        // ... (resto de parques)
    ];

    parquesParaMapa.forEach(parque => {
        const customIcon = L.divIcon({
            className: 'bosquea-marker',
            html: `
                <div class="pin-shape"><div class="pin-dot"></div></div>
                <span class="pin-number">${parque.numero}</span>
            `,
            iconSize: [30, 45],
            iconAnchor: [15, 45]
        });

        L.marker(parque.coords, { icon: customIcon })
            .addTo(map)
            .bindPopup(`<b>${parque.nombre}</b><br><a href="parque-detalle.html?id=${parque.id}">Ver Parque</a>`);
    });
}

/**
 * DETALLE: Extrae el ID de la URL y rellena la ficha del parque
 */
function renderParque() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    const p = datosParques[id];

    if (p) {
        document.getElementById('park-name').innerText = p.nombre;
        document.getElementById('park-image').src = p.imagen;
        document.getElementById('park-description').innerText = p.descripcion;
        document.getElementById('data-surface-total').innerText = p.superficieTotal;
        document.getElementById('data-surface-socio').innerText = p.superficieSocio;
        document.getElementById('data-provincias').innerText = p.provincias;
        document.getElementById('data-comunidades').innerText = p.comunidades;

        const coordsList = document.getElementById('data-coords');
        coordsList.innerHTML = p.coordenadas.map(c => `<li>${c}</li>`).join('');
    }
}

/**
 * PERFIL: Maneja el cambio de pestañas (Deseados, Comentarios, etc.)
 */
function gestionarTabsPerfil() {
    const links = document.querySelectorAll('.sidebar-link');
    const sections = document.querySelectorAll('.content-section');

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = link.getAttribute('data-section');

            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            sections.forEach(sec => {
                sec.classList.toggle('active', sec.id === `section-${target}`);
            });
        });
    });
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
            const parqueId = card.getAttribute('data-id');
            if (e.target.tagName !== 'A' && parqueId) {
                // Ajuste de ruta dependiendo de si estamos en raíz o en /pages/
                const prefix = window.location.pathname.includes('pages/') ? '' : 'pages/';
                window.location.href = `${prefix}parque-detalle.html?id=${parqueId}`;
            }
        });
    });
}

/**
 * Alterna entre mostrar/ocultar contraseña en los inputs de tipo password
 */
function gestionarVisibilidadPassword() {
    document.querySelectorAll('.eye-icon').forEach(icon => {
        icon.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input');
            const isPassword = input.type === "password";
            input.type = isPassword ? "text" : "password";
            this.classList.toggle('fa-eye', isPassword);
            this.classList.toggle('fa-eye-slash', !isPassword);
        });
    });
}

/**
 * Maneja el envío del formulario de registro y simula éxito
 */
function gestionarFormularioRegistro() {
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);
            console.log("Registro:", Object.fromEntries(formData));
            alert('¡Registro completado con éxito! Bienvenido a Bosquea.');
            window.location.href = '/index.html';
        });
    }
}