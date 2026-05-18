document.addEventListener("DOMContentLoaded", () => {
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));

    if (!usuarioLogueado) {
        mostrarLoginEstetico(); 
    } else {
        renderizarHerramientasPorRol(usuarioLogueado.rol_id);
    }
});

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

        // Activar menú admin
        gestionarTabsAdmin();

        // Cargar la sección por defecto
        cargarSeccionAdmin('rutas');

        // Activar botón logout
        vincularEventoLogout();
    } else {
        // USUARIO NORMAL
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
                    <span class="input-icon-wrapper">
                        <i class="far fa-user"></i>
                    </span>
                    <input type="text" id="login-email" placeholder="Nickname o Email">
                </div>
                
                <div class="input-group">
                    <span class="input-icon-wrapper">
                        <i class="fas fa-lock"></i>
                    </span>
                    <input type="password" id="login-password" placeholder="Contraseña">
                </div>
                
                <button class="btn-login-main" id="btn-ejecutar-login">Iniciar Sesión</button>
                
                <p class="forgot-pass">¿Olvidaste la contraseña?</p>
                
                <div class="signup-prompt">
                    <span>¿No tienes cuenta?</span> 
                    <a href="registro.html">Registrarse</a>
                </div>
            </div>
        </div>`;

    document.getElementById("btn-ejecutar-login").addEventListener("click", ejecutarLogin);
}

async function cargarPerfilReal(userId) {
    const container = document.getElementById("profile-main-container");

    try {
        // Carga estructura
        const htmlContent = await fetch('user.html').then(res => res.text());
        container.innerHTML = htmlContent;

        // Obtener datos
        const response = await fetch(`http://127.0.0.1:5000/api/users/${userId}/profile`);
        const data = await response.json();

        // Rellenar información
        document.getElementById("user-nickname").textContent = `@${data.nickname}`;

        const emailInput = document.getElementById("reporte-email");
        const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));
        if (emailInput && usuarioLogueado && usuarioLogueado.email) {
            emailInput.value = usuarioLogueado.email;
        }

        // Actualizar
        actualizarContadores(data.estadisticas);
        renderizarListaParques(data.lista_deseados, "grid-deseados", "deseado");
        renderizarListaParques(data.lista_visitados, "grid-visitados", "visitado");
        renderizarComentariosUsuario(userId);

        // Activar navegación
        gestionarTabsPerfil();
        vincularEventoLogout();
        inicializarSeccionPorDefecto();

        // Incidencias
        vincularFormularioIncidencias();

    } catch (e) {
        console.error("Error al cargar datos del usuario:", e);
    }
}

function inicializarSeccionPorDefecto() {
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(sec => {
        if (sec.id === 'section-deseados') {
            sec.classList.add('active');
            sec.style.display = 'block'; // Muestra solo deseados
        } else {
            sec.classList.remove('active');
            sec.style.display = 'none';  // Oculta las demás
        }
    });
}

// Renderiza las tarjetas de parques en los grids de Deseados/Visitados
function renderizarListaParques(lista, contenedorId, tipo) {
    const grid = document.getElementById(contenedorId);
    if (!grid) return;
    grid.innerHTML = (lista.length === 0) ? `<p class="empty-msg">Aún no tienes parques aquí.</p>` : "";

    lista.forEach(parque => {
        const actionBtn = tipo === 'visitado'
            ? `<i class="fas fa-check-circle" style="color: #466933;"></i>`
            : `<i class="fas fa-trash-alt"></i>`;

        const urlImagen = parque.img || parque.imagen || '../assets/uploads/default.png';

        const card = document.createElement("article");
        card.className = "mini-park-card";
        card.innerHTML = `
            <div class="card-header-tag">
                <i class="fas fa-tree"></i> Parque Natural
            </div>
            <div class="card-img-wrapper">
                <img src="${urlImagen}" alt="${parque.nombre}">
            </div>
            <div class="card-info">
                <h3>${parque.nombre}</h3>
                <p class="card-location">${parque.ubicacion || 'España'}</p>
                <p class="card-preview-text">Representa los ecosistemas protegidos de esta maravillosa región natural...</p>
                
                <div class="card-actions-row">
                    <a href="parque-detalle.html?nombre=${encodeURIComponent(parque.nombre)}" class="btn-view-corporate">
                        Visitar
                    </a>
                    <button class="btn-remove-corporate" onclick="eliminarParque(${parque.id}, '${tipo}')">
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
    const contenedor = document.getElementById("list-comentarios");
    if (!contenedor) return;

    try {
        const res = await fetch(`http://127.0.0.1:5000/api/users/${userId}/comments`);
        const comentarios = await res.json();

        // Si el servidor devuelve un error en el JSON
        if (comentarios.error) {
            console.error("Error desde la API:", comentarios.error);
            contenedor.innerHTML = `<p class="empty-msg">Hubo un error al cargar tus comentarios.</p>`;
            return;
        }

        contenedor.innerHTML = (comentarios.length === 0) ? `<p class="empty-msg">No has escrito comentarios.</p>` : "";

        comentarios.forEach(com => {
            // Mapeo de las variables
            const nombreParque = com.parque_nombre;
            const contenido = com.contenido;
            const fecha = com.fecha;
            const idComentario = com.id;

            const imgParque = com.parque_img.startsWith('http') || com.parque_img.startsWith('..')
                ? com.parque_img
                : `../assets/uploads/${com.parque_img}`;

            const div = document.createElement("div");
            div.className = "user-comment-card";
            div.innerHTML = `
                <div class="comment-card-left">
                    <div class="comment-park-avatar">
                        <img src="${imgParque}" alt="${nombreParque}">
                    </div>
                    <div class="comment-card-body">
                        <h4>${nombreParque}</h4>
                        <p class="comment-text">${contenido}</p>
                        <div class="comment-actions">
                            <button class="btn-delete-comment" onclick="eliminarParque(${idComentario}, 'comentario')">
                                <i class="fas fa-trash-alt"></i> Eliminar Comentario
                            </button>
                        </div>
                    </div>
                </div>
                <div class="comment-card-right">
                    <span class="comment-date">${fecha}</span>
                </div>
            `;
            contenedor.appendChild(div);
        });
    } catch (err) {
        console.error("Error en la petición de comentarios:", err);
    }
}

/* ==========================================
    FUNCIONES AUXILIARES
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

    // Si no hay, salimos para evitar errores
    if (links.length === 0 || sections.length === 0) return;

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            const target = link.getAttribute('data-section');

            // Gestionar estado visual
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // Gestionar visibilidad
            sections.forEach(sec => {
                // Quitamos la clase active de todas
                sec.classList.remove('active');
                sec.style.display = 'none';

                // Si el ID coincide con section-nombre
                if (sec.id === `section-${target}`) {
                    sec.classList.add('active');
                    sec.style.display = 'block'; //
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
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario")) || {};

    switch (seccion) {
        case 'rutas':
            area.innerHTML = `
                <div class="admin-header-row">
                    <h3 class="section-display-title">Gestión de Rutas</h3>
                    <button class="btn-submit-report" style="width: auto; padding: 10px 20px; margin: 0;" onclick="abrirModalRuta()">
                        <i class="fas fa-plus"></i> Nueva Ruta
                    </button>
                </div>
                <div id="lista-rutas-admin" class="config-options-list">Cargando rutas...</div>
            `;
            break;

        case 'parques':
            area.innerHTML = `
                <div class="admin-header-row">
                    <h3 class="section-display-title">Gestión de Parques</h3>
                </div>
                
                <form id="form-crear-parque" class="error-report-form" style="margin-top: 10px;">
                    <div class="form-group-corporate">
                        <label for="admin-parque-nombre">Nombre del Parque <span class="required-asterisk">*</span></label>
                        <input type="text" id="admin-parque-nombre" class="textarea-corporate" style="height: auto; padding: 12px;" required placeholder="Ej. Sierra de Guadarrama">
                    </div>

                    <div class="form-group-corporate">
                        <label for="admin-parque-ubicacion">Ubicación <span class="required-asterisk">*</span></label>
                        <input type="text" id="admin-parque-ubicacion" class="textarea-corporate" style="height: auto; padding: 12px;" required placeholder="Ej. Madrid, España">
                    </div>

                    <div class="form-group-corporate">
                        <label for="admin-parque-tamanio">Tamaño (ha) <span class="required-asterisk">*</span></label>
                        <input type="number" id="admin-parque-tamanio" class="textarea-corporate" style="height: auto; padding: 12px;" required placeholder="Ej. 33960">
                    </div>

                    <div class="form-group-corporate">
                        <label for="admin-parque-descripcion">Descripción Corta</label>
                        <textarea id="admin-parque-descripcion" class="textarea-corporate" rows="3" placeholder="Breve reseña sobre el ecosistema protegido..."></textarea>
                    </div>

                    <button type="submit" class="btn-submit-report"><i class="fas fa-save"></i> Crear Parque Natural</button>
                </form>
                <hr style="border: 0; border-top: 1px solid #e8e8e0; margin: 30px 0; max-width: 600px;">
                <div id="lista-parques-admin" class="config-options-list">Cargando parques...</div>
            `;

            document.getElementById("form-crear-parque").addEventListener("submit", async (e) => {
                e.preventDefault();
                const nombre = document.getElementById("admin-parque-nombre").value;
                const ubicacion = document.getElementById("admin-parque-ubicacion").value;
                const tamanio = document.getElementById("admin-parque-tamanio").value;
                const descripcion = document.getElementById("admin-parque-descripcion").value;

                try {
                    const response = await fetch('http://127.0.0.1:5000/api/admin/parques', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            rol_id: usuarioLogueado.rol_id,
                            nombre, ubicacion, tamanio, descripcion
                        })
                    });
                    const resData = await response.json();
                    if (response.ok) {
                        alert("¡Parque creado con éxito!");
                        document.getElementById("form-crear-parque").reset();
                        cargarSeccionAdmin('parques');
                    } else {
                        alert(resData.error || "Error al crear el parque");
                    }
                } catch (err) {
                    console.error(err);
                }
            });
            break;

        case 'informes':
            area.innerHTML = `
                <h3 class="section-display-title">Importar/Exportar Datos (XML)</h3>
                <p class="form-field-subtitle" style="font-size: 14px; margin-bottom: 20px;">Gestiona la base de datos de rutas mediante transferencias seguras de archivos estructurados XML.</p>
                
                <div class="error-report-form">
                    <div class="form-group-corporate">
                        <label>Exportar Estructura</label>
                        <button onclick="descargarXML()" class="btn-submit-report" style="background-color: #fafaf6; color: #8f7238; border: 1px solid #c4c4b5;">
                            <i class="fas fa-file-code" style="margin-right: 8px;"></i> Exportar Base de Datos a XML
                        </button>
                    </div>

                    <div class="form-group-corporate" style="margin-top: 10px;">
                        <label for="input-xml">Cargar Archivo XML</label>
                        <span class="form-field-subtitle">Sube el fichero con la nueva colección de rutas para procesar</span>
                        <div class="drag-drop-zone" id="drop-zone-xml" onclick="document.getElementById('input-xml').click()">
                            <input type="file" id="input-xml" accept=".xml" style="display:none" onchange="subirXML(this)">
                            <i class="fas fa-file-upload drag-zone-icon"></i>
                            <p class="drag-zone-text">Arrastra el archivo XML aquí o <span class="accent-link-text">buscar en el equipo</span></p>
                        </div>
                    </div>
                </div>
            `;
            break;

        case 'excel':
            area.innerHTML = `
                <h3 class="section-display-title">Reporte Estadístico</h3>
                <p class="form-field-subtitle" style="font-size: 14px; margin-bottom: 20px;">Descarga y analiza las métricas consolidadas mensuales correspondientes a la afluencia y visitas.</p>
                
                <div class="error-report-form">
                    <div class="drag-drop-zone" style="cursor: default; background-color: #fafaf6; padding: 35px 20px;">
                        <i class="fas fa-file-excel drag-zone-icon" style="font-size: 40px; color: #466933;"></i>
                        <h4 style="margin: 10px 0 5px 0; color: #2b2b2b; font-weight: 500;">Generador de Informes Bosquea</h4>
                        <p class="form-field-subtitle" style="text-align: center; max-width: 400px; margin-bottom: 15px;">
                            El archivo descargable incluye gráficos dinámicos autogenerados nativamente mediante nuestra hoja de cálculo Excel.
                        </p>
                        <button onclick="descargarExcel()" class="btn-submit-report" style="background-color: #466933; max-width: 280px;">
                            <i class="fas fa-download"></i> Descargar Excel (.xlsx)
                        </button>
                    </div>
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

//Eliminar parque de Fav o Vistos
async function eliminarParque(id, tipo) {
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));
    if (!usuarioLogueado) return;

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
                cargarPerfilReal(usuarioLogueado.id);
            }
        } catch (error) {
            console.error("Error en toggle:", error);
        }
    }

    else if (tipo === 'comentario') {
        if (!confirm("¿Borrar este comentario?")) return;

        try {
            const response = await fetch(`http://127.0.0.1:5000/api/comments/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                renderizarComentariosUsuario(usuarioLogueado.id);
            }
        } catch (error) {
            console.error("Error borrando comentario:", error);
        }
    }
}

// Formulario de incidencias
function vincularFormularioIncidencias() {
    const formulario = document.getElementById("form-reporte-error");
    if (!formulario) return;

    formulario.addEventListener("submit", async (e) => {
        e.preventDefault();

        const descripcion = document.getElementById("reporte-descripcion").value;
        const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));

        if (!usuarioLogueado || !usuarioLogueado.id) {
            alert("Error: No se ha detectado una sesión activa.");
            return;
        }

        // Preparamos el JSON
        const payload = {
            descripcion: descripcion,
            id_usuario: usuarioLogueado.id
        };

        try {
            const response = await fetch('http://127.0.0.1:5000/api/reports/incident', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const resultado = await response.json();

            if (response.ok) {
                alert("¡Incidencia reportada con éxito!");
                formulario.reset();

                // Mantenemos el email del usuario
                const emailInput = document.getElementById("reporte-email");
                if (emailInput && usuarioLogueado.email) {
                    emailInput.value = usuarioLogueado.email;
                }
            } else {
                alert(resultado.error || "Ocurrió un error al enviar el reporte.");
            }
        } catch (err) {
            console.error("Error al conectar con el servidor de reportes:", err);
            alert("No se pudo conectar con el servidor.");
        }
    });
}