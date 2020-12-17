package com.iwombat.rollbacks.persist.dao

import com.iwombat.rollbacks.persist.model.Motorcycle
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MotorcycleDAO : CrudRepository<Motorcycle, UUID> {
    //fun findAllByLocation(location:String) : Iterable<Motorcycle>
    //fun findAllByMarque(marque: Marque)
}