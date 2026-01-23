let allProducts = [];
let activeCategory = 'all';

document.addEventListener("DOMContentLoaded", () => {
    fetchProductos();
    
    const searchInput = document.getElementById("global-search");
    if(searchInput){
        searchInput.addEventListener("input", (e) => {
            filtrarProductos(activeCategory, e.target.value);
        });
    }
});

async function fetchProductos() {
    try {
        const response = await fetch("http://localhost:8080/VerdeGo_Server/api/productos");
        if (!response.ok) throw new Error("Error en red");
        
        allProducts = await response.json();
        setupFiltros(allProducts);
        renderProductos(allProducts);
        
    } catch (error) {
        console.error("Error:", error);
        document.getElementById("products-grid").innerHTML = `
            <div class="col-12 text-center text-danger py-5">
                <i class="bi bi-exclamation-triangle fs-1"></i>
                <p class="mt-2">No se pudieron cargar los productos. Asegúrate de que el servidor está encendido.</p>
            </div>`;
    }
}

function setupFiltros(productos) {
    const categorias = ['Todos', ...new Set(productos.map(p => p.categoria ? p.categoria.nombre : 'General'))];
    
    const container = document.getElementById("category-filters");
    if(!container) return;
    
    container.innerHTML = '';

    categorias.forEach(cat => {
        const btn = document.createElement("button");
        const isAll = cat === 'Todos';
        const value = isAll ? 'all' : cat;
        
        const baseClasses = "btn rounded-pill px-4 py-2 fw-medium";
        
        if (isAll) {
            btn.className = `${baseClasses} btn-filter-active`;
        } else {
            btn.className = `${baseClasses} btn-filter`;
        }
        
        btn.textContent = cat;
        
        btn.addEventListener("click", () => {
            document.querySelectorAll("#category-filters button").forEach(b => {
                b.className = `${baseClasses} btn-filter`;
            });
            
            btn.className = `${baseClasses} btn-filter-active`;
            
            activeCategory = value;
            filtrarProductos(value, document.getElementById("global-search")?.value || "");
        });
        
        container.appendChild(btn);
    });
}

function filtrarProductos(categoria, texto) {
    const busqueda = texto.toLowerCase();
    const filtrados = allProducts.filter(p => {
        const catNombre = p.categoria ? p.categoria.nombre : 'General';
        const matchCat = categoria === 'all' || catNombre === categoria;
        const matchText = p.nombre.toLowerCase().includes(busqueda);
        return matchCat && matchText;
    });
    renderProductos(filtrados);
}

function renderProductos(productos) {
    const grid = document.getElementById("products-grid");
    if(!grid) return;
    
    if (productos.length === 0) {
        grid.innerHTML = '<div class="col-12 text-center py-5 text-muted"><p>No se encontraron productos.</p></div>';
        return;
    }

    grid.innerHTML = productos.map(p => {
        const precio = p.precio.toFixed(2);
        const imagen = p.imagenUrl || 'https://via.placeholder.com/400x300?text=Sin+Imagen';
        const unidad = p.unidadMedida === 'kg' ? 'kg' : 'ud';
        const catNombre = p.categoria ? p.categoria.nombre : 'General';

        return `
        <div class="col">
            <div class="card h-100 product-card d-flex flex-column">
                
                <div class="product-img-wrapper cursor-pointer">
                    <img src="${imagen}" alt="${p.nombre}">
                </div>

                <div class="card-body p-4 d-flex flex-column flex-grow-1 gap-2">
                    
                    <div class="d-flex flex-column gap-1">
                        <h5 class="fw-bold text-verdego-dark mb-0 cursor-pointer text-truncate" 
                            style="cursor: pointer;"
                            onmouseover="this.style.color='#167a4b'" 
                            onmouseout="this.style.color='var(--verdego-dark)'">
                            ${p.nombre}
                        </h5>
                        <span class="small text-secondary">${catNombre}</span>
                    </div>

                    <div class="mt-auto pt-2">
                        <div class="d-flex align-items-baseline gap-1 mb-3">
                            <span class="fs-4 fw-bold" style="color: #167a4b;">${precio}€</span>
                            <span class="small text-secondary">/ ${unidad}</span>
                        </div>

                        <button onclick="window.cartLogic.addItem({idProducto: ${p.idProducto}, nombre: '${p.nombre}', precio: ${p.precio}, unidadMedida: '${p.unidadMedida}', imagenUrl: '${imagen}'})" 
                                class="btn w-100 py-2 d-flex align-items-center justify-content-center gap-2 text-white fw-medium shadow-sm"
                                style="background-color: #167a4b; border-radius: 0.75rem; transition: background-color 0.2s;"
                                onmouseover="this.style.backgroundColor='#0b4d2d'"
                                onmouseout="this.style.backgroundColor='#167a4b'">
                            <i class="bi bi-cart-plus"></i> Añadir al carrito
                        </button>
                    </div>
                </div>
            </div>
        </div>`;
    }).join('');
}

window.agregarAlCarrito = function(id) {
    const producto = allProducts.find(p => p.idProducto === id);
    if (producto && window.cartLogic) {
        window.cartLogic.addItem(producto);
    } else {
        console.error("No se pudo añadir. Producto no encontrado o cartLogic no cargado.");
    }
};