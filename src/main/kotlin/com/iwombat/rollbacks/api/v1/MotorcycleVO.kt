package com.iwombat.rollbacks.api.v1

import com.iwombat.rollbacks.persist.model.Marque
import com.iwombat.rollbacks.persist.model.Motorcycle
import org.springframework.beans.BeanUtils
import java.util.*

data class MotorcycleVO(
        var id: UUID = UUID.randomUUID(),
        var marque: Marque = Marque.UNKNOWN,
        var model: String = "",
        var vin: String = "",
        var location: String = "",
        var year: Int = 0,
        var version: Int = 0
) {

    fun convertToEntity() : Motorcycle {
        val entity = Motorcycle()
        BeanUtils.copyProperties(this, entity)
        return entity
    }

    companion object {
        fun toValue(entity : Motorcycle) : MotorcycleVO {
            val value = MotorcycleVO()
            BeanUtils.copyProperties(entity, value )
            return value
        }
    }
}