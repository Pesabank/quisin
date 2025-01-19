package com.quisin.restaurant.interceptor

import com.quisin.restaurant.service.RestaurantActivationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

@Component
class RestaurantActivationInterceptor(
    private val restaurantActivationService: RestaurantActivationService
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val restaurantId = extractRestaurantId(request)
        if (restaurantId != null && !restaurantActivationService.isRestaurantActive(restaurantId)) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.writer.write("Restaurant is not active")
            return false
        }
        return true
    }

    private fun extractRestaurantId(request: HttpServletRequest): UUID? {
        val path = request.requestURI
        val regex = "/api/v1/restaurants/([^/]+)".toRegex()
        val matchResult = regex.find(path)
        return matchResult?.groupValues?.get(1)?.let {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
