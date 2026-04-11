package cl.previred.desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Punto de entrada principal para la aplicacion Spring Boot.
 *
 * <p>Esta clase inicia el contexto de Spring y configura automaticamente
 * los componentes del framework incluyendo:</p>
 * <ul>
 *   <li>Spring MVC para manejo de peticiones web</li>
 *   <li>Scaneo de servlets annotados con @WebServlet</li>
 *   <li>Inyeccion de dependencias</li>
 * </ul>
 *
 * <p>La aplicacion expone servicios REST para la gestion de empleados
 * a traves del endpoint {@code /api/empleados/*}.</p>
 *
 * @see SpringApplication#run(Class, String...)
 * @see ServletComponentScan
 * @since 1.0
 */
@SpringBootApplication
@ServletComponentScan
public class PreviredDesafioLegacyApplication {

    /**
     * Metodo principal que inicia la aplicacion Spring Boot.
     *
     * @param args argumentos de linea de comandos pasados a la aplicacion
     */
    public static void main(String[] args) {
        SpringApplication.run(PreviredDesafioLegacyApplication.class, args);
    }
}
