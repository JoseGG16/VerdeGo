import { showModal } from './modalBootstrap.js';

let estado = {
    items: JSON.parse(localStorage.getItem("carritoVerdeGo")) || [],
    puntosUsuario: 0,
    puntosAplicados: 0,
    codigoCupon: '',
    cuponAplicado: null,
    direccionesUsuario: [] 
};

const VALOR_PUNTO = 1;
const PUNTOS_MINIMOS_CANJE = 1000;
const DESCUENTO_PUNTOS_FIJO = 5.00;

document.addEventListener("DOMContentLoaded", () => {
    cargarPuntosUsuario();
    actualizarBadge();
    configurarContenedorScroll(); 
    renderizarCarrito();
});

function cargarPuntosUsuario() {
    const usuarioJSON = localStorage.getItem("usuarioVerdeGo");
    if (usuarioJSON) {
        const usuario = JSON.parse(usuarioJSON);
        estado.puntosUsuario = usuario.puntosFidelizacion || 0;
    } else {
        estado.puntosUsuario = 0;
    }
}

function guardarEstado() {
    localStorage.setItem("carritoVerdeGo", JSON.stringify(estado.items));
    actualizarBadge();
    renderizarCarrito();
}

function actualizarBadge() {
    const badge = document.getElementById("cart-count");
    if (badge) {
        const totalItems = estado.items.reduce((acc, item) => acc + item.cantidad, 0);
        badge.textContent = Math.ceil(totalItems); 
    }
}

function configurarContenedorScroll() {
    const contenedor = document.getElementById('cart-items-container');
    if (contenedor) {
        const parent = contenedor.parentElement; 
        parent.style.padding = '0'; 
        
        contenedor.style.height = '100%';
        contenedor.style.overflowY = 'auto'; 
        contenedor.style.padding = '1.5rem'; 
        contenedor.style.display = 'flex';
        contenedor.style.flexDirection = 'column';
        contenedor.style.gap = '1.5rem'; 
    }
    ['cart-points-section', 'cart-coupon-section', 'cart-summary-section'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = 'none';
    });
}

function renderizarCarrito() {
    const contenedor = document.getElementById('cart-items-container');
    if (!contenedor) return;

    if (estado.items.length === 0) {
        contenedor.innerHTML = `
            <div class="h-100 d-flex flex-column justify-content-center align-items-center text-muted">
                <i class="bi bi-cart-x display-1 opacity-25 mb-3"></i>
                <p class="fw-medium">Tu carrito está vacío</p>
                <button class="btn btn-outline-success btn-sm mt-3" data-bs-dismiss="offcanvas">Empezar a comprar</button>
            </div>`;
        return;
    }

    const htmlProductos = `<div class="d-flex flex-column gap-3">` + estado.items.map(item => {
        const unidad = (item.unit || item.unidadMedida || '').toLowerCase();
        const esKilo = unidad === 'kg';
        const step = esKilo ? 0.25 : 1;
        const cantidadVisual = esKilo ? item.cantidad.toFixed(2) : item.cantidad;

        return `
        <div class="d-flex gap-3 p-3 rounded-4" style="background-color: #f6f9f7;">
            <div class="flex-shrink-0" style="width: 80px; height: 80px;">
                <img src="${item.image || item.imagenUrl || 'https://via.placeholder.com/150'}" alt="${item.name}" class="w-100 h-100 object-fit-cover rounded-3">
            </div>
            <div class="flex-grow-1 min-w-0">
                <h6 class="mb-1 text-truncate fw-bold" style="color: #0b4d2d;">${item.name || item.nombre}</h6>
                <p class="mb-2 small fw-bold" style="color: #167a4b;">
                    ${(item.price || item.precio).toFixed(2)}€ <span class="text-secondary fw-normal">/ ${unidad}</span>
                </p>
                <div class="d-flex align-items-center gap-2">
                    <button onclick="window.cartLogic.updateQuantity(${item.id || item.idProducto}, ${item.cantidad - step})" class="btn btn-white border-0 shadow-sm d-flex align-items-center justify-content-center p-0" style="width: 32px; height: 32px; border-radius: 8px;"><i class="bi bi-dash" style="color: #167a4b;"></i></button>
                    <span class="fw-bold text-center" style="width: 40px;">${cantidadVisual}</span>
                    <button onclick="window.cartLogic.updateQuantity(${item.id || item.idProducto}, ${item.cantidad + step})" class="btn btn-white border-0 shadow-sm d-flex align-items-center justify-content-center p-0" style="width: 32px; height: 32px; border-radius: 8px;"><i class="bi bi-plus-lg" style="color: #167a4b;"></i></button>
                    <button onclick="window.cartLogic.removeItem(${item.id || item.idProducto})" class="btn ms-auto p-2 text-danger"><i class="bi bi-trash"></i></button>
                </div>
            </div>
        </div>
    `}).join('') + `</div>`;

    const htmlPuntos = getHtmlPuntos();
    const htmlCupones = getHtmlCupones();
    const htmlResumen = getHtmlResumen();

    contenedor.innerHTML = `${htmlProductos} <div class="mt-2 pt-3 border-top">${htmlPuntos}</div> <div class="mt-0">${htmlCupones}</div> <div class="mt-2">${htmlResumen}</div>`;
}

function getHtmlPuntos() {
    if (!localStorage.getItem("usuarioVerdeGo")) return `<div class="p-3 mb-3 text-center bg-light rounded-3 border text-muted small"><a href="/login.html" class="fw-bold text-success text-decoration-none">Inicia sesión</a> para ver tus puntos.</div>`;
    const subtotal = estado.items.reduce((sum, item) => sum + (item.price || item.precio) * item.cantidad, 0);
    const porcentaje = Math.min(100, (estado.puntosUsuario / PUNTOS_MINIMOS_CANJE) * 100);
    const tieneSuficientes = estado.puntosUsuario >= PUNTOS_MINIMOS_CANJE;
    const cardStyle = `background: linear-gradient(135deg, #e6f4ec 0%, #f6f9f7 100%); border: 2px solid rgba(22, 122, 75, 0.2); border-radius: 0.75rem; padding: 1.25rem;`;

    let html = `<div style="${cardStyle}" class="mb-3"><div class="d-flex align-items-center gap-2 mb-3"><i class="bi bi-gift-fill" style="color: #167a4b;"></i><h6 class="mb-0 fw-bold" style="color: #0b4d2d;">Puntos de fidelización</h6></div>`;
    if (estado.puntosAplicados > 0) {
        html += `<div class="bg-white rounded-3 p-3 d-flex justify-content-between align-items-center shadow-sm"><div><p class="mb-0 small fw-bold" style="color: #0b4d2d;">Puntos aplicados: ${estado.puntosAplicados}</p><p class="mb-0 text-secondary" style="font-size: 0.75rem;">Descuento: -${DESCUENTO_PUNTOS_FIJO.toFixed(2)}€</p></div><button onclick="window.cartLogic.removePoints()" class="btn btn-link p-0 text-decoration-none small fw-bold text-danger">Quitar</button></div>`;
    } else {
        html += `<div class="bg-white rounded-3 p-3 mb-3 shadow-sm"><div class="d-flex justify-content-between align-items-start mb-1"><span class="small text-secondary">Acumulados</span><span class="badge bg-success bg-opacity-10 text-success rounded-pill">+${Math.floor(subtotal)} nuevos</span></div><p class="fs-4 fw-bold mb-1" style="color: #167a4b;">${estado.puntosUsuario} pts</p></div>`;
        html += tieneSuficientes ? `<button onclick="window.cartLogic.applyPoints()" class="btn w-100 py-2 fw-medium shadow-sm d-flex align-items-center justify-content-center gap-2" style="background-color: #167a4b; color: white; border-radius: 0.5rem;"><i class="bi bi-gift"></i> Usar todos mis puntos</button>` : `<button disabled class="btn w-100 py-2 fw-medium shadow-sm text-secondary" style="background-color: #e5e7eb; border-radius: 0.5rem; cursor: not-allowed;">Necesitas 1000 puntos para canjear</button>`;
    }
    return html + `</div>`;
}

function getHtmlCupones() {
    const cardStyle = `background: linear-gradient(135deg, #e6f4ec 0%, #f6f9f7 100%); border: 2px solid rgba(22, 122, 75, 0.2); border-radius: 0.75rem; padding: 1.25rem;`;
    let html = `<div style="${cardStyle}" class="mb-3"><div class="d-flex align-items-center gap-2 mb-3"><i class="bi bi-tag-fill" style="color: #167a4b;"></i><h6 class="mb-0 fw-bold" style="color: #0b4d2d;">Cupón de descuento</h6></div>`;
    if (estado.cuponAplicado) {
        html += `<div class="bg-white rounded-3 p-3 d-flex justify-content-between align-items-center shadow-sm"><div><p class="mb-0 small fw-bold" style="color: #0b4d2d;">Cupón: ${estado.cuponAplicado.codigo}</p><p class="mb-0 text-secondary" style="font-size: 0.75rem;">Descuento: -${estado.cuponAplicado.descuento.toFixed(2)}€</p></div><button onclick="window.cartLogic.removeCoupon()" class="btn btn-link p-0 text-decoration-none small fw-bold text-danger">Quitar</button></div>`;
    } else {
        html += `<div class="d-flex gap-2 mb-2"><input type="text" class="form-control shadow-sm border-0" placeholder="Introduce tu código" style="border: 1px solid #e5e7eb;" value="${estado.codigoCupon}" oninput="window.cartLogic.updateCouponInput(this.value)"><button onclick="window.cartLogic.applyCoupon()" class="btn text-white shadow-sm fw-medium px-4" style="background-color: #167a4b;">Aplicar</button></div><p class="mb-0 text-secondary" style="font-size: 0.75rem;">Ej: BIENVENIDA10, VERANO20</p>`;
    }
    return html + `</div>`;
}

function getHtmlResumen() {
    const subtotal = estado.items.reduce((sum, item) => sum + (item.price || item.precio) * item.cantidad, 0);
    const descPuntos = estado.puntosAplicados > 0 ? DESCUENTO_PUNTOS_FIJO : 0;
    const descCupon = estado.cuponAplicado ? estado.cuponAplicado.descuento : 0;
    const base = Math.max(0, subtotal - descPuntos - descCupon);
    const envio = base > 50 ? 0 : 4.99;
    const iva = base * 0.10; 
    const total = base + envio + iva;

    return `
        <div class="mb-3 text-secondary" style="font-size: 0.9rem;">
            <div class="d-flex justify-content-between mb-1"><span>Subtotal</span><span>${subtotal.toFixed(2)}€</span></div>
            ${descPuntos > 0 ? `<div class="d-flex justify-content-between text-success"><span>Puntos (-1000)</span><span>-${descPuntos.toFixed(2)}€</span></div>` : ''}
            ${descCupon > 0 ? `<div class="d-flex justify-content-between text-success"><span>Cupón</span><span>-${descCupon.toFixed(2)}€</span></div>` : ''}
            <div class="d-flex justify-content-between"><span>Envío</span><span>${envio.toFixed(2)}€</span></div>
            <div class="d-flex justify-content-between"><span>IVA (10%)</span><span>${iva.toFixed(2)}€</span></div>
        </div>
        <div class="d-flex justify-content-between align-items-center mb-4 pt-3 border-top">
            <span class="fw-bold fs-5" style="color: #0b4d2d;">Total</span>
            <span class="fw-bold fs-4" style="color: #0b4d2d;">${total.toFixed(2)}€</span>
        </div>
        <button class="btn w-100 py-3 fw-bold shadow-sm text-white" style="background-color: #167a4b; border-radius: 0.75rem;" 
            onmouseover="this.style.backgroundColor='#0b4d2d'" onmouseout="this.style.backgroundColor='#167a4b'"
            onclick="window.cartLogic.prepararCheckout()">
            Finalizar compra
        </button>
    `;
}

window.cartLogic = {
    addItem: (producto) => {
        const id = producto.idProducto || producto.id;
        const existe = estado.items.find(i => (i.id || i.idProducto) === id);
        if (existe) existe.cantidad += 1;
        else estado.items.push({ id: id, idProducto: id, name: producto.nombre, nombre: producto.nombre, price: producto.precio, precio: producto.precio, unit: producto.unidadMedida, unidadMedida: producto.unidadMedida, image: producto.imagenUrl, imagenUrl: producto.imagenUrl, cantidad: 1 });
        guardarEstado();
        const el = document.getElementById('cartDrawer');
        if (el) bootstrap.Offcanvas.getOrCreateInstance(el).show();
    },
    updateQuantity: (id, rawQty) => {
        const item = estado.items.find(i => (i.id || i.idProducto) === id);
        if (!item) return;
        const unidad = (item.unit || item.unidadMedida || '').toLowerCase();
        const esKilo = unidad === 'kg';
        const minimo = esKilo ? 0.25 : 1;
        if (rawQty < minimo) return;
        item.cantidad = Math.round(rawQty * 100) / 100;
        guardarEstado();
    },
    removeItem: (id) => {
        estado.items = estado.items.filter(i => (i.id || i.idProducto) !== id);
        guardarEstado();
    },
    applyPoints: () => {
        if (estado.puntosUsuario >= PUNTOS_MINIMOS_CANJE) { estado.puntosAplicados = PUNTOS_MINIMOS_CANJE; renderizarCarrito(); }
        else showModal('error', 'Puntos insuficientes', `Necesitas ${PUNTOS_MINIMOS_CANJE} puntos.`);
    },
    removePoints: () => { estado.puntosAplicados = 0; renderizarCarrito(); },
    updateCouponInput: (val) => { estado.codigoCupon = val; },
    applyCoupon: async () => {
        try {
            const res = await fetch("http://localhost:8080/VerdeGo_Server/api/pedidos/validar-cupon", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ codigo: estado.codigoCupon }) });
            const data = await res.json();
            if (data.error) showModal('error', 'Error', 'Cupón no válido.');
            else {
                const subtotal = estado.items.reduce((s, i) => s + (i.price||i.precio)*i.cantidad, 0);
                const desc = data.tipo === 'PORCENTAJE' ? subtotal * (data.valor > 1 ? data.valor/100 : data.valor) : data.valor;
                estado.cuponAplicado = { codigo: data.codigo, descuento: desc, tipo: data.tipo, valor: data.valor };
                estado.codigoCupon = ''; renderizarCarrito();
            }
        } catch (e) { console.error(e); }
    },
    removeCoupon: () => { estado.cuponAplicado = null; renderizarCarrito(); },

    prepararCheckout: async () => {
        const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
        if (!usuario) { showModal('info', 'Inicia sesión', 'Necesitas identificarte.', () => window.location.href = "/login.html"); return; }
        if (estado.items.length === 0) return;

        const cartDrawer = document.getElementById('cartDrawer');
        if(cartDrawer) bootstrap.Offcanvas.getInstance(cartDrawer)?.hide();
        const modal = new bootstrap.Modal(document.getElementById('checkoutModal'));
        modal.show();

        const subtotal = estado.items.reduce((s, i) => s + (i.price||i.precio)*i.cantidad, 0);
        const desc = (estado.puntosAplicados>0?5:0) + (estado.cuponAplicado?estado.cuponAplicado.descuento:0);
        const base = Math.max(0, subtotal - desc);
        const envio = base > 50 ? 0 : 4.99;
        const totalFinal = (base * 1.10 + envio).toFixed(2);

        document.getElementById('co-total-display').textContent = totalFinal + "€";

        try {
            const res = await fetch(`http://localhost:8080/VerdeGo_Server/api/direcciones/listar?idUsuario=${usuario.idUsuario}`);
            const direcciones = await res.json();
            estado.direccionesUsuario = direcciones;
            
            const selectDir = document.getElementById("co-direccion-select");
            const divNuevaDir = document.getElementById("co-nueva-direccion");
            
            if (selectDir) {
                if (direcciones.length > 0) {
                    selectDir.innerHTML = direcciones.map(d => `<option value="${d.idDireccion}" ${d.esPrincipal?'selected':''}>${d.alias} - ${d.calle}</option>`).join('') + `<option value="nueva">+ Nueva Dirección</option>`;
                    divNuevaDir.classList.add("d-none");
                    selectDir.onchange = (e) => { e.target.value === "nueva" ? divNuevaDir.classList.remove("d-none") : divNuevaDir.classList.add("d-none"); };
                } else {
                    selectDir.innerHTML = `<option value="nueva" selected>+ Nueva Dirección</option>`;
                    divNuevaDir.classList.remove("d-none");
                }
            }
        } catch(e) { console.error(e); }

        const listaResumen = document.getElementById("co-lista-resumen");
        if(listaResumen) listaResumen.innerHTML = estado.items.map(i => `<div class="d-flex justify-content-between small mb-1"><span>${i.cantidad}x ${i.name||i.nombre}</span><span class="fw-bold">${((i.price||i.precio)*i.cantidad).toFixed(2)}€</span></div>`).join('');

        const containerPayPal = document.getElementById("paypal-checkout-container");
        if(containerPayPal) {
            containerPayPal.innerHTML = ""; 
            
            setTimeout(() => {
                if(window.paypal) {
                    window.paypal.Buttons({
                        style: {
                            layout: 'vertical',
                            color:  'gold', 
                            shape:  'rect',
                            label:  'pay'
                        },
                        createOrder: (data, actions) => {
                            const selectDir = document.getElementById("co-direccion-select");
                            if (selectDir.value === "nueva") {
                                const dir = document.getElementById('co-direccion').value;
                                if(!dir) { alert("Rellena la dirección primero."); return actions.reject(); }
                            }
                            
                            return actions.order.create({
                                purchase_units: [{
                                    description: "Pedido VerdeGo",
                                    amount: { value: totalFinal }
                                }]
                            });
                        },
                        onApprove: (data, actions) => {
                            return actions.order.capture().then(details => {
                                console.log("Pago OK:", details);
                                window.cartLogic.confirmarPedidoBackend(details);
                            });
                        },
                        onError: (err) => {
                            console.error(err);
                            alert("Error en el proceso de pago.");
                        }
                    }).render('#paypal-checkout-container');
                } else {
                    containerPayPal.innerHTML = "<div class='alert alert-danger p-2 small'>No se pudo cargar PayPal. Revisa tu conexión.</div>";
                }
            }, 500);
        }
    },

    confirmarPedidoBackend: async (detallesPago) => {
        const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
        
        const selectDir = document.getElementById("co-direccion-select");
        let direccionEnvio, ciudadEnvio, cpEnvio;

        if (selectDir && selectDir.value !== "nueva") {
            const dirId = parseInt(selectDir.value);
            const dirObj = estado.direccionesUsuario.find(d => d.idDireccion === dirId);
            if(dirObj) { direccionEnvio = dirObj.calle; ciudadEnvio = dirObj.ciudad; cpEnvio = dirObj.codigoPostal; }
        } else {
            direccionEnvio = document.getElementById('co-direccion').value;
            ciudadEnvio = document.getElementById('co-ciudad').value;
            cpEnvio = document.getElementById('co-cp').value;
        }

        const subtotal = estado.items.reduce((s, i) => s + (i.price||i.precio)*i.cantidad, 0);
        const desc = (estado.puntosAplicados>0?5:0) + (estado.cuponAplicado?estado.cuponAplicado.descuento:0);
        const total = (Math.max(0, subtotal - desc) * 1.10 + (subtotal>50?0:4.99));

        try {
            const res = await fetch("http://localhost:8080/VerdeGo_Server/api/pedidos/finalizar", {
                method: "POST", headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    idCliente: usuario.idUsuario,
                    total: total,
                    puntosUsados: estado.puntosAplicados,
                    codigoDescuento: estado.cuponAplicado?.codigo,
                    idCodigoDescuento: estado.cuponAplicado?1:null,
                    direccion: direccionEnvio, ciudad: ciudadEnvio, cp: cpEnvio, pais: "España",
                    idTransaccion: detallesPago.id, 
                    estadoPago: "PAGADO",
                    productos: estado.items.map(i => ({ id: i.id||i.idProducto, cantidad: i.cantidad, precio: i.price||i.precio }))
                })
            });
            const data = await res.json();
            if(data.status === 'ok') {
                bootstrap.Modal.getInstance(document.getElementById('checkoutModal')).hide();
                showModal('success', '¡Pedido Pagado!', `Referencia PayPal: ${detallesPago.id}`, () => {
                    estado.items=[]; estado.puntosAplicados=0; estado.cuponAplicado=null; guardarEstado(); 
                    window.location.href="/pedidos.html";
                });
            } else {
                showModal('error', 'Error', 'Pago realizado pero error al guardar pedido.');
            }
        } catch(e) { console.error(e); }
    }
};