package com.robotech.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import com.robotech.web.dao.IClubDao;
import com.robotech.web.dao.IVictoriasDao;
import com.robotech.web.models.Categoria;
import com.robotech.web.models.Club;
import com.robotech.web.models.Enfrentamiento;
import com.robotech.web.models.Estado_membresia;
import com.robotech.web.models.Estado_torneo;
import com.robotech.web.models.Estado_usuario;
import com.robotech.web.models.Fase;
import com.robotech.web.models.Inscripciones;
import com.robotech.web.models.Membresia_club;
import com.robotech.web.models.Tipo_usuario;
import com.robotech.web.models.Torneo;
import com.robotech.web.models.Usuario;
import com.robotech.web.services.CategoriaService;
import com.robotech.web.services.ClubService;
import com.robotech.web.services.EnfrentamientoService;
import com.robotech.web.services.Estado_membresiaService;
import com.robotech.web.services.Estado_torneoService;
import com.robotech.web.services.Estado_usuarioService;
import com.robotech.web.services.FaseService;
import com.robotech.web.services.InscripcionesService;
import com.robotech.web.services.Membresia_ClubService;
import com.robotech.web.services.Tipo_usuarioService;
import com.robotech.web.services.TorneoService;
import com.robotech.web.services.UploadFileService;
import com.robotech.web.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RobotechTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private Tipo_usuarioService tipoUsuarioService;
	@Autowired
	private Estado_usuarioService estadoUsuarioService;
	@Autowired
	private CategoriaService categoriaServicePer;
	@Mock
	private CategoriaService categoriaService;
	@Mock
	private IVictoriasDao victoriasDao;
	@Mock
	private UploadFileService uploadService;
	@Mock
	private ClubService clubService;
	@Mock
	private IClubDao clubDao;
	@Mock
	private Estado_membresiaService estadoMembresiaService;
	@Mock
	private Membresia_ClubService membresiaClubService;
	@Mock
	private TorneoService torneoService;
	@Mock
	private InscripcionesService inscripcionesService;
	@Mock
	private EnfrentamientoService enfrentamientoService;
	@Mock
	private UsuarioService usuarioService;
	@Mock
	private Estado_torneoService estadoTorneoService;
	@Mock
	private FaseService faseService;

	private Usuario usuarioDePrueba;

	private Usuario usuarioDePruebaLogin;
	private Usuario usuarioAdmin;

	private MockHttpSession sessionA;
	private MockHttpSession session;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this); // Inicializa los mocks manualmente
	}

	@BeforeEach
	void setUpa() {
		estadoUsuarioService = mock(Estado_usuarioService.class);
	}
	

	@BeforeEach
	public void setUp() {
		usuarioDePrueba = new Usuario();
		usuarioDePrueba.setId(68);
		usuarioDePrueba.setNombre("Gian");
		usuarioDePrueba.setUsername("gian");
		usuarioDePrueba.setCorreo("gian@gmail.com");
		usuarioDePrueba.setPassword(passwordEncoder.encode("123456"));
		usuarioDePrueba.setEdad(25);
		usuarioDePrueba.setTrayectoria(5);
		usuarioDePrueba.setFoto_robot("robot.jpg");
		usuarioDePrueba.setFoto_perfil("perfil.jpg");

		Estado_usuario estadoU = new Estado_usuario();
		estadoU.setId(1);
		estadoU.setNombre("Aprobado");
		usuarioDePrueba.setEstadoUsuario(estadoU);

		usuarioDePrueba.setTipoUsuario(tipoUsuarioService.listarID(2));
		usuarioDePrueba.setCategoriaId(categoriaService.listarID(1));

	}

	@BeforeEach
	public void setUpLogin() {
		// Crear el usuario de prueba
		usuarioDePruebaLogin = new Usuario();
		usuarioDePruebaLogin.setId(84);
		usuarioDePruebaLogin.setNombre("Gian1");
		usuarioDePruebaLogin.setUsername("gian1");
		usuarioDePruebaLogin.setCorreo("gian1@gmail.com");
		usuarioDePruebaLogin.setPassword(passwordEncoder.encode("123456"));
		usuarioDePruebaLogin.setEdad(25);
		usuarioDePruebaLogin.setTrayectoria(5);
		usuarioDePruebaLogin.setFoto_robot("robot.jpg");
		usuarioDePruebaLogin.setFoto_perfil("perfil.jpg");

		usuarioDePruebaLogin.setTipoUsuario(tipoUsuarioService.listarID(2));
		usuarioDePruebaLogin.setEstadoUsuario(estadoUsuarioService.listarID(1));
		
		
		usuarioDePruebaLogin.setCategoriaId(categoriaService.listarID(1));

		// Guardar el usuario en la base de datos de prueba
		usuarioService.guardarUsuario(usuarioDePruebaLogin); // Asegúrate de que el servicio está disponible para
																// guardar el usuario

		session = new MockHttpSession();
		session.setAttribute("usuario", usuarioDePruebaLogin);
	}

	@BeforeEach
	public void setUpAdmin() {
		// Crear usuario administrador
		usuarioAdmin = new Usuario();
		usuarioAdmin.setId(122);
		usuarioAdmin.setNombre("Admin");
		usuarioAdmin.setUsername("admin123");
		usuarioAdmin.setCorreo("admin@gmail.com");
		usuarioAdmin.setPassword(passwordEncoder.encode("123456"));
		usuarioAdmin.setEdad(25);
		usuarioAdmin.setTrayectoria(10);
		usuarioAdmin.setFoto_perfil("perfil.jpg");

		// Asignar Tipo de Usuario (Administrador)
		Tipo_usuario tipo = new Tipo_usuario();
		tipo.setId(1); // ID del rol Admin
		tipo.setNombre("Administrador");
		usuarioAdmin.setTipoUsuario(tipo);

		// Estado de Usuario
		Estado_usuario estadoU = new Estado_usuario();
		estadoU.setId(1);
		estadoU.setNombre("Aprobado");
		usuarioAdmin.setEstadoUsuario(estadoU);

		// Asignar categoría
		Categoria cat = new Categoria();
		cat.setId(1);
		cat.setNombre("Amateur");
		usuarioAdmin.setCategoriaId(cat);

		// Simular sesión con usuario autenticado
		sessionA = new MockHttpSession();
		sessionA.setAttribute("usuario", usuarioAdmin);

		// Simular autenticación con permisos adecuados
		List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("Administrador"));
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(usuarioAdmin, null, authorities));
		SecurityContextHolder.setContext(securityContext);
	}

	// TEST CU2: VER COMUNIDAD SIN TEMA
	@Test
	@WithMockUser(username = "gian@gmail.com", roles = "USUARIO") // Simula un usuario autenticado
	public void testVerComunidad_SinTema() throws Exception {
		mockMvc.perform(get("/home/comunidad")).andExpect(status().isOk()) // Ahora debería devolver 200 OK en lugar de
																			// 302
				.andExpect(view().name("home/comunidad")).andExpect(model().attributeExists("comunidad"))
				.andExpect(model().attributeExists("temas")).andExpect(model().attribute("tema", (String) null));

	}

	// TEST CU2: VER COMUNIDAD CON TEMA
	@Test
	@WithMockUser(username = "gian@gmail.com", roles = "USUARIO") // Simula un usuario autenticado
	public void testVerComunidad_ConTema() throws Exception {
		mockMvc.perform(get("/home/comunidad").param("tema", "Robots de pelea")).andExpect(status().isOk())
				.andExpect(view().name("home/comunidad")).andExpect(model().attributeExists("comunidad"))
				.andExpect(model().attributeExists("temas")).andExpect(model().attribute("tema", "Robots de pelea"));
	}

	// TEST CU3: AGREGAR COMENTARIO
	@Test
	@Transactional
	public void testGuardarComentario_SinAutenticacion() throws Exception {
		mockMvc.perform(post("/home/comunidad/addcomentario").param("tema", "Robots de Combate")
				.param("comentario", "Este es un comentario de prueba.").with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/home/comunidad"))
				.andExpect(flash().attribute("error", "Debes estar registrado para comentar."));
	}

	// TEST CU3: AGREGAR COMENTARIO CON IMAGEN
	@Test
	@Transactional
	@WithMockUser(username = "gian@gmail.com", roles = "USUARIO")
	public void testGuardarComentario_ExitosoConImagen() throws Exception {
		MockMultipartFile imagen = new MockMultipartFile("img", "robot.jpg", "image/jpeg",
				"contenido de prueba".getBytes());

		// Realiza la solicitud POST directamente sin necesidad de obtener manualmente
		// el token CSRF
		mockMvc.perform(multipart("/home/comunidad/addcomentario").file(imagen).param("tema", "Robots de Combate")
				.param("comentario", "¡Este robot es increíble!").with(csrf()) // Aquí el token CSRF se añadirá
																				// automáticamente
				.sessionAttr("usuario", usuarioDePrueba)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home/comunidad"))
				.andExpect(flash().attribute("success", "El comentario se agregó correctamente."));
	}

	// TEST CU4 : VER TABLA DE MÉRITO
	@Test
	@WithMockUser(username = "gian@gmail.com", roles = "USUARIO") // Simula un usuario autenticado
	public void testVerTablaMerito() throws Exception {
		mockMvc.perform(get("/home/clasificacion").param("page", "0").param("size", "6")).andExpect(status().isOk()) // Debe
																														// retornar
																														// 200
																														// OK
				.andExpect(view().name("home/clasificacion")).andExpect(model().attributeExists("victorias"))
				.andExpect(model().attributeExists("currentPage")).andExpect(model().attributeExists("size"))
				.andExpect(model().attributeExists("totalPages")).andExpect(model().attributeExists("totalElements"));
	}

	// TEST CU5: REGISTRO (VER VISTA)
	@Test
	public void testAccederVistaRegistro() throws Exception {
		mockMvc.perform(get("/registrar")).andExpect(status().isOk()) // El status esperado es 200 OK
				.andExpect(view().name("registro")) // La vista esperada es "registro"
				.andExpect(model().attributeExists("usuario")); // Debe haber un atributo "usuario" en el modelo
	}

	// TEST CU5: REGISTRO CON CAMPOS VACÍOS O INVÁLIDOS
	@Test
	public void testRegistrarConCamposInvalidos() throws Exception {
		// Crear un archivo vacío para simular la carga de un archivo
		MockMultipartFile imagen = new MockMultipartFile("file", "robot.jpg", "image/jpeg", new byte[0] // Archivo vacío
		);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar").file(imagen) // Añadir archivo vacío
				.param("nombre", "Gian").param("username", "gian").param("correo", "gian@gmail.com")
				.param("password", "123456").param("trayectoria", "5").param("edad", "17") // Edad inválida
				.with(csrf())) // Incluir CSRF
				.andExpect(status().isOk()) // Status debe ser 200 OK
				.andExpect(view().name("registro")) // Se debe mostrar la vista de registro
				.andExpect(model().attribute("error", "Debes ser mayor de 18 años para registrarte.")); // Error de
																										// validación de
																										// edad
	}

	// TEST CU5: REGISTRO CON CORREO DUPLICADO
	@Test
	public void testRegistrarConCorreoDuplicado() throws Exception {
		// Crear un archivo vacío para simular la carga de un archivo
		MockMultipartFile imagen = new MockMultipartFile("file", "robot.jpg", "image/jpeg",
				"contenido de prueba".getBytes() // Archivo vacío
		);

		// Simular que el correo ya está registrado
		mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar").file(imagen) // Archivo vacío
				.param("nombre", "Gian").param("username", "gian").param("correo", "gian@gmail.com") // Correo duplicado
				.param("password", "123456").param("trayectoria", "5").param("edad", "25")
				.param("foto_robot", "robot_default.jpg") // Parámetro de foto_robot
				.with(csrf())) // Incluir CSRF
				.andExpect(status().isOk()) // Status debe ser 200 OK
				.andExpect(view().name("registro")) // Se debe mostrar la vista de registro
				.andExpect(model().attribute("error", "El correo ya está registrado. Por favor, utliza otro correo.")); // Error
																														// de
																														// correo
																														// duplicado
	}

	// TEST CU5: REGISTRO CON USERNAME DUPLICADO
	@Test
	public void testRegistrarConUsernameDuplicado() throws Exception {
		MockMultipartFile imagen = new MockMultipartFile("file", "robot.jpg", "image/jpeg",
				"contenido de prueba".getBytes() // Archivo con contenido de prueba
		);

		// Simular que el username ya está registrado
		mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar").file(imagen) // Archivo con contenido de prueba
				.param("nombre", "Gian").param("username", "gian") // Username duplicado
				.param("correo", "gian.nuevo@gmail.com") // Correo que no está registrado
				.param("password", "123456").param("trayectoria", "5").param("edad", "25")
				.param("foto_robot", "robot_default.jpg").with(csrf())) // Incluir CSRF
				.andExpect(status().isOk()) // Status debe ser 200 OK
				.andExpect(view().name("registro")) // Se debe mostrar la vista de registro
				.andExpect(model().attribute("error", "El username ya está en uso. Por favor, elige otro username.")); // Error
																														// de
																														// username
																														// duplicado
	}

	// TEST CU5: REGISTRO EXITOSO
	@Transactional
	@Test
	public void testRegistrarExitoso() throws Exception {
		MockMultipartFile imagen = new MockMultipartFile("file", "robot.jpg", "image/jpeg",
				"contenido de prueba".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar").file(imagen) // Imagen válida
				.param("nombre", "Gian").param("username", "gianNuevo").param("correo", "gian.nuevo@gmail.com")
				.param("password", "123456").param("trayectoria", "10").param("edad", "25").with(csrf())) // Incluir
																											// CSRF
				.andExpect(status().is3xxRedirection()) // Verifica que la respuesta sea una redirección (3xx)
				.andExpect(redirectedUrl("/login")) // Se espera redirección al login
				.andExpect(flash().attribute("registroExitoso",
						"¡Tu cuenta ha sido registrada con éxito! Inicia sesión.")); // Mensaje de éxito
	}

	// TEST CU6 : INICIAR SESION EXITOSO
	@Test
	public void testLoginExitoso() throws Exception {
		String correo = "gian1@gmail.com";
		String password = "123456";

		mockMvc.perform(post("/login").param("username", correo) // Usar "username" si no has personalizado el parámetro
				.param("password", password).with(csrf()) // Incluir CSRF
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().is3xxRedirection()) // Redirección
																											// esperada
				.andExpect(redirectedUrl("/home")); // Verificar la URL de redirección
	}

	// TEST CU6 : INICIAR SESION FALLIDO
	@Test
	public void testLoginFallido() throws Exception {
		// Intentar hacer login con credenciales incorrectas
		String correo = "gian1@gmail.com"; // Correo correcto
		String password = "incorrecta"; // Contraseña incorrecta

		mockMvc.perform(post("/login").param("username", correo) // Usar "username" si no has personalizado el parámetro
				.param("password", password) // Contraseña incorrecta
				.with(csrf()) // Incluir CSRF
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().is3xxRedirection()) // Redirección
																											// esperada
				.andExpect(redirectedUrl("/login?error=true")); // Se espera redirección a login con error=true
	}

	// TEST CU07 : RECUPERAR CONTRASESÑA MOSTRAR VISTA

	@Test
	public void testMostrarFormularioRecuperacion() throws Exception {
		mockMvc.perform(get("/forgot-password")).andExpect(status().isOk()) // Esperamos que la respuesta sea exitosa
				.andExpect(view().name("login/forgot-password")); // Se espera la vista "login/forgot-password"
	}

	// TEST CU07 : RECUPERAR CONTRASESÑA CORREO NO REGISTRADO
	@Test
	public void testVerificarCorreoNoRegistrado() throws Exception {
		String email = "noexiste@gmail.com"; // Correo no registrado

		mockMvc.perform(post("/forgot-password").param("email", email) // Correo a verificar
				.with(csrf())) // Incluir CSRF
				.andExpect(status().is3xxRedirection()) // Redirección esperada
				.andExpect(redirectedUrl("/forgot-password")) // Se espera redirección al formulario
				.andExpect(flash().attribute("error", "No se encontró un usuario con ese correo electrónico.")); // Mensaje
																													// de
																													// error
	}

	// TEST CU07 : RECUPERAR CONTRASESÑA ENVIAR ENLACE
	@Test
	public void testVerificarCorreoYEnviarEnlace() throws Exception {
		// Suponiendo que "gian@gmail.com" es un correo registrado
		String email = "gian@gmail.com";

		mockMvc.perform(post("/forgot-password").param("email", email) // Correo registrado
				.with(csrf())) // Incluir CSRF
				.andExpect(status().is3xxRedirection()) // Redirección esperada
				.andExpect(redirectedUrl("/forgot-password")) // Se espera redirección al formulario
				.andExpect(flash().attribute("success", "Se ha enviado un enlace de recuperación a tu correo.")); // Mensaje
																													// de
																													// éxito
	}

	// TEST CU07 : RECUPERAR CONTRASEÑA TOKEN INCORRECTO
    @Test
    @Transactional
    public void testMostrarFormularioRestablecerConTokenInvalido() throws Exception {
        String token = "invalidToken";  // Token inválido

        mockMvc.perform(get("/reset-password")
                .param("token", token))  // Token inválido
            .andExpect(status().is3xxRedirection())  // Redirección esperada
            .andExpect(redirectedUrl("/forgot-password"))  // Redirección al formulario de recuperación
            .andExpect(flash().attribute("error", "El enlace de recuperación es inválido o ha expirado."));  // Mensaje de error
    }
    
 // TEST CU08 : VER PERFIL SESION INICIADA
    @Test
    public void testPerfilUsuarioConUsuarioEnSesion() throws Exception {
        // Asignar la categoría dentro del test
    	 Categoria categoria = categoriaServicePer.listarID(1);
    	    if (categoria == null) {
    	        throw new RuntimeException("❌ La categoría con ID 1 no existe en la base de datos.");
    	    }
        usuarioDePruebaLogin.setCategoriaId(categoria); // 🔥 Asignar categoría antes de la sesión

        // Crear sesión y agregar usuario
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", usuarioDePruebaLogin);

        // Verificar que el usuario en sesión tiene categoría asignada
        Usuario usuarioEnSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioEnSesion.getCategoriaId() == null) {
            throw new RuntimeException("❌ El usuario en sesión no tiene categoría asignada.");
        }

        // Simular autenticación en Spring Security
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(usuarioDePruebaLogin, null, new ArrayList<>()));
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        // Ejecutar la prueba con sesión autenticada
        mockMvc.perform(get("/home/perfil").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("home/verPerfil"))
            .andExpect(model().attribute("usuario", usuarioDePruebaLogin))
            .andExpect(model().attribute("cantidadVictorias", 0));
    }
    
 // TEST CU08 : VER PERFIL SIN USUARIO EN SESION
    @Test
    @Transactional
    public void testPerfilUsuarioSinUsuarioEnSesion() throws Exception {
        // Realizar la solicitud GET sin sesión
        mockMvc.perform(get("/home/perfil"))
            .andExpect(status().is3xxRedirection())  // Esperamos un código 302 (Redirección)
            .andExpect(redirectedUrl("/login"));  // Verificar redirección a login
    }
    
 // TEST CU09 : MODIFICAR PERFIL CON USERNAME Y SIN IMAGEN
    @Test
    @Transactional
    public void testActualizarPerfil_ConUsernameDisponible_Y_SinImagen() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", usuarioDePrueba);

        lenient().when(usuarioService.existeUsername("nuevoUsername")).thenReturn(false);

        mockMvc.perform(post("/home/perfil/editar")
                .param("username", "nuevoUsername")
                .session(session)
                .with(csrf()) // CSRF obligatorio para POST
                .header("X-Requested-With", "XMLHttpRequest")) // Simula AJAX
            .andExpect(status().isFound())  // 302 Redirect en lugar de 200
            .andExpect(redirectedUrl("/home/perfil"))  // Verifica la URL de redirección
            .andExpect(flash().attributeExists("success"))
            .andExpect(flash().attribute("success", "Perfil actualizado exitosamente"));
    }
    
  //TEST CU10: VER CLUBS
    @Test
    public void testListarClubes() throws Exception {
        // Simula una solicitud GET a /clubes
        mockMvc.perform(get("/home/clubes"))
               .andExpect(status().isOk()) // Verifica que el estado de la respuesta sea 200 OK
               .andExpect(view().name("home/club")) // Verifica que la vista devuelta sea "home/club"
               .andExpect(model().attributeExists("club")); // Verifica que el modelo contenga el atributo "club"
    }
    
 // TEST CU10: VER CLUB ID SIN INICIAR SESION

    @Test
    public void testListarClubId_UsuarioNoAutenticado() throws Exception {
        // Simula una solicitud GET a /clubes/{id} sin un usuario autenticado
        mockMvc.perform(get("/home/clubes/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("home/clubDetalles"))
               .andExpect(model().attributeExists("mensajeCuenta")); // Verifica que se muestre el mensaje de cuenta requerida
    }
    
 // TEST CU11 : SOLICITUD DE INGRESO A CLUB SIN SESION
    @Test
    public void testInscribirseClub_SinSesion() throws Exception {
        mockMvc.perform(post("/home/clubes/1/inscribirse")
                .with(csrf())) // Si CSRF está habilitado en tu configuración
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home/clubes/1")) // Ajusta según el destino real de la redirección
            .andExpect(result -> {
                FlashMap flashMap = result.getFlashMap();
                assertNotNull(flashMap);
                assertTrue(flashMap.containsKey("mensajeError"));
            });
    }
    
 // TEST CU11 : SOLICITUD DE INGRESO A CLUB 
    @Test
    public void testInscribirseClub_SolicitudNueva() throws Exception {
        int clubId = 1;
        Club club = new Club();
        club.setId(clubId);

        // Simular que no existe membresía rechazada (pero con lenient para evitar UnnecessaryStubbingException)
        lenient().when(membresiaClubService.obtenerMembresiaRechazadaPorUsuario(usuarioDePruebaLogin.getId()))
                 .thenReturn(null);
        lenient().when(clubService.listarID(clubId)).thenReturn(club);

        // Estado "Pendiente"
        Estado_membresia estadoPendiente = new Estado_membresia();
        estadoPendiente.setId(3);
        estadoPendiente.setNombre("Pendiente");
        lenient().when(estadoMembresiaService.listarID(3)).thenReturn(estadoPendiente);

        // Ejecutar la solicitud POST simulada con la sesión
        mockMvc.perform(post("/home/clubes/1/inscribirse")
                .session(session) // Se usa la sesión inicializada en @BeforeEach
                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/home/clubes/1"))
               .andExpect(flash().attributeExists("mensajeExito"));
    }

  //TEST CU12 : VER TORNEOS
    @Test
    void testListarTorneos() throws Exception {
        int page = 0;
        int size = 10;
        
        mockMvc.perform(get("/home/torneos")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
            .andExpect(status().isOk())
            .andExpect(view().name("home/torneo"))
            .andExpect(model().attributeExists("torneo"))
            .andExpect(model().attribute("torneo", Matchers.notNullValue()));
    }
    
  //TEST CU12 : VER TORNEOS SIN INICIAR SESION
    @Test
    void testListarTorneosId_UsuarioNoAutenticado() throws Exception {
        int torneoId = 1;

        // NO mockeamos servicios si no los usa el controlador en este caso específico.

        mockMvc.perform(get("/home/torneos/{id}", torneoId).with(csrf())) // No se envía sesión
            .andExpect(status().isOk())
            .andExpect(view().name("home/torneoDetalles"))
            .andExpect(model().attributeExists("mensajeCuenta"))
            .andExpect(model().attribute("mensajeCuenta", "Debe tener una cuenta registrada con nosotros."));
    }
    
    @Test
    void testTorneosId_UsuarioSinMembresia() throws Exception {
        int torneoId = 1;
        Usuario usuario = new Usuario();
        usuario.setId(10);

        mockMvc.perform(get("/home/torneos/{id}", torneoId)
                .sessionAttr("usuario", usuario))
            .andExpect(status().isOk())
            .andExpect(view().name("home/torneoDetalles"))
            .andExpect(model().attributeExists("mensajeMembresia"))
            .andExpect(model().attribute("mensajeMembresia", "Usted no pertenece a ningún club."));
    }
    
 // TEST CU13 : VER TORNEO INSCRITO EN OTRO TORNEO
    @Test
    void testTorneosId_UsuarioInscritoOtroTorneo() throws Exception {

        int torneoId = 1;
        Usuario usuario = new Usuario();
        usuario.setId(1);

        // Asignar categoría al usuario
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Amateur");
        usuario.setCategoriaId(categoria);

        Torneo torneo = new Torneo();
        torneo.setId(torneoId);
        torneo.setJugReq(8); // Límite de jugadores

        Estado_membresia estadoMembresia = new Estado_membresia();
        estadoMembresia.setId(2);
        estadoMembresia.setNombre("Aprobado");

        Membresia_club membresia = new Membresia_club();
        membresia.setEstadoMembresiaId(estadoMembresia); // Corregido

        Torneo torneoAnterior = new Torneo();
        torneoAnterior.setNombre("Torneo Anterior");

        Inscripciones otraInscripcion = new Inscripciones();
        otraInscripcion.setTorneoId(torneoAnterior); // Corregido

        mockMvc.perform(get("/home/torneos/{id}", torneoId)
                .sessionAttr("usuario", usuario))
            .andExpect(status().isOk())
            .andExpect(view().name("home/torneoDetalles"));
    }
    
 // TEST CU13 : PARTICIPAR EN  UN TORNEO
    @Test
    void testTorneosId_UsuarioPuedeInscribirse() throws Exception {
        int torneoId = 1;
        Usuario usuario = new Usuario();
        usuario.setId(1);
        
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Amateur");
        
        usuario.setCategoriaId(categoria);

        Torneo torneo = new Torneo();
        torneo.setId(torneoId);
        torneo.setJugReq(8);
        torneo.setCategoriaId(categoria);
        
        Estado_membresia estadoMembresia = new Estado_membresia();
        estadoMembresia.setId(1);
        estadoMembresia.setNombre("Aprobado");
        
        Club club = new Club();
        club.setId(1);
        club.setNombre("IronWarriors");

        Membresia_club membresia = new Membresia_club();
        membresia.setUsuarioId(usuario);
        membresia.setEstadoMembresiaId(estadoMembresia);
        membresia.setClubId(club);

        lenient().when(torneoService.listarID(torneoId)).thenReturn(torneo);
        lenient().when(membresiaClubService.obtenerPorUsuario(usuario.getId())).thenReturn(membresia);
        lenient().when(inscripcionesService.existeInscripcion(usuario.getId(), torneoId)).thenReturn(false);

        mockMvc.perform(get("/home/torneos/{id}", torneoId)
                .sessionAttr("usuario", usuario))
            .andExpect(status().isOk())
            .andExpect(view().name("home/torneoDetalles"));
    }
    
 // TEST CU14 : VER USUARIOS EN ESPERA SIN INICIAR SESION
    @Test
    void testMostrarUsuariosEnEspera_SinSesion() throws Exception {
        mockMvc.perform(get("/admin/usuarios-enespera"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login")); // Acepta URLs absolutas o relativas
    }
    
 // TEST CU14 : VER USUARIOS EN ESPERA
    @Test
    void testMostrarUsuariosEnEspera_ListaConUsuarios() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1);
        usuario1.setNombre("Usuario 1");
        Usuario usuario2 = new Usuario();
        usuario2.setId(2);
        usuario2.setNombre("Usuario 2");

        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        Page<Usuario> page = new PageImpl<>(usuarios, PageRequest.of(0, 5), usuarios.size());

        lenient().when(usuarioService.listtarUsuariosEnEspera(0, 5)).thenReturn(page);

        mockMvc.perform(get("/admin/usuarios-enespera").sessionAttr("usuario", new Usuario()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(2)))
                .andExpect(model().attributeExists("currentPage", "totalPages", "size"))
                .andExpect(view().name("admin/usuariosAdmin"));
    }
    
    // TEST CU14 : ACTUALIZAR ESTADO DE USUARIO EN ESPERA
    @Test
    @Transactional
    void testCambiarEstadoUsuarioDeEsperaAAprobado() throws Exception {
        // Datos de prueba
        Integer usuarioId = 84;
        Integer estadoEnEsperaId = 3;
        Integer estadoAprobadoId = 1;

        // Crear usuario con estado "En espera"
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        Estado_usuario estadoEnEspera = new Estado_usuario();
        estadoEnEspera.setId(estadoEnEsperaId);
        estadoEnEspera.setNombre("En Espera");
        usuario.setEstadoUsuario(estadoEnEspera);

        // Estado "Aprobado"
        Estado_usuario estadoAprobado = new Estado_usuario();
        estadoAprobado.setId(estadoAprobadoId);
        estadoAprobado.setNombre("Aprobado");

        // Simular el comportamiento del servicio
        lenient().when(usuarioService.listarID(usuarioId)).thenReturn(usuario);
        lenient().when(estadoUsuarioService.listarID(estadoAprobadoId)).thenReturn(estadoAprobado);

        // Simular la actualización del usuario en la base de datos
        lenient().doAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            System.out.println("GuardarUsuario llamado con Usuario ID: " + u.getId()); // Depuración
            if (u.getId().equals(usuarioId)) {
                usuario.setEstadoUsuario(estadoAprobado);
                System.out.println("Estado actualizado a: " + usuario.getEstadoUsuario().getId()); // Depuración
            }
            return null;
        }).when(usuarioService).guardarUsuario(any(Usuario.class));

        // Simular la solicitud POST con sesión de administrador
        mockMvc.perform(post("/admin/usuarios/cambiarEstado")
                .session(sessionA) // Sesión de administrador
                .with(csrf()) // CSRF token
                .param("usuarioId", usuarioId.toString()) // Enviar como lista (un solo valor)
                .param("estadoId", estadoAprobadoId.toString())) // Enviar como lista (un solo valor)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/usuarios-enespera"));

        // 🔹 Imprimir el estado después de guardar para depuración
        System.out.println("Estado después de guardar: " + usuario.getEstadoUsuario().getId());

    }
    
    //TEST CU15 : CREAR TORNEO
    @Test
    @Transactional
    void testCrearTorneos_Exito() throws Exception {
        MockMultipartFile imagen = new MockMultipartFile("img", "imagen.jpg", "image/jpeg", "imagen".getBytes());
        String nombreTorneo = "Torneo Prueba";
        Categoria categoria = new Categoria();
        categoria.setId(1);
        Estado_torneo estadoTorneo = new Estado_torneo();
        estadoTorneo.setId(1);

        lenient().when(torneoService.torneoExiste(nombreTorneo)).thenReturn(false);
        lenient().when(uploadService.saveImage(any(MultipartFile.class))).thenReturn("imagen.jpg");
        lenient().when(categoriaService.listarID(1)).thenReturn(categoria);
        lenient().when(estadoTorneoService.listarID(1)).thenReturn(estadoTorneo);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/torneos/crearTorneos")
                .file(imagen)
                .param("nombre", nombreTorneo)
                .param("descripcion", "Descripción de prueba")
                .param("centro", "Centro de competencia")
                .param("fecha", "2025-03-01")
                .param("jugReq", "8")
                .param("categoriaId", "1")
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("/admin/torneos"));
    }
    
    //TEST CU16 : CREAR CLUB
    @Test
    @Transactional
    void testCrearClub_Exito() throws Exception {
        MockMultipartFile imagen = new MockMultipartFile("img", "imagen.jpg", "image/jpeg", "imagen".getBytes());
        String nombreClub = "Club Prueba";
        Usuario usuarioMock = new Usuario();

        lenient().when(clubService.existePorNombre(nombreClub)).thenReturn(false);
        lenient().when(usuarioService.obtenerUltimoUsuario3(3)).thenReturn(usuarioMock);
        lenient().when(uploadService.saveImage(any(MultipartFile.class))).thenReturn("imagen.jpg");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/clubs/crear")
                .file(imagen)
                .param("nombre", nombreClub)
                .param("descripcion", "Descripción del club")
                .with(csrf())
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("/admin/clubs"));
    }
    
    //TEST CU19 : GENERAR TURNOS
    @Test
    @Transactional
    public void testGenerarEnfrentamiento() throws Exception {
        int torneoId = 200;
        int faseId = 2;

        List<Enfrentamiento> enfrentamientos = Arrays.asList(new Enfrentamiento(), new Enfrentamiento());

        lenient().when(enfrentamientoService.obtenerPorTorneo(torneoId)).thenReturn(enfrentamientos);
        lenient().when(enfrentamientoService.generarEnfrentamientosAleatorios(eq(torneoId), any(Fase.class)))
                .thenReturn(enfrentamientos); // Corregido

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/admin/torneos/generar")
                .param("torneoId", String.valueOf(torneoId))
                .param("faseId", String.valueOf(faseId));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/verTorneo/" + torneoId));
    }
    
  //TEST CU20 : GENERAR TURNOS SEMIFINAL
    @Test
    @Transactional
    public void testGenerarSemifinal() throws Exception {
        int torneoId = 1;
        Fase faseSemifinal = new Fase();
        faseSemifinal.setId(2);
        faseSemifinal.setNombre("Semifinal");

        lenient().when(faseService.listarID(2)).thenReturn(faseSemifinal);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/admin/torneos/generarSemifinal")
                .param("torneoId", String.valueOf(torneoId))
                .session(sessionA)
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/verTorneo/" + torneoId));
    }
    
  //TEST CU20 : GENERAR TURNOS FINAL
    @Test
    @Transactional
    public void testGenerarFinal() throws Exception {
        int torneoId = 400;
        Fase faseFinal = new Fase();
        faseFinal.setId(3);
        faseFinal.setNombre("Final");

        lenient().when(faseService.listarID(3)).thenReturn(faseFinal);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/admin/torneos/generarFinal")
                .param("torneoId", String.valueOf(torneoId))
                .session(sessionA)
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/verTorneo/" + torneoId));
    }
    
}
