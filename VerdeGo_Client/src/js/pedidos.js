import { showModal } from './modalBootstrap.js';

const URL_API_LISTAR = "http://localhost:8080/VerdeGo_Server/api/pedidos/listar";
const URL_API_CANCELAR = "http://localhost:8080/VerdeGo_Server/api/pedidos/cancelar";

document.addEventListener("DOMContentLoaded", async () => {
    verificarAccesoCliente();
    await cargarPedidos();
    setupFiltros();
});

// 1. Verificar seguridad (Solo clientes)
function verificarAccesoCliente() {
    const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
    if (!usuario || (usuario.rol && usuario.rol.idRol !== 2) && usuario.idRol !== 2) {
        window.location.href = "/index.html";
    }
}

// 2. Cargar pedidos desde la Base de Datos
async function cargarPedidos() {
    const container = document.getElementById("orders-container");
    const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));

    try {
        const res = await fetch(`${URL_API_LISTAR}?idUsuario=${usuario.idUsuario}`);
        
        if (!res.ok) throw new Error("Error al conectar con el servidor");
        
        const pedidos = await res.json();

        if (pedidos.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5 bg-white rounded-4 border shadow-sm">
                    <i class="bi bi-bag-x display-1 text-muted opacity-25"></i>
                    <h4 class="mt-3 text-verdego-dark fw-bold">No tienes pedidos aún</h4>
                    <p class="text-muted">¡Anímate a probar nuestros productos frescos!</p>
                    <a href="/productos.html" class="btn btn-verdego mt-2 px-4 py-2 rounded-pill shadow-sm">
                        <i class="bi bi-shop me-2"></i>Ir a la tienda
                    </a>
                </div>`;
            return;
        }

        renderizarLista(pedidos);

    } catch (error) {
        console.error(error);
        container.innerHTML = `
            <div class="alert alert-danger d-flex align-items-center rounded-3 shadow-sm" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2 fs-4"></i>
                <div>
                    <strong>Error:</strong> No se pudieron cargar tus pedidos. Inténtalo más tarde.
                </div>
            </div>`;
    }
}

// 3. Renderizar la lista de tarjetas
function renderizarLista(pedidos) {
    const container = document.getElementById("orders-container");
    container.innerHTML = pedidos.map(pedido => crearTarjetaPedido(pedido)).join('');
}

// 4. Generar HTML de cada tarjeta
function crearTarjetaPedido(p) {
    const puntosGanados = p.puntosGanados || 0; 
    const puntosGastados = p.puntosUsados || 0;
    
    let htmlDescuento = '';
    
    const descuento = p.descuentoAplicado || 0;
    const nombreCupon = p.nombreCupon || 'CUPÓN';

    if (descuento > 0) {
        htmlDescuento = `
            <div class="d-flex justify-content-end align-items-center text-danger small mt-1">
                <span class="badge bg-danger-subtle text-danger border border-danger-subtle me-2">
                    <i class="bi bi-tag-fill me-1"></i>${nombreCupon}
                </span>
                <span>Ahorrado: -${descuento.toFixed(2)}€</span>
            </div>
        `;
    }

    let badgeClass = "bg-secondary text-white";
    let iconClass = "bi-circle";
    const estado = p.estado ? p.estado.toUpperCase() : "DESCONOCIDO";

    switch(estado) {
        case 'PENDIENTE': badgeClass = "status-pendiente"; iconClass = "bi-hourglass-split"; break;
        case 'ENVIADO': badgeClass = "status-enviado"; iconClass = "bi-truck"; break;
        case 'ENTREGADO': badgeClass = "status-entregado"; iconClass = "bi-check-circle-fill"; break;
        case 'CANCELADO': badgeClass = "status-cancelado"; iconClass = "bi-x-circle-fill"; break;
    }

    const btnCancelar = (estado === 'PENDIENTE') 
        ? `<button onclick="window.logicPedidos.cancelar(${p.idPedido})" class="btn btn-outline-danger btn-sm rounded-pill px-3 hover-shadow transition">
             <i class="bi bi-x-lg me-1"></i> Cancelar Pedido
           </button>` 
        : '';

    const productosHtml = p.detalles && p.detalles.length > 0 ? p.detalles.map(d => {
        const prod = d.producto;
        return `
            <div class="d-flex align-items-center gap-3 bg-white p-2 rounded-3 border mb-2 transition-hover">
                <img src="${prod.imagenUrl || 'https://via.placeholder.com/60?text=IMG'}" class="rounded-3 border" style="width: 50px; height: 50px; object-fit: cover;">
                <div class="flex-grow-1 min-w-0">
                    <div class="fw-bold text-dark text-truncate">${prod.nombre}</div>
                    <div class="small text-muted">
                        ${d.cantidad} ${prod.unidadMedida} x ${d.precioUnitario.toFixed(2)}€
                    </div>
                </div>
                <div class="fw-bold text-verdego-medium text-nowrap">
                    ${(d.cantidad * d.precioUnitario).toFixed(2)}€
                </div>
            </div>
        `;
    }).join('') : '<div class="text-muted small fst-italic">Detalles no disponibles</div>';

    const fechaObj = new Date(p.fechaPedido || p.fecha); 
    const fechaStr = fechaObj.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
    const horaStr = fechaObj.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });

    return `
    <div class="order-card p-4 mb-4 filter-item bg-white" data-status="${estado}">
        <div class="d-flex flex-column flex-md-row justify-content-between gap-3 align-items-start align-items-md-center">
            
            <div class="d-flex flex-column gap-1">
                <div class="d-flex align-items-center gap-3 flex-wrap">
                    <h5 class="fw-bold text-verdego-dark mb-0">Pedido #${p.idPedido}</h5>
                    <span class="badge-status ${badgeClass} shadow-sm">
                        <i class="bi ${iconClass}"></i> ${estado}
                    </span>
                </div>
                <div class="text-muted small">
                    <i class="bi bi-calendar3 me-1"></i> ${fechaStr} 
                    <span class="mx-1">&bull;</span> 
                    <i class="bi bi-clock me-1"></i> ${horaStr}
                </div>
            </div>

            <div class="text-md-end d-flex flex-column align-items-md-end">
                <div class="fs-5 fw-bold text-verdego-medium">${p.total.toFixed(2)}€</div>
                ${htmlDescuento}
                <div class="small text-muted mt-1">${p.detalles ? p.detalles.length : 0} artículos</div>
            </div>

            <button class="btn btn-toggle-details shadow-sm" type="button" data-bs-toggle="collapse" data-bs-target="#detalle-${p.idPedido}">
                Ver detalles <i class="bi bi-chevron-down ms-1"></i>
            </button>
        </div>

        <div class="collapse mt-3" id="detalle-${p.idPedido}">
            <div class="border-top pt-4 mt-2">
                <div class="row g-4">
                    <div class="col-lg-8">
                        <div class="order-details-bg h-100 p-3">
                            <h6 class="fw-bold text-verdego-dark mb-3 border-bottom pb-2">
                                <i class="bi bi-basket me-2"></i>Productos
                            </h6>
                            <div class="d-flex flex-column gap-1">
                                ${productosHtml}
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="d-flex flex-column gap-3 h-100">
                            <div class="order-details-bg p-3">
                                <h6 class="fw-bold text-verdego-dark mb-2"><i class="bi bi-geo-alt-fill me-2"></i>Dirección de Envío</h6>
                                <p class="mb-0 text-secondary small lh-sm">
                                    ${p.dirDireccion || 'Sin dirección'}<br>
                                    ${p.dirCiudad || ''} ${p.dirCp ? '('+p.dirCp+')' : ''}<br>
                                    ${p.dirPais || 'España'}
                                </p>
                            </div>

                            <div class="order-details-bg p-3">
                                <h6 class="fw-bold text-verdego-dark mb-2"><i class="bi bi-gift-fill me-2"></i>Fidelización</h6>
                                ${puntosGastados > 0 
                                    ? `<div class="d-flex justify-content-between text-danger small mb-1">
                                         <span>Puntos usados:</span> <span class="fw-bold">-${puntosGastados}</span>
                                       </div>` 
                                    : ''}
                                <div class="d-flex justify-content-between text-success fw-bold small">
                                    <span>Puntos ganados:</span> <span>+${puntosGanados}</span>
                                </div>
                            </div>

                            <div class="mt-auto text-end pt-2">
                                ${btnCancelar}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `;
}

// 5. Configurar Filtros
function setupFiltros() {
    const botones = document.querySelectorAll('.btn-filter');
    botones.forEach(btn => {
        btn.addEventListener('click', () => {
            botones.forEach(b => b.classList.remove('btn-filter-active'));
            btn.classList.add('btn-filter-active');

            const filtro = btn.dataset.filter;
            const items = document.querySelectorAll('.filter-item');

            items.forEach(item => {
                if (filtro === 'TODOS' || item.dataset.status === filtro) {
                    item.classList.remove('d-none');
                    item.style.animation = 'fadeIn 0.3s ease';
                } else {
                    item.classList.add('d-none');
                }
            });
        });
    });
}

// 6. Exponer lógica al objeto window para el onclick del HTML
window.logicPedidos = {
    cancelar: (idPedido) => {
        showModal(
            'warning',                            
            '¿Cancelar pedido?',                 
            'Vas a cancelar el pedido #' + idPedido + '. Esta acción no se puede deshacer.', 
            async () => {                          
                try {
                    const res = await fetch(URL_API_CANCELAR, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ idPedido: idPedido })
                    });
                    const data = await res.json();
                    if (data.status === 'ok') {
                        showModal('success', 'Pedido Cancelado', 'El pedido se ha cancelado correctamente.', () => {
                            window.location.reload(); 
                        });
                    } else {
                        showModal('error', 'Error', data.error || 'No se pudo cancelar.');
                    }
                } catch (e) {
                    showModal('error', 'Error', 'Error de conexión.');
                }
            },
            true,                 
            'Sí, cancelar pedido' 
        );
    }
};