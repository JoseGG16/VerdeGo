
document.addEventListener("DOMContentLoaded", () => {
    fetchDestacados();
});

async function fetchDestacados() {
    const contenedor = document.getElementById('destacados-container');
    if(!contenedor) return;

    try {
        const response = await fetch("http://localhost:8080/VerdeGo_Server/api/productos/destacados");
        if (!response.ok) throw new Error("Error fetching");
        const productos = await response.json();

        if (productos.length === 0) {
            contenedor.innerHTML = '<p class="text-muted text-center w-100">No hay productos destacados.</p>';
            return;
        }

        contenedor.innerHTML = productos.map(p => {
            const precio = p.precio.toFixed(2);
            const imagen = p.imagenUrl || 'https://via.placeholder.com/400x300?text=Sin+Imagen';
            const unidad = p.unidadMedida === 'kg' ? 'kg' : 'ud';
            const catNombre = p.categoria ? p.categoria.nombre : 'General';

            return `
            <div class="col">
                <div class="card h-100 product-card d-flex flex-column border-0 shadow-sm rounded-4 overflow-hidden">
                    
                    <div class="product-img-wrapper cursor-pointer position-relative" style="height: 200px; overflow: hidden;" onclick="window.location.href='/productos.html'">
                        <img src="${imagen}" alt="${p.nombre}" class="w-100 h-100 object-fit-cover hover-scale transition-transform duration-300">
                    </div>

                    <div class="card-body p-4 d-flex flex-column flex-grow-1 gap-2">
                        <div class="d-flex flex-column gap-1">
                            <h5 class="fw-bold text-verdego-dark mb-0 text-truncate">${p.nombre}</h5>
                            <span class="small text-secondary">${catNombre}</span>
                        </div>

                        <div class="mt-auto pt-2">
                            <div class="d-flex align-items-baseline gap-1 mb-3">
                                <span class="fs-4 fw-bold" style="color: #167a4b;">${precio}€</span>
                                <span class="small text-secondary">/ ${unidad}</span>
                            </div>

                            <button onclick="window.cartLogic.addItem({idProducto: ${p.idProducto}, nombre: '${p.nombre}', precio: ${p.precio}, unidadMedida: '${p.unidadMedida}', imagenUrl: '${imagen}'})" 
                                    class="btn w-100 py-2 d-flex align-items-center justify-content-center gap-2 text-white fw-medium shadow-sm"
                                    style="background-color: #167a4b; border-radius: 0.75rem;">
                                <i class="bi bi-cart-plus"></i> Añadir
                            </button>
                        </div>
                    </div>
                </div>
            </div>`;
        }).join('');

    } catch (error) {
        console.error("Error cargando destacados:", error);
        contenedor.innerHTML = `<div class="col-12 text-center text-danger">Error al cargar productos. Revisa que el servidor Java esté encendido.</div>`;
    }
}