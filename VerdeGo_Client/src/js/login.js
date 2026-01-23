import { showModal } from './modalBootstrap.js';
const URL_API = "http://localhost:8080/VerdeGo_Server/api/usuarios/login";

const toggleBtn = document.getElementById("togglePassword");
const passInput = document.getElementById("password");
const iconEye = toggleBtn.querySelector("i");

toggleBtn.addEventListener("click", () => {
    const type = passInput.getAttribute("type") === "password" ? "text" : "password";
    passInput.setAttribute("type", type);
    iconEye.classList.toggle("bi-eye");
    iconEye.classList.toggle("bi-eye-slash");
});

document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();
    const form = event.target;

    if (!form.checkValidity()) {
        event.stopPropagation();
        form.classList.add('was-validated');
        return;
    }

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const btnSubmit = document.getElementById("btn-submit");
    const btnText = document.getElementById("btn-text");
    const btnLoader = document.getElementById("btn-loader");
    const msgZona = document.getElementById("msg-zona");

    btnSubmit.disabled = true;
    btnText.classList.add("d-none");
    btnLoader.classList.remove("d-none");
    msgZona.classList.add("d-none");

    try {
        const response = await fetch(URL_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, password: password })
        });

        if (!response.ok) throw new Error("Error de conexión");
        
        const data = await response.json();

        if (data.error) {
            throw new Error(data.error);
        }

        localStorage.setItem("usuarioVerdeGo", JSON.stringify(data));
                
        showModal(
            'success',
            '¡Bienvenido de nuevo!',
            'Has iniciado sesión correctamente. Redirigiendo...',
            () => { window.location.href = "/index.html"; }
        );

    } catch (error) {
        console.error(error);
        
        msgZona.classList.remove("d-none");
        msgZona.classList.add("alert-danger");
        msgZona.innerHTML = `<i class="bi bi-exclamation-triangle me-2"></i> ${error.message || "Usuario o contraseña incorrectos"}`;

        btnSubmit.disabled = false;
        btnText.classList.remove("d-none");
        btnLoader.classList.add("d-none");
    }
});

document.getElementById("btn-guest").addEventListener("click", () => {
    localStorage.removeItem("usuarioVerdeGo");
    
    window.location.href = "/index.html";
});