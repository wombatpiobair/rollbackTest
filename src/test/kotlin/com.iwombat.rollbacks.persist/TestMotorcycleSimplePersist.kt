package com.iwombat.rollbacks.persist

import com.iwombat.rollbacks.persist.model.Marque
import com.iwombat.rollbacks.persist.model.Motorcycle
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@DataJpaTest
@ComponentScan("com.iwombat.rollbacks.persist*")
class  TestMotorcycleSimplePersist() {

    @Autowired
    lateinit var service : MotorcycleService

    @Test
    fun `should persist a single object`() {
        val cycle = Motorcycle(marque= Marque.ARIEL, model="4G-600", year=1938, vin="A4G563", location="shop")

        val persist = service.saveMotorcycle(cycle)

        assertTrue(persist.id.equals(cycle.id))
    }

}