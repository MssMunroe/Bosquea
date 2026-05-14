// 1. Mostrar el nombre del archivo seleccionado y validar tamaño al elegirlo
const fileInput = document.getElementById('icono-file');
const dropZoneText = document.querySelector('.drop-zone-content span');

fileInput.addEventListener('change', () => {
    if (fileInput.files.length > 0) {
        const archivo = fileInput.files[0];
        const limiteMB = 2;
        const limiteBytes = limiteMB * 1024 * 1024;

        if (archivo.size > limiteBytes) {
            alert(`La imagen es demasiado grande. El límite son ${limiteMB}MB.`);
            fileInput.value = ""; // Limpiamos el input para que no intente subirlo
            dropZoneText.innerText = "Arrastra el archivo";
            dropZoneText.style.color = "red";
        } else {
            dropZoneText.innerText = `Archivo seleccionado: ${archivo.name}`;
            dropZoneText.style.color = "#4b6a32";
        }
    }
});

// 2. Evento de envío del formulario
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    // Validar contraseñas iguales
    if (form.password.value !== form.confirm_password.value) {
        alert("Las contraseñas no coinciden");
        return;
    }

    const formData = new FormData();
    formData.append('nombre', form.nombre.value);
    formData.append('nickname', form.nickname.value);
    formData.append('email', form.email.value);
    formData.append('contra', form.password.value);
    formData.append('dni', form.dni.value);
    formData.append('codigo_postal', form.cp.value);

    if (fileInput.files[0]) {
        formData.append('icono', fileInput.files[0]);
    }

    try {
        const response = await fetch('http://127.0.0.1:5000/api/auth/register', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (response.ok) {
            alert("¡Registro completado!");
            window.location.href = "perfil.html";
        } else {
            alert(data.error);
        }
    } catch (error) {
        console.error("Error:", error);
    }
});

/**
 * Alterna entre mostrar/ocultar contraseña en los inputs de tipo password
 */
function gestionarVisibilidadPassword() {
    document.querySelectorAll('.eye-icon').forEach(icon => {
        icon.addEventListener('click', function () {
            const input = this.parentElement.querySelector('input');
            const isPassword = input.type === "password";
            input.type = isPassword ? "text" : "password";
            this.classList.toggle('fa-eye', isPassword);
            this.classList.toggle('fa-eye-slash', !isPassword);
        });
    });
}