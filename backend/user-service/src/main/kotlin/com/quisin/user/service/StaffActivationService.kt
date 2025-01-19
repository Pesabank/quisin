package com.quisin.user.service

import com.quisin.user.domain.UserStatus
import com.quisin.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StaffActivationService(
    private val userRepository: UserRepository
) {
    fun toggleStaffStatus(
        staffId: UUID, 
        newStatus: UserStatus
    ): Boolean {
        val staff = userRepository.findById(staffId)
            .orElseThrow { IllegalArgumentException("Staff member not found") }

        staff.status = newStatus
        userRepository.save(staff)

        return true
    }

    fun isStaffActive(staffId: UUID): Boolean {
        val staff = userRepository.findById(staffId)
            .orElseThrow { IllegalArgumentException("Staff member not found") }
        
        return staff.status == UserStatus.ACTIVE
    }

    fun filterActiveStaff(staffIds: List<UUID>): List<UUID> {
        return staffIds.filter { isStaffActive(it) }
    }
}
