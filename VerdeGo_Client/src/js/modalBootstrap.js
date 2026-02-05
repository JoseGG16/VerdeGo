export function showModal(type, title, message, onConfirm, showCancel = false, buttonCustomText = null) {
    
    if (!window.bootstrap) {
        console.error("ERROR: Bootstrap no cargado");
        if (onConfirm) onConfirm();
        return;
    }

    const modalElement = document.getElementById('genericModal');
    const titleEl = document.getElementById('modal-title');
    const msgEl = document.getElementById('modal-message');
    const iconEl = document.getElementById('modal-icon');
    const iconBgEl = document.getElementById('modal-icon-bg');
    const btnAction = document.getElementById('modal-btn-action');
    const btnCancel = document.getElementById('modal-btn-cancel'); 

    // 1. Textos básicos
    titleEl.textContent = title;
    msgEl.textContent = message;

    // 2. Control del botón cancelar (Usando el parámetro showCancel)
    
    if (showCancel || onConfirm) {
        btnCancel.classList.remove('d-none');
    } else {
        btnCancel.classList.add('d-none');
    }

    // 3. Reset de estilos base
    iconEl.className = "bi"; 
    iconBgEl.className = "d-inline-flex align-items-center justify-content-center rounded-circle";
    btnAction.className = "btn btn-lg rounded-4 py-3 text-white fw-bold shadow-sm w-100";

    // 4. Configuración según el TIPO
    if (type === 'success') {
        iconEl.classList.add("bi-check-lg", "text-white");
        iconBgEl.style.backgroundColor = "#167a4b"; 
        btnAction.style.backgroundColor = "#167a4b";
        btnAction.textContent = "Continuar";

    } else if (type === 'logout') {
        // Estilo ROJO para cerrar sesión
        iconEl.classList.add("bi-box-arrow-right", "text-white");
        iconBgEl.style.backgroundColor = "#dc3545"; 
        btnAction.style.backgroundColor = "#dc3545"; 
        btnAction.textContent = "Sí, cerrar sesión"; // Texto por defecto para logout

    } else if (type === 'warning') {
        // Estilo para cancelar pedidos (Icono Amarillo, Botón Rojo)
        iconEl.classList.add("bi-exclamation-lg", "text-white");
        iconBgEl.style.backgroundColor = "#ffc107"; // Amarillo de advertencia
        btnAction.style.backgroundColor = "#dc3545"; // Rojo de peligro
        btnAction.textContent = "Confirmar";

    } else if (type === 'info') {
        iconEl.classList.add("bi-shield-check"); 
        iconEl.style.color = "#5a7a6a";
        iconBgEl.style.backgroundColor = "#f6f9f7";
        btnAction.style.backgroundColor = "#6c757d"; 
        btnAction.textContent = "Entendido";
        
    } else if (type === 'error') {
        iconEl.classList.add("bi-exclamation-triangle-fill", "text-white");
        iconBgEl.style.backgroundColor = "#dc3545";
        btnAction.style.backgroundColor = "#dc3545";
        btnAction.textContent = "Entendido";
    }

    // 5. Sobreescribir texto SOLO si se pasa el 6º parámetro
    if (buttonCustomText) {
        btnAction.textContent = buttonCustomText;
    }

    // 6. Clonar botón para limpiar eventos anteriores
    const newBtn = btnAction.cloneNode(true);
    btnAction.parentNode.replaceChild(newBtn, btnAction);

    newBtn.addEventListener('click', () => {
        const modalInstance = window.bootstrap.Modal.getInstance(modalElement);
        if (modalInstance) modalInstance.hide();
        if (onConfirm) onConfirm();
    });

    const myModal = new window.bootstrap.Modal(modalElement);
    myModal.show();
}
