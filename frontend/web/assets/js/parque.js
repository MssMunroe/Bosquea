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

    // Si hay usuario, añadimos su ID para comprobar favs
    let url = `http://127.0.0.1:5000/api/parques/${encodeURIComponent(nombreParque)}`;
    if (usuario && usuario.id) {
        url += `?user_id=${usuario.id}`;
    }

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error("Parque no encontrado");
        const parque = await response.json();

        // Verificamos si este parque ya está en sus favoritos (opcional, pero recomendado)
        // Por ahora, lo pintamos vacío y el usuario interactúa.

        let heartHTML = "";
        if (usuario) {
            const iconoClase = parque.es_favorito ? 'fas active' : 'far';

            heartHTML = `
                <button id="btn-fav" class="fav-button" onclick="toggleFav(${usuario.id}, ${parque.id})">
                    <i id="heart-icon" class="${iconoClase} fa-heart"></i>
                </button>
            `;
        }

        contenedor.innerHTML = `
            <div class="park-header">
                <img src="${parque.img}" alt="${parque.nombre}" class="detail-img">
                <div class="title-wrapper">
                    <h1 class="detail-title">${parque.nombre}</h1>
                    ${heartHTML}
                </div>
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

        renderizarComentarios(parque.id);

    } catch (error) {
        console.error("Error:", error);
    }
}

// Función para el click del corazón
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
            // Cambiamos el icono visualmente
            if (data.estado) {
                icon.classList.replace('far', 'fas'); // Corazón relleno
                icon.classList.add('active');
            } else {
                icon.classList.replace('fas', 'far'); // Corazón vacío
                icon.classList.remove('active');
            }
        }
    } catch (error) {
        console.error("Error al gestionar favorito:", error);
    }
}

//Funcion para agregar comentarios
async function renderizarComentarios(parqueId) {
    const contenedor = document.querySelector(".park-description");
    const usuario = JSON.parse(localStorage.getItem("usuario"));

    try {
        const response = await fetch(`http://127.0.0.1:5000/api/parques/${parqueId}/comments`);
        const comentarios = await response.json();

        // 1. Construimos el formulario (solo si hay usuario)
        let htmlFormulario = "";
        if (usuario) {
            htmlFormulario = `
                <div class="add-comment-box">
                    <h3>Deja un comentario</h3>
                    <textarea id="nuevo-comentario" placeholder="Escribe tu experiencia aquí..."></textarea>
                    <button class="btn-submit-comment" onclick="enviarComentario(${parqueId})">Publicar comentario</button>
                </div>
            `;
        } else {
            htmlFormulario = `<p class="login-prompt">Debes <a href="perfil.html">iniciar sesión</a> para comentar.</p>`;
        }

        // 2. Construimos la lista de comentarios
        let htmlLista = `<div id="comments-list">`;
        if (comentarios.length === 0) {
            htmlLista += `<p class="no-comments">No hay comentarios aún. ¡Sé el primero!</p>`;
        } else {
            comentarios.forEach(com => {
                const nombreAvatar = com.avatar || 'default.png';
                const rutaAvatar = `../assets/uploads/${nombreAvatar}`;

                htmlLista += `
            <div class="comment-card">
                <img src="${rutaAvatar}" class="comment-avatar" onerror="this.src='../assets/uploads/default.png'">
                <div class="comment-body">
                    <div class="comment-header">
                        <strong>@${com.autor}</strong>
                        <span class="comment-date">${com.fecha}</span>
                    </div>
                    <p>${com.contenido}</p>
                </div>
            </div>
        `;
            });
        }
        htmlLista += `</div>`;

        // 3. Lo juntamos todo en un contenedor principal
        const fullCommentsHTML = `
            <section class="comments-section">
                ${htmlFormulario}
                <hr>
                <h3>Opiniones de otros usuarios</h3>
                ${htmlLista}
            </section>
        `;

        // Limpiamos si ya existía (por si el usuario publica varios seguidos) y lo añadimos
        const seccionAntigua = document.querySelector(".comments-section");
        if (seccionAntigua) seccionAntigua.remove();

        contenedor.insertAdjacentHTML('afterend', fullCommentsHTML);

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
                id_usuario: usuario.id, // Usamos el ID del localStorage
                id_parque: parqueId
            })
        });

        if (response.ok) {
            textarea.value = ""; // Limpiamos el texto
            renderizarComentarios(parqueId); // Recargamos solo la sección de comentarios
        } else {
            const err = await response.json();
            alert("Error: " + err.mensaje);
        }
    } catch (error) {
        console.error("Error al publicar comentario:", error);
    }
}