package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.*
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dto.UbicacionMongoDTO
import ar.edu.unq.eperdemic.services.UbicacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UbicacionServiceImpl() : UbicacionService {

    @Autowired
    private lateinit var distritoDAO: DistritoDAO

    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO

    @Autowired
    private lateinit var ubicacionNeoDAO: UbicacionNeoDAO

    @Autowired
    private lateinit var ubicacionMongoDAO: UbicacionMongoDAO

    @Autowired
    private lateinit var vectorDAO: VectorDAO

    override fun crearUbicacion(ubicacion: Ubicacion): Ubicacion {
        if (ubicacionDAO.cantUbicacionesConNombre(ubicacion.nombre) > 0L) {
            throw NombreDuplicadoExcepcion("El nombre de la ubicación ya existe.")
        }
        val distrito = distritoDAO.findDistritoPorCoordenadas(
            ubicacion.coordenada!!.first,
            ubicacion.coordenada!!.second
        )?: throw NoDistritosException("No hay distritos en esta coordenada")

        val ubicacionPersistida = ubicacionDAO.save(ubicacion)
        ubicacionNeoDAO.guardar(ubicacionPersistida.id!!, ubicacion.nombre)
        val ubicacionMongo = UbicacionMongoDTO.desdeModelo(ubicacionPersistida)
        ubicacionMongoDAO.save(ubicacionMongo)

        distrito.ubicacionesDistrito.add(ubicacionMongo)
        distritoDAO.save(distrito)
        return ubicacionPersistida
    }

    override fun actualizarUbicacion(ubicacion: Ubicacion): Ubicacion {
        if (ubicacion.id == null || ubicacionDAO.findByIdOrNull(ubicacion.id!!) == null)
            throw EntidadNoPersistidaException("Ubicacion no esta persistido")

        val ubicacionPersistida = ubicacionDAO.save(ubicacion)
        ubicacionNeoDAO.actualizar(ubicacionPersistida.id!!, ubicacion.nombre)
        this.actualizarUbicacionMongo(ubicacion)
        return ubicacionPersistida
    }

    private fun actualizarUbicacionMongo(ubicacion: Ubicacion) {
        val ubicacionMongo: UbicacionMongoDTO? = ubicacionMongoDAO.findByIdJPAOrIdJPANull(ubicacion.id!!)

        ubicacionMongo ?: throw EntidadNoPersistidaException("Ubicacion no esta persistido")

        ubicacionMongo.nombreUbicacion = ubicacion.nombre
        ubicacionMongo.coordenadas = GeoJsonPoint(ubicacion.coordenada!!.first, ubicacion.coordenada!!.second)

        ubicacionMongoDAO.save(ubicacionMongo)
    }

    override fun recuperarUbicacion(id: Long): Ubicacion {
        val ubicacion =
            ubicacionDAO.findByIdOrNull(id) ?: throw IdInvalidoExcepcion("Ubicacion con id: ${id} no existe")
        return ubicacion
    }

    override fun recuperarATodasLasUbicaciones(): List<Ubicacion> {
        return ubicacionDAO.findAll().toList()
    }

    override fun eliminarUbicacion(id: Long) {
        ubicacionDAO.deleteById(id)
        ubicacionNeoDAO.deleteByIdJPA(id)
    }

    override fun vectoresInfectados(nombreUbicacion: String): List<Vector> {
        return ubicacionDAO.vectoresInfectados(nombreUbicacion)
    }

    override fun ubicacionesInfectadas(): List<String> {
        return ubicacionDAO.ubicacionesInfectadas()
    }

    override fun mover(vectorId: Long, ubicacionId: Long) {
        val (vector, nuevaUbicacion) = validarVectorYUbicacion(vectorId, ubicacionId)

        if (!estaConectadaCon(vector.ubicacion.nombre, nuevaUbicacion.nombre))
            throw UbicacionMuyLejana("No es posible llegar a ${nuevaUbicacion.nombre}")

        if (existeLaUbicacionAMenosDe100KM(vector.ubicacion.id!!, ubicacionId) == null)
           throw UbicacionMuyLejana("No es posible llegar a ${nuevaUbicacion.nombre} por que esta a mas de 100KM")

        if (!esAlcanzable(vector, nuevaUbicacion.nombre))
            throw UbicacionNoAlcanzable("No es posible atravesar los caminos hasta ${nuevaUbicacion.nombre}")

        this.contagiarATodosEn(nuevaUbicacion.nombre, vector)

        vector.ubicacion = nuevaUbicacion
        vectorDAO.save(vector)
    }

    override fun moverPorCaminoMasCorto(vectorId: Long, nombreDeUbicacion: String) {

        val (vector, nuevaUbicacion) = validarVectorYUbicacion(vectorId, null, nombreDeUbicacion.lowercase())

        if (!esAlcanzable(vector, nuevaUbicacion.nombre))
            throw UbicacionNoAlcanzable("No es posible atravesar los caminos hasta ${nuevaUbicacion.nombre}")

        val ubicacionesIntermedias =
            nombresUbicacionesDelCaminoMasCorto(vector.ubicacion.nombre, nuevaUbicacion.nombre, vector)

        for (ubicacion in ubicacionesIntermedias) {
            this.contagiarATodosEn(ubicacion, vector)
        }

        vector.ubicacion = nuevaUbicacion
        vectorDAO.save(vector)
    }

    private fun validarVectorYUbicacion(
        vectorId: Long,
        ubicacionId: Long? = null,
        nombreDeUbicacion: String? = null
    ): Pair<Vector, Ubicacion> {

        val vector =
            vectorDAO.findByIdOrNull(vectorId) ?: throw IdInvalidoExcepcion("No existe vector con el id ${vectorId}")

        if (vector.ubicacion.nombre == nombreDeUbicacion) throw UbicacionInvalidaException("El vector ya esta en ${nombreDeUbicacion}")

        lateinit var nuevaUbicacion: Ubicacion;

        if (ubicacionId != null) {
            nuevaUbicacion =
                ubicacionDAO.findByIdOrNull(ubicacionId)
                    ?: throw IdInvalidoExcepcion("No existe ubicacion con el id ${ubicacionId}")
        } else if (nombreDeUbicacion != null) {
            nuevaUbicacion =
                ubicacionDAO.findByNombreOrNombreNull(nombreDeUbicacion)
                    ?: throw NombreInvalidoException("No existe ubicacion con el nombre ${nombreDeUbicacion}")
        }

        return Pair(vector, nuevaUbicacion)
    }

    override fun nombresUbicacionesDelCaminoMasCorto(
        nombreUbicacion1: String,
        nombreUbicacion2: String,
        vector: Vector
    ): List<String> {
        val caminoMasCorto = ubicacionNeoDAO.ubicacionesDelCaminoMasCorto(
            nombreUbicacion1.lowercase(),
            nombreUbicacion2.lowercase(),
            vector.caminosRecorribles()
        )

        if (caminoMasCorto.isEmpty())
            throw UbicacionNoAlcanzable("No es posible atravesar los caminos hasta ${nombreUbicacion2}")

        return caminoMasCorto
    }

    override fun expandir(ubicacionId: Long) {
        val ubicacion = ubicacionDAO.findByIdOrNull(ubicacionId)
            ?: throw IdInvalidoExcepcion("No existe ubicacion con el id ${ubicacionId}")
        val vectoresContagiados = ubicacionDAO.vectoresInfectados(ubicacion.nombre)

        if (vectoresContagiados.isNotEmpty()) {
            val vectorContagiadoRandom = Randomizador.getElemRandom(vectoresContagiados)

            if (vectorContagiadoRandom != null) {
                this.contagiarATodosEn(ubicacion.nombre, vectorContagiadoRandom)
            }
        }
    }

    private fun contagiarATodosEn(ubicacionNombre: String, vectorContagiador: Vector) {
        if (vectorContagiador.estaInfectado()) {

            vectorContagiador.eliminacionAutomatica = false

            val vectores = vectorDAO.recuperarVectoresEn(ubicacionNombre.lowercase())


            vectores.forEach { vector -> vector.contagiarseEnfermedadesDe(vectorContagiador) }
            vectorContagiador.eliminarEspeciesRecolectadas()
            vectorDAO.saveAll(vectores)

            vectorContagiador.eliminacionAutomatica = true
        }
    }

    override fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String) {
        ubicacionDAO.findByNombreOrNombreNull(nombreDeUbicacion1.lowercase())
            ?: throw NombreInvalidoException("No existe ubicacion ${nombreDeUbicacion1}")
        ubicacionDAO.findByNombreOrNombreNull(nombreDeUbicacion2.lowercase())
            ?: throw NombreInvalidoException("No existe ubicacion ${nombreDeUbicacion2}")
        if (!listOf("Terrestre", "Aereo", "Maritimo").contains(tipoCamino))
            throw TipoDeCaminoInvalido("tipo de camino: ${tipoCamino} es invalido ")

        ubicacionNeoDAO.conectar(nombreDeUbicacion1.lowercase(), nombreDeUbicacion2.lowercase(), tipoCamino)
    }

    override fun conectados(nombreDeUbicacion: String): List<Ubicacion> {
        ubicacionDAO.findByNombreOrNombreNull(nombreDeUbicacion.lowercase())
            ?: throw NombreInvalidoException("No existe ubicacion ${nombreDeUbicacion}")
        return ubicacionNeoDAO.conectados(nombreDeUbicacion.lowercase()).map { ubicacion -> ubicacion.aModelo() }
    }

    override fun estaConectadaCon(nombreDeUbicacion1: String, nombreDeUbicacion2: String): Boolean {
        return ubicacionNeoDAO.estaConectadaCon(nombreDeUbicacion1.lowercase(), nombreDeUbicacion2.lowercase())
    }

    override fun esAlcanzable(vector: Vector, nombreDeUbicacionDestino: String): Boolean {

        return ubicacionNeoDAO.esAlcanzable(
            vector.ubicacion.nombre,
            nombreDeUbicacionDestino.lowercase(),
            vector.caminosRecorribles()
        )
    }

    private fun existeLaUbicacionAMenosDe100KM(ubicacionId1: Long, ubicacionId2: Long): UbicacionMongoDTO? {
        val ubicacionActual = ubicacionMongoDAO.findByIdJPAOrIdJPANull(ubicacionId1)
            ?: throw IdInvalidoExcepcion("No existe ubicacion con el id ${ubicacionId1}")
        return ubicacionMongoDAO.existeUbicacionAMenosDeCienKm(ubicacionId2.toString(), ubicacionActual.coordenadas.x, ubicacionActual.coordenadas.y)
    }

    override fun deleteAll() {
        ubicacionNeoDAO.detachDelete()
        ubicacionMongoDAO.deleteAll()
    }

}