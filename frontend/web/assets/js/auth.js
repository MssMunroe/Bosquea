/**
 * Gestión de usuarios: Login, Registro y UI de formularios
 */

// Mostrar/Ocultar contraseña (el ojito)
function gestionarVisibilidadPassword() {
    document.querySelectorAll('.eye-icon').forEach(icon => {
        icon.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input');
            input.type = input.type === "password" ? "text" : "password";
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });
    });
}

// Registro con subida de imagen
const formRegistro = document.getElementById('registerForm');
if (formRegistro) {
    formRegistro.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(formRegistro); // FormData pilla todo auto si los 'name' coinciden

        try {
            const res = await fetch('http://127.0.0.1:5000/api/auth/register', {
                method: 'POST',
                body: formData
            });
            if (res.ok) {
                alert("¡Cuenta creada!");
                window.location.href = "../index.html";
            }
        } catch (err) { alert("Error de conexión"); }
    });
}