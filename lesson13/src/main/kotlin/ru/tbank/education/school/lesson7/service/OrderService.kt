package ru.tbank.education.school.lesson7.service

import org.springframework.stereotype.Service
import ru.tbank.education.school.lesson7.dto.CreateOrderRequest
import ru.tbank.education.school.lesson7.dto.Order
import ru.tbank.education.school.lesson7.dto.OrderItem
import ru.tbank.education.school.lesson7.dto.OrderStatus
import java.math.BigDecimal

@Service
class OrderService {

    private val orders = mutableMapOf<Long, Order>()
    private var idCounter = 1L

    fun create(request: CreateOrderRequest): Order {
        val order = toOrder(idCounter++, request, OrderStatus.NEW)
        orders[order.id] = order
        return order
    }

    private fun toOrder(id: Long, request: CreateOrderRequest, status: OrderStatus): Order {
        val items = request.items.map { item ->
            val lineTotal = item.price.multiply(BigDecimal.valueOf(item.quantity.toLong()))
            OrderItem(
                sku = item.sku,
                quantity = item.quantity,
                price = item.price,
                lineTotal = lineTotal
            )
        }
        val total = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.lineTotal) }
        return Order(
            id = id,
            customerEmail = request.customerEmail,
            deliveryAddress = request.deliveryAddress,
            items = items,
            totalAmount = total,
            status = status
        )
    }
}
