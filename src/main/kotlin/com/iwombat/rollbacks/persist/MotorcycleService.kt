package com.iwombat.rollbacks.persist

import com.iwombat.rollbacks.persist.dao.MotorcycleDAO
import com.iwombat.rollbacks.persist.model.Motorcycle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.transaction.Transactional

@Component
@Transactional(Transactional.TxType.SUPPORTS)
class MotorcycleService @Autowired constructor(private val dao: MotorcycleDAO) {
    private val logger: Logger = LoggerFactory.getLogger(MotorcycleService::class.java)

    @Transactional(Transactional.TxType.REQUIRED)
    fun assignLocation(location: String, motorcycles: List<Motorcycle>) {
        motorcycles.forEach{
            it.location=location
            dao.save(it)
            logger.info("Updated cycle: ${it.id} with locatio: ${it.location}")
        }

    }

    fun saveMotorcycle(cycle: Motorcycle) : Motorcycle {
        return dao.save(cycle)
    }

    fun findMotorcycle(id : UUID) : Motorcycle {
        return dao.findById(id).get()
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun saveMotorcycles(cycles: List<Motorcycle>)  {
        dao.saveAll(cycles)
    }

    fun delete(id: UUID) {
        dao.deleteById(id)
    }
}
