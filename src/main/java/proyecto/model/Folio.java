package proyecto.model;

import proyecto.exception.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

// Cuenta de una reserva, esto es: saldo = cargos - pagos
// Los movimientos no se borran ni se editan, una corrección es un movimiento contrario
public class Folio {

    private ArrayList<Movimiento> cargos = new ArrayList<Movimiento>();
    private ArrayList<Movimiento> pagos = new ArrayList<Movimiento>();

    public void agregarCargo(String concepto, long valor, LocalDateTime fecha) throws PadreException {
        if (valor <= 0) {
            throw new DatoInvalidoException("El valor del cargo debe ser mayor que cero.");
        }
        cargos.add(new Movimiento(concepto, valor, fecha, null));
    }

    public void agregarPago(long valor, MedioPago medio, LocalDateTime fecha) throws PadreException {
        if (valor <= 0) {
            throw new DatoInvalidoException("El valor del pago debe ser mayor que cero.");
        }
        pagos.add(new Movimiento("Pago", valor, fecha, medio));
    }

    // La devolución se guarda como un pago negativo
    public void agregarDevolucion(long valor, MedioPago medio, LocalDateTime fecha) {
        pagos.add(new Movimiento("Devolución", -valor, fecha, medio));
    }

    // Un movimiento ya fue corregido si existe otro que apunta a él
    private boolean yaCorregido(ArrayList<Movimiento> lista, Movimiento original) {
        for (Movimiento movimiento : lista) {
            if (movimiento.getCorrige() == original) {
                return true;
            }
        }
        return false;
    }

    public void corregirCargo(int posicion, String motivo, LocalDateTime fecha) throws PadreException {
        Movimiento original = cargos.get(posicion);
        if (yaCorregido(cargos, original) || original.getValor() < 0) {
            throw new ReglaNegocioException("Ese cargo ya fue corregido o es una correccion.");
        }
        cargos.add(new Movimiento("Correccion de '" + original.getConcepto() + "': " + motivo, -original.getValor(), fecha, null, original));
    }

    public void corregirPago(int posicion, String motivo, LocalDateTime fecha) throws PadreException {
        Movimiento original = pagos.get(posicion);
        if (yaCorregido(pagos, original) || original.getValor() < 0) {
            throw new ReglaNegocioException("Ese pago ya fue corregido o es una devolucion/correccion.");
        }
        pagos.add(new Movimiento("Correccion de pago: " + motivo, -original.getValor(), fecha, original.getMedio(), original));
    }

    public ArrayList<Movimiento> getCargos() {
        return new ArrayList<Movimiento>(cargos);
    }

    public ArrayList<Movimiento> getPagos() {
        return new ArrayList<Movimiento>(pagos);
    }

    public long totalCargos() {
        long total = 0;
        for (Movimiento m : cargos) {
            total = total + m.getValor();
        }
        return total;
    }

    public long totalPagos() {
        long total = 0;
        for (Movimiento m : pagos) {
            total = total + m.getValor();
        }
        return total;
    }

    public long saldo() {
        return totalCargos() - totalPagos();
    }

    public MedioPago medioDelUltimoPago() {
        for (int i = pagos.size() - 1; i >= 0; i--) {
            if (pagos.get(i).getValor() > 0) {
                return pagos.get(i).getMedio();
            }
        }
        return MedioPago.TRANSFERENCIA;
    }
}