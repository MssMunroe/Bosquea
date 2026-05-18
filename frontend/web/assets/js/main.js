document.addEventListener("DOMContentLoaded", () => {
    // Carga de piezas que se repiten en toda la web
    loadComponent('.main-header', '/components/header.html', marcarPaginaActiva);
    loadComponent('.main-footer', '/components/footer.html');
    loadComponent('.hero-container', '/components/hero.html', () => {
        inicializarBusquedaHero();
    });

    // Inicializo funciones solo si el elemento existe en la página actual
    if (document.getElementById('map')) inicializarMapa();
    if (document.querySelector('.profile-dashboard')) gestionarTabsPerfil();

    configurarScrollHeader();
});

// Inyecta HTML externo en los contenedores (Header/Footer)
function loadComponent(selector, file, callback) {
    const container = document.querySelector(selector);
    if (!container) return;

    fetch(file)
        .then(res => res.ok ? res.text() : Promise.reject())
        .then(data => {
            container.innerHTML = data;
            if (callback) callback();
        })
        .catch(err => console.error("Error cargando componente:", file));
}

// Resalta donde estamos en el menú
function marcarPaginaActiva() {
    const path = window.location.pathname;
    const links = {
        'mapa.html': 'link-mapa',
        'rutas.html': 'link-rutas',
        'perfil.html': 'link-perfil',
        'index.html': 'link-inicio'
    };

    Object.entries(links).forEach(([file, id]) => {
        if (path.includes(file) || (path === '/' && id === 'link-inicio')) {
            document.getElementById(id)?.classList.add('active');
        }
    });
}

// Efecto de sombra en el header al bajar
function inicializarBusquedaHero() {
    const input = document.getElementById('hero-search-input');
    const resultsContainer = document.getElementById('search-results');

    if (!input) {
        console.error("No se encontró el input de búsqueda");
        return;
    }

    input.addEventListener('input', async (e) => {
        const text = e.target.value.trim();

        if (text.length < 2) {
            resultsContainer.style.display = 'none';
            return;
        }

        try {
            const res = await fetch(`https://bosquea-backend.onrender.com/api/search?q=${text}`);
            const data = await res.json();
            
            renderizarResultados(data);
        } catch (err) {
            console.error("Error en búsqueda:", err);
        }
    });
}

function renderizarResultados(data) {
    const resultsContainer = document.getElementById('search-results');
    if (data.length > 0) {
        resultsContainer.innerHTML = data.map(item => `
            <a href="${item.url}" class="result-item">
                <i class="${item.tipo === 'parque' ? 'fas fa-tree' : 'fas fa-route'}"></i>
                <span>${item.nombre}</span>
                <small>${item.tipo.toUpperCase()}</small>
            </a>
        `).join('');
        resultsContainer.style.display = 'block';
    } else {
        resultsContainer.innerHTML = '<div class="result-item">No hay resultados</div>';
        resultsContainer.style.display = 'block';
    }
}

function configurarScrollHeader() {
    const header = document.querySelector('.main-header');
    
    window.addEventListener('scroll', () => {
        if (window.scrollY > 50) {
            header.classList.add('header-scrolled');
        } else {
            header.classList.remove('header-scrolled');
        }
    });
}