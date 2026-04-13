package cl.maraneda.previred.utils;

import cl.maraneda.previred.dto.OutputDTO;
import cl.maraneda.previred.exceptions.PreviredRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Util {
    private Util() {}

    public static String readBodyFromRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        StringBuilder body = new StringBuilder();
        try(BufferedReader reader = req.getReader()) {
            reader.lines().forEach(body::append);
        }catch(IOException _){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Se produjo un error interno de entrada/salida al intentar leer los datos del nuevo usuario");
        }
        return body.toString();
    }

    public static void writeResponse(HttpServletResponse resp, OutputDTO outdto) throws IOException{
        try (PrintWriter out = resp.getWriter()) {
            out.print(outdto);
            out.flush();
        }
    }

    public static void doResponse(HttpServletResponse resp, OutputDTO outdto, int status) throws IOException {
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        resp.setStatus(status);
        writeResponse(resp, outdto);
    }

    public static void doResponse(HttpServletResponse resp, OutputDTO outdto) throws IOException {
        doResponse(resp, outdto, HttpServletResponse.SC_OK);
    }

    public static void doErrorResponse(int status, String msg, HttpServletResponse resp) throws IOException {
        OutputDTO outdto = new OutputDTO();
        outdto.setMsg(msg);
        resp.setStatus(status);
        Util.doResponse(resp, outdto, status);
    }

    public static void doErrorResponse(int status, Throwable e, HttpServletResponse resp) throws IOException {
        if(e.getCause()==null){
            doErrorResponse(status, e.getMessage(), resp);
        }else{
            doErrorResponse(status, e.getCause().getMessage(), resp);
        }
    }

    public static boolean validarRut(String r){
        if(r == null){
            return false;
        }
        r = r.replace(".", "");
        if(!Pattern.compile("^\\d{7,8}-[0-9Kk]$").matcher(r).matches()){
            return false;
        }
        String[] partes = r.split("-");
        StringBuilder sb = new StringBuilder(partes[0]);
        AtomicInteger count = new AtomicInteger(2);
        AtomicInteger sum = new AtomicInteger();
        int[] digitos =
            sb.reverse().toString()
              .chars()
              .map(c -> c - '0')
              .toArray();
        IntStream.of(digitos).forEach(d -> {
            sum.addAndGet(count.getAndIncrement() * d);
            if(count.get() == 8){
                count.set(2);
            }
        });
        int ndv = 11 - (sum.get() % 11);
        char dv = switch(ndv){
          case 11 -> '0';
          case 10 -> 'K';
          default -> Character.forDigit(ndv, 10);
        };
        return dv == partes[1].charAt(0);
    }

   public static <T> List<String> obtenerCamposFaltantes(T input, List<String> excludeFields){

        Set<String> exclude = (excludeFields == null)
                ? Set.of()
                : excludeFields.stream()
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());

        return Arrays.stream(input.getClass().getDeclaredMethods())
                .filter(m -> m.getName().startsWith("get") && !m.getName().equals("getClass"))
                .filter(m -> {
                    String fieldName = m.getName().substring(3).toLowerCase();
                    return !exclude.contains(fieldName);
                })
                .filter(m -> {
                    try {
                        return switch (m.invoke(input)) {
                            case null -> true;
                            case String s -> s.isBlank();
                            case Integer i -> i == 0;
                            default -> false;
                        };
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new PreviredRuntimeException(e);
                    }
                })
                .map(m -> m.getName().substring(3).toLowerCase())
                .toList();
    }
}
