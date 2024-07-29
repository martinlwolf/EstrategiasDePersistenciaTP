package ar.edu.unq.eperdemic.helper.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class DataServiceImpl () : DataService {

    @Autowired
    lateinit var entityManager: EntityManager

    @Transactional
    override fun cleanAll() {
        val tablesQuery = entityManager.createNativeQuery("SHOW TABLES")
        val tables = tablesQuery.resultList.map { it.toString() }

        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate()

        try {
            for (table in tables) {
                entityManager.createNativeQuery("DELETE FROM $table").executeUpdate()
            }
        } finally {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1;").executeUpdate()
        }
    }

}
