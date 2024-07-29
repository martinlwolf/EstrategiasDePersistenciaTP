package ar.edu.unq.eperdemic.modelo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class NombresDeUbicaciones {
    @Id
    var id:String? = null;
    var nombre:String = "";
}