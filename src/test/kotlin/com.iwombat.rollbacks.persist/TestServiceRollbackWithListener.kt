package com.iwombat.rollbacks.persist

import com.iwombat.rollbacks.api.v1.MotorcycleVO
import com.iwombat.rollbacks.persist.model.Marque
import com.iwombat.rollbacks.persist.model.Motorcycle
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.ComponentScan
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.transaction.AfterTransaction
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.lang.Exception
import java.util.*
import javax.transaction.Transactional


@ExtendWith(SpringExtension::class)
@DataJpaTest
@ComponentScan("com.iwombat.rollbacks.persist*")
class  TestServiceRollbackWithListener() {

    @Autowired
    lateinit var service : MotorcycleService

    @Autowired
    lateinit var wrapper : WrappedMotorcycleService

    @Autowired
    lateinit var publisher : ApplicationEventPublisher

    val testCaseId = UUID.randomUUID()

    lateinit var rollbackCounter : RollbackCounter
    @BeforeEach
    fun resetData() {
        rollbackCounter = wrapper.rollbackCounter
        rollbackCounter.reset()
        try {
            service.delete(testCaseId)
        } catch (e : Exception) {
            // eat any failures
        }
    }

    @Test
    fun `should fail on race condition with version`() {
        val cycle1 = Motorcycle(id= testCaseId, marque= Marque.ARIEL, model="4G-600", year=1938, vin="A4G563", location="shop", version=1)
        val cycle2 = Motorcycle(marque= Marque.BSA, model="M20", year=1941, vin="BSA1234", location="home", version=1)
        val cycle3 = Motorcycle(marque= Marque.HARLEY_DAVIDSON, model="FLH", year=1963, vin="63FL3456", location="condo", version=1)

        val entityList = listOf(cycle1, cycle2, cycle3)

        val persist = wrapper.saveAllWrapper(entityList)

        val frozenValueObjects = entityList.map { MotorcycleVO.toValue(it)}

        // now update the last one in the list and roll version inside its own transaction
        // so we can force an error later
        val cycle3Value = MotorcycleVO.toValue(cycle3)
        cycle3Value.location = "shop"
        wrapper.saveWrapper(cycle3Value.convertToEntity())
        val persisted = service.findMotorcycle(cycle3Value.id)
        assertTrue(persisted.version == 2)

        val entitiesToChange = frozenValueObjects.map{it.convertToEntity()}

        publisher.publishEvent(MyEvent())

        try {
            service.assignLocation("home", entitiesToChange)
        } catch (e : ObjectOptimisticLockingFailureException) {
            println("failed assignment with message --------> ${e.message}")
            // eat our version mismatch failures
        }

    }

    @AfterTransaction
    fun checkIt() {
        val firstEvent = service.findMotorcycle(testCaseId)
        assertTrue(firstEvent.version == 1, "Version not original")
        assertTrue((rollbackCounter.count > 0), "No rollback detected" )
    }

}