package com.quisin.user.interceptor

import com.quisin.user.service.StaffActivationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

@Component
class StaffActivationInterceptor(
    private val staffActivationService: StaffActivationService
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val staffId = extractStaffId(request)
        if (staffId != null) {
            return if (staffActivationService.isStaffActive(staffId)) {
                true
            } else {
                response.status = HttpStatus.FORBIDDEN.value()
                response.writer.write("Staff member is not active")
                false
            }
        }
        return true
    }

    private fun extractStaffId(request: HttpServletRequest): UUID? {
        val staffIdStr = request.getHeader("X-Staff-ID")
        return try {
            UUID.fromString(staffIdStr)
        } catch (e: Exception) {
            null
        }
    }
}
