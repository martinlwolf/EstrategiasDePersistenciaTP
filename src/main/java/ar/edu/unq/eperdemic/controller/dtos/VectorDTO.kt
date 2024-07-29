package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.exceptions.TipoDeVectorDesconocido
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.vectores.Animal
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.modelo.vectores.Insecto
import ar.edu.unq.eperdemic.modelo.vectores.Vector

class VectorDTO(
    var id: Long?,
    var ubicacion: Ubicacion,
    var tipoVector: String,
    var especies: MutableSet<EspecieSimpleDTO> = HashSet(),
    var mutaciones: MutableList<MutacionDTO> = mutableListOf()
){
    companion object {
        fun desdeModelo(vector: Vector) =
            VectorDTO(
                id = vector.id,
                ubicacion = vector.ubicacion,
                tipoVector = vector.getTipoVector(),
                especies = vector.especies
                    .map { especie -> EspecieSimpleDTO.desdeModelo(especie) }
                    .toCollection(HashSet()),
                mutaciones = vector.mutaciones
                        .map { mutacion -> MutacionDTO.desdeModelo(mutacion) }
                    .toCollection(mutableListOf())
            )
    }

    fun aModelo(): Vector {
        val vector = this.crearSubclaseVector()
        vector.id = this.id
        vector.ubicacion = this.ubicacion
        vector.especies = this.especies.map { especieDTO  -> especieDTO.aModelo() }.toCollection(HashSet())
        vector.mutaciones = this.mutaciones.map { mutacionDTO  -> mutacionDTO.aModelo() }.toCollection(HashSet())

        return vector
    }

    private fun crearSubclaseVector(): Vector{
        return when (this.tipoVector) {
            "Insecto" -> return Insecto()
            "Animal" -> return Animal()
            "Humano" -> return Humano()
            else -> { throw TipoDeVectorDesconocido("tipo de vector desconocido")}
        }
    }
}