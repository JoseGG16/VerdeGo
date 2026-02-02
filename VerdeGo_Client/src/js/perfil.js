import { showModal } from './modalBootstrap.js';
import { actualizarNavbar } from './authNavbar.js';

document.addEventListener("DOMContentLoaded", () => {
    cargarDatosUsuario();
    inicializarEventos();
    cargarDirecciones();
});

function cargarDatosUsuario() {
    const usuarioJSON = localStorage.getItem("usuarioVerdeGo");
    if (!usuarioJSON) {
        window.location.href = "/login.html";
        return;
    }

    const usuario = JSON.parse(usuarioJSON);

    const displayName = document.getElementById("display-name");
    if(displayName) displayName.textContent = `${usuario.nombre} ${usuario.apellidos || ''}`;
    
    const displayEmail = document.getElementById("display-email");
    if(displayEmail) displayEmail.textContent = usuario.email;
    
    const iniciales = (usuario.nombre.charAt(0) + (usuario.apellidos ? usuario.apellidos.charAt(0) : '')).toUpperCase();
    const userInitials = document.getElementById("user-initials");
    if(userInitials) userInitials.textContent = iniciales;

    const inputNombre = document.getElementById("input-nombre");
    if(inputNombre) inputNombre.value = usuario.nombre;
    
    const inputApellidos = document.getElementById("input-apellidos");
    if(inputApellidos) inputApellidos.value = usuario.apellidos || "";
    
    const inputEmail = document.getElementById("input-email");
    if(inputEmail) inputEmail.value = usuario.email;
    
    const inputTelefono = document.getElementById("input-telefono");
    if(inputTelefono) inputTelefono.value = usuario.telefono || "";

    const puntos = usuario.puntosFidelizacion || 0;
    actualizarGraficoPuntos(puntos);

    if (usuario.paypalEmail) {
        mostrarPayPalVinculado(usuario.paypalEmail);
    } else {
        mostrarPayPalDesvinculado();
        setTimeout(() => { renderizarBotonPayPal(); }, 500);
    }
}

function actualizarGraficoPuntos(currentPoints) {
    const nextRewardPoints = 1000;
    const pointsValue = currentPoints * 0.01;
    const progressPercentage = Math.min((currentPoints / nextRewardPoints) * 100, 100);

    const pointsDisplay = document.getElementById("points-display");
    if(pointsDisplay) pointsDisplay.textContent = `${currentPoints} puntos`;
    
    const pointsValueEl = document.getElementById("points-value");
    if(pointsValueEl) pointsValueEl.textContent = `Equivale a ${pointsValue.toFixed(2)}€`;
    
    const progressBar = document.getElementById("progress-bar");
    if(progressBar) progressBar.style.width = `${progressPercentage}%`;
    
    const progressText = document.getElementById("progress-text");
    if(progressText) progressText.textContent = `${progressPercentage.toFixed(0)}%`;
    
    const missing = Math.max(0, nextRewardPoints - currentPoints);
    const pointsNeeded = document.getElementById("points-needed");
    if(pointsNeeded) {
        pointsNeeded.textContent = missing > 0 
        ? `Te faltan ${missing} puntos para tu próximo descuento` 
        : "¡Felicidades! Tienes puntos suficientes para un descuento.";
    }
}

function inicializarEventos() {
    const inputs = document.querySelectorAll("#profile-form input:not(#input-email)");
    const btnToggle = document.getElementById("btn-toggle-edit");
    const saveActions = document.getElementById("save-actions");
    const textEditBtn = document.getElementById("text-edit-btn");
    let isEditing = false;

    if(btnToggle) {
        btnToggle.addEventListener("click", () => {
            isEditing = !isEditing;
            inputs.forEach(input => input.disabled = !isEditing);
            
            if (isEditing) {
                saveActions.classList.remove("d-none");
                textEditBtn.textContent = "Cancelar edición";
                btnToggle.classList.remove("btn-light");
                btnToggle.classList.add("btn-secondary");
            } else {
                saveActions.classList.add("d-none");
                textEditBtn.textContent = "Editar perfil";
                btnToggle.classList.add("btn-light");
                btnToggle.classList.remove("btn-secondary");
                cargarDatosUsuario(); 
            }
        });
    }

    const btnSave = document.getElementById("btn-save");
    if(btnSave) {
        btnSave.addEventListener("click", async () => {
            const usuarioAntiguo = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
            
            const usuarioActualizado = {
                idUsuario: usuarioAntiguo.idUsuario,
                idRol: usuarioAntiguo.idRol,
                email: usuarioAntiguo.email,
                password: usuarioAntiguo.password,
                nombre: document.getElementById("input-nombre").value,
                apellidos: document.getElementById("input-apellidos").value,
                telefono: document.getElementById("input-telefono").value
            };

            try {
                const response = await fetch("http://localhost:8080/VerdeGo_Server/api/usuarios/modificar", {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(usuarioActualizado)
                });

                const data = await response.json();

                if (data.error) {
                    alert("❌ Error: " + data.error);
                } else {
                    const usuarioFinal = { ...usuarioAntiguo, ...data }; 
                    localStorage.setItem("usuarioVerdeGo", JSON.stringify(usuarioFinal));
                    
                    actualizarNavbar();

                    showModal('success', 'Datos guardados', 'Tu perfil se ha actualizado correctamente.', () => {
                        isEditing = false;
                        if(btnToggle) btnToggle.click(); 
                        cargarDatosUsuario();
                    });
                }
            } catch (error) {
                console.error(error);
                alert("Error de conexión con el servidor");
            }
        });
    }

    const btnLogout = document.getElementById("btn-logout");
    if (btnLogout) {
        btnLogout.addEventListener("click", () => {
            showModal(
                'logout',
                'Cerrando sesión',
                '¿Seguro que quieres salir de VerdeGo?',
                async () => {
                    try {
                        await fetch("http://localhost:8080/VerdeGo_Server/api/usuarios/logout", { method: "POST" });
                    } catch (e) { console.error(e); }
                    
                    localStorage.removeItem("usuarioVerdeGo");

                    setTimeout(() => {
                        showModal(
                            'info', 
                            'Sesión finalizada',
                            'Has desconectado tu cuenta correctamente. Esperamos verte pronto.',
                            () => { window.location.href = "/login.html"; },
                            false 
                        );
                    }, 500);
                },
                true 
            );
        });
    }

    const btnUnlinkPaypal = document.getElementById("btn-unlink-paypal");
    if(btnUnlinkPaypal) {
        btnUnlinkPaypal.addEventListener("click", async () => {
            if(confirm("¿Seguro que quieres desvincular tu cuenta PayPal?")) {
                const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
                try {
                    const response = await fetch("http://localhost:8080/VerdeGo_Server/api/usuarios/desvincular-paypal", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ idUsuario: usuario.idUsuario })
                    });
                    
                    if(response.ok) {
                        usuario.paypalEmail = null;
                        localStorage.setItem("usuarioVerdeGo", JSON.stringify(usuario));
                        mostrarPayPalDesvinculado();
                        renderizarBotonPayPal();
                    }
                } catch(e) { console.error(e); }
            }
        });
    }
}


function mostrarPayPalVinculado(email) {
    const linkedDiv = document.getElementById("paypal-linked");
    const unlinkedDiv = document.getElementById("paypal-unlinked");
    const emailDisplay = document.getElementById("paypal-email-display");

    if(linkedDiv) linkedDiv.classList.remove("d-none");
    if(unlinkedDiv) unlinkedDiv.classList.add("d-none");
    if(emailDisplay) emailDisplay.textContent = email;
}

function mostrarPayPalDesvinculado() {
    const linkedDiv = document.getElementById("paypal-linked");
    const unlinkedDiv = document.getElementById("paypal-unlinked");

    if(linkedDiv) linkedDiv.classList.add("d-none");
    if(unlinkedDiv) unlinkedDiv.classList.remove("d-none");
}

function renderizarBotonPayPal() {
    const container = document.getElementById('paypal-button-container');
    if (!container) return;
    
    container.innerHTML = ''; 

    if (!window.paypal) {
        console.error("SDK de PayPal no cargado.");
        container.innerHTML = '<p class="text-danger small">Error cargando PayPal.</p>';
        return;
    }

    window.paypal.Buttons({
        style: {
            layout: 'vertical',
            color:  'gold',
            shape:  'rect',
            label:  'paypal',
            height: 45,
            tagline: false
        },
        createOrder: function(data, actions) {
            return actions.order.create({
                purchase_units: [{ amount: { value: '0.01' } }]
            });
        },
        onApprove: function(data, actions) {
            return actions.order.capture().then(async function(details) {
                const emailPaypal = details.payer.email_address;
                const nombrePaypal = details.payer.name.given_name;
                
                const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
                try {
                    const response = await fetch("http://localhost:8080/VerdeGo_Server/api/usuarios/vincular-paypal", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ 
                            idUsuario: usuario.idUsuario,
                            paypalEmail: emailPaypal
                        })
                    });

                    if(response.ok) {
                        usuario.paypalEmail = emailPaypal;
                        localStorage.setItem("usuarioVerdeGo", JSON.stringify(usuario));
                        showModal('success', 'Cuenta vinculada', `Has vinculado la cuenta de ${nombrePaypal}`);
                        mostrarPayPalVinculado(emailPaypal);
                    } else {
                        showModal('error', 'Error', 'No se pudo vincular la cuenta en el servidor.');
                    }
                } catch (error) { console.error(error); }
            });
        },
        onError: function (err) { console.error('Error PayPal:', err); }
    }).render('#paypal-button-container');

}
async function cargarDirecciones() {
    const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
    if(!usuario) return;

    const contenedor = document.getElementById("lista-direcciones");
    if(!contenedor) return;

    try {
        const response = await fetch(`http://localhost:8080/VerdeGo_Server/api/direcciones/listar?idUsuario=${usuario.idUsuario}`);
        const direcciones = await response.json();

        if (direcciones.length === 0) {
            contenedor.innerHTML = '<div class="text-center text-muted small py-3 border rounded-3 bg-light">No tienes direcciones guardadas.</div>';
            return;
        }

        contenedor.innerHTML = direcciones.map(dir => `
            <div class="border rounded-3 p-3 position-relative ${dir.esPrincipal ? 'border-success bg-success bg-opacity-10' : 'bg-white'}">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <h6 class="fw-bold mb-0 text-verdego-dark">${dir.alias || 'Dirección'}</h6>
                            ${dir.esPrincipal ? '<span class="badge bg-success" style="font-size: 0.65rem;">PRINCIPAL</span>' : ''}
                        </div>
                        <p class="mb-0 small text-secondary">
                            ${dir.calle}<br>
                            ${dir.codigoPostal}, ${dir.ciudad}
                        </p>
                    </div>
                    <div class="dropdown">
                        <button class="btn btn-link text-muted p-0" data-bs-toggle="dropdown">
                            <i class="bi bi-three-dots-vertical"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                            ${!dir.esPrincipal ? `<li><button class="dropdown-item small" onclick="marcarPrincipal(${dir.idDireccion})"><i class="bi bi-star me-2"></i>Hacer principal</button></li>` : ''}
                            <li><button class="dropdown-item small" onclick='editarDireccion(${JSON.stringify(dir)})'><i class="bi bi-pencil me-2"></i>Editar</button></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><button class="dropdown-item small text-danger" onclick="eliminarDireccion(${dir.idDireccion})"><i class="bi bi-trash me-2"></i>Eliminar</button></li>
                        </ul>
                    </div>
                </div>
            </div>
        `).join('');

    } catch (e) {
        console.error(e);
        contenedor.innerHTML = '<p class="text-danger small text-center">Error al cargar direcciones</p>';
    }
}

window.abrirModalDireccion = () => {
    document.getElementById("formDireccion").reset();
    document.getElementById("dir-id").value = "";
    document.getElementById("modalDireccionTitulo").textContent = "Nueva Dirección";
    const modal = new bootstrap.Modal(document.getElementById("modalDireccion"));
    modal.show();
};

window.editarDireccion = (dir) => {
    document.getElementById("dir-id").value = dir.idDireccion;
    document.getElementById("dir-alias").value = dir.alias;
    document.getElementById("dir-calle").value = dir.calle;
    document.getElementById("dir-ciudad").value = dir.ciudad;
    document.getElementById("dir-cp").value = dir.codigoPostal;
    document.getElementById("dir-principal").checked = dir.esPrincipal;
    
    document.getElementById("modalDireccionTitulo").textContent = "Editar Dirección";
    const modal = new bootstrap.Modal(document.getElementById("modalDireccion"));
    modal.show();
};

window.marcarPrincipal = async (idDireccion) => {
    const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
    try {
        await fetch("http://localhost:8080/VerdeGo_Server/api/direcciones/principal", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ idUsuario: usuario.idUsuario, idDireccion: idDireccion })
        });
        cargarDirecciones();
    } catch(e) { console.error(e); }
};

window.eliminarDireccion = async (idDireccion) => {
    if(!confirm("¿Eliminar esta dirección?")) return;
    const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
    try {
        await fetch(`http://localhost:8080/VerdeGo_Server/api/direcciones/eliminar?id=${idDireccion}&idUsuario=${usuario.idUsuario}`, { method: "DELETE" });
        cargarDirecciones();
    } catch(e) { console.error(e); }
};

const formDireccion = document.getElementById("formDireccion");
if(formDireccion) {
    formDireccion.addEventListener("submit", async (e) => {
        e.preventDefault();
        const usuario = JSON.parse(localStorage.getItem("usuarioVerdeGo"));
        const idDireccion = document.getElementById("dir-id").value;
        
        const datos = {
            idDireccion: idDireccion ? parseInt(idDireccion) : 0,
            usuario: { idUsuario: usuario.idUsuario }, 
            alias: document.getElementById("dir-alias").value,
            calle: document.getElementById("dir-calle").value,
            ciudad: document.getElementById("dir-ciudad").value,
            codigoPostal: document.getElementById("dir-cp").value,
            pais: "España",
            esPrincipal: document.getElementById("dir-principal").checked
        };

        const url = idDireccion 
            ? "http://localhost:8080/VerdeGo_Server/api/direcciones/editar" 
            : "http://localhost:8080/VerdeGo_Server/api/direcciones/crear";
        
        const method = idDireccion ? "PUT" : "POST";

        try {
            const res = await fetch(url, {
                method: method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(datos)
            });
            
            if(res.ok) {
                bootstrap.Modal.getInstance(document.getElementById("modalDireccion")).hide();
                cargarDirecciones();
            } else {
                alert("Error al guardar");
            }
        } catch(e) { console.error(e); }
    });
}
