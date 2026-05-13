document.addEventListener("DOMContentLoaded", () => {
    fetchParque();
});

async function fetchParque() {

    // Sacamos el id de la URL
    const params = new URLSearchParams(window.location.search);

    const id = params.get("id");

    // Contenedor principal
    const contenedor = document.getElementById("parque-detail");

    try {

        // Petición a la API
        const response = await fetch(`http://127.0.0.1:5000/api/parques/${id}`);

        // Convertimos a JSON
        const parque = await response.json();

        // Rellenamos elementos HTML
        renderParque(parque);

    } catch(error) {

        console.error("Error cargando parque:", error);

        contenedor.innerHTML = `
            <p class="error">
                Error al cargar el parque.
            </p>
        `;
    }
}

function renderParque(parque) {

    // Nombre
    document.getElementById("park-name").innerText =
        parque.nombre;

    // Imagen
    document.getElementById("park-image").src =
        parque.img || "../assets/img/default.jpg";

    document.getElementById("park-image").alt =
        parque.nombre;

    // Descripción
    document.getElementById("park-description").innerText =
        parque.descripcion;

    // Ubicación
    document.getElementById("data-location").innerText =
        parque.ubicacion;

    // Lista animales
    const listaAnimales =
        document.getElementById("animals-list");

    listaAnimales.innerHTML = "";

    // Recorremos animales
    parque.animales.forEach(animal => {

        const li = document.createElement("li");

        li.innerText = animal.nombre;

        listaAnimales.appendChild(li);

    });

}