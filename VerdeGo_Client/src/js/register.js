import { showModal } from './modalBootstrap.js';

const URL_API = "http://localhost:8080/VerdeGo_Server/api/usuarios/registro";

document.addEventListener("DOMContentLoaded", () => {
    inicializarEventos();
});

function inicializarEventos() {
    const toggleBtn = document.getElementById("togglePassword");
    const passInput = document.getElementById("password");
    const iconEye = toggleBtn.querySelector("i");

    toggleBtn.addEventListener("click", () => {
        const type = passInput.getAttribute("type") === "password" ? "text" : "password";
        passInput.setAttribute("type", type);
        iconEye.classList.toggle("bi-eye");
        iconEye.classList.toggle("bi-eye-slash");
    });

    const nombreInput = document.getElementById("nombre");
    const apellidosInput = document.getElementById("apellidos");
    const emailInput = document.getElementById("email");
    const passInputReg = document.getElementById("password");
    const confirmPassInput = document.getElementById("confirmPassword");
    const telefonoInput = document.getElementById("telefono");
    const form = document.getElementById("registerForm");

    nombreInput.addEventListener("input", () => validarCampoVacio(nombreInput, "El nombre es obligatorio."));
    apellidosInput.addEventListener("input", () => validarCampoVacio(apellidosInput, "Los apellidos son obligatorios."));
    emailInput.addEventListener("input", () => validarEmail(emailInput));
    
    passInputReg.addEventListener("input", () => {
        validarPassword(passInputReg);
        if (confirmPassInput.value.length > 0) {
            validarConfirmacion(passInputReg, confirmPassInput);
        }
    });

    confirmPassInput.addEventListener("input", () => validarConfirmacion(passInputReg, confirmPassInput));
    telefonoInput.addEventListener("input", () => validarTelefono(telefonoInput));

    form.addEventListener("submit", async function(event) {
        event.preventDefault();

        const vNombre = validarCampoVacio(nombreInput, "El nombre es obligatorio.");
        const vApellidos = validarCampoVacio(apellidosInput, "Los apellidos son obligatorios.");
        const vEmail = validarEmail(emailInput);
        const vPass = validarPassword(passInputReg);
        const vConfirm = validarConfirmacion(passInputReg, confirmPassInput);
        const vTel = validarTelefono(telefonoInput);

        if (!vNombre || !vApellidos || !vEmail || !vPass || !vConfirm || !vTel) {
            return; 
        }

        await enviarRegistro();
    });
}


function marcarError(input, mensaje) {
    input.classList.add("is-invalid");
    input.classList.remove("is-valid");
    const errorDiv = document.getElementById(input.id + "Error");
    if (errorDiv) errorDiv.textContent = mensaje;
    return false;
}

function marcarExito(input) {
    input.classList.remove("is-invalid");
    input.classList.add("is-valid");
    const errorDiv = document.getElementById(input.id + "Error");
    if (errorDiv) errorDiv.textContent = "";
    return true;
}

function validarCampoVacio(input, msgError) {
    if (input.value.trim() === "") {
        return marcarError(input, msgError);
    }
    return marcarExito(input);
}

function validarEmail(input) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(input.value)) {
        return marcarError(input, "Correo electrónico no válido.");
    }
    return marcarExito(input);
}

function validarPassword(input) {
    const regexFuerte = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
    if (!regexFuerte.test(input.value)) {
        return marcarError(input, "Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 símbolo.");
    }
    return marcarExito(input);
}

function validarConfirmacion(passInput, confirmInput) {
    if (confirmInput.value !== passInput.value) {
        return marcarError(confirmInput, "Las contraseñas no coinciden.");
    } else if (confirmInput.value === "") {
        return marcarError(confirmInput, "Confirma tu contraseña.");
    }
    return marcarExito(confirmInput);
}

function validarTelefono(input) {
    const regexTel = /^[0-9]{9}$/;
    if (!regexTel.test(input.value)) {
        return marcarError(input, "El teléfono debe tener 9 dígitos y ser válido.");
    }
    return marcarExito(input);
}


async function enviarRegistro() {
    const btnSubmit = document.getElementById("btn-submit");
    const btnText = document.getElementById("btn-text");
    const btnLoader = document.getElementById("btn-loader");
    const msgZona = document.getElementById("msg-zona");

    btnSubmit.disabled = true;
    btnText.classList.add("d-none");
    btnLoader.classList.remove("d-none");
    if (msgZona) msgZona.classList.add("d-none");

    const nuevoUsuario = {
        nombre: document.getElementById("nombre").value,
        apellidos: document.getElementById("apellidos").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        telefono: document.getElementById("telefono").value
    };

    try {
        const response = await fetch(URL_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(nuevoUsuario)
        });

        const data = await response.json();

        if (data.error) {
            if (msgZona) {
                msgZona.classList.remove("d-none", "alert-success");
                msgZona.classList.add("alert-danger");
                msgZona.innerHTML = `<i class="bi bi-exclamation-circle me-2"></i> ${data.error}`;
            }
            restaurarBoton();
        } else {
            showModal(
                'success',
                '¡Cuenta creada!',
                'Tu registro se ha completado con éxito. Ya puedes iniciar sesión.',
                () => { window.location.href = "/login.html"; }
            );
        }
    } catch (error) {
        if (msgZona) {
            msgZona.classList.remove("d-none", "alert-success");
            msgZona.classList.add("alert-danger");
            msgZona.textContent = "Error de conexión con el servidor.";
        }
        restaurarBoton();
    }
}

function restaurarBoton() {
    const btnSubmit = document.getElementById("btn-submit");
    const btnText = document.getElementById("btn-text");
    const btnLoader = document.getElementById("btn-loader");
    
    btnSubmit.disabled = false;
    btnText.classList.remove("d-none");
    btnLoader.classList.add("d-none");
}