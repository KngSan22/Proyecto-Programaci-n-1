package proyecto.model;

import java.time.LocalDateTime;

// Lo que se pueda cancelar debe saber calcular cuanto se devuelve
public interface Cancelable {

    Liquidacion calcularLiquidacion(LocalDateTime ahora);
}