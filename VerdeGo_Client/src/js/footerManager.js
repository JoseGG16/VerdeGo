document.addEventListener("DOMContentLoaded", () => {
    renderizarFooter();
});

export function renderizarFooter() {
    const footerContainer = document.getElementById("main-footer");
    
    if (!footerContainer) return;

    footerContainer.innerHTML = `
    <footer class="bg-white border-top mt-5 pt-5">
        <div class="container">
            <div class="row g-5">
                
                <div class="col-lg-4 col-md-6">
                    <div class="d-flex flex-column gap-3">
                        <div class="d-flex align-items-center gap-2">
                            <div style="width: 40px; height: 40px;">
                                <img src="/imagenes/logo.png" alt="VerdeGo" class="w-100 h-100 object-fit-contain">
                            </div>
                            <span class="fs-4 fw-bold" style="color: #0b4d2d;">VerdeGo</span>
                        </div>
                        <p class="text-secondary small mb-0">
                            Tu tienda online de productos frescos, locales y sostenibles. 
                            Comprometidos con la calidad y el medio ambiente.
                        </p>
                    </div>
                </div>

                <div class="col-lg-4 col-md-6">
                    <h5 class="fw-bold mb-3" style="color: #0b4d2d;">Enlaces rápidos</h5>
                    <ul class="list-unstyled d-flex flex-column gap-2">
                        <li>
                            <a href="/index.html" class="text-decoration-none text-secondary hover-link transition-colors">
                                Inicio
                            </a>
                        </li>
                        <li>
                            <a href="/productos.html" class="text-decoration-none text-secondary hover-link transition-colors">
                                Productos
                            </a>
                        </li>
                        <li>
                            <a href="#" class="text-decoration-none text-secondary hover-link transition-colors">
                                Sobre nosotros
                            </a>
                        </li>
                        <li>
                            <a href="#" class="text-decoration-none text-secondary hover-link transition-colors">
                                Política de envíos
                            </a>
                        </li>
                    </ul>
                </div>

                <div class="col-lg-4 col-md-12">
                    <h5 class="fw-bold mb-3" style="color: #0b4d2d;">Contacto</h5>
                    <div class="d-flex align-items-center gap-2 text-secondary">
                        <i class="bi bi-envelope-fill fs-5" style="color: #167a4b;"></i>
                        <a href="mailto:hola@verdego.com" class="text-decoration-none text-secondary hover-link">
                            hola@verdego.com
                        </a>
                    </div>
                </div>

            </div>

            <div class="mt-5 pt-4 pb-4 border-top text-center text-secondary small">
                © 2025 VerdeGo. Todos los derechos reservados.
            </div>
        </div>
    </footer>

    <style>
        .hover-link:hover {
            color: #167a4b !important;
        }
    </style>
    `;
}