
export function checkSession() {
    const userStr = localStorage.getItem("usuarioVerdeGo");
    const user = userStr ? JSON.parse(userStr) : null;

    const navGuest = document.querySelectorAll(".view-guest");
    const navUser = document.querySelectorAll(".view-user");   
    const userNameDisplay = document.getElementById("user-name-display");
    const userRoleDisplay = document.getElementById("user-role-display");
    const userInitials = document.getElementById("user-initials");

    if (user) {
        console.log("Sesión activa:", user.nombre);

        navGuest.forEach(el => el.classList.add("d-none"));
        navUser.forEach(el => el.classList.remove("d-none"));

        if (userNameDisplay) userNameDisplay.textContent = user.nombre + " " + (user.apellidos || "");
        
        let rolTexto = "Usuario";
        if (user.rol && user.rol.idRol === 1) rolTexto = "Administrador";
        if (user.rol && user.rol.idRol === 2) rolTexto = "Cliente";
        if (user.rol && user.rol.idRol === 3) rolTexto = "Empleado";
        
        if (userRoleDisplay) userRoleDisplay.textContent = rolTexto;

        if (userInitials) userInitials.textContent = user.nombre.charAt(0).toUpperCase();

    } else {
        console.log("Modo invitado");

        navGuest.forEach(el => el.classList.remove("d-none"));
        navUser.forEach(el => el.classList.add("d-none"));
    }
}

window.logout = function() {
    localStorage.removeItem("usuarioVerdeGo");
    window.location.href = "/index.html"; 
}

document.addEventListener("DOMContentLoaded", checkSession);