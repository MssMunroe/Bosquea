// 1. Manejo del archivo (Dropzone)
const fileInput = document.getElementById('icono-file');
const dropZoneText = document.querySelector('.drop-zone-content span');

if (fileInput) {
    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            const archivo = fileInput.files[0];
            if (archivo.size > 2 * 1024 * 1024) {
                alert("La imagen es demasiado grande (máx 2MB)");
                fileInput.value = "";
            } else {
                dropZoneText.innerText = `Seleccionado: ${archivo.name}`;
            }
        }
    });
}

// 2. Envío del Formulario
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;

    // Validación de seguridad básica
    if (form.password.value !== form.confirm_password.value) {
        alert("Las contraseñas no coinciden");
        return;
    }

    // 1. Construcción manual y limpia del FormData
    const formData = new FormData();
    formData.append('nombre', document.getElementById('nombre').value);
    formData.append('nickname', document.getElementById('nickname').value);
    formData.append('email', document.getElementById('email').value);
    formData.append('contra', form.password.value);
    formData.append('dni', document.getElementById('dni').value);
    formData.append('codigo_postal', document.getElementById('cp').value);

    // El archivo del icono
    const fileInput = document.getElementById('icono-file');
    if (fileInput && fileInput.files[0]) {
        formData.append('icono', fileInput.files[0]);
    }

    try {
        console.log("Enviando registro..."); // Para ver en consola F12
        const response = await fetch('http://127.0.0.1:5000/api/auth/register', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (response.ok) {
            alert("¡Usuario creado con éxito!");
            // 2. REDIRECCIÓN ABSOLUTA (Prueba con la ruta completa si ../ falla)
            window.location.replace("../index.html"); 
        } else {
            // Si el servidor responde con error (ej: email duplicado)
            alert(data.error || "Error en los datos");
        }
    } catch (error) {
        // 3. Este bloque atrapa fallos de red o errores de sintaxis
        console.error("Error detectado:", error);
        alert("Error crítico: El servidor no responde.");
    }
});
