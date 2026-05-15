/**
 * PERFIL.JS - GESTIÓN DE ACCESO Y DASHBOARD
 * Lógica: Admin (Rol 1) / Usuario (Otros) / Login (Sin sesión)
 */

document.addEventListener("DOMContentLoaded", () => {
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));

    if (!usuarioLogueado) {
        mostrarLoginEstetico(); // No hay sesión
    } else {
        renderizarHerramientasPorRol(usuarioLogueado.rol_id); // Hay sesión
    }
});

/* ==========================================
   1. CONTROL DE ACCESO Y VISTAS PRINCIPALES
   ========================================== */

async function renderizarHerramientasPorRol(rol_id) {
    const container = document.getElementById("profile-main-container");
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));

    if (rol_id === 1) {
        // --- VISTA DE ADMINISTRADOR ---
        container.innerHTML = `
            <div class="admin-dashboard">
                <aside class="sidebar">
                    <h2>Panel Admin</h2>
                    <nav>
                        <a href="#" class="sidebar-link active" data-section="rutas">Rutas</a>
                        <a href="#" class="sidebar-link" data-section="parques">Parques</a>
                        <a href="#" class="sidebar-link" data-section="informes">Informes (XML)</a>
                        <a href="#" class="sidebar-link" data-section="excel">Reporte Excel</a>
                    </nav>
                    <button id="btn-logout" class="btn-logout-admin">Cerrar Sesión</button>
                </aside>
                <main class="admin-content" id="admin-content-area">
                    </main>
            </div>
        `;

        // 1. Activar los clics del menú admin
        gestionarTabsAdmin();
        
        // 2. Cargar la primera sección por defecto
        cargarSeccionAdmin('rutas');

        // 3. Activar botón logout
        vincularEventoLogout();
    } else {
        // --- VISTA DE USUARIO NORMAL ---
        cargarPerfilReal(usuarioLogueado.id);
    }
}

// Formulario de login con estilo visual
function mostrarLoginEstetico() {
    const container = document.getElementById("profile-main-container");
    container.innerHTML = `
        <div class="login-wrapper">
            <div class="login-box">
                <div class="input-group">
                    <i class="fas fa-user"></i>
                    <input type="text" id="login-email" placeholder="Nickname o Email">
                </div>
                <div class="input-group">
                    <i class="fas fa-lock"></i>
                    <input type="password" id="login-password" placeholder="Contraseña">
                </div>
                <button class="btn-login-main" id="btn-ejecutar-login">Iniciar Sesión</button>
                <p class="forgot-pass">¿Olvidaste la contraseña?</p>
                <div class="signup-prompt">¿No tienes cuenta? <a href="registro.html">Registrarse</a></div>
                <div class="divider"><span>O</span></div>
                <p class="social-text">Inicia sesión con una red social</p>
                <div class="social-icons">
                    <i class="fab fa-facebook"></i><i class="fab fa-instagram"></i><i class="fab fa-google"></i>
                </div>
            </div>
        </div>`;

    document.getElementById("btn-ejecutar-login").addEventListener("click", ejecutarLogin);
}

/* ==========================================
   2. LÓGICA DE USUARIO (DASHBOARD)
   ========================================== */

async function cargarPerfilReal(userId) {
    const container = document.getElementById("profile-main-container");

    try {
        // 1. Carga estructura base desde user.html
        const htmlContent = await fetch('user.html').then(res => res.text());
        container.innerHTML = htmlContent;

        // 2. Obtener datos del servidor
        const response = await fetch(`http://127.0.0.1:5000/api/users/${userId}/profile`);
        const data = await response.json();

        // 3. Rellenar información básica
        document.getElementById("user-nickname").textContent = `@${data.nickname}`;

        const avatarImg = document.querySelector(".profile-avatar");
        if (avatarImg && data.icono) {
            const ruta = (data.icono === 'default.png') ? '../assets/img/' : '../assets/uploads/';
            avatarImg.src = ruta + data.icono;
        }

        // 4. Actualizar contadores y grids
        actualizarContadores(data.estadisticas);
        renderizarListaParques(data.lista_deseados, "grid-deseados", "deseado");
        renderizarListaParques(data.lista_visitados, "grid-visitados", "visitado");
        renderizarComentariosUsuario(userId);

        // 5. Activar navegación interna y logout
        gestionarTabsPerfil();
        vincularEventoLogout();

    } catch (e) {
        console.error("Error al cargar datos del usuario:", e);
    }
}

// Renderiza las tarjetas de parques en los grids de Deseados/Visitados
function renderizarListaParques(lista, contenedorId, tipo) {
    const grid = document.getElementById(contenedorId);
    if (!grid) return;
    grid.innerHTML = (lista.length === 0) ? `<p class="empty-msg">Aún no tienes parques aquí.</p>` : "";

    lista.forEach(parque => {
        const actionBtn = tipo === 'visitado'
            ? `<i class="fas fa-check-circle" style="color: #4b6a32;"></i>`
            : `<i class="fas fa-trash-alt"></i>`;

        const card = document.createElement("article");
        card.className = "mini-park-card";
        card.innerHTML = `
            <div class="card-img-wrapper">
                <img src="${parque.img}" alt="${parque.nombre}">
            </div>
            <div class="card-info">
                <h3>${parque.nombre}</h3>
                <p>${parque.ubicacion}</p>
                <div class="card-actions">
                    <a href="parque-detalle.html?nombre=${encodeURIComponent(parque.nombre)}" class="btn-view">
                        Ver más
                    </a>
                    <button class="btn-remove" onclick="eliminarParque(${parque.id}, '${tipo}')">
                        ${actionBtn}
                    </button>
                </div>
            </div>
        `;
        grid.appendChild(card);
    });
}

// Obtiene y pinta los comentarios del usuario
async function renderizarComentariosUsuario(userId) {
    const contenedor = document.getElementById("grid-comentarios");
    if (!contenedor) return;

    try {
        const res = await fetch(`http://127.0.0.1:5000/api/users/${userId}/comments`);
        const comentarios = await res.json();
        contenedor.innerHTML = (comentarios.length === 0) ? `<p class="empty-msg">No has escrito comentarios.</p>` : "";

        comentarios.forEach(com => {
            const div = document.createElement("div");
            div.className = "user-comment-card";
            div.innerHTML = `
                <div class="comment-info">
                    <h4>En: ${com.parque_nombre}</h4>
                    <span class="comment-date">${com.fecha}</span>
                </div>
                <p class="comment-text">"${com.contenido}"</p>
                <div class="comment-actions">
                    <button class="btn-delete-comment" onclick="eliminarParque(${com.id}, 'comentario')">
                        <i class="fas fa-trash"></i> Eliminar
                    </button>
                </div>`;
            contenedor.appendChild(div);
        });
    } catch (err) { console.error("Error comentarios:", err); }
}

/* ==========================================
   3. FUNCIONES AUXILIARES Y EVENTOS
   ========================================== */

async function ejecutarLogin() {
    const email = document.getElementById("login-email").value;
    const contra = document.getElementById("login-password").value;

    try {
        const response = await fetch('http://127.0.0.1:5000/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, contra })
        });
        const data = await response.json();

        if (response.ok) {
            localStorage.setItem("usuario", JSON.stringify(data.usuario));
            window.location.reload();
        } else {
            alert(data.error || "Error al iniciar sesión");
        }
    } catch (err) { console.error("Error login:", err); }
}

function gestionarTabsPerfil() {
    const links = document.querySelectorAll('.sidebar-link[data-section]');
    const sections = document.querySelectorAll('.content-section');

    // Si no hay links o secciones, salimos para evitar errores
    if (links.length === 0 || sections.length === 0) return;

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault(); // Evita que la página salte arriba

            const target = link.getAttribute('data-section');
            console.log("Cambiando a sección:", target); // Para depuración

            // 1. Gestionar estado visual de los botones del menú
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // 2. Gestionar visibilidad de las secciones de contenido
            sections.forEach(sec => {
                // Quitamos la clase active de todas
                sec.classList.remove('active');

                // Forzamos el ocultado por estilo si tu CSS no es suficiente
                sec.style.display = 'none';

                // Si el ID coincide con section-nombre (ej: section-deseados)
                if (sec.id === `section-${target}`) {
                    sec.classList.add('active');
                    sec.style.display = 'block'; // O 'grid' según tu diseño
                }
            });
        });
    });
}

function gestionarTabsAdmin() {
    const links = document.querySelectorAll('.sidebar-link');
    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = link.getAttribute('data-section');

            // Cambiar clase activa en el menú
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // Cargar la lógica de la sección
            cargarSeccionAdmin(target);
        });
    });
}

function cargarSeccionAdmin(seccion) {
    const area = document.getElementById("admin-content-area");
    
    switch(seccion) {
        case 'rutas':
            area.innerHTML = `
                <div class="admin-header-row">
                    <h3>Gestión de Rutas</h3>
                    <button class="btn-add" onclick="abrirModalRuta()">+ Nueva Ruta</button>
                </div>
                <div id="lista-rutas-admin">Cargando rutas...</div>
            `;
            // Aquí llamarías a una función que haga fetch a /api/routes y pinte una tabla con botones de Editar/Eliminar
            break;

        case 'parques':
            area.innerHTML = `
                <div class="admin-header-row">
                    <h3>Gestión de Parques</h3>
                    <button class="btn-add" onclick="abrirModalParque()">+ Nuevo Parque</button>
                </div>
                <div id="lista-parques-admin">Cargando parques...</div>
            `;
            // Lógica similar para parques
            break;

        case 'informes':
            area.innerHTML = `
                <div class="admin-card">
                    <h3>Importar/Exportar Datos (XML)</h3>
                    <p>Gestiona la base de datos mediante archivos XML.</p>
                    <div class="admin-actions">
                        <button onclick="descargarXML()" class="btn-xml"><i class="fas fa-file-code"></i> Exportar a XML</button>
                        <hr>
                        <input type="file" id="input-xml" style="display:none" onchange="subirXML(this)">
                        <button onclick="document.getElementById('input-xml').click()" class="btn-upload">
                            <i class="fas fa-upload"></i> Importar XML
                        </button>
                    </div>
                </div>
            `;
            break;

        case 'excel':
            area.innerHTML = `
                <div class="admin-card">
                    <h3>Reporte Estadístico</h3>
                    <p>Descarga el informe mensual de afluencia y actividad en formato Excel.</p>
                    <button onclick="descargarExcel()" class="btn-download">
                        <i class="fas fa-file-excel"></i> Descargar Excel (.xlsx)
                    </button>
                </div>
            `;
            break;
    }
}

function vincularEventoLogout() {
    const btn = document.getElementById("btn-logout");
    if (btn) {
        btn.addEventListener("click", () => {
            localStorage.removeItem("usuario");
            window.location.href = "../index.html";
        });
    }
}

function actualizarContadores(stats) {
    if (document.getElementById("count-deseados"))
        document.getElementById("count-deseados").textContent = `${stats.lista_deseos} Parques`;
    if (document.getElementById("count-visitados"))
        document.getElementById("count-visitados").textContent = `${stats.parques_visitados} Parques`;
}

// Funciones de utilidad para acciones de Admin
function descargarExcel() {
    // Reutiliza tu endpoint de Flask
    window.open('http://127.0.0.1:5000/api/admin/report-chart', '_blank');
}

async function subirXML(input) {
    const file = input.files[0];
    if (!file) return;

    const usuario = JSON.parse(localStorage.getItem("usuario"));
    const formData = new FormData();
    formData.append('file', file);
    formData.append('rol_id', usuario.rol_id);

    try {
        const response = await fetch('http://127.0.0.1:5000/api/admin/import-xml', {
            method: 'POST',
            body: formData
        });
        if (response.ok) {
            alert("XML importado con éxito");
        } else {
            alert("Error al importar el XML");
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

/**
 * ELIMINAR PARQUE DE LAS LISTAS (Deseados o Visitados)
 */
async function eliminarParque(id, tipo) {
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));
    if (!usuarioLogueado) return;

    // --- CASO A: ELIMINAR DE DESEADOS (Tu endpoint toggle) ---
    if (tipo === 'deseado') {
        if (!confirm("¿Quitar de tu lista de favoritos?")) return;

        try {
            const response = await fetch('http://127.0.0.1:5000/api/favoritos/toggle', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    id_usuario: usuarioLogueado.id,
                    id_parque: id
                })
            });

            if (response.ok) {
                cargarPerfilReal(usuarioLogueado.id); // Refrescar vista
            }
        } catch (error) {
            console.error("Error en toggle:", error);
        }
    }

    // --- CASO B: ELIMINAR COMENTARIO (Tu endpoint delete_comment) ---
    else if (tipo === 'comentario') {
        if (!confirm("¿Borrar este comentario?")) return;

        try {
            const response = await fetch(`http://127.0.0.1:5000/api/comments/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                renderizarComentariosUsuario(usuarioLogueado.id); // Refrescar solo comentarios
            }
        } catch (error) {
            console.error("Error borrando comentario:", error);
        }
    }
}