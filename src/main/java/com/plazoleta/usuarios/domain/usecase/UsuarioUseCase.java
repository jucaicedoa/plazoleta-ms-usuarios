package com.plazoleta.usuarios.domain.usecase;

import com.plazoleta.usuarios.domain.api.UsuarioServicePort;
import com.plazoleta.usuarios.domain.exception.CampoInvalidoException;
import com.plazoleta.usuarios.domain.exception.EmailInvalidoException;
import com.plazoleta.usuarios.domain.exception.UsuarioMayorDeEdadException;
import com.plazoleta.usuarios.domain.model.DatosCreacionUsuario;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.domain.spi.PasswordEncoderPort;
import com.plazoleta.usuarios.domain.spi.UsuarioPersistencePort;

import java.time.LocalDate;
import java.time.Period;

public class UsuarioUseCase implements UsuarioServicePort {

    private final UsuarioPersistencePort persistencePort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UsuarioUseCase(UsuarioPersistencePort persistencePort, PasswordEncoderPort passwordEncoderPort) {
        this.persistencePort = persistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void crearPropietario(DatosCreacionUsuario datos) {
        validar(datos);
        // Encriptar la clave antes de crear el usuario
        String claveEncriptada = passwordEncoderPort.encode(datos.getClave());
        // Crear datos actualizados con clave encriptada
        DatosCreacionUsuario datosConClaveEncriptada = DatosCreacionUsuario.builder()
                .nombre(datos.getNombre())
                .apellido(datos.getApellido())
                .documento(datos.getDocumento())
                .celular(datos.getCelular())
                .fechaNacimiento(datos.getFechaNacimiento())
                .correo(datos.getCorreo())
                .clave(claveEncriptada)
                .restauranteId(null)
                .build();

        Rol rolPropietario = new Rol(null, "PROPIETARIO");
        Usuario usuario = Usuario.crear(datosConClaveEncriptada, rolPropietario);

        persistencePort.guardarUsuario(usuario);
    }

    @Override
    public void crearEmpleado(DatosCreacionUsuario datos) {
        validar(datos);
        String claveEncriptada = passwordEncoderPort.encode(datos.getClave());
        DatosCreacionUsuario datosConClaveEncriptada = DatosCreacionUsuario.builder()
                .nombre(datos.getNombre())
                .apellido(datos.getApellido())
                .documento(datos.getDocumento())
                .celular(datos.getCelular())
                .fechaNacimiento(datos.getFechaNacimiento())
                .correo(datos.getCorreo())
                .clave(claveEncriptada)
                .restauranteId(datos.getRestauranteId())
                .build();
        // Rol por nombre; en BD se persiste el role_id (adapter resuelve con findByName).
        Rol rolEmpleado = new Rol(null, "EMPLEADO");
        Usuario usuario = Usuario.crear(datosConClaveEncriptada, rolEmpleado);
        persistencePort.guardarUsuario(usuario);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Integer id) {
        return persistencePort.obtenerUsuarioPorId(id);
    }

    private void validar(DatosCreacionUsuario datos) {
        if (!datos.getDocumento().matches("\\d+"))
            throw new CampoInvalidoException("Documento inválido");

        if (!datos.getCorreo().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$"))
            throw new EmailInvalidoException();

        if (!datos.getCelular().matches("^\\+?\\d{1,13}$"))
            throw new CampoInvalidoException("Celular inválido");

        if (Period.between(datos.getFechaNacimiento(), LocalDate.now()).getYears() < 18)
            throw new UsuarioMayorDeEdadException();

        if (persistencePort.existeCorreo(datos.getCorreo()))
            throw new CampoInvalidoException("Correo ya registrado");
    }
}