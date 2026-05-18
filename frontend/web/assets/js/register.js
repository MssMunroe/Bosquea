const fileInput = document.getElementById('icono-file');
const dropZoneText = document.querySelector('.drop-zone-content span');

if (fileInput) {
    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            const archivo = fileInput.files[0];
            
            // Validación de tamaño del archivo
            if (archivo.size > 2 * 1024 * 1024) {
                alert("La imagen es demasiado grande (máx 2MB)");
                fileInput.value = ""; // Limpia el input
                dropZoneText.innerText = "Haz clic para subir o arrastra una imagen de perfil";
            } else {
                // Muestra el nombre del archivo seleccionado en la interfaz
                dropZoneText.innerText = `Seleccionado: ${archivo.name}`;
            }
        }
    });
}

//FORMULARIO
const formRegistro = document.getElementById('registerForm');

if (formRegistro) {
    formRegistro.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Comprobar que las contraseñas coinciden
        const pass = formRegistro.querySelector('input[name="password"]').value;
        const confirmPass = formRegistro.querySelector('input[name="confirm_password"]').value;

        if (pass !== confirmPass) {
            alert("Las contraseñas no coinciden");
            return;
        }

        // Captura automática inicial
        const formData = new FormData(formRegistro); 

        formData.append('contra', pass);
        
        formData.delete('password');
        formData.delete('confirm_password');

        try {
            const res = await fetch('https://bosquea-backend.onrender.com/api/auth/register', {
                method: 'POST',
                body: formData 
            });
            
            const data = await res.json();

            if (res.ok) {
                alert("¡Usuario creado con éxito!");
                window.location.replace("../index.html"); 
            } else {
                alert(data.error || "Error en los datos introducidos");
            }
        } catch (err) { 
            console.error("Error detectado en la petición:", err);
            alert("Error crítico: El servidor no responde."); 
        }
    });
}

// MOSTRAR / OCULTAR CONTRASEÑA
function gestionarVisibilidadPassword() {
    document.querySelectorAll('.eye-icon').forEach(icon => {
        icon.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input');
            
            if (input.type === "password") {
                input.type = "text";
                this.classList.remove('fa-eye-slash');
                this.classList.add('fa-eye');
            } else {
                input.type = "password";
                this.classList.remove('fa-eye');
                this.classList.add('fa-eye-slash');
            }
        });
    });
}

// Inicialización
document.addEventListener("DOMContentLoaded", () => {
    gestionarVisibilidadPassword();
});