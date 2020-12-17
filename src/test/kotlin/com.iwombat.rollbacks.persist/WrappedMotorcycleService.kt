package com.iwombat.rollbacks.persist

import com.iwombat.rollbacks.persist.model.Motorcycle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import javax.transaction.Transactional

@Component
class WrappedMotorcycleService {
    @Autowired
    lateinit var service : MotorcycleService

    @Autowired
    lateinit var publisher : ApplicationEventPublisher

    var rollbackCounter = RollbackCounter()

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun saveAllWrapper(cycles : List<Motorcycle>) {
        service.saveMotorcycles(cycles)
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun saveWrapper(cycle : Motorcycle) {
        service.saveMotorcycle(cycle)
    }

    // we use this one in the wrapped transaction test
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun assignWrapper(location : String , cycles : List<Motorcycle>) {
        publisher.publishEvent(MyEvent())
        service.assignLocation(location, cycles)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun afterRollback(event:MyEvent) {
        println("Received event id: ${event.payload}")
        rollbackCounter.incrementCount()
    }
}

class RollbackCounter {
    var count = 0
    fun incrementCount() { count++ }
    fun reset() {count = 0}
}

class MyEvent {
    val payload: Any = java.util.UUID.randomUUID()
}

