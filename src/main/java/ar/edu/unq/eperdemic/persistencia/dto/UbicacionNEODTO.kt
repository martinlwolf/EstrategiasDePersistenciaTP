package ar.edu.unq.eperdemic.persistencia.dto

import ar.edu.unq.eperdemic.modelo.Ubicacion
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property

@Node("UbicacionNEO")
class UbicacionNEODTO() {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Property
    var idJPA: Long? = null

    @Property
    lateinit var nombre: String

    companion object {
        fun desdeModelo(ubicacion: Ubicacion): UbicacionNEODTO{
            val ubicacionNeo = UbicacionNEODTO ()
            ubicacionNeo.idJPA = ubicacion.id
            ubicacionNeo.nombre = ubicacion.nombre
            return ubicacionNeo
        }
    }

    fun aModelo(): Ubicacion {
        val ubicacion = Ubicacion()
        ubicacion.nombre = this.nombre
        ubicacion.id = this.idJPA
        return ubicacion
    }
}