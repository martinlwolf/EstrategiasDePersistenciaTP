package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.persistencia.dto.UbicacionMongoDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("Ubicacion")
class Distrito{

    @Id
    var idDistrito:String? = null;
    lateinit var nombreDistrito:String;
    lateinit var coordenadasDistrito: GeoJsonPolygon;
    var tipo:String = "Distrito";
    var ubicacionesDistrito: MutableList<UbicacionMongoDTO> = mutableListOf()

    protected constructor() {
    }

    constructor(nombre:String, coordenadas: GeoJsonPolygon, ubicacionesDistrito: MutableList<UbicacionMongoDTO> = mutableListOf()) {
        this.nombreDistrito = nombre.lowercase();
        this.coordenadasDistrito = coordenadas;
        this.ubicacionesDistrito = ubicacionesDistrito;
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val distrito = other as Distrito?
        return idDistrito == distrito!!.idDistrito
    }

    override fun hashCode(): Int {
        return Objects.hash(idDistrito)
    }
}