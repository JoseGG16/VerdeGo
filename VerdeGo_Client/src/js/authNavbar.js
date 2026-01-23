import { showModal } from './modalBootstrap.js';

document.addEventListener("DOMContentLoaded", () => {
    actualizarNavbar();
});

export function actualizarNavbar() {
    const usuarioJSON = localStorage.getItem("usuarioVerdeGo");
    const usuario = usuarioJSON ? JSON.parse(usuarioJSON) : null;

    
    const ID_ROL_ADMIN = 1;
    const ID_ROL_EMPLEADO = 3; 

    const esEmpleado = usuario && usuario.rol && (usuario.rol.idRol === ID_ROL_ADMIN || usuario.rol.idRol === ID_ROL_EMPLEADO);
    
    const rutaActual = window.location.pathname;

    const renderizarEnlace = (href, iconClass, label) => {
        let isActive = false;
        if (href === '/index.html') {
            isActive = rutaActual === '/' || rutaActual.endsWith('/index.html');
        } else {
            isActive = rutaActual.includes(href);
        }

        const containerBase = "position-relative d-flex align-items-center gap-3 px-4 py-3 rounded-3 w-100 text-start overflow-hidden";
        
        const estadoStyle = isActive
            ? "background: linear-gradient(to right, #e6f4ec, #f6f9f7); color: #0b4d2d; font-weight: 600; box-shadow: 0 4px 6px -1px rgba(22, 122, 75, 0.1);"
            : "background: transparent; color: #4b5563; font-weight: 500;";

        const transitionStyle = "border: none; transition: all 0.3s ease;";

        const decorativos = isActive ? `
            <div style="position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #167a4b; border-radius: 0 4px 4px 0;"></div>
            <div style="position: absolute; inset: 0; background: linear-gradient(to right, rgba(22, 122, 75, 0.05), transparent); pointer-events: none;"></div>
            <div style="position: absolute; right: 16px; top: 50%; transform: translateY(-50%); width: 8px; height: 8px; background-color: #167a4b; border-radius: 50%; box-shadow: 0 0 8px rgba(22, 122, 75, 0.4);"></div>
        ` : '';

        const iconStyle = isActive 
            ? "color: #167a4b; transform: scale(1.1); transition: all 0.3s ease;" 
            : "color: #9ca3af; transition: all 0.3s ease;";

        return `
            <a href="${href}" class="text-decoration-none mb-2 d-block">
                <div class="${containerBase}" style="${estadoStyle} ${transitionStyle}" 
                     onmouseover="if(!${isActive}) { this.style.backgroundColor = '#f9fafb'; this.style.color = '#0b4d2d'; this.querySelector('i').style.color = '#167a4b'; }" 
                     onmouseout="if(!${isActive}) { this.style.backgroundColor = 'transparent'; this.style.color = '#4b5563'; this.querySelector('i').style.color = '#9ca3af'; }">
                    ${decorativos}
                    <i class="${iconClass} fs-5 flex-shrink-0" style="position: relative; z-index: 10; ${iconStyle}"></i>
                    <span style="position: relative; z-index: 10;">${label}</span>
                </div>
            </a>
        `;
    };

    const drawerBody = document.querySelector("#menuDrawer .offcanvas-body");
    
    if (drawerBody) {
        let contenidoHTML = '';

        if (usuario) {
            const iniciales = (usuario.nombre.charAt(0) + (usuario.apellidos ? usuario.apellidos.charAt(0) : '')).toUpperCase();
            
            let nombreRolMostrado = "Cliente";
            if (usuario.rol && usuario.rol.nombre) {
                const nombreRaw = usuario.rol.nombre.toLowerCase();
                nombreRolMostrado = nombreRaw.charAt(0).toUpperCase() + nombreRaw.slice(1);
            }

            contenidoHTML += `
                <div id="drawer-user-area" class="p-4 border-bottom" style="background-color: #f6f9f7;">
                    <div class="d-flex align-items-center gap-3 mb-4">
                        <div class="rounded-circle d-flex align-items-center justify-content-center text-white fw-bold fs-4 shadow-sm" 
                             style="width: 56px; height: 56px; background: linear-gradient(135deg, #167a4b, #0b4d2d); flex-shrink: 0;">
                            ${iniciales}
                        </div>
                        <div class="overflow-hidden">
                            <div class="fw-bold text-dark text-truncate" style="color: #0b4d2d !important;">${usuario.nombre} ${usuario.apellidos || ''}</div>
                            <div class="text-secondary small text-truncate">${usuario.email}</div>
                            <div class="badge bg-success bg-opacity-10 text-success mt-1 fw-normal" style="font-size: 0.7rem;">
                                ${nombreRolMostrado}
                            </div>
                        </div>
                    </div>
                    
                    <div class="d-grid gap-2">
                        <a href="/perfil.html" class="btn btn-verdego rounded-3 py-2 border-0 text-white" style="background-color: #167a4b;">
                            <i class="bi bi-person me-2"></i> Mi perfil
                        </a>
                        <button id="drawer-btn-logout" class="btn btn-outline-secondary rounded-3 py-2 border-2">
                            <i class="bi bi-box-arrow-right me-2"></i> Cerrar sesión
                        </button>
                    </div>
                </div>`;
        } else {
            contenidoHTML += `
                <div id="drawer-user-area" class="p-4 border-bottom text-center" style="background-color: #f6f9f7;">
                    <p class="text-secondary mb-3 small">Bienvenido a VerdeGo. Inicia sesión para ver tus pedidos.</p>
                    <a href="/login.html" class="btn btn-success w-100 rounded-3 py-2 mb-2">Iniciar Sesión</a>
                    <a href="/register.html" class="btn btn-outline-success w-100 rounded-3 py-2">Crear cuenta</a>
                </div>`;
        }

        contenidoHTML += `<div class="p-3 d-flex flex-column gap-1">`;
        
        contenidoHTML += renderizarEnlace('/index.html', 'bi bi-house-door', 'Inicio');
        contenidoHTML += renderizarEnlace('/productos.html', 'bi bi-bag', 'Productos');
        
        contenidoHTML += `
            <a href="#" class="text-decoration-none mb-2 d-block" data-bs-toggle="offcanvas" data-bs-target="#cartDrawer">
                <div class="position-relative d-flex align-items-center gap-3 px-4 py-3 rounded-3 w-100 text-start overflow-hidden" 
                     style="background: transparent; color: #4b5563; font-weight: 500; transition: all 0.3s ease;"
                     onmouseover="this.style.backgroundColor = '#f9fafb'; this.style.color = '#0b4d2d'; this.querySelector('i').style.color = '#167a4b';" 
                     onmouseout="this.style.backgroundColor = 'transparent'; this.style.color = '#4b5563'; this.querySelector('i').style.color = '#9ca3af';">
                    <i class="bi bi-cart3 fs-5 flex-shrink-0" style="color: #9ca3af; transition: all 0.3s ease; position: relative; z-index: 10;"></i>
                    <span style="position: relative; z-index: 10;">Carrito</span>
                </div>
            </a>
        `;
        
        contenidoHTML += renderizarEnlace('/pedidos.html', 'bi bi-box-seam', 'Mis pedidos');
        contenidoHTML += `</div>`;

        if (esEmpleado) {
            contenidoHTML += `
                <div class="mt-2 pt-3 px-3 border-top">
                    <h6 class="text-secondary text-uppercase px-3 mb-2 fw-bold" style="font-size: 0.7rem; letter-spacing: 1px; opacity: 0.7;">
                        Administración
                    </h6>
                    <div class="d-flex flex-column gap-1">
                        ${renderizarEnlace('/admin/dashboard.html', 'bi bi-speedometer2', 'Panel de Control')}
                    </div>
                </div>
            `;
        }

        drawerBody.innerHTML = contenidoHTML;

        const btnLogout = document.getElementById("drawer-btn-logout");
        if (btnLogout) {
            btnLogout.addEventListener("click", manejarLogout);
        }
    }
}

function manejarLogout() {
    const offcanvasEl = document.getElementById('menuDrawer');
    if (offcanvasEl && window.bootstrap) {
        const offcanvas = window.bootstrap.Offcanvas.getInstance(offcanvasEl);
        if (offcanvas) offcanvas.hide();
    }

    showModal(
        'logout',
        '¿Cerrar sesión?',
        '¿Seguro que quieres desconectarte de tu cuenta?',
        async () => {
            try {
                await fetch("http://localhost:8080/VerdeGo_Server/api/usuarios/logout", { method: "POST" });
            } catch (e) { console.error(e); }
            
            localStorage.removeItem("usuarioVerdeGo");

            setTimeout(() => {
                showModal(
                    'info', 
                    '¡Hasta pronto!', 
                    'Has cerrado sesión correctamente.', 
                    () => { window.location.href = "/login.html"; },
                    false 
                );
            }, 500);
        },
        true 
    );
}