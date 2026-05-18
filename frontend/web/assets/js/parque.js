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
    const usuario = JSON.parse(localStorage.getItem("usuario"));

    let url = `http://127.0.0.1:5000/api/parques/${encodeURIComponent(nombreParque)}`;
    if (usuario && usuario.id) {
        url += `?user_id=${usuario.id}`;
    }

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error("Parque no encontrado");
        const parque = await response.json();

        // Contenedor para las acciones del usuario (Favorito y Visitado)
        let userActionsHTML = "";
        if (usuario) {
            const favIconClase = parque.es_favorito ? 'fas active' : 'far';
            const visitIconClase = parque.es_visitado ? 'fas active' : 'far';

            userActionsHTML = `
                <div class="park-actions-group">
                    <button id="btn-fav" class="fav-button" onclick="toggleFav(${usuario.id}, ${parque.id})" title="Añadir a deseados">
                        <i id="heart-icon" class="${favIconClase} fa-heart"></i>
                    </button>
                    
                    <button id="btn-visited" class="visited-button" onclick="toggleVisited(${usuario.id}, ${parque.id})" title="Marcar como visitado">
                        <i id="folder-icon" class="${visitIconClase} fa-folder"></i>
                    </button>
                </div>
            `;
        }

        contenedor.innerHTML = `
            <div class="park-detail-wrapper">
                <div class="park-main-header">
                    <h1 class="detail-title">Parque Nacional de ${parque.nombre}</h1>
                    ${userActionsHTML}
                </div>
                
                <div class="image-showcase">
                    <img src="${parque.img}" alt="${parque.nombre}" class="detail-img">
                </div>
                
                <div class="park-content-body">
                    <p class="main-description">${parque.descripcion}</p>
                    
                    <div class="datos-basicos-section">
                        <h3>Datos básicos</h3>
                        <ul class="datos-basicos-list">
                            <li><strong>Superficie:</strong> ${parque.tamanio} ha.</li>
                            <li><strong>Ubicación:</strong> ${parque.ubicacion}.</li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <div class="comments-anchor-container"></div>
        `;

        renderizarComentarios(parque.id);

    } catch (error) {
        console.error("Error:", error);
        contenedor.innerHTML = "<p class='error-msg'>Error al cargar la información del parque.</p>";
    }
}

async function toggleFav(userId, parqueId) {
    const icon = document.getElementById("heart-icon");
    try {
        const response = await fetch('http://127.0.0.1:5000/api/favoritos/toggle', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id_usuario: userId, id_parque: parqueId })
        });
        const data = await response.json();

        if (response.ok) {
            if (data.estado) {
                icon.classList.replace('far', 'fas');
                icon.classList.add('active');
            } else {
                icon.classList.replace('fas', 'far');
                icon.classList.remove('active');
            }
        }
    } catch (error) {
        console.error("Error al gestionar favorito:", error);
    }
}

// Para alternar el estado de "Visitado"
async function toggleVisited(userId, parqueId) {
    const icon = document.getElementById("folder-icon");
    try {
        const response = await fetch('http://127.0.0.1:5000/api/visitados/toggle', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id_usuario: userId, id_parque: parqueId })
        });
        const data = await response.json();

        if (response.ok) {
            if (data.estado) {
                icon.classList.replace('far', 'fas');
                icon.classList.add('active');
                icon.classList.replace('fa-folder', 'fa-folder-open');
            } else {
                icon.classList.replace('fas', 'far');
                icon.classList.remove('active');
                icon.classList.replace('fa-folder-open', 'fa-folder');
            }
        }
    } catch (error) {
        console.error("Error al gestionar visitado:", error);
    }
}

async function renderizarComentarios(parqueId) {
    const anchor = document.querySelector(".comments-anchor-container");
    const usuario = JSON.parse(localStorage.getItem("usuario"));

    if (!anchor) return;

    try {
        const response = await fetch(`http://127.0.0.1:5000/api/parques/${parqueId}/comments`);
        const comentarios = await response.json();

        let htmlFormulario = "";
        if (usuario) {
            htmlFormulario = `
                <div class="add-comment-box">
                    <h3>Deja un comentario</h3>
                    <textarea id="nuevo-comentario" placeholder="Escribe tu experiencia aquí..."></textarea>
                    <div class="btn-comment-wrapper">
                        <button class="btn-submit-comment" onclick="enviarComentario(${parqueId})">Publicar comentario</button>
                    </div>
                </div>
            `;
        } else {
            htmlFormulario = `
                <div class="login-prompt-box">
                    <p class="login-prompt">Debes <a href="perfil.html">iniciar sesión</a> para dejar un comentario.</p>
                </div>
            `;
        }

        let htmlLista = `<div id="comments-list">`;
        if (comentarios.length === 0) {
            htmlLista += `<p class="no-comments">No hay comentarios aún. ¡Sé el primero en compartir su experiencia!</p>`;
        } else {
            comentarios.forEach(com => {
                const nombreAvatar = com.avatar || 'default.png';
                const rutaAvatar = `../assets/uploads/${nombreAvatar}`;

                htmlLista += `
                    <div class="comment-card">
                        <img src="${rutaAvatar}" class="comment-avatar" onerror="this.src='../assets/uploads/default.png'">
                        <div class="comment-body">
                            <div class="comment-header">
                                <span class="comment-author">@${com.autor}</span>
                                <span class="comment-date">${com.fecha}</span>
                            </div>
                            <p class="comment-text">${com.contenido}</p>
                        </div>
                    </div>
                `;
            });
        }
        htmlLista += `</div>`;

        anchor.innerHTML = `
            <section class="comments-section">
                ${htmlFormulario}
                <h3 class="opinions-heading">Opiniones de otros usuarios</h3>
                ${htmlLista}
            </section>
        `;

    } catch (error) {
        console.error("Error cargando comentarios:", error);
    }
}

async function enviarComentario(parqueId) {
    const textarea = document.getElementById("nuevo-comentario");
    const contenido = textarea.value.trim();
    const usuario = JSON.parse(localStorage.getItem("usuario"));

    if (!contenido) {
        alert("El comentario no puede estar vacío.");
        return;
    }

    try {
        const response = await fetch('http://127.0.0.1:5000/api/comments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                contenido: contenido,
                id_usuario: usuario.id,
                id_parque: parqueId
            })
        });

        if (response.ok) {
            textarea.value = "";
            renderizarComentarios(parqueId);
        } else {
            const err = await response.json();
            alert("Error: " + err.mensaje);
        }
    } catch (error) {
        console.error("Error al publicar comentario:", error);
    }
}