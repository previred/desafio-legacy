package cl.maraneda.previred.converter;

import cl.maraneda.previred.exceptions.PreviredRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converter {

    private Converter() {}

    public static <C1, C2> C2 convert(Class<C2> destClass, C1 source){
        List<Method> getters =
            Arrays.stream(source.getClass().getMethods())
                  .filter(x -> x.getName().startsWith("get") && !x.getName().equals("getClass"))
                  .toList();
        List<Method> setters =
            Arrays.stream(destClass.getMethods())
                    .filter(x -> x.getName().startsWith("set"))
                    .toList();
        Map<String, Method> getterMap = getters.stream()
                .collect(Collectors.toMap(Method::getName, m -> m));
        try {
            C2 dest = destClass.getDeclaredConstructor().newInstance();
            setters.forEach(s -> {
                try {
                    Method getter = getterMap.get(s.getName().replace("set", "get"));
                    if(getter == null){
                        return;
                    }
                    Object val = getter.invoke(source);
                    s.invoke(dest, val);
                } catch (Exception e) {
                    throw new PreviredRuntimeException(e);
                }
            });
            return dest;
        }catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new PreviredRuntimeException(e);
        }
    }

    public static <C1, C2> List<C2> convert(
            Class<C2> destClass, List<C1> source
    ){
        return source.stream()
                     .map(x -> convert(destClass, x))
                     .toList();
    }
}
