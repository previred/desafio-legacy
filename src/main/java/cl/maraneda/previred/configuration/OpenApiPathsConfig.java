package cl.maraneda.previred.configuration;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class OpenApiPathsConfig {

    @Bean
    public OpenAPI apiConEndpoints() {

        Schema<?> usuarioSchema = new ObjectSchema()
                .addProperty("nombre", new StringSchema().description("Nombre del usuario"))
                .addProperty("apellido", new StringSchema().description("Apellido del usuario"))
                .addProperty("rut", new StringSchema().description("RUT o DNI del usuario en formato <num>-<dv> (Sin puntos, con guion y digito verificador, entre 0 y 9 o la letra K)").pattern("^\\d{7,8}-[0-9Kk]$"))
                .addProperty("cargo", new StringSchema().description("Cargo del usuario"))
                .addProperty("salario", new IntegerSchema().description("Salario del usuario").minimum(BigDecimal.valueOf(400000)));

        Schema<?> montoSchema = new ObjectSchema()
                .addProperty("id", new IntegerSchema().minimum(BigDecimal.ONE).description("ID de un usuario existente"))
                .addProperty("monto", new IntegerSchema().minimum(BigDecimal.ONE).description("Cantidad a abonar o descontar"))
                .addProperty("tipo",
                    new IntegerSchema()._enum(List.of(1, -1))
                        .description("Indicador de bono o descuento. 1=Bono (se obtendra un error si el monto ingresado es mayor a la mitad del salario), -1=Descuento (se obtendra un error si el descuento es mayor al salario"));

        Content jsonContent = new Content().addMediaType(
                "application/json",
                new MediaType().schema(usuarioSchema)
        );

        Content montoContent = new Content().addMediaType(
                "application/json",
                new MediaType().schema(montoSchema)
        );

        PathItem empleadosPath = new PathItem()

                // GET
                .get(new Operation()
                        .summary("Permite buscar todos los usuarios actualmente almacenados")
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse().description("Se obtuvo correctamente la lista de los usuarios"))
                                .addApiResponse("500",
                                        new ApiResponse().description("Se obtuvo un error interno al tratar de buscar los usuarios")
                        ))
                )

                // POST
                .post(new Operation()
                        .summary("Permite crear un usuario nuevo")
                        .requestBody(new io.swagger.v3.oas.models.parameters.RequestBody()
                                .content(jsonContent))
                        .responses(new ApiResponses()
                                .addApiResponse("201",
                                        new ApiResponse().description("Se creó el usuario exitosamente"))
                                .addApiResponse("400",
                                        new ApiResponse().description("No se ingresaron los datos del usuario o ya existe un usuario con el mismo RUT o DNI"))
                                .addApiResponse("500",
                                        new ApiResponse().description("Se obtuvo un error interno al intentar crear el usuario"))
                        )
                )

                // DELETE
                .delete(new Operation()
                        .summary("Permite eliminar un usuario por medio de su ID especificada")
                        .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                .name("id")
                                .in("query")
                                .required(true)
                                .schema(new IntegerSchema()))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse().description("Usuario eliminado exitosamente"))
                                .addApiResponse("400",
                                        new ApiResponse().description("No se ingreso la ID del usuario o la ID ingresada no es numerica"))
                                .addApiResponse("404",
                                        new ApiResponse().description("Usuario no encontrado"))
                                .addApiResponse("500",
                                        new ApiResponse().description("Se obtuvo un error interno al intentar eliminar el usuario"))
                        ))
                //PATCH
                .patch(new Operation()
                        .summary("Permite abonar o descontar una cantidad al monto actual del usuario")
                        .requestBody(new io.swagger.v3.oas.models.parameters.RequestBody()
                                           .content(montoContent))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse().description("El monto del usuario fue actualizado exitosamente"))
                                .addApiResponse("400",
                                        new ApiResponse().description("No se ingresaron los datos para actualizar o no se especificó un tipo de monto (bono o descuento) o se ingreso un monto demasiado grande (mayor al salario si es descuento, mayor a la mitad de este si es bono)"))
                                .addApiResponse("404",
                                        new ApiResponse().description("El usuario no fue encontrado o no hubo cambios en su monto"))
                                .addApiResponse("500",
                                        new ApiResponse().description("Se obtuvo un error interno al intentar actualizar el monto del usuario"))
                        ));

        return new OpenAPI()
                .info(new Info()
                        .title("API Usuarios")
                        .version("1.0")
                        .description("API basada en Servlets para Previred"))
                .path("/api/empleados", empleadosPath);
    }
}
