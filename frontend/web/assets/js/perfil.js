document.addEventListener("DOMContentLoaded", () => {
    const usuarioLogueado = JSON.parse(localStorage.getItem("usuario"));

    if (!usuarioLogueado) {
        // No hay sesión: mostramos el formulario estilo la imagen que pasaste
        mostrarLoginEstetico();
    } else {
        // Hay sesión: cargamos los datos del perfil real
        cargarPerfilReal(usuarioLogueado.id);
    }
});

// FUNCIÓN PARA EL PERFIL POR DEFECTO (EL DE TU IMAGEN)
function mostrarLoginEstetico() {
    const container = document.getElementById("profile-main-container");

    // Aquí inyectamos el HTML que imita tu diseño
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
                
                <div class="signup-prompt">
                    ¿No tienes cuenta? <a href="registro.html">Registrarse</a>
                </div>
                
                <div class="divider">
                    <span>O</span>
                </div>
                
                <p class="social-text">Inicia sesión con una red social</p>
                <div class="social-icons">
                    <i class="fab fa-facebook"></i>
                    <i class="fab fa-instagram"></i>
                    <i class="fab fa-google"></i>
                </div>
            </div>
        </div>
    `;

    // Escuchar el clic del botón de login
    document.getElementById("btn-ejecutar-login").addEventListener("click", ejecutarLogin);
}

// LÓGICA PARA HACER EL LOGIN CONTRA TU API
async function ejecutarLogin() {
    const email = document.getElementById("login-email").value;
    const contra = document.getElementById("login-password").value;

    try {
        const response = await fetch('http://127.0.0.1:5000/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: email,   // Asegúrate de que se llame 'email'
                contra: contra  // Asegúrate de que se llame 'contra'
            })
        });

        const data = await response.json();

        if (response.ok) {
            // Guardamos el usuario (que ahora debe incluir el ID)
            localStorage.setItem("usuario", JSON.stringify(data.usuario));
            // Recargamos la página para que el script detecte la sesión y cargue el perfil real
            window.location.reload();
        } else {
            alert(data.error || "Error al iniciar sesión");
        }
    } catch (error) {
        console.error("Error en el login:", error);
    }
}

// FUNCIÓN PARA CARGAR EL PERFIL REAL (DASHBOARD)
async function cargarHTML(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error("No se pudo cargar el archivo: " + url);
    return await response.text();
}

async function cargarPerfilReal(userId) {
    const container = document.getElementById("profile-main-container");
    
    try {
        // 1. Cargar el HTML tal cual lo tienes
        const htmlContent = await cargarHTML('user.html'); 
        container.innerHTML = htmlContent;

        // 2. Pedir datos al servidor
        const response = await fetch(`http://127.0.0.1:5000/api/users/${userId}/profile`);
        const data = await response.json();

        // --- CARGA DE DATOS DINÁMICOS ---

        // A. Nickname (usando tu ID existente)
        const nicknameElement = document.getElementById("user-nickname");
        if (nicknameElement) {
            nicknameElement.textContent = `@${data.nickname}`;
        }

        // B. Imagen de perfil (usando la clase profile-avatar que ya tienes)
        const avatarImg = document.querySelector(".profile-avatar");
        if (avatarImg && data.img_perfil) {
            const esDefault = data.img_perfil === 'default-avatar.png';
            const rutaBase = esDefault ? '../assets/img/' : '../assets/uploads/';
            avatarImg.src = rutaBase + data.img_perfil;
        }

        // C. Estadísticas (coincidiendo con tus IDs de Parques)
        if(document.getElementById("count-deseados")) {
            document.getElementById("count-deseados").textContent = `${data.estadisticas.lista_deseos} Parques`;
        }
        if(document.getElementById("count-visitados")) {
            document.getElementById("count-visitados").textContent = `${data.estadisticas.parques_visitados} Parques`;
        }

        // 3. Renderizar los grids
        renderizarListaParques(data.lista_deseados, "grid-deseados", "deseado");
        renderizarListaParques(data.lista_visitados, "grid-visitados", "visitado");

        // 4. Activar los eventos existentes
        gestionarTabsPerfil();

        // 5. Logout (en el botón que dejaste al final)
        const logoutBtn = document.getElementById("btn-logout");
        if (logoutBtn) {
            logoutBtn.addEventListener("click", () => {
                localStorage.removeItem("usuario");
                window.location.href = "../index.html"; 
            });
        }

    } catch (e) {
        console.error("Error al cargar datos dinámicos:", e);
    }
}
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

function renderizarListaParques(lista, contenedorId, tipo) {
    const grid = document.getElementById(contenedorId);
    grid.innerHTML = "";

    if (lista.length === 0) {
        grid.innerHTML = `<p class="empty-msg">Aún no tienes parques en esta lista.</p>`;
        return;
    }

    lista.forEach(parque => {
        const card = document.createElement("article");
        card.className = "mini-park-card";
        
        // Si es visitado, ponemos el check verde, si es deseado, la papelera
        const actionBtn = tipo === 'visitado' 
            ? `<i class="fas fa-check-circle" style="color: #4b6a32;"></i>`
            : `<i class="fas fa-trash-alt"></i>`;

        card.innerHTML = `
            <div class="card-img-wrapper">
                <img src="${parque.img}" alt="${parque.nombre}">
            </div>
            <div class="card-info">
                <h3>${parque.nombre}</h3>
                <p>${parque.ubicacion}</p>
                <div class="card-actions">
                    <a href="parque-detalle.html?id=${parque.id}" class="btn-view">Ver más</a>
                    <button class="btn-remove" onclick="eliminarParque(${parque.id}, '${tipo}')">
                        ${actionBtn}
                    </button>
                </div>
            </div>
        `;
        grid.appendChild(card);
    });
}
