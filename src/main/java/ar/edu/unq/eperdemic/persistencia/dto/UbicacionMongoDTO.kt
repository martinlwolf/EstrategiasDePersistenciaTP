package ar.edu.unq.eperdemic.persistencia.dto

import ar.edu.unq.eperdemic.exceptions.CampoInvalidoExcepcion
import ar.edu.unq.eperdemic.modelo.Ubicacion
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document


@Document("Ubicacion")
class UbicacionMongoDTO() {

    @Id
    var id: String? = null

    var idJPA: String? = null

    lateinit var nombreUbicacion: String

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    lateinit var coordenadas: GeoJsonPoint

    var tipo:String = "Ubicacion";

    companion object {
        fun desdeModelo(ubicacion: Ubicacion): UbicacionMongoDTO{
            ubicacion.coordenada?: throw CampoInvalidoExcepcion("Ubicacion sin coordenada")

            val ubicacionMongo = UbicacionMongoDTO ()
            ubicacionMongo.idJPA = ubicacion.id.toString()
            ubicacionMongo.nombreUbicacion = ubicacion.nombre
            ubicacionMongo.coordenadas = GeoJsonPoint(ubicacion.coordenada!!.first, ubicacion.coordenada!!.second)
            return ubicacionMongo
        }
    }

    fun aModelo(): Ubicacion {
        val ubicacion = Ubicacion()
        ubicacion.nombre = this.nombreUbicacion
        ubicacion.id = this.idJPA!!.toLong()
        ubicacion.coordenada = Pair(this.coordenadas.x, this.coordenadas.y)
        return ubicacion
    }

}