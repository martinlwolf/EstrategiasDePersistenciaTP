package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.Distrito
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon

class DistritoDTO (
    var id: String?,
    var nombre: String,
    var coordenadas: List<Pair<Double,Double>>
) {

    companion object {
        fun desdeModelo(distrito: Distrito) =
            DistritoDTO (
                id = distrito.idDistrito,
                nombre = distrito.nombreDistrito,
                coordenadas = distrito.coordenadasDistrito.map { it -> Pair(it.x,it.y) }
            )
    }

    fun aModelo(): Distrito {
        val distrito = Distrito(this.nombre, GeoJsonPolygon(this.coordenadas.map { it -> Point(it.first,it.second) }))
        distrito.idDistrito = this.id
        return distrito
    }

}